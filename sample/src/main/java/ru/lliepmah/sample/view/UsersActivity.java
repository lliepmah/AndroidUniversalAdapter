package ru.lliepmah.sample.view;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.lliepmah.lib.UniversalAdapter;
import ru.lliepmah.sample.R;
import ru.lliepmah.sample.holder.LabelHolderBuilder;
import ru.lliepmah.sample.holder.UserDetailHolder;
import ru.lliepmah.sample.holder.UserDetailHolderBuilder;
import ru.lliepmah.sample.holder.UserHolder;
import ru.lliepmah.sample.holder.UserHolderBuilder;
import ru.lliepmah.sample.model.Person;

import static ru.lliepmah.sample.data.DataSource.getUsers;

/**
 * Created by Arthur Korchagin on 27.10.16
 */

public class UsersActivity extends AppCompatActivity implements UserHolder.OnPersonClickListener, UserDetailHolder.OnDetailPersonClickListener {

    @BindView(R.id.rv_items)
    RecyclerView mRecyclerView;

    private UniversalAdapter mAdapter;

    private List<Person> mUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);
        ButterKnife.bind(this);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new UniversalAdapter(new UserHolderBuilder(this),
                new UserDetailHolderBuilder(this),
                new LabelHolderBuilder(""));

        mUsers = getUsers();
        mAdapter.add(getString(R.string.users));
        mAdapter.addAll(mUsers.subList(0, 2), UserHolderBuilder.HOLDER_ID);

        mRecyclerView.setAdapter(mAdapter);
    }

    @OnClick(R.id.btn_add_items)
    void onAddUser() {
        int itemCount = mAdapter.getItemCount(Person.class);
        if (itemCount < mUsers.size()) {
            mAdapter.add(mUsers.get(itemCount), UserHolderBuilder.HOLDER_ID);
            mAdapter.notifyItemChanged(itemCount);
        }
    }

    @Override
    public void onPersonClick(Person person) {
        int index = mAdapter.indexOf(person);
        mAdapter.replace(index, person, UserDetailHolderBuilder.HOLDER_ID);
        mAdapter.notifyItemChanged(index);
    }

    @Override
    public void onDetailPersonClick(Person person) {
        int index = mAdapter.indexOf(person);
        mAdapter.replace(index, person, UserHolderBuilder.HOLDER_ID);
        mAdapter.notifyItemChanged(index);
    }

}
