package com.projectg.geyserupdater.velocity.logger;

import com.projectg.geyserupdater.common.logger.UpdaterLogger;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;
import org.slf4j.Logger;

public final class Slf4jUpdaterLogger implements UpdaterLogger {

    private final Logger logger;

    public Slf4jUpdaterLogger(Logger logger) {
        this.logger = logger;
        UpdaterLogger.setLogger(this);
    }

    @Override
    public void error(String message) {
        logger.error(message);
    }

    @Override
    public void warn(String message) {
        logger.warn(message);
    }

    @Override
    public void info(String message) {
        logger.info(message);
    }

    @Override
    public void debug(String message) {
        logger.debug(message);
    }

    @Override
    public void trace(String message) {
        logger.trace(message);
    }

    @Override
    public void enableDebug() {
        if (!logger.isDebugEnabled()) {
            Configurator.setLevel(logger.getName(), Level.DEBUG);
        }
    }

    @Override
    public void disableDebug() {
        if (logger.isDebugEnabled()) {
            Configurator.setLevel(logger.getName(), Level.INFO);
        }
    }

    @Override
    public boolean isDebug() {
        return logger.isDebugEnabled();
    }
}
