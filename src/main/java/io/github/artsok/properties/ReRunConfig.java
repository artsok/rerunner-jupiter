package io.github.artsok.properties;

import org.aeonbits.owner.Config;

import java.util.List;

/**
 * Config for Owner Framework
 *
 * @author Artem Sokovets
 */
public interface ReRunConfig extends Config {

    @Key("rerun.enable")
    @DefaultValue("true")
    boolean enable();

    @Key("rerun.minSuccess")
    @DefaultValue("1")
    int minSuccess();

    @Key("rerun.totalRepeats")
    @DefaultValue("1")
    int totalRepeats();

    @Key("rerun.exceptionPool")
    @DefaultValue("Exception.class")
    List<Exception> exceptionPool();
}
