import { baseUrl } from "@/shared/constants/config";
import { TeacherPositionResponse } from "@/shared/types/teacher-positions";
import { normalizeApiData } from "@/shared/utils/api-client";

// API flow: This module wraps backend endpoints and returns typed data for UI/hooks.
export const fetchTeacherPositions =
	async (): Promise<TeacherPositionResponse> => {
		const response = await fetch(`${baseUrl}/teacher-positions`, {
			method: "GET",
			headers: {
				"Content-Type": "application/json",
			},
		});

		if (!response.ok) {
			throw new Error(
				`Failed to fetch teacher positions: ${response.status} ${response.statusText}`
			);
		}

		return normalizeApiData(await response.json());
	};
