package com.lliepmah.common;

import android.support.annotation.NonNull;

import com.google.common.collect.ImmutableList;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.ParameterSpec;

import java.util.List;

import javax.lang.model.element.VariableElement;

/**
 * Created by Arthur Korchagin on 26.10.16
 */

public class SpecUtils {

    private static final char FIELD_PREFIX = '_';

    public static ImmutableList<ParameterSpec> buildParameters(List<? extends VariableElement> elements) {
        ImmutableList.Builder<ParameterSpec> builder = ImmutableList.builder();
        for (VariableElement variableElement : elements) {
            builder.add(buildParameter(variableElement));
        }
        return builder.build();
    }

    public static ImmutableList<FieldSpec> buildFields(List<? extends VariableElement> elements) {
        ImmutableList.Builder<FieldSpec> builder = ImmutableList.builder();
        for (VariableElement variableElement : elements) {
            builder.add(buildField(variableElement));
        }
        return builder.build();
    }

    @NonNull
    public static FieldSpec buildField(VariableElement element) {
        return FieldSpec.builder(ClassName.get(element.asType()), FIELD_PREFIX + String.valueOf(element.getSimpleName())).build();
    }

    @NonNull
    public static ParameterSpec buildParameter(VariableElement element) {
        return ParameterSpec.builder(ClassName.get(element.asType()), String.valueOf(element.getSimpleName())).build();
    }
}
