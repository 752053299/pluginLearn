package com.cogujie.buildUtil.Util.parser

import com.cogujie.buildUtil.Util.Utils
import groovy.xml.QName

/**
 * Created by wangzhi on 17/2/21.
 */
public class ManifestSimpleParser extends AbXmlParser {

    public Set<String> activities = new HashSet<>()
    public Set<String> services = new HashSet<>()
    public Set<String> receivers = new HashSet<>()
    public Set<String> providers = new HashSet<>()

    ManifestSimpleParser(File manifestFile) {
        super(manifestFile)
    }

    private Set<String> parseComponents(def application, String componentName, String packageName) {
        Set<String> sets = new HashSet<>()
        def androidNameSpace = new QName("http://schemas.android.com/apk/res/android", "name", "android")
        application[componentName].each { node ->
            node = node as Node
            String name = node.attribute(androidNameSpace)
            sets.add(Utils.getFQN(packageName, name))
        }
        return sets
    }

    public void parse() {

        Node manifest = getRootNode()

        String packageName = manifest['@package']

        activities = parseComponents(manifest.application, "activity", packageName)
        services = parseComponents(manifest.application, "service", packageName)
        receivers = parseComponents(manifest.application, "receiver", packageName)
        providers = parseComponents(manifest.application, "provider", packageName)
    }


    public static void main(String[] args) {
        ManifestSimpleParser parer = new ManifestSimpleParser(new File("D:\\work\\MyTestApp\\demo\\build\\intermediates\\bundle_manifest\\debug\\bundle-manifest\\AndroidManifest.xml"))
        parer.parse()
        parer.activities.getAt(0).substring()
    }

}
