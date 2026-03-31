package com.ciphertext.opencarebackend.mapper;
import com.ciphertext.opencarebackend.modules.appointment.dto.request.AppointmentRequest;
import com.ciphertext.opencarebackend.modules.appointment.dto.response.AppointmentResponse;
import com.ciphertext.opencarebackend.modules.appointment.dto.response.enums.AppointmentStatusResponse;
import com.ciphertext.opencarebackend.modules.appointment.dto.response.enums.AppointmentTypeResponse;
import com.ciphertext.opencarebackend.modules.payment.dto.response.enums.PaymentStatusResponse;
import com.ciphertext.opencarebackend.entity.Appointment;
import com.ciphertext.opencarebackend.entity.Doctor;
import com.ciphertext.opencarebackend.entity.DoctorWorkplace;
import com.ciphertext.opencarebackend.entity.Hospital;
import com.ciphertext.opencarebackend.enums.AppointmentStatus;
import com.ciphertext.opencarebackend.enums.AppointmentType;
import com.ciphertext.opencarebackend.enums.PaymentStatus;
import org.mapstruct.*;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
@Component
public interface AppointmentMapper {

    @Mapping(source = "patientProfile", target = "patientProfile")
    @Mapping(source = "doctor", target = "doctor")
    @Mapping(source = "hospital", target = "hospital")
    @Mapping(source = "doctorWorkplace", target = "doctorWorkplace")
    @Mapping(source = "appointmentType", target = "appointmentType", qualifiedByName = "appointmentTypeEnumToResponse")
    @Mapping(source = "status", target = "status", qualifiedByName = "appointmentStatusEnumToResponse")
    @Mapping(source = "paymentStatus", target = "paymentStatus", qualifiedByName = "paymentStatusEnumToResponse")
    AppointmentResponse toResponse(Appointment appointment);

    @Mapping(source = "doctorId", target = "doctor", qualifiedByName = "doctorFromId")
    @Mapping(source = "hospitalId", target = "hospital", qualifiedByName = "hospitalFromId")
    @Mapping(source = "doctorWorkplaceId", target = "doctorWorkplace", qualifiedByName = "doctorWorkplaceFromId")
    @Mapping(source = "appointmentType", target = "appointmentType", qualifiedByName = "appointmentTypeStringToEnum")
    @Mapping(target = "patientProfile", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "paymentStatus", ignore = true)
    @Mapping(target = "appointmentNumber", ignore = true)
    Appointment toEntity(AppointmentRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void partialUpdate(AppointmentRequest request, @MappingTarget Appointment appointment);

    @Named("appointmentTypeEnumToResponse")
    default AppointmentTypeResponse appointmentTypeEnumToResponse(AppointmentType type) {
        return type != null ? type.toResponse() : null;
    }

    @Named("appointmentTypeStringToEnum")
    default AppointmentType appointmentTypeStringToEnum(String type) {
        return StringUtils.hasText(type) ? AppointmentType.valueOf(type) : null;
    }

    @Named("appointmentStatusEnumToResponse")
    default AppointmentStatusResponse appointmentStatusEnumToResponse(AppointmentStatus status) {
        return status != null ? status.toResponse() : null;
    }

    @Named("paymentStatusEnumToResponse")
    default PaymentStatusResponse paymentStatusEnumToResponse(PaymentStatus status) {
        return status != null ? status.toResponse() : null;
    }

    @Named("doctorFromId")
    default Doctor doctorFromId(Long doctorId) {
        if (doctorId == null) {
            return null;
        }
        Doctor doctor = new Doctor();
        doctor.setId(doctorId);
        return doctor;
    }

    @Named("hospitalFromId")
    default Hospital hospitalFromId(Integer hospitalId) {
        if (hospitalId == null) {
            return null;
        }
        Hospital hospital = new Hospital();
        hospital.setId(hospitalId);
        return hospital;
    }

    @Named("doctorWorkplaceFromId")
    default DoctorWorkplace doctorWorkplaceFromId(Long doctorWorkplaceId) {
        if (doctorWorkplaceId == null) {
            return null;
        }
        DoctorWorkplace workplace = new DoctorWorkplace();
        workplace.setId(doctorWorkplaceId);
        return workplace;
    }
}
