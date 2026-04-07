"use server";

import { redirect } from "next/navigation";
import { clearAuthTokens } from "@/shared/utils/auth";

// Auth flow: This file handles session/token actions used by login/logout/refresh flows.
export async function logout() {
	try {
		// Clear all auth tokens from cookies
		await clearAuthTokens();

		// Redirect to login page
		redirect("/login");
	} catch (error) {
		console.error("Logout failed:", error);
		// Still redirect even if clearing tokens fails
		redirect("/login");
	}
}
