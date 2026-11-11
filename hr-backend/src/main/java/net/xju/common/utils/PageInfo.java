package net.xju.common.utils;

import java.util.List;

public class PageInfo<T> {
    private int pageNo;
    private int pageSize;
    private int totalPage;
    private int count;
    private List<T> records;

    private int current;
    private int next;
    private int pre;
    private boolean hasNext;
    private boolean hasPre;
    private boolean last;
    private boolean first;



    public PageInfo(int pageNo, int pageSize, int count, List<T> records) {
        this.pageNo = pageNo;
        this.pageSize = pageSize;
        this.count = count;
        this.records = records;
        totalPage = (count/pageSize) + (count%pageSize==0?0:1);

        this.current =pageNo;
        this.hasNext = (pageNo!=totalPage);
        this.hasPre = (pageNo!=1);

        this.next = this.current + (hasNext?1:0);
        this.pre = this.current - (hasPre?1:0);
        this.last = !hasNext;
        this.first = !hasPre;
    }

    public int getPageNo() {
        return pageNo;
    }

    public int getPageSize() {
        return pageSize;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public int getCount() {
        return count;
    }

    public List<T> getRecords() {
        return records;
    }

    public int getCurrent() {
        return current;
    }

    public int getNext() {
        return next;
    }

    public int getPre() {
        return pre;
    }

    public boolean isHasNext() {
        return hasNext;
    }

    public boolean isHasPre() {
        return hasPre;
    }

    public boolean isLast() {
        return last;
    }

    public boolean isFirst() {
        return first;
    }
}
