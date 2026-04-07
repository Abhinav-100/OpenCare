"use client";

import { apiRequest } from "@/shared/utils/api-client";
import { Country } from "@/shared/types/countries";

/**
 * Fetches all countries
 */
// API flow: This module wraps backend endpoints and returns typed data for UI/hooks.
export async function getCountries() {
  return apiRequest<Country[]>("/countries", {
    method: "GET",
  });
}
