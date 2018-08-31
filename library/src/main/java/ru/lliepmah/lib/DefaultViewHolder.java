package ru.lliepmah.lib;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Arthur Korchagin on 30.05.16
 */
public abstract class DefaultViewHolder<T> extends RecyclerView.ViewHolder {

    public DefaultViewHolder(View itemView) {
        super(itemView);
    }

    public abstract void bind(@NonNull T model);

    public View getView() {
        return itemView;
    }

}
