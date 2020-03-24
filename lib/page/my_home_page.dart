import 'dart:convert';

import 'package:cached_network_image/cached_network_image.dart';
import 'package:dio/dio.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:intl/intl.dart';
import 'package:news_mobile/api/api.dart';
import 'package:news_mobile/db/news_db.dart';
import 'package:news_mobile/db/news_details_db.dart';
import 'package:news_mobile/model/news_details_entity.dart';
import 'package:news_mobile/model/news_entity.dart';
import 'package:news_mobile/page/cache_dialog.dart';
import 'package:news_mobile/page/news_details_page.dart';
import 'package:news_mobile/ui/theme/theme.dart';
import 'package:shared_preferences/shared_preferences.dart';

class MyHomePage extends StatefulWidget {
  final String title;

  MyHomePage({Key key, this.title}) : super(key: key);

  @override
  State<StatefulWidget> createState() {
    return _HomePageState();
  }
}

class _HomePageState extends State<MyHomePage> with WidgetsBindingObserver {
  static const _TAG = "_MyHomePageState: ";
  static const _KEY_NEWS_ID = "news_id";
  static const _KEY_NEWS_PREARRANGED_TIME = "news_prearranged_time";
  static const _KEY_LIMIT = "limit";
  static const _KEY_SCROLL_OFFSET = "_KEY_SCROLL_OFFSET";
  static const _SHOW_TOP_BTN_THRESHOLD = 1000;
  List<NewsEntity> _newsEntities = [];
  var _isVisibleNetRequestProgress = false;
  var _isNoMoreData = false;
  ScrollController _scrollController;
  var _isShowToTopBtn = false;
  var _isLoading = false;
  var _hasError = false;
  var _errorMsg = "";
  DateTime _lastPressedAt;

  @override
  void initState() {
    print(_TAG + "initState()");
    WidgetsBinding.instance.addObserver(this);
    _restoreScrollPosition();
    super.initState();
  }

  @override
  void didChangeAppLifecycleState(AppLifecycleState state) {
    print(_TAG + "didChangeAppLifecycleState()");
    switch (state) {
      case AppLifecycleState.inactive: // 用户可见，但不可响应用户操作
        print(_TAG + "didChangeAppLifecycleState(): inactive");
        break;
      case AppLifecycleState.resumed: // 应用可见并可响应用户操作
        print(_TAG + "didChangeAppLifecycleState(): resumed");
        _restoreScrollPosition();
        break;
      case AppLifecycleState.paused: // 已经暂停了，用户不可见、不可操作
        print(_TAG + "didChangeAppLifecycleState(): paused");
        _saveScrollPosition();
        break;
      case AppLifecycleState.suspending: // 应用被挂起，此状态IOS永远不会回调
        print(_TAG + "didChangeAppLifecycleState(): suspending");
        break;
    }
    super.didChangeAppLifecycleState(state);
  }

  void _restoreScrollPosition() async {
    if (_newsEntities.isNotEmpty) {
      return;
    }

    final prefs = await SharedPreferences.getInstance();
    final newsId = prefs.getString(_KEY_NEWS_ID) ?? "";
    final newsPrearrangedTime = prefs.getInt(_KEY_NEWS_PREARRANGED_TIME);
    final limit = prefs.getInt(_KEY_LIMIT);
    final offset = prefs.getDouble(_KEY_SCROLL_OFFSET) ?? 0.0;

    if (_scrollController == null) {
      _scrollController = ScrollController(initialScrollOffset: offset);
      _scrollController.addListener(_scrollListener);
    }

    if (canShowToTopBtn(offset)) {
      _isShowToTopBtn = true;
    }

    if (newsId.isEmpty) {
      _firstRequest();
    } else {
      var news = await NewsDb.instance.newsEntities(
        newsPrearrangedTime: newsPrearrangedTime,
        newsId: newsId,
        limit: limit,
        excludeNewsId: false,
      );
      if (news.isEmpty) {
        _firstRequest();
      } else {
        _newsEntities = news;
        setState(() {});
      }
    }
  }

