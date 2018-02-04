package io.github.artsok.properties;

import org.aeonbits.owner.Config;

/**
 * Config for Owner Framework
 *
 * @author Artem Sokovets
 */
public interface ReRunConfig implements Config {

    @Key("rerun.enable")
    boolean enable();

    @Key("rerun.minSuccess")
    int minSucces();

    @Key("rerun.totalRepeats")
    int totalRepeats();


    //List<Exception>
}
