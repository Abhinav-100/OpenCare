package com.ciphertext.opencarebackend.modules.user.service;

import com.ciphertext.opencarebackend.entity.UserActivity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface UserActivityService {

    Page<UserActivity> getAllUserActivities(Pageable pageable);

    UserActivity getUserActivityById(Long profileId);

    UserActivity getUserActivityByProfileId(Long profileId);

    /**
     * Update user activity on login
     *
     * @param profileId the profile ID
     * @param clientIp  the client IP address
     * @param device    the device information
     * @param browser   the browser information
     * @param operatingSystem the operating system information
     */
    void updateOnLogin(Long profileId, String clientIp, String device, String browser, String operatingSystem);

    /**
     * Update user activity on logout
     *
     * @param profileId the profile ID
     */
    void updateOnLogout(Long profileId);

    /**
     * Track user registration activity
     *
     * @param profileId the profile ID
     * @param clientIp  the client IP address
     * @param device    the device information
     * @param browser   the browser information
     * @param operatingSystem the operating system information
     */
    void trackRegistration(Long profileId, String clientIp, String device, String browser, String operatingSystem);

    /**
     * Update last activity time for a user
     *
     * @param profileId the profile ID
     */
    void updateLastActivity(Long profileId);

    /**
     * Update user activity when an ad is seen
     *
     * @param profileId the profile ID
     */
    void updateAdSeen(Long profileId);

    /**
     * Update user activity when an ad is clicked
     *
     * @param profileId the profile ID
     */
    void updateAdClicked(Long profileId);

    /**
     * Update session duration
     *
     * @param profileId      the profile ID
     * @param sessionStart   the session start time
     * @param sessionEnd     the session end time
     */
    void updateSessionDuration(Long profileId, LocalDateTime sessionStart, LocalDateTime sessionEnd);

    /**
     * Get or create user activity for a profile
     *
     * @param profileId the profile ID
     * @return the user activity
     */
    UserActivity getOrCreateUserActivity(Long profileId);
}
