package com.bt.metb.aggregator.messageprocessor;

import com.bt.metb.aggregator.constants.WMGConstant;

public enum RequestType {
    CREATE(WMGConstant.CREATE_ACTION), AMEND(WMGConstant.AMEND_ACTION),CANCEL (WMGConstant.CANCEL_ACTION), UPDATE_TASK_STATUS(WMGConstant.UPDATE_ACTION);
    private String text;

    RequestType(String initValue){

        text=initValue;
    }
    protected String getText(){

        return this.text;
    }
    protected static RequestType evaluate(String input){
        if (input != null){
            for ( RequestType reqType:RequestType.values()){
                if (input.equals(reqType.getText())) {
                    return reqType;
                }
            }
        }
        return null;
    }
}
