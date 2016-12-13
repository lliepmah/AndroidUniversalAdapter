package ru.lliepmah.lib;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import ru.lliepmah.lib.exceptions.ErrorHandler;


/**
 * Created by Arthur Korchagin on 03.02.16
 */

public class UniversalAdapter extends RecyclerView.Adapter<DefaultViewHolder> {

    private final List<ViewModelWrapper> mItems = new ArrayList<>();
    @SuppressLint("UseSparseArrays")
    private final HashMap<Integer, Builder> mBuilders = new HashMap<>();

    public UniversalAdapter(Builder... builders) {
        for (Builder builder : builders) {
            mBuilders.put(builder.getId(), builder);
        }
    }

    public UniversalAdapter(List<Builder> builders) {
        for (Builder builder : builders) {
            mBuilders.put(builder.getId(), builder);
        }
    }

    @Override
    public DefaultViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return mBuilders.get(viewType).build(parent);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onBindViewHolder(DefaultViewHolder holder, int position) {
        holder.bind(mItems.get(position).getModel());
    }

    @Override
    public int getItemViewType(int position) {
        return mItems.get(position).getBuilderId();
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public int getItemCount(Class itemClass) {
        int count = 0;
        for (ViewModelWrapper wrapper : mItems) {
            if (itemClass.isInstance(wrapper.getModel())) {
                count++;
            }
        }
        return count;
    }


    public void add(int pos, ViewModelWrapper wrapper) {
        if (wrapper != null) {
            mItems.add(pos, wrapper);
        }
    }

    public void replace(int index, Object item, int holderId) {
        if (item != null) {
            Builder builder = findBuilder(holderId);
            if (!builder.getHolderClass().isInstance(item)) {
                ErrorHandler.errorBuilderCannotHandleItem(builder, item);
            }

            mItems.remove(index);
            add(index, ViewModelWrapper.build(item, holderId));
        }
    }

    public void replace(int index, Object item) {
        if (item != null) {
            mItems.remove(index);
            add(index, item);
        }
    }

    public boolean add(ViewModelWrapper wrapper) {
        return wrapper != null && mItems.add(wrapper);
    }

    public void add(Object item, int builderId) {
        add(mItems.size(), item, builderId);
    }

    public boolean add(Object item) {
        return add(mItems.size(), item);
    }

    public boolean add(int index, Object item) {
        if (item != null) {
            int builderId = findBuilderId(item);
            mItems.add(index, ViewModelWrapper.build(item, builderId));
            return true;
        }
        return false;
    }

    public void add(int index, Object item, int builderId) {
        if (item != null) {
            Builder builder = findBuilder(builderId);
            if (!builder.getHolderClass().isInstance(item)) {
                ErrorHandler.errorBuilderCannotHandleItem(builder, item);
            }
            mItems.add(index, ViewModelWrapper.build(item, builderId));
        }
    }

    public Object getItem(int position) {
        return mItems.get(position).getModel();
    }

    public void addAll(List items) {
        if (items != null && items.size() > 0) {
            int builderId = findBuilderId(items.get(0));
            addAll(items, builderId);
        }
    }

    public void addAll(List items, int builderId) {
        if (items != null && items.size() > 0) {
            Builder builder = findBuilder(builderId);
            Object firstItem = items.get(0);
            if (!builder.getHolderClass().isInstance(firstItem)) {
                ErrorHandler.errorBuilderCannotHandleItem(builder, firstItem);
            }

            for (Object item : items) {
                mItems.add(new ViewModelWrapper<>(item, builderId));
            }
        }
    }

    public void replaceAll(List items) {
        mItems.clear();
        addAll(items);
    }


    public void replaceAll(List items, int builderId) {
        mItems.clear();
        addAll(items, builderId);
    }

    public void addAllWrappers(List<ViewModelWrapper> items) {
        mItems.addAll(items);
    }

    public void replaceAllWrappers(List<ViewModelWrapper> elements) {
        mItems.clear();
        mItems.addAll(elements);
    }

    public void removeItems(int startIndex, int endIndex) {
        for (int i = startIndex; i <= endIndex; i++) {
            mItems.remove(startIndex);
        }
    }

    public boolean clear() {
        if (mItems == null || mItems.size() == 0) {
            return false;
        }

        mItems.clear();
        return true;
    }

    /* Internal utils */
    private Builder findBuilder(int builderId) {
        if (!mBuilders.containsKey(builderId)) {
            ErrorHandler.erroNoOneBuildersHaveId(mBuilders.values(), builderId);
        }
        return mBuilders.get(builderId);
    }

    private int findBuilderId(Object item) {
        List<Builder> builders = findBuilders(item);
        if (builders.size() == 0) {
            ErrorHandler.erroNoOneBuildersHandleItem(mBuilders.values(), item);
        } else if (builders.size() > 1) {
            ErrorHandler.errorMoreThanOneBuildersHandleItem(builders, item);
        }
        return builders.get(0).getId();
    }

    @NonNull
    private List<Builder> findBuilders(Object item) {
        List<Builder> list = new LinkedList<>();

        for (Builder builder : mBuilders.values()) {
            if (builder.getHolderClass().isInstance(item)) {
                list.add(builder);
            }
        }
        return list;
    }

    public int indexOf(Object searchItem) {
        for (ViewModelWrapper wrapper : mItems) {
            if (searchItem.equals(wrapper.getModel())) {
                return mItems.indexOf(wrapper);
            }
        }
        return -1;
    }
}
