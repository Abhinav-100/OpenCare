package com.ciphertext.opencarebackend.mapper;
import com.ciphertext.opencarebackend.modules.catalog.dto.request.TagRequest;
import com.ciphertext.opencarebackend.modules.catalog.dto.response.TagResponse;
import com.ciphertext.opencarebackend.entity.Tag;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface TagMapper {

    TagResponse toResponse(Tag tag);

    @Mapping(target = "id", ignore = true)
    Tag toEntity(TagRequest tagRequest);

    @Mapping(target = "id", ignore = true)
    void updateEntityFromRequest(TagRequest tagRequest, @MappingTarget Tag tag);
}
