// Health Records Types

// Enums
export type EncounterType =
  | "OPD"
  | "IPD"
  | "EMERGENCY"
  | "TELECONSULT"
  | "LAB_VISIT"
  | "FOLLOW_UP";

export type ConditionType = "ALLERGY" | "DIAGNOSIS" | "CHRONIC";

export type ConditionSeverity = "MILD" | "MODERATE" | "SEVERE";

export type ConditionStatus = "ACTIVE" | "RESOLVED" | "MANAGED";

// Health Encounter (doctor visits)
export interface HealthEncounter {
  id: number;
  encounterType: EncounterType;
  visitDate: string; // ISO date
  location?: string;
  diagnosisSummary?: string;
  procedures?: string;
  followUpRequired?: boolean;
  followUpDate?: string;
  reportLink?: string;
  notes?: string;
  doctor?: {
    id: number;
    name: string;
    specializations?: string[];
  };
  createdAt?: string;
  lastUpdated?: string;
}

export interface CreateEncounterRequest {
  encounterType: EncounterType;
  visitDate: string;
  location?: string;
  diagnosisSummary?: string;
  procedures?: string;
  followUpRequired?: boolean;
  followUpDate?: string;
  reportLink?: string;
  notes?: string;
  doctor?: { id: number };
}

// Health Condition (allergies, diagnoses, chronic conditions)
export interface HealthCondition {
  id: number;
  conditionType: ConditionType;
  name: string;
  severity?: ConditionSeverity;
  reaction?: string; // For allergies
  diagnosisDate?: string;
  status?: ConditionStatus;
  icdCode?: string;
  notes?: string;
  createdAt?: string;
  lastUpdated?: string;
}

export interface CreateConditionRequest {
  conditionType: ConditionType;
  name: string;
  severity?: ConditionSeverity;
  reaction?: string;
  diagnosisDate?: string;
  status?: ConditionStatus;
  icdCode?: string;
  notes?: string;
}

// Health Medication
export interface HealthMedication {
  id: number;
  medicationName: string;
  rxnormCode?: string;
  dosageAmount?: string;
  dosageUnit?: string;
  frequency?: string;
  startDate: string;
  endDate?: string;
  prescriptionNotes?: string;
  isActive?: boolean;
  lastTaken?: string;
  prescribedBy?: {
    id: number;
    name: string;
  };
  createdAt?: string;
}

export interface CreateMedicationRequest {
  medicationName: string;
  rxnormCode?: string;
  dosageAmount?: string;
  dosageUnit?: string;
  frequency?: string;
  startDate: string;
  endDate?: string;
  prescriptionNotes?: string;
  isActive?: boolean;
  prescribedBy?: { id: number };
}

// Tab types for the dashboard
export type HealthRecordsTab =
  | "vitals"
  | "encounters"
  | "conditions"
  | "medications";
