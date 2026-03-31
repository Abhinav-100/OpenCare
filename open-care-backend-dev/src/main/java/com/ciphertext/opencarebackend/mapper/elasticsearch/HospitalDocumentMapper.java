package com.ciphertext.opencarebackend.mapper.elasticsearch;
import com.ciphertext.opencarebackend.modules.provider.dto.elasticsearch.HospitalDocument;
import com.ciphertext.opencarebackend.entity.Hospital;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Component;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
@Component
public interface HospitalDocumentMapper {
    @Mapping(target = "districtId", source = "district.id")
    @Mapping(target = "districtName", source = "district.name")
    @Mapping(target = "upazilaId", source = "upazila.id")
    @Mapping(target = "upazilaName", source = "upazila.name")
    @Mapping(target = "unionId", source = "union.id")
    @Mapping(target = "unionName", source = "union.name")
    @Mapping(target = "hospitalType", source = "hospitalType")
    @Mapping(target = "organizationType", source = "organizationType")
    HospitalDocument toDocument(Hospital hospital);
}
