package ru.lliepmah.sample.holder;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import ru.lliepmah.HolderBuilder;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.lliepmah.lib.DefaultViewHolder;
import ru.lliepmah.sample.R;
import ru.lliepmah.sample.holder.listeners.OnDetailListener;
import ru.lliepmah.sample.holder.listeners.OnLikeListener;
import ru.lliepmah.sample.model.Post;

/**
 * Created by Arthur Korchagin on 25.10.16
 */

@HolderBuilder(R.layout.li_post_best)
public class PostBestHolder extends DefaultViewHolder<Post> {

    private OnLikeListener mOnLikeListener;
    private OnDetailListener mOnDetailListener;

    private Post mPost;

    @BindView(R.id.li_post_best_tv_title)
    TextView mTvTitle;
    @BindView(R.id.li_post_best_tv_text)
    TextView mTvText;

    PostBestHolder(View itemView, OnLikeListener onLikeListener, OnDetailListener onDetailListener) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        mOnLikeListener = onLikeListener;
        mOnDetailListener = onDetailListener;
    }

    @Override
    public void bind(@NonNull Post post) {
        mPost = post;
        mTvText.setText(post.getText());
        mTvTitle.setText(post.getTitle());
    }

    @OnClick(R.id.li_post_best_iv_like)
    void onLikeClick() {
        if (mOnLikeListener != null) {
            mOnLikeListener.onLikeClick(mPost);
        }
    }

    @OnClick(R.id.li_post_best_root)
    void onRootClick() {
        if (mOnDetailListener != null) {
            mOnDetailListener.onDetailClick(mPost);
        }
    }

}
