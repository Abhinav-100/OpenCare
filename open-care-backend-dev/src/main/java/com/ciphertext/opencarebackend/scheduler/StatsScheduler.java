package com.ciphertext.opencarebackend.scheduler;

import com.ciphertext.opencarebackend.modules.provider.service.MedicalSpecialityService;
import com.ciphertext.opencarebackend.modules.provider.service.MedicalTestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class StatsScheduler {
    private final MedicalSpecialityService medicalSpecialityService;
    private final MedicalTestService medicalTestService;

    @Scheduled(cron = "0 0 2 * * *")
    public void updateMedicalSpecialityStatsNightly() {
        log.info("Nightly update: medical speciality stats started");
        medicalSpecialityService.refreshAll();
        log.info("Nightly update: medical speciality stats finished");
    }

    @Scheduled(cron = "0 10 2 * * *")
    public void updateMedicalTestStatsNightly() {
        log.info("Nightly update: medical test stats started");
        medicalTestService.refreshAll();
        log.info("Nightly update: medical test stats finished");
    }

}
