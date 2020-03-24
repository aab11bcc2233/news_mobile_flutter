import 'dart:io';

import 'package:filesize/filesize.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:flutter_cache_manager/flutter_cache_manager.dart';
import 'package:flutter_file_manager/flutter_file_manager.dart';
import 'package:news_mobile/utils/channel.dart';

const _DIALOG_MIN_HEIGHT = 132.0;
const _DIALOG_ITEM_HEIGHT = 44.0;

class CacheDialog extends StatefulWidget {
  @override
  State<StatefulWidget> createState() {
    return _CacheDialog();
  }

  static PageRouteBuilder getPageRouteBuilder() {
    Widget _buildMaterialDialogTransitions(
        BuildContext context,
        Animation<double> animation,
        Animation<double> secondaryAnimation,
        Widget child) {
      return FadeTransition(
        opacity: CurvedAnimation(
          parent: animation,
          curve: Curves.easeOut,
        ),
        child: child,
      );
    }

    return PageRouteBuilder(
      pageBuilder: (context, _, __) => CacheDialog(),
      barrierDismissible: true,
      opaque: false,
      barrierColor: Colors.black54,
      transitionDuration: const Duration(milliseconds: 150),
      transitionsBuilder: _buildMaterialDialogTransitions,
    );
  }
}

class _CacheDialog extends State<CacheDialog> {
  static const _KEY_IMG_CACHE_SIZE = "img_cache_size";
  static const _KEY_VOICE_CACHE_SIZE = "voice_cache_size";
  static const _KEY_TOTAL_CACHE_SIZE = "total_cache_size";

  String _deleteType = "";
  Map<String, String> _cacheSizeMap;
  bool _isClearing = false;

  Future<void> _getCacheSize() async {
    Map<String, int> cache;
    try {
      dynamic obj = await MethodChannelManager.invokeMethod("getCacheSize");
      cache = Map.from(obj);
    } catch (_) {
      cache = Map();
      cache[_KEY_IMG_CACHE_SIZE] = 0;
      cache[_KEY_VOICE_CACHE_SIZE] = 0;
    }

    final imgCachePath = await DefaultCacheManager().getFilePath();
    var cacheSize = 0;
    try {
      cacheSize =
          await FileManager(root: Directory(imgCachePath)).walk().map((f) {
        if (f is File) {
          return f.lengthSync();
        } else {
          return 0;
        }
      }).reduce((a, b) => a + b);
    } catch (_) {}

    cache[_KEY_IMG_CACHE_SIZE] += cacheSize;

    cache[_KEY_TOTAL_CACHE_SIZE] =
        cache.entries.toList().map((v) => v.value).reduce((a, b) => a + b);

    _cacheSizeMap = cache.map((key, value) => MapEntry(key, filesize(value)));

    Future.delayed(Duration(milliseconds: 300), () => setState(() {}));
  }

  @override
  void initState() {
    _getCacheSize();
    super.initState();
  }

  void _onDeletePress() async {
    switch (_deleteType) {
      case _KEY_IMG_CACHE_SIZE:
        try {
          await MethodChannelManager.invokeMethod("clearImageCache");
        } on Exception catch (_) {}

        await DefaultCacheManager().emptyCache();

        break;
      case _KEY_VOICE_CACHE_SIZE:
        try {
          await MethodChannelManager.invokeMethod("clearVoiceCache");
        } on Exception catch (_) {}
        break;
    }

    _isClearing = false;
    _deleteType = "";
    await _getCacheSize();
  }

