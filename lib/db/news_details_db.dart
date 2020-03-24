import 'package:news_mobile/model/news_details_entity.dart';
import 'package:sqflite/sqflite.dart';

import 'db.dart';

class NewsDetailsDb {
  static const String _TABLE_NAME = "news_details";
  static const String _COLUMN_NEWS_ID = "news_id";
  static const String _COLUMN_CONTENT = "content";
  static const String _COLUMN_CONTENT_HTML = "content_html";
  static const String _COLUMN_CONTENT_HTML_STYLE = "content_html_style";
  static const String _COLUMN_CONTENT_HTML_WITH_RUBY = "content_html_with_ruby";
  static const String _COLUMN_CONTENT_HTML_WITH_RUBY_STYLE =
      "content_html_with_ruby_style";

  static const String SQL_TABLE = '''
  CREATE TABLE $_TABLE_NAME (
    $_COLUMN_NEWS_ID TEXT PRIMARY KEY NOT NULL,
    $_COLUMN_CONTENT TEXT NOT NULL,
    $_COLUMN_CONTENT_HTML TEXT NOT NULL,
    $_COLUMN_CONTENT_HTML_STYLE TEXT NOT NULL,
    $_COLUMN_CONTENT_HTML_WITH_RUBY TEXT NOT NULL,
    $_COLUMN_CONTENT_HTML_WITH_RUBY_STYLE TEXT NOT NULL
    )
  ''';

  static final instance = NewsDetailsDb._();

  NewsDetailsDb._();

  Future<int> insertNewsDetails(NewsDetailsEntity entity) async {
    final Database myDb = await DbHelper.instance.db;

    return await myDb.insert(
      _TABLE_NAME,
      entity.toJson(),
      /*conflictAlgorithm: ConflictAlgorithm.ignore*/
    );
  }

  Future<NewsDetailsEntity> newsDetails(String newsId) async {
    final Database myDb = await DbHelper.instance.db;

    if (newsId == null || newsId.isEmpty) {
      return null;
    }

    final List<Map<String, dynamic>> maps = await myDb.query(_TABLE_NAME,
        where: '$_COLUMN_NEWS_ID == ?', whereArgs: [newsId]);

    final List<NewsDetailsEntity> entities = _generateNewsEntities(maps);
    if (entities.isEmpty) {
      return null;
    }

    return entities[0];
  }

  List<NewsDetailsEntity> _generateNewsEntities(
      List<Map<String, dynamic>> maps) {
    if (maps == null || maps.isEmpty) {
      return [];
    }
    return List.generate(maps.length, (i) {
      var kv = maps[i];
      return NewsDetailsEntity(
        newsId: kv[_COLUMN_NEWS_ID],
        content: kv[_COLUMN_CONTENT],
        contentHtml: kv[_COLUMN_CONTENT_HTML],
        contentHtmlStyle: kv[_COLUMN_CONTENT_HTML_STYLE],
        contentHtmlWithRuby: kv[_COLUMN_CONTENT_HTML_WITH_RUBY],
        contentHtmlWithRubyStyle: kv[_COLUMN_CONTENT_HTML_WITH_RUBY_STYLE],
      );
    });
  }
}
