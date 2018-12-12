package com.project.technion.appark;

public class User {
    private Integer id;
    private String name;
    private String contact_info;
    private DataBase db;

    public User(Integer id, String name, String contact_info, DataBase db) {
        this.id = id;
        this.name = name;
        this.contact_info = contact_info;
        this.db = db;
    }

    public Integer getId() {
        return id;
    }

     public String getName() {
        return name;
    }

    public String getContactInfo() {
        return contact_info;
    }

    public void update(User updatedUser) {
        db.update(updatedUser);
        this.id = updatedUser.getId();
        this.name = updatedUser.getName();
        this.contact_info = updatedUser.getContactInfo();
    }

    public String toString() {
        return "UserImplementation [id=" + id + ", name=" + name + ", contact_info=" + contact_info + ", db=" + db + "]";
    }
}
