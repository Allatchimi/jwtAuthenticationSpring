package com.kidami.security.requests;

import lombok.Data;

import java.util.List;

@Data
public class LessonVideoRequest {

    private List<LessonVideoItemRequest> lessonItems;
    private boolean isPlay;

    public LessonVideoRequest() {
    }

    public LessonVideoRequest(List<LessonVideoItemRequest> lessonItems, boolean isPlay) {
        this.lessonItems = lessonItems;
        this.isPlay = isPlay;
    }

    public List<LessonVideoItemRequest> getLessonItems() {
        return lessonItems;
    }

    public void setLessonItems(List<LessonVideoItemRequest> lessonItems) {
        this.lessonItems = lessonItems;
    }

    public boolean isPlay() {
        return isPlay;
    }

    public void setPlay(boolean play) {
        isPlay = play;
    }
}
