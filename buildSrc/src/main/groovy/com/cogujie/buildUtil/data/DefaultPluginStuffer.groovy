package com.cogujie.buildUtil.data

import com.cogujie.buildUtil.query.DataQuery
import org.gradle.api.GradleException

/**
 * Created by wangzhi on 17/2/20.
 */
public class DefaultPluginStuffer {


    DefaultPluginStuffer() {
    }


    //todo 处理其他位于顶层但不在配置中的的library
    void fillPlugins(DataQuery dataQuery) {
        Collection<PluginData> plugins = dataQuery.allPlugins
        //1.填充所有plugin的includeLibs
        plugins.each { plugin ->
            plugin.version = dataQuery.getVersion(plugin)
            plugin.includeLibs = collectAllLibsInPlugin(plugin)
            plugin.includeLibs.each { lib ->
                dataQuery.putCoordinatesToPlugin(lib, plugin)
            }
        }

        //2.将还没有被分类的library划分到宿主plugin中
        dataQuery.getAllLibs().each { lib ->
            PluginData pluginData = dataQuery.queryPluginByLibrary(lib)
            if (pluginData == null) {
                dataQuery.hostPlugin.includeLibs.add(lib)
                dataQuery.putCoordinatesToPlugin(lib, dataQuery.hostPlugin)
            }
        }

        //3.这个时候,所有的library都可以找到对应的plugin了,所以填充dependencies和components字段
        plugins.each { plugin ->
            plugin.components = collectComponentsInPlugin(plugin)
            plugin.dependencies = collectDependenciesInPlugin(dataQuery, plugin)
        }
    }

    /**
     * 以plugin的顶层aar出发
     */
    public Collection<LibraryData> collectAllLibsInPlugin(PluginData plugin) {
        Queue<LibraryData> queue = new LinkedList()
        Queue<LibraryData> finishedQueue = new LinkedList()
        Set<LibraryData> libraries = new HashSet<>()
        libraries.addAll(plugin.includeLibs)
        libraries.each { lib ->
            queue.offer(lib)
        }
        LibraryData header = null
        while ((header = queue.poll()) != null) {
            finishedQueue.offer(header)
            header.dependencies.each { depLib ->
                if (canSplit(depLib)) {
                    libraries.add(depLib)
                    if (!finishedQueue.contains(depLib)) {
                        queue.offer(depLib)
                    }
                }
            }
        }
        return libraries
    }

    public Collection<ComponentData> collectComponentsInPlugin(PluginData plugin) {
        Set<ComponentData> components = new HashSet<>()
        plugin.includeLibs.each { lib ->
            if (lib.component != null) {
                components.add(lib.component)
            }
        }
        return components
    }

    public Collection<PluginData> collectDependenciesInPlugin(DataQuery dataQuery, PluginData pluginData) {
        Set<PluginData> dependencies = new HashSet<>()
        pluginData.includeLibs.each {
            it.dependencies.each { lib ->
                //只有在该lib与依赖的lib不在一个plugin时,才加入到plugin的依赖
                if (!pluginData.includeLibs.contains(lib)) {
                    dependencies.add(dataQuery.queryPluginByLibrary(lib))
                }
            }
        }
        return dependencies
    }

    public Set<LibraryData> getTopLibraries(LibraryData lib, Set<LibraryData> traversedSet) {
        Set<LibraryData> libs = new HashSet<>()
        traversedSet.add(lib)
        if (lib.isTopLib) {
            libs.add(lib)
        } else {
            lib.whoDependencyThis.each { depLib ->
                if (!traversedSet.contains(depLib)) {
                    libs.addAll(getTopLibraries(depLib, traversedSet))
                }
            }
        }
        return libs
    }

    /**
     * coordinate <==> top libs
     */

    HashMap<String, Set<LibraryData>> libDepByGroupCacheMap = new HashMap<>()

    /**
     * 一个lib是否只被一个plugin依赖
     */
    public boolean canSplit(LibraryData lib) {
        Set<LibraryData> topLibs = libDepByGroupCacheMap.get(lib.coordinate)

        if (topLibs == null) {
            topLibs=new HashSet<>()
            HashSet<LibraryData> traversedSet = new HashSet<LibraryData>()
            lib.whoDependencyThis.each { depLib ->
                topLibs.addAll(getTopLibraries(depLib, traversedSet))
            }
            libDepByGroupCacheMap.put(lib.coordinate,topLibs)
        }

        if (topLibs.size() == 1) {
            return true
        } else if (topLibs.size() == 0) {
            throw new GradleException("lib: " + GradleUtil.getCoordinates(lib) + " has not top module.")
        } else {
            return false
        }
    }


}
