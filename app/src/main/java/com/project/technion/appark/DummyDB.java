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
        User user1 = new User(1, "Guy", "guy@gmail.com");
        User user2 = new User(2, "Addir", "addir@gmail.com");
        User user3 = new User(3, "Tal", "tal@gmail.com");
        users.put(1, user1);
        users.put(2, user2);
        users.put(3, user3);
    }

    private Map<Integer, User> users = new HashMap<>();
    Map<Integer, ParkingSpot> parkingSpots = new HashMap<>();
    Map<Integer, List<Integer>> parkingSpotsOfUsers = new HashMap<>();
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
    public ParkingSpot getParkingSpot(Integer id) throws ParkingSpotNotInSystem {
        if (!parkingSpots.containsKey(id)) throw new ParkingSpotNotInSystem();
        return parkingSpots.get(id);
    }

    @Override
    public void add(User b) {
        users.put(b.getId(), b);
    }

    @Override
    public void add(ParkingSpot s) {
        parkingSpots.put(s.getId(), s);
        add(s.getUser().getId(),s.getId());
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
        users.put(s.getId(), s);
    }

    @Override
    public void update(ParkingSpot s) throws ParkingSpotNotInSystem {
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
    public List<ParkingSpot> getAllParkingSpot() {
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