  void _firstRequest() {
    _requestNews(newsPrearrangedTime: DateTime.now().millisecondsSinceEpoch);
  }

  Future<bool> _saveScrollPosition() async {
    if (_newsEntities.isNotEmpty && _scrollController != null) {
      final prefs = await SharedPreferences.getInstance();
      final entity = _newsEntities.first;
      await prefs.setString(_KEY_NEWS_ID, entity.newsId);
      await prefs.setInt(
          _KEY_NEWS_PREARRANGED_TIME, entity.newsPrearrangedTime);
      await prefs.setInt(_KEY_LIMIT, _newsEntities.length);
      return await prefs.setDouble(
          _KEY_SCROLL_OFFSET, _scrollController.offset);
    }

    return true;
  }

  void _scrollListener() {
//    print(_TAG + "_scrollListener() 当前滚动位置 offset = " + _scrollController.offset.toString());
//    ScrollPosition position = _scrollController.position;
//    print(_TAG + "_scrollListener() 当前滚动位置 pixels = " + position.pixels.toString());
//    print(_TAG + "_scrollListener() 最大可滚动长度 maxScrollExtent = " + position.maxScrollExtent.toString());
//    print(_TAG + "_scrollListener() 滑出ViewPort顶部的长度 extentBefore = " + position.extentBefore.toString());
//    print(_TAG + "_scrollListener() ViewPort内部长度 extentInside = " + position.extentInside.toString());
//    print(_TAG + "_scrollListener() 列表中未滑入ViewPort部分的长度 extentAfter = " + position.extentAfter.toString());
//    print(_TAG + "_scrollListener() 是否滑到了可滚动组件的边界 atEdge = " + position.atEdge.toString());
    if (_scrollController != null) {
      if (canHideToTopBtn(_scrollController.offset)) {
        setState(() {
          _isShowToTopBtn = false;
        });
      } else if (canShowToTopBtn(_scrollController.offset)) {
        setState(() {
          _isShowToTopBtn = true;
        });
      }
    }
  }

  bool canHideToTopBtn(double offset) =>
      offset < _SHOW_TOP_BTN_THRESHOLD && _isShowToTopBtn;

  bool canShowToTopBtn(double offset) =>
      offset >= _SHOW_TOP_BTN_THRESHOLD && _isShowToTopBtn == false;

  @override
  void didChangeDependencies() {
    print(_TAG + "didChangeDependencies()");
    super.didChangeDependencies();
  }

  @override
  void deactivate() {
    print(_TAG + "deactivate()");
    super.deactivate();
  }

  @override
  void didUpdateWidget(MyHomePage oldWidget) {
    print(_TAG + "didUpdateWidget()");
    super.didUpdateWidget(oldWidget);
  }

  @override
  void dispose() {
    print(_TAG + "dispose()");
    WidgetsBinding.instance..removeObserver(this);
    if (_scrollController != null) {
      _scrollController.dispose();
    }
    super.dispose();
  }

