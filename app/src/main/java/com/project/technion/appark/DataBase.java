package com.project.technion.appark;

import java.util.List;

public interface DataBase {

    User getUser(Integer id);

    void add(User b);

    void removeUser(Integer id);

    void update(User updatedUser);

    List<User> getAllUsers();
}