  @override
  Widget build(BuildContext context) {
    return Center(
      child: Column(
        mainAxisSize: MainAxisSize.min,
        children: <Widget>[
          Container(
            width: MediaQuery.of(context).size.width * 0.8,
            constraints: const BoxConstraints(minHeight: _DIALOG_MIN_HEIGHT),
            child: Card(
              elevation: 2.0,
              shape: RoundedRectangleBorder(
                borderRadius: BorderRadius.circular(4.0),
              ),
              child: Stack(
                children: <Widget>[
                  AnimatedOpacity(
                    opacity: _cacheSizeMap == null || _isClearing ? 1.0 : 0.0,
                    duration: const Duration(milliseconds: 300),
                    child: Container(
                      constraints:
                          const BoxConstraints(minHeight: _DIALOG_MIN_HEIGHT),
                      child: Center(
                          child: SizedBox(
                        width: 20.0,
                        height: 20.0,
                        child: CircularProgressIndicator(
                          strokeWidth: 2.0,
                        ),
                      )),
                    ),
                  ),
                  _deleteType.isEmpty || _isClearing
                      ? AnimatedOpacity(
                          opacity:
                              _cacheSizeMap != null && !_isClearing ? 1.0 : 0.0,
                          duration: const Duration(milliseconds: 300),
                          child: _cacheSizeMap == null
                              ? null
                              : Column(
                                  children: <Widget>[
                                    Container(
                                      padding: const EdgeInsets.only(
                                          left: 24.0, right: 16.0),
                                      height: _DIALOG_ITEM_HEIGHT,
                                      child: Align(
                                        alignment: Alignment.centerLeft,
                                        child: Text(
                                          "总计    ${_cacheSizeMap[_KEY_TOTAL_CACHE_SIZE]}",
                                          style: TextStyle(fontSize: 20.0),
                                        ),
                                      ),
                                    ),
                                    Container(
                                      padding: const EdgeInsets.only(
                                          left: 24.0, right: 16.0),
                                      height: _DIALOG_ITEM_HEIGHT,
                                      child: Stack(
                                        children: <Widget>[
                                          Align(
                                            alignment: Alignment.centerLeft,
                                            child: Text(
                                              "图片    ${_cacheSizeMap[_KEY_IMG_CACHE_SIZE]}",
                                              style: TextStyle(fontSize: 16.0),
                                            ),
                                          ),
                                          Align(
                                            alignment: Alignment.centerRight,
                                            child: SizedBox(
                                              width: 40.0,
                                              height: 40.0,
                                              child: InkWell(
                                                customBorder:
                                                    const CircleBorder(),
                                                highlightColor:
                                                    Colors.grey[200],
                                                splashColor: Colors.grey[100],
                                                child: Icon(
                                                  Icons.delete,
                                                  color: Colors.grey[300],
                                                ),
                                                onTap: () {
                                                  _deleteType =
                                                      _KEY_IMG_CACHE_SIZE;
                                                  setState(() {});
                                                },
                                              ),
                                            ),
                                          )
                                        ],
                                      ),
                                    ),
                                    Container(
                                      padding: const EdgeInsets.only(
                                          left: 24.0, right: 16.0),
                                      height: _DIALOG_ITEM_HEIGHT,
                                      child: Stack(
                                        children: <Widget>[
                                          Align(
                                            alignment: Alignment.centerLeft,
                                            child: Text(
                                              "音频    ${_cacheSizeMap[_KEY_VOICE_CACHE_SIZE]}",
                                              style: TextStyle(fontSize: 16.0),
                                            ),
                                          ),
                                          Align(
                                            alignment: Alignment.centerRight,
                                            child: SizedBox(
                                              width: 40.0,
                                              height: 40.0,
                                              child: InkWell(
                                                customBorder:
                                                    const CircleBorder(),
                                                highlightColor:
                                                    Colors.grey[200],
                                                splashColor: Colors.grey[100],
                                                child: Icon(
                                                  Icons.delete,
                                                  color: Colors.grey[300],
                                                ),
                                                onTap: () {
                                                  _deleteType =
                                                      _KEY_VOICE_CACHE_SIZE;
                                                  setState(() {});
                                                },
                                              ),
                                            ),
                                          )
                                        ],
                                      ),
                                    ),
                                  ],
                                ),
                        )
                      : _ClearCacheDialog(
                          message:
                              "确定删除${(_deleteType == _KEY_IMG_CACHE_SIZE) ? "图片" : "音频"}缓存吗？",
                          onCancelPress: () => setState(() => _deleteType = ""),
                          onDeletePress: () {
                            setState(() {
                              _isClearing = true;
                              _onDeletePress();
                            });
                          }),
                ],
              ),
            ),
          )
        ],
      ),
    );
  }
}

class _ClearCacheDialog extends StatelessWidget {
  final String message;
  final VoidCallback onCancelPress;
  final VoidCallback onDeletePress;

  _ClearCacheDialog(
      {Key key,
      @required this.message,
      @required this.onCancelPress,
      @required this.onDeletePress})
      : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Column(
      children: <Widget>[
        Container(
          padding: const EdgeInsets.only(left: 24.0, right: 16.0),
          height: _DIALOG_ITEM_HEIGHT,
          child: Align(
            alignment: Alignment.centerLeft,
            child: Text(
              "提示",
              style: TextStyle(fontSize: 20.0),
            ),
          ),
        ),
        Container(
          padding: const EdgeInsets.only(left: 24.0, right: 16.0),
          height: _DIALOG_ITEM_HEIGHT,
          child: Stack(
            children: <Widget>[
              Align(
                alignment: Alignment.centerLeft,
                child: Text(
                  this.message,
                  style: TextStyle(fontSize: 16.0),
                ),
              ),
            ],
          ),
        ),
        Container(
          padding: const EdgeInsets.only(left: 24.0),
          height: _DIALOG_ITEM_HEIGHT,
          child: ButtonTheme.bar(
            child: ButtonBar(
              children: <Widget>[
                FlatButton(
                  child: Text("取消"),
                  onPressed: onCancelPress,
                ),
                FlatButton(
                  child: Text("删除"),
                  onPressed: onDeletePress,
                ),
              ],
            ),
          ),
        ),
      ],
    );
  }
}
