package com.cogujie.buildUtil.config

import com.cogujie.buildUtil.Util.MLog
import com.cogujie.buildUtil.Util.Utils
import com.cogujie.buildUtil.data.ModuleData

import javax.rmi.CORBA.Util


/**
 * 读取配置文件
 */
public class ConfigReader extends AbConfigReader {

    /**
     * module application的全限定名 <=> moduledata
     */
    Map<String, ModuleData> moduleDataMap = new HashMap<>()

    boolean isDebug

    ConfigReader(File configFile, boolean isDebug) {
        super(configFile)
        this.isDebug = isDebug
    }


    @Override
    void processModule(Node module) {
        String name = module.get('@name')
        String packageName = module.get('@package')
        String init = module.get('@init')
        String applicationName = module.application.name.text()
        String type = Utils.getType(module, isDebug)
        ModuleData data = new ModuleData(name, type, packageName, init, Utils.getFQN(packageName, applicationName))
        //MLog.i(data)
        moduleDataMap.put(data.applicationFQN, data)
    }


}

