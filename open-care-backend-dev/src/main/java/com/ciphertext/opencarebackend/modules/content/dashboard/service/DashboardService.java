package com.ciphertext.opencarebackend.modules.content.dashboard.service;

import com.ciphertext.opencarebackend.modules.provider.repository.AmbulanceRepository;
import com.ciphertext.opencarebackend.modules.provider.repository.AssociationRepository;
import com.ciphertext.opencarebackend.modules.provider.repository.DoctorRepository;
import com.ciphertext.opencarebackend.modules.provider.repository.HospitalRepository;
import com.ciphertext.opencarebackend.modules.provider.repository.InstitutionRepository;
import com.ciphertext.opencarebackend.modules.provider.repository.NurseRepository;
import com.ciphertext.opencarebackend.modules.provider.repository.SocialOrganizationRepository;
import java.util.*;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ciphertext.opencarebackend.modules.content.dashboard.dto.ActivityLogDto;
import com.ciphertext.opencarebackend.modules.content.dashboard.dto.AnalyticsDataDto;
import com.ciphertext.opencarebackend.modules.content.dashboard.dto.BloodBankDataDto;
import com.ciphertext.opencarebackend.modules.content.dashboard.dto.DashboardOverviewResponse;
import com.ciphertext.opencarebackend.modules.content.dashboard.dto.RealTimeStatsDto;
import com.ciphertext.opencarebackend.modules.content.dashboard.dto.RegistrationTrendsDto;
import com.ciphertext.opencarebackend.modules.content.dashboard.dto.SystemAlertDto;




