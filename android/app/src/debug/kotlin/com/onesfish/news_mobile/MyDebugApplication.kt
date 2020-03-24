package com.onesfish.news_mobile

import com.facebook.stetho.Stetho
import io.flutter.app.FlutterApplication

/**
 *
 * Create by htp on 2019/8/28
 */

class MyDebugApplication : FlutterApplication() {
    override fun onCreate() {
        super.onCreate()
        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this))
                        .build())
    }
}