"use client";

import { apiGet } from "@/shared/utils/api-client";

// API flow: This module wraps backend endpoints and returns typed data for UI/hooks.
export interface HomeStats {
	totalDoctors: number;
	totalHospitals: number;
}

interface DoctorsResponse {
	totalItems: number;
}

interface HospitalsResponse {
	totalItems: number;
}

export const fetchHomeStats = async (): Promise<HomeStats> => {
	// Fetch counts from doctors and hospitals APIs (more reliable than /home/featured)
	const [doctorsRes, hospitalsRes] = await Promise.all([
		apiGet<DoctorsResponse>("/doctors?page=0&size=1", { includeAuth: false }),
		apiGet<HospitalsResponse>("/hospitals?page=0&size=1", { includeAuth: false }),
	]);

	return {
		totalDoctors: doctorsRes.data?.totalItems ?? 0,
		totalHospitals: hospitalsRes.data?.totalItems ?? 0,
	};
};
