package com.gatedcommunity.backend.repository;

import com.gatedcommunity.backend.entity.FacilityBooking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FacilityBookingRepository extends JpaRepository<FacilityBooking, Long> {
    List<FacilityBooking> findByResidentIdOrderByBookingDateDesc(Long residentId);
    List<FacilityBooking> findAllByOrderByBookingDateDesc();
    List<FacilityBooking> findByFacilityId(Long facilityId);
}
