package com.cogujie.buildUtil

import com.cogujie.buildUtil.Util.Utils
import com.cogujie.buildUtil.Util.parser.ManifestSimpleParser
import com.cogujie.buildUtil.Util.parser.PomParser
import com.cogujie.buildUtil.data.ComponentData
import com.cogujie.buildUtil.data.LibraryData
import org.gradle.api.Project
import com.cogujie.buildUtil.Util.MLog

/**
 * Created by wangzhi on 17/2/15.
 */
public class DependencyAnalyzer {

    Project project
    Set<LibraryData> libs
    Map<String, LibraryData> coordinateToLibMap

    DependencyAnalyzer(Project project, Set<LibraryData> libs) {
        this.project = project
        this.libs = libs
    }

    /**
     * 分析每个aar,得到起依赖\组件等信息
     */
    public analyseDependencies() {
        coordinateToLibMap = new HashMap<>()
        libs.each { lib ->
            coordinateToLibMap.put(Utils.getCoordinatesNoVersion(lib), lib)
        }
        libs.each { lib ->
            if (!lib.isJar) {
                if (lib.manifest.exists()){
                    lib.packageName = Utils.getPackageNameFromManifest(lib.manifest)
                    lib.component = getComponentData(lib.manifest)
                }
                lib.dependencies = getDependencies(project, lib)
            }
        }
        libs.each { lib ->
            if (!Utils.isCollectionEmpty(lib.whoDependencyThis)) {
                lib.isTopLib = false
            }
        }


    }
//
    private Set<LibraryData> getDependencies(Project project, LibraryData lib) {
        Set<LibraryData> libraries = new HashSet<>()
        println("---------------------------------------------------------")
        if (lib.isLocalModule) {
            Utils.getDepForLocalModule(project.rootProject, ":" + lib.artifactId).each { coordinate ->
                addDependency(lib, libraries, coordinate)
            }
        } else {
            PomParser parser = new PomParser(lib.pom)
            parser.parse()
            parser.depCoordinateSet.each { coordinate ->
                addDependency(lib, libraries, coordinate)
            }
        }
        MLog.i("${lib.coordinate} has ${libraries.size()} dep.")
        return libraries
    }
//
    private void addDependency(LibraryData targetLib, Collection<LibraryData> libraries, String coordinate) {
        LibraryData libraryData = coordinateToLibMap.get(coordinate)
        if (libraryData == null) {
            MLog.i(targetLib.coordinate + " has depend on a not found library: " + coordinate)
        } else {
            if (libraryData.whoDependencyThis == null) {
                libraryData.whoDependencyThis = new HashSet<>()
            }
            libraryData.whoDependencyThis.add(targetLib)
            libraries.add(libraryData)
        }
    }

    public ComponentData getComponentData(File manifest) {
        ManifestSimpleParser parser = new ManifestSimpleParser(manifest)
        parser.parse()
        return new ComponentData(parser.activities, parser.services, parser.receivers, parser.providers)
    }




}
