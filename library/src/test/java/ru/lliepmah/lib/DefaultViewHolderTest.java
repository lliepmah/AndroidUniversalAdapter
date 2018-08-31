package ru.lliepmah.lib;

import android.support.annotation.NonNull;
import android.view.View;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

/**
 * Created by Arthur Korchagin on 22.12.16
 */

@RunWith(RobolectricTestRunner.class)
//@Config(constants = BuildConfig.class, manifest = "./src/main/AndroidManifest.xml")
@Config(sdk = 21) public class DefaultViewHolderTest {

  private DefaultViewHolder<Object> mDefaultViewHolder;
  private View mItemView;

  @Before public void setUp() {
    mItemView = Mockito.mock(View.class);

    mDefaultViewHolder = new DefaultViewHolder<Object>(mItemView) {
      @Override public void bind(@NonNull Object model) {

      }
    };
  }

  @Test public void getView() throws Exception {
    MatcherAssert.assertThat(mDefaultViewHolder.getView(), Is.is(mItemView));
  }
}