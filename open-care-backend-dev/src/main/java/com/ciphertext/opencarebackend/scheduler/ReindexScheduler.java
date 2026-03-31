package com.ciphertext.opencarebackend.scheduler;


import com.ciphertext.opencarebackend.modules.provider.service.elasticsearch.DoctorSearchService;
import com.ciphertext.opencarebackend.modules.provider.service.elasticsearch.HospitalSearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReindexScheduler {

    private final HospitalSearchService hospitalSearchService;
    private final DoctorSearchService doctorSearchService;

    // 02:00 AM daily (use 14 for 2 PM): "0 0 14 * * *"
    @Scheduled(cron = "0 0 2 * * *", zone = "Asia/Dhaka")
    public void reindexHospitalsNightly() {
        log.info("Nightly reindex: hospitals started");
        hospitalSearchService.indexAllHospitals(); // async on your existing taskExecutor
    }

    // Stagger to reduce ES load.
    @Scheduled(cron = "0 5 2 * * *", zone = "Asia/Dhaka")
    public void reindexDoctorsNightly() {
        log.info("Nightly reindex: doctors started");
        doctorSearchService.indexAllDoctors();
    }
}
