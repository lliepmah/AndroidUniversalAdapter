package ru.lliepmah.lib;

import android.view.ViewGroup;
import java.util.Arrays;
import java.util.List;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import ru.lliepmah.lib.exceptions.WrongItemException;

/**
 * Created by Arthur Korchagin on 13.12.16
 */

@RunWith(RobolectricTestRunner.class)
//@Config(constants = BuildConfig.class, manifest = "./src/main/AndroidManifest.xml")
@Config(sdk = 21) public class UniversalAdapterTest {

  private static final int FIRST_BUILDER_ID = 1;
  private static final int SECOND_BUILDER_ID = 2;
  private static final int FIRST_VIEW_TYPE = 1;
  private static final int SECOND_VIEW_TYPE = 2;

  private static final int EMPTY_COUNT = 0;
  private static final int ONE_COUNT = 1;

  private static final int POS_FIRST = 0;

  private Builder mBuilder1;
  private Builder mBuilder2;

  @Before public void setUp() throws Exception {
    DefaultViewHolder holder1 = Mockito.mock(DefaultViewHolder.class);
    Mockito.when(holder1.getItemViewType()).thenReturn(FIRST_VIEW_TYPE);

    DefaultViewHolder holder2 = Mockito.mock(DefaultViewHolder.class);
    Mockito.when(holder2.getItemViewType()).thenReturn(SECOND_VIEW_TYPE);

    mBuilder1 = Mockito.mock(Builder.class);
    Mockito.when(mBuilder1.getHolderClass()).thenReturn(Model1.class);
    Mockito.when(mBuilder1.getId()).thenReturn(FIRST_BUILDER_ID);
    Mockito.when(mBuilder1.build(Matchers.any(ViewGroup.class))).thenReturn(holder1);

    mBuilder2 = Mockito.mock(Builder.class);
    Mockito.when(mBuilder2.getHolderClass()).thenReturn(Model2.class);
    Mockito.when(mBuilder2.getId()).thenReturn(SECOND_BUILDER_ID);
    Mockito.when(mBuilder2.build(Matchers.any(ViewGroup.class))).thenReturn(holder2);
  }

  @Test public void addViewModelWrapper() {
    UniversalAdapter adapter = new UniversalAdapter(mBuilder1, mBuilder2);
    Assert.assertTrue(adapter.add(new ViewModelWrapper<>(new Model1(), FIRST_BUILDER_ID)));
    Assert.assertFalse(adapter.add(null));
  }

  @Test public void onBindViewHolder() {
    UniversalAdapter adapter = Mockito.spy(new UniversalAdapter(mBuilder1, mBuilder2));
    Model1 one = new Model1();
    adapter.add(one);

    DefaultViewHolder holder = Mockito.mock(DefaultViewHolder.class);
    adapter.onBindViewHolder(holder, 0);

    Mockito.verify(holder).bind(one);
  }

  @Test public void removeItems() throws Exception {
    UniversalAdapter adapter = new UniversalAdapter(mBuilder1, mBuilder2);

    Model1 one = new Model1();
    Model2 four = new Model2();

    List objects = Arrays.asList(one, new Model2(), new Model2(), new Model1(), four, new Model1(),
        new Model1());

    adapter.addAll(objects);

    adapter.removeItems(0, 3);

    Assert.assertEquals(adapter.getItemCount(), 3);
    Assert.assertEquals(adapter.getItem(0), four);
  }

  @Test public void addNullObject() {
    UniversalAdapter adapter = new UniversalAdapter(mBuilder1, mBuilder2);
    adapter.add(0, null, FIRST_BUILDER_ID);
    Assert.assertEquals(adapter.getItemCount(), 0);
  }

  @Test(expected = WrongItemException.class) public void addWrongBuilder() {
    UniversalAdapter adapter = new UniversalAdapter(mBuilder1, mBuilder2);
    adapter.add(0, new Model1(), SECOND_BUILDER_ID);
  }

  @Test public void indexOf() throws Exception {
    UniversalAdapter adapter = new UniversalAdapter(mBuilder1, mBuilder2);

    List objects =
        Arrays.asList(new Model1(), new Model2(), new Model2(), new Model1(), new Model2(),
            new Model1(), new Model1());

    Assert.assertEquals(-1, adapter.indexOf(objects.get(0)));

    adapter.addAll(objects);

    for (Object item : objects) {
      Assert.assertEquals(objects.indexOf(item), adapter.indexOf(item));
    }
  }

