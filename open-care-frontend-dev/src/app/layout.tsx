import { Geist, Geist_Mono } from "next/font/google";
import { Toaster } from "@/modules/platform/components/ui/sonner";
import "./globals.css";
import { LayoutWrapper } from "@/modules/platform/components/common/LayoutWrapper";
import GoogleAnalytics from "@/modules/platform/components/GoogleAnalytics";
import { ClientProviders } from "@/modules/platform/components/common/ClientProviders";
import type { Metadata } from "next";

const geistSans = Geist({
	variable: "--font-geist-sans",
	subsets: ["latin"],
});

const geistMono = Geist_Mono({
	variable: "--font-geist-mono",
	subsets: ["latin"],
});

export const metadata: Metadata = {
	title: {
		default: "OpenCare - Odisha Healthcare Platform",
		template: "%s | OpenCare",
	},
	description:
		"Connect with certified doctors, find hospitals, and access government health schemes (BSKY, Ayushman Bharat) across Odisha — all in one platform.",
	keywords: [
		"Odisha healthcare",
		"doctors in Odisha",
		"hospitals in Odisha",
		"BSKY scheme",
		"Ayushman Bharat",
		"OpenCare",
		"find doctor Bhubaneswar",
	],
	openGraph: {
		type: "website",
		siteName: "OpenCare",
		title: "OpenCare - Odisha Healthcare Platform",
		description:
			"Find certified doctors, hospitals and health schemes across Odisha.",
		locale: "en_IN",
	},
	twitter: {
		card: "summary_large_image",
		title: "OpenCare - Odisha Healthcare Platform",
		description:
			"Find certified doctors, hospitals and health schemes across Odisha.",
	},
};

export default function RootLayout({
	children,
}: Readonly<{
	children: React.ReactNode;
}>) {
	return (
		<html lang="en">
			<body
				className={`${geistSans.variable} ${geistMono.variable} antialiased`}
			>
				<ClientProviders>
					<GoogleAnalytics />
					<LayoutWrapper>{children}</LayoutWrapper>
					<Toaster />
				</ClientProviders>
			</body>
		</html>
	);
}
