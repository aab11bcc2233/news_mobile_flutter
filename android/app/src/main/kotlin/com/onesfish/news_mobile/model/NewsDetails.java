package com.onesfish.news_mobile.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Create by htp on 2019/8/21
 */
public class NewsDetails implements Parcelable {


    /**
     * content :
     * content_html :
     * <style>
     * body {
     * padding: 2.0rem;
     * font-family: "Helvetica Neue", Helvetica, Arial, "PingFang SC", "Hiragino Sans GB", "Heiti SC", "Microsoft YaHei", "WenQuanYi Micro Hei", sans-serif;
     * word-wrap: break-word;
     * }
     * h1 {
     * font-size: 2.0rem;
     * line-height: 4.0rem;
     * }
     * p {
     * font-size: 2.0rem;
     * line-height: 4.0rem;
     * text-indent: 4.0rem;
     * }
     * rt {
     * font-size: 1.8rem;
     * }
     * .under {
     * padding-bottom: 4px;
     * border-bottom: 2px solid #000;
     * }
     * </style>
     * <body></body>
     * content_html_with_ruby :
     * <style>
     * body {
     * padding: 2.0rem;
     * font-family: "Helvetica Neue", Helvetica, Arial, "PingFang SC", "Hiragino Sans GB", "Heiti SC", "Microsoft YaHei", "WenQuanYi Micro Hei", sans-serif;
     * word-wrap: break-word;
     * }
     * h1 {
     * font-size: 2.0rem;
     * line-height: 5.8rem;
     * }
     * p {
     * font-size: 2.0rem;
     * line-height: 5.8rem;
     * text-indent: 4.0rem;
     * }
     * rt {
     * font-size: 1.8rem;
     * }
     * .under {
     * padding-bottom: 4px;
     * border-bottom: 2px solid #000;
     * }
     * </style>
     * <body></body>
     */

    private String content;
    @SerializedName("content_html")
    private String contentHtml;
    @SerializedName("content_html_style")
    private String contentHtmlStyle;
    @SerializedName("content_html_with_ruby")
    private String contentHtmlWithRuby;
    @SerializedName("content_html_with_ruby_style")
    private String contentHtmlWithRubyStyle;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContentHtml() {
        return contentHtml;
    }

    public void setContentHtml(String contentHtml) {
        this.contentHtml = contentHtml;
    }

    public String getContentHtmlWithRuby() {
        return contentHtmlWithRuby;
    }

    public void setContentHtmlWithRuby(String contentHtmlWithRuby) {
        this.contentHtmlWithRuby = contentHtmlWithRuby;
    }

    public String getContentHtmlStyle() {
        return contentHtmlStyle;
    }

    public void setContentHtmlStyle(String contentHtmlStyle) {
        this.contentHtmlStyle = contentHtmlStyle;
    }

    public String getContentHtmlWithRubyStyle() {
        return contentHtmlWithRubyStyle;
    }

    public void setContentHtmlWithRubyStyle(String contentHtmlWithRubyStyle) {
        this.contentHtmlWithRubyStyle = contentHtmlWithRubyStyle;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.content);
        dest.writeString(this.contentHtml);
        dest.writeString(this.contentHtmlStyle);
        dest.writeString(this.contentHtmlWithRuby);
        dest.writeString(this.contentHtmlWithRubyStyle);
    }

    public NewsDetails() {
    }

    protected NewsDetails(Parcel in) {
        this.content = in.readString();
        this.contentHtml = in.readString();
        this.contentHtmlStyle = in.readString();
        this.contentHtmlWithRuby = in.readString();
        this.contentHtmlWithRubyStyle = in.readString();
    }

    public static final Parcelable.Creator<NewsDetails> CREATOR = new Parcelable.Creator<NewsDetails>() {
        @Override
        public NewsDetails createFromParcel(Parcel source) {
            return new NewsDetails(source);
        }

        @Override
        public NewsDetails[] newArray(int size) {
            return new NewsDetails[size];
        }
    };
}
