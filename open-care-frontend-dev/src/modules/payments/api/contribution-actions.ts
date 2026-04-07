"use client";

import { apiRequest } from "@/shared/utils/api-client";
import { ContributionAction } from "@/shared/types/contribution-actions";

/**
 * Fetches all contribution actions
 */
// API flow: This module wraps backend endpoints and returns typed data for UI/hooks.
export async function getContributionActions() {
  return apiRequest<ContributionAction[]>("/contribution-actions", {
    method: "GET",
  });
}
