import { Suspense } from "react";
import { Metadata } from "next";
import LoginView from "./_LoginView";

export const metadata: Metadata = {
	title: "Login",
	description: "Sign in to your OpenCare account to access healthcare services across Odisha.",
};

function LoginFallback() {
	return (
		<div className="min-h-screen bg-gray-50 flex items-center justify-center">
			<div className="animate-pulse text-gray-400">Loading...</div>
		</div>
	);
}

// Page flow: This route renders a screen entry and delegates business/data logic to module components.
export default function LoginPage() {
	return (
		<Suspense fallback={<LoginFallback />}>
			<LoginView />
		</Suspense>
	);
}
