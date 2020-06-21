#proguard相关注意：
#1.新增数据类
#2.新增反射逻辑的类
#3.新增第三方库
#4.新增注解实现类
#5.新增 .so load的类
#
#请@伯约

#hotpatch时需要打开 这两个开关
#----------------------------
-applymapping ../mapping/code-mapping.txt
#------------------------------

-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontskipnonpubliclibraryclassmembers
#-useuniqueclassmembernames

-verbose
-dontoptimize
-dontpreverify
#-dontobfuscate


# 这一堆是影响打包的
-dontwarn javax.annotation.**
-dontwarn org.eclipse.jdt.annotation.**
-dontwarn java.awt.image.BufferedImage
-dontwarn okio.**
-dontwarn okhttp3.**
-dontwarn org.jetbrains.annotations.**
-dontwarn com.facebook.**
-dontwarn com.googlecode.**
-dontwarn com.tencent.**
#以下是pandora-client用到的被@hide的framework层api
-dontwarn android.app.ActivityThread
-dontwarn android.app.IActivityManager
-dontwarn android.app.LoadedApk
-dontwarn android.app.ResourcesManager
-dontwarn android.app.IAppTask
-dontwarn android.app.Instrumentation
-dontwarn android.content.pm.IPackageManager
-dontwarn android.content.res.ResourcesImpl
-dontwarn android.content.res.ResourcesKey
-dontwarn android.content.res.CompatibilityInfo
-dontwarn android.content.res.ResourcesKey
-dontwarn android.os.Binder
-dontwarn android.os.IBinder
-dontwarn android.os.IInterface
-dontwarn android.view.HardwareRenderer
-dontwarn android.view.HardwareRenderer$HardwareDrawCallbacks
-dontwarn android.**
-dontwarn android.view.ViewRootImpl
-dontwarn android.view.DisplayAdjustments
-dontwarn com.android.internal.content.NativeLibraryHelper
-dontwarn com.android.internal.content.NativeLibraryHelper$Handle
-dontwarn com.android.internal.content.ReferrerIntent
#以上是pandora-client用到的被@hide的framework层api

#-dontwarn com.mogujie.android.tangram.template.**
#-dontwarn com.mogujie.shoppingguide.bizview.ShoppingGuideSelectBannerView$SelectBannerViewHolder


#保留行号信息
-keepattributes SourceFile,LineNumberTable

-keepattributes *Annotation*
-keep public class com.google.vending.licensing.ILicensingService
-keep public class com.android.vending.licensing.ILicensingService

# For native methods, see http://proguard.sourceforge.net/manual/examples.html#native
-keepclasseswithmembernames,includedescriptorclasses class * {
    native <methods>;
}

# keep setters in Views so that animations can still work.
# see http://proguard.sourceforge.net/manual/examples.html#beans
-keepclassmembers public class * extends android.view.View {
   void set*(***);
   *** get*();
}

# We want to keep methods in Activity that could be used in the XML attribute onClick
-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}
#-keep public class * extends android.app.Activity
#-keep public class * extends android.app.Application
#-keep public class * extends android.app.Service
#-keep public class * extends android.content.BroadcastReceiver
#-keep public class * extends android.content.ContentProvider



# For enumeration classes, see http://proguard.sourceforge.net/manual/examples.html#enumerations

# see https://github.com/google/gson/issues/572
-keepclassmembers enum * {*;}

-keepclassmembers class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator CREATOR;
}

-keepclassmembers class **.R$* {
    public static <fields>;
}

# The support library contains references to newer platform versions.
# Don't warn about those in case this app is linking against an older
# platform version.  We know about them, and they are safe.
-dontwarn android.support.**


# 统一保护所有数据类
-keep  class **.data.** { *;}
-keep  class **.model.** { *;}
-keep  class **.module.** { *;}
-keep  class **.entity.** { *;}
-keep  class **.Data.** { *;}
-keep  class **.Module.** { *;}
# 统一保护需要序列化的类
-keep class * implements java.io.Serializable {*;}

# buildconfig
-keep class com.mogujie.BuildConfig {*;}




# 下面是对组件的单独配置
#------------------------------------------------------------------
# com.mogujie.im.uikit:audio @不觉
#-dontwarn com.mogujie.im.uikit.audio.**$**
-keep class com.mogujie.im.uikit.audio.**$** {*;}

#com.mogujie:securityData @东榆
-keep class com.tencent.StubShell.** {* ;}

#com.mogujie:security_chain @东榆
-keep class android.content.pm.PackageInfo {* ;}
-keep class android.content.pm.PackageManager {* ;}
-keep class android.content.Context {* ;}

