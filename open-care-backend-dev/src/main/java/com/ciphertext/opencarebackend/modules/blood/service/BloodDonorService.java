package com.ciphertext.opencarebackend.modules.blood.service;
import com.ciphertext.opencarebackend.modules.blood.dto.filter.BloodDonorFilter;
import com.ciphertext.opencarebackend.entity.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BloodDonorService {
    Page<Profile> getPaginatedBloodDonorsWithFilters(BloodDonorFilter filter, Pageable pageable);
}
