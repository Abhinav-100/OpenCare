package com.ciphertext.opencarebackend.modules.clinical.service;

import com.ciphertext.opencarebackend.entity.HealthCondition;
import com.ciphertext.opencarebackend.entity.HealthEncounter;
import com.ciphertext.opencarebackend.entity.HealthMedication;

import java.util.List;

public interface HealthRecordsService {

    // Encounters
    List<HealthEncounter> getMyEncounters(String keycloakUserId);
    HealthEncounter getEncounterById(Long id);
    HealthEncounter createEncounter(HealthEncounter encounter, String keycloakUserId);
    HealthEncounter updateEncounter(HealthEncounter encounter, Long id);
    void deleteEncounter(Long id);

    // Conditions
    List<HealthCondition> getMyConditions(String keycloakUserId);
    HealthCondition getConditionById(Long id);
    HealthCondition createCondition(HealthCondition condition, String keycloakUserId);
    HealthCondition updateCondition(HealthCondition condition, Long id);
    void deleteCondition(Long id);

    // Medications
    List<HealthMedication> getMyMedications(String keycloakUserId);
    List<HealthMedication> getMyActiveMedications(String keycloakUserId);
    HealthMedication getMedicationById(Long id);
    HealthMedication createMedication(HealthMedication medication, String keycloakUserId);
    HealthMedication updateMedication(HealthMedication medication, Long id);
    void deleteMedication(Long id);
}