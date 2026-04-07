import { AdminHeader } from "@/modules/admin/components/admin-header";
import { SettingsForm } from "@/modules/admin/components/settings-form";

// Page flow: This route renders a screen entry and delegates business/data logic to module components.
export default function SettingsPage() {
	return (
		<div className="flex flex-col">
			<AdminHeader
				title="Settings"
				description="Manage your healthcare system configuration"
			/>

			<div className="flex-1 space-y-4 p-4 md:p-8 pt-6">
				<SettingsForm />
			</div>
		</div>
	);
}
