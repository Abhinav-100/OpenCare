"use client";

import { apiRequest } from "@/shared/utils/api-client";
import {
  DashboardOverview,
  RealTimeStats,
  Alert,
  RegistrationTrends,
  RecentActivity,
} from "@/shared/types/dashboard";

/**
 * Fetches the dashboard overview statistics
 */
// API flow: This module wraps backend endpoints and returns typed data for UI/hooks.
export async function getDashboardOverview() {
  return apiRequest<DashboardOverview>("/superadmin/dashboard/overview", {
    method: "GET",
  });
}

/**
 * Fetches real-time system statistics
 */
export async function getRealTimeStats() {
  return apiRequest<RealTimeStats>("/superadmin/dashboard/real-time-stats", {
    method: "GET",
  });
}

/**
 * Fetches system alerts
 */
export async function getDashboardAlerts() {
  return apiRequest<Alert[]>("/superadmin/dashboard/alerts", {
    method: "GET",
  });
}

/**
 * Fetches registration trends data
 * @param months - Number of months to fetch (default: 12)
 */
export async function getRegistrationTrends(months: number = 12) {
  return apiRequest<RegistrationTrends>(
    `/superadmin/dashboard/registration-trends?months=${months}`,
    {
      method: "GET",
    }
  );
}

/**
 * Fetches recent activities
 * @param limit - Number of activities to fetch (default: 20)
 */
export async function getRecentActivities(limit: number = 20) {
  return apiRequest<RecentActivity[]>(
    `/superadmin/dashboard/recent-activities?limit=${limit}`,
    {
      method: "GET",
    }
  );
}
