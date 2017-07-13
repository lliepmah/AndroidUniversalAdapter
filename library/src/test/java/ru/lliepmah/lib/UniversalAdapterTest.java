package ru.lliepmah.lib;

import android.view.ViewGroup;

import org.hamcrest.core.Is;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.Arrays;
import java.util.List;
import ru.lliepmah.lib.exceptions.WrongItemException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by Arthur Korchagin on 13.12.16
 */

@RunWith(RobolectricTestRunner.class)
//@Config(constants = BuildConfig.class, manifest = "./src/main/AndroidManifest.xml")
@Config(sdk = 21)
public class UniversalAdapterTest {

    public static final int FIRST_BUILDER_ID = 1;
    public static final int SECOND_BUILDER_ID = 2;
    public static final int FIRST_VIEW_TYPE = 1;
    public static final int SECOND_VIEW_TYPE = 2;

    private Builder mBuilder1;
    private Builder mBuilder2;

    @Before public void setUp() throws Exception {
        DefaultViewHolder holder1 = mock(DefaultViewHolder.class);
        when(holder1.getItemViewType()).thenReturn(FIRST_VIEW_TYPE);

        DefaultViewHolder holder2 = mock(DefaultViewHolder.class);
        when(holder2.getItemViewType()).thenReturn(SECOND_VIEW_TYPE);

        mBuilder1 = mock(Builder.class);
        when(mBuilder1.getHolderClass()).thenReturn(Model1.class);
        when(mBuilder1.getId()).thenReturn(FIRST_BUILDER_ID);
        when(mBuilder1.build(any(ViewGroup.class))).thenReturn(holder1);

        mBuilder2 = mock(Builder.class);
        when(mBuilder2.getHolderClass()).thenReturn(Model2.class);
        when(mBuilder2.getId()).thenReturn(SECOND_BUILDER_ID);
        when(mBuilder2.build(any(ViewGroup.class))).thenReturn(holder2);
    }

    //public void removeItems(int startIndex, int endIndex) {
    //    for (int i = startIndex; i <= endIndex; i++) {
    //        mItems.remove(startIndex);
    //    }
    //}


    @Test
    public void removeItems() throws Exception {
        UniversalAdapter adapter = new UniversalAdapter(mBuilder1, mBuilder2);

        Model1 one = new Model1();
        Model2 four = new Model2();

        List objects = Arrays.asList(one,
            new Model2(),
            new Model2(),
            new Model1(), four,
            new Model1(),
            new Model1());

        adapter.addAll(objects);

        adapter.removeItems(0, 3);

        assertEquals(adapter.getItemCount(), 3);
        assertEquals(adapter.getItem(0), four);
    }


    @Test public void addNullObject() {
        UniversalAdapter adapter = new UniversalAdapter(mBuilder1, mBuilder2);
        adapter.add(0, null, FIRST_BUILDER_ID);
        assertEquals(adapter.getItemCount(), 0);
    }

    @Test(expected = WrongItemException.class) public void addWrongBuilder() {
        UniversalAdapter adapter = new UniversalAdapter(mBuilder1, mBuilder2);
        adapter.add(0, new Model1(), SECOND_BUILDER_ID);
    }

    @Test
    public void indexOf() throws Exception {
        UniversalAdapter adapter = new UniversalAdapter(mBuilder1, mBuilder2);

        List objects = Arrays.asList(
                new Model1(),
                new Model2(),
                new Model2(),
                new Model1(),
                new Model2(),
                new Model1(),
                new Model1());

        assertEquals(-1, adapter.indexOf(objects.get(0)));

        adapter.addAll(objects);

        for (Object item : objects) {
            assertEquals(objects.indexOf(item), adapter.indexOf(item));
        }

    }

    @Test
    public void getItemViewType() {
        UniversalAdapter adapter = new UniversalAdapter(mBuilder1, mBuilder2);
        List objects = Arrays.asList(
            new Model1(),
            new Model2(),
            new Model2(),
            new Model1(),
            new Model2(),
            new Model1(),
            new Model1()
        );
        adapter.addAll(objects);
        for (Object model : objects) {
            int index = objects.indexOf(model);
            Builder builder = model instanceof Model1 ? mBuilder1 : mBuilder2;
            assertEquals(builder.getId(), adapter.getItemViewType(index));
        }
        assertNotEquals(mBuilder2.getId(), adapter.getItemViewType(3));
    }

