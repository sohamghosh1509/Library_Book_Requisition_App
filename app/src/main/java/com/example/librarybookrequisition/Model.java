package com.example.librarybookrequisition;

public class Model {
    String bookLocation,bookName,booksCount,category, pushKey, userId, notification;
    String name,phone,dept;
    public Model()
    {

    }
    public Model(String bookLocation,String bookName,String booksCount,String category,String studentName,String phone,String dept,
                 String userId, String pushKey, String notification)
    {
        this.bookLocation = bookLocation;
        this.bookName = bookName;
        this.booksCount = booksCount;
        this.category = category;
        this.name = studentName;
        this.phone = phone;
        this.dept = dept;
        this.pushKey = pushKey;
        this.userId = userId;
        this.notification = notification;
    }

    public void setBookLocation(String bookLocation) {
        this.bookLocation = bookLocation;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public void setBooksCount(String booksCount) {
        this.booksCount = booksCount;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setStudentName(String studentName) {
        this.name = name;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setDept(String dept) {
        this.dept = dept;
    }

    public String getBookLocation() {
        return bookLocation;
    }

    public String getBookName() {
        return bookName;
    }

    public String getBooksCount() {
        return booksCount;
    }

    public String getCategory() {
        return category;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getDept() {
        return dept;
    }

    public void setPushKey(String pushKey) { this.pushKey = pushKey; }

     public String getPushKey() { return pushKey; }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public String getNotification() {
        return notification;
    }

    public void setNotification(String notification) {
        this.notification = notification;
    }
}