package com.ciphertext.opencarebackend.modules.blood.service;
import com.ciphertext.opencarebackend.modules.blood.dto.filter.BloodBankFilter;
import com.ciphertext.opencarebackend.entity.BloodBank;
import com.ciphertext.opencarebackend.entity.BloodInventory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BloodBankService {

    Page<BloodBank> getPaginatedDataWithFilters(BloodBankFilter filter, Pageable pageable);

    List<BloodBank> getAllActiveBloodBanks();

    BloodBank getBloodBankById(Integer id);

    BloodBank createBloodBank(BloodBank bloodBank);

    BloodBank updateBloodBank(BloodBank bloodBank, Integer id);

    void deleteBloodBankById(Integer id);

    List<BloodInventory> getBloodBankInventory(Integer bloodBankId);

    List<BloodInventory> getAvailableInventory(Integer bloodBankId);
}
