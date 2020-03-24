import 'package:intl/intl.dart';

class NewsEntity {
  String newsId;
  String title;
  int newsPrearrangedTime;
  String _newsPrearrangedTimeFormat;
  String imageUrl;
  String voiceUrl;
  String imageFilePath;

  NewsEntity(
      {this.newsPrearrangedTime,
      this.title,
      this.newsId,
      this.imageUrl,
      this.voiceUrl,
      this.imageFilePath});

  String get newsPrearrangedTimeFormat {
    if (_newsPrearrangedTimeFormat == null) {
      _newsPrearrangedTimeFormat = DateFormat("yyyy-MM-dd HH:mm")
          .format(DateTime.fromMillisecondsSinceEpoch(newsPrearrangedTime));
    }

    return _newsPrearrangedTimeFormat;
  }

  NewsEntity.fromJson(Map<String, dynamic> json) {
    newsPrearrangedTime = json['news_prearranged_time'];
    title = json['title'];
    newsId = json['news_id'];
    imageUrl = json['image_url'];
    voiceUrl = json['voice_url'];
    imageFilePath = json['image_file_path'];
  }

  Map<String, dynamic> toJson({bool skipImageFilePath: false}) {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['news_prearranged_time'] = this.newsPrearrangedTime;
    data['title'] = this.title;
    data['news_id'] = this.newsId;
    data['image_url'] = this.imageUrl;
    data['voice_url'] = this.voiceUrl;
    if (!skipImageFilePath) {
      data['image_file_path'] = this.imageFilePath;
    }
    return data;
  }
}
