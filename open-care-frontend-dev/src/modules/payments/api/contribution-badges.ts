"use client";

import { apiRequest } from "@/shared/utils/api-client";
import { ContributionBadge } from "@/shared/types/contribution-badges";

/**
 * Fetches all contribution badges
 */
export async function getContributionBadges() {
  return apiRequest<ContributionBadge[]>("/contribution-badges", {
    method: "GET",
  });
}
