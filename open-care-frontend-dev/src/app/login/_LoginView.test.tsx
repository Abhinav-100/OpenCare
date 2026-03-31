/// <reference types="vitest" />

import React from "react";
import { render, screen } from "@testing-library/react";
import { describe, it, expect, vi } from "vitest";
import LoginView from "./_LoginView";

vi.mock("next/link", () => ({
	default: ({ children }: { children: any }) => children,
}));

vi.mock("next/navigation", () => ({
	useSearchParams: () => ({
		get: () => null,
	}),
}));

vi.mock("@/modules/access/context/auth-context", () => ({
	useAuth: () => ({
		refetchUser: vi.fn(),
	}),
}));

vi.mock("@/shared/utils/auth-client", () => ({
	saveUserSession: vi.fn(),
	hasAdminRole: vi.fn(() => false),
}));

vi.mock("./actions", () => ({
	submitLogin: vi.fn(),
}));

describe("LoginView", () => {
	it("renders login UI fields", () => {
		render(React.createElement(LoginView));

		expect(
			screen.getByRole("heading", { name: /welcome back/i }),
		).toBeInTheDocument();
		expect(screen.getByLabelText(/e-mail/i)).toBeInTheDocument();
		expect(screen.getByLabelText(/password/i)).toBeInTheDocument();
		expect(
			screen.getByRole("button", { name: /sign in/i }),
		).toBeInTheDocument();
	});
});
