package com.ciphertext.opencarebackend.modules.content.dashboard.scheduler;

import com.ciphertext.opencarebackend.modules.content.dashboard.service.DashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.cache.CacheManager;

@Component
@RequiredArgsConstructor
@Slf4j
public class DashboardScheduler {

    private final DashboardService dashboardService;
    private final SimpMessagingTemplate messagingTemplate;
    private final CacheManager cacheManager;

    @Scheduled(fixedRate = 30000) // Every 30 seconds
    public void broadcastRealTimeStats() {
        try {
            var stats = dashboardService.getRealTimeStats();
            messagingTemplate.convertAndSend("/topic/dashboard/real-time", stats);
            log.debug("Broadcasted real-time stats to dashboard subscribers - Online users: {}, System load: {}%",
                     stats.getOnlineUsers(), String.format("%.1f", stats.getSystemLoad()));
        } catch (Exception e) {
            log.error("Error broadcasting real-time stats", e);
        }
    }

    @Scheduled(fixedRate = 60000) // Every minute
    public void broadcastAlerts() {
        try {
            var alerts = dashboardService.getCriticalAlerts();
            messagingTemplate.convertAndSend("/topic/dashboard/alerts", alerts);
            log.debug("Broadcasted {} alerts to dashboard subscribers", alerts.size());
        } catch (Exception e) {
            log.error("Error broadcasting alerts", e);
        }
    }

    @Scheduled(fixedRate = 300000) // Every 5 minutes
    public void broadcastOverviewUpdate() {
        try {
            // Evict cache to force fresh data
            evictSpecificCache("dashboardOverview");

            var overview = dashboardService.getOverviewMetrics();
            messagingTemplate.convertAndSend("/topic/dashboard/overview", overview);
            log.debug("Broadcasted overview update to dashboard subscribers - Total doctors: {}, hospitals: {}",
                     overview.getTotalDoctors(), overview.getTotalHospitals());
        } catch (Exception e) {
            log.error("Error broadcasting overview update", e);
        }
    }

    @Scheduled(fixedRate = 120000) // Every 2 minutes - refresh blood bank data
    public void refreshBloodBankData() {
        try {
            evictSpecificCache("bloodBankData");
            var bloodData = dashboardService.getBloodBankStatus();
            messagingTemplate.convertAndSend("/topic/dashboard/bloodbank", bloodData);
            log.debug("Refreshed and broadcasted blood bank data");
        } catch (Exception e) {
            log.error("Error refreshing blood bank data", e);
        }
    }

    @Scheduled(cron = "0 0 * * * *") // Every hour
    public void refreshCacheStatistics() {
        try {
            log.info("=== Caffeine Cache Statistics ===");
            cacheManager.getCacheNames().forEach(cacheName -> {
                var cache = cacheManager.getCache(cacheName);
                if (cache != null && cache.getNativeCache() instanceof com.github.benmanes.caffeine.cache.Cache) {
                    var caffeineCache = (com.github.benmanes.caffeine.cache.Cache<?, ?>) cache.getNativeCache();
                    var stats = caffeineCache.stats();
                    log.info("Cache '{}' - Size: {}, Hit Rate: {:.2f}%, Evictions: {}",
                            cacheName,
                            caffeineCache.estimatedSize(),
                            stats.hitRate() * 100,
                            stats.evictionCount());
                }
            });
        } catch (Exception e) {
            log.error("Error logging cache statistics", e);
        }
    }

    @Scheduled(fixedRate = 180000) // Every 3 minutes - refresh activities
    public void refreshRecentActivities() {
        try {
            evictSpecificCache("recentActivities");
            var activities = dashboardService.getRecentActivities(20);
            messagingTemplate.convertAndSend("/topic/dashboard/activities", activities);
            log.debug("Refreshed and broadcasted {} recent activities", activities.size());
        } catch (Exception e) {
            log.error("Error refreshing recent activities", e);
        }
    }

    private void evictSpecificCache(String cacheName) {
        try {
            var cache = cacheManager.getCache(cacheName);
            if (cache != null) {
                cache.clear();
                log.debug("Evicted cache: {}", cacheName);
            }
        } catch (Exception e) {
            log.warn("Failed to evict cache: {}", cacheName, e);
        }
    }
}