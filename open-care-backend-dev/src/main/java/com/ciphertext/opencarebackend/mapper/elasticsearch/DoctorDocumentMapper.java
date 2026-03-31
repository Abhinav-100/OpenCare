package com.ciphertext.opencarebackend.mapper.elasticsearch;
import com.ciphertext.opencarebackend.modules.provider.dto.elasticsearch.DoctorDocument;
import com.ciphertext.opencarebackend.entity.Doctor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Component;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
@Component
public interface DoctorDocumentMapper {
    @Mapping(target = "name", source = "profile.name")
    @Mapping(target = "bnName", source = "profile.bnName")
    @Mapping(target = "districtId", source = "profile.district.id")
    @Mapping(target = "districtName", source = "profile.district.name")
    @Mapping(target = "upazilaId", source = "profile.upazila.id")
    @Mapping(target = "upazilaName", source = "profile.upazila.name")
    @Mapping(target = "unionId", source = "profile.union.id")
    @Mapping(target = "unionName", source = "profile.union.name")
    @Mapping(target = "imageUrl", source = "profile.imageUrl")
    DoctorDocument toDocument(Doctor doctor);
}
