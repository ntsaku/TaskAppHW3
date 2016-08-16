package jp.techacademy.naoto.sakurai.taskapp;

import java.io.Serializable;
import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Task extends RealmObject implements Serializable {
    private String title; // タイトル
    private String contents; // 内容
    private Date date; // 日時
    private int category_id; // カテゴリーID   Categoryクラスの id
    private String category_name; // カテゴリー名　Categoryクラスの category_name

    // id をプライマリーキーとして設定
    @PrimaryKey
    private int id;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCategory_id() {return category_id;}

    public void setCategory_id(int category_id) {this.category_id = category_id;}

    public String getCategory_name() {return category_name;}

    public void setCategory_name(String category_name) {this.category_name = category_name;}


}