  Future<void> _requestNews(
      {int newsPrearrangedTime: -1, String newsId, int limit: 5}) async {
    _isLoading = true;
    await Future.delayed(const Duration(milliseconds: 300), () {});
    var news = await NewsDb.instance.newsEntities(
        newsPrearrangedTime: newsPrearrangedTime, newsId: newsId, limit: limit);

    if (news.isEmpty && (newsId == null || newsId.isEmpty)) {
      final resp = await Api.newsList();
      _hasError = resp.hasError;
      if (_hasError) {
        _errorMsg = resp.message;
      } else {
        List list = json.decode(resp.data);
        news = list
//            .where((i) => i["date"] == '2019-08-29') // test for _onRefresh()
            .expand((i) => i["data"])
            .map((i) => NewsEntity.fromJson(i))
            .toList();

        final dbInsertResult =
            await NewsDb.instance.insertNewsEntities(news.reversed.toList());
        print(_TAG +
            "_requestTopNews(): dbInsertResult = " +
            dbInsertResult.length.toString());
        if (dbInsertResult.isNotEmpty) {
          news = await NewsDb.instance.newsEntities(
              newsPrearrangedTime: DateTime.now().millisecondsSinceEpoch,
              newsId: newsId,
              limit: limit);
        } else {}
      }
    }

    if (news.isEmpty) {
      _isNoMoreData = _newsEntities.isNotEmpty;
    } else {
      _isNoMoreData = false;

      _newsEntities.addAll(news);
    }

    _isLoading = false;
    setState(() {});
  }

  Future<void> _onRefresh() async {
    if (_isLoading) {
      return;
    }

//    if (_newsEntities.isEmpty) {
//      await _requestNews(
//          newsPrearrangedTime: DateTime.now().millisecondsSinceEpoch);
//      return;
//    }

    final resp = await Api.newsList(); // 此接口返回所有的数据，包括已经在页面上展示的数据。
    if (resp.hasError) {
      Scaffold.of(context).showSnackBar(SnackBar(
        content: new Text(resp.message),
      ));
      return;
    }

    /*
      接口返回格式如下。
      [
        {
         "date": "2019-08-31",
         "data": ...
        },
        {
         "date": "2019-08-30",
         "data": ...
        }
      ]
      
      以下代码是为了用界面上已有的最新时间的数据，与接口上数据对比，
      找出时间更加新的数据。
     */

    final entity = _newsEntities.first;
    final publishAt = DateFormat("yyyy-MM-dd").format(
        DateTime.fromMillisecondsSinceEpoch(
            entity.newsPrearrangedTime)); // 界面上最新时间的数据
    final publishDate = DateTime.parse(publishAt); // 界面上最新的数据的时间

    List list = json.decode(resp.data);
    list = list.where((item) {
      var date = DateTime.parse(item["date"]); // 接口返回的数据的时间
      return date.millisecondsSinceEpoch >=
          publishDate.millisecondsSinceEpoch; // 找出更加新的时间的数据
    }).toList();

    var news = list
        .expand((i) => i["data"])
        .map((i) => NewsEntity.fromJson(i))
        .toList();

    // 从已经过滤好的数据中剔除，界面上已存在的数据。
    int index = news.indexWhere((item) =>
        item.newsId == entity.newsId &&
        item.newsPrearrangedTime == entity.newsPrearrangedTime);
    if (index > -1) {
      news = news.sublist(0, index);

      final dbInsertResult =
          await NewsDb.instance.insertNewsEntities(news.reversed.toList());
      print(_TAG +
          "_onRefresh(): dbInsertResult = " +
          dbInsertResult.length.toString());

      _newsEntities.insertAll(0, news);
    }
    setState(() {});
  }

  void _showCacheDialog(BuildContext context) {
    Navigator.of(context).push(CacheDialog.getPageRouteBuilder());
  }

  Widget _listViewBuilder(BuildContext context, Orientation orientation) {
    if (orientation == Orientation.portrait) {
      return ListView.separated(
        padding: const EdgeInsets.only(left: 14.0, right: 14.0, top: 8.0),
        controller: _scrollController,
        physics: const BouncingScrollPhysics(),
        itemCount: _newsEntities.length + 1,
        separatorBuilder: (BuildContext context, int index) {
          return SizedBox(
            height: 8.0,
          );
        },
        itemBuilder: _itemBuilder,
      );
    } else {
      return GridView.builder(
        padding: const EdgeInsets.only(left: 14.0, right: 14.0, top: 8.0),
        gridDelegate: const SliverGridDelegateWithFixedCrossAxisCount(
            crossAxisCount: 2,
            childAspectRatio: 1.2,
            mainAxisSpacing: 8.0,
            crossAxisSpacing: 8.0),
        controller: _scrollController,
        physics: const BouncingScrollPhysics(),
        itemCount: _newsEntities.length + 1,
        itemBuilder: _itemBuilder,
      );
    }
  }

