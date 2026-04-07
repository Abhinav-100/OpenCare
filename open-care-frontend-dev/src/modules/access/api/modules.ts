import { apiGet, apiPost } from "@/shared/utils/api-client";
import {
  clearModuleSession,
  getModuleAuthHeader,
  saveModuleSession,
} from "@/shared/utils/module-auth-client";
import type {
  BillResponse,
  DoctorLabTestRequest,
  DoctorLabTestResponse,
  GenerateModuleBillRequest,
  LabReportResponse,
  LabReportUploadRequest,
  ModuleAuthResponse,
  ModuleLoginRequest,
  ModuleOverviewResponse,
  ModuleRegisterRequest,
  PaymentTrackResponse,
  RecordModulePaymentRequest,
} from "@/shared/types/modules";

function requireModuleAuthHeaders(): Record<string, string> {
  const headers = getModuleAuthHeader();
  if (!headers.Authorization) {
    throw new Error("Module session not found. Please login to module auth.");
  }

  return headers;
}

// API flow: This module wraps backend endpoints and returns typed data for UI/hooks.
export async function moduleRegister(
  payload: ModuleRegisterRequest
): Promise<ModuleAuthResponse> {
  const response = await apiPost<ModuleAuthResponse>(
    "/modules/auth/register",
    payload,
    { includeAuth: false }
  );

  if (!response.ok || !response.data) {
    throw new Error(response.error || "Module registration failed");
  }

  saveModuleSession(response.data);
  return response.data;
}

export async function moduleLogin(
  payload: ModuleLoginRequest
): Promise<ModuleAuthResponse> {
  const response = await apiPost<ModuleAuthResponse>(
    "/modules/auth/login",
    payload,
    { includeAuth: false }
  );

  if (!response.ok || !response.data) {
    throw new Error(response.error || "Module login failed");
  }

  saveModuleSession(response.data);
  return response.data;
}

export function moduleLogout(): void {
  clearModuleSession();
}

export async function fetchModuleAuthOverview(): Promise<ModuleOverviewResponse> {
  const response = await apiGet<ModuleOverviewResponse>("/modules/auth/overview", {
    includeAuth: false,
  });

  if (!response.ok || !response.data) {
    throw new Error(response.error || "Failed to fetch module auth overview");
  }

  return response.data;
}

export async function generateModuleBill(
  payload: GenerateModuleBillRequest
): Promise<BillResponse> {
  const response = await apiPost<BillResponse>(
    "/modules/billing/bills/generate",
    payload,
    {
      includeAuth: false,
      headers: requireModuleAuthHeaders(),
    }
  );

  if (!response.ok || !response.data) {
    throw new Error(response.error || "Failed to generate module bill");
  }

  return response.data;
}

export async function recordModulePayment(
  payload: RecordModulePaymentRequest
): Promise<PaymentTrackResponse> {
  const response = await apiPost<PaymentTrackResponse>(
    "/modules/billing/payments/record",
    payload,
    {
      includeAuth: false,
      headers: requireModuleAuthHeaders(),
    }
  );

  if (!response.ok || !response.data) {
    throw new Error(response.error || "Failed to record module payment");
  }

  return response.data;
}

export async function fetchModuleBillPayments(
  billId: number
): Promise<PaymentTrackResponse[]> {
  const response = await apiGet<PaymentTrackResponse[]>(
    `/modules/billing/bills/${billId}/payments`,
    {
      includeAuth: false,
      headers: requireModuleAuthHeaders(),
    }
  );

  if (!response.ok || !response.data) {
    throw new Error(response.error || "Failed to fetch module bill payments");
  }

  return response.data;
}

export async function fetchBillingModuleOverview(): Promise<ModuleOverviewResponse> {
  const response = await apiGet<ModuleOverviewResponse>("/modules/billing/overview", {
    includeAuth: false,
    headers: requireModuleAuthHeaders(),
  });

  if (!response.ok || !response.data) {
    throw new Error(response.error || "Failed to fetch billing module overview");
  }

  return response.data;
}

export async function fetchBillingAdminSummary(): Promise<Record<string, unknown>> {
  const response = await apiGet<Record<string, unknown>>(
    "/modules/billing/admin/summary",
    {
      includeAuth: false,
      headers: requireModuleAuthHeaders(),
    }
  );

  if (!response.ok || !response.data) {
    throw new Error(response.error || "Failed to fetch billing admin summary");
  }

  return response.data;
}

export async function requestDoctorLabTest(
  payload: DoctorLabTestRequest
): Promise<DoctorLabTestResponse> {
  const response = await apiPost<DoctorLabTestResponse>(
    "/modules/doctor/lab-tests/request",
    payload,
    {
      includeAuth: false,
      headers: requireModuleAuthHeaders(),
    }
  );

  if (!response.ok || !response.data) {
    throw new Error(response.error || "Failed to request doctor lab test");
  }

  return response.data;
}

export async function fetchDoctorModuleOverview(): Promise<ModuleOverviewResponse> {
  const response = await apiGet<ModuleOverviewResponse>("/modules/doctor/overview", {
    includeAuth: false,
    headers: requireModuleAuthHeaders(),
  });

  if (!response.ok || !response.data) {
    throw new Error(response.error || "Failed to fetch doctor module overview");
  }

  return response.data;
}

export async function uploadLabReport(
  payload: LabReportUploadRequest
): Promise<LabReportResponse> {
  const response = await apiPost<LabReportResponse>(
    "/modules/lab/reports/upload",
    payload,
    {
      includeAuth: false,
      headers: requireModuleAuthHeaders(),
    }
  );

  if (!response.ok || !response.data) {
    throw new Error(response.error || "Failed to upload lab report");
  }

  return response.data;
}

export async function fetchLabModuleOverview(): Promise<ModuleOverviewResponse> {
  const response = await apiGet<ModuleOverviewResponse>("/modules/lab/overview", {
    includeAuth: false,
    headers: requireModuleAuthHeaders(),
  });

  if (!response.ok || !response.data) {
    throw new Error(response.error || "Failed to fetch lab module overview");
  }

  return response.data;
}
