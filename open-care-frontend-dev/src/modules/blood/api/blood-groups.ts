import { baseUrl } from "@/shared/constants/config";
import { BloodGroupResponse } from "@/shared/types/blood-groups";
import { normalizeApiData } from "@/shared/utils/api-client";

export const fetchBloodGroups = async (): Promise<BloodGroupResponse> => {
  const response = await fetch(`${baseUrl}/blood-groups`, {
    method: "GET",
    headers: {
      "Content-Type": "application/json",
    },
  });

  if (!response.ok) {
    throw new Error(
      `Failed to fetch blood groups: ${response.status} ${response.statusText}`
    );
  }

  return normalizeApiData(await response.json());
};
