package com.cogujie.buildUtil.query

import com.cogujie.buildUtil.Util.ProguardTool
import com.cogujie.buildUtil.Util.Utils
import com.cogujie.buildUtil.data.LibraryData
import com.cogujie.buildUtil.data.PluginData


/**
 * Created by wangzhi on 17/2/24.
 */
public class DataQuery implements IPluginQuery {

    /**
     * coordinates <=> 所在Plugin
     */
    Map<String, PluginData> coordinatesPluginMap

    /**
     * 类路径 <=> 所在Library
     */
    Map<String, LibraryData> classToAarMap

    /**
     * module名称 <=> plugin
     */
    Map<String, PluginData> nameToPluginMap

    /**
     * 包含了除了宿主中的lib以外的所有的module
     */
    Collection<PluginData> pluginsAllTypeNoHostCache

    /**
     * 包含了除了宿主中的lib以外的所有type为plugin的module
     */
    Collection<PluginData> pluginsNoHostCache

    PluginData hostPlugin

    String hostVersion

    Set<LibraryData> libs

    Map<String, LibraryData> coordinateToLibMap

    ProguardTool proguardTool

    DataQuery(Set<LibraryData> libs, ProguardTool proguardTool, String hostVersion) {
        this.libs = libs
        this.proguardTool = proguardTool
        this.hostVersion = hostVersion

        coordinatesPluginMap = new HashMap<>()
        classToAarMap = new HashMap<>()
        nameToPluginMap = new HashMap<>()
        coordinateToLibMap = new HashMap<>()
        libs.each { lib ->
            coordinateToLibMap.put(Utils.getCoordinatesNoVersion(lib), lib)
        }

        genClassToAarMapping(libs)
    }

    void setHostPlugin(PluginData hostPlugin) {
        this.hostPlugin = hostPlugin
        addPlugin(hostPlugin)
    }

    public void addPlugin(PluginData pluginData) {
        nameToPluginMap.put(pluginData.name, pluginData)
    }

    /* moduleconfig.xml 中只会有在源moduleconfig.xml记录的plugin  */

    /**
     * stuffer在填充plugin数据时,会调用该方法拿所有的plugin
     */
    public Collection<PluginData> getAllPlugins() {
        return nameToPluginMap.values()
    }


    public Collection<PluginData> getPluginsWithAllTypeNoHost() {
        if (pluginsAllTypeNoHostCache != null) {
            return pluginsAllTypeNoHostCache
        }

        pluginsAllTypeNoHostCache = new HashSet<>()
        nameToPluginMap.values().each { plugin ->
            pluginsAllTypeNoHostCache.add(plugin)
        }
        pluginsAllTypeNoHostCache.remove(hostPlugin)
        return pluginsAllTypeNoHostCache
    }

    /**
     * 在 1.class -> jar -> dex -> apk
     *    2.manifest -> apk
     * 的过程中
     * 都是调用该方法拿除去host之外的所有的type为'plugin'的 plugin
     *
     */

    public Collection<PluginData> getPluginsNoHost() {
        if (pluginsNoHostCache != null) {
            return pluginsNoHostCache
        }
        pluginsNoHostCache = new HashSet<>()
        nameToPluginMap.values().each { plugin ->
            if (GradleUtil.isPluginType(plugin)) {
                pluginsNoHostCache.add(plugin)
            }
        }
        pluginsNoHostCache.remove(hostPlugin)
        return pluginsNoHostCache
    }

    public PluginData getPluginByName(String name) {
        return nameToPluginMap.get(name)
    }


    public void genClassToAarMapping(Set<LibraryData> libs) {
        classToAarMap = new HashMap<>()
        libs.each { libData ->
            libData.classSet.each { classPath ->
                classToAarMap.put(classPath, libData)
            }
        }
    }

    public Set<LibraryData> getAllLibs() {
        return libs
    }

    /**
     * 根据类路径(如 a/b/c.class)查找类所在的aar,path必须是没混淆的
     */
    public LibraryData queryLibByClassPath(String path) {
        return classToAarMap.get(path)
    }


    public PluginData queryPluginByLibrary(LibraryData libraryData) {
        return queryPluginByCoordinates(Utils.getCoordinates(libraryData))
    }

    public PluginData queryPluginByCoordinates(String coordinates) {
        return coordinatesPluginMap.get(coordinates)
    }

    public void putCoordinatesToPlugin(String coordinates, PluginData pluginData) {
        coordinatesPluginMap.put(coordinates, pluginData)
    }

    public void putCoordinatesToPlugin(LibraryData libraryData, PluginData pluginData) {
        putCoordinatesToPlugin(Utils.getCoordinates(libraryData), pluginData)
    }

    public LibraryData findLibWithCoordNoVersion(String coord) {
        return coordinateToLibMap.get(coord)
    }

    public LibraryData findLibWithPackageName(String packageName) {
        Collection<LibraryData> allFinds = getAllLibs().findAll {
            return it.packageName.equals(packageName)
        }
        if (allFinds.size() == 0) {
            Log.i("packageName: " + packageName)
        }
        try {
            LibraryData findLib = getOnlyOneElement(allFinds)
            return findLib
        } catch (Exception e) {
            Log.w(e.getMessage())
            return null
        }
    }


    public static <T> T getOnlyOneElement(Collection<T> collection) {
        if (collection.size() != 1) {
            throw new RuntimeException("except size is 1,but found " + collection.size())
        }
        return collection.iterator().next()
    }


    HashMap<String, PluginData> classToPluginCache = new HashMap<>()

    /**
     * 传入类路径(如 a/b/c.class),返回该类所属的PluginData
     * 类路径可能是混淆了的
     */
    public PluginData classToPlugin(String classPath) {

        PluginData pluginData = classToPluginCache.get(classPath)
        if (pluginData != null) {
            return pluginData
        }

        //proguardTool != null 说明有混淆
        String realClassPath = classPath
        if (proguardTool != null) {
            realClassPath = proguardTool.getRealName(classPath)
        }
        LibraryData lib

        //操蛋的是,在有的jar包里也有R.class,所以先判断一下所有的library中是否有对应class,如果没有,再看是不是android的R.class

        lib = queryLibByClassPath(realClassPath)

        if (lib == null && realClassPath ==~ Constant.MATCHER_R) {
            String packageName = realClassPath.substring(0, realClassPath.lastIndexOf(File.separator))
            lib = findLibWithPackageName(packageName.replace(File.separator, "."))
        }

        if (lib == null) {
            pluginData = hostPlugin
        } else {
            pluginData = queryPluginByLibrary(lib)
        }
        classToPluginCache.put(classPath, pluginData)
        return pluginData
    }


    public String getVersion(PluginData pluginData) {
        //现在的方案是所有module都用build.config的versionName
        return hostVersion
    }


    public boolean isSplitPlugin(PluginData pluginData) {
        return (pluginData != hostPlugin && GradleUtil.isPluginType(pluginData))
    }

}
