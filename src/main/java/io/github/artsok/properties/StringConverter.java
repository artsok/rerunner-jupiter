package io.github.artsok.properties;

import org.aeonbits.owner.Converter;

import java.lang.reflect.Method;

public class StringConverter implements Converter {

    @Override
    public String convert(Method method, String text) {
        return text.replaceAll(".class", "");
    }
}
