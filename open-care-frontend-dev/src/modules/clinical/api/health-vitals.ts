import { baseUrl } from "@/shared/constants/config";
import { HealthVitals } from "@/shared/types/health-vitals";
import { normalizeApiData } from "@/shared/utils/api-client";

/**
 * Get latest health vitals for the current user
 */
// API flow: This module wraps backend endpoints and returns typed data for UI/hooks.
export const getLatestHealthVitals = async (
  token: string
): Promise<HealthVitals> => {
  const response = await fetch(`${baseUrl}/health-vitals/self/latest`, {
    method: "GET",
    headers: {
      Authorization: `Bearer ${token}`,
      "Content-Type": "application/json",
    },
  });

  if (!response.ok) {
    throw new Error(
      `Failed to fetch health vitals: ${response.status} ${response.statusText}`
    );
  }

  return normalizeApiData(await response.json());
};
