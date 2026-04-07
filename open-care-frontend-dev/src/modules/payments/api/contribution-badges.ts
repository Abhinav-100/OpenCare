"use client";

import { apiRequest } from "@/shared/utils/api-client";
import { ContributionBadge } from "@/shared/types/contribution-badges";

/**
 * Fetches all contribution badges
 */
// API flow: This module wraps backend endpoints and returns typed data for UI/hooks.
export async function getContributionBadges() {
  return apiRequest<ContributionBadge[]>("/contribution-badges", {
    method: "GET",
  });
}
