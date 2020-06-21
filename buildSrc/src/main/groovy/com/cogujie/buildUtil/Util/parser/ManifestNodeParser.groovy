package com.cogujie.buildUtil.Util.parser

import groovy.xml.QName
/**
 * Created by wangzhi on 17/2/27.
 */
public class ManifestNodeParser extends AbXmlParser {


    public Map<String, Node> activityMap = new HashMap<>()
    public Map<String, Node> serviceMap = new HashMap<>()
    public Map<String, Node> receiverMap = new HashMap<>()
    public Map<String, Node> providerMap = new HashMap<>()


    ManifestNodeParser(File manifestFile) {
        super(manifestFile)
    }

    private Map<String, Node> parseComponents(def application, String componentName) {
        Map<String, Node> map = new HashMap<>()
        def androidNameSpace = new QName("http://schemas.android.com/apk/res/android", "name", "android")

        application[componentName].each { node ->
            node = node as Node
            String name = node.attribute(androidNameSpace)
            map.put(name, node)
        }
        return map
    }


    void parse() {
        Node manifest = getRootNode()

        activityMap = parseComponents(manifest.application, "activity")
        serviceMap = parseComponents(manifest.application, "service")
        receiverMap = parseComponents(manifest.application, "receiver")
        providerMap = parseComponents(manifest.application, "provider")

    }



    public static void main(String[] args) {

    }


}
