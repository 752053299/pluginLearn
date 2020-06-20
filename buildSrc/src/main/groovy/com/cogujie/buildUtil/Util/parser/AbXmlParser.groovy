package com.cogujie.buildUtil.Util.parser

/**
 * Created by wangzhi on 17/2/27.
 */
public class AbXmlParser implements IXmlParser {

    File xmlFile

    AbXmlParser(File xmlFile) {
        this.xmlFile = xmlFile
    }

    @Override
    void parse() {

    }

    @Override
    Node getRootNode() {
        return new XmlParser().parse(xmlFile)
    }
}
