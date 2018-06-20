package com.beesightsoft.training6.domain.http;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.beesightsoft.training6.R;
import com.beesightsoft.training6.domain.login.LoginPresenter;
import com.beesightsoft.training6.service.comment.RestCommentService;
import com.beesightsoft.training6.service.model.Comment;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.IconicsDrawable;

import java.util.List;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class CommentSimpleAdapter extends RecyclerView.Adapter<CommentSimpleAdapter.CommentViewHolder> {
    List<Comment> comments;
    HttpActivity presenter;

    public CommentSimpleAdapter(HttpActivity presenter, List<Comment> comments) {
        this.presenter = presenter;
        this.comments = comments;
    }

    @Override
    public CommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_simple_item_layout, parent, false);
        CommentViewHolder viewHolder = new CommentViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CommentViewHolder holder, int position) {
        Comment comment = comments.get(position);
        holder.bindView(comment);
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    public void setData(List<Comment> comments) {
        this.comments = comments;
        this.notifyDataSetChanged();
    }

    public void addComment(int index, Comment comment) {
        if (index == -1)
            index = comments.size();

        this.comments.add(index, comment);
        this.notifyDataSetChanged();
    }

    public void removeComment(int index) {
        if (index < comments.size() && index >= 0) {
            this.comments.remove(index);
            this.notifyDataSetChanged();
        }
    }

    public Comment getFirstItem() {
        return comments.size() == 0 ? null : comments.get(0);
    }

    class CommentViewHolder extends RecyclerView.ViewHolder {
        @Inject
        protected RestCommentService restCommentService;

        View itemView;
        TextView txtName, txtEmail, txtBody;
        Comment comment;

        CommentViewHolder(View itemView) {
            super(itemView);

            this.itemView = itemView;
            this.txtName = itemView.findViewById(R.id.comment_item_tv_name);
            this.txtEmail = itemView.findViewById(R.id.comment_item_tv_email);
            this.txtBody = itemView.findViewById(R.id.comment_item_tv_body);
        }

        void bindView(Comment comment) {
            this.comment = comment;
            this.txtName.setText(comment.getName());
            this.txtEmail.setText(comment.getNiceEmail());
            this.txtBody.setText(comment.getBody());
        }
    }
}
