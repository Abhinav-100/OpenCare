package com.ciphertext.opencarebackend.modules.clinical.service.impl;

import com.ciphertext.opencarebackend.exception.BadRequestException;
import com.ciphertext.opencarebackend.exception.ResourceNotFoundException;
import com.ciphertext.opencarebackend.modules.clinical.repository.HealthConditionRepository;
import com.ciphertext.opencarebackend.modules.clinical.repository.HealthEncounterRepository;
import com.ciphertext.opencarebackend.modules.clinical.repository.HealthMedicationRepository;
import com.ciphertext.opencarebackend.modules.clinical.service.HealthRecordsService;
import com.ciphertext.opencarebackend.modules.provider.repository.DoctorRepository;
import com.ciphertext.opencarebackend.modules.user.repository.ProfileRepository;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import com.ciphertext.opencarebackend.entity.Doctor;
import com.ciphertext.opencarebackend.entity.HealthCondition;
import com.ciphertext.opencarebackend.entity.HealthEncounter;
import com.ciphertext.opencarebackend.entity.HealthMedication;
import com.ciphertext.opencarebackend.entity.Profile;




@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class HealthRecordsServiceImpl implements HealthRecordsService {

    private final HealthEncounterRepository encounterRepository;
    private final HealthConditionRepository conditionRepository;
    private final HealthMedicationRepository medicationRepository;
    private final ProfileRepository profileRepository;
    private final DoctorRepository doctorRepository;

    // ==================== ENCOUNTERS ====================

    @Override
    @Transactional(readOnly = true)
    public List<HealthEncounter> getMyEncounters(String keycloakUserId) {
        return encounterRepository.findByKeycloakUserId(keycloakUserId);
    }

    @Override
    @Transactional(readOnly = true)
    public HealthEncounter getEncounterById(Long id) {
        return encounterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Encounter not found with id: " + id));
    }

    @Override
    public HealthEncounter createEncounter(HealthEncounter encounter, String keycloakUserId) {
        Profile profile = profileRepository.findByKeycloakUserId(keycloakUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User profile not found"));
        encounter.setProfile(profile);

        if (encounter.getEncounterType() == null) {
            throw new BadRequestException("Encounter type is required");
        }
        if (encounter.getVisitDate() == null) {
            throw new BadRequestException("Visit date is required");
        }

        hydrateDoctor(encounter);
        return encounterRepository.save(encounter);
    }

    @Override
    public HealthEncounter updateEncounter(HealthEncounter encounter, Long id) {
        HealthEncounter existing = getEncounterById(id);

        existing.setEncounterType(encounter.getEncounterType());
        existing.setVisitDate(encounter.getVisitDate());
        existing.setLocation(encounter.getLocation());
        existing.setDiagnosisSummary(encounter.getDiagnosisSummary());
        existing.setProcedures(encounter.getProcedures());
        existing.setFollowUpRequired(encounter.getFollowUpRequired());
        existing.setFollowUpDate(encounter.getFollowUpDate());
        existing.setReportLink(encounter.getReportLink());
        existing.setNotes(encounter.getNotes());

        hydrateDoctor(existing);
        if (encounter.getDoctor() != null) {
            existing.setDoctor(encounter.getDoctor());
        }

        return encounterRepository.save(existing);
    }

    @Override
    public void deleteEncounter(Long id) {
        HealthEncounter encounter = getEncounterById(id);
        encounterRepository.delete(encounter);
    }

    // ==================== CONDITIONS ====================

    @Override
    @Transactional(readOnly = true)
    public List<HealthCondition> getMyConditions(String keycloakUserId) {
        return conditionRepository.findByKeycloakUserId(keycloakUserId);
    }

    @Override
    @Transactional(readOnly = true)
    public HealthCondition getConditionById(Long id) {
        return conditionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Condition not found with id: " + id));
    }

    @Override
    public HealthCondition createCondition(HealthCondition condition, String keycloakUserId) {
        Profile profile = profileRepository.findByKeycloakUserId(keycloakUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User profile not found"));
        condition.setProfile(profile);

        if (condition.getConditionType() == null) {
            throw new BadRequestException("Condition type is required");
        }
        if (!StringUtils.hasText(condition.getName())) {
            throw new BadRequestException("Condition name is required");
        }

        return conditionRepository.save(condition);
    }

    @Override
    public HealthCondition updateCondition(HealthCondition condition, Long id) {
        HealthCondition existing = getConditionById(id);

        existing.setConditionType(condition.getConditionType());
        existing.setName(condition.getName());
        existing.setSeverity(condition.getSeverity());
        existing.setReaction(condition.getReaction());
        existing.setDiagnosisDate(condition.getDiagnosisDate());
        existing.setStatus(condition.getStatus());
        existing.setIcdCode(condition.getIcdCode());
        existing.setNotes(condition.getNotes());

        return conditionRepository.save(existing);
    }

    @Override
    public void deleteCondition(Long id) {
        HealthCondition condition = getConditionById(id);
        conditionRepository.delete(condition);
    }

    // ==================== MEDICATIONS ====================

    @Override
    @Transactional(readOnly = true)
    public List<HealthMedication> getMyMedications(String keycloakUserId) {
        return medicationRepository.findByKeycloakUserId(keycloakUserId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<HealthMedication> getMyActiveMedications(String keycloakUserId) {
        return medicationRepository.findActiveByKeycloakUserId(keycloakUserId);
    }

    @Override
    @Transactional(readOnly = true)
    public HealthMedication getMedicationById(Long id) {
        return medicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Medication not found with id: " + id));
    }

    @Override
    public HealthMedication createMedication(HealthMedication medication, String keycloakUserId) {
        Profile profile = profileRepository.findByKeycloakUserId(keycloakUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User profile not found"));
        medication.setProfile(profile);

        if (!StringUtils.hasText(medication.getMedicationName())) {
            throw new BadRequestException("Medication name is required");
        }
        if (medication.getStartDate() == null) {
            throw new BadRequestException("Start date is required");
        }

        hydratePrescriber(medication);
        return medicationRepository.save(medication);
    }

    @Override
    public HealthMedication updateMedication(HealthMedication medication, Long id) {
        HealthMedication existing = getMedicationById(id);

        existing.setMedicationName(medication.getMedicationName());
        existing.setRxnormCode(medication.getRxnormCode());
        existing.setDosageAmount(medication.getDosageAmount());
        existing.setDosageUnit(medication.getDosageUnit());
        existing.setFrequency(medication.getFrequency());
        existing.setStartDate(medication.getStartDate());
        existing.setEndDate(medication.getEndDate());
        existing.setPrescriptionNotes(medication.getPrescriptionNotes());
        existing.setIsActive(medication.getIsActive());
        existing.setLastTaken(medication.getLastTaken());

        hydratePrescriber(medication);
        if (medication.getPrescribedBy() != null) {
            existing.setPrescribedBy(medication.getPrescribedBy());
        }

        return medicationRepository.save(existing);
    }

    @Override
    public void deleteMedication(Long id) {
        HealthMedication medication = getMedicationById(id);
        medicationRepository.delete(medication);
    }

    // ==================== HELPERS ====================

    private void hydrateDoctor(HealthEncounter encounter) {
        if (encounter.getDoctor() != null && encounter.getDoctor().getId() != null) {
            Doctor doctor = doctorRepository.findById(encounter.getDoctor().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Doctor not found"));
            encounter.setDoctor(doctor);
        }
    }

    private void hydratePrescriber(HealthMedication medication) {
        if (medication.getPrescribedBy() != null && medication.getPrescribedBy().getId() != null) {
            Doctor doctor = doctorRepository.findById(medication.getPrescribedBy().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Doctor not found"));
            medication.setPrescribedBy(doctor);
        }
    }
}