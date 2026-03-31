package com.ciphertext.opencarebackend.modules.user.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class UserActivityResponse {
    private Long profileId;
    private String profileName;
    private String profileEmail;
    private LocalDateTime lastLoginTime;
    private LocalDateTime lastLogoutTime;
    private LocalDateTime lastActivityTime;
    private String lastLoginIp;
    private String lastLoginDevice;
    private String lastLoginBrowser;
    private Double lastKnownLocationLatitude;
    private Double lastKnownLocationLongitude;
    private Integer totalLogins;
    private Integer totalSessions;
    private Long avgSessionDurationSeconds;
    private Integer adClickCount;
    private LocalDateTime lastAdSeen;
    private LocalDateTime lastAdClicked;
}
