package ru.lliepmah.sample.holder;

import android.view.View;

import ru.lliepmah.lib.DefaultViewHolder;

/**
 * @author by Arthur Korchagin on 01.07.18.
 */
public abstract class BaseViewHolder<T extends CharSequence> extends DefaultViewHolder<T> {

    public BaseViewHolder(View itemView) {
        super(itemView);
    }

    T mModel;

    @Override
    public void bind(T model) {

    }
}