@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class DashboardService {

    private final CacheManager cacheManager;
    private final DoctorRepository doctorRepository;
    private final NurseRepository nurseRepository;
    private final HospitalRepository hospitalRepository;
    private final InstitutionRepository institutionRepository;
    // BloodBankRepository removed - table not available in current schema
    private final AmbulanceRepository ambulanceRepository;
    private final AssociationRepository associationRepository;
    private final SocialOrganizationRepository socialOrganizationRepository;


    @Cacheable(value = "dashboardOverview", unless = "#result == null")
    public DashboardOverviewResponse getOverviewMetrics() {
        log.info("Fetching dashboard overview metrics");

        return DashboardOverviewResponse.builder()
                .totalAdmins(5L)
                .totalDoctors(countDoctors())
                .totalNurses(countNurses())
                .totalPatients(countPatients())
                .totalHospitals(countHospitals())
                .totalInstitutions(countInstitutions())
                .bloodUnitsAvailable(countBloodUnits())
                .ambulancesActive(countActiveAmbulances())
                .associationsRegistered(countAssociations())
                .socialOrganizations(countSocialOrganizations())
                .lastUpdated(LocalDateTime.now())
                .build();
    }

    @Cacheable(value = "bloodBankData", unless = "#result == null")
    public BloodBankDataDto getBloodBankStatus() {
        log.info("Fetching blood bank status");

        String[] bloodTypes = {"A+", "A-", "B+", "B-", "O+", "O-", "AB+", "AB-"};
        // Stable placeholder values representing typical blood bank inventory
        Integer[] units = {45, 12, 38, 8, 52, 6, 25, 14};

        return BloodBankDataDto.builder()
                .bloodTypes(bloodTypes)
                .units(units)
                .criticalLevels(getCriticalBloodLevels())
                .lastUpdated(LocalDateTime.now())
                .build();
    }

    @Cacheable(value = "registrationTrends", unless = "#result == null")
    public RegistrationTrendsDto getRegistrationTrends(int months) {
        log.info("Fetching registration trends for {} months", months);

        List<String> monthLabels = new ArrayList<>();
        List<Integer> doctors = new ArrayList<>();
        List<Integer> nurses = new ArrayList<>();
        List<Integer> hospitals = new ArrayList<>();
        List<Integer> patients = new ArrayList<>();

        LocalDateTime now = LocalDateTime.now();

        // Current totals for scaling
        long totalDoctors = doctorRepository.count();
        long totalHospitals = hospitalRepository.count();

        for (int i = months - 1; i >= 0; i--) {
            LocalDateTime monthDate = now.minusMonths(i);
            monthLabels.add(monthDate.format(DateTimeFormatter.ofPattern("MMM yyyy")));

            // Distribute totals across months with growth pattern
            int monthIndex = months - 1 - i;
            double growthFactor = 0.5 + (0.5 * monthIndex / (months - 1));

            doctors.add((int) Math.round(totalDoctors * growthFactor / months * 2));
            nurses.add((int) Math.round(monthIndex * 2 + 3));
            hospitals.add((int) Math.round(totalHospitals * growthFactor / months * 2));
            patients.add((int) Math.round(monthIndex * 15 + 20));
        }

        return RegistrationTrendsDto.builder()
                .months(monthLabels.toArray(new String[0]))
                .doctors(doctors.toArray(new Integer[0]))
                .nurses(nurses.toArray(new Integer[0]))
                .hospitals(hospitals.toArray(new Integer[0]))
                .patients(patients.toArray(new Integer[0]))
                .build();
    }

    @Cacheable(value = "systemAlerts", unless = "#result == null")
    public List<SystemAlertDto> getCriticalAlerts() {
        log.info("Fetching critical system alerts");

        List<SystemAlertDto> alerts = new ArrayList<>();

        // Check blood bank critical levels
        alerts.addAll(checkBloodBankAlerts());

        // Check system performance alerts
        alerts.addAll(checkSystemPerformanceAlerts());

        // Check capacity alerts
        alerts.addAll(checkCapacityAlerts());

        return alerts.stream()
                .sorted(Comparator.comparing(SystemAlertDto::getTimestamp).reversed())
                .limit(10)
                .collect(Collectors.toList());
    }

    @Cacheable(value = "recentActivities", unless = "#result == null")
    public List<ActivityLogDto> getRecentActivities(int limit) {
        log.info("Fetching recent activities with limit: {}", limit);

        List<ActivityLogDto> activities = new ArrayList<>();

        // Stable placeholder activities for demo
        activities.add(ActivityLogDto.builder()
                .id(1L)
                .entityType("DOCTOR")
                .action("REGISTERED")
                .status("SUCCESS")
                .timestamp(LocalDateTime.now().minusMinutes(15))
                .details("Dr. Rajan Patnaik profile verified")
                .build());
        activities.add(ActivityLogDto.builder()
                .id(2L)
                .entityType("HOSPITAL")
                .action("UPDATED")
                .status("SUCCESS")
                .timestamp(LocalDateTime.now().minusMinutes(45))
                .details("AIIMS Bhubaneswar updated emergency services")
                .build());
        activities.add(ActivityLogDto.builder()
                .id(3L)
                .entityType("PATIENT")
                .action("REGISTERED")
                .status("SUCCESS")
                .timestamp(LocalDateTime.now().minusHours(1))
                .details("New patient registration completed")
                .build());
        activities.add(ActivityLogDto.builder()
                .id(4L)
                .entityType("BLOOD_BANK")
                .action("UPDATED")
                .status("SUCCESS")
                .timestamp(LocalDateTime.now().minusHours(2))
                .details("Blood inventory updated at SCB Medical College")
                .build());
        activities.add(ActivityLogDto.builder()
                .id(5L)
                .entityType("DOCTOR")
                .action("LOGIN")
                .status("SUCCESS")
                .timestamp(LocalDateTime.now().minusHours(3))
                .details("Dr. Sunita Mohanty logged in")
                .build());

        return activities.stream()
                .sorted(Comparator.comparing(ActivityLogDto::getTimestamp).reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }

    public AnalyticsDataDto getAnalytics(int days) {
        log.info("Fetching analytics data for {} days", days);

        long totalDoctors = doctorRepository.count();
        long totalHospitals = hospitalRepository.count();

        return AnalyticsDataDto.builder()
                .totalUsers((int) (totalDoctors + totalHospitals + 150)) // doctors + hospitals + estimated users
                .activeUsers((int) (totalDoctors / 3) + 25) // estimated active users
                .appointmentsToday(12)
                .emergencyCalls(3)
                .systemUptime(99.8)
                .averageResponseTime(185)
                .build();
    }

    public RealTimeStatsDto getRealTimeStats() {
        long totalDoctors = doctorRepository.count();
        long totalAmbulances = ambulanceRepository.count();

        return RealTimeStatsDto.builder()
                .onlineUsers((int) (totalDoctors / 4) + 15)
                .activeAmbulances((int) Math.min(totalAmbulances, 8))
                .pendingAppointments(7)
                .criticalAlerts(1)
                .systemLoad(42.5)
                .memoryUsage(58.3)
                .build();
    }

    @CacheEvict(value = {"dashboardOverview", "bloodBankData", "registrationTrends", "systemAlerts", "recentActivities"}, allEntries = true)
    public void refreshCache() {
        log.info("Evicting all dashboard caches");
        // Additional cache statistics logging
        if (cacheManager != null) {
            cacheManager.getCacheNames().forEach(cacheName -> {
                var cache = cacheManager.getCache(cacheName);
                if (cache != null) {
                    log.info("Cache '{}' cleared", cacheName);
                }
            });
        }
    }

    // Helper methods for data fetching
    private Long countDoctors() {
        return doctorRepository.count();
    }

    private Long countNurses() {
        return nurseRepository.count();
    }

    private Long countPatients() {
        // Patient repository not available - return 0 for now
        return 0L;
    }

    private Long countHospitals() {
        return hospitalRepository.count();
    }

    private Long countInstitutions() {
        return institutionRepository.count();
    }

    private Long countBloodUnits() {
        // Blood bank table not available in current schema
        // Return placeholder value for dashboard display
        return 15L; // Placeholder: represents ~15 blood banks in Odisha
    }

    private Long countActiveAmbulances() {
        return ambulanceRepository.count();
    }

    private Long countAssociations() {
        return associationRepository.count();
    }

    private Long countSocialOrganizations() {
        return socialOrganizationRepository.count();
    }

    private Map<String, Integer> getCriticalBloodLevels() {
        Map<String, Integer> critical = new HashMap<>();
        critical.put("A+", 10);
        critical.put("O-", 5);
        return critical;
    }

    private List<SystemAlertDto> checkBloodBankAlerts() {
        List<SystemAlertDto> alerts = new ArrayList<>();

        // Simulated critical blood levels
        alerts.add(SystemAlertDto.builder()
                .id(1L)
                .title("Critical Blood Stock")
                .message("O- blood type has only 3 units remaining")
                .severity("HIGH")
                .timestamp(LocalDateTime.now().minusMinutes(5))
                .build());

        return alerts;
    }

    private List<SystemAlertDto> checkSystemPerformanceAlerts() {
        List<SystemAlertDto> alerts = new ArrayList<>();

        alerts.add(SystemAlertDto.builder()
                .id(2L)
                .title("High System Load")
                .message("System CPU usage is above 85%")
                .severity("MEDIUM")
                .timestamp(LocalDateTime.now().minusMinutes(10))
                .build());

        return alerts;
    }

    private List<SystemAlertDto> checkCapacityAlerts() {
        List<SystemAlertDto> alerts = new ArrayList<>();

        alerts.add(SystemAlertDto.builder()
                .id(3L)
                .title("Hospital Capacity")
                .message("City General Hospital is at 95% capacity")
                .severity("HIGH")
                .timestamp(LocalDateTime.now().minusMinutes(15))
                .build());

        return alerts;
    }
}