import { baseUrl } from "@/shared/constants/config";
import { LoginFormData } from "@/modules/access/validations/login-schema";
import { SignupFormData } from "@/modules/access/validations/signup-schema";
import { LoginResponse } from "@/shared/types/auth";

function getFriendlyLoginError(rawMessage: string, status?: number): string {
	const message = rawMessage.toLowerCase();

	if (
		status === 401 ||
		message.includes("invalid_grant") ||
		message.includes("invalid user credentials")
	) {
		return "Invalid email or password. Please try again.";
	}

	if (message.includes("keycloak client error") || message.includes("unauthorized")) {
		return "Sign in failed. Please check your email and password.";
	}

	if (status !== undefined && status >= 500) {
		return "Login service is temporarily unavailable. Please try again shortly.";
	}

	return "Login failed. Please try again.";
}

export const login = async (
	loginData: LoginFormData
): Promise<LoginResponse> => {
	try {
		const response = await fetch(`${baseUrl}/auth/login`, {
			method: "POST",
			headers: {
				"Content-Type": "application/json",
			},
			body: JSON.stringify(loginData),
		});

		if (!response.ok) {
			// Try to get error message from response body
			const errorBody = await response.json().catch(() => null);
			const errorMessage =
				errorBody?.message || errorBody?.error || `Login failed (${response.status})`;
			throw new Error(getFriendlyLoginError(errorMessage, response.status));
		}

		return (await response.json()) as LoginResponse;
	} catch (error) {
		// Handle network errors (backend not running, etc.)
		if (error instanceof TypeError && error.message.includes("fetch")) {
			throw new Error(
				"Unable to connect to server. Please check if backend and Keycloak are running."
			);
		}
		throw error;
	}
};

/**
 * Refresh access token using refresh token
 */
export const refreshToken = async (
	refreshToken: string
): Promise<LoginResponse> => {
	const response = await fetch(`${baseUrl}/auth/refresh`, {
		method: "POST",
		headers: {
			"Content-Type": "application/json",
		},
		body: JSON.stringify({
			refreshToken: refreshToken,
		}),
	});

	if (!response.ok) {
		throw new Error(
			`Failed to refresh token: ${response.status} ${response.statusText}`
		);
	}

	return (await response.json()) as LoginResponse;
};

export const forgotPassword = async (
	email: string
): Promise<{ ok: boolean; error?: string }> => {
	const response = await fetch(`${baseUrl}/auth/forgot-password`, {
		method: "POST",
		headers: { "Content-Type": "application/json" },
		body: JSON.stringify({ email }),
	});

	if (!response.ok) {
		return { ok: false, error: `Request failed (${response.status})` };
	}

	return { ok: true };
};

export const register = async (
	data: SignupFormData
): Promise<{ ok: boolean; message?: string; error?: string }> => {
	const response = await fetch(`${baseUrl}/auth/register`, {
		method: "POST",
		headers: {
			"Content-Type": "application/json",
		},
		body: JSON.stringify(data),
	});

	const body = (await response.json().catch(() => ({}))) as {
		message?: string;
		error?: string;
	};

	if (!response.ok) {
		// Backend returns { message: "..." } on errors like 409 (duplicate email)
		const message =
			body?.message || body?.error || `Registration failed (${response.status})`;
		return { ok: false, error: message };
	}

	return { ok: true, message: body?.message };
};
