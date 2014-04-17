/*
 * Copyright
 */
package pl.gsiql.poker;

import java.util.concurrent.atomic.AtomicReference;

/**
 * TODO describe me!
 *
 * @version created on 2014-04-17, 11:05
 */
public interface TableListener {
    AtomicReference<ConfigDTO> config = new AtomicReference<>();

    default void configChanged(final ConfigDTO cft) {
        config.set(cft);
    }

    default ConfigDTO currentConfig() {
        return config.get();
    }

    void event(final TableAction action);
}
