package com.project.technion.appark;

public class User {
    private String name;
    private String contact_info;


    public User( String name, String contact_info) {
        this.name = name;
        this.contact_info = contact_info;
    }

     public String getName() {
        return name;
    }

    public String getContactInfo() {
        return contact_info;
    }

    public void update(User updatedUser) {
        this.name = updatedUser.getName();
        this.contact_info = updatedUser.getContactInfo();
    }

    public String toString() {
        return "UserImplementation [ name=" + name + ", contact_info=" + contact_info + "]";
    }
}
