package com.example.PassengerCodeApp.service;

import com.example.PassengerCodeApp.model.Passenger;
import com.example.PassengerCodeApp.repository.PassengerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class PassengerService {

    @Autowired
    private PassengerRepository passengerRepository;

    @Autowired
    private JavaMailSender mailSender;

    public List<Passenger> getPassengers(String name) {
        if (name != null) {
            return passengerRepository.findByNameContaining(name);
        } else {
            return passengerRepository.findAll();
        }
    }

    public Passenger registerPassenger(Passenger passenger) {
        validatePassenger(passenger);

        String passengerCode = generatePassengerCode(passenger);
        passenger.setPassengerCode(passengerCode);

        Passenger savedPassenger = passengerRepository.save(passenger);

        sendConfirmationEmail(savedPassenger);

        String qrCodeUrl = generateQRCode(passengerCode);
        System.out.println("QR Code URL: " + qrCodeUrl);

        return savedPassenger;
    }

    private void validatePassenger(Passenger passenger) {
        LocalDate today = LocalDate.now();
        long age = ChronoUnit.YEARS.between(passenger.getBirthdate(), today);

        if (age < 1 || age > 80) {
            throw new IllegalArgumentException("Passenger age must be between 12 months and 80 years.");
        }

        if (age > 18 && "children".equalsIgnoreCase(passenger.getMealPreference())) {
            throw new IllegalArgumentException("Adults over 18 cannot order a children's meal.");
        }

        if ("UK".equalsIgnoreCase(passenger.getFlightDestination()) && "First".equalsIgnoreCase(passenger.getTravelClass())) {
            throw new IllegalArgumentException("There is no first class for UK flights.");
        }
    }

    public String generatePassengerCode(Passenger passenger) {
        StringBuilder code = new StringBuilder();

        code.append(getFlightDestinationCode(passenger));

        code.append(getGenderCode(passenger));

        code.append(getMealCode(passenger));

        code.append(getTravelClassCode(passenger));

        return code.toString();
    }

    private String getFlightDestinationCode(Passenger passenger) {
        String destination = passenger.getFlightDestination().toLowerCase();
        LocalDateTime now = LocalDateTime.now();
        boolean isNightFlight = now.getHour() >= 22 || now.getHour() < 6;

        if ("uk".equals(destination)) {
            return isNightFlight ? "u" : "U";
        } else if (destination.matches(".*europe.*")) {
            return isNightFlight ? "e" : "E";
        } else if (destination.matches(".*asia.*")) {
            return isNightFlight ? "a" : "A";
        } else if (destination.matches(".*americas.*")) {
            return isNightFlight ? "z" : "Z";
        }
        throw new IllegalArgumentException("Unknown flight destination.");
    }

    private String getGenderCode(Passenger passenger) {
        LocalDate today = LocalDate.now();
        long age = ChronoUnit.YEARS.between(passenger.getBirthdate(), today);
        if (age < 12) {
            return "Male".equalsIgnoreCase(passenger.getGender()) ? "x" : "y";
        }
        return "Male".equalsIgnoreCase(passenger.getGender()) ? "X" : "Y";
    }

    private String getMealCode(Passenger passenger) {
        String meal = passenger.getMealPreference().toLowerCase();
        LocalDate today = LocalDate.now();
        long age = ChronoUnit.YEARS.between(passenger.getBirthdate(), today);

        switch (meal) {
            case "european":
                return age < 12 ? "g" : "G";
            case "asian":
                return age < 12 ? "h" : "H";
            case "vegetarian":
                return age < 12 ? "k" : "K";
            default:
                throw new IllegalArgumentException("Unknown meal preference.");
        }
    }

    private String getTravelClassCode(Passenger passenger) {
        switch (passenger.getTravelClass().toLowerCase()) {
            case "first":
                return "P";
            case "business":
                return "Q";
            case "economy":
                return "R";
            default:
                throw new IllegalArgumentException("Unknown travel class.");
        }
    }

    private String getCountryCode(String address) {
        if (address != null && address.toLowerCase().contains("europe")) {
            return "-EU";
        } else {
            return "-ZZ";
        }
    }

    private void sendConfirmationEmail(Passenger passenger) {
        System.out.println("Sending email to " + passenger.getEmail() + " with passenger code: " + passenger.getPassengerCode());
    }

    public String generateQRCode(String passengerCode) {
        String apiUrl = "https://api.qrserver.com/v1/create-qr-code/?data=" + passengerCode + "&size=150x150";
        return apiUrl;
    }

    public void sendEmail(Passenger passenger) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(passenger.getEmail());
        message.setSubject("Your Flight Code");
        message.setText("Dear " + passenger.getName() + ",\nYour flight code is: " + passenger.getPassengerCode());
        mailSender.send(message);
    }
}
