package com.cogujie.buildUtil.config

/**
 * Created by wangzhi on 17/2/28.
 */
public interface IRunTimeProvider {
    String getPluginPathInRunTime(String fileName)

    String getPluginPathInApk(String fileName)
}
