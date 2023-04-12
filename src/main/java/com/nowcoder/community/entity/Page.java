package com.nowcoder.community.entity;

public class Page {
    private int currentPageId = 1;  //  当前页索引，1开始
    private int limit = 10;         //  每页显示帖子数
    private int totalRows = 0;      //  总贴子数
    private String path;            //  访问路径

    public int getCurrentPageId() {
        return currentPageId;
    }

    public void setCurrentPageId(int currentPageId) {
        if (currentPageId < 1) {
            return;
        }
        this.currentPageId = currentPageId;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        if (limit < 1 || limit > 100) {
            return;
        }
        this.limit = limit;
    }

    public int getTotalRows() {
        return totalRows;
    }

    public void setTotalRows(int totalRows) {
        if (totalRows < 0) {
            return;
        }
        this.totalRows = totalRows;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    /**
     * 获取当前页的帖子的起始行索引
     * @return
     */
    public int getOffset() {
        int totalPages = getTotalPages();
        if (currentPageId > totalPages) {
            setCurrentPageId(totalPages);
        }
        return (currentPageId - 1) * limit;
    }

    /**
     * 获取总的分页数
     * @return
     */
    public int getTotalPages() {
        if (totalRows == 0) {
            return 1;
        }
        return (int)Math.ceil((double) totalRows / limit);
    }

    /**
     * 获取起始分页号
     * @return
     */
    public int getFrom() {
        return currentPageId > 2 ? currentPageId - 2 : 1;
    }

    /**
     * 获取结束分页号
     * @return
     */
    public int getTo() {
        int totalPages = getTotalPages();
        return currentPageId > 2 ? Math.min(currentPageId + 2, totalPages) : Math.min(5, totalPages);
    }
}
