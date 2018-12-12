package com.project.technion.appark;

import com.project.technion.appark.exceptions.ParkingSpotNotInSystem;

import java.util.List;

public interface DataBase {

    User getUser(Integer id);

    ParkingSpot getParkingSpot(Integer id) throws ParkingSpotNotInSystem;

    void add(User b);

    void add(ParkingSpot s);

    void removeUser(Integer id);

    void removeParkingSpot(Integer id);

    void update(User updatedUser);

    void update(ParkingSpot s) throws ParkingSpotNotInSystem;

    List<ParkingSpot> getAllParkingSpot();

    List<User> getAllUsers();
}
