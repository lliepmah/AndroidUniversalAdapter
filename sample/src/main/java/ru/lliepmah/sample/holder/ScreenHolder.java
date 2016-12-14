package ru.lliepmah.sample.holder;

import android.view.View;
import android.widget.TextView;

import ru.lliepmah.HolderBuilder;
import ru.lliepmah.HolderConstructor;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.lliepmah.lib.DefaultViewHolder;
import ru.lliepmah.sample.R;
import ru.lliepmah.sample.model.Screen;

/**
 * Created by Arthur Korchagin on 25.10.16
 */

@HolderBuilder(R.layout.li_screen)
public class ScreenHolder extends DefaultViewHolder<Screen> {

    private final OnOpenScreenListener mOnOpenScreenListener;

    @BindView(R.id.li_screen_tv_name)
    TextView mTvText;

    private Screen mScreen;

    @HolderConstructor
    ScreenHolder(View view, OnOpenScreenListener onOpenScreenListener) {
        super(view);
        ButterKnife.bind(this, view);
        mOnOpenScreenListener = onOpenScreenListener;
    }

    public ScreenHolder(View itemView) {
        super(itemView);
        mOnOpenScreenListener = null;
    }

    @Override
    public void bind(Screen screen) {
        mScreen = screen;
        mTvText.setText(screen.getTitle());
    }

    @OnClick(R.id.li_screen_root)
    void onScreenChoose() {
        if (mOnOpenScreenListener != null) {
            mOnOpenScreenListener.onOpenScreen(mScreen);
        }
    }

    public interface OnOpenScreenListener {
        void onOpenScreen(Screen screen);
    }
}
