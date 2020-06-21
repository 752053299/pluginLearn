package com.build.java;

import com.android.build.gradle.internal.ide.DependenciesImpl;
import com.android.build.gradle.internal.ide.dependencies.ArtifactUtils;
import com.android.build.gradle.internal.ide.dependencies.BuildMappingUtils;
import com.android.build.gradle.internal.ide.dependencies.LibraryUtils;
import com.android.build.gradle.internal.ide.dependencies.MavenCoordinatesUtils;
import com.android.build.gradle.internal.ide.dependencies.ResolvedArtifact;
import com.android.build.gradle.internal.publishing.AndroidArtifacts;
import com.android.build.gradle.internal.scope.VariantScope;
import com.android.builder.model.AndroidLibrary;
import com.android.builder.model.Dependencies;
import com.android.builder.model.JavaLibrary;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import org.gradle.api.artifacts.ArtifactCollection;
import org.gradle.api.artifacts.component.ComponentIdentifier;
import org.gradle.api.artifacts.component.ProjectComponentIdentifier;
import org.gradle.api.artifacts.result.ArtifactResult;
import org.gradle.api.artifacts.result.ResolvedArtifactResult;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.android.build.gradle.internal.publishing.AndroidArtifacts.ConsumedConfigType.COMPILE_CLASSPATH;
import static com.android.build.gradle.internal.publishing.AndroidArtifacts.ConsumedConfigType.RUNTIME_CLASSPATH;

public class JavaUtil {

