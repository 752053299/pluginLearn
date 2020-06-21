package com.cogujie.buildUtil.config
/**
 * Created by wangzhi on 17/3/2.
 */
public abstract class AbConfigReader {

    File configFile

    AbConfigReader(File configFile) {
        this.configFile = configFile
    }

    public void readConfigFile() {
        Node manifest = new XmlParser().parse(configFile)
        manifest.get("module").each { module ->
            processModule(module)
        }
    }

    abstract void processModule(Node module)
}
