/// <reference types="vitest" />

import React from "react";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { render, screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { describe, it, expect, vi } from "vitest";
import { BookAppointmentModal } from "./book-appointment-modal";
import { createAppointment, fetchDoctorSlots } from "@/modules/clinical/api/appointments";

vi.mock("next/navigation", () => ({
	useRouter: () => ({
		push: vi.fn(),
	}),
}));

vi.mock("sonner", () => ({
	toast: {
		success: vi.fn(),
		error: vi.fn(),
	},
}));

vi.mock("@/modules/clinical/api/appointments", () => ({
	fetchDoctorSlots: vi.fn(),
	createAppointment: vi.fn(),
}));

vi.mock("@/modules/access/context/auth-context", () => ({
	useAuth: () => ({
		isAuthenticated: true,
	}),
}));

vi.mock("@/modules/platform/components/ui/dialog", () => ({
	Dialog: ({ open, children }: { open: boolean; children: any }) =>
		open ? React.createElement("div", null, children) : null,
	DialogContent: ({ children }: { children: any }) =>
		React.createElement("div", null, children),
	DialogDescription: ({ children }: { children: any }) =>
		React.createElement("p", null, children),
	DialogHeader: ({ children }: { children: any }) =>
		React.createElement("div", null, children),
	DialogTitle: ({ children }: { children: any }) =>
		React.createElement("h2", null, children),
}));

vi.mock("@/modules/platform/components/ui/calendar", () => ({
	Calendar: ({ onSelect }: { onSelect: (date: Date) => void }) =>
		React.createElement(
			"button",
			{ onClick: () => onSelect(new Date(2026, 0, 1)) },
			"Select test date",
		),
}));

const mockDoctor = {
	id: 1,
	profile: {
		name: "Dr Demo",
	},
	specializations: "Cardiology",
	yearOfExperience: 7,
	consultationFeeOnline: 500,
	consultationFeeOffline: 700,
} as any;

describe("BookAppointmentModal", () => {
	it("books appointment after selecting date and slot", async () => {
		vi.mocked(fetchDoctorSlots).mockResolvedValue([
			{ startTime: "10:00", endTime: "10:30", isAvailable: true },
		]);
		vi.mocked(createAppointment).mockResolvedValue({
			appointmentDate: "2026-01-01",
			startTime: "10:00",
		} as any);

		const queryClient = new QueryClient({
			defaultOptions: {
				queries: { retry: false },
				mutations: { retry: false },
			},
		});

		render(
			React.createElement(
				QueryClientProvider,
				{ client: queryClient },
				React.createElement(BookAppointmentModal, {
					open: true,
					onOpenChange: vi.fn(),
					doctor: mockDoctor,
				}),
			),
		);

		const user = userEvent.setup();

		await user.click(screen.getByRole("button", { name: /select test date/i }));
		await user.click(await screen.findByRole("button", { name: /10:00/i }));
		await user.click(screen.getByRole("button", { name: /confirm booking/i }));

		await waitFor(() => {
			expect(createAppointment).toHaveBeenCalledTimes(1);
		});
		expect(createAppointment).toHaveBeenCalledWith(
			expect.objectContaining({
				doctorId: 1,
				appointmentType: "ONLINE",
				startTime: "10:00",
				endTime: "10:30",
			}),
		);
	});
});
