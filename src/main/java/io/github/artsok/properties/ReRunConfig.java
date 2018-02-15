package io.github.artsok.properties;

import org.aeonbits.owner.Config;
import org.aeonbits.owner.Config.Sources;

import java.util.List;

/**
 * Config for Owner Framework
 *
 * @author Artem Sokovets
 */
@Sources({"file:${user.dir}/rerun.properties" })
public interface ReRunConfig extends Config {

    @DefaultValue("true")
    @Key("rerun.enable")
    boolean enable();

    @Key("rerun.minSuccess")
    @DefaultValue("1")
    int minSuccess();

    @Key("rerun.totalRepeats")
    @DefaultValue("1")
    int totalRepeats();

    @Key("rerun.exceptionClasses")
    @DefaultValue("java.lang.Exception")
    @ConverterClass(ExceptionConverter.class)
    List<Class<? extends Exception>> exceptionPool();
}

