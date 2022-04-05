package shandiankulishe.kleebot.services;

import shandiankulishe.kleebot.log.Logger;

public abstract class Service {
    protected Logger logger=Logger.getLogger(this.getClass());
    public String getServiceName(){
        return this.getClass().getName();
    }
    public void initialize(){
        logger.info(this.getClass().getName()+" initialized by default");
    }
    public void stop(){
        logger.info(this.getClass().getName()+" stopped by default");
    }
}
