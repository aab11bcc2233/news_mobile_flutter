package com.onesfish.news_mobile.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.htphtp.tools.time.DateUtil;

/**
 * Create by htp on 2019/8/21
 */
public class News implements Parcelable {

    /**
     * top_priority_number : 1
     * top_display_flag : true
     * news_id : k10012038471000
     * news_prearranged_time : 1566355500000
     * title : 昭和天皇との会話を書いた大事な資料が見つかった
     * title_with_ruby : <ruby>昭和天皇<rt>しょうわてんのう</rt></ruby>との<ruby>会話<rt>かいわ</rt></ruby>を<ruby>書<rt>か</rt></ruby>いた<ruby>大事<rt>だいじ</rt></ruby>な<ruby>資料<rt>しりょう</rt></ruby>が<ruby>見<rt>み</rt></ruby>つかった
     * outline_with_ruby : <p><ruby>昭和天皇<rt>しょうわてんのう</rt></ruby>は、<ruby>今<rt>いま</rt></ruby>の<a href='javascript:void(0)' class='dicWin' id='id-0001'><ruby>天皇<rt>てんのう</rt></ruby></a><a href='javascript:void(0)' class='dicWin' id='id-0002'><ruby>陛下<rt>へいか</rt></ruby></a>の<ruby>祖父<rt>そふ</rt></ruby>で、<a href='javascript:void(0)' class='dicWin' id='id-0003'><ruby>上皇<rt>じょうこう</rt></ruby></a>さまの<ruby>父<rt>ちち</rt></ruby>です。
     * news_file_ver : true
     * news_publication_status : true
     * has_news_web_image : true
     * has_news_web_movie : true
     * has_news_easy_image : false
     * has_news_easy_movie : false
     * has_news_easy_voice : true
     * news_web_image_uri :
     * news_web_movie_uri : k10012038471_201908162032_201908162044.mp4
     * news_easy_image_uri : ''
     * news_easy_movie_uri : ''
     * news_easy_voice_uri : k10012038471000.mp4
     */

    @SerializedName("top_priority_number")
    private String topPriorityNumber;
    @SerializedName("top_display_flag")
    private boolean topDisplayFlag;
    @SerializedName("news_id")
    private String newsId;
    @SerializedName("news_prearranged_time")
    private long newsPrearrangedTime;
    @SerializedName("title")
    private String title;
    @SerializedName("title_with_ruby")
    private String titleWithRuby;
    @SerializedName("outline_with_ruby")
    private String outlineWithRuby;
    @SerializedName("news_file_ver")
    private boolean newsFileVer;
    @SerializedName("news_publication_status")
    private boolean newsPublicationStatus;
    @SerializedName("has_news_web_image")
    private boolean hasNewsWebImage;
    @SerializedName("has_news_web_movie")
    private boolean hasNewsWebMovie;
    @SerializedName("has_news_easy_image")
    private boolean hasNewsEasyImage;
    @SerializedName("has_news_easy_movie")
    private boolean hasNewsEasyMovie;
    @SerializedName("has_news_easy_voice")
    private boolean hasNewsEasyVoice;
    @SerializedName("news_web_image_uri")
    private String newsWebImageUri;
    @SerializedName("news_web_movie_uri")
    private String newsWebMovieUri;
    @SerializedName("news_easy_image_uri")
    private String newsEasyImageUri;
    @SerializedName("news_easy_movie_uri")
    private String newsEasyMovieUri;
    @SerializedName("news_easy_voice_uri")
    private String newsEasyVoiceUri;
    @SerializedName("image_url")
    private String imageUrl;
    @SerializedName("image_file_path")
    private String imageFilePath;
    @SerializedName("voice_url")
    private String voiceUrl;
    private String newsPrearrangedTimeFormat;


    public String getNewsPrearrangedTimeFormat() {
        if (newsPrearrangedTimeFormat == null) {
            this.newsPrearrangedTimeFormat = DateUtil.dateFormat("yyyy-MM-dd HH:mm", newsPrearrangedTime);
        }
        return newsPrearrangedTimeFormat;
    }

    public String getTopPriorityNumber() {
        return topPriorityNumber;
    }

    public void setTopPriorityNumber(String topPriorityNumber) {
        this.topPriorityNumber = topPriorityNumber;
    }

    public boolean isTopDisplayFlag() {
        return topDisplayFlag;
    }

    public void setTopDisplayFlag(boolean topDisplayFlag) {
        this.topDisplayFlag = topDisplayFlag;
    }

    public String getNewsId() {
        return newsId;
    }

    public void setNewsId(String newsId) {
        this.newsId = newsId;
    }

    public long getNewsPrearrangedTime() {
        return newsPrearrangedTime;
    }

    public void setNewsPrearrangedTime(long newsPrearrangedTime) {
        this.newsPrearrangedTime = newsPrearrangedTime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitleWithRuby() {
        return titleWithRuby;
    }

    public void setTitleWithRuby(String titleWithRuby) {
        this.titleWithRuby = titleWithRuby;
    }

    public String getOutlineWithRuby() {
        return outlineWithRuby;
    }

    public void setOutlineWithRuby(String outlineWithRuby) {
        this.outlineWithRuby = outlineWithRuby;
    }

    public boolean isNewsFileVer() {
        return newsFileVer;
    }

    public void setNewsFileVer(boolean newsFileVer) {
        this.newsFileVer = newsFileVer;
    }

    public boolean isNewsPublicationStatus() {
        return newsPublicationStatus;
    }

    public void setNewsPublicationStatus(boolean newsPublicationStatus) {
        this.newsPublicationStatus = newsPublicationStatus;
    }

    public boolean isHasNewsWebImage() {
        return hasNewsWebImage;
    }

    public void setHasNewsWebImage(boolean hasNewsWebImage) {
        this.hasNewsWebImage = hasNewsWebImage;
    }

    public boolean isHasNewsWebMovie() {
        return hasNewsWebMovie;
    }

    public void setHasNewsWebMovie(boolean hasNewsWebMovie) {
        this.hasNewsWebMovie = hasNewsWebMovie;
    }

    public boolean isHasNewsEasyImage() {
        return hasNewsEasyImage;
    }

    public void setHasNewsEasyImage(boolean hasNewsEasyImage) {
        this.hasNewsEasyImage = hasNewsEasyImage;
    }

    public boolean isHasNewsEasyMovie() {
        return hasNewsEasyMovie;
    }

    public void setHasNewsEasyMovie(boolean hasNewsEasyMovie) {
        this.hasNewsEasyMovie = hasNewsEasyMovie;
    }

    public boolean isHasNewsEasyVoice() {
        return hasNewsEasyVoice;
    }

    public void setHasNewsEasyVoice(boolean hasNewsEasyVoice) {
        this.hasNewsEasyVoice = hasNewsEasyVoice;
    }

    public String getNewsWebImageUri() {
        return newsWebImageUri;
    }

    public void setNewsWebImageUri(String newsWebImageUri) {
        this.newsWebImageUri = newsWebImageUri;
    }

    public String getNewsWebMovieUri() {
        return newsWebMovieUri;
    }

    public void setNewsWebMovieUri(String newsWebMovieUri) {
        this.newsWebMovieUri = newsWebMovieUri;
    }

    public String getNewsEasyImageUri() {
        return newsEasyImageUri;
    }

    public void setNewsEasyImageUri(String newsEasyImageUri) {
        this.newsEasyImageUri = newsEasyImageUri;
    }

    public String getNewsEasyMovieUri() {
        return newsEasyMovieUri;
    }

    public void setNewsEasyMovieUri(String newsEasyMovieUri) {
        this.newsEasyMovieUri = newsEasyMovieUri;
    }

    public String getNewsEasyVoiceUri() {
        return newsEasyVoiceUri;
    }

    public void setNewsEasyVoiceUri(String newsEasyVoiceUri) {
        this.newsEasyVoiceUri = newsEasyVoiceUri;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getVoiceUrl() {
        return voiceUrl;
    }

    public void setVoiceUrl(String voiceUrl) {
        this.voiceUrl = voiceUrl;
    }

    public String getImageFilePath() {
        return imageFilePath;
    }

    public void setImageFilePath(String imageFilePath) {
        this.imageFilePath = imageFilePath;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.topPriorityNumber);
        dest.writeByte(this.topDisplayFlag ? (byte) 1 : (byte) 0);
        dest.writeString(this.newsId);
        dest.writeLong(this.newsPrearrangedTime);
        dest.writeString(this.title);
        dest.writeString(this.titleWithRuby);
        dest.writeString(this.outlineWithRuby);
        dest.writeByte(this.newsFileVer ? (byte) 1 : (byte) 0);
        dest.writeByte(this.newsPublicationStatus ? (byte) 1 : (byte) 0);
        dest.writeByte(this.hasNewsWebImage ? (byte) 1 : (byte) 0);
        dest.writeByte(this.hasNewsWebMovie ? (byte) 1 : (byte) 0);
        dest.writeByte(this.hasNewsEasyImage ? (byte) 1 : (byte) 0);
        dest.writeByte(this.hasNewsEasyMovie ? (byte) 1 : (byte) 0);
        dest.writeByte(this.hasNewsEasyVoice ? (byte) 1 : (byte) 0);
        dest.writeString(this.newsWebImageUri);
        dest.writeString(this.newsWebMovieUri);
        dest.writeString(this.newsEasyImageUri);
        dest.writeString(this.newsEasyMovieUri);
        dest.writeString(this.newsEasyVoiceUri);
        dest.writeString(this.imageUrl);
        dest.writeString(this.imageFilePath);
        dest.writeString(this.voiceUrl);
        dest.writeString(this.newsPrearrangedTimeFormat);
    }

    public News() {
    }

    protected News(Parcel in) {
        this.topPriorityNumber = in.readString();
        this.topDisplayFlag = in.readByte() != 0;
        this.newsId = in.readString();
        this.newsPrearrangedTime = in.readLong();
        this.title = in.readString();
        this.titleWithRuby = in.readString();
        this.outlineWithRuby = in.readString();
        this.newsFileVer = in.readByte() != 0;
        this.newsPublicationStatus = in.readByte() != 0;
        this.hasNewsWebImage = in.readByte() != 0;
        this.hasNewsWebMovie = in.readByte() != 0;
        this.hasNewsEasyImage = in.readByte() != 0;
        this.hasNewsEasyMovie = in.readByte() != 0;
        this.hasNewsEasyVoice = in.readByte() != 0;
        this.newsWebImageUri = in.readString();
        this.newsWebMovieUri = in.readString();
        this.newsEasyImageUri = in.readString();
        this.newsEasyMovieUri = in.readString();
        this.newsEasyVoiceUri = in.readString();
        this.imageUrl = in.readString();
        this.imageFilePath = in.readString();
        this.voiceUrl = in.readString();
        this.newsPrearrangedTimeFormat = in.readString();
    }

    public static final Parcelable.Creator<News> CREATOR = new Parcelable.Creator<News>() {
        @Override
        public News createFromParcel(Parcel source) {
            return new News(source);
        }

        @Override
        public News[] newArray(int size) {
            return new News[size];
        }
    };
}
