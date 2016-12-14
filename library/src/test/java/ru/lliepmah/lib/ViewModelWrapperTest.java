package ru.lliepmah.lib;

import android.view.View;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * Created by Arthur Korchagin on 14.12.16
 */
@RunWith(MockitoJUnitRunner.class)
public class ViewModelWrapperTest {

    private static final int BUILDER_ID = 123;

    @Mock
    View mItemView;

    private Object mObj;

    private ViewModelWrapper<Object> mViewModelWrapper;

    @Before
    public void setUp() throws Exception {
        mObj = new Object();
        mViewModelWrapper = new ViewModelWrapper<>(mObj, BUILDER_ID);
    }

    @Test
    public void build() throws Exception {
        assertThat(ViewModelWrapper.build(mObj, BUILDER_ID), is(ViewModelWrapper.class));
    }

    @Test
    public void getModel() throws Exception {
        assertThat(mViewModelWrapper.getModel(), is(mObj));
    }

    @Test
    public void getBuilderId() throws Exception {
        assertThat(mViewModelWrapper.getBuilderId(), is(BUILDER_ID));
    }

}