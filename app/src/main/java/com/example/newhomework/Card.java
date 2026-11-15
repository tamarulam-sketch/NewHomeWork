package com.example.newhomework;

import com.google.firebase.database.Exclude;

public class Card {

    // ארבעת השדות שנשמרים בענן
    private String name;
    private String job;
    private String phone;
    private String site;

    // המפתח הוא כתובת הכרטיס בעץ Firebase ולא שדה שנשמר
    private String key;

    // Firebase חייב בנאי ריק כדי ליצור אובייקט בזמן קריאה מהמסד
    public Card() {
    }

    // בנאי ליצירת כרטיס חדש מהטופס
    public Card(String name, String job, String phone, String site) {
        this.name = name;
        this.job = job;
        this.phone = phone;
        this.site = site;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    @Exclude
    public String getKey() {
        return key;
    }

    @Exclude
    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return name + " · " + job;
    }
}

