package com.ciphertext.opencarebackend.mapper;
import com.ciphertext.opencarebackend.modules.provider.dto.request.DoctorDegreeRequest;
import com.ciphertext.opencarebackend.modules.provider.dto.response.DoctorDegreeResponse;
import com.ciphertext.opencarebackend.entity.DoctorDegree;
import org.mapstruct.*;
import org.springframework.stereotype.Component;

/**
 * MapStruct mapper for converting between DoctorDegree entities and DTOs.
 * Handles automatic mapping of fields and relationships.
 *
 * @author Sadman
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
@Component
public interface DoctorDegreeMapper {

    /**
     * Convert DoctorDegree entity to response DTO.
     * Includes audit fields (createdAt, updatedAt) from Auditable base class.
     *
     * @param doctorDegree the entity to convert
     * @return the response DTO
     */
    DoctorDegreeResponse toResponse(DoctorDegree doctorDegree);

    /**
     * Convert request DTO to DoctorDegree entity.
     * Maps nested IDs to entity references.
     *
     * @param request the request DTO
     * @return the entity
     */
    @Mapping(target = "doctor.id", source = "doctorId")
    @Mapping(target = "degree.id", source = "degreeId")
    @Mapping(target = "medicalSpeciality.id", source = "medicalSpecialityId")
    @Mapping(target = "institution.id", source = "institutionId")
    DoctorDegree toEntity(DoctorDegreeRequest request);

    /**
     * Partially update an existing entity with values from request DTO.
     * Only non-null values from the request are applied.
     *
     * @param request the request DTO with updated values
     * @param doctorDegree the entity to update
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "doctor.id", source = "doctorId")
    @Mapping(target = "degree.id", source = "degreeId")
    @Mapping(target = "medicalSpeciality.id", source = "medicalSpecialityId")
    @Mapping(target = "institution.id", source = "institutionId")
    void partialUpdate(DoctorDegreeRequest request, @MappingTarget DoctorDegree doctorDegree);
}