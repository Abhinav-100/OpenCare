"use client";

import { useState } from "react";
import Link from "next/link";
import Image from "next/image";
import {
	Menu,
	X,
	User,
	LogOut,
	ChevronDown,
	LayoutDashboard,
	Stethoscope,
	Building2,
	Calendar,
	Droplets,
	Shield,
	FileText,
} from "lucide-react";
import { Button } from "@/modules/platform/components/ui/button";
import { useAuth } from "@/modules/access/context/auth-context";
import { clientLogout, getUserInfo } from "@/shared/utils/auth-client";
import { useLanguage } from "@/shared/constants/translations";

export default function Header() {
	const { isAuthenticated, isAdmin, logout } = useAuth();
	const { t } = useLanguage();
	const [menuOpen, setMenuOpen] = useState(false);
	const [userMenuOpen, setUserMenuOpen] = useState(false);

	// Navigation links with translations
	const navLinks = [
		{ href: "/doctors", label: t.nav.doctors, icon: Stethoscope },
		{ href: "/hospitals", label: t.nav.hospitals, icon: Building2 },
		{ href: "/blood-banks", label: t.nav.bloodBanks, icon: Droplets },
		{ href: "/health-schemes", label: t.nav.healthSchemes, icon: Shield },
		{ href: "/health-records", label: t.nav.healthRecords, icon: FileText },
		{ href: "/appointments", label: t.nav.appointments, icon: Calendar },
	];

	// Get user name from JWT
	const userInfo = getUserInfo();
	const userName = userInfo?.givenName || userInfo?.name?.split(" ")[0] || "User";

	const handleLogout = () => {
		logout();
		clientLogout();
		setUserMenuOpen(false);
		setMenuOpen(false);
	};

	// Dashboard link based on role
	const dashboardLink = isAdmin ? "/admin" : "/dashboard";
	const dashboardLabel = isAdmin ? "Admin Panel" : "Dashboard";

	return (
		<header className="bg-white/95 backdrop-blur-md border-b border-gray-200/50 sticky top-0 z-50 transition-all duration-300 shadow-sm">
			<div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
				<div className="flex justify-between items-center h-20">
					{/* Logo */}
					<Link
						href="/"
						className="flex items-center hover:opacity-80 transition-opacity"
						onClick={() => setMenuOpen(false)}
					>
						<Image
							src="/logo.png"
							alt="OpenCare Logo"
							width={55}
							height={55}
							className="object-contain"
						/>
					</Link>

					{/* Desktop Navigation */}
					<nav className="hidden lg:flex items-center space-x-2">
						{navLinks.map((link) => {
							const IconComponent = link.icon;
							return (
								<Link
									key={link.href}
									href={link.href}
									className="flex items-center gap-2 px-4 py-2 rounded-full text-gray-600 font-medium transition-all hover:text-teal-600 hover:bg-teal-50"
								>
									<IconComponent className="w-4 h-4" />
									{link.label}
								</Link>
							);
						})}
					</nav>

					{/* Desktop Auth Buttons */}
					<div className="hidden lg:flex items-center gap-3">
						{isAuthenticated ? (
							<div className="relative">
								<button
									onClick={() => setUserMenuOpen((v) => !v)}
									className="flex items-center gap-2 px-3 py-2 rounded-lg bg-teal-50 text-teal-700 hover:bg-teal-100 transition-colors"
								>
									<div className="w-8 h-8 rounded-full bg-teal-600 flex items-center justify-center">
										<User className="w-4 h-4 text-white" />
									</div>
									<span className="font-medium">Hi, {userName}</span>
									<ChevronDown className={`w-4 h-4 transition-transform ${userMenuOpen ? "rotate-180" : ""}`} />
								</button>

								{/* User Dropdown */}
								{userMenuOpen && (
									<div className="absolute right-0 mt-2 w-48 bg-white rounded-lg shadow-lg border border-gray-200 py-1 z-50">
										<Link
											href={dashboardLink}
											onClick={() => setUserMenuOpen(false)}
											className="flex items-center gap-2 px-4 py-2 text-gray-700 hover:bg-gray-50"
										>
											<LayoutDashboard className="w-4 h-4" />
											{dashboardLabel}
										</Link>
										<Link
											href="/profile"
											onClick={() => setUserMenuOpen(false)}
											className="flex items-center gap-2 px-4 py-2 text-gray-700 hover:bg-gray-50"
										>
											<User className="w-4 h-4" />
											My Profile
										</Link>
										<hr className="my-1 border-gray-100" />
										<button
											onClick={handleLogout}
											className="flex items-center gap-2 w-full px-4 py-2 text-red-600 hover:bg-red-50"
										>
											<LogOut className="w-4 h-4" />
											Sign Out
										</button>
									</div>
								)}
							</div>
						) : (
							<div className="flex space-x-4">
								<Link href="/login">
									<Button
										variant="outline"
										className="border-teal-600 text-teal-600 hover:bg-teal-50"
									>
										Sign In
									</Button>
								</Link>
								<Link href="/signup">
									<Button className="bg-teal-600 hover:bg-teal-700">
										Get Started
									</Button>
								</Link>
							</div>
						)}
					</div>

					{/* Mobile: Hamburger + User/Sign In */}
					<div className="flex lg:hidden items-center gap-3">
						{isAuthenticated ? (
							<button
								onClick={() => setUserMenuOpen((v) => !v)}
								className="p-2 rounded-full bg-teal-50 text-teal-700"
							>
								<User className="w-5 h-5" />
							</button>
						) : (
							<Link href="/login">
								<Button
									variant="outline"
									size="sm"
									className="border-teal-600 text-teal-600 hover:bg-teal-50"
								>
									Sign In
								</Button>
							</Link>
						)}
						<button
							onClick={() => setMenuOpen((v) => !v)}
							aria-label="Toggle menu"
							className="p-2 rounded-md text-gray-600 hover:text-teal-600 hover:bg-gray-100 transition-colors"
						>
							{menuOpen ? <X className="w-6 h-6" /> : <Menu className="w-6 h-6" />}
						</button>
					</div>
				</div>
			</div>

			{/* Mobile User Menu */}
			{userMenuOpen && (
				<div className="lg:hidden absolute right-4 top-20 w-48 bg-white rounded-lg shadow-lg border border-gray-200 py-1 z-50">
					<Link
						href={dashboardLink}
						onClick={() => setUserMenuOpen(false)}
						className="flex items-center gap-2 px-4 py-2 text-gray-700 hover:bg-gray-50"
					>
						<LayoutDashboard className="w-4 h-4" />
						{dashboardLabel}
					</Link>
					<Link
						href="/profile"
						onClick={() => setUserMenuOpen(false)}
						className="flex items-center gap-2 px-4 py-2 text-gray-700 hover:bg-gray-50"
					>
						<User className="w-4 h-4" />
						My Profile
					</Link>
					<hr className="my-1 border-gray-100" />
					<button
						onClick={handleLogout}
						className="flex items-center gap-2 w-full px-4 py-2 text-red-600 hover:bg-red-50"
					>
						<LogOut className="w-4 h-4" />
						Sign Out
					</button>
				</div>
			)}

			{/* Mobile Dropdown Menu */}
			{menuOpen && (
				<div className="lg:hidden border-t border-gray-100 bg-white shadow-lg">
					<nav className="max-w-7xl mx-auto px-4 py-4 flex flex-col gap-1">
						{navLinks.map((link) => {
							const IconComponent = link.icon;
							return (
								<Link
									key={link.href}
									href={link.href}
									onClick={() => setMenuOpen(false)}
									className="flex items-center gap-3 px-3 py-3 rounded-lg text-gray-700 font-medium hover:bg-teal-50 hover:text-teal-700 transition-colors"
								>
									<IconComponent className="w-5 h-5" />
									{link.label}
								</Link>
							);
						})}
						{!isAuthenticated && (
							<div className="mt-3 pt-3 border-t border-gray-100">
								<Link href="/signup" onClick={() => setMenuOpen(false)}>
									<Button className="w-full bg-teal-600 hover:bg-teal-700">
										Get Started
									</Button>
								</Link>
							</div>
						)}
					</nav>
				</div>
			)}
		</header>
	);
}
