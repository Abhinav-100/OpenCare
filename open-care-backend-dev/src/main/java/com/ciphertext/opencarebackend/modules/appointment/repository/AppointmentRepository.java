package com.ciphertext.opencarebackend.modules.appointment.repository;

import com.ciphertext.opencarebackend.entity.Appointment;
import com.ciphertext.opencarebackend.enums.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long>, JpaSpecificationExecutor<Appointment> {

    boolean existsByAppointmentNumber(String appointmentNumber);

    Optional<Appointment> findByAppointmentNumber(String appointmentNumber);

    List<Appointment> findByDoctorIdAndAppointmentDate(Long doctorId, LocalDate appointmentDate);

    List<Appointment> findByPatientProfileIdOrderByAppointmentDateDescStartTimeDesc(Long patientProfileId);

    @Query("SELECT a FROM Appointment a WHERE a.patientProfile.keycloakUserId = :keycloakUserId ORDER BY a.appointmentDate DESC, a.startTime DESC")
    List<Appointment> findByPatientKeycloakUserId(@Param("keycloakUserId") String keycloakUserId);

        @Query("SELECT a FROM Appointment a WHERE a.doctor.profile.keycloakUserId = :keycloakUserId ORDER BY a.appointmentDate DESC, a.startTime DESC")
        List<Appointment> findByDoctorKeycloakUserId(@Param("keycloakUserId") String keycloakUserId);

    @Query("SELECT a FROM Appointment a WHERE a.doctor.id = :doctorId AND a.appointmentDate = :date AND a.status NOT IN :excludedStatuses")
    List<Appointment> findActiveAppointmentsByDoctorAndDate(
            @Param("doctorId") Long doctorId,
            @Param("date") LocalDate date,
            @Param("excludedStatuses") List<AppointmentStatus> excludedStatuses
    );

    @Query("SELECT COUNT(a) > 0 FROM Appointment a WHERE a.doctor.id = :doctorId AND a.appointmentDate = :date " +
            "AND a.status NOT IN :excludedStatuses " +
            "AND ((a.startTime <= :startTime AND a.endTime > :startTime) OR (a.startTime < :endTime AND a.endTime >= :endTime) OR (a.startTime >= :startTime AND a.endTime <= :endTime))")
    boolean existsOverlappingAppointment(
            @Param("doctorId") Long doctorId,
            @Param("date") LocalDate date,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime,
            @Param("excludedStatuses") List<AppointmentStatus> excludedStatuses
    );

    @Query("SELECT a FROM Appointment a WHERE a.appointmentDate = :date AND a.status = :status AND a.reminderSent = false")
    List<Appointment> findAppointmentsForReminder(@Param("date") LocalDate date, @Param("status") AppointmentStatus status);

    long countByDoctorIdAndAppointmentDateAndStatusNotIn(Long doctorId, LocalDate date, List<AppointmentStatus> excludedStatuses);
}