package com.example.notepad;

import android.widget.EditText;

public class ModelClass {


    public String id;
    public String title;
    public String content;
    public String img;
    public  String uid;

    public  ModelClass(){
    }

    public ModelClass(String id, String title, String content, String uid) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.uid = uid;
    }

    public ModelClass(String id, String title, String content, String img,String uid) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.img=img;
        this.uid = uid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
