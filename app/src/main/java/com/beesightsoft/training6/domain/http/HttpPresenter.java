package com.beesightsoft.training6.domain.http;

import android.support.annotation.NonNull;

import com.beesightsoft.training6.service.comment.RestCommentService;
import com.beesightsoft.training6.service.model.Comment;
import com.beesightsoft.training6.utils.HttpUtils;
import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;

import org.apache.commons.httpclient.NameValuePair;

import java.util.List;

import javax.inject.Inject;

public class HttpPresenter extends MvpBasePresenter<HttpView> {

    @Inject
    protected RestCommentService restCommentService;

    @Inject
    public HttpPresenter() {

    }

    public void getComments() {
        String url = "https://jsonplaceholder.typicode.com/comments";
        getView().showLoading();
        try {
            List<Comment> comments = HttpUtils.get(url);
            getView().onGetCommentsSuccessful(comments);
        } catch (Exception e) {
            getView().onGetCommentsFailed(e);
        }
        getView().hideLoading();
    }

    public void deleteComment(@NonNull String id) {
        //todo delete first item
        String url = "https://jsonplaceholder.typicode.com/comments/" + id;
        getView().showLoading();
        try {
            boolean isDeleted = HttpUtils.delete(url);
            getView().onDeleteCommentSuccessful(isDeleted);
        } catch (Exception e) {
            getView().onDeleteCommentFailed(e);
        }
        getView().hideLoading();
    }

    public void insertComment(Comment comment) {
        String url = "https://jsonplaceholder.typicode.com/comments";
        NameValuePair[] nameValuePairs = new NameValuePair[]{
                new NameValuePair("id", comment.getId()),
                new NameValuePair("postId", comment.getPostId()),
                new NameValuePair("name", comment.getName()),
                new NameValuePair("email", comment.getNiceEmail()),
                new NameValuePair("body", comment.getBody()),
        };
        getView().showLoading();
        try {
            Comment result = HttpUtils.post(url, nameValuePairs);
            getView().onInsertCommentSuccessful(result);
        } catch (Exception e) {
            getView().onInsertCommentFailed(e);
        }
        getView().hideLoading();
    }

}
