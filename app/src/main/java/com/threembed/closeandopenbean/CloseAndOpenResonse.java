package com.threembed.closeandopenbean;

import com.threembed.homebean.PendingBillsHelper;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by embed on 2/6/15.
 */
public class CloseAndOpenResonse implements Serializable
{


    String Message;
    String errNum;
    String errFlag;
    String alreadyAMemberText;

    ArrayList<DataInCloseOpen> Data;
    ArrayList<PendingBillsHelper> pending;


    public String getAlreadyAMemberText() {
        return alreadyAMemberText;
    }

    public String getErrNum() {
        return errNum;
    }

    public void setErrNum(String errNum) {
        this.errNum = errNum;
    }

    public String getErrFlag() {
        return errFlag;
    }

    public void setErrFlag(String errFlag) {
        this.errFlag = errFlag;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public ArrayList<DataInCloseOpen> getData() {
        return Data;
    }

    public void setData(ArrayList<DataInCloseOpen> data) {
        Data = data;
    }

    public ArrayList<PendingBillsHelper> getPending() {
        return pending;
    }
}
