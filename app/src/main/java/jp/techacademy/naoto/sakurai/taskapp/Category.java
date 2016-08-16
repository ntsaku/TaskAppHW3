package jp.techacademy.naoto.sakurai.taskapp;

import java.io.Serializable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by NAOSAN on 2016/08/14.
 */

public class Category extends RealmObject implements Serializable {
    // id をプライマリーキーとして設定
    @PrimaryKey
    private int id;
    private String category_name;  // カテゴリー名

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCategory_name() { return category_name;}

    public void setCategory_name(String category_name) {this.category_name = category_name;}

}
