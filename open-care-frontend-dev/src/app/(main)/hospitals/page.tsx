import HospitalsView from "./_HospitalsView";
import type { Metadata } from "next";

export const metadata: Metadata = {
	title: "Find Hospitals in Odisha",
	description:
		"Discover hospitals across Odisha. Filter by district, type (government, private, NGO), and services like emergency care and blood bank. View on map.",
	openGraph: {
		title: "Find Hospitals in Odisha | OpenCare",
		description:
			"Hospitals across Odisha — filter by location, type, and emergency services.",
	},
};

// Page flow: This route renders a screen entry and delegates business/data logic to module components.
export default function HospitalsPage() {
	return <HospitalsView />;
}
