package com.antonio.skybase.services;

import com.antonio.skybase.config.TestConfig;
import com.antonio.skybase.entities.Aircraft;
import com.antonio.skybase.exceptions.BadRequestException;
import com.antonio.skybase.exceptions.NotFoundException;
import com.antonio.skybase.repositories.AircraftRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Import(TestConfig.class)
@ActiveProfiles("test")
@Transactional
class AircraftServiceIntegrationTest {

    @Autowired
    private AircraftService aircraftService;

    @Autowired
    private AircraftRepository aircraftRepository;

    @BeforeEach
    void setUp() {
        aircraftRepository.deleteAll();
    }

    @Test
    void testCreateAircraftSuccess() {
        // Given
        Aircraft aircraft = new Aircraft();
        aircraft.setRegistration("ABC123");
        aircraft.setType("Boeing 737");
        aircraft.setRange(5000);
        aircraft.setCapacity(180);

        // When
        Aircraft createdAircraft = aircraftService.create(aircraft);

        // Then
        assertThat(createdAircraft).isNotNull();
        assertThat(createdAircraft.getId()).isNotNull();
        assertThat(createdAircraft.getRegistration()).isEqualTo("ABC123");
        assertThat(createdAircraft.getType()).isEqualTo("Boeing 737");
        assertThat(createdAircraft.getRange()).isEqualTo(5000);
        assertThat(createdAircraft.getCapacity()).isEqualTo(180);

        // Verify it's saved in database
        Aircraft fromDb = aircraftRepository.findById(createdAircraft.getId()).orElse(null);
        assertThat(fromDb).isNotNull();
        assertThat(fromDb.getRegistration()).isEqualTo("ABC123");
        assertThat(fromDb.getType()).isEqualTo("Boeing 737");
    }

    @Test
    void testCreateAircraftWithDuplicateRegistration() {
        // Given - Create first aircraft
        Aircraft firstAircraft = new Aircraft();
        firstAircraft.setRegistration("DEF456");
        firstAircraft.setType("Airbus A320");
        firstAircraft.setRange(6000);
        firstAircraft.setCapacity(160);
        aircraftService.create(firstAircraft);

        // Given - Create second aircraft with same registration
        Aircraft secondAircraft = new Aircraft();
        secondAircraft.setRegistration("DEF456"); // Duplicate registration
        secondAircraft.setType("Boeing 737");
        secondAircraft.setRange(5500);
        secondAircraft.setCapacity(170);

        // When & Then
        assertThrows(BadRequestException.class, () -> aircraftService.create(secondAircraft));

        // Verify only one aircraft exists
        List<Aircraft> aircraft = aircraftRepository.findAll();
        assertThat(aircraft).hasSize(1);
        assertThat(aircraft.get(0).getType()).isEqualTo("Airbus A320");
    }

    @Test
    void testGetAllAircraft() {
        // Given
        Aircraft aircraft1 = new Aircraft();
        aircraft1.setRegistration("GHI789");
        aircraft1.setType("Boeing 777");
        aircraft1.setRange(12000);
        aircraft1.setCapacity(300);
        aircraftRepository.save(aircraft1);

        Aircraft aircraft2 = new Aircraft();
        aircraft2.setRegistration("JKL012");
        aircraft2.setType("Airbus A350");
        aircraft2.setRange(15000);
        aircraft2.setCapacity(350);
        aircraftRepository.save(aircraft2);

        // When
        List<Aircraft> aircraft = aircraftService.getAll();

        // Then
        assertThat(aircraft).hasSize(2);
        assertThat(aircraft).extracting(Aircraft::getRegistration)
                .containsExactlyInAnyOrder("GHI789", "JKL012");
        assertThat(aircraft).extracting(Aircraft::getType)
                .containsExactlyInAnyOrder("Boeing 777", "Airbus A350");
    }

    @Test
    void testGetAircraftByIdSuccess() {
        // Given
        Aircraft aircraft = new Aircraft();
        aircraft.setRegistration("MNO345");
        aircraft.setType("Boeing 787");
        aircraft.setRange(14000);
        aircraft.setCapacity(280);
        Aircraft saved = aircraftRepository.save(aircraft);

        // When
        Aircraft found = aircraftService.getById(saved.getId());

        // Then
        assertThat(found).isNotNull();
        assertThat(found.getId()).isEqualTo(saved.getId());
        assertThat(found.getRegistration()).isEqualTo("MNO345");
        assertThat(found.getType()).isEqualTo("Boeing 787");
        assertThat(found.getRange()).isEqualTo(14000);
        assertThat(found.getCapacity()).isEqualTo(280);
    }

    @Test
    void testGetAircraftByIdNotFound() {
        // When & Then
        assertThrows(NotFoundException.class, () -> aircraftService.getById(9999));
    }

