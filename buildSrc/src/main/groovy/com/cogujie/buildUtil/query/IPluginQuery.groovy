package com.cogujie.buildUtil.query

import com.cogujie.buildUtil.data.PluginData


/**
 * Created by wangzhi on 17/3/2.
 */
public interface IPluginQuery {
    PluginData queryPluginByCoordinates(String coordinates)

    void putCoordinatesToPlugin(String coordinates, PluginData pluginData)
}
