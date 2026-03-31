"use client";

import { apiRequest } from "@/shared/utils/api-client";
import { Domain } from "@/shared/types/domain";

/**
 * Fetches all domains
 */
export async function getDomains() {
  return apiRequest<Domain[]>("/domain", {
    method: "GET",
  });
}