    @Test
    public void getItem() {
        UniversalAdapter adapter = new UniversalAdapter(mBuilder1, mBuilder2);
        List objects = Arrays.asList(
            new Model1(),
            new Model2(),
            new Model2(),
            new Model1(),
            new Model2(),
            new Model1(),
            new Model1()
        );

        adapter.addAll(objects);
        for (Object model : objects) {
            int index = objects.indexOf(model);
            assertEquals(model, adapter.getItem(index));
        }
    }

    @Test(expected = WrongItemException.class)
    public void addWrongItem() {
        UniversalAdapter adapter = new UniversalAdapter(mBuilder1, mBuilder2);
        adapter.add(0, new Model1(), SECOND_BUILDER_ID);
    }

    @Test(expected = WrongItemException.class)
    public void addWrongBuilderId() {
        UniversalAdapter adapter = new UniversalAdapter(mBuilder1);
        adapter.add(0, new Model1(), SECOND_BUILDER_ID);
    }

    @Test
    public void add() {
        UniversalAdapter adapter = new UniversalAdapter(mBuilder2);
        adapter.add(0, new Model2(), SECOND_BUILDER_ID);

        assertEquals(adapter.getItemCount(), 1);
    }

    @Test(expected = WrongItemException.class)
    public void addNoHolders() {
        UniversalAdapter adapter = new UniversalAdapter();
        adapter.add(new Model1());
    }

    @Test
    public void clear() throws Exception {

        UniversalAdapter adapter = new UniversalAdapter(mBuilder1, mBuilder2);
        Object obj1 = new Model1();

        assertEquals(0, adapter.getItemCount());
        assertFalse(adapter.clear());
        assertEquals(0, adapter.getItemCount());

        assertTrue(adapter.add(obj1));
        assertEquals(1, adapter.getItemCount());
        assertTrue(adapter.clear());
        assertEquals(0, adapter.getItemCount());
    }

    @Test
    public void onCreateViewHolder() throws Exception {
        UniversalAdapter adapter = new UniversalAdapter(Arrays.asList(mBuilder1, mBuilder2));
        ViewGroup parent = mock(ViewGroup.class);
        DefaultViewHolder viewHolder = adapter.onCreateViewHolder(parent, mBuilder1.getId());
        assertThat(viewHolder.getItemViewType(), Is.is(mBuilder1.getId()));
    }

    @Test
    public void getItemCount() throws Exception {
        UniversalAdapter adapter = new UniversalAdapter(Arrays.asList(mBuilder1, mBuilder2));

        Object obj1 = new Model1();
        Object obj2 = new Model1();
        Object obj3 = new Model2();

        assertEquals(0, adapter.getItemCount());

        assertTrue(adapter.add(obj1));
        assertEquals(1, adapter.getItemCount());

        assertTrue(adapter.add(obj2));
        assertEquals(2, adapter.getItemCount());

        assertTrue(adapter.add(obj3));
        assertEquals(3, adapter.getItemCount());
    }

    @Test
    public void getItemCount_WithClass() throws Exception {
        UniversalAdapter adapter = new UniversalAdapter(Arrays.asList(mBuilder1, mBuilder2));

        Object obj1 = new Model1();
        Object obj2 = new Model1();
        Object obj3 = new Model2();

        assertEquals(0, adapter.getItemCount(Model1.class));

        assertTrue(adapter.add(obj1));
        assertEquals(1, adapter.getItemCount(Model1.class));

        assertTrue(adapter.add(obj2));
        assertEquals(2, adapter.getItemCount(Model1.class));

        assertTrue(adapter.add(obj3));
        assertEquals(2, adapter.getItemCount(Model1.class));
        assertEquals(1, adapter.getItemCount(Model2.class));
    }

    class Model1 { /* no-op */
    }

    class Model2 { /* no-op */
    }

}