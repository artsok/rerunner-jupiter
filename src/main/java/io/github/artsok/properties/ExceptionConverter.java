package io.github.artsok.properties;

import lombok.extern.slf4j.Slf4j;
import org.aeonbits.owner.Converter;

import java.lang.reflect.Method;


/**
 * Convert from String Name Exception to Class Exception
 */
@Slf4j
public class ExceptionConverter implements Converter<Class> {

    /**
     * Implemented mechanism of data conversion
     * @param method - information about method {@link ReRunConfig} which call this converter
     * @param valueFromProperty - value from property file
     * @return {@link Class}
     */
    @SuppressWarnings("unchecked")
    @Override
    public Class convert(Method method, String valueFromProperty) {
        Class<? extends Exception> exceptionClass;
        log.debug("Get value '{}'  from property file 'rerun.properties'", valueFromProperty);
        try {
            log.debug("Start to loading {}.class", valueFromProperty);
            exceptionClass = (Class<? extends Exception>) Class.forName(valueFromProperty);
            log.debug("Class loaded");
        } catch (ClassNotFoundException exception) {
            log.error("Error to loading {}.class", valueFromProperty);
            log.warn("Start to loading java.lang.Exception.class by default");
            try {
                exceptionClass = (Class<? extends Exception>) Class.forName("java.lang.Exception");
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(exception);
            }
        }
        return exceptionClass;
    }
}
