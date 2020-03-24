import 'dart:async';

import 'package:path/path.dart';
import 'package:sqflite/sqflite.dart';

import 'news_db.dart';
import 'news_details_db.dart';

class DbHelper {
  static final instance = DbHelper._();
  static Database _db;

  DbHelper._();

  Future<Database> get db async {
    if (_db != null) {
      return _db;
    }
    _db = await _initDb();
    return _db;
  }

  _initDb() async {
    final databasesPath = await getDatabasesPath();
    String path = join(databasesPath, 'news.db');
    final ourDb = await openDatabase(path, version: 1, onCreate: _onCreate);
    return ourDb;
  }

  FutureOr<void> _onCreate(Database db, int version) async {
    db.execute(NewsDb.SQL_TABLE);
    db.execute(NewsDetailsDb.SQL_TABLE);
  }

  Future<void> close() async {
    final dbClient = await db;
    final result = await dbClient.close();
    _db = null;
    return result;
  }
}
