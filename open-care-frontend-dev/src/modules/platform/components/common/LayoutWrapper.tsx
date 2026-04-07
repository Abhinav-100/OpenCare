"use client";
import { usePathname } from "next/navigation";
import Header from "./Header";
import Footer from "./Footer";

interface LayoutWrapperProps {
	children: React.ReactNode;
}

export function LayoutWrapper({ children }: LayoutWrapperProps) {
	const pathname = usePathname();

	// Check if the current path is an admin route or auth route
	const isAdminRoute = pathname?.startsWith("/admin");
	const isAuthRoute =
		pathname?.startsWith("/login") ||
		pathname?.startsWith("/signup") ||
		pathname?.startsWith("/auth");

	// Admin/auth pages use their own focused layouts.
	if (isAdminRoute || isAuthRoute) {
		return <>{children}</>;
	}

	// Public and main app pages share the standard site chrome.
	return (
		<>
			<Header />
			{children}
			<Footer />
		</>
	);
}
