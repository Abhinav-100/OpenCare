package com.ciphertext.opencarebackend.modules.provider.service;
import com.ciphertext.opencarebackend.modules.provider.dto.filter.NurseFilter;
import com.ciphertext.opencarebackend.entity.Nurse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.List;


public interface NurseService {
    public Page<Nurse> getPaginatedDataWithFilters(NurseFilter nurseFilter, Pageable pagingSort);

    Nurse getNurseById(Long id);

    Nurse createNurse(Nurse nurse);

    Nurse updateNurse(Nurse nurse, Long id);

    ResponseEntity<Object> deleteNurseById(Long id);

    List<Nurse> getAllNurses();
    String createNurseUser(Long nurseId);

}
