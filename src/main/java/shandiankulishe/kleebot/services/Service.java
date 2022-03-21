package shandiankulishe.kleebot.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public interface Service {
    default String getServiceName(){
        return this.getClass().getName();
    }
    default void initialize(){
        Logger logger= LogManager.getLogger(this.getClass());
        logger.info(this.getClass().getName()+" initialized by default");
    }
    default void stop(){
        Logger logger= LogManager.getLogger(this.getClass());
        logger.info(this.getClass().getName()+" stopped by default");
    }
}
