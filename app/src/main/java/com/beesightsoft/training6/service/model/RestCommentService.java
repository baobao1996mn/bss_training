package com.beesightsoft.training6.service.model;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import rx.Observable;

public interface RestCommentService {

    @GET("comments")
    Call<List<Comment>> getComments();

    @GET("comments")
    Observable<List<Comment>> getCommentsUseRx();
}
