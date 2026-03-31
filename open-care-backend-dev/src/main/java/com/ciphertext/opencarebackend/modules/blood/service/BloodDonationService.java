package com.ciphertext.opencarebackend.modules.blood.service;
import com.ciphertext.opencarebackend.modules.blood.dto.filter.BloodDonationFilter;
import com.ciphertext.opencarebackend.modules.blood.dto.response.BloodDonationResponse;
import com.ciphertext.opencarebackend.entity.BloodDonation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BloodDonationService {
    Page<BloodDonation> getPaginatedDataWithFilters(BloodDonationFilter filter, Pageable pagingSort);

    List<BloodDonation> getAllBloodDonation();

    BloodDonation getBloodDonationById(Long id);

    BloodDonation createBloodDonation(BloodDonation bloodDonation);

    BloodDonation updateBloodDonationById(BloodDonation bloodDonation, Long id);

    void deleteBloodDonationById(Long id);

    List<BloodDonationResponse> getBloodDonationsByProfileId(Long id);
}
