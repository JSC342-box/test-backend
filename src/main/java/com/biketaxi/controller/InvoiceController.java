package com.biketaxi.controller;

import com.biketaxi.entity.Ride;
import com.biketaxi.repository.RideRepository;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
public class InvoiceController {
    private final RideRepository rideRepository;

    public InvoiceController(RideRepository rideRepository) {
        this.rideRepository = rideRepository;
    }

    @GetMapping(value = "/api/rides/{id}/invoice", produces = MediaType.TEXT_HTML_VALUE)
    public String getInvoice(@PathVariable("id") UUID rideId) {
        Ride ride = rideRepository.findById(rideId).orElseThrow();
        return "<html><body>" +
                "<h2>Bike Taxi Ride Invoice</h2>" +
                "<p><b>Ride ID:</b> " + ride.getId() + "</p>" +
                "<p><b>Rider:</b> " + (ride.getRider() != null ? ride.getRider().getFirstName() + " " + ride.getRider().getLastName() : "-") + "</p>" +
                "<p><b>Driver:</b> " + (ride.getDriver() != null ? ride.getDriver().getFirstName() + " " + ride.getDriver().getLastName() : "-") + "</p>" +
                "<p><b>Pickup:</b> (" + ride.getPickupLat() + ", " + ride.getPickupLng() + ")</p>" +
                "<p><b>Drop:</b> (" + ride.getDropLat() + ", " + ride.getDropLng() + ")</p>" +
                "<p><b>Status:</b> " + ride.getStatus() + "</p>" +
                "<p><b>Fare:</b> ₹" + (ride.getFinalFare() != null ? ride.getFinalFare() : ride.getEstimatedFare()) + "</p>" +
                "<p><b>Date:</b> " + (ride.getCompletedAt() != null ? ride.getCompletedAt() : ride.getStartedAt()) + "</p>" +
                "</body></html>";
    }
} 