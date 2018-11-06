package ru.lliepmah.lib;

import android.view.View;

import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


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
        MatcherAssert.assertThat(ViewModelWrapper.build(mObj, BUILDER_ID), Is.is(ViewModelWrapper.class));
    }

    @Test
    public void getModel() throws Exception {
        MatcherAssert.assertThat(mViewModelWrapper.getModel(), Is.is(mObj));
    }

    @Test
    public void getBuilderId() throws Exception {
        MatcherAssert.assertThat(mViewModelWrapper.getBuilderId(), Is.is(BUILDER_ID));
    }

}