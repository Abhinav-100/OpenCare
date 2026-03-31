package com.ciphertext.opencarebackend.modules.user.service.impl;

import com.ciphertext.opencarebackend.entity.Profile;
import com.ciphertext.opencarebackend.entity.UserActivity;
import com.ciphertext.opencarebackend.exception.ResourceNotFoundException;
import com.ciphertext.opencarebackend.modules.user.repository.ProfileRepository;
import com.ciphertext.opencarebackend.modules.user.repository.UserActivityRepository;
import com.ciphertext.opencarebackend.modules.user.service.UserActivityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserActivityServiceImpl implements UserActivityService {

    private final UserActivityRepository userActivityRepository;
    private final ProfileRepository profileRepository;

    @Override
    public Page<UserActivity> getAllUserActivities(Pageable pageable) {
        log.info("Fetching all user activities with pagination: {}", pageable);
        return userActivityRepository.findAll(pageable);
    }

    @Override
    public UserActivity getUserActivityById(Long profileId) {
        log.info("Fetching user activity by profile ID: {}", profileId);
        return userActivityRepository.findById(profileId)
                .orElseThrow(() -> new ResourceNotFoundException("User activity not found for profile ID: " + profileId));
    }

    @Override
    public UserActivity getUserActivityByProfileId(Long profileId) {
        log.info("Fetching user activity by profile ID: {}", profileId);
        return userActivityRepository.findByProfileId(profileId).orElse(null);
    }

    @Async
    @Override
    @Transactional
    public void updateOnLogin(Long profileId, String clientIp, String device, String browser, String operatingSystem) {
        log.info("Updating user activity on login for profile ID: {}", profileId);

        try {
            UserActivity activity = getOrCreateUserActivity(profileId);

            LocalDateTime now = LocalDateTime.now();
            activity.setLastLoginTime(now);
            activity.setLastActivityTime(now);

            // Use pre-extracted metadata
            activity.setLastLoginIp(clientIp);
            activity.setLastLoginDevice(device);
            activity.setLastLoginBrowser(browser);
            activity.setLastLoginOperatingSystem(operatingSystem);

            // Increment counters
            activity.setTotalLogins(activity.getTotalLogins() + 1);
            activity.setTotalSessions(activity.getTotalSessions() + 1);

            userActivityRepository.save(activity);
            log.debug("User activity updated on login for profile ID: {}", profileId);
        } catch (Exception e) {
            log.error("Failed to update user activity on login for profile ID: {}", profileId, e);
        }
    }

    @Async
    @Override
    @Transactional
    public void updateOnLogout(Long profileId) {
        log.info("Updating user activity on logout for profile ID: {}", profileId);

        try {
            userActivityRepository.findByProfileId(profileId).ifPresent(activity -> {
                LocalDateTime now = LocalDateTime.now();
                activity.setLastLogoutTime(now);

                // Calculate and update session duration if login time is available
                if (activity.getLastLoginTime() != null) {
                    Duration sessionDuration = Duration.between(activity.getLastLoginTime(), now);
                    updateAverageSessionDuration(activity, sessionDuration);
                }

                userActivityRepository.save(activity);
                log.debug("User activity updated on logout for profile ID: {}", profileId);
            });
        } catch (Exception e) {
            log.error("Failed to update user activity on logout for profile ID: {}", profileId, e);
        }
    }

    @Async
    @Override
    @Transactional
    public void trackRegistration(Long profileId, String clientIp, String device, String browser, String operatingSystem) {
        log.info("Tracking registration activity for profile ID: {}", profileId);

        try {
            UserActivity activity = getOrCreateUserActivity(profileId);

            LocalDateTime now = LocalDateTime.now();
            activity.setLastLoginTime(now);
            activity.setLastActivityTime(now);

            // Set initial login metadata
            activity.setLastLoginIp(clientIp);
            activity.setLastLoginDevice(device);
            activity.setLastLoginBrowser(browser);
            activity.setLastLoginOperatingSystem(operatingSystem);

            // Initialize counters for new user
            activity.setTotalLogins(1);
            activity.setTotalSessions(1);

            userActivityRepository.save(activity);
            log.debug("Registration activity tracked for profile ID: {}", profileId);
        } catch (Exception e) {
            log.error("Failed to track registration activity for profile ID: {}", profileId, e);
        }
    }

    @Async
    @Override
    @Transactional
    public void updateLastActivity(Long profileId) {
        log.debug("Updating last activity time for profile ID: {}", profileId);

        try {
            userActivityRepository.findByProfileId(profileId).ifPresent(activity -> {
                activity.setLastActivityTime(LocalDateTime.now());
                userActivityRepository.save(activity);
            });
        } catch (Exception e) {
            log.error("Failed to update last activity for profile ID: {}", profileId, e);
        }
    }

    @Async
    @Override
    @Transactional
    public void updateAdSeen(Long profileId) {
        log.debug("Updating ad seen for profile ID: {}", profileId);

        try {
            UserActivity activity = getOrCreateUserActivity(profileId);
            activity.setLastAdSeen(LocalDateTime.now());
            activity.setLastActivityTime(LocalDateTime.now());
            userActivityRepository.save(activity);
            log.debug("Ad seen updated for profile ID: {}", profileId);
        } catch (Exception e) {
            log.error("Failed to update ad seen for profile ID: {}", profileId, e);
        }
    }

    @Async
    @Override
    @Transactional
    public void updateAdClicked(Long profileId) {
        log.debug("Updating ad clicked for profile ID: {}", profileId);

        try {
            UserActivity activity = getOrCreateUserActivity(profileId);
            LocalDateTime now = LocalDateTime.now();
            activity.setLastAdClicked(now);
            activity.setLastActivityTime(now);
            activity.setAdClickCount(activity.getAdClickCount() + 1);
            userActivityRepository.save(activity);
            log.debug("Ad clicked updated for profile ID: {}", profileId);
        } catch (Exception e) {
            log.error("Failed to update ad clicked for profile ID: {}", profileId, e);
        }
    }

    @Async
    @Override
    @Transactional
    public void updateSessionDuration(Long profileId, LocalDateTime sessionStart, LocalDateTime sessionEnd) {
        log.debug("Updating session duration for profile ID: {}", profileId);

        try {
            userActivityRepository.findByProfileId(profileId).ifPresent(activity -> {
                Duration sessionDuration = Duration.between(sessionStart, sessionEnd);
                updateAverageSessionDuration(activity, sessionDuration);
                userActivityRepository.save(activity);
                log.debug("Session duration updated for profile ID: {}", profileId);
            });
        } catch (Exception e) {
            log.error("Failed to update session duration for profile ID: {}", profileId, e);
        }
    }

    @Override
    @Transactional
    public UserActivity getOrCreateUserActivity(Long profileId) {
        return userActivityRepository.findByProfileId(profileId)
                .orElseGet(() -> {
                    log.info("Creating new user activity for profile ID: {}", profileId);
                    Profile profile = profileRepository.findById(profileId)
                            .orElseThrow(() -> new ResourceNotFoundException("Profile not found with ID: " + profileId));

                    UserActivity newActivity = new UserActivity();
                    newActivity.setProfile(profile);
                    newActivity.setTotalLogins(0);
                    newActivity.setTotalSessions(0);
                    newActivity.setAdClickCount(0);

                    return userActivityRepository.save(newActivity);
                });
    }

    /**
     * Calculate and update the average session duration
     * Formula: new_avg = ((old_avg * (total_sessions - 1)) + new_session) / total_sessions
     */
    private void updateAverageSessionDuration(UserActivity activity, Duration newSessionDuration) {
        int totalSessions = activity.getTotalSessions();
        Duration currentAvg = activity.getAvgSessionDuration();

        if (totalSessions <= 1 || currentAvg == null) {
            activity.setAvgSessionDuration(newSessionDuration);
        } else {
            long totalSeconds = currentAvg.getSeconds() * (totalSessions - 1) + newSessionDuration.getSeconds();
            long avgSeconds = totalSeconds / totalSessions;
            activity.setAvgSessionDuration(Duration.ofSeconds(avgSeconds));
        }
    }
}
