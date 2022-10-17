package com.thingsgod.entity;

/**
 * @desc 定义书签
 */
public class Bookmark {
    //书签id
    Integer id;
    //页码
    Integer page;
    //标题
    String title;
    //层级
    Integer level;
    //父id
    Integer pid;

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public Integer getLevel() {
        return level;
    }
    public void setLevel(Integer level) {
        this.level = level;
    }
    public Integer getPid() {
        return pid;
    }
    public void setPid(Integer pid) {
        this.pid = pid;
    }
    public Integer getPage() {
        return page;
    }
    public void setPage(Integer page) {
        this.page = page;
    }

    @Override
    public String toString() {
        return "BookTemp{" +
                "id=" + id +
                ", page=" + page +
                ", title='" + title + '\'' +
                ", level=" + level +
                ", pid=" + pid +
                '}';
    }
}
