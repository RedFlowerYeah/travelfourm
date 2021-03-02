package com.travelfourm.entity;

/**
 * 分页的信息管理*/
public class Page {
    //当前页面
    private int current = 1;

    //显示页面上限帖子数
    private int limit = 10;

    //显示共有多少帖子总数
    private int rows;

    //查询路径
    private String path;

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        if (current >= 1){
            this.current = current;
        }
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        if (limit >= 1 && limit <= 100) {
            this.limit=limit;
        }
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        if (rows >= 0) {
            this.rows = rows;
        }
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    /**
     * 获取当前页面起始页
     */
    public int getOffset(){
        // current * limit - limit
        return (current - 1) * limit ;
    }


    /**
     * 获取总页数*/
    public int getTotal(){
        // rows / limit +1 如果对帖子数量求余=0时，则为当前页；否则，则需要+1
        if (rows % limit ==0){
            return rows / limit;
        }else {
            return rows / limit +1;
        }
    }

    /**
     * 获取起始页码*/
    public int getFrom(){
        int from = current - 2;
        return from < 1 ? 1 : from;
    }

    /**
     * 获取结束页码*/
    public int getTo(){
        int to =current + 2;
        int total=getTotal();
        return to > total ? total : to;
    }
}