#com.mogujie:security_check @东榆
-keep class android.content.Context {* ;}
-keep class android.content.res.AssetManager {* ;}

#com.mogujie:emulator @东榆
-keep class com.mogujie.emulatorlib.EmulatorAppInstall {* ;}

#com.mogujie:debugcore @独白
-keep class com.mogujie.mgbasicdebugitem.uitil.JniCrash { *; }

#com.mogujie.android:mwpsdk-debug
#com.mogujie.android:mwpsdk-socket
#com.mogujie.android:mwpsdk-api @盖瑞
-keep class com.mogujie.mwpsdk.domain.** {* ;}
-keep class com.mogujie.mwpsdk.api.** {*;}

#com.mogujie.android:mwcs-client @盖瑞 橡皮
-keep class com.mogujie.mwcs.library.model.** {*;}
-keep class com.mogujie.mwcs.library.Invocation {*;}
-keep class com.mogujie.mwcs.library.Session {*;}
-keep class * implements  com.mogujie.mwcs.library.TransportManager{*;}
-keep class com.mogujie.mwcs.library.mars.MarsSession {*;}
-keep class com.mogujie.mwcs.library.mars.MarsInvocation {*;}
-keep class com.tencent.mars.** {*;}

#------------------------------------------
#-dontwarn  **.domain.**
#-keep  class **.domain.** { *;}
#
#-dontwarn  com.mogujie.mwpsdk.**
#-keepnames  class  com.mogujie.mwpsdk.** {*;}
#
#-keep class * implements com.mogujie.mwpsdk.api.IRemoteCallback {*;}
#-keep     class * implements com.mogujie.mwpsdk.common.ISign  {*;}
#-keep     class * implements com.mogujie.mwpsdk.common.IAdapterHelper  {*;}
#-keep     class * implements com.mogujie.mwpsdk.network.INetWorkFactory  {*;}
#-keep     class * implements com.mogujie.mwpsdk.common.mstate  {*;}
#
#-dontwarn com.mogujie.token.UrlTokenMaker
-keep class  com.mogujie.token.UrlTokenMaker { *;}
#-----------------------------------------------------------

# com.mogujie.protocol:collectionpipe @海猪
-keep class com.mogujie.protocol.collection.EnvConfigImpl {*;}

#com.mogujie:uninstall_observer @海猪
-keep class com.mogujie.uninstall.** {*;}

# com.mogujie:msh @海猪
-keep class * extends com.mogujie.msh.ModuleApplication {*;}
-keep class * implements com.mogujie.msh.IAppLifecycle {*;}
-keep class com.mogujie.login.LoginRouter {*;}
-keep class com.mogujie.login.UserManager {*;}
#-keep class * extends com.mogujie.msh.ModuleService {*;}

-keep class com.mogujie.mgrouter.MGRouter$RouterGo {*;}

#h5相关@怀铅@哲西
#----------------------------------------------------
#com.mogujie.hdp.mgjhdpplugin:pullrefresh
#com.mogujie.mdata.module.hybirdevent:lib
#com.mogujie.xteam:WebviewInterface
#com.mogujie.hdp.mgjhdpplugin:notification
#com.mogujie.hdp.mgjhdpplugin:device
#com.mogujie.hdp.mgjhdpplugin:pevent
#com.mogujie.hdp.mgjhdpplugin:mgjglobalnotification
#com.mogujie.hdp.mgjhdpplugin:progress
#com.mogujie.hdp.mgjhdpplugin:appinfo
#com.mogujie.hdp.mgjhdpplugin:bundle
#com.mogujie.hdp.plugins:calendar
#com.mogujie:downloader
#com.mogujie.hdp.plugins:camera
#com.mogujie:webviewlibrary
#com.mogujie.hdp.plugins:contact
#com.mogujie.hdp.mgjhdpplugin:tracker
#com.mogujie.hdp.mgjhdpplugin:utils
#com.mogujie.hdp.mgjhdpplugin:location
#com.mogujie.hdp.plugins:mitengine
#com.mogujie.hdp.mgjhdpplugin:security
#com.mogujie.hdp.mgjhdpplugin:user
#com.mogujie.hdp.mgjhdpplugin:ajax
#com.mogujie.hdp.mgjhdpplugin:share
#com.mogujie.hdp.mgjhdpplugin:image
#com.mogujie.hdp:bundle
#com.mogujie.xteam:WebviewInterface
#com.mogujie:webContainerApp4Mgj
#com.mogujie.hdp.mgjhdpplugin:debug
#com.mogujie.hdp:framework
#com.mogujie.hdp.mgjhdpplugin:navigation
-dontwarn  org.apache.**
-keep class  org.apache.** { *;}
-keep class web.** { *;}
-dontwarn org.chromium.**
-keep class  org.chromium.** { *;}
# web view setting related class
-keep class mogujie.impl.** { *;}
#webview implement class
-keep class mogujie.impl.android.** { *;}
#webview interface related classes
-keep class mogujie.Interface.** { *;}
-keep class mogujie.impl.MGWebViewController {*;}
-keep class com.mogujie.WebEvent { *;}
-keep class com.mogujie.offsite.web.withPlugin.OffSitePluginWebView { *;}
# basic plugin of mogujie
-keep class com.mogujie.web.plugin.** { *;}
-keep class  com.mogujie.hdp.** { *;}
-keep class org.apache.cordova.plugin.** { *;}
# 下载模块
-keep class  com.mogujie.download.api.comdownload.** { *;}



