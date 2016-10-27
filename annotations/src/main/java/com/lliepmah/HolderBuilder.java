package com.lliepmah;

import android.support.annotation.LayoutRes;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Arthur Korchagin on 25.10.16
 */

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface HolderBuilder {
    @LayoutRes
    int value();
}
