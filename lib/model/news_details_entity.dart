class NewsDetailsEntity {
  String newsId;
  String contentHtmlWithRuby;
  String contentHtmlWithRubyStyle;
  String content;
  String contentHtml;
  String contentHtmlStyle;

  NewsDetailsEntity(
      {this.newsId, this.contentHtmlWithRuby, this.contentHtmlWithRubyStyle, this.content, this.contentHtml, this.contentHtmlStyle});

  NewsDetailsEntity.fromJson(Map<String, dynamic> json) {
    newsId = json['news_id'];
    contentHtmlWithRuby = json['content_html_with_ruby'];
    contentHtmlWithRubyStyle = json['content_html_with_ruby_style'];
    content = json['content'];
    contentHtml = json['content_html'];
    contentHtmlStyle = json['content_html_style'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['news_id'] = this.newsId;
    data['content_html_with_ruby'] = this.contentHtmlWithRuby;
    data['content_html_with_ruby_style'] = this.contentHtmlWithRubyStyle;
    data['content'] = this.content;
    data['content_html'] = this.contentHtml;
    data['content_html_style'] = this.contentHtmlStyle;
    return data;
  }
}
