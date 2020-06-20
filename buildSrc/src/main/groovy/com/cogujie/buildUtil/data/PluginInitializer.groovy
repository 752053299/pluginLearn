package com.cogujie.buildUtil.data

import com.cogujie.buildUtil.Constant
import com.cogujie.buildUtil.Util.MLog
import com.cogujie.buildUtil.Util.Utils
import com.cogujie.buildUtil.config.ConfigReader
import com.cogujie.buildUtil.query.DataQuery
import org.gradle.api.GradleException

/**
 *
 */
public class PluginInitializer {

    public static final int startSdkPackageId = 0x70
    public static final int endSdkPackageId = 0x7f
    public static final int MAX_SDK_SIZE = endSdkPackageId - startSdkPackageId

    DataQuery dataQuery

    PluginInitializer(DataQuery dataQuery, String packageName) {
        this.dataQuery = dataQuery
        dataQuery.setHostPlugin(genePluginData(packageName))
    }

    public static PluginData genePluginData(String packageName) {
        ModuleData hostModuleData = new ModuleData(Constant.HOST_PLUGIN_NAME, Constant.COM_TYPE, packageName, Constant.NORMAL_INIT_TYPE, "")
        PluginData hostPlugin = new PluginData(hostModuleData)
        hostPlugin.packageId = endSdkPackageId+""
        return hostPlugin
    }

    /**
     * 生成plugin
     */
    public void generate(DefaultPluginStuffer stuffer, ConfigReader moduleReader) {

        Map<String, ModuleData> remainMap = new HashMap(moduleReader.moduleDataMap)
        dataQuery.getAllLibs().each { aar ->
            moduleReader.moduleDataMap.each { appFQN, module ->
                if (aar.classSet.contains(Utils.FQN2Path(appFQN))) {
                    String coordinates = Utils.getCoordinates(aar)

                    PluginData pluginData = new PluginData(module)
                    dataQuery.addPlugin(pluginData)

                    if (!aar.isTopLib) {
                        try {
                            String errStr = coordinates + " was declared the top module in app/src/main/assets/ModuleConfig.xml, but following libraries directly depend on it:\n"
                            aar.whoDependencyThis.each {
                                errStr += (it.coordinate + "\n")
                            }
                            errStr += "--------------------------------------------"
                            throw new GradleException(errStr)
                        } catch (Exception e) {
                            if (Utils.isPluginType(pluginData) || Utils.isLazyInit(pluginData)) {
                                throw e;
                            } else {
                                MLog.i(e.getMessage())
                            }
                        }
                        aar.isTopLib = true
                    }

                    if (dataQuery.queryPluginByCoordinates(coordinates) != null) {
                        throw new GradleException(coordinates + " in the two module: " +
                                module.name + " and " + dataQuery.queryPluginByCoordinates(coordinates).name)
                    }

                    pluginData.includeLibs.add(aar)
                    remainMap.remove(appFQN)
                    dataQuery.putCoordinatesToPlugin(coordinates, pluginData)
                }
            }
        }

        remainMap.keySet().each { appFQN ->

            try {
                throw new GradleException("module : " + appFQN + "  not found.")
            } catch (Exception e) {
                MLog.i(e.message)
            }

        }

        stuffer.fillPlugins(dataQuery)

    }


}
