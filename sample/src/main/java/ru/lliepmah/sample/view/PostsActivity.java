package ru.lliepmah.sample.view;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.lliepmah.lib.UniversalAdapter;
import ru.lliepmah.sample.R;
import ru.lliepmah.sample.holder.LabelHolderBuilder;
import ru.lliepmah.sample.holder.PostBestHolderBuilder;
import ru.lliepmah.sample.holder.PostHolderBuilder;
import ru.lliepmah.sample.holder.listeners.OnDetailListener;
import ru.lliepmah.sample.holder.listeners.OnLikeListener;
import ru.lliepmah.sample.model.Post;

import static ru.lliepmah.sample.data.DataSource.getBestPosts;
import static ru.lliepmah.sample.data.DataSource.getPosts;

/**
 * Created by Arthur Korchagin on 27.10.16
 */

public class PostsActivity extends AppCompatActivity implements OnDetailListener, OnLikeListener {

    @BindView(R.id.rv_items)
    RecyclerView mRecyclerView;

    private UniversalAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new UniversalAdapter(new PostHolderBuilder(this, this),
                new PostBestHolderBuilder(this, this),
                new LabelHolderBuilder(getString(R.string.posts_category)));

        mAdapter.add(getString(R.string.best));
        mAdapter.addAll(getBestPosts(), PostBestHolderBuilder.HOLDER_ID);
        mAdapter.add(getString(R.string.other));
        mAdapter.addAll(getPosts(), PostHolderBuilder.HOLDER_ID);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onDetailClick(Post post) {
        showMessage(getString(R.string.open_post_id, post.getId()));
    }


    @Override
    public void onLikeClick(Post post) {
        showMessage(getString(R.string.like_post_id, post.getId()));
    }

    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
