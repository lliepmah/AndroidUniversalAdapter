package ru.lliepmah.sample.holder;

import android.support.annotation.NonNull;
import android.view.View;

import ru.lliepmah.HolderBuilder;
import ru.lliepmah.sample.R;

/**
 * @author by Arthur Korchagin on 01.07.18.
 */
@HolderBuilder(R.layout.li_label)
public class ChildViewHolder extends BaseViewHolder<String> {

    ChildViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void bind(@NonNull String model) {
        super.bind(model);
    }
}