  @Test public void getItemViewType() {
    UniversalAdapter adapter = new UniversalAdapter(mBuilder1, mBuilder2);
    List objects =
        Arrays.asList(new Model1(), new Model2(), new Model2(), new Model1(), new Model2(),
            new Model1(), new Model1());
    adapter.addAll(objects);
    for (Object model : objects) {
      int index = objects.indexOf(model);
      Builder builder = model instanceof Model1 ? mBuilder1 : mBuilder2;
      Assert.assertEquals(builder.getId(), adapter.getItemViewType(index));
    }
    Assert.assertNotEquals(mBuilder2.getId(), adapter.getItemViewType(3));
  }

  @Test public void getItem() {
    UniversalAdapter adapter = new UniversalAdapter(mBuilder1, mBuilder2);
    List objects =
        Arrays.asList(new Model1(), new Model2(), new Model2(), new Model1(), new Model2(),
            new Model1(), new Model1());

    adapter.addAll(objects);
    for (Object model : objects) {
      int index = objects.indexOf(model);
      Assert.assertEquals(model, adapter.getItem(index));
    }
  }

  @Test(expected = WrongItemException.class) public void addWrongItem() {
    UniversalAdapter adapter = new UniversalAdapter(mBuilder1, mBuilder2);
    adapter.add(POS_FIRST, new Model1(), SECOND_BUILDER_ID);
  }

  @Test(expected = WrongItemException.class) public void addWrongBuilderId() {
    UniversalAdapter adapter = new UniversalAdapter(mBuilder1);
    adapter.add(POS_FIRST, new Model1(), SECOND_BUILDER_ID);
  }

  @Test public void add() {
    UniversalAdapter adapter = new UniversalAdapter(mBuilder2);
    adapter.add(POS_FIRST, new Model2(), SECOND_BUILDER_ID);

    Assert.assertEquals(adapter.getItemCount(), 1);
  }

  @Test(expected = WrongItemException.class) public void addNoHolders() {
    UniversalAdapter adapter = new UniversalAdapter();
    adapter.add(new Model1());
  }

  @Test public void clear() throws Exception {

    UniversalAdapter adapter = new UniversalAdapter(mBuilder1, mBuilder2);
    Object obj1 = new Model1();

    Assert.assertEquals(EMPTY_COUNT, adapter.getItemCount());
    Assert.assertFalse(adapter.clear());
    Assert.assertEquals(EMPTY_COUNT, adapter.getItemCount());

    Assert.assertTrue(adapter.add(obj1));
    Assert.assertEquals(ONE_COUNT, adapter.getItemCount());
    Assert.assertTrue(adapter.clear());
    Assert.assertEquals(EMPTY_COUNT, adapter.getItemCount());
  }

  @Test public void onCreateViewHolder() throws Exception {
    UniversalAdapter adapter = new UniversalAdapter(Arrays.asList(mBuilder1, mBuilder2));
    ViewGroup parent = Mockito.mock(ViewGroup.class);
    DefaultViewHolder viewHolder = adapter.onCreateViewHolder(parent, mBuilder1.getId());
    Assert.assertThat(viewHolder.getItemViewType(), Is.is(mBuilder1.getId()));
  }

  @Test public void getItemCount() throws Exception {
    UniversalAdapter adapter = new UniversalAdapter(Arrays.asList(mBuilder1, mBuilder2));

    Object obj1 = new Model1();
    Object obj2 = new Model1();
    Object obj3 = new Model2();

    Assert.assertEquals(EMPTY_COUNT, adapter.getItemCount());

    Assert.assertTrue(adapter.add(obj1));
    Assert.assertEquals(ONE_COUNT, adapter.getItemCount());

    Assert.assertTrue(adapter.add(obj2));
    Assert.assertEquals(2, adapter.getItemCount());

    Assert.assertTrue(adapter.add(obj3));
    Assert.assertEquals(3, adapter.getItemCount());
  }

  @Test public void getItemCountWithClass() throws Exception {
    UniversalAdapter adapter = new UniversalAdapter(Arrays.asList(mBuilder1, mBuilder2));

    Object obj1 = new Model1();
    Object obj2 = new Model1();
    Object obj3 = new Model2();

    Assert.assertEquals(EMPTY_COUNT, adapter.getItemCount(Model1.class));

    Assert.assertTrue(adapter.add(obj1));
    Assert.assertEquals(ONE_COUNT, adapter.getItemCount(Model1.class));

    Assert.assertTrue(adapter.add(obj2));
    Assert.assertEquals(2, adapter.getItemCount(Model1.class));

    Assert.assertTrue(adapter.add(obj3));
    Assert.assertEquals(2, adapter.getItemCount(Model1.class));
    Assert.assertEquals(ONE_COUNT, adapter.getItemCount(Model2.class));
  }

  class Model1 { /* no-op */
  }

  class Model2 { /* no-op */
  }
}