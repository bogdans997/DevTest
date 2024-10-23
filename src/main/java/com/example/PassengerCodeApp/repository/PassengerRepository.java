package com.example.PassengerCodeApp.repository;

import com.example.PassengerCodeApp.model.Passenger;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PassengerRepository extends JpaRepository<Passenger, Long> {
    List<Passenger> findByNameContaining(String name);
}