#xcore相关@燕行
#com.mogujie:xcore
#com.mogujie:xcoreApp4Mgj
-keep class com.mogujie.xcore.ui.CoreContextManager { *; }
-keep class com.mogujie.xcore.webView.GetXcFile { *; }
-keep class com.mogujie.xcore.webView.ParserXcCallBack { *; }
-keep class * extends com.mogujie.jscore.core.JavaObjectWrap { *;}
-keep class com.mogujie.jscore.core.** { *; }
-keep class com.mogujie.jscore.adapter.** { *;}
-keep class com.mogujie.jscore.thread.ThreadTimer { *; }
-keep class * implements com.mogujie.jscore.adapter.** { *; }
-keep class com.mogujie.xcore.ui.nodeimpl.anim.AnimProperties { *; }
-keep class com.mogujie.xcore.xc.IndexXc { *; }
-keep class mogujie.impl.MGWebViewController {*;}
-keep class com.mogujie.xcoreapp4mgj.XCoreAppController{ *; }
-keep class com.mogujie.hdp.framework.util.**$** { *; }
-keep class com.mogujie.xcore.webView.XCoreWebView { *; }
-keep class com.mogujie.xcore.ui.nodeimpl.** {*;}


#com.mogujie:finger_print_mgj
#com.mogujie:finger_print @卡赞
-keep class  com.mogujie.fingerprint.FingerPrint { *;}
-keep class com.mogujie.security.EncryptUtils {*;}


#com.mogujie.android:mwpsdk-adapter @克雷
-keep class com.mogujie.mwpsdk.adapter.MWPRequestManager {*;}

#com.mogujie:tangram @克雷
-keep class com.mogujie.android.tangram.config.ItemView {*;}
-keepclassmembers class * {
    @com.mogujie.android.tangram.config.ItemView *;
}
-keep class * implements com.mogujie.android.tangram.module.ModuleView {*;}
-keep class * extends com.mogujie.android.tangram.module.BaseModuleView {*;}
-keep class * implements com.mogujie.android.tangram.data.IData {*;}
-keep class * implements com.mogujie.android.tangram.data.IListData {*;}
-keep class * implements com.mogujie.android.tangram.data.IListInfoData {*;}
-keep class * implements com.mogujie.android.tangram.data.IPagingTypeData {*;}
-keep class * extends com.mogujie.android.tangram.data.BaseMWPData {*;}
-keep class * extends com.mogujie.android.tangram.data.MaitBaseData {*;}
-keep class com.mogujie.android.tangram.template.TemplateRequest** {*;}

# vlayout
-keepattributes InnerClasses
-keep class com.alibaba.android.vlayout.ExposeLinearLayoutManagerEx { *; }
-keep class android.support.v7.widget.RecyclerView$LayoutParams { *; }
-keep class android.support.v7.widget.RecyclerView$ViewHolder { *; }
-keep class android.support.v7.widget.ChildHelper { *; }
-keep class android.support.v7.widget.ChildHelper$* { *; }
-keep class android.support.v7.widget.RecyclerView$LayoutManager { *; }

#com.mogujie:net @克雷
-dontwarn com.android.volley.**
-keep class com.android.volley.** { *;}
-dontwarn org.msgpack.template.builder.ReflectionTemplateBuilder
-keep class org.msgpack.template.builder.ReflectionTemplateBuilder { *;}

#com.mogujie:api @克雷
-keep class com.minicooper.api.BaseApi {*;}
-keep class com.mogujie.mwpsdk.adapter.MWPRequestManager {*;}
-keep class com.minicooper.dns.HttpDnsManager {*;}
-keep class * extends com.minicooper.model.MGBaseData {*;}

