package ru.lliepmah.sample.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import ru.lliepmah.lib.UniversalAdapter;
import ru.lliepmah.sample.R;
import ru.lliepmah.sample.holder.LabelHolderBuilder;
import ru.lliepmah.sample.holder.ScreenHolder;
import ru.lliepmah.sample.holder.ScreenHolderBuilder;
import ru.lliepmah.sample.model.Screen;

import static ru.lliepmah.sample.data.DataSource.getScreens;

public class MainActivity extends AppCompatActivity implements ScreenHolder.OnOpenScreenListener {


    RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = (RecyclerView) findViewById(R.id.rv_items);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        UniversalAdapter adapter = new UniversalAdapter(new ScreenHolderBuilder(this),
                new LabelHolderBuilder(""));

        adapter.add(getString(R.string.menu));
        adapter.addAll(getScreens());

        mRecyclerView.setAdapter(adapter);
    }

    @Override
    public void onOpenScreen(Screen screen) {
        startActivity(new Intent(this, screen.getActivityClass()));
    }
}
