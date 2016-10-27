package ru.lliepmah.lib;

import android.view.ViewGroup;

public abstract class Builder<M> {

    abstract public Class<M> getHolderClass();

    abstract public int getId();

    abstract public DefaultViewHolder<M> build(ViewGroup parent);
}
