package com.project.technion.appark;

public class User {
    private Integer id;
    private String name;
    private String contact_info;


    public User(Integer id, String name, String contact_info) {
        this.id = id;
        this.name = name;
        this.contact_info = contact_info;
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
        this.id = updatedUser.getId();
        this.name = updatedUser.getName();
        this.contact_info = updatedUser.getContactInfo();
    }

    public String toString() {
        return "UserImplementation [id=" + id + ", name=" + name + ", contact_info=" + contact_info + "]";
    }
}
