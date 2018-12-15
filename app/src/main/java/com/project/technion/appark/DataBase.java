package com.project.technion.appark;

import com.project.technion.appark.exceptions.ParkingSpotNotInSystem;

import java.util.List;

public interface DataBase {

    User getUser(Integer id);

    int getNextParkingSpotID();

    DummyParkingSpot getParkingSpot(Integer id) throws ParkingSpotNotInSystem;

    void add(User b);

    void add(DummyParkingSpot s);

    void removeUser(Integer id);

    void removeParkingSpot(Integer id);

    void update(User updatedUser);

    void update(DummyParkingSpot s) throws ParkingSpotNotInSystem;

    List<Integer> getParkingSpotsOfUser(Integer userId);

    void add(Integer userId, Integer hisParkingSpot);

    List<DummyParkingSpot> getAllParkingSpot();

    List<User> getAllUsers();
}
