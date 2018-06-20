package com.beesightsoft.training6.domain.http;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.beesightsoft.training6.R;
import com.beesightsoft.training6.factory.DaggerApplicationComponent;
import com.beesightsoft.training6.service.model.Comment;
import com.hannesdorfmann.mosby.mvp.MvpActivity;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

@SuppressLint("Registered")
@EActivity(R.layout.activity_http)
public class HttpActivity extends MvpActivity<HttpView, HttpPresenter> implements HttpView {

    @Inject
    protected HttpPresenter presenter;

    @ViewById(R.id.activity_http_recycler_view_comments)
    protected RecyclerView commentRecyclerView;

    private CommentSimpleAdapter simpleAdapter;

    @AfterInject
    void afterInject() {
        DaggerApplicationComponent.builder()
                .build()
                .inject(this);
    }

    @NonNull
    @Override
    public HttpPresenter createPresenter() {
        return presenter;
    }

    @SuppressLint("SetTextI18n")
    @AfterViews
    void afterView() {
        simpleAdapter = new CommentSimpleAdapter(this, new ArrayList<>());
        commentRecyclerView.setAdapter(simpleAdapter);
        commentRecyclerView.setHasFixedSize(true);
        commentRecyclerView.setLayoutManager(new GridLayoutManager(this, 1));

        presenter.getComments();
    }


    @Override
    public void showLoading() {
        Toast.makeText(this, "Show loading dialog ...", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void hideLoading() {
        Toast.makeText(this, "Hide loading dialog ...", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onGetCommentsSuccessful(List<Comment> comments) {
        simpleAdapter.setData(comments);
    }

    @Override
    public void onGetCommentsFailed(Exception exception) {
        Toast.makeText(getBaseContext(), exception.getMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onInsertCommentSuccessful(Comment comment) {
        simpleAdapter.addComment(-1, comment);
        Toast.makeText(getBaseContext(), comment.getId() + " inserted !", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onInsertCommentFailed(Exception exception) {
        Toast.makeText(getBaseContext(), exception.getMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDeleteCommentSuccessful(boolean isSuccessfully) {
        if (isSuccessfully)
            simpleAdapter.removeComment(0);
        Toast.makeText(getBaseContext(), "Delete item: " + (isSuccessfully ? "successfully" : "failed"), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDeleteCommentFailed(Exception exception) {
        Toast.makeText(getBaseContext(), exception.getMessage(), Toast.LENGTH_SHORT).show();
    }

    @Click(R.id.activity_http_btn_get_all)
    protected void onClickGetAll() {
        presenter.getComments();
    }

    @Click(R.id.activity_http_btn_delete_first)
    protected void onClickDeleteFirst() {
        Comment firstComment = simpleAdapter.getFirstItem();
        if (firstComment != null)
            presenter.deleteComment(firstComment.getId());
    }

    @Click(R.id.activity_http_btn_insert_item)
    protected void onInsertedRandom() {
        //fake data
        Comment comment = new Comment();
        comment.setId("501");
        comment.setPostId("101");
        comment.setNiceEmail("email123@gmail.com");
        comment.setName("emailwhoever...");
        comment.setBody("Description of comment ...");
        presenter.insertComment(comment);
    }

}
