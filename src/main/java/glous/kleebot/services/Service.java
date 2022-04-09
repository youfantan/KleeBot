package glous.kleebot.services;

import glous.kleebot.log.Logger;

public abstract class Service {
    private static final int RULE_START_WITH=0;
    private static final int RULE_CONTAINS=1;
    private static final int RULE_END_WITH=2;
    private static final int RULE_REGEXP=-1;
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
    private void registerTriggerRules(int RULE,String matchStr){
    }
}
