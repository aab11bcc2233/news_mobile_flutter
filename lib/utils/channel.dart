import 'dart:async';

import 'package:flutter/services.dart';

typedef Future<dynamic> Handler([dynamic arguments]);

class MethodChannelManager {
  static final _manager = _MethodChannelManager();

  MethodChannelManager._();

  static void addHandler(String key, Handler handler) {
    _manager.addHandler(key, handler);
  }

  static Handler removeHandler(String key) {
    return _manager.removeHandler(key);
  }

  static Future<T> invokeMethod<T>(String method, [dynamic arguments]) async {
    return _manager.invokeMethod(method, arguments);
  }
}

class _MethodChannelManager {
  static final _platform =
      const MethodChannel('com.onesfish.news_mobile/method_call');

  Map<String, Handler> _methodHandler = Map();

  _MethodChannelManager() {
    _platform.setMethodCallHandler((MethodCall call) {
      var handler = _methodHandler[call.method];
      if (handler == null) {
        return null;
      }
      return handler(call.arguments);
    });
  }

  void addHandler(String key, Handler handler) {
    _checkArg(key);
    _methodHandler[key] = handler;
  }

  Handler removeHandler(String key) {
    _checkArg(key);
    return _methodHandler.remove(key);
  }

  void _checkArg(String key) {
    if (key == null || key.isEmpty) {
      throw 'key can not be null';
    }
  }

  Future<T> invokeMethod<T>(String method, [dynamic arguments]) async {
    return _platform.invokeMethod(method, arguments);
  }
}
