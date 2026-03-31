import { apiGet, buildUrl } from "@/shared/utils/api-client";
import type {
  BloodBank,
  BloodBankListResponse,
  BloodInventoryItem,
} from "@/shared/types/blood-banks";

export const fetchBloodBanks = async (
  params: Record<string, unknown> = {}
): Promise<BloodBankListResponse> => {
  const url = buildUrl("/blood-banks", params);
  const response = await apiGet<BloodBankListResponse>(url);
  if (!response.ok) throw new Error(response.error || "Failed to fetch blood banks");
  return response.data as BloodBankListResponse;
};

export const fetchBloodBankById = async (id: number): Promise<BloodBank> => {
  const response = await apiGet<BloodBank>(`/blood-banks/${id}`);
  if (!response.ok) throw new Error(response.error || "Failed to fetch blood bank");
  return response.data as BloodBank;
};

export const fetchBloodBankInventory = async (
  id: number
): Promise<BloodInventoryItem[]> => {
  const response = await apiGet<BloodInventoryItem[]>(`/blood-banks/${id}/inventory`);
  if (!response.ok) throw new Error(response.error || "Failed to fetch inventory");
  return response.data as BloodInventoryItem[];
};

export const fetchAvailableInventory = async (
  id: number
): Promise<BloodInventoryItem[]> => {
  const response = await apiGet<BloodInventoryItem[]>(
    `/blood-banks/${id}/inventory/available`
  );
  if (!response.ok) throw new Error(response.error || "Failed to fetch available inventory");
  return response.data as BloodInventoryItem[];
};
