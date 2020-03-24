import 'dart:convert';

import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:news_mobile/model/news_details_entity.dart';
import 'package:news_mobile/model/news_entity.dart';
import 'package:news_mobile/ui/theme/theme.dart';
import 'package:video_player/video_player.dart';
import 'package:webview_flutter/webview_flutter.dart';

class NewsDetailsPage extends StatefulWidget {
  static const routeName = '/news_details';

  final NewsEntity newsEntity;
  final NewsDetailsEntity newsDetailsEntity;

  NewsDetailsPage(
      {Key key, @required this.newsEntity, @required this.newsDetailsEntity})
      : super(key: key);

  @override
  State<StatefulWidget> createState() {
    return _NewsDetailsPageState();
  }

  static PageRoute getPageRouteBuilder(
      NewsEntity newsEntity, NewsDetailsEntity newsDetailsEntity) {
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
      pageBuilder: (context, _, __) => NewsDetailsPage(
        newsEntity: newsEntity,
        newsDetailsEntity: newsDetailsEntity,
      ),
      transitionDuration: const Duration(milliseconds: 300),
      transitionsBuilder: _buildMaterialDialogTransitions,
    );
  }
}

class _NewsDetailsPageState extends State<NewsDetailsPage> {
  static const _IMG_HIRAGANA_FILL = "assets/hiragana_a_fill.png";
  static const _IMG_HIRAGANA_STROKE = "assets/hiragana_a_stroke.png";

  WebViewController _controller;
  bool _isLoadFinish = false;

  String _imgHiragana = _IMG_HIRAGANA_FILL;

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text(widget.newsEntity.title),
        actions: <Widget>[
          SizedBox(
            width: 48.0,
            height: 48.0,
            child: InkWell(
              customBorder: const CircleBorder(),
              child: Tooltip(
                message: _imgHiragana == _IMG_HIRAGANA_FILL ? "隐藏假名" : "显示假名",
                child: Center(
                  child: Image(
                    width: 20.0,
                    height: 20.0,
                    image: AssetImage(_imgHiragana),
                    color: Theme.of(context).accentIconTheme.color,
                  ),
                ),
              ),
              onTap: () {
                setState(() {
                  if (_imgHiragana == _IMG_HIRAGANA_FILL) {
                    _imgHiragana = _IMG_HIRAGANA_STROKE;
                  } else {
                    _imgHiragana = _IMG_HIRAGANA_FILL;
                  }
                  _controller.evaluateJavascript("""
        var rts = document.getElementsByTagName("rt");
         for (var i = 0; i < rts.length; i++) {
            var style = rts[i].style;
            if (style.visibility == "" || style.visibility == "visible") {
                style.visibility = "hidden";
            } else {
                style.visibility = "visible";
            }
          }
                  """);
                });
              },
            ),
          ),
          Builder(
            builder: (context) {
              return SizedBox(
                width: 48.0,
                height: 48.0,
                child: InkWell(
                  customBorder: const CircleBorder(),
                  child: Tooltip(
                    message: "复制内容",
                    child: Center(
                        child: Icon(
                      Icons.content_copy,
                      size: 20.0,
                    )),
                  ),
                  onTap: () {
                    Clipboard.setData(new ClipboardData(
                        text: widget.newsEntity.title +
                            widget.newsDetailsEntity.content));
                    Scaffold.of(context).showSnackBar(SnackBar(
                      content: new Text("已复制"),
                    ));
                  },
                ),
              );
            },
          ),
        ],
      ),
      body: Stack(
        children: <Widget>[
          Center(
            child: SizedBox(
              width: 20.0,
              height: 20.0,
              child: AnimatedOpacity(
                opacity: _isLoadFinish ? 0.0 : 1.0,
                duration: const Duration(milliseconds: 300),
                child: CircularProgressIndicator(
                  strokeWidth: 2.0,
                ),
              ),
            ),
          ),
          AnimatedOpacity(
            opacity: _isLoadFinish ? 1.0 : 0.0,
            duration: const Duration(milliseconds: 300),
            child: WebView(
              initialUrl: '',
              javascriptMode: JavascriptMode.unrestricted,
              onPageFinished: (String url) {
                setState(() {
                  _isLoadFinish = true;
                });
              },
              onWebViewCreated: (WebViewController controller) {
                _controller = controller;
                _loadData(context);
              },
            ),
          )
        ],
      ),
      floatingActionButton: VoicePlayerButton(
        voiceUrl: widget.newsEntity.voiceUrl,
      ),
    );
  }

  void _loadData(BuildContext context) {
    var details = widget.newsDetailsEntity;
    String bodyColor;
    String bgColor;
    String nameColor;
    String locationColor;
    String groupColor;
    String underColor;

    if (MyTheme.isDark(context)) {
      bodyColor = "#5D949E";
      bgColor = "#424242";
      nameColor = "#35A16B";
      locationColor = "#FF7F00";
      groupColor = "#BB86FC";
      underColor = "#757575";
    } else {
      bodyColor = "#333333";
      bgColor = "#FFF2E2";
      nameColor = "#35A16B";
      locationColor = "#FF7F00";
      groupColor = "#0041CC";
      underColor = "#000";
    }

    var style = """
          <style>
            * {
            font-size: 18px; 
            }
            body { 
              padding: 1.0rem; 
              font-family: "Helvetica Neue", Helvetica, Arial, "PingFang SC", "Hiragino Sans GB", "Heiti SC", "Microsoft YaHei", "WenQuanYi Micro Hei", sans-serif; word-wrap: break-word; 
              background-color: bodyBgColor;
              padding-bottom: 72px;
              color: bodyColor;
            }
            h1 { 
//                font-size: 2.0rem; 
                line-height: 3.0rem; 
            } 
            p { 
//              font-size: 2.0rem; 
              line-height: 3.0rem; 
//              text-indent: 2.0rem; 
            } 
            rt { 
//              font-size: 1.8rem;
                visibility: visible;
            } 
            .under { 
              padding-bottom: 4px; 
              border-bottom: 1px solid underColor; 
            } 
            .colorN { 
              color: nameColor; 
            } 
            .colorL { 
              color: locationColor; 
            } 
            .colorC { 
              color: groupColor; 
            } 
        </style>
          """;
    style = style.replaceFirst("bodyColor", bodyColor);
    style = style.replaceFirst("bodyBgColor", bgColor);
    style = style.replaceFirst("nameColor", nameColor);
    style = style.replaceFirst("locationColor", locationColor);
    style = style.replaceFirst("groupColor", groupColor);
    style = style.replaceFirst("underColor", underColor);

    var html = details.contentHtmlWithRuby;

    var text =
        '<head><meta name="viewport" content="width=device-width, initial-scale=1.0">$style</head>$html';

    final uri = Uri.dataFromString(text,
            mimeType: 'text/html', encoding: Encoding.getByName('utf-8'))
        .toString();

    _controller.loadUrl(uri);
  }
}

