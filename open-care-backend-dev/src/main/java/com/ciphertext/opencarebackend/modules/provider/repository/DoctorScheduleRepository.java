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
/**
 * Flow note: DoctorScheduleRepository belongs to the provider doctor/hospital module.
 * Read this with neighboring controller/service/repository files to trace request flow.
 */
public interface DoctorScheduleRepository extends JpaRepository<DoctorSchedule, Long> {
    long countDoctorScheduleByDoctorWorkplace_Doctor_Id(Long doctorId);

    List<DoctorSchedule> findByDoctorWorkplace_Doctor_IdAndDaysOfWeek(Long doctorId, DaysOfWeek daysOfWeek);
}