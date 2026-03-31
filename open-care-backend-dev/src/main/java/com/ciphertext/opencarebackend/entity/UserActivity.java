package com.ciphertext.opencarebackend.entity;

import com.vladmihalcea.hibernate.type.interval.PostgreSQLIntervalType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.locationtech.jts.geom.Point;

import java.time.Duration;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "user_activity")
public class UserActivity {

    @Id
    @Column(name = "profile_id")
    private Long profileId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "profile_id")
    private Profile profile;

    @Column(name = "last_login_time")
    private LocalDateTime lastLoginTime;

    @Column(name = "last_logout_time")
    private LocalDateTime lastLogoutTime;

    @Column(name = "last_activity_time")
    private LocalDateTime lastActivityTime;

    @Column(name = "last_login_ip", length = 45)
    private String lastLoginIp;

    @Column(name = "last_login_device", length = 100)
    private String lastLoginDevice;

    @Column(name = "last_login_browser", length = 100)
    private String lastLoginBrowser;

    @Column(name = "last_login_operating_system", length = 100)
    private String lastLoginOperatingSystem;

    @Column(name = "last_known_location", columnDefinition = "geography(Point, 4326)")
    private Point lastKnownLocation;

    @Column(name = "total_logins", nullable = false)
    private Integer totalLogins = 0;

    @Column(name = "total_sessions", nullable = false)
    private Integer totalSessions = 0;

    @Type(PostgreSQLIntervalType.class)
    @Column(name = "avg_session_duration")
    private Duration avgSessionDuration;

    @Column(name = "ad_click_count", nullable = false)
    private Integer adClickCount = 0;

    @Column(name = "last_ad_seen")
    private LocalDateTime lastAdSeen;

    @Column(name = "last_ad_clicked")
    private LocalDateTime lastAdClicked;
}
