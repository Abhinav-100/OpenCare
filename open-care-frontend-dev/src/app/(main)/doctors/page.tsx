import { Suspense } from "react";
import DoctorsView from "./_DoctorsView";
import type { Metadata } from "next";

export const metadata: Metadata = {
	title: "Find Doctors in Odisha",
	description:
		"Browse verified doctors across Odisha. Filter by speciality, district, and availability. Book consultations with qualified healthcare professionals.",
	openGraph: {
		title: "Find Doctors in Odisha | OpenCare",
		description:
			"Verified doctors across Odisha. Filter by speciality and location.",
	},
};

// Page flow: This route renders a screen entry and delegates business/data logic to module components.
export default function DoctorsPage() {
	return (
		// Suspense boundary required by Next.js when a child client component
		// uses useSearchParams() — prevents the page from opting out of SSR
		<Suspense fallback={<div className="min-h-screen bg-gray-50" />}>
			<DoctorsView />
		</Suspense>
	);
}
