package com.project.technion.appark;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

public class User {
    private String name;
    private String contactInfo;
    public List<ParkingSpot> parkingSpots;
    public List<Reservation> reservations;

    public User() {
        parkingSpots = new ArrayList<>();
        reservations = new ArrayList<>();
    }

    public User(String name, String contactInfo) {
        this.name = name;
        this.contactInfo = contactInfo;
        parkingSpots = new ArrayList<>();
        reservations = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public void update(User updatedUser) {
        this.name = updatedUser.getName();
        this.contactInfo = updatedUser.getContactInfo();
    }

    public String toString() {
        return "UserImplementation [ name=" + name + ", contactInfo=" + contactInfo + "]";
    }

    public void removeOfferById(String offerId) {
        for (ParkingSpot p : parkingSpots) {
            if (p.offers.contains(offerId)) {
                p.offers.remove(offerId);
                return;
            }
        }
    }

    public void removeReservationById(String reservationId) {
        reservations = reservations.stream()
                .filter(r -> !(r.id.equals(reservationId)))
                .collect(Collectors.toList());
    }

    public boolean addOffer(Offer offer) {
        for(ParkingSpot ps : parkingSpots){
            if (ps.id.equals(offer.parkingSpotId)){
                ps.offers.add(offer.id);
                return true;
            }
        }
        return false;
    }
}
