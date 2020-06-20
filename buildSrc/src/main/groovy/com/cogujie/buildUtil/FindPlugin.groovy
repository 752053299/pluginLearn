package com.cogujie.buildUtil


import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import com.android.build.gradle.internal.variant.ApplicationVariantData
import com.android.build.gradle.tasks.MergeSourceSetFolders
import com.cogujie.buildUtil.Util.FileUtils
import com.cogujie.buildUtil.Util.MLog
import com.cogujie.buildUtil.Util.ProguardTool
import com.cogujie.buildUtil.Util.Utils
import com.cogujie.buildUtil.config.ConfigReader
import com.cogujie.buildUtil.config.ConfigWriter
import com.cogujie.buildUtil.config.IRunTimeProvider
import com.cogujie.buildUtil.data.DefaultPluginStuffer
import com.cogujie.buildUtil.data.LibraryData
import com.cogujie.buildUtil.data.PluginInitializer
import com.cogujie.buildUtil.query.DataQuery
import groovy.xml.MarkupBuilder
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task

import java.lang.reflect.Field

class FindPlugin implements Plugin<Project> {

    //  private File moduleConfigFile
    //private File moduleConfigTempFile

    private static final byte[] sy = new byte[4];

    @Override
    void apply(Project project) {
        println "${project.name} apply find plugin"
        project.buildscript.dependencies.getAttributesSchema().getAttributes().each {
            println(it.name)
        }
        BaseAppModuleExtension be = project.android
        project.afterEvaluate {
            be.getApplicationVariants().each { baseVariant ->
                ApplicationVariantData vd = baseVariant.variantData
                println(vd.variantConfiguration.getFullName())
                String variantName = baseVariant.name.capitalize()
                def afterPreBuildTask = project.tasks.findByName("pre${variantName}Build")

                afterPreBuildTask.doLast {
                    println(afterPreBuildTask.name + " do last")

                    ProguardTool proguardTool = null
                    if (Utils.isProguardOpen(baseVariant)) {
                        proguardTool = new ProguardTool()
                    }

                    //1.get all aar data from dependencies
                    LibraryProvider lp = new LibraryProvider(project, baseVariant)
                    Set<LibraryData> libs = lp.getLibraries()
                    //   2.analyse library's dependencies
                    DependencyAnalyzer analyzer = new DependencyAnalyzer(project, libs)
                    analyzer.analyseDependencies()
////                    //3.read module config
//                    File moduleConfigFile = FileUtils.joinFile(project.projectDir, "src", "main", "assets", "ModuleConfig.xml")
//                    ConfigReader moduleReader = new ConfigReader(moduleConfigFile, Utils.isDebug(baseVariant))
//                    moduleReader.readConfigFile()

//                    //4.generate plugin data from configure
//                    String packageName = vd.getVariantConfiguration().getOriginalApplicationId()
//                    DataQuery dataQuery = new DataQuery(libs, proguardTool, baseVariant.versionName)
//                    PluginInitializer core = new PluginInitializer(dataQuery, packageName)
//                    core.generate(new DefaultPluginStuffer(), moduleReader)
//
//                   // 5.write config
//                    ConfigWriter configWriter = new ConfigWriter(dataQuery,new IRunTimeProvider() {
//                        @Override
//                        String getPluginPathInRunTime(String fileName) {
//                            //path="assets/ModuleLifeStylePublish.apk
//                            return  Constant.ASSETS +"/"+ fileName
//                        }
//                        @Override
//                        String getPluginPathInApk(String fileName) {
//                            return ""
//                        }
//                    })
//
//
//                    File moduleConfigTempFile = FileUtils.joinFile(project.projectDir, "ModuleConfig.xml")
//
//
//                    if (!moduleConfigTempFile.exists()) {
//                        boolean copySuccess = FileUtils.testCopy(moduleConfigFile, moduleConfigTempFile)
//                        MLog.i("is copy ModuleConfig.xml success :  " + copySuccess)
//                    }
//
//                    //根据TempConfig文件中的内容，组装新内容，并写入assets中的ModuleConfig.xml中
 //                     configWriter.writeConfig(moduleConfigTempFile,moduleConfigFile)
                }

                MergeSourceSetFolders mergeTask = project.tasks.findByName("merge${variantName}Assets")
                mergeTask.doLast {
                    File outPutFile = mergeTask.getOutputDir().asFile.get()
                    println(outPutFile)
                    File targetFile = new File(outPutFile,"ModuleConfig.xml")
                    if (targetFile == null){
                        throw new GroovyRuntimeException("can not find ModuleConfig.xml in ${outPutFile.absolutePath}")
                    }
                    writeFile(targetFile)
                }
            }
        }

        project.gradle.buildFinished {
            println("project do finish")
//            File moduleConfigFile = FileUtils.joinFile(project.projectDir, "src", "main", "assets", "ModuleConfig.xml")
//            File moduleConfigTempFile = FileUtils.joinFile(project.projectDir, "ModuleConfig.xml")
//
//            FileUtils.moveFile(moduleConfigTempFile, moduleConfigFile)
        }
    }


    public static Field[] getAllFields(Object object) {
        Class clazz = object.getClass();
        List<Field> fieldList = new ArrayList<>();
        while (clazz != null) {
            fieldList.addAll(new ArrayList<>(Arrays.asList(clazz.getDeclaredFields())));
            clazz = clazz.getSuperclass();
        }
        Field[] fields = new Field[fieldList.size()];
        fieldList.toArray(fields);
        return fields;
    }


    private static void writeFile(File file) {
        def sw = new StringWriter();
        def xml = new MarkupBuilder(sw)
        xml.MSG(type: "current") {
            module("oooo")
            module("dadfa")
            module("aazzzz")
        }
        file.write(sw.toString())
        println("write file ")
    }
}