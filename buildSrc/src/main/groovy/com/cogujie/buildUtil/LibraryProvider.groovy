package com.cogujie.buildUtil

import com.android.build.gradle.internal.api.ApplicationVariantImpl
import com.android.build.gradle.internal.ide.DependenciesImpl
import com.android.build.gradle.internal.ide.dependencies.BuildMappingUtils
import com.android.build.gradle.internal.scope.VariantScope
import com.android.builder.model.AndroidLibrary
import com.android.builder.model.JavaLibrary
import com.build.java.JavaUtil
import com.cogujie.buildUtil.Util.DownloadUtil
import com.cogujie.buildUtil.Util.FileUtils
import com.cogujie.buildUtil.Util.MLog
import com.cogujie.buildUtil.Util.Utils
import com.cogujie.buildUtil.Util.ZipTraversal
import com.cogujie.buildUtil.data.LibraryData
import com.google.common.collect.ImmutableMap
import org.gradle.api.GradleException
import org.gradle.api.Project

import java.util.zip.ZipEntry

/**
 * 获取工程依赖的所有组件
 */
public class LibraryProvider {

    Project project
    ApplicationVariantImpl variant
    DownloadUtil downloadUtil

    LibraryProvider(Project project, ApplicationVariantImpl variant) {
        this.project = project
        this.variant = variant
        downloadUtil = new DownloadUtil(project)
    }
//
//    /**
//     *
//     * 搜集该工程对应variant的所有依赖,不包括local jar
//     *
//     * 因此,对于宿主工程而言,所有的local jar都时沉入到宿主dex的.
//     *
//     * 对与宿主工程而言,这样是否会有问题?
//     */
    public Set<LibraryData> getLibraries() {
        Set<LibraryData> libraries = new HashSet<>()

        ImmutableMap<String, String> buildMapping = BuildMappingUtils.computeBuildMapping(project.getGradle());
        VariantScope vs = variant.variantData.scope
        DependenciesImpl di = JavaUtil.createDependencies(
                vs,
                buildMapping,
        )
        println("-----------------android libs ${di.libraries.size()}--------")
        di.libraries.each { lib ->
            libraries.add(generateLibrary(lib))
        }
        println("-----------------java libs${di.javaLibraries.size()}--------")
        di.javaLibraries.each { jar ->
            libraries.add(generateLibrary(jar))
        }

        return libraries
    }


//    public Set<File> getLocalJars() {
//        return variant.variantData.variantConfiguration.getLocalPackagedJars()
//    }

    private LibraryData generateLibrary(Object obj) {
        LibraryData libData
        if (obj instanceof JavaLibrary) {
            libData = new LibraryData((JavaLibrary) obj)
        } else {
            libData = new LibraryData((AndroidLibrary) obj)
        }




        ZipTraversal.traversal(libData.jarFile, new ZipTraversal.Callback() {
            @Override
            void oneEntry(ZipEntry zipEntry, byte[] bytes) {
                if (zipEntry.name.endsWith(".class")) {
                    libData.classSet.add(zipEntry.name)
                }
            }
        })

        if (libData.localJars != null) {
            libData.localJars.each { localJar ->
                ZipTraversal.traversal(localJar, new ZipTraversal.Callback() {
                    @Override
                    void oneEntry(ZipEntry zipEntry, byte[] bytes) {
                        if (zipEntry.name.endsWith(".class")) {
                            libData.classSet.add(zipEntry.name)
                        }
                    }
                })
            }
        }

        //if lib is a local module
        if (libData.bundleDir == null) {
            if ("unspecified".equals(libData.version)) {
                //create
                libData.isLocalModule = true
                return libData
            } else {
                throw new GradleException("unknown lib: " + libData.coordinate)
            }
        }

        File pom = getOneFile(libData.bundleDir, new FileNameFilter() {
            @Override
            boolean accept(File file) {
                return file.isFile() && file.name.endsWith(".pom")
            }

        })
        if (pom == null || !pom.exists()) {
            String pomFileName = libData.artifactId + "-" + libData.version + ".pom"
            String relativePath = "${libData.groupId.replace(".", "/")}/${libData.artifactId}/${libData.version}/"
            String sha1 = downloadUtil.openStringFormMaven(relativePath + pomFileName + ".sha1")
            File localPom
            if (Utils.isStringEmpty(sha1)) {
                localPom = FileUtils.joinFile(libData.bundleDir, "no_sha1", pomFileName)
            } else {
                localPom = FileUtils.joinFile(libData.bundleDir, sha1, pomFileName)
            }
            boolean result = downloadUtil.downFromMaven(relativePath + pomFileName,
                    localPom)
            if (result) {
                pom = localPom
            } else {
                throw new GradleException("can not download pom file. ")
            }
        }

        libData.pom = pom
        //println("pom loca:${pom.absolutePath}")
        return libData
    }
////
    public interface FileNameFilter {
        boolean accept(File file)
    }

    public static File getOneFile(File dir, FileNameFilter filter) {
        List<File> files = eachFileRecurse(dir, filter)
        if (files.size() == 0) {
            MLog.i("pom not found in " + dir)
            return null
        }

        File latestFile = files.get(0)
        for (int i = 1; i < files.size(); i++) {
            if (files.get(i).lastModified() > latestFile.lastModified()) {
                latestFile = files.get(i)
            }
        }

        if (files.size() > 1) {
            MLog.i("found more than one pom file,we select " + latestFile)
        }

        return latestFile
    }


    public static File getOneSubFile(File dir, FileNameFilter filter) {
        List<File> files = eachFileRecurse(dir, filter)
        if (files.size() > 1) {
            throw new RuntimeException("the size of files is not 1")
        }
        if (files.size() == 0) {
            MLog.i("pom not found in " + dir)
            return null
        }

        return files.get(0)
    }

    public static List<File> eachFileRecurse(File dir, FileNameFilter filter) {
        List<File> files = new ArrayList<>()
        dir.eachFileRecurse { file ->
            if (filter.accept(file)) {
                files.add(file)
            }
        }
        return files
    }


}