# lego
-keep class com.mogujie.componentizationframework.template.tools.TemplateTimeUtils {*;}
-keep class com.mogujie.componentizationframework.core.data.** {*;}
-keep class com.mogujie.componentizationframework.core.component.** {*;}
-keep class com.mogujie.componentizationframework.core.recycler.** {*;}
-keep class com.mogujie.componentizationframework.core.vlayout.** {*;}
-keep class com.mogujie.lego.ext.component.** {*;}
-keep class com.mogujie.lego.ext.container.** {*;}
-keep class com.mogujie.lego.ext.data.** {*;}
-keep class **.ComponentRegisterMap { *;}
-keep class com.mogujie.lego.ext.view.** {
      public <init>(android.content.Context, android.util.AttributeSet,int);
      public <init>(android.content.Context, android.util.AttributeSet);
      public <init>(android.content.Context);
}

#com.mogujie:cart @深绿
-keep class com.mogujie.cart.MGCartFragment { *; }

#com.mogujie:payback 支付成功页相关 @欧祎
-keep class com.mogujie.payback.** {*;}


#直播相关@五木
#com.mogujie.facedetector:facedetector
#jp.co.cyberagent.android.gpuimage:gpuimage
#com.mogujie:live
#com.mogujie:livevideo
#com.mogujie:floatwindow
#com.mogujie:livecomponent
-keep class com.mogujie.guigu.skinsoftenlib.SkinSoften{ *; }
-keep class com.mogujie.livevideo.render.FaceStickerNativeUtil{ *; }
-keep class main.java.com.mogujie.facedetector.*{ *; }
-keep class jp.co.cyberagent.android.gpuimage.*{ *; }
-keep class com.tencent.**{ *; }
-keep class com.mogujie.floatwindow.*{ *; }
-keep class tencent.tls.**{ *; }
-keep class com.mogujie.mgsocialeventbus.*{ *; }
-keep class com.mogujie.videoplayer.util.BrightnessManager{*;}
-keep class com.mogujie.live.component.playback.presenter.IPlaybackPresenter{ *;}
-keep class com.mogujie.live.activity.MGLiveViewerActivity{ *;}
-keep class * implements com.mogujie.livevideo.error.LiveError.ICallback{*;}


#com.mogujie:image @逍缈
-keep class com.facebook.imagepipeline.gif.GifImage{ *; }
-keep class com.facebook.imagepipeline.gif.GifFrame{ *; }
-keep class com.facebook.animated.gif.GifFrame{ *; }
-keep class com.facebook.animated.gif.GifFrame{ *; }
-keep class com.facebook.animated.webp.WebPFrame{ *; }
-keep class com.facebook.animated.webp.WebPImage{ *; }
-keep class com.facebook.imagepipeline.memory.NativeMemoryChunk{ *; }
-keep class com.facebook.imagepipeline.nativecode.Bitmaps{ *; }

-keep,allowobfuscation @interface com.facebook.common.internal.DoNotStrip
-keep,allowobfuscation @interface com.facebook.soloader.DoNotOptimize


#长链相关 @樱木
#com.mogujie:mec-android
#com.mogujie.io.mec:network-lib
-keep class com.mogujie.mgcchannel.** { *;}
#-dontwarn com.mogujie.mgcchannel.** 
-keep class com.mogujie.mec.** { *;}
# -dontwarn com.mogujie.mec.**



#com.mogujie.modular:lib @灰谷
-keep class * implements com.mogujie.mgmodular.Modular.marketview.IMarketView {*;}
-keep class com.mogujie.mgmodular.Modular.view.CateData {*;}
-keep class com.mogujie.mgmodular.Modular.view.CellConfig {*;}
-keep class com.mogujie.mgmodular.Modular.MarketViewFactory$** {*;}
-keep class com.mogujie.mgmodular.Modular.MarketViewFactory {*;}
-keep class com.mogujie.mgmodular.Modular.manager.PageConfigManager$** {*;}

#com.mogujie:triplebuy @灰谷
-keep class com.mogujie.triplebuy.triplebuy.view.GridContainer$** {*;}
-keep class com.mogujie.triplebuy.api.UploadResultData {*;}

