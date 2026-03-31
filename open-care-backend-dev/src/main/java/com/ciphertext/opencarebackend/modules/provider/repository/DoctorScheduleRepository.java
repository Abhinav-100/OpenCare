package com.ciphertext.opencarebackend.modules.provider.repository;

import com.ciphertext.opencarebackend.entity.DoctorSchedule;
import com.ciphertext.opencarebackend.enums.DaysOfWeek;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Sadman
 */
@Repository
public interface DoctorScheduleRepository extends JpaRepository<DoctorSchedule, Long> {
    long countDoctorScheduleByDoctorWorkplace_Doctor_Id(Long doctorId);

    List<DoctorSchedule> findByDoctorWorkplace_Doctor_IdAndDaysOfWeek(Long doctorId, DaysOfWeek daysOfWeek);
}