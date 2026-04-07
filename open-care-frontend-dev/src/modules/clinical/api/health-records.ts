import { apiGet, apiPost, apiPut, apiDelete } from "@/shared/utils/api-client";
import {
  HealthEncounter,
  HealthCondition,
  HealthMedication,
  CreateEncounterRequest,
  CreateConditionRequest,
  CreateMedicationRequest,
} from "@/shared/types/health-records";

// ==================== ENCOUNTERS ====================

// API flow: This module wraps backend endpoints and returns typed data for UI/hooks.
export async function fetchMyEncounters(): Promise<HealthEncounter[]> {
  const response = await apiGet<HealthEncounter[]>(
    "/health-records/encounters/self"
  );
  return response.data || [];
}

export async function fetchEncounterById(id: number): Promise<HealthEncounter> {
  const response = await apiGet<HealthEncounter>(
    `/health-records/encounters/${id}`
  );
  if (!response.data) throw new Error("Encounter not found");
  return response.data;
}

export async function createEncounter(
  data: CreateEncounterRequest
): Promise<HealthEncounter> {
  const response = await apiPost<HealthEncounter>(
    "/health-records/encounters",
    data
  );
  if (!response.data) throw new Error("Failed to create encounter");
  return response.data;
}

export async function updateEncounter(
  id: number,
  data: CreateEncounterRequest
): Promise<HealthEncounter> {
  const response = await apiPut<HealthEncounter>(
    `/health-records/encounters/${id}`,
    data
  );
  if (!response.data) throw new Error("Failed to update encounter");
  return response.data;
}

export async function deleteEncounter(id: number): Promise<void> {
  await apiDelete(`/health-records/encounters/${id}`);
}

// ==================== CONDITIONS ====================

export async function fetchMyConditions(): Promise<HealthCondition[]> {
  const response = await apiGet<HealthCondition[]>(
    "/health-records/conditions/self"
  );
  return response.data || [];
}

export async function fetchConditionById(id: number): Promise<HealthCondition> {
  const response = await apiGet<HealthCondition>(
    `/health-records/conditions/${id}`
  );
  if (!response.data) throw new Error("Condition not found");
  return response.data;
}

export async function createCondition(
  data: CreateConditionRequest
): Promise<HealthCondition> {
  const response = await apiPost<HealthCondition>(
    "/health-records/conditions",
    data
  );
  if (!response.data) throw new Error("Failed to create condition");
  return response.data;
}

export async function updateCondition(
  id: number,
  data: CreateConditionRequest
): Promise<HealthCondition> {
  const response = await apiPut<HealthCondition>(
    `/health-records/conditions/${id}`,
    data
  );
  if (!response.data) throw new Error("Failed to update condition");
  return response.data;
}

export async function deleteCondition(id: number): Promise<void> {
  await apiDelete(`/health-records/conditions/${id}`);
}

// ==================== MEDICATIONS ====================

export async function fetchMyMedications(): Promise<HealthMedication[]> {
  const response = await apiGet<HealthMedication[]>(
    "/health-records/medications/self"
  );
  return response.data || [];
}

export async function fetchMyActiveMedications(): Promise<HealthMedication[]> {
  const response = await apiGet<HealthMedication[]>(
    "/health-records/medications/self/active"
  );
  return response.data || [];
}

export async function fetchMedicationById(
  id: number
): Promise<HealthMedication> {
  const response = await apiGet<HealthMedication>(
    `/health-records/medications/${id}`
  );
  if (!response.data) throw new Error("Medication not found");
  return response.data;
}

export async function createMedication(
  data: CreateMedicationRequest
): Promise<HealthMedication> {
  const response = await apiPost<HealthMedication>(
    "/health-records/medications",
    data
  );
  if (!response.data) throw new Error("Failed to create medication");
  return response.data;
}

export async function updateMedication(
  id: number,
  data: CreateMedicationRequest
): Promise<HealthMedication> {
  const response = await apiPut<HealthMedication>(
    `/health-records/medications/${id}`,
    data
  );
  if (!response.data) throw new Error("Failed to update medication");
  return response.data;
}

export async function deleteMedication(id: number): Promise<void> {
  await apiDelete(`/health-records/medications/${id}`);
}
