package com.ciphertext.opencarebackend.modules.provider.dto.response;
import com.ciphertext.opencarebackend.modules.shared.dto.response.enums.DaysOfWeekResponse;
import lombok.Getter;
import lombok.Setter;

import java.sql.Time;

@Getter
@Setter
/**
 * Flow note: DoctorScheduleResponse belongs to the provider doctor/hospital module.
 * Read this with neighboring controller/service/repository files to trace request flow.
 */
public class DoctorScheduleResponse {
    private Long id;
    private DoctorWorkplaceResponse doctorWorkplace;
    private DaysOfWeekResponse daysOfWeek;
    private Time startTime;
    private Time endTime;
}
