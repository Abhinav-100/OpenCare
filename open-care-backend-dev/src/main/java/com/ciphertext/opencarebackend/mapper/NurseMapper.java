package com.ciphertext.opencarebackend.mapper;
import com.ciphertext.opencarebackend.modules.provider.dto.response.NurseResponse;
import com.ciphertext.opencarebackend.modules.provider.dto.request.NurseRequest;
import com.ciphertext.opencarebackend.entity.Nurse;
import org.mapstruct.*;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Period;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
@Component
public interface NurseMapper {
    @Mapping( target = "yearOfExperience", expression = "java(calculateYearsOfExperience(nurse.getStartDate()))")
    NurseResponse toResponse(Nurse nurse);

    Nurse toEntity(NurseRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void partialUpdate(NurseRequest request, @MappingTarget Nurse nurse);

    default Integer calculateYearsOfExperience(LocalDate startDate) {
        if (startDate == null) {
            return null;
        }
        return Period.between(startDate, LocalDate.now()).getYears();
    }
}
