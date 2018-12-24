package com.baizhi.entity;

import java.io.Serializable;

/**
 * 诗歌表
 *实现序列化
 */
public class Poetries implements Serializable {
    private Integer id;
    //内容
    private String content;
    //标题
    private String title;
    //父类id
    private Poets poetId;

    @Override
    public String toString() {
        return "Poetries{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", title='" + title + '\'' +
                ", poetId=" + poetId +
                '}';
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Poets getPoetId() {
        return poetId;
    }

    public void setPoetId(Poets poetId) {
        this.poetId = poetId;
    }

    public Poetries(Integer id, String content, String title, Poets poetId) {
        this.id = id;
        this.content = content;
        this.title = title;
        this.poetId = poetId;
    }

    public Poetries() {
    }
}
