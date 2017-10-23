package com.ziac.model;

import java.util.ArrayList;
import java.util.List;

public class CodeDO {
    private String codeId;
    private String codeDescription;
    private String hash;
    private List<String> inputDataIdList = new ArrayList();
    private List<String> outPutDataIdList = new ArrayList<>();

    public String getCodeId() {
        return codeId;
    }

    public void setCodeId(String codeId) {
        this.codeId = codeId;
    }

    public String getCodeDescription() {
        return codeDescription;
    }

    public void setCodeDescription(String codeDescription) {
        this.codeDescription = codeDescription;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public List<String> getInputDataIdList() {
        return inputDataIdList;
    }

    public void setInputDataIdList(List<String> inputDataIdList) {
        this.inputDataIdList = inputDataIdList;
    }

    public List<String> getOutPutDataIdList() {
        return outPutDataIdList;
    }

    public void setOutPutDataIdList(List<String> outPutDataIdList) {
        this.outPutDataIdList = outPutDataIdList;
    }


}
