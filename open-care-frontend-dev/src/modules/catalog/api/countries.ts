"use client";

import { apiRequest } from "@/shared/utils/api-client";
import { Country } from "@/shared/types/countries";

/**
 * Fetches all countries
 */
export async function getCountries() {
  return apiRequest<Country[]>("/countries", {
    method: "GET",
  });
}
