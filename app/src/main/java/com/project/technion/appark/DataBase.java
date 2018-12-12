package com.project.technion.appark;

import com.project.technion.appark.exceptions.ParkingSpotNotInSystem;

import java.util.List;

public interface DataBase {

    User getUser(Integer id);

    int getNextParkingSpotID();

    ParkingSpot getParkingSpot(Integer id) throws ParkingSpotNotInSystem;

    void add(User b);

    void add(ParkingSpot s);

    void removeUser(Integer id);

    void removeParkingSpot(Integer id);

    void update(User updatedUser);

    void update(ParkingSpot s) throws ParkingSpotNotInSystem;

    List<Integer> getParkingSpotsOfUser(Integer userId);

    void add(Integer userId, Integer hisParkingSpot);

    List<ParkingSpot> getAllParkingSpot();

    List<User> getAllUsers();
}
