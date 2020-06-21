package com.cogujie.buildUtil.config

import com.cogujie.buildUtil.Util.MLog
import com.cogujie.buildUtil.data.PluginData
import com.cogujie.buildUtil.query.DataQuery


/**
 * Created by wangzhi on 17/2/20.
 */
public class ConfigWriter extends AbConfigWriter {

//Constant.ASSETS
  //  String packageName;
    IRunTimeProvider runTimePathProvider


    ConfigWriter(DataQuery dataQuery ,IRunTimeProvider runTimePathProvider) {
        super(dataQuery)
        this.dataQuery = dataQuery
        this.runTimePathProvider = runTimePathProvider
    }

    @Override
    void writeModule(Node module, PluginData plugin) {
        MLog.i("write config plugin name: ${plugin.name}, apk name : ${plugin.apkPath}")
        //add attribute
        module.attributes().put("path", runTimePathProvider.getPluginPathInRunTime(plugin.getFileNameInApk()))
        module.attributes().put("appVersion", plugin.version)
        //plugin的type可能和xml中的type不一致:当debugType='plugin' type='com'
        //且是debug编译时,plugin的type是会被改成'plugin'的,所以我们要给运行时app提供的type应该是真实plugin的type
        module.attributes().put("type", plugin.type)
        module.attributes().put("init", plugin.init)
        if (module.attributes().containsKey("debugType")) {
            module.attributes().remove("debugType")
        }
        fillCompileNode(module, plugin)
        fillComponents(module, plugin)
    }

    public void fillCompileNode(Node module, PluginData plugin) {
        Node compileNode = module.appendNode("compile")
        plugin.includeLibs.each { lib ->
            compileNode.appendNode("include", null, lib.coordinate)
        }
        plugin.dependencies.each { dep ->
            compileNode.appendNode("dependency", null, dep.name)
        }
    }

    public void fillComponents(Node module, PluginData plugin) {
        Node componentNode = module.appendNode("component")
        println("components: " + plugin.components)
        plugin.components.each { component ->
            component.activities.each {
                componentNode.appendNode("activity", null, it)
            }
            component.services.each {
                componentNode.appendNode("service", null, it)
            }
            component.receivers.each {
                componentNode.appendNode("receiver", null, it)
            }
            component.providers.each {
                componentNode.appendNode("provider", null, it)
            }
        }
    }

}
