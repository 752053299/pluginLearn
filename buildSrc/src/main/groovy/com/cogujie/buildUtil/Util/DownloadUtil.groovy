package com.cogujie.buildUtil.Util


import org.gradle.api.Project
import org.gradle.api.artifacts.repositories.ArtifactRepository

/**
 * Created by wangzhi on 17/4/10.
 */
public class DownloadUtil {
    Project project
    List<String> urls

    DownloadUtil(Project project) {
        this.project = project
        project.extensions
        urls = new ArrayList<>()
        Iterator iterator = project.repositories.iterator()
        while (iterator.hasNext()) {
            ArtifactRepository rep=  iterator.next()
            String name = rep.getName()
            if (!name.toLowerCase().contains("local")) {
                urls.add(rep.getUrl().toString())
            }
        }
    }


    String openStringFormMaven(String suffix) {
        String result = null
        for (String url : urls) {
            String remote = url.endsWith("/") ? (url + suffix) : (url + "/" + suffix)
            try {
                result = new URL(remote).openStream().text
                println "download from ${remote} success"
                break
            } catch (Exception e) {
                println "download from ${remote}  falid"
            } finally {

            }
        }
        return result

    }


    boolean downFromMaven(String suffix, File local) {
        FileUtils.initFile(local)

        boolean result = false
        for (String url : urls) {
            def out = new BufferedOutputStream(new FileOutputStream(local))
            String remote = url.endsWith("/") ? (url + suffix) : (url + "/" + suffix)
            try {
                out << new URL(remote).openStream()
                println "download from ${remote} to ${local} success"
                result = true
                break
            } catch (Exception e) {
                e.printStackTrace()
                local.delete()
                println "download from ${remote} to ${local} falid"
                result = false
            } finally {
                out.close()
            }
        }
        return result
    }

    public static void main(String[] args) {
        String result
        result = new URL("http://gitlab.mogujie.org/bigandroid/dexsplitplugin/blob/master/src/main/groovy/mgdexsplit/plugin/Utils.groovy").openStream().text
        println(result)
//        DownloadUtil downloadUtil = new DownloadUtil(null)
//        downloadUtil.urls.add("file:/Users/farmerjohn/Library/Android/sdk/extras/google/m2repository/")
//        downloadUtil.urls.add("http://maven.mogujie.org/nexus/content/groups/mitpublic/")
//
//        downloadUtil.downFromMaven("com/mogujie/dynamic_server/0.0.1/dynamic_server-0.0.1.pom", new File("/Users/farmerjohn/IdeaProjects/pandora-gradle/tmp"))
    }
}
