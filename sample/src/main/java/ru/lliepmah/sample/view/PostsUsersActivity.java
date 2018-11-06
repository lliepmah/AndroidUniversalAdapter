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
import ru.lliepmah.sample.data.DataSource;
import ru.lliepmah.sample.holder.LabelHolderBuilder;
import ru.lliepmah.sample.holder.PostHolderBuilder;
import ru.lliepmah.sample.holder.UserHolder;
import ru.lliepmah.sample.holder.UserHolderBuilder;
import ru.lliepmah.sample.holder.listeners.OnDetailListener;
import ru.lliepmah.sample.holder.listeners.OnLikeListener;
import ru.lliepmah.sample.model.Person;
import ru.lliepmah.sample.model.Post;

/**
 * Created by Arthur Korchagin on 27.10.16
 */

public class PostsUsersActivity extends AppCompatActivity implements OnDetailListener, OnLikeListener, UserHolder.OnPersonClickListener {

    @BindView(R.id.rv_items)
    RecyclerView mRecyclerView;

    private UniversalAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new UniversalAdapter(new UserHolderBuilder(this),
                new PostHolderBuilder(this, this),
                new LabelHolderBuilder(""));

        mAdapter.add(getString(R.string.users));
        mAdapter.addAll(DataSource.getUsers());
        mAdapter.add(getString(R.string.posts_category));
        mAdapter.addAll(DataSource.getPosts());
        mAdapter.add(getString(R.string.best));
        mAdapter.addAll(DataSource.getBestPosts());

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

    @Override
    public void onPersonClick(Person person) {

    }
}
