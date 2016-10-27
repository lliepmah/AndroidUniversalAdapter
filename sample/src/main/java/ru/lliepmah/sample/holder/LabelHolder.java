package ru.lliepmah.sample.holder;

import android.view.View;
import android.widget.TextView;

import com.lliepmah.HolderBuilder;
import com.lliepmah.HolderConstructor;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.lliepmah.lib.DefaultViewHolder;
import ru.lliepmah.sample.R;

/**
 * Created by Arthur Korchagin on 25.10.16
 */

@HolderBuilder(R.layout.li_label)
class LabelHolder extends DefaultViewHolder<String> {

    private final String mValue;

    @BindView(R.id.li_label_tv_text)
    TextView mTvText;


    @HolderConstructor
    LabelHolder(View view, String value) {
        super(view);
        ButterKnife.bind(this, view);
        mValue = value;
    }

    public LabelHolder(View itemView) {
        super(itemView);
        mValue = "";
    }

    @Override
    public void bind(String text) {
        mTvText.setText(mValue + text);
    }
}
