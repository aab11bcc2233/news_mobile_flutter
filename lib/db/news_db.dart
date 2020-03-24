import 'package:news_mobile/model/news_entity.dart';
import 'package:sqflite/sqflite.dart';

import 'db.dart';

class NewsDb {
  static const String _TABLE_NAME = "news";
  static const String _COLUMN_ID = "id";
  static const String _COLUMN_NEWS_ID = "news_id";
  static const String _COLUMN_TITLE = "title";
  static const String _COLUMN_NEWS_PREARRANGED_TIME = "news_prearranged_time";
  static const String _COLUMN_IMAGE_URL = "image_url";
  static const String _COLUMN_VOICE_URL = "voice_url";

  static const String SQL_TABLE = '''
  CREATE TABLE $_TABLE_NAME (
    $_COLUMN_ID INTEGER PRIMARY KEY,
    $_COLUMN_NEWS_ID TEXT NOT NULL UNIQUE,
    $_COLUMN_TITLE TEXT NOT NULL,
    $_COLUMN_NEWS_PREARRANGED_TIME INTEGER NOT NULL,
    $_COLUMN_IMAGE_URL TEXT NOT NULL,
    $_COLUMN_VOICE_URL TEXT NOT NULL
    )
  ''';

  static final instance = NewsDb._();

  NewsEntity _firstRow;

  NewsDb._();

  Future<List<dynamic>> insertNewsEntities(
      List<NewsEntity> newsEntities) async {
    final Database myDb = await DbHelper.instance.db;
    final batch = myDb.batch();
    newsEntities.forEach((news) {
      var map = news.toJson(skipImageFilePath: true);
//      map[_COLUMN_ID] = "";
      return batch.insert(_TABLE_NAME, map,
          conflictAlgorithm: ConflictAlgorithm.ignore);
    });
    return await batch.commit();
  }

  Future<List<NewsEntity>> newsEntities(
      {int newsPrearrangedTime: -1,
      String newsId,
      int limit: 5,
      bool excludeNewsId: true}) async {
    final Database myDb = await DbHelper.instance.db;

    List<Map<String, dynamic>> maps;

    if (newsPrearrangedTime <= 0) {
      maps = await myDb.query(_TABLE_NAME);

      return _generateNewsEntities(maps);
    }

    // 这段代码是为了判断数据是否被全部读取完。
    final newsIdNotEmpty = newsId != null && newsId.isNotEmpty;
    if (newsIdNotEmpty) {
      if (_firstRow == null) {
        final firstRowMap =
            await myDb.query(_TABLE_NAME, orderBy: ' $_COLUMN_ID', limit: 1);

        final List<NewsEntity> firstList = _generateNewsEntities(firstRowMap);
        if (firstList.isNotEmpty) {
          _firstRow = firstList.first;
        }
      }

      if (_firstRow.newsId == newsId) { // 若 newsId 是表中第一行，说明已没有数据可查询了。
        return [];
      }
    }

    maps = await myDb.query(_TABLE_NAME,
        where: '$_COLUMN_NEWS_PREARRANGED_TIME <= ?',
        whereArgs: [newsPrearrangedTime],
        orderBy: '$_COLUMN_NEWS_PREARRANGED_TIME DESC, $_COLUMN_ID DESC',
        limit: limit);

    var result = _generateNewsEntities(maps);
    if (newsIdNotEmpty && excludeNewsId) {
      // 这里为了将页面展示中，已存在的 newsId 剔除。
      final index = result.indexWhere((item) => item.newsId == newsId);
      final start = index + 1;
      if (start < result.length) {
        result = result.sublist(start);
      }
    }

    return result;
  }

  List<NewsEntity> _generateNewsEntities(List<Map<String, dynamic>> maps) {
    if (maps == null || maps.isEmpty) {
      return [];
    }
    return List.generate(maps.length, (i) {
      var kv = maps[i];
      return NewsEntity(
          newsId: kv[_COLUMN_NEWS_ID],
          title: kv[_COLUMN_TITLE],
          newsPrearrangedTime: kv[_COLUMN_NEWS_PREARRANGED_TIME],
          imageUrl: kv[_COLUMN_IMAGE_URL],
          voiceUrl: kv[_COLUMN_VOICE_URL]);
    });
  }
}