class VoicePlayerButton extends StatefulWidget {
  final String voiceUrl;

  VoicePlayerButton({Key key, @required this.voiceUrl}) : super(key: key);

  @override
  State<StatefulWidget> createState() {
    return _VoicePlayerButtonState();
  }
}

class _VoicePlayerButtonState extends State<VoicePlayerButton> {
  VideoPlayerController _controller;
  bool _isBuffering = false;
  bool _isPlaying = false;
  bool _isSeekToStart = false;

  void _resetPlayerState() {
    _isBuffering = false;
    _isPlaying = false;
    _isSeekToStart = false;
  }

  @override
  void initState() {
    _controller = VideoPlayerController.network(widget.voiceUrl, formatHint: VideoFormat.hls);
    _controller.addListener(_playerEventListener);

    _controller.initialize().then((_) {
      setState(() {});
    });
    super.initState();
  }

  Future _playerEventListener() async {
    var value = _controller.value;

    print("initialized = " + value.initialized.toString());
    print("hasError = " + value.hasError.toString());

    if (!value.initialized) {
      _resetPlayerState();
      return;
    }

    if (value.hasError) {
      _resetPlayerState();
      return;
    }

    if (value.errorDescription != null) {
      print("errorDescription = " + value.errorDescription);
      _resetPlayerState();
      return;
    }

    var totalSeconds = value.duration.inSeconds;
    print("duration seconds = " + totalSeconds.toString());
    print("position second = " + value.position.inSeconds.toString());

    var isBuffering = value.isBuffering;
    print("isBuffering = " + value.isBuffering.toString());
    print("isLooping = " + value.isLooping.toString());
    var isPlaying = value.isPlaying;
    print("isPlaying = " + value.isPlaying.toString());

    print(
        "---------------------------------------------------------------------------------");

    if (_isSeekToStart) {
      print("seekTo 0 end");
      print(
          "===================================================================");
      _isSeekToStart = false;
      setState(() {
        _controller.pause();
      });
      return;
    }

    var needRebuild = false;

    if (_isBuffering != isBuffering) {
      _isBuffering = isBuffering;
      needRebuild = true;
    }

    if (_isPlaying != isPlaying) {
      _isPlaying = isPlaying;
      needRebuild = true;
    }

    var isEnd =
        (totalSeconds > 0) && (totalSeconds == value.position.inSeconds);
    if (isEnd) {
      print("isEnd = true, seekTo 0 begin");
      print(
          "+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
      _isSeekToStart = true;
      await _controller.seekTo(Duration());
      return;
    }

    if (needRebuild) {
      setState(() {});
    }
  }

  @override
  void dispose() {
    _controller.dispose();
    super.dispose();
  }

  void _pressPlayer() {
    setState(() {
      if (_isPlaying) {
        _controller.pause();
      } else {
        _controller.play();
      }
    });
  }

  @override
  Widget build(BuildContext context) {
    return Stack(
      children: <Widget>[
        Align(
          alignment: Alignment.bottomRight,
          child: FloatingActionButton(
            child: Icon(_isPlaying ? Icons.pause : Icons.play_arrow),
          ),
        ),
        Align(
          alignment: Alignment.bottomRight,
          child: GestureDetector(
            onTap: _pressPlayer,
            child: AnimatedOpacity(
              opacity: _isBuffering ? 1.0 : 0.0,
              duration: Duration(milliseconds: 200),
              child: Container(
                width: kToolbarHeight,
                height: kToolbarHeight,
                decoration: BoxDecoration(
                  shape: BoxShape.circle,
                  color: Colors.black45,
                ),
                child: Center(
                  child: SizedBox(
                    width: 20.0,
                    height: 20.0,
                    child: CircularProgressIndicator(
                      strokeWidth: 2.0,
                      valueColor: AlwaysStoppedAnimation<Color>(Colors.white70),
                    ),
                  ),
                ),
              ),
            ),
          ),
        ),
      ],
    );
  }
}