-keep class com.mogujie.triplebuy.freemarket.newmarketviews.** {
    public <init>(com.mogujie.componentizationframework.core.tools.ComponentContext);
}
-keep class com.mogujie.triplebuy.freemarket.outfitcomponent.** {*;}
#com.mogujie.v2.waterfall @灰谷
-keep class com.mogujie.v2.waterfall.goodswaterfall.api.** {*;}
-keep class com.mogujie.v3.waterfall.goodswaterfall.api.** {*;}
-keep class com.mogujie.v3.waterfall.component.WaterfallComponent {
  public <init>(com.mogujie.componentizationframework.core.tools.ComponentContext);
}
-dontwarn com.mogujie.v2.waterfall.goodswaterfall.api.**
#com.mogujie.fulltank @灰谷
-keep class com.mogujie.fulltank.asyn.** {*;}
-keep class com.mogujie.fulltank.manager.TankManager {*;}
-keep class com.mogujie.fulltank.manager.TaskManager {*;}
#com.mogujie.searchutils
-keep class * implements com.mogujie.searchutils.sortable.view.ComplexFilterView$OnDismissListener {*;}

#com.mogujie.im.uikit:basecommon @屠夫
-keep class com.mogujie.im.uikit.basecommon.adapter.annotation.*  {*;}

#com.mogujie:cardscanner @乘风
-keep class io.card.payment.DetectionInfo
-keepclassmembers class io.card.payment.DetectionInfo {public *;}
-keep class io.card.payment.CreditCard
-keep class io.card.payment.CreditCard$1
-keepclassmembers class io.card.payment.CreditCard {
   *;
}
-keepclassmembers class io.card.payment.CardScanner {
   *** onEdgeUpdate(...);
}
-keep public class io.card.payment.* {
    public protected *;
}

# purse 组件化 @克雷
-keep class com.mogujie.purse.indexv3.components.** {
    public <init>(com.mogujie.componentizationframework.core.tools.ComponentContext);
}
-keep class com.mogujie.purse.indexv3.view.** {
    public <init>(android.content.Context, android.util.AttributeSet,int);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context);
}

# com.mogujie:popup @良暮
-keep class **.PopViewRegisterMap { *;}
-keep public class * implements com.mogujie.popup.interfaces.IPopView { *;}
-keep public class * extends com.mogujie.popup.core.BasePopView { *;}

# com.mogujie:shoppingguide @泪蓝
-keep class com.mogujie.shoppingguide.fragment.MGSGuideHomeFragment {*;}
-keep class com.mogujie.shoppingguide.bizview.** {
    public <init>(com.mogujie.componentizationframework.core.tools.ComponentContext);
}
-keep class com.mogujie.shoppingguide.view.** {
    public <init>(android.content.Context, android.util.AttributeSet,int);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context);
}

# com.mogujie:index @风潇
-keep class com.mogujie.index.fragment.AttentionFragment { *;}
# com.mogujie:outfit @风潇
-keep class com.mogujie.outfit.fragment.GeminiFragment { *;}
-keep class com.mogujie.outfit.component.** { *;}
# feedsdk相关
-keep class * implements com.feedsdk.api.ubiz.base.IDataProvider {*;}
-keep class * implements com.feedsdk.api.ubiz.base.IResponseGetter {*;}

# com.mogujie:me @沐勋
-keep class com.mogujie.me.index.fragment.MeFragment {*;}

# com.mogujie:videoeditor @沐勋
-keep class  com.mogujie.videoeditor.**  { *;}
-keep class com.mp4parser.iso14496.**  {* ;}
-keep class com.mp4parser.iso23001.part7.**  {* ;}
-keep class com.mp4parser.iso23009.part1.**  {* ;}
-keep class com.coremedia.**  { *;}
-keep class com.googlecode.mp4parser.**  {* ;}

# com.mogujie:transformersdk @沐勋
-keep class  com.mogujie.transformersdk.util.GPUImageFilterUtil { *;}

# @商瞿
-keep class com.mogujie.componentizationframework.component.** { *;}
-keep class com.mogujie.componentizationframework.core.** { *;}
-keep class com.mogujie.bill.component.** { *;}
-keep class com.mogujie.detail.compdetail.component.** { *;}
-keep class com.mogujie.detail.compdetail.** { *;}
-keep class com.mogujie.detail.componentizationdetail.** { *;}
-keep class com.mogujie.lifestyledetail.componentization.** { *;}
-keep class * extends com.mogujie.componentizationframework.component.holder.ComponentBaseViewHolder {*;}
-keep class * implements com.mogujie.componentizationframework.component.view.interfaces.DataView {*;}
-keep class * implements com.mogujie.componentizationframework.core.interfaces.IComponent {*;}
-keep class * implements com.mogujie.componentizationframework.core.interfaces.IModelView {*;}
# 详情服务化接口
-keep class * implements com.mogujie.base.comservice.api.IDetailService {*;}
# @佳伟 店铺模块化
-keep class com.mogujie.modulardecorate.** { *;}
-keep class com.mogujie.cssshop.** { *;}
-keep class com.mogujie.csslayout.** { *;}

