package com.gatedcommunity.backend.service;

import com.gatedcommunity.backend.dto.FacilityDTO;
import com.gatedcommunity.backend.entity.Facility;
import com.gatedcommunity.backend.entity.FacilityBooking;
import com.gatedcommunity.backend.model.User;
import com.gatedcommunity.backend.repository.FacilityBookingRepository;
import com.gatedcommunity.backend.repository.FacilityRepository;
import com.gatedcommunity.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FacilityService {

    private final FacilityRepository facilityRepository;
    private final FacilityBookingRepository bookingRepository;
    private final UserRepository userRepository;

    // ── Facility CRUD ──

    public FacilityDTO.FacilityResponse createFacility(FacilityDTO.FacilityRequest req) {
        Facility f = Facility.builder()
                .name(req.getName())
                .description(req.getDescription())
                .capacity(req.getCapacity())
                .openTime(req.getOpenTime())
                .closeTime(req.getCloseTime())
                .build();
        return mapFacility(facilityRepository.save(f));
    }

    public List<FacilityDTO.FacilityResponse> getAllFacilities() {
        return facilityRepository.findAll().stream().map(this::mapFacility).collect(Collectors.toList());
    }

    public List<FacilityDTO.FacilityResponse> getActiveFacilities() {
        return facilityRepository.findByIsActiveTrue().stream().map(this::mapFacility).collect(Collectors.toList());
    }

    public FacilityDTO.FacilityResponse toggleFacility(Long id) {
        Facility f = facilityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Facility not found"));
        f.setIsActive(!f.getIsActive());
        return mapFacility(facilityRepository.save(f));
    }

    // ── Booking ──

    public FacilityDTO.BookingResponse createBooking(Long residentId, FacilityDTO.BookingRequest req) {
        Facility facility = facilityRepository.findById(req.getFacilityId())
                .orElseThrow(() -> new RuntimeException("Facility not found"));
        User resident = userRepository.findById(residentId)
                .orElseThrow(() -> new RuntimeException("Resident not found"));

        List<FacilityBooking> existingBookings = bookingRepository.findByFacilityId(req.getFacilityId());
        LocalTime reqStart = LocalTime.parse(req.getStartTime());
        LocalTime reqEnd = LocalTime.parse(req.getEndTime());

        for (FacilityBooking b : existingBookings) {
            if ("PENDING".equals(b.getStatus()) || "APPROVED".equals(b.getStatus())) {
                if (b.getBookingDate().equals(req.getBookingDate())) {
                    LocalTime bStart = LocalTime.parse(b.getStartTime());
                    LocalTime bEnd = LocalTime.parse(b.getEndTime());
                    if (reqStart.isBefore(bEnd) && reqEnd.isAfter(bStart)) {
                        throw new RuntimeException("Amenity is already booked for this specific time.");
                    }
                }
            }
        }

        FacilityBooking booking = FacilityBooking.builder()
                .facility(facility)
                .resident(resident)
                .bookingDate(req.getBookingDate())
                .startTime(req.getStartTime())
                .endTime(req.getEndTime())
                .notes(req.getNotes())
                .build();
        return mapBooking(bookingRepository.save(booking));
    }

    public List<FacilityDTO.BookingResponse> getMyBookings(Long residentId) {
        return bookingRepository.findByResidentIdOrderByBookingDateDesc(residentId)
                .stream().map(this::mapBooking).collect(Collectors.toList());
    }

    public List<FacilityDTO.BookingResponse> getAllBookings() {
        return bookingRepository.findAllByOrderByBookingDateDesc()
                .stream().map(this::mapBooking).collect(Collectors.toList());
    }

    public FacilityDTO.BookingResponse updateBookingStatus(Long bookingId, String status) {
        FacilityBooking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        booking.setStatus(status);
        return mapBooking(bookingRepository.save(booking));
    }

    public void deleteBooking(Long bookingId) {
        bookingRepository.deleteById(bookingId);
    }

    // ── Mappers ──

    private FacilityDTO.FacilityResponse mapFacility(Facility f) {
        return FacilityDTO.FacilityResponse.builder()
                .id(f.getId())
                .name(f.getName())
                .description(f.getDescription())
                .capacity(f.getCapacity())
                .openTime(f.getOpenTime())
                .closeTime(f.getCloseTime())
                .isActive(f.getIsActive())
                .build();
    }

    private FacilityDTO.BookingResponse mapBooking(FacilityBooking b) {
        return FacilityDTO.BookingResponse.builder()
                .id(b.getId())
                .facilityId(b.getFacility().getId())
                .facilityName(b.getFacility().getName())
                .residentId(b.getResident().getId())
                .residentName(b.getResident().getName())
                .residentPhone(b.getResident().getPhone())
                .bookingDate(b.getBookingDate())
                .startTime(b.getStartTime())
                .endTime(b.getEndTime())
                .status(b.getStatus())
                .notes(b.getNotes())
                .createdAt(b.getCreatedAt())
                .build();
    }
}
