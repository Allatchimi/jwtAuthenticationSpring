package com.kidami.security.requests;

import lombok.Data;

@Data
public class LessonVideoItemRequest {

    private String name;
    private String url;
    private String thumbnail;

    public LessonVideoItemRequest() {
    }

    public LessonVideoItemRequest(String name, String url, String thumbnail) {
        this.name = name;
        this.url = url;
        this.thumbnail = thumbnail;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }
}
