"use client";

import { apiRequest } from "@/shared/utils/api-client";
import { Domain } from "@/shared/types/domain";

/**
 * Fetches all domains
 */
// API flow: This module wraps backend endpoints and returns typed data for UI/hooks.
export async function getDomains() {
  return apiRequest<Domain[]>("/domain", {
    method: "GET",
  });
}
