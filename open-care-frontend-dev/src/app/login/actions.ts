"use server";

import { login } from "@/modules/access/api/auth";
import { LoginResponse } from "@/shared/types/auth";
import { saveAuthTokens } from "@/shared/utils/auth";

export interface LoginState {
	success: boolean;
	error?: string;
	data?: LoginResponse;
	shouldRedirect?: boolean;
	redirectTo?: string;
}

function sanitizeLoginErrorMessage(message: string): string {
	const normalized = message.toLowerCase();

	if (
		normalized.includes("invalid_grant") ||
		normalized.includes("invalid user credentials") ||
		normalized.includes("unauthorized")
	) {
		return "Invalid email or password. Please try again.";
	}

	if (normalized.includes("keycloak") || normalized.includes("client error")) {
		return "Sign in failed. Please try again.";
	}

	return message;
}

export async function submitLogin(
	prevState: LoginState,
	formData: FormData
): Promise<LoginState> {
	// Anti-bot: Check honeypot
	const honeypot = formData.get("website_url");
	if (honeypot) {
		return {
			success: false,
			error: "Submission blocked. Please try again.",
		};
	}

	// Anti-bot: Check timing (humans take at least 2 seconds)
	const formLoadTime = formData.get("_formLoadTime");
	if (formLoadTime) {
		const timeSpent = Date.now() - parseInt(formLoadTime as string, 10);
		if (timeSpent < 2000) {
			return {
				success: false,
				error: "Please slow down. Try again in a moment.",
			};
		}
	}

	const username = formData.get("username");
	const password = formData.get("password");

	if (
		!username ||
		!password ||
		typeof username !== "string" ||
		typeof password !== "string"
	) {
		return {
			success: false,
			error: "Invalid form data. Please provide both username and password.",
		};
	}

	try {
		const response = await login({ username, password });

		// Save tokens to cookies using utility function
		await saveAuthTokens(response);

		// Return success state with redirect info
		return {
			success: true,
			data: response,
			shouldRedirect: true,
			redirectTo: "/",
		};
	} catch (error) {
		const message =
			error instanceof Error
				? sanitizeLoginErrorMessage(error.message)
				: "Login failed. Please try again.";

		return {
			success: false,
			error: message,
		};
	}
}
