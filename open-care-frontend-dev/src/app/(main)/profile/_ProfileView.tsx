"use client";

import { User, Mail, Phone, MapPin, Calendar, Shield } from "lucide-react";
import { useAuth } from "@/modules/access/context/auth-context";
import { getUserInfo } from "@/shared/utils/auth-client";
import { Card, CardContent, CardHeader, CardTitle } from "@/modules/platform/components/ui/card";
import { Button } from "@/modules/platform/components/ui/button";
import { Input } from "@/modules/platform/components/ui/input";
import { Label } from "@/modules/platform/components/ui/label";

export default function ProfileView() {
	const { isLoading } = useAuth();
	const userInfo = getUserInfo();

	if (isLoading) {
		return (
			<div className="flex items-center justify-center min-h-[60vh]">
				<div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary"></div>
			</div>
		);
	}

	const fullName = userInfo?.name || "Not set";
	const firstName = userInfo?.givenName || "";
	const lastName = userInfo?.familyName || "";
	const email = userInfo?.email || "Not set";
	const username = userInfo?.username || "Not set";

	return (
		<div className="min-h-screen bg-gray-50">
			{/* Header */}
			<div className="bg-gradient-to-r from-teal-600 to-teal-700 py-12">
				<div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8">
					<div className="flex items-center gap-6">
						<div className="w-24 h-24 rounded-full bg-white flex items-center justify-center shadow-lg">
							<User className="w-12 h-12 text-teal-600" />
						</div>
						<div className="text-white">
							<h1 className="text-3xl font-bold">{fullName}</h1>
							<p className="text-teal-100 mt-1">@{username}</p>
						</div>
					</div>
				</div>
			</div>

			{/* Content */}
			<div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
				<div className="grid gap-6">
					{/* Personal Information */}
					<Card>
						<CardHeader>
							<CardTitle className="flex items-center gap-2">
								<User className="w-5 h-5 text-teal-600" />
								Personal Information
							</CardTitle>
						</CardHeader>
						<CardContent className="space-y-6">
							<div className="grid grid-cols-1 md:grid-cols-2 gap-6">
								<div className="space-y-2">
									<Label htmlFor="firstName">First Name</Label>
									<Input
										id="firstName"
										value={firstName}
										disabled
										className="bg-gray-50"
									/>
								</div>
								<div className="space-y-2">
									<Label htmlFor="lastName">Last Name</Label>
									<Input
										id="lastName"
										value={lastName}
										disabled
										className="bg-gray-50"
									/>
								</div>
							</div>
							<div className="space-y-2">
								<Label htmlFor="username">Username</Label>
								<div className="relative">
									<User className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-gray-400" />
									<Input
										id="username"
										value={username}
										disabled
										className="pl-10 bg-gray-50"
									/>
								</div>
							</div>
						</CardContent>
					</Card>

					{/* Contact Information */}
					<Card>
						<CardHeader>
							<CardTitle className="flex items-center gap-2">
								<Mail className="w-5 h-5 text-teal-600" />
								Contact Information
							</CardTitle>
						</CardHeader>
						<CardContent className="space-y-6">
							<div className="space-y-2">
								<Label htmlFor="email">Email Address</Label>
								<div className="relative">
									<Mail className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-gray-400" />
									<Input
										id="email"
										value={email}
										disabled
										className="pl-10 bg-gray-50"
									/>
								</div>
							</div>
							<div className="space-y-2">
								<Label htmlFor="phone">Phone Number</Label>
								<div className="relative">
									<Phone className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-gray-400" />
									<Input
										id="phone"
										value="Not set"
										disabled
										className="pl-10 bg-gray-50"
									/>
								</div>
							</div>
							<div className="space-y-2">
								<Label htmlFor="address">Address</Label>
								<div className="relative">
									<MapPin className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-gray-400" />
									<Input
										id="address"
										value="Not set"
										disabled
										className="pl-10 bg-gray-50"
									/>
								</div>
							</div>
						</CardContent>
					</Card>

					{/* Account Settings */}
					<Card>
						<CardHeader>
							<CardTitle className="flex items-center gap-2">
								<Shield className="w-5 h-5 text-teal-600" />
								Account Settings
							</CardTitle>
						</CardHeader>
						<CardContent>
							<div className="flex flex-col sm:flex-row gap-4">
								<Button variant="outline" disabled>
									<Calendar className="w-4 h-4 mr-2" />
									Change Password
								</Button>
								<Button variant="outline" disabled>
									Edit Profile
								</Button>
							</div>
							<p className="text-sm text-gray-500 mt-4">
								Profile editing is currently disabled. This feature will be available soon.
							</p>
						</CardContent>
					</Card>
				</div>
			</div>
		</div>
	);
}
