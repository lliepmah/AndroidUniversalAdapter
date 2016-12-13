package ru.lliepmah.lib;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by Arthur Korchagin on 13.12.16
 */

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.DEFAULT_MANIFEST)
public class UniversalAdapterTest {

    private Builder mBuilder1;
    private Builder mBuilder2;

    @Before
    public void setUp() throws Exception {

        mBuilder1 = mock(Builder.class);
        when(mBuilder1.getHolderClass()).thenReturn(Model1.class);
        when(mBuilder1.getId()).thenReturn(1);

        mBuilder2 = mock(Builder.class);
        when(mBuilder2.getHolderClass()).thenReturn(Model2.class);
        when(mBuilder2.getId()).thenReturn(2);

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
    public void getItemCount() throws Exception {
        UniversalAdapter adapter = new UniversalAdapter(mBuilder1, mBuilder2);

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

    class Model1 { /* no-op */
    }

    class Model2 { /* no-op */
    }

}