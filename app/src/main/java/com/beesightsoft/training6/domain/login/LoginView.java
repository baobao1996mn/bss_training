package com.beesightsoft.training6.domain.login;

import com.beesightsoft.training6.service.model.Comment;
import com.hannesdorfmann.mosby.mvp.MvpView;

import java.util.List;

public interface LoginView extends MvpView {
    void showLoading();
    void hideLoading();
    void onGetCommentsSuccessful(List<Comment> comments);
    void onGetCommentsFailed(Throwable throwable);
    void onInsertCommentSuccessful(Comment comment);
    void onInsertCommentFailed(Throwable throwable);
}