    public static DependenciesImpl createDependencies(VariantScope variantScope,
                                                      ImmutableMap<String, String> buildMapping) {

        try {
            ImmutableList.Builder<Dependencies.ProjectIdentifier> projects =
                    ImmutableList.builder();
            ImmutableList.Builder<AndroidLibrary> androidLibraries = ImmutableList.builder();
            ImmutableList.Builder<JavaLibrary> javaLibrary = ImmutableList.builder();

            // get the runtime artifact. We only care about the ComponentIdentifier so we don't
            // need to call getAllArtifacts() which computes a lot more many things.
            // Instead just get all the jars to get all the dependencies.
            // Note: Query for JAR instead of PROCESSED_JAR due to b/110054209
            ArtifactCollection runtimeArtifactCollection =
                    ArtifactUtils.computeArtifactList(
                            variantScope,
                            RUNTIME_CLASSPATH,
                            AndroidArtifacts.ArtifactScope.ALL,
                            AndroidArtifacts.ArtifactType.JAR);

            // build a list of the artifacts
            Set<ComponentIdentifier> runtimeIdentifiers =
                    new HashSet<>(runtimeArtifactCollection.getArtifacts().size());
            for (ResolvedArtifactResult result : runtimeArtifactCollection.getArtifacts()) {
                runtimeIdentifiers.add(result.getId().getComponentIdentifier());
            }

//            Set<ResolvedArtifact> artifacts = getArtifacts(
//                    variantScope,buildMapping
//            );
            Set<ResolvedArtifact> artifacts =
                    ArtifactUtils.getAllArtifacts(
                            variantScope,
                            COMPILE_CLASSPATH,
                            null,
                            buildMapping);
            for (ResolvedArtifact artifact : artifacts) {
                ComponentIdentifier id = artifact.getComponentIdentifier();

                boolean isProvided = !runtimeIdentifiers.contains(id);

                boolean isSubproject = id instanceof ProjectComponentIdentifier;

                String projectPath = null;
                String buildId = null;
                if (isSubproject) {
                    final ProjectComponentIdentifier projectId = (ProjectComponentIdentifier) id;
                    projectPath = projectId.getProjectPath();
                    buildId = BuildMappingUtils.getBuildId(projectId, buildMapping);
                }

                if (artifact.getDependencyType() == ResolvedArtifact.DependencyType.JAVA) {
                    if (projectPath != null) {
                        projects.add(
                                new DependenciesImpl.ProjectIdentifierImpl(buildId, projectPath));
                        continue;
                    }
                    // FIXME: Dependencies information is not set correctly.
                    javaLibrary.add(
                            new com.android.build.gradle.internal.ide.JavaLibraryImpl(
                                    artifact.getArtifactFile(),
                                    null, /* buildId */
                                    null, /* projectPath */
                                    ImmutableList.of(), /* dependencies */
                                    null, /* requestedCoordinates */
                                    MavenCoordinatesUtils.getMavenCoordinates(artifact),
                                    false, /* isSkipped */
                                    isProvided));
                } else {
                    if (artifact.isWrappedModule()) {
                        // force external dependency mode.
                        buildId = null;
                        projectPath = null;
                    }

                    File extractedFolder = artifact.getExtractedFolder();
                    if (extractedFolder == null) {
                        // fall back so the value is non null, in case of sub-modules which don't
                        // have aar/extracted folders.
                        extractedFolder = artifact.getArtifactFile();
                    }

                    androidLibraries.add(
                            new com.android.build.gradle.internal.ide.AndroidLibraryImpl(
                                    MavenCoordinatesUtils.getMavenCoordinates(artifact),
                                    buildId,
                                    projectPath,
                                    artifact.getArtifactFile(),
                                    extractedFolder,
                                    LibraryUtils.findResStaticLibrary(variantScope, artifact),
                                    artifact.getVariantName(),
                                    isProvided,
                                    false, /* dependencyItem.isSkipped() */
                                    ImmutableList.of(), /* androidLibraries */
                                    ImmutableList.of(), /* javaLibraries */
                                    LibraryUtils.getLocalJarCache().get(extractedFolder)));
                }
            }

            // get runtime-only jars by filtering out compile dependencies from runtime artifacts.
            Set<ComponentIdentifier> compileIdentifiers =
                    artifacts
                            .stream()
                            .map(ResolvedArtifact::getComponentIdentifier)
                            .collect(Collectors.toSet());
            List<File> runtimeOnlyClasspath =
                    runtimeArtifactCollection
                            .getArtifacts()
                            .stream()
                            .filter(
                                    it ->
                                            !compileIdentifiers.contains(
                                                    it.getId().getComponentIdentifier()))
                            .map(ResolvedArtifactResult::getFile)
                            .collect(Collectors.toList());

            return new DependenciesImpl(
                    androidLibraries.build(),
                    javaLibrary.build(),
                    projects.build(),
                    runtimeOnlyClasspath);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }


    public static Set<ResolvedArtifact> getArtifacts(VariantScope variantScope,ImmutableMap<String, String> buildMapping) {

        ArtifactCollection allArtifactList = variantScope.getArtifactCollection(
                RUNTIME_CLASSPATH,
                AndroidArtifacts.ArtifactScope.ALL,
                AndroidArtifacts.ArtifactType.JAR
        );

        // Then we can query for MANIFEST that will give us only the Android project so that we
        // can detect JAVA vs ANDROID.
        ArtifactCollection manifestList = variantScope.getArtifactCollection(
                RUNTIME_CLASSPATH,
                AndroidArtifacts.ArtifactScope.ALL,
                AndroidArtifacts.ArtifactType.MANIFEST
        );
        ArtifactCollection nonNamespacedManifestList = variantScope.getArtifactCollection(
                RUNTIME_CLASSPATH,
                AndroidArtifacts.ArtifactScope.ALL,
                AndroidArtifacts.ArtifactType.NON_NAMESPACED_MANIFEST
        );

        // We still need to understand wrapped jars and aars. The former is difficult (TBD), but
        // the latter can be done by querying for EXPLODED_AAR. If a sub-project is in this list,
        // then we need to override the type to be external, rather than sub-project.
        // This is why we query for Scope.ALL
        // But we also simply need the exploded AARs for external Android dependencies so that
        // Studio can access the content.
        ArtifactCollection explodedAarList = variantScope.getArtifactCollection(
                RUNTIME_CLASSPATH,
                AndroidArtifacts.ArtifactScope.ALL,
                AndroidArtifacts.ArtifactType.EXPLODED_AAR
        );


        // We also need the actual AARs so that we can get the artifact location and find the source
        // location from it.
        // Note: Query for AAR instead of PROCESSED_AAR due to b/110054209
        ArtifactCollection aarList = variantScope.getArtifactCollection(
                RUNTIME_CLASSPATH,
                AndroidArtifacts.ArtifactScope.EXTERNAL,
                AndroidArtifacts.ArtifactType.AAR
        );


        // build a list of wrapped AAR, and a map of all the exploded-aar artifacts
        HashSet<ComponentIdentifier> wrapperModules = new HashSet<>();
        Set<ResolvedArtifactResult> explodedAarArtifacts = explodedAarList.getArtifacts();
        HashMap<ComponentIdentifier,ResolvedArtifactResult> explodedAarResults = new HashMap<>(explodedAarArtifacts.size());

        for (ResolvedArtifactResult result : explodedAarArtifacts) {
            ComponentIdentifier componentIdentifier = result.getId().getComponentIdentifier();
            if (componentIdentifier instanceof ProjectComponentIdentifier) {
                wrapperModules.add(componentIdentifier);
            }
            explodedAarResults.put(componentIdentifier,result);
        }


        Set<ResolvedArtifactResult> aarArtifacts = aarList.getArtifacts();
        HashMap<ComponentIdentifier,ResolvedArtifactResult> aarResults = new HashMap<>(aarArtifacts.size());
        for (ResolvedArtifactResult result : aarArtifacts) {
            aarResults.put(result.getId().getComponentIdentifier(),result);
        }

        // build a list of android dependencies based on them publishing a MANIFEST element
        HashSet<ResolvedArtifactResult> manifestArtifacts =new HashSet<>();
        manifestArtifacts.addAll(manifestList.getArtifacts());
        manifestArtifacts.addAll(nonNamespacedManifestList.getArtifacts());

        HashSet<ComponentIdentifier> manifestIds = new HashSet<>(manifestArtifacts.size());
        for (ResolvedArtifactResult result : manifestArtifacts) {
            manifestIds.add(result.getId().getComponentIdentifier());
            System.out.println(result.getId().getComponentIdentifier() + "  ---  " + result);
        }


        // build the final list, using the main list augmented with data from the previous lists.
        Set<ResolvedArtifactResult> allArtifacts = allArtifactList.getArtifacts();

        // use a linked hash set to keep the artifact order.
        LinkedHashSet<ResolvedArtifact> artifacts = new LinkedHashSet<>(allArtifacts.size());

        for (ResolvedArtifactResult artifact : allArtifacts) {
         //   System.out.println(artifact.getId().getComponentIdentifier());
            ComponentIdentifier componentIdentifier = artifact.getId().getComponentIdentifier();
            boolean isWrappedModule = wrapperModules.contains(componentIdentifier);
            ResolvedArtifact.DependencyType dependencyType = ResolvedArtifact.DependencyType.JAVA;
            ResolvedArtifactResult mainArtifact = artifact;
            ResolvedArtifactResult extractedAar =null;

            if (manifestIds.contains(componentIdentifier)) {
                dependencyType = ResolvedArtifact.DependencyType.ANDROID;
                // if it's an android dependency, we swap out the manifest result for the exploded
                // AAR result.
                // If the exploded AAR is null then it's a sub-project and we can keep the manifest
                // as the Library we'll create will be a ModuleLibrary which doesn't care about
                // the artifact file anyway.
                ResolvedArtifactResult explodedAar = explodedAarResults.get(componentIdentifier);
             //   System.out.println(explodedAar);
                if (explodedAar != null) {
                    extractedAar = explodedAar;
                    // and we need the AAR bundle itself (if it exists)
                    ResolvedArtifactResult temp = aarResults.get(componentIdentifier);
                    mainArtifact =  temp == null ? mainArtifact: temp;
                }
            }

            artifacts.add(
                   new ResolvedArtifact(
                            mainArtifact,
                            extractedAar,
                            dependencyType,
                            isWrappedModule,
                           buildMapping
                    ));
        }


        return artifacts;
    }



}
