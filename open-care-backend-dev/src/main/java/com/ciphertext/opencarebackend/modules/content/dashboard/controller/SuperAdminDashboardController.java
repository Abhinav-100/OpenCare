package com.ciphertext.opencarebackend.modules.content.dashboard.controller;

import com.ciphertext.opencarebackend.modules.content.dashboard.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import com.ciphertext.opencarebackend.modules.content.dashboard.dto.ActivityLogDto;
import com.ciphertext.opencarebackend.modules.content.dashboard.dto.AnalyticsDataDto;
import com.ciphertext.opencarebackend.modules.content.dashboard.dto.BloodBankDataDto;
import com.ciphertext.opencarebackend.modules.content.dashboard.dto.DashboardOverviewResponse;
import com.ciphertext.opencarebackend.modules.content.dashboard.dto.RealTimeStatsDto;
import com.ciphertext.opencarebackend.modules.content.dashboard.dto.RegistrationTrendsDto;
import com.ciphertext.opencarebackend.modules.content.dashboard.dto.SystemAlertDto;

@RestController
@RequestMapping("/api/superadmin/dashboard")
@RequiredArgsConstructor
@PreAuthorize("hasRole('SUPER_ADMIN')")
@CrossOrigin(origins = "*")
public class SuperAdminDashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/overview")
    public ResponseEntity<DashboardOverviewResponse> getDashboardOverview() {
        DashboardOverviewResponse overview = dashboardService.getOverviewMetrics();
        return ResponseEntity.ok(overview);
    }

    @GetMapping("/blood-bank")
    public ResponseEntity<BloodBankDataDto> getBloodBankData() {
        BloodBankDataDto bloodData = dashboardService.getBloodBankStatus();
        return ResponseEntity.ok(bloodData);
    }

    @GetMapping("/registration-trends")
    public ResponseEntity<RegistrationTrendsDto> getRegistrationTrends(
            @RequestParam(defaultValue = "6") int months) {
        RegistrationTrendsDto trends = dashboardService.getRegistrationTrends(months);
        return ResponseEntity.ok(trends);
    }

    @GetMapping("/alerts")
    public ResponseEntity<List<SystemAlertDto>> getCriticalAlerts() {
        List<SystemAlertDto> alerts = dashboardService.getCriticalAlerts();
        return ResponseEntity.ok(alerts);
    }

    @GetMapping("/recent-activities")
    public ResponseEntity<List<ActivityLogDto>> getRecentActivities(
            @RequestParam(defaultValue = "20") int limit) {
        List<ActivityLogDto> activities = dashboardService.getRecentActivities(limit);
        return ResponseEntity.ok(activities);
    }

    @GetMapping("/analytics")
    public ResponseEntity<AnalyticsDataDto> getAnalytics(
            @RequestParam(defaultValue = "7") int days) {
        AnalyticsDataDto analytics = dashboardService.getAnalytics(days);
        return ResponseEntity.ok(analytics);
    }

    @GetMapping("/real-time-stats")
    public ResponseEntity<RealTimeStatsDto> getRealTimeStats() {
        RealTimeStatsDto stats = dashboardService.getRealTimeStats();
        return ResponseEntity.ok(stats);
    }

    @PostMapping("/refresh-cache")
    public ResponseEntity<String> refreshCache() {
        dashboardService.refreshCache();
        return ResponseEntity.ok("Cache refreshed successfully");
    }
}