#com.mogujie.android:awesome.extra
#com.mogujie.android:awesome @小创
-keep class com.mogujie.android.awesome.** { *;}


# gson的配置
#-dontwarn com.google.gson.Gson
#-keep class  com.google.gson.Gson { *;}
-keep class sun.misc.Unsafe { *; }
-keepattributes Signature
-keep class com.google.gson.examples.android.model.** { *; }
-keep class  com.google.gson.Gson { *;}


#vegetaglass
-keep class com.mogujie.utils.VegetaglassPipe {*;}

# rxjava
-dontwarn sun.misc.**
-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
 long producerIndex;
 long consumerIndex;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
 rx.internal.util.atomic.LinkedQueueNode producerNode;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueConsumerNodeRef {
 rx.internal.util.atomic.LinkedQueueNode consumerNode;
}

#com.mogujie.im:mgimclient-android @疯紫
-keep class com.mogujie.im.db.dao.**  { *;}
-keep class com.mogujie.im.db.abstraction.**  { *;}
-keep class * extends com.mogujie.im.db.abstraction.DaoSupport {*;}
-keep class * extends de.greenrobot.dao.AbstractDaoMaster {*;}
-keep class com.mogujie.im.nova.** { *;}

#com.mogujie.imcloud:imsdk-android @疯紫
-keep  class  com.mogujie.imsdk.core.channel.filter**  { *;}
-keep  class  com.mogujie.imsdk.core.datagram.protocol.pb**  { *;}
-keep  class  com.mogujie.imsdk.core.support.db.dao**  { *;}
-keep class * implements com.mogujie.imsdk.core.service.IService {*;}
-keep class com.mogujie.imsdk.core.datagram.protocol.** {*;}
-keep class * extends com.mogujie.imsdk.core.support.db.abstraction.DaoSupport {*;}
#jni回调调用
-keep class com.mogujie.im.io.dynamic.Network {*;}


#com.mogujie.io:network-lib-new @疯紫
-keep class com.mogujie.io.**{ *;}
# com.mogujie.im.network:hermes @越前
-keep class com.mogujie.im.network.** {*;}

#com.mogujie.im.uikit:bottombar @疯紫
-keep class * implements com.mogujie.im.uikit.bottombar.morepanel.ItemView {*;}
-keep class com.mogujie.im.uikit.bottombar.anno.**  { *;}

#com.mogujie:acra @东野
-keep class com.mogujie.mgacra.collector.**{ *;}
-keep class com.mogujie.mgacra.utils.** { *;}
-keep class com.mogujie.mgacra.listeners.** { *;}
-keep class com.mogujie.mgacra.GalileoManager { *;}
-keep class com.mogujie.mgacra.GalileoSender { *;}
-keep class com.mogujie.mgacra.MGACRA { *;}

#prism @东野
-keep class com.mogujie.prism.PrismConfigEntity { *;}

# com.mogujie:mglogincomponent @春小
-keep class com.mogujie.login.coreapi.data.** {*;}
-keep class * implements com.mogujie.login.sdk.ILoginConfig {*;}
-keep class * extends com.mogujie.componentizationframework.template.tools.coach.CoachEvent {*;}
-keep class * {
	void *(com.mogujie.componentizationframework.template.tools.coach.CoachEvent);
}
-keep class * extends com.mogujie.coach.CoachEvent {*;}
-keep class * {
	void *(com.mogujie.coach.CoachEvent);
}


#com.mogujie:share @春小
-keep class com.tencent.** {*;}
-keep class  com.sina.** { *;}
-keep class com.facebook.**
-keep @com.facebook.common.internal.DoNotStrip class *
-keepclassmembers class * {
    @com.facebook.common.internal.DoNotStrip *;
}

# com.mogujie:feedsdk-bizview @风潇
-keep class * implements com.feedsdk.bizview.cmbineview.IViewGetter{*;}
-keep class com.feedsdk.client.api.FeedData{*;}

# com.mogujie:blur @橡皮
-keep class com.mogujie.utils.blur.opengl.functor.** {*;}

# 腾讯定位
-keepattributes *Annotation*
-keepclassmembers class ** {
    public void on*Event(...);
}

# otto
-keepclassmembers class ** {
    @com.squareup.otto.Subscribe public *;
    @com.squareup.otto.Produce public *;
}

# socialeventbus
-keepclassmembers class ** {
    @com.mogujie.mgsocialeventbus.Subscribe public *;
}

#greendao
-keep public class * extends de.greenrobot.dao.**


