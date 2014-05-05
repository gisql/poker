/*
 * @(#) TableListener.java
 *
 * Copyright 2014 the poker project.
 */

package poker;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Listener being notified about table events.
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

    void tableChanged(final TableEvent event);
}
