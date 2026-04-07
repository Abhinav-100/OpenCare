import { redirect } from "next/navigation";

// Page flow: This route renders a screen entry and delegates business/data logic to module components.
export default function AdminPage() {
	redirect("/admin/dashboard");
}
