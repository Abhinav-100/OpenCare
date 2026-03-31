package com.ciphertext.opencarebackend.mapper;
import com.ciphertext.opencarebackend.modules.user.dto.response.UserActivityResponse;
import com.ciphertext.opencarebackend.entity.UserActivity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
@Component
public interface UserActivityMapper {

    @Mapping(source = "profileId", target = "profileId")
    @Mapping(source = "profile.name", target = "profileName")
    @Mapping(source = "profile.email", target = "profileEmail")
    @Mapping(source = "lastKnownLocation.y", target = "lastKnownLocationLatitude")
    @Mapping(source = "lastKnownLocation.x", target = "lastKnownLocationLongitude")
    @Mapping(source = "avgSessionDuration", target = "avgSessionDurationSeconds", qualifiedByName = "durationToSeconds")
    UserActivityResponse toResponse(UserActivity userActivity);

    @Named("durationToSeconds")
    default Long durationToSeconds(Duration duration) {
        return duration != null ? duration.getSeconds() : null;
    }
}
