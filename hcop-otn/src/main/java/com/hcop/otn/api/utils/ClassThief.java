package com.hcop.otn.api.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class ClassThief {

    static public void setFinalStatic(String clsName, String fieldName, Object newValue) throws IllegalAccessException, NoSuchFieldException, ClassNotFoundException {

        Class cls = Class.forName(clsName);
        Field field = cls.getDeclaredField(fieldName);

        field.setAccessible(true);

        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);

        field.set(null, newValue);

    }
}
