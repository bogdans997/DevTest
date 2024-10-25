package com.example.PassengerCodeApp.controller;

import com.example.PassengerCodeApp.model.Passenger;
import com.example.PassengerCodeApp.service.PassengerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
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

    @GetMapping("/form")
    public String showForm(Model model) {
        model.addAttribute("passenger", new Passenger());
        return "form";
    }

    @PostMapping("/submit")
    public String submitForm(@ModelAttribute Passenger passenger, Model model) {
        System.out.println("Passenger details received: " + passenger.toString());
        String passengerCode = passengerService.generatePassengerCode(passenger);
        passenger.setPassengerCode(passengerCode);
        passengerService.registerPassenger(passenger);
        passengerService.sendEmail(passenger);
        String qrCodeUrl = passengerService.generateQRCode(passengerCode);
        model.addAttribute("qrCodeUrl", qrCodeUrl);
        return "success";
    }
}