import { AdminHeader } from "@/modules/admin/components/admin-header";
import { UsersTable } from "@/modules/admin/components/users-table";

// Page flow: This route renders a screen entry and delegates business/data logic to module components.
export default function UsersPage() {
	return (
		<div className="flex flex-col">
			<AdminHeader
				title="Users Management"
				description="Manage healthcare staff and patient accounts"
			/>

			<div className="flex-1 space-y-4 p-4 md:p-8 pt-6">
				<UsersTable />
			</div>
		</div>
	);
}
