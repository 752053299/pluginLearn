package com.cogujie.buildUtil.data


import com.android.builder.model.AndroidLibrary
import com.android.builder.model.JavaLibrary
import com.cogujie.buildUtil.Util.Utils
import groovy.transform.ToString

/**
 * Created by wangzhi on 17/2/15.
 */

@ToString(includeNames = true, includeFields = true, ignoreNulls = true, includes = ["coordinate"])
public class LibraryData {

    public String groupId
    public String artifactId
    public String version

    public String coordinate


    public long size = 0

    /**
     * true代表该lib是jar 依赖,false 代表是aar依赖
     */
    public boolean isJar

    /**
     *  true代表该lib是本地的module工程
     */
    public boolean isLocalModule = false

    /**
     * true代表该lib是顶层module
     */
    public boolean isTopLib = true

    /**
     * true代表该lib中有用到upaar插件
     * 只有isLocalModule为true时,该字段才有意义
     */
    public boolean hasUpInfo = true

    /**
     * pom文件所在路径,可能为空
     */
    public File pom

    /**
     * 该lib包含的类文件
     */
    public Set<String> classSet

    /**
     * 该lib在本地仓库中的目录 如 ~/.gradle/caches/modules-2/files-2.1/com.google.code.gson/gson/2.3
     */
    public File bundleDir

    /**
     * 类所在的jar包
     */
    public File jarFile

    /**
     * asset目录
     */
    public File assetsFolder

    /**
     * resource目录
     */
    public File resFolder

    /**
     * jni目录
     */
    public File jniFolder

    /**
     * aar中libs的jar
     */
    public Collection<File> localJars

    /**
     * manifestFile
     */
    public File manifest

    /**
     * R.txt
     */
    public File symbolFile

    /**
     * manifest中的包名
     */
    public String packageName

    /**
     * 该lib包含的组件
     */
    public ComponentData component

    /**
     * 该lib直接的依赖所有lib
     */
    public Set<LibraryData> dependencies

    /**
     * 该lib直接的依赖所有lib
     */
    public Set<LibraryData> whoDependencyThis

//
    public LibraryData() {

    }
//
   protected LibraryData(String groupId, String artifactId, String version) {
        this.groupId = groupId
        this.artifactId = artifactId
        this.version = version
        coordinate = Utils.getCoordinates(groupId, artifactId, version)
    }
//
//
    public LibraryData(AndroidLibrary dependency) {
        this(dependency.resolvedCoordinates.getGroupId(),
                dependency.resolvedCoordinates.getArtifactId(),
                dependency.resolvedCoordinates.getVersion())
        jarFile = dependency.getJarFile()
        isJar = false
        classSet = new HashSet<String>()
        localJars = dependency.getLocalJars()
        bundleDir = Utils.getSpecParentFile(dependency.getBundle(), version)
        manifest = dependency.manifest
        resFolder = dependency.resFolder
        jniFolder = dependency.jniFolder
        assetsFolder = dependency.assetsFolder
        symbolFile = dependency.symbolFile
    }
//
    public LibraryData(JavaLibrary dependency) {
        this(dependency.resolvedCoordinates.getGroupId(),
                dependency.resolvedCoordinates.getArtifactId(),
                dependency.resolvedCoordinates.getVersion())

        jarFile = dependency.getJarFile()
        isJar = true
        classSet = new HashSet<String>()
        bundleDir = Utils.getSpecParentFile(jarFile, version)
    }


//    String getLibName() {
//        return coordinate.replace(":", "_")
//    }
//
//    public String dir() {
//        return getLibName() + File.separator
//    }
//
//    public String getJarPath() {
//        return dir() + "classes" + Constant.SUFFIX_JAR
//    }
//
//    public String getApkPath() {
//        return dir() + getFileNameInApk()
//    }
//
//    /**
//     * 返回lib包 在最终打出来的apk中的名字
//     */
//    public String getFileNameInApk() {
//        return getLibName() + Constant.SUFFIX_APK
//    }
//
//    public String getDexPath() {
//        return dir() + "classes" + Constant.SUFFIX_DEX
//    }
//
//    public String getClassPath() {
//        return dir() + "classes"
//    }
//
//    public String getManifestPath() {
//        return dir() + Constant.MANIFEST
//    }
//
//    public String getResPath() {
//        return dir() + "res.zip"
//    }
//
//    public String getApPath() {
//        return dir() + "res.ap_"
//    }
//
//    public String getRDirPath() {
//        return dir() + "R"
//    }
//
//
//    public String getSymbolPath() {
//        return dir() + "symbol"
//    }

}
