package io.github.artsok.properties;

import lombok.extern.slf4j.Slf4j;
import org.aeonbits.owner.Converter;

import java.lang.reflect.Method;

@Slf4j
public class ExceptionConverter implements Converter<Class> {

    @Override
    public Class convert(Method method, String text) {
        System.out.println(method);

        log.info("What we read from text " + text);
        Class<? extends Exception> exceptionClass = null;
        try {
            exceptionClass = (Class<? extends Exception>) Class.forName(text);
            log.info("Загрузили класс  {}", exceptionClass);

        } catch (ClassNotFoundException exception) {
            log.error("Exception to load 'rerun.exceptionClasses' from rerun.properties", exception);
            throw new RuntimeException(exception);
        }
        //log.info(" <FKF  " + exceptionClass.getTypeName());
        return exceptionClass;
    }

}
