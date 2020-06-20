package com.cogujie.buildUtil.config

import com.cogujie.buildUtil.Constant
import com.cogujie.buildUtil.Util.FileUtils
import com.cogujie.buildUtil.Util.MLog
import com.cogujie.buildUtil.Util.Utils
import com.cogujie.buildUtil.data.PluginData
import com.cogujie.buildUtil.query.DataQuery
import org.gradle.api.GradleException

/**
 * Created by wangzhi on 17/3/2.
 */
public abstract class AbConfigWriter {


    DataQuery dataQuery

    AbConfigWriter(DataQuery dataQuery) {
        this.dataQuery = dataQuery
    }

    public byte[] getModuleConfigBytes(File originConfigFile) {
        Node manifest = new XmlParser().parse(originConfigFile)
        manifest.get("module").each { module ->
            module = module as Node
            String name = module.get('@name')
            PluginData plugin = dataQuery.getPluginByName(name)
            if (plugin == null) {
                try {
                    throw new GradleException("module : " + name + "  not found.")
                } catch (Exception e) {
                   MLog.i(e.message)
                }
            }

            writeModule(module, plugin)
        }

        ByteArrayOutputStream bos = new ByteArrayOutputStream()
        Utils.printXml(bos, manifest)
        return bos.toByteArray()
    }

    public void writeConfig(File inConfigFile, File writeToPath) {
        //FileUtils.copyFile(inConfigFile,tempModulePath)
        byte[] bytes = getModuleConfigBytes(inConfigFile)
       // File tempConfigFile = new File(inConfigFile.parentFile, "ModuleConfig.xml")
        writeToPath.setBytes(bytes)
//
//
//
//        if (outConfigFile.equals(inConfigFile)) {
//            File tempConfigFile = new File(inConfigFile.parentFile, "ModuleConfig.xml")
//
//            tempConfigFile.setBytes(bytes)
//            //tempConfigFile.mkdir()
//        } else {
//            outConfigFile.setBytes(bytes)
//        }
    }

    public abstract void writeModule(Node module, PluginData plugin)
}
