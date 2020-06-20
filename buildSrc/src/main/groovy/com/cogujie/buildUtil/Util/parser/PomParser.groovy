package com.cogujie.buildUtil.Util.parser

import com.cogujie.buildUtil.Util.Utils

/**
 * Created by wangzhi on 17/2/21.
 */
public class PomParser extends AbXmlParser {

    Set<String> depCoordinateSet

    PomParser(File xmlFile) {
        super(xmlFile)
    }

    @Override
    void parse() {
        depCoordinateSet = new HashSet<>()
        Node project = getRootNode()
        project.dependencies.dependency.each { dep ->
            depCoordinateSet.add(Utils.getCoordinatesNoVersion(dep.groupId.text(), dep.artifactId.text()))
        }
    }

    public static void main(String[] args) {
        PomParser parser = new PomParser(new File("/Users/farmerjohn/.gradle/caches/modules-2/files-2.1/com.mogujie.tinker/tinker-android-lib/0.0.1.1-MAIN/3b8fdf5a358d8df50cd3b48a18146fde39aac143/tinker-android-lib-0.0.1.1-MAIN.pom"))
        parser.parse()
        println(parser.depCoordinateSet)
    }
}