-keep class sun.security.ssl.SSLContextImpl {*;}

# solar 数据结构keep就好了
-keep class  com.mogujie.commanager.** { *;}


# houston
-keep class * extends com.mogujie.houstonsdk.HoustonExtEntity{*;}
-keep class * extends com.mogujie.houstonsdk.HoustonStub{*;}
-keep class com.mogujie.houstonsdk.HoustonCenter {*;}

# 框架层的配置
-keep class **.ComEntry { *;}
-keep class **.EnvConfigImpl { *;}

# 启动动画
-keep class com.mogujie.base.utils.init.MGWelcomeImageUtils$ListDatasWrapper{*;}
-keep class com.mogujie.base.data.MGWelcomeData$** {*;}
-keep class com.mogujie.base.utils.init.**{*;}

# 欧祎
-keep class com.mogujie.littlestore.widget.LSLoadingView {*;}
-keep class * implements com.feedsdk.bizview.adapter.IAdapter {*;}

-dontwarn com.alipay.**
-keepnames class  com.alipay.** { *;}
#屏幕解锁弹窗 （仅Android）
#-dontwarn com.mogujie.security.analyse.**
-keep class  com.mogujie.security.analyse.**  { *;}
#分享  mgsdk里的  勾尘
-keep class * extends com.mogujie.base.utils.social.ShareBaseData{*;}
-keep class com.mogujie.base.utils.social.ShareShopData$*{*;}
# hosttabbar里面的数据类 沐勋
-keep class  com.mogujie.mghosttabbar.contants.** { *;}

#用户名合法校验
-keep class com.mogujie.base.service.uname.MGCheckUnameData {*;}

# 组件主题
-keepnames class com.mogujie.theme.ThemeData** {*;}
-keep class * implements com.mogujie.theme.Themeable {*;}

# 复制分享预埋的数据类
-keepnames class com.mogujie.cbdetector.GoodsSummary {*;}

#反射keep
-keep class  com.mogujie.utils.EasyDebugInfo {*;}

# leakcanary
-dontwarn com.squareup.haha.guava.**
-dontwarn com.squareup.haha.perflib.**
-dontwarn com.squareup.haha.trove.**
-dontwarn com.squareup.leakcanary.**
-keep class com.squareup.haha.** { *; }
-keep class com.squareup.leakcanary.** { *; }
-dontwarn android.app.Notification

-keep class com.astonmartin.utils.ApplicationContextGetter {*;}
-keep class com.mogujie.uploader.CDNUploader {*;}

#Yoga Relative @QiaoXi
-keep class com.facebook.yoga.** {*;}

#可以去掉的
-keep class com.mogujie.customskus.view.LiveActorSkuView {*;}
-keep class com.mogujie.customskus.view.LiveSkuView {*;}
-keep class com.mogujie.customskus.view.LiveWhiteSkuView {*;}
-keep class com.mogujie.detail.compsku.GDSkuView {*;}
-keep class com.mogujie.normalsku.NormalSkuView {*;}
-keep class com.mogujie.livecomponent.component.common.LiveBasePresenter{*;}


# CoordinatorLayout resolves the behaviors of its child components with reflection.
-keep public class * extends android.support.design.widget.CoordinatorLayout$Behavior {
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>();
}

#appmate
-keep class com.mogujie.appmate.apt.annotation.AMRegisterAnnotation {*;}
-keepclassmembers class * {
    @com.mogujie.appmate.apt.annotation.AMRegisterAnnotation *;
     public static void register*();
}

-keep public class * extends com.mogujie.appmate.v2.base.unit.AMRow {
    public <init>(android.os.Bundle);
}

-keep public class * extends com.mogujie.appmate.v2.base.unit.AMPage {
    public <init>(android.os.Bundle);
}
-keep class com.mogujie.mgbasicdebugitem.mateItem.item.MWPMockItem {*;}

-keep class * extends com.mogujie.crosslanglib.lang.CrossValue{*;}

-keep class com.mogujie.newsku.SkuView {*;}

-keep class com.mogujie.liveskulib.LiveSkuView {*;}

#com.mogujie:notification @屠夫
-keep class com.igexin.** {*;}
-keep class com.minicooper.notification.** {*;}

# OPPO-PUSH
-keep class com.coloros.mcssdk.** { *; }
-dontwarn com.coloros.mcssdk.**

# VIVO-Push
-keep class com.vivo.push.** { *; }
-dontwarn com.vivo.push.**

# 华为-PUSH
-dontwarn com.huawei.hms.**
-keep class com.huawei.hms.** { *; }

