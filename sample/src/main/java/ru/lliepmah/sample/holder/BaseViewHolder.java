package ru.lliepmah.sample.holder;

import android.support.annotation.NonNull;
import android.view.View;

import ru.lliepmah.lib.DefaultViewHolder;

/**
 * @author by Arthur Korchagin on 01.07.18.
 */
public abstract class BaseViewHolder<T extends CharSequence> extends DefaultViewHolder<T> {

    BaseViewHolder(View itemView) {
        super(itemView);
    }

    T mModel;

    @Override
    public void bind(@NonNull T model) {

    }
}
