package com.beesightsoft.training6.domain.login;

import android.widget.Toast;

import com.beesightsoft.training6.service.comment.RestCommentService;
import com.beesightsoft.training6.service.model.Comment;
import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class LoginPresenter extends MvpBasePresenter<LoginView> {

    @Inject
    protected RestCommentService restCommentService;

    @Inject
    public LoginPresenter() {

    }

    public void getComments() {
        getView().showLoading();
        restCommentService.getCommentsUseRx()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnTerminate(() -> {
                    if (isViewAttached()) {
                        getView().hideLoading();
                    }
                })
                .subscribe(comments -> {
                    if (isViewAttached()) {
                        getView().onGetCommentsSuccessful(comments);
                    }
                }, throwable -> {
                    if (isViewAttached()) {
                        getView().onGetCommentsFailed(throwable);
                    }
                });
    }


    //todo add item
    void addComment() {
        //todo fake data
        Comment comment = new Comment();
        comment.setId("501");
        comment.setBody("Đây là body");
        comment.setName("BaoBao");
        comment.setNiceEmail("baobaobaobao@gmail.com");
        comment.setPostId("101");

        getView().showLoading();
        restCommentService.insertCommentUseRx(comment)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnTerminate(() -> {
                    if (isViewAttached()) {
                        getView().hideLoading();
                    }
                })
                .subscribe(commentReuslt -> {
                            if (isViewAttached()) {
                                getView().onInsertCommentSuccessful(commentReuslt);
                            }
                        }, throwable -> {
                            if (isViewAttached()) {
                                getView().onInsertCommentFailed(throwable);
                            }
                        }
                );
    }


}
