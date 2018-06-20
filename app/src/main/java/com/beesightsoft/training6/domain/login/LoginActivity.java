package com.beesightsoft.training6.domain.login;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.beesightsoft.training6.R;
import com.beesightsoft.training6.domain.http.HttpActivity;
import com.beesightsoft.training6.domain.http.HttpActivity_;
import com.beesightsoft.training6.factory.DaggerApplicationComponent;
import com.beesightsoft.training6.service.model.Comment;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.internal.CallbackManagerImpl;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.hannesdorfmann.mosby.mvp.MvpActivity;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.IconicsDrawable;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

@SuppressLint("Registered")
@EActivity(R.layout.activity_login)
public class LoginActivity extends MvpActivity<LoginView, LoginPresenter> implements
        LoginView,
        GoogleApiClient.OnConnectionFailedListener,
        FacebookCallback {
    final String TAG = LoginActivity.class.getSimpleName();
    final int RC_SIGN_IN_GOOGLE = 14524;

    private GoogleApiClient googleApiClient;
    private CallbackManager callbackManager;
    private CommentAdapter commentAdapter;

    @ViewById(R.id.activity_login_recycler_view_comments)
    protected RecyclerView commentRecyclerView;

    @Inject
    protected LoginPresenter presenter;

    @AfterInject
    void afterInject() {
        DaggerApplicationComponent.builder()
                .build()
                .inject(this);

        //todo init twitter
        Twitter.initialize(this);
    }

    @NonNull
    @Override
    public LoginPresenter createPresenter() {
        return presenter;
    }

    @SuppressLint("SetTextI18n")
    @AfterViews
    void afterView() {
        presenter.getComments();
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager, this);

        googleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .addOnConnectionFailedListener(this)
                .build();

        commentAdapter = new CommentAdapter(this.getPresenter(), new ArrayList<>());
        commentRecyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        commentRecyclerView.setHasFixedSize(true);
        commentRecyclerView.setAdapter(commentAdapter);
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
        Toast.makeText(this, "Items: " + comments.size(), Toast.LENGTH_SHORT).show();
        commentAdapter.setData(comments);
    }

    @Override
    public void onGetCommentsFailed(Throwable throwable) {
        Toast.makeText(this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onInsertCommentSuccessful(Comment comment) {
        Toast.makeText(getBaseContext(), comment.getId() + " inserted", Toast.LENGTH_SHORT).show();
        commentAdapter.addComment(0, comment);
    }

    @Override
    public void onInsertCommentFailed(Throwable throwable) {
        Toast.makeText(getBaseContext(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        googleApiClient.disconnect();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_login, menu);
        menu.getItem(0).setIcon(new IconicsDrawable(getBaseContext())
                .icon(FontAwesome.Icon.faw_plus)
                .actionBar()
                .color(Color.WHITE));
        menu.getItem(1).setIcon(new IconicsDrawable(getBaseContext())
                .icon(FontAwesome.Icon.faw_sync)
                .actionBar()
                .color(Color.WHITE));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_login_add_comment:
                presenter.addComment();
                break;
            case R.id.menu_login_refresh:
                presenter.getComments();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    //todo facebook click
    @Click(R.id.activity_login_btn_fb)
    protected void clickLoginWithFacebook() {
        LoginManager.getInstance().logInWithReadPermissions(this, null);
    }

    //todo twitter click
    @Click(R.id.activity_login_btn_twitter)
    protected void clickLoginWithTwitter() {
        TwitterLoginButton btnTwitter = new TwitterLoginButton(this);
        btnTwitter.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                Toast.makeText(getBaseContext(), result.data.getUserName(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void failure(TwitterException exception) {
                Toast.makeText(getBaseContext(), exception.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        btnTwitter.callOnClick();
    }

    //todo google click
    @Click(R.id.activity_login_btn_google)
    protected void clickLoginWithGoogle() {
        Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(intent, RC_SIGN_IN_GOOGLE);
    }


    //todo result
    private void getResultFromTwitter(Intent data) {
        /*
        Get result from onActivityResult of Twitter
        */
        String tk = data.getExtras().getString("tk", "");
        String ts = data.getExtras().getString("ts", "");
        String screen_name = data.getExtras().getString("screen_name", "");
        Long user_id = data.getExtras().getLong("user_id", 0);

        TwitterAuthToken token = new TwitterAuthToken(tk, ts);
        TwitterSession session = new TwitterSession(token, user_id, screen_name);

        Toast.makeText(getBaseContext(), "Twitter: " + session.getUserName(), Toast.LENGTH_SHORT).show();
    }

    private void getResultFromGoogle(Intent data) {
        /*
        Get result from onActivityResult of Google
        */
        GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
        if (result.isSuccess()) {
            Toast.makeText(this, "Google: success", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Google: failed", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == TwitterAuthConfig.DEFAULT_AUTH_REQUEST_CODE) {
            getResultFromTwitter(data);
        } else if (requestCode == CallbackManagerImpl.RequestCodeOffset.Login.toRequestCode()) {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        } else if (requestCode == RC_SIGN_IN_GOOGLE) {
            getResultFromGoogle(data);
        }
    }

    //todo google callbacks
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "onConnectionFailed", Toast.LENGTH_SHORT).show();
    }


    //todo facebook callbacks
    @Override
    public void onSuccess(Object o) {
        Toast.makeText(this, "onSuccess", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCancel() {

    }

    @Override
    public void onError(FacebookException error) {

    }

    @Click(R.id.activity_login_btn_http_example)
    protected void onClickHttpExample(View view) {
        /*
        Intent to HttpActivity
         */
        Intent intent = new Intent(LoginActivity.this, HttpActivity_.class);
        startActivity(intent);
    }
}
