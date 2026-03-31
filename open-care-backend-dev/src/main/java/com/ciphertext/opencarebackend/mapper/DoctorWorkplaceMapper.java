package com.ciphertext.opencarebackend.mapper;
import com.ciphertext.opencarebackend.modules.provider.dto.request.DoctorWorkplaceRequest;
import com.ciphertext.opencarebackend.modules.provider.dto.response.DoctorWorkplaceResponse;
import com.ciphertext.opencarebackend.modules.shared.dto.response.enums.TeacherPositionResponse;
import com.ciphertext.opencarebackend.entity.DoctorWorkplace;
import com.ciphertext.opencarebackend.enums.TeacherPosition;
import org.mapstruct.*;
import org.springframework.stereotype.Component;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
@Component
public interface DoctorWorkplaceMapper {
    @Mapping(target = "teacherPosition", source = "teacherPosition", qualifiedByName = "teacherEnumToResponse")
    DoctorWorkplaceResponse toResponse(DoctorWorkplace doctorWorkplace);

    @Mapping(target = "doctor.id", source = "doctorId")
    @Mapping(target = "medicalSpeciality.id", source = "medicalSpecialityId")
    @Mapping(target = "institution.id", source = "institutionId")
    @Mapping(target = "hospital.id", source = "hospitalId")
    @Mapping(target = "teacherPosition", source = "teacherPosition", qualifiedByName = "teacherStringToEnum")
    DoctorWorkplace toEntity(DoctorWorkplaceRequest request);

    /**
     * Partially update an existing entity with values from request DTO.
     * Only non-null values from the request are applied.
     *
     * @param request the request DTO with updated values
     * @param doctorWorkplace the entity to update
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "doctor.id", source = "doctorId")
    @Mapping(target = "medicalSpeciality.id", source = "medicalSpecialityId")
    @Mapping(target = "institution.id", source = "institutionId")
    @Mapping(target = "hospital.id", source = "hospitalId")
    @Mapping(target = "teacherPosition", source = "teacherPosition", qualifiedByName = "teacherStringToEnum")
    void partialUpdate(DoctorWorkplaceRequest request, @MappingTarget DoctorWorkplace doctorWorkplace);

    @Named("teacherEnumToResponse")
    default TeacherPositionResponse teacherEnumToResponse(TeacherPosition teacherPosition) {
        return teacherPosition != null ? teacherPosition.toResponse() : null;
    }

    @Named("teacherStringToEnum")
    default TeacherPosition teacherStringToEnum(String teacherPosition) {
        return teacherPosition != null ? TeacherPosition.valueOf(teacherPosition) : null;
    }
}
