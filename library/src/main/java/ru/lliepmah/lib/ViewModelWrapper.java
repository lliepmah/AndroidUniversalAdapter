package ru.lliepmah.lib;


/**
 * Created by Arthur Korchagin on 03.02.16
 */
public class ViewModelWrapper<T> {

    private final T mViewModel;
    private final int mBuilderId;

    public static <T> ViewModelWrapper<T> build(T viewModel, int builderId) {
        return new ViewModelWrapper<>(viewModel, builderId);
    }

    public ViewModelWrapper(T viewModel, int builderId) {
        mViewModel = viewModel;
        mBuilderId = builderId;
    }

    public T getModel() {
        return mViewModel;
    }

    public int getBuilderId() {
        return mBuilderId;
    }
}
