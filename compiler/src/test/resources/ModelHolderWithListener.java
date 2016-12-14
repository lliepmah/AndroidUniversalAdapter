package com.lliepmah;

import android.view.View;

import ru.lliepmah.HolderBuilder;
import ru.lliepmah.lib.DefaultViewHolder;


@HolderBuilder(2)
public class ModelHolderWithListener extends DefaultViewHolder<ModelHolderWithListener.Model> {

    private final OnCLickListener mOnCLickListener;

    public ModelHolderWithListener(View itemView, OnCLickListener onCLickListener) {
        super(itemView);
        mOnCLickListener = onCLickListener;
    }

    @Override
    public void bind(Model model) {
    }

    public static class Model {
        int id;
        String name;
    }

    interface OnCLickListener {
        void onClick();
    }

}