  Widget _itemBuilder(BuildContext context, int index) {
    final len = _newsEntities.length;
    if (index == len) {
      if (_isNoMoreData) {
        return Container(
          margin: const EdgeInsets.only(
              left: 16.0, top: 4.0, right: 16.0, bottom: 8.0),
          child: Center(
            child: Text(
              "到底啦",
              style: TextStyle(color: Colors.grey, fontSize: 12.0),
            ),
          ),
        );
      }

      final item = _newsEntities[len - 1];
      _requestNews(
          newsPrearrangedTime: item.newsPrearrangedTime, newsId: item.newsId);

      return Container(
        margin: const EdgeInsets.only(
            left: 16.0, top: 4.0, right: 16.0, bottom: 8.0),
        child: Center(
          child: SizedBox(
            width: 16.0,
            height: 16.0,
            child: CircularProgressIndicator(
              strokeWidth: 1.0,
            ),
          ),
        ),
      );
    }

    return NewsItemWidget(
      newsEntity: _newsEntities[index],
    );
  }

  List<Widget> _getPopupMenus(BuildContext context) {
    final menus = [PopupMenuItem(
      value: "cached",
      child: SizedBox(
        width: 80.0,
        child: Row(
          children: <Widget>[
            Icon(
              Icons.cached,
              color: MyTheme.isDark(context)
                  ? Colors.grey
                  : Colors.black,
            ),
            Padding(
              padding: const EdgeInsets.only(left: 8.0),
              child: Text("缓存"),
            ),
          ],
        ),
      ),
    )];

    final isPlatformDark = MediaQuery.of(context).platformBrightness == Brightness.dark;
    if (!isPlatformDark) {
      menus.add(PopupMenuItem(
        child: SizedBox(
          width: 80.0,
          child: MyTheme.isDark(context)
              ? Row(
            children: <Widget>[
              Icon(
                Icons.wb_sunny,
                color: Colors.grey,
              ),
              Padding(
                padding: const EdgeInsets.only(left: 8.0),
                child: Text("白天"),
              ),
            ],
          )
              : Row(
            children: <Widget>[
              Image.asset(
                  "assets/baseline_nights_stay_black_24dp.png"),
              Padding(
                padding: const EdgeInsets.only(left: 8.0),
                child: Text("夜间"),
              ),
            ],
          ),
        ),
        value: "theme",
      ));
    }

    return menus;
  }

