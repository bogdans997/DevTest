package com.example.PassengerCodeApp.controller;

import com.example.PassengerCodeApp.model.Passenger;
import com.example.PassengerCodeApp.service.PassengerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/passengers")
public class PassengerController {

    @Autowired
    private PassengerService passengerService;

    @PostMapping("/register")
    public ResponseEntity<Passenger> registerPassenger(@RequestBody Passenger passenger) {
        Passenger savedPassenger = passengerService.registerPassenger(passenger);
        return ResponseEntity.ok(savedPassenger);
    }

    @GetMapping("/list")
    public List<Passenger> getPassengers(@RequestParam(value = "name", required = false) String name) {
        return passengerService.getPassengers(name);
    }
}