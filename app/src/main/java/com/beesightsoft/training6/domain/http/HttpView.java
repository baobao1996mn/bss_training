package com.beesightsoft.training6.domain.http;

import com.beesightsoft.training6.service.model.Comment;
import com.hannesdorfmann.mosby.mvp.MvpView;

import java.util.List;

public interface HttpView extends MvpView {
    void showLoading();
    void hideLoading();
    void onGetCommentsSuccessful(List<Comment> comments);
    void onGetCommentsFailed(Exception exception);
    void onInsertCommentSuccessful(Comment comment);
    void onInsertCommentFailed(Exception exception);
    void onDeleteCommentSuccessful(boolean result);
    void onDeleteCommentFailed(Exception exception);
}
