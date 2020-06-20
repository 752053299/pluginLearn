package com.cogujie.buildUtil.Util


import com.cogujie.buildUtil.Constant
import com.cogujie.buildUtil.data.LibraryData
import com.cogujie.buildUtil.data.PluginData
import org.gradle.api.Project

//import com.android.xml.AndroidManifest
import org.gradle.api.tasks.Exec

import java.security.MessageDigest

/**
 * Created by wangzhi on 16/3/10.
 */
class Utils {


    public static boolean isStringEmpty(String s) {
        return (s == null || s.length() == 0)
    }

    public static boolean isCollectionEmpty(Object collection) {
        return (collection == null || collection.size() == 0)
    }

    static def unzip(String outputFolder, String zipFilePath) {
//        final String cmd = "unzip -o ${zipFilePath} -d ${outputFolder}";
        ArrayList<String> args = new ArrayList<>()
        args.add("unzip")
        args.add("-o")
        args.add(zipFilePath)
        args.add("-d")
        args.add(outputFolder)
        Exec.exec(args, null)

    }

    public static String getFQN(String packagePath, String name) {
        if (name.startsWith(".")) {
            return packagePath + name
        } else {
            return name
        }
    }

//
//    public
//    static String curl(long port, int connect_time, String index, String... otherArgs) {
//        ArrayList<String> args = new ArrayList<String>();
//        args.add("curl")
//        if (connect_time != -1) {
//            args.add("-m")
//            args.add(connect_time)
//        }
//
//        args.add("http://127.0.0.1:" + port + "/" + index)
//        if (otherArgs != null) {
//            otherArgs.each { otherArg ->
//                args.add(otherArg)
//            }
//        }
//
//        return new Exec().exec(args);
//    }
//
//    public
//    static String curlT(long port, String filePath, String index) {
//        ArrayList<String> args = new ArrayList<String>();
//        args.add("curl")
//        args.add("-T")
//        args.add(filePath)
//        args.add("http://127.0.0.1:" + port + "/" + index)
//        return new Exec().exec(args);
//    }

    public static XmlNodePrinter printXml(OutputStream os, Node node) {
        XmlNodePrinter xmlNodePrinter = new XmlNodePrinter(new PrintWriter(os))
        xmlNodePrinter.setPreserveWhitespace(true)
        xmlNodePrinter.print(node)
    }


    public static byte[] toByteArray(final InputStream input) throws IOException {
        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        final byte[] buffer = new byte[8024];
        int n = 0;
        long count = 0;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        return output.toByteArray();
    }


    public static String md5(File file) {
        return MessageDigest.getInstance("MD5").digest(file.bytes).encodeHex().toString()
    }

    public static String sha1(File file) {
        return MessageDigest.getInstance("sha1").digest(file.bytes).encodeHex().toString()
    }


    public static File getSpecParentFile(File file, String name) {
        File parent = file.parentFile
        while (parent != null && !parent.name.equals(name)) {
            parent = parent.parentFile
        }
        return parent

    }

    public static String getCoordinates(String groupId, String artifactId, String version) {
        return groupId + ":" + artifactId + ":" + version
    }
    public static String getCoordinates(LibraryData libData) {
        return libData.coordinate
    }
    public static String getCoordinatesNoVersion(String groupId, String artifactId) {
        return groupId + ":" + artifactId
    }

    public static String getCoordinatesNoVersion(LibraryData libData) {
        return libData.groupId + ":" + libData.artifactId
    }

    public static String getPackageNameFromManifest(File manifest) {

        Node rootNode = new XmlParser().parse(manifest)
        return rootNode['@package']
    }



    public static void closeQuietly(Closeable co) {
        try {
            if (co != null) {
                co.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static final String CLASS_SUFFIX = ".class"

    public static String path2FQN(String path) {
        if (path.endsWith(CLASS_SUFFIX)) {
            String FQN = path.substring(0, path.length() - CLASS_SUFFIX.length())
            return FQN.replace(File.separator, ".")
        } else {
            throw new RuntimeException("path must end with \".class\" ")
        }

    }


    public static String FQN2Path(String classFQN) {
        String path = classFQN.replace(".", File.separator)
        return path + CLASS_SUFFIX
    }


    public static <T> T getOnlyOneElement(Iterable<T> iterable) {
        return getOnlyOneElement(iterable.iterator())
    }

    public static <T> T getOnlyOneElement(Iterator<T> iterator) {
        T element = null
        int count = 0;
        while (iterator.hasNext()) {
            T next = iterator.next()
            if (element == null) {
                element = next
            }
            count++;
        }
        if (count != 1) {
            throw new RuntimeException("except size is 1,but found " + count)
        }
        return element
    }

    public static Collection<String> getDepForLocalModule(Project rootProject, String projectPath) {
        Project targetProject = rootProject.findProject(projectPath)
        Collection<String> coordinatesSet = new HashSet<>()

        targetProject.configurations.each { config ->
            config.allDependencies.each { dep ->
                String coordinates = getCoordinatesNoVersion(dep.group, dep.name)
                //排除无效的dep
                if (dep.group != null) {
                    coordinatesSet.add(coordinates)
                }
            }
        }

        return coordinatesSet
    }

    static boolean isDebug(def variant) {
        return variant.name.capitalize().toLowerCase().contains("debug")
    }

    static boolean isRelease(def variant) {
        return variant.name.capitalize().toLowerCase().contains("release")
    }
    /**
     *  this method may be hook in split plugin
     */
    public static String getType(Node module, boolean isDebug) {
        return Constant.COM_TYPE
    }

    public static boolean isProguardOpen(def variant) {
        return variant.getBuildType().minifyEnabled
    }

    static boolean isPluginType(PluginData pluginData) {
        return Constant.PLUGIN_TYPE.equals(pluginData.type)
    }

    static boolean isLazyInit(PluginData pluginData) {
        return Constant.LAZY_INIT_TYPE.equals(pluginData.init)
    }



}
