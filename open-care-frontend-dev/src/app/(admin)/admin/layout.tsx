"use client";
import { AdminSidebar } from "@/modules/admin/components/admin-sidebar";
import { SidebarProvider } from "@/modules/platform/components/ui/sidebar";

// QueryClient is already provided by root ClientProviders — no need for a second one here.
// Having two nested QueryClientProviders creates isolated caches, which means
// invalidateQueries and cache settings from the root provider are ignored for admin pages.

export default function AdminLayout({
	children,
}: {
	children: React.ReactNode;
}) {
	return (
		<SidebarProvider>
			<div className="flex min-h-screen w-full">
				<AdminSidebar />
				<main className="flex-1 overflow-hidden">{children}</main>
			</div>
		</SidebarProvider>
	);
}
