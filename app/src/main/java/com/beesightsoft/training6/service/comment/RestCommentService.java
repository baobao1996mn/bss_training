package com.beesightsoft.training6.service.comment;

import com.beesightsoft.training6.service.model.Comment;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

public interface RestCommentService {

    @GET("comments")
    Call<List<Comment>> getComments();

    @GET("comments")
    Observable<List<Comment>> getCommentsUseRx();

    @DELETE("comments/{id}")
    Observable<Comment> deleteCommentUseRx(@Path("id") String id);

    @POST("comments")
    Observable<Comment> insertCommentUseRx(@Body Comment comment);
}
