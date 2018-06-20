package com.beesightsoft.training6.domain.login;

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
import com.beesightsoft.training6.service.comment.RestCommentService;
import com.beesightsoft.training6.service.model.Comment;
import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.IconicsDrawable;

import java.util.List;
import java.util.function.Predicate;

import javax.inject.Inject;

import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {
    List<Comment> comments;
    LoginPresenter presenter;

    public CommentAdapter(LoginPresenter presenter, List<Comment> comments) {
        this.presenter = presenter;
        this.comments = comments;
    }

    @Override
    public CommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_item_layout, parent, false);
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
        this.comments.add(index,comment);
        this.notifyDataSetChanged();
    }

    class CommentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @Inject
        protected RestCommentService restCommentService;

        View itemView;
        TextView txtName, txtEmail, txtBody;
        ImageView imgDelete;
        Comment comment;

        CommentViewHolder(View itemView) {
            super(itemView);

            this.itemView = itemView;
            this.txtName = itemView.findViewById(R.id.comment_item_tv_name);
            this.txtEmail = itemView.findViewById(R.id.comment_item_tv_email);
            this.txtBody = itemView.findViewById(R.id.comment_item_tv_body);
            this.imgDelete = itemView.findViewById(R.id.comment_item_img_delete);

            this.imgDelete.setImageDrawable(new IconicsDrawable(itemView.getContext()).icon(FontAwesome.Icon.faw_times));
            this.imgDelete.setOnClickListener(this);
        }

        void bindView(Comment comment) {
            this.comment = comment;
            this.txtName.setText(comment.getName());
            this.txtEmail.setText(comment.getNiceEmail());
            this.txtBody.setText(comment.getBody());
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void onClick(View view) {
            onClickDeleteItem(view.getContext());
        }


        //todo delete item
        @RequiresApi(api = Build.VERSION_CODES.N)
        void onClickDeleteItem(Context context) {
            new AlertDialog
                    .Builder(context)
                    .setTitle("Remove")
                    .setMessage("Remove this comment (id = " + comment.getId() + ")")
                    .setPositiveButton("Remove", (dialogInterface, i) -> {
                        removeItemById(comment.getId());
                        dialogInterface.dismiss();
                    })
                    .setNegativeButton("Cancel", (dialogInterface, i) -> {
                        dialogInterface.dismiss();
                    })
                    .create()
                    .show();
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        void removeItemById(String id) {
            presenter.getView().showLoading();
            presenter.restCommentService.deleteCommentUseRx(id)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnTerminate(() -> {
                        if (presenter.isViewAttached()) {
                            presenter.getView().hideLoading();
                        }
                    })
                    .subscribe(comment -> {
                        if (presenter.isViewAttached()) {
                            onDeleteCommentSuccessful();
                        }
                    }, throwable -> {
                        if (presenter.isViewAttached()) {
                            onDeleteCommentFailed(throwable);
                        }
                    });
        }

        private void onDeleteCommentFailed(Throwable throwable) {
            Toast.makeText(itemView.getContext(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        private void onDeleteCommentSuccessful() {
            Toast.makeText(itemView.getContext(), comment.getId() + " deleted", Toast.LENGTH_SHORT).show();
            //presenter.getComments();
            comments.removeIf(comment2 -> comment.getId().equals(comment2.getId()));
            notifyDataSetChanged();
        }
    }
}
