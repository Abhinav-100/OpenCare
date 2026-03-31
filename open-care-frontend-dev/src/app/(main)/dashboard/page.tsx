import { Metadata } from "next";
import DashboardView from "./_DashboardView";

export const metadata: Metadata = {
	title: "My Dashboard",
	description: "Manage your healthcare journey - view appointments, health records, and profile.",
};

export default function DashboardPage() {
	return <DashboardView />;
}
