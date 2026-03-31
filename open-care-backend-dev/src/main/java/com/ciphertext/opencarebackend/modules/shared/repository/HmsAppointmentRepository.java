package com.ciphertext.opencarebackend.modules.shared.repository;

import com.ciphertext.opencarebackend.modules.shared.entity.HmsAppointmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface HmsAppointmentRepository extends JpaRepository<HmsAppointmentEntity, Long> {

    List<HmsAppointmentEntity> findByPatientIdOrderByScheduledAtDesc(Long patientId);

    List<HmsAppointmentEntity> findByDoctorIdOrderByScheduledAtDesc(Long doctorId);

    Optional<HmsAppointmentEntity> findByIdAndDoctorId(Long id, Long doctorId);
}