  @override
  Widget build(BuildContext context) {


    return Scaffold(
      appBar: AppBar(
        actionsIconTheme: MyTheme.of(context).accentIconTheme,
        iconTheme: MyTheme.of(context).iconTheme,
        title: Text(widget.title),
        actions: <Widget>[
          PopupMenuButton(
            itemBuilder: (_) => _getPopupMenus(context),
            onSelected: (value) {
              switch (value) {
                case "theme":
                  {
                    if (MyTheme.isDark(context)) {
                      MyTheme.changeTheme(context, MyThemeKeys.LIGHT);
                    } else {
                      MyTheme.changeTheme(context, MyThemeKeys.DARK);
                    }
                  }
                  break;
                case "cached":
                  {
                    _showCacheDialog(context);
                  }
                  break;
              }
            },
            child: Padding(
              padding: const EdgeInsets.only(
                  left: 8.0, top: 8.0, right: 8.0, bottom: 8.0),
              child: Icon(
                Icons.more_vert,
                size: 24.0,
              ),
            ),
          ),
        ],
      ),
      floatingActionButton: !_isShowToTopBtn
          ? null
          : FloatingActionButton(
              child: Icon(Icons.arrow_upward),
              onPressed: () {
                //返回到顶部时执行动画
                if (_scrollController != null) {
                  _scrollController.animateTo(.0,
                      duration: Duration(milliseconds: 300),
                      curve: Curves.ease);
                }
              }),
      body: Builder(
          builder: (context) => WillPopScope(
                onWillPop: () async {
                  if (_lastPressedAt == null ||
                      DateTime.now().difference(_lastPressedAt) >
                          Duration(seconds: 1)) {
                    //两次点击间隔超过1秒则重新计时
                    _lastPressedAt = DateTime.now();
                    Scaffold.of(context).showSnackBar(SnackBar(
                      content: new Text("再按一次退出"),
                    ));
                    return false;
                  }

                  return _saveScrollPosition();
                },
                child: NotificationListener<ProgressVisibleNotification>(
                    onNotification: (notification) {
                      setState(() {
                        _isVisibleNetRequestProgress = notification.isVisible;
                      });
                      return true;
                    },
                    child: Stack(
                      children: <Widget>[
                        Container(
                            width: double.infinity,
                            height: double.infinity,
                            child: Visibility(
                              visible: _newsEntities.isEmpty,
                              child: _hasError
                                  ? Center(
                                      child: FlatButton(
                                          onPressed: () {
                                            setState(() {
                                              _hasError = false;
                                              _firstRequest();
                                            });
                                          },
                                          child: Text(
                                            _errorMsg + "，点击重试",
                                            style: TextStyle(
                                              color: MyTheme.isDark(context)
                                                  ? MyThemes.getTheme(
                                                          MyThemeKeys.LIGHT)
                                                      .primaryColorDark
                                                  : MyTheme.of(context)
                                                      .primaryColor,
                                            ),
                                          )),
                                    )
                                  : Center(
                                      child: CircularProgressIndicator(),
                                    ),
                            )),
                        Visibility(
                            visible: _newsEntities.isNotEmpty,
                            child: RefreshIndicator(
                              onRefresh: _onRefresh,
                              child: OrientationBuilder(
                                builder: _listViewBuilder,
                              ),
                            )),
                        Visibility(
                          visible: _isVisibleNetRequestProgress,
                          child: Container(
                            width: double.infinity,
                            height: double.infinity,
                            decoration:
                                BoxDecoration(color: Colors.transparent),
                            child: Center(
                              child: Container(
                                width: 60.0,
                                height: 60.0,
                                decoration: BoxDecoration(
                                    color: Colors.black45,
                                    borderRadius: BorderRadius.circular(6.0)),
                                alignment: Alignment.center,
                                child: SizedBox(
                                  width: 20.0,
                                  height: 20.0,
                                  child: CircularProgressIndicator(
                                    strokeWidth: 2.0,
                                    valueColor:
                                        AlwaysStoppedAnimation(Colors.white),
                                  ),
                                ),
                              ),
                            ),
                          ),
                        )
                      ],
                    )),
              )),
    );
  }
}

class NewsItemWidget extends StatelessWidget {
  static const _TAG = "NewsItemWidget";

  final NewsEntity newsEntity;

  NewsItemWidget({Key key, @required this.newsEntity}) : super(key: key);

  void _clickItem(BuildContext context) async {
    bool dataFromNetwork = false;
    try {
      var detailsEntity =
          await NewsDetailsDb.instance.newsDetails(newsEntity.newsId);

      if (detailsEntity == null) {
        dataFromNetwork = true;
        ProgressVisibleNotification(true).dispatch(context);

        var resp = await Api.newsDetails(newsEntity.newsId);
        if (resp.hasError) {
          Scaffold.of(context).showSnackBar(SnackBar(
            content: new Text(resp.message),
          ));
          return;
        }

        String newsDetailsJson = resp.data;
        var jsonData = json.decode(newsDetailsJson);
        String content = jsonData["content"];
        if (content == null || content.isEmpty) {
          Scaffold.of(context).showSnackBar(SnackBar(
            content: new Text("Not Found"),
          ));
          return;
        }

        detailsEntity = NewsDetailsEntity.fromJson(jsonData);

        NewsDetailsDb.instance.insertNewsDetails(detailsEntity);
      }

      if (detailsEntity == null) {
        Scaffold.of(context).showSnackBar(SnackBar(
          content: new Text("error"),
        ));
        return;
      }

//      if (newsEntity.imageUrl != null && newsEntity.imageUrl.isNotEmpty) {
//        final cacheManager = DefaultCacheManager();
//        final imgFile =
//            await cacheManager.getFileFromCache(newsEntity.imageUrl);
//        if (imgFile != null) {
//          newsEntity.imageFilePath = imgFile.file.path;
//        }
//      }
//
//      var result = await MethodChannelManager.invokeMethod('startNewsDetails', {
//        "is_dark": MyTheme.instanceOf(context).isDark,
//        "news": json.encode(newsEntity),
//        "details": json.encode(detailsEntity),
//      });
      var args = {
        "news": newsEntity,
        "details": detailsEntity,
      };

      Navigator.pushNamed(context, NewsDetailsPage.routeName, arguments: args);

//      print("$_TAG startNewsDetails() startNewsDetails result = $result");
    } on DioError catch (e) {
      Scaffold.of(context).showSnackBar(SnackBar(
        content: new Text(e.message),
      ));
    } on PlatformException catch (_) {} finally {
      if (dataFromNetwork) {
        ProgressVisibleNotification(false).dispatch(context);
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    return _NewsItemContentBig(
      newsEntity,
      clickItem: _clickItem,
    );
  }
}

class _NewsItemContentBig extends StatelessWidget {
  final NewsEntity newsEntity;

  final ValueChanged<BuildContext> clickItem;

  _NewsItemContentBig(this.newsEntity, {Key key, @required this.clickItem})
      : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Container(
      child: Card(
          elevation: 2.0,
          shape: RoundedRectangleBorder(
            borderRadius: BorderRadius.circular(4.0),
          ),
          child: InkWell(
            onTap: () {
              clickItem(context);
            },
            child: Container(
              child: Column(
                children: <Widget>[
                  Container(
                    width: double.infinity,
                    height: 160.0,
                    child: CachedNetworkImage(
                      imageUrl: newsEntity.imageUrl,
                      imageBuilder: (context, imageProvider) => Container(
                        height: double.infinity,
                        decoration: BoxDecoration(
                          image: DecorationImage(
                            image: imageProvider,
                            fit: BoxFit.cover,
                          ),
                          borderRadius: const BorderRadius.only(
                              topLeft: const Radius.circular(4.0),
                              topRight: const Radius.circular(4.0)),
                        ),
                      ),
                    ),
                  ),
                  Container(
                    padding:
                        const EdgeInsets.only(left: 8.0, right: 8.0, top: 8.0),
                    child: Align(
                      alignment: Alignment.centerLeft,
                      child: Text(
                        newsEntity.title,
                        maxLines: 2,
                        softWrap: true,
                        overflow: TextOverflow.ellipsis,
                        style: TextStyle(fontSize: 16.0),
                      ),
                    ),
                  ),
                  Container(
                    padding: const EdgeInsets.all(8.0),
                    child: Align(
                      alignment: Alignment.centerLeft,
                      child: Text(
                        newsEntity.newsPrearrangedTimeFormat,
                        style: TextStyle(
                            fontSize: 12.0,
                            color: Theme.of(context).textTheme.body2.color),
                      ),
                    ),
                  ),
                ],
              ),
            ),
          )),
    );
  }
}

class ProgressVisibleNotification extends Notification {
  final bool isVisible;

  ProgressVisibleNotification(this.isVisible);
}
