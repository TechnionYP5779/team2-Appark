package com.project.technion.appark;

import com.project.technion.appark.exceptions.ParkingSpotNotInSystem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DummyDB implements DataBase {
    private static final DummyDB ourInstance = new DummyDB();
    private static int psCounter = 0;

    public static DummyDB getInstance() {
        return ourInstance;
    }

    private DummyDB() {
    }

    private Map<Integer, User> users = new HashMap<>();
    private Map<Integer, DummyParkingSpot> parkingSpots = new HashMap<>();
    private Map<Integer, List<Integer>> parkingSpotsOfUsers = new HashMap<>();
//    List<Reservation> reservations = new ArrayList<>();

    @Override
    public User getUser(Integer id) {
        return users.get(id);
    }

    @Override
    public int getNextParkingSpotID() {
        return psCounter++;
    }

    @Override
    public DummyParkingSpot getParkingSpot(Integer id) throws ParkingSpotNotInSystem {
        if (!parkingSpots.containsKey(id)) throw new ParkingSpotNotInSystem();
        return parkingSpots.get(id);
    }

    @Override
    public void add(User b) {
        users.put(0, b);
    }

    @Override
    public void add(DummyParkingSpot s) {
        parkingSpots.put(s.getId(), s);
        add(0,s.getId());
    }

    @Override
    public void removeUser(Integer id) {
        users.remove(id);
    }

    @Override
    public void removeParkingSpot(Integer id) {
        parkingSpots.remove(id);
    }

    @Override
    public void update(User s) {
        users.put(0, s);
    }

    @Override
    public void update(DummyParkingSpot s) throws ParkingSpotNotInSystem {
        if (!parkingSpots.containsKey(s.getId())) throw new ParkingSpotNotInSystem();
        parkingSpots.put(s.getId(), s);
    }

    @Override
    public List<Integer> getParkingSpotsOfUser(Integer userId) {
        if (!parkingSpotsOfUsers.containsKey(userId)) return new ArrayList<>();
        return new ArrayList<>(parkingSpotsOfUsers.get(userId));
    }

    @Override
    public void add(Integer userId, Integer hisParkingSpot) {
        List<Integer> parkingSpotsIds = parkingSpotsOfUsers.get(userId);
        if (parkingSpotsIds == null) {
            parkingSpotsIds = new ArrayList<>();
            parkingSpotsOfUsers.put(userId, parkingSpotsIds);
        }

        if (!parkingSpotsIds.contains(hisParkingSpot)) {
            parkingSpotsIds.add(hisParkingSpot);
        }

    }

    @Override
    public List<DummyParkingSpot> getAllParkingSpot() {
        return new ArrayList<>(parkingSpots.values());
    }

//    @Override public List<Reservation> getAllReservations() {
//        return reservations;
//    }
//
//    @Override public void add(Reservation s) {
//        reservations.add(s);
//
//    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }
}
