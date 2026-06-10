package com.example.studentms.vo;

import java.util.List;

public class CursorPageResult<T>{
    private List<T>records;
    private Integer nextLastId;
    private Boolean hasNext;

    public CursorPageResult(){}

    public CursorPageResult(List<T> records, Integer nextLastId, Boolean hasNext) {
        this.records = records;
        this.nextLastId = nextLastId;
        this.hasNext = hasNext;
    }

    public List<T> getRecords() {
        return records;
    }

    public void setRecords(List<T> records) {
        this.records = records;
    }

    public Integer getNextLastId() {
        return nextLastId;
    }

    public void setNextLastId(Integer nextLastId) {
        this.nextLastId = nextLastId;
    }

    public Boolean getHasNext() {
        return hasNext;
    }

    public void setHasNext(Boolean hasNext) {
        this.hasNext = hasNext;
    }
}
