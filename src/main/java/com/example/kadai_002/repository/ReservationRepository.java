package com.example.kadai_002.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.kadai_002.entity.Reservation;
import com.example.kadai_002.entity.Store;

public interface ReservationRepository extends JpaRepository<Reservation, Integer> {
    long countByStoreAndReservationDateAndReservationTime(Store store, LocalDate date, LocalTime time);
    Page<Reservation> findByUserId(Integer userId, Pageable pageable);
    
    List<Reservation> findByStoreAndReservationDate(Store store, LocalDate date);
}