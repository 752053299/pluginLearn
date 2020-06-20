package com.cogujie.buildUtil.data

import com.cogujie.buildUtil.Constant
import groovy.transform.ToString

/**
 * Created by wangzhi on 17/2/15.
 */
@ToString(includeNames = true, includeFields = true, ignoreNulls = true, includes = ["name"])
public class PluginData{

    /**
     * plugin的名字
     */
    public String name

    /**
     * plugin的名字
     */
    public String packageName

    /**
     * 编译资源时的packageId
     */
    public String packageId

    /**
     * plugin的类型
     * com or plugin
     */
    public String type

    /**
     * plugin init 类型,分为lazy和normal
     */
    public String init

    /**
     * 插件版本,现在是读versionName
     */
    public String version

    /**
     * 包含的library
     */
    public Set<LibraryData> includeLibs

    /**
     * 依赖的plugin
     */
    public Set<PluginData> dependencies

    /**
     * 组件信息
     */
    public Set<ComponentData> components


    public PluginData(ModuleData moduleData) {
        this.name = moduleData.name
        this.packageName = moduleData.packageName
        this.init = moduleData.init
        this.type = moduleData.type

        includeLibs = new HashSet<LibraryData>()
        dependencies = new HashSet<PluginData>()
        components = new HashSet<ComponentData>()
    }

    public String dir() {
        return name + File.separator
    }

    public String getJarPath() {
        return dir() + "classes" + Constant.SUFFIX_JAR
    }

    public String getApkPath() {
        return dir() + getFileNameInApk()
    }

    /**
     * 返回plugin包 在最终打出来的apk中的名字
     */
    public String getFileNameInApk() {
        return name + Constant.SUFFIX_APK
    }

    public String getDexPath() {
        return dir() + "classes" + Constant.SUFFIX_DEX
    }

    public String getManifestPath() {
        return dir() + Constant.MANIFEST
    }

    public String getResPath() {
        return dir() + "res.zip"
    }

    public String getRPath() {
        return dir() + "R"
    }
}
