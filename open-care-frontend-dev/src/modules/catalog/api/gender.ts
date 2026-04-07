import { baseUrl } from "@/shared/constants/config";
import { GenderResponse } from "@/shared/types/gender";
import { normalizeApiData } from "@/shared/utils/api-client";

// API flow: This module wraps backend endpoints and returns typed data for UI/hooks.
export const fetchGenders = async (): Promise<GenderResponse> => {
  const response = await fetch(`${baseUrl}/gender`, {
    method: "GET",
    headers: {
      "Content-Type": "application/json",
    },
  });

  if (!response.ok) {
    throw new Error(
      `Failed to fetch genders: ${response.status} ${response.statusText}`
    );
  }

  return normalizeApiData(await response.json());
};
