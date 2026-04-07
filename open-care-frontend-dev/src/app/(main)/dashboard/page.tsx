import { Metadata } from "next";
import DashboardView from "./_DashboardView";

export const metadata: Metadata = {
	title: "My Dashboard",
	description: "Manage your healthcare journey - view appointments, health records, and profile.",
};

// Page flow: This route renders a screen entry and delegates business/data logic to module components.
export default function DashboardPage() {
	return <DashboardView />;
}
