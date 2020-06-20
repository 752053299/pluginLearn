package com.cogujie.buildUtil.Util

import org.gradle.api.GradleException

/**
 * Created by wangzhi on 17/2/15.
 */
public class ProguardTool {

    boolean mappingReady = false

    private HashMap<String, String> proguardMap;

    ProguardTool() {
        proguardMap = new HashMap<>()
    }

    /**
     * 读取proguard的mappingFile
     */
    public void readMappingFile(File mappingFile) {

        proguardMap = new HashMap<>()
        mappingReady = true

        MLog.i("start read mapping map, mapping file is " + mappingFile.absolutePath)
        mappingFile.readLines().each { line ->
            line = line.trim();
            // Is it a non-comment line?
            if (!line.startsWith("#")) {
                // Is it a class mapping or a class member mapping?
                if (line.endsWith(":")) {
                    int arrowIndex = line.indexOf("->");
                    if (arrowIndex < 0) {
                        return null;
                    }
                    int colonIndex = line.indexOf(':', arrowIndex + 2);
                    if (colonIndex < 0) {
                        return null;
                    }
                    // Extract the elements.
                    String className = line.substring(0, arrowIndex).trim();
                    String newClassName = line.substring(arrowIndex + 2, colonIndex).trim();
                    proguardMap.put(newClassName.replace(".", "/") + ".class", className.replace(".", "/") + ".class")

                }

            }
        }

    }


    public String getRealName(String proguardName) {
        checkMappingReady()
        return proguardMap.get(proguardName)

    }

    public void checkMappingReady() {
        if (!mappingReady) {
            throw new GradleException("proguard is open,but mapping file are not ready.")
        }
    }

}
