import 'package:news_mobile/model/news_details_entity.dart';
import 'package:news_mobile/model/news_entity.dart';

class EntityFactory {
  static T generateOBJ<T>(json) {
    if (1 == 0) {
      return null;
    } else if (T.toString() == "NewsDetailsEntity") {
      return NewsDetailsEntity.fromJson(json) as T;
    } else if (T.toString() == "NewsEntity") {
      return NewsEntity.fromJson(json) as T;
    } else {
      return null;
    }
  }
}