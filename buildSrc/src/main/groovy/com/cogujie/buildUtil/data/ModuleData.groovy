package com.cogujie.buildUtil.data

import groovy.transform.ToString

/**
 * 对应配置文件的module信息
 */
@ToString(includeNames = true, includeFields = true, ignoreNulls = true)
public class ModuleData {

    public String name

    public String type

    public String packageName

    public String init

    /**
     * fully qualified name for module application
     */
    public String applicationFQN

    ModuleData(String name, String type, String packageName, String init, String applicationFQN) {
        this.name = name
        this.type = type
        this.packageName = packageName
        this.init = init
        this.applicationFQN = applicationFQN
    }
}