-keep class com.huawei.android.** { *; }
-dontwarn com.huawei.android.**

-keep class com.hianalytics.android.** { *; }
-dontwarn com.hianalytics.android.**

-keep class com.huawei.updatesdk.** { *; }
-dontwarn com.huawei.updatesdk.**

-ignorewarning
-keepattributes *Annotation*
-keepattributes Exceptions
-keepattributes InnerClasses
-keepattributes Signature
-keepattributes SourceFile,LineNumberTable

# 小米-PUSH
-keep class com.xiaomi.** { *; }
-dontwarn com.xiaomi.push.**
-keep class org.apache.thrift.** { *; }

# 魅族-Push
-keep class com.meizu.** { *; }
-dontwarn com.meizu.**

# VIVO-Push
-keep class com.vivo.push.** { *; }
-dontwarn com.vivo.push.**

# 腾讯地图
-keep class com.tencent.mapsdk.**{*;}
-keep class com.tencent.tencentmap.**{*;}
-keepclassmembers class ** {
    public void on*Event(...);
}
-keep class c.t.**{*;}
-keep class com.tencent.map.geolocation.**{*;}

-dontwarn  c.t.**

# 腾讯视频上传sdk
-keep class com.mogujie.videotencent.** {*;}
-keep class com.tencent.cos.xml.** {*;}
-keep class bolts.** {*;}
-keep class okhttp3.** {*;}
-keep class okio.** {*;}
-keep class com.tencent.qcloud.core.** {*;}
-keep class com.thoughtworks.xstream.** {*;}

-keep class com.mogujie.chooser.** {*;}
-keep class com.mogujie.lifestylepublish.** {*;}
-keep class com.mogujie.multimedia.** {*;}
-keep class com.mogujie.publish.** {*;}
-keep class com.mogujie.transformer.** {*;}

# 安全团队数美设备指纹
-keep class com.ishumei.dfp.SMSDK { *; }

# 安全团队顶象设备指纹
-dontwarn *.com.dingxiang.mobile.**
-dontwarn *.com.mobile.strenc.**

-keep class com.dingxiang.mobile.risk.**{*;}
-keep class *.com.dingxiang.mobile.**{*;}
-keep class com.security.inner.**{*;}
-keep class *.com.mobile.strenc.**{*;}

# 三方APP调起悬浮窗
-keep class com.mogujie.appjumpback.** {*;}

# walle 渠道读取
-keep class com.astonmartin.utils.walle.WalleChannelReader {*;}

#涂图SDK @朔风
-keepattributes Signature
-keepattributes *Annotation*
-keepattributes InnerClasses
-keepattributes EnclosingMethod

-keep class **.R
-keep class **.R$* {
    <fields>;
}

# For native methods, see http://proguard.sourceforge.net/manual/examples.html#native
-keepclasseswithmembernames class * {
    native <methods>;
}

-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# keep setters in Views so that animations can still work.
# see http://proguard.sourceforge.net/manual/examples.html#beans
-keepclassmembers public class * extends android.view.View {
   void set*(***);
   *** get*();
}

# We want to keep methods in Activity that could be used in the XML attribute onClick
-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}

# For enumeration classes, see http://proguard.sourceforge.net/manual/examples.html#enumerations
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}



-verbose
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*


# The support library contains references to newer platform versions.
# Don't warn about those in case this app is linking against an older
# platform version.  We know about them, and they are safe.
-dontwarn android.support.**
-dontwarn org.apache.commons.**
-dontwarn com.nostra13.**
-dontwarn org.lasque.**

-keep class org.apache.commons.**{ *; }
-keep class org.lasque.tusdk.**{public *; protected *; }
-keep class org.lasque.tusdk.core.utils.image.GifHelper{ *; }
-keep class com.nineoldandroids.**{ *; }

# 解决此类问题：Error: Can't find common super class of...
-dontoptimize
-dontpreverify

# 移动安全联盟 MDID
-keep class com.bun.miitmdid.core.** {*;}
-keep class com.bun.miitmdid.supplier.IdSupplier {*;}

# bugly
-dontwarn com.tencent.bugly.**
-keep public class com.tencent.bugly.** {*;}

# 极光 SDK
-dontwarn cn.jpush.**
-keep class cn.jpush.** { *; }
-dontwarn cn.jiguang.**
-keep class cn.jiguang.** { *; }
-dontwarn com.cmic.**
-keep class com.cmic.** { *; }
-dontwarn com.unicom.**
-keep class com.unicom.** { *; }
-dontwarn cn.com.chinatelecom.**
-keep class cn.com.chinatelecom.** { *; }