    @Test
    void testUpdateAircraftSuccess() {
        // Given
        Aircraft existingAircraft = new Aircraft();
        existingAircraft.setRegistration("PQR678");
        existingAircraft.setType("Boeing 737");
        existingAircraft.setRange(5000);
        existingAircraft.setCapacity(180);
        Aircraft saved = aircraftRepository.save(existingAircraft);

        Aircraft updateData = new Aircraft();
        updateData.setRegistration("PQR678-UPDATED");
        updateData.setType("Boeing 737 MAX");
        updateData.setRange(5500);
        updateData.setCapacity(185);

        // When
        Aircraft updated = aircraftService.update(saved.getId(), updateData);

        // Then
        assertThat(updated).isNotNull();
        assertThat(updated.getId()).isEqualTo(saved.getId());
        assertThat(updated.getRegistration()).isEqualTo("PQR678-UPDATED");
        assertThat(updated.getType()).isEqualTo("Boeing 737 MAX");
        assertThat(updated.getRange()).isEqualTo(5500);
        assertThat(updated.getCapacity()).isEqualTo(185);

        // Verify in database
        Aircraft fromDb = aircraftRepository.findById(saved.getId()).orElse(null);
        assertThat(fromDb).isNotNull();
        assertThat(fromDb.getRegistration()).isEqualTo("PQR678-UPDATED");
        assertThat(fromDb.getType()).isEqualTo("Boeing 737 MAX");
    }

    @Test
    void testUpdateAircraftWithDuplicateRegistration() {
        // Given - Create two aircraft
        Aircraft aircraft1 = new Aircraft();
        aircraft1.setRegistration("STU901");
        aircraft1.setType("Airbus A320");
        aircraft1.setRange(6000);
        aircraft1.setCapacity(160);
        aircraftRepository.save(aircraft1);

        Aircraft aircraft2 = new Aircraft();
        aircraft2.setRegistration("VWX234");
        aircraft2.setType("Boeing 737");
        aircraft2.setRange(5000);
        aircraft2.setCapacity(180);
        Aircraft savedAircraft2 = aircraftRepository.save(aircraft2);

        // Try to update aircraft2 with aircraft1's registration
        Aircraft updateData = new Aircraft();
        updateData.setRegistration("STU901"); // Duplicate registration
        updateData.setType("Boeing 737");
        updateData.setRange(5000);
        updateData.setCapacity(180);

        // When & Then
        assertThrows(BadRequestException.class, () -> aircraftService.update(savedAircraft2.getId(), updateData));

        // Verify original registration is preserved
        Aircraft fromDb = aircraftRepository.findById(savedAircraft2.getId()).orElse(null);
        assertThat(fromDb).isNotNull();
        assertThat(fromDb.getRegistration()).isEqualTo("VWX234");
    }

    @Test
    void testUpdateAircraftNotFound() {
        // Given
        Aircraft updateData = new Aircraft();
        updateData.setRegistration("NONEXISTENT");
        updateData.setType("Some Type");
        updateData.setRange(1000);
        updateData.setCapacity(100);

        // When & Then
        assertThrows(NotFoundException.class, () -> aircraftService.update(9999, updateData));
    }

    @Test
    void testDeleteAircraftSuccess() {
        // Given
        Aircraft aircraft = new Aircraft();
        aircraft.setRegistration("YZA567");
        aircraft.setType("Airbus A330");
        aircraft.setRange(11000);
        aircraft.setCapacity(250);
        Aircraft saved = aircraftRepository.save(aircraft);

        // When
        aircraftService.delete(saved.getId());

        // Then
        assertThat(aircraftRepository.findById(saved.getId())).isEmpty();
    }

    @Test
    void testDeleteAircraftNotFound() {
        // When & Then - Should not throw exception for non-existent aircraft
        // This is expected behavior - delete operations are idempotent
        aircraftService.delete(9999);

        // Verify no aircraft exist
        assertThat(aircraftRepository.findAll()).isEmpty();
    }

    @Test
    void testGetAvailableAircraftByDate() {
        // Given
        Aircraft aircraft1 = new Aircraft();
        aircraft1.setRegistration("BCD890");
        aircraft1.setType("Boeing 737");
        aircraft1.setRange(5000);
        aircraft1.setCapacity(180);
        Aircraft savedAircraft1 = aircraftRepository.save(aircraft1);

        Aircraft aircraft2 = new Aircraft();
        aircraft2.setRegistration("EFG123");
        aircraft2.setType("Airbus A320");
        aircraft2.setRange(6000);
        aircraft2.setCapacity(160);
        Aircraft savedAircraft2 = aircraftRepository.save(aircraft2);

        // Note: Since we don't have AircraftAssignment setup in this test,
        // all aircraft should be available for any date
        java.time.LocalDate testDate = java.time.LocalDate.of(2023, 10, 15);

        // When
        List<Aircraft> availableAircraft = aircraftService.getAvailableAircraftByDate(testDate);

        // Then
        assertThat(availableAircraft).hasSize(2);
        assertThat(availableAircraft).extracting(Aircraft::getRegistration)
                .containsExactlyInAnyOrder("BCD890", "EFG123");
        // Verify that our saved aircraft are included
        assertThat(availableAircraft).contains(savedAircraft1, savedAircraft2);
    }
}
