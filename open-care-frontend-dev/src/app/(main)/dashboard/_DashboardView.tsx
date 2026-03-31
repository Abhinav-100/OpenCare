"use client";

import { useQuery } from "@tanstack/react-query";
import Link from "next/link";
import { Calendar, Hospital, Stethoscope, User, FileText, Heart, AlertCircle, Pill, Clock } from "lucide-react";
import { format } from "date-fns";
import { useAuth } from "@/modules/access/context/auth-context";
import { getUserInfo } from "@/shared/utils/auth-client";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/modules/platform/components/ui/card";
import { Button } from "@/modules/platform/components/ui/button";
import { Badge } from "@/modules/platform/components/ui/badge";
import { fetchMyAppointments } from "@/modules/clinical/api/appointments";
import { fetchMyConditions, fetchMyMedications, fetchMyEncounters } from "@/modules/clinical/api/health-records";

function getEnumCode(value: unknown): string {
	if (typeof value === "string") {
		return value;
	}

	if (value && typeof value === "object") {
		const code = (value as { value?: unknown }).value;
		if (typeof code === "string") {
			return code;
		}
	}

	return "";
}

function getEnumLabel(value: unknown): string {
	if (typeof value === "string") {
		return value;
	}

	if (value && typeof value === "object") {
		const displayName = (value as { displayName?: unknown }).displayName;
		if (typeof displayName === "string" && displayName.trim() !== "") {
			return displayName;
		}

		const code = (value as { value?: unknown }).value;
		if (typeof code === "string") {
			return code;
		}
	}

	return "";
}

export default function DashboardView() {
	const { isLoading, isAuthenticated } = useAuth();
	const userInfo = getUserInfo();

	// Fetch user data
	const { data: appointments = [] } = useQuery({
		queryKey: ["my-appointments"],
		queryFn: fetchMyAppointments,
		enabled: isAuthenticated,
	});

	const { data: conditions = [] } = useQuery({
		queryKey: ["health-conditions"],
		queryFn: fetchMyConditions,
		enabled: isAuthenticated,
	});

	const { data: medications = [] } = useQuery({
		queryKey: ["health-medications"],
		queryFn: fetchMyMedications,
		enabled: isAuthenticated,
	});

	const { data: encounters = [] } = useQuery({
		queryKey: ["health-encounters"],
		queryFn: fetchMyEncounters,
		enabled: isAuthenticated,
	});

	// Calculate stats
	const upcomingAppointments = appointments.filter(
		(apt) => {
			const statusCode = getEnumCode(apt.status);
			return statusCode !== "CANCELLED" && statusCode !== "COMPLETED";
		}
	);
	const totalHealthRecords = conditions.length + medications.length + encounters.length;
	const activeMedications = medications.filter((med) => med.isActive).length;

	if (isLoading) {
		return (
			<div className="flex items-center justify-center min-h-[60vh]">
				<div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary"></div>
			</div>
		);
	}

	const firstName = userInfo?.givenName || userInfo?.name?.split(" ")[0] || userInfo?.username || "User";

	return (
		<div className="container mx-auto px-4 py-8">
			{/* Welcome Section */}
			<div className="mb-8">
				<h1 className="text-3xl font-bold text-gray-900">
					Welcome back, {firstName}!
				</h1>
				<p className="text-gray-600 mt-2">
					Manage your healthcare journey from your personal dashboard.
				</p>
			</div>

			{/* Quick Actions */}
			<div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6 mb-8">
				<Link href="/doctors">
					<Card className="hover:shadow-lg transition-shadow cursor-pointer border-l-4 border-l-blue-500">
						<CardHeader className="flex flex-row items-center gap-4">
							<div className="p-3 bg-blue-100 rounded-full">
								<Stethoscope className="h-6 w-6 text-blue-600" />
							</div>
							<div>
								<CardTitle className="text-lg">Find Doctors</CardTitle>
								<CardDescription>Search specialists near you</CardDescription>
							</div>
						</CardHeader>
					</Card>
				</Link>

				<Link href="/hospitals">
					<Card className="hover:shadow-lg transition-shadow cursor-pointer border-l-4 border-l-green-500">
						<CardHeader className="flex flex-row items-center gap-4">
							<div className="p-3 bg-green-100 rounded-full">
								<Hospital className="h-6 w-6 text-green-600" />
							</div>
							<div>
								<CardTitle className="text-lg">Find Hospitals</CardTitle>
								<CardDescription>Locate healthcare facilities</CardDescription>
							</div>
						</CardHeader>
					</Card>
				</Link>

				<Link href="/health-schemes">
					<Card className="hover:shadow-lg transition-shadow cursor-pointer border-l-4 border-l-purple-500">
						<CardHeader className="flex flex-row items-center gap-4">
							<div className="p-3 bg-purple-100 rounded-full">
								<Heart className="h-6 w-6 text-purple-600" />
							</div>
							<div>
								<CardTitle className="text-lg">Health Schemes</CardTitle>
								<CardDescription>BSKY, Ayushman Bharat & more</CardDescription>
							</div>
						</CardHeader>
					</Card>
				</Link>
			</div>

			{/* Main Dashboard Content */}
			<div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
				{/* Appointments Section */}
				<Card className="lg:col-span-2">
					<CardHeader>
						<div className="flex items-center justify-between">
							<div className="flex items-center gap-3">
								<Calendar className="h-5 w-5 text-primary" />
								<CardTitle>My Appointments</CardTitle>
							</div>
							<Button variant="outline" size="sm" asChild>
								<Link href="/doctors">Book New</Link>
							</Button>
						</div>
					</CardHeader>
					<CardContent>
						{upcomingAppointments.length === 0 ? (
							<div className="text-center py-8 text-gray-500">
								<Calendar className="h-12 w-12 mx-auto mb-4 text-gray-300" />
								<p className="font-medium">No upcoming appointments</p>
								<p className="text-sm mt-1">
									Book your first appointment with a doctor
								</p>
								<Button variant="outline" className="mt-4" asChild>
									<Link href="/doctors">Find Doctors</Link>
								</Button>
							</div>
						) : (
							<div className="space-y-4">
								{upcomingAppointments.slice(0, 3).map((apt) => (
									(() => {
										const appointmentTypeLabel = getEnumLabel(apt.appointmentType) || "Appointment";
										const statusCode = getEnumCode(apt.status);
										const statusLabel = getEnumLabel(apt.status) || statusCode || "Unknown";

										return (
									<div
										key={apt.id}
										className="flex items-start gap-4 p-4 border rounded-lg hover:bg-gray-50 transition-colors"
									>
										<div className="flex-shrink-0 w-12 h-12 rounded-full bg-teal-100 flex items-center justify-center">
											<Stethoscope className="h-6 w-6 text-teal-600" />
										</div>
										<div className="flex-1 min-w-0">
											<div className="flex items-center gap-2 mb-1">
												<p className="font-semibold text-gray-900">
													Dr. {apt.doctor.profile.name}
												</p>
												<Badge className="bg-teal-100 text-teal-800 text-xs">
														{appointmentTypeLabel}
												</Badge>
											</div>
											<p className="text-sm text-gray-600 mb-1">
												{apt.doctor.specializations}
											</p>
											<div className="flex items-center gap-4 text-sm text-gray-500">
												<span className="flex items-center gap-1">
													<Calendar className="h-3 w-3" />
													{format(new Date(apt.appointmentDate), "MMM dd, yyyy")}
												</span>
												<span className="flex items-center gap-1">
													<Clock className="h-3 w-3" />
													{apt.startTime}
												</span>
											</div>
										</div>
										<Badge variant={statusCode === "CONFIRMED" ? "default" : "outline"}>
											{statusLabel}
										</Badge>
									</div>
										);
									})()
								))}
								{upcomingAppointments.length > 3 && (
									<Button variant="link" className="w-full" asChild>
										<Link href="/appointments">View all {upcomingAppointments.length} appointments →</Link>
									</Button>
								)}
							</div>
						)}
					</CardContent>
				</Card>

				{/* Profile Card */}
				<Card>
					<CardHeader>
						<div className="flex items-center gap-3">
							<User className="h-5 w-5 text-primary" />
							<CardTitle>My Profile</CardTitle>
						</div>
					</CardHeader>
					<CardContent>
						<div className="space-y-4">
							<div>
								<p className="text-sm text-gray-500">Name</p>
								<p className="font-medium">
									{userInfo?.name || "Not set"}
								</p>
							</div>
							<div>
								<p className="text-sm text-gray-500">Email</p>
								<p className="font-medium">
									{userInfo?.email || "Not set"}
								</p>
							</div>
							<div>
								<p className="text-sm text-gray-500">Username</p>
								<p className="font-medium">
									{userInfo?.username || "Not set"}
								</p>
							</div>
							<Button variant="outline" className="w-full" disabled>
								Edit Profile
							</Button>
						</div>
					</CardContent>
				</Card>

				{/* Health Records */}
				<Card className="lg:col-span-2">
					<CardHeader>
						<div className="flex items-center justify-between">
							<div className="flex items-center gap-3">
								<FileText className="h-5 w-5 text-primary" />
								<CardTitle>Health Records</CardTitle>
							</div>
							<Button variant="outline" size="sm" asChild>
								<Link href="/health-records">View All</Link>
							</Button>
						</div>
					</CardHeader>
					<CardContent>
						{totalHealthRecords === 0 ? (
							<div className="text-center py-8 text-gray-500">
								<FileText className="h-12 w-12 mx-auto mb-4 text-gray-300" />
								<p className="font-medium">No health records</p>
								<p className="text-sm mt-1">
									Start tracking your health journey
								</p>
								<Button variant="outline" className="mt-4" asChild>
									<Link href="/health-records">Add Health Records</Link>
								</Button>
							</div>
						) : (
							<div className="space-y-4">
								{/* Active Medications */}
								{medications.filter((m) => m.isActive).slice(0, 2).map((med) => (
									<div key={med.id} className="flex items-start gap-3 p-3 border rounded-lg">
										<Pill className="h-5 w-5 text-blue-600 mt-0.5" />
										<div className="flex-1">
											<div className="flex items-center gap-2">
												<p className="font-medium text-gray-900">{med.medicationName}</p>
												<Badge className="bg-green-100 text-green-800 text-xs">Active</Badge>
											</div>
											<p className="text-sm text-gray-600 mt-1">
												{med.dosageAmount} {med.dosageUnit} · {med.frequency}
											</p>
										</div>
									</div>
								))}

								{/* Recent Conditions */}
								{conditions
									.filter((c) => getEnumCode(c.status) === "ACTIVE")
									.slice(0, 2)
									.map((cond) => {
										const conditionTypeCode = getEnumCode(cond.conditionType);
										const conditionTypeLabel = getEnumLabel(cond.conditionType) || "Condition";
										const severityLabel = getEnumLabel(cond.severity);

										return (
									<div key={cond.id} className="flex items-start gap-3 p-3 border rounded-lg">
										<AlertCircle className="h-5 w-5 text-red-600 mt-0.5" />
										<div className="flex-1">
											<div className="flex items-center gap-2">
												<p className="font-medium text-gray-900">{cond.name}</p>
												<Badge className={
													conditionTypeCode === "ALLERGY" ? "bg-red-100 text-red-800" :
													conditionTypeCode === "CHRONIC" ? "bg-purple-100 text-purple-800" :
													"bg-blue-100 text-blue-800"
												} style={{ fontSize: '10px' }}>
													{conditionTypeLabel}
												</Badge>
											</div>
											{severityLabel && (
												<p className="text-sm text-gray-600 mt-1">
													Severity: {severityLabel}
												</p>
											)}
										</div>
									</div>
										);
									})}

								<Button variant="link" className="w-full" asChild>
									<Link href="/health-records">View all health records →</Link>
								</Button>
							</div>
						)}
					</CardContent>
				</Card>

				{/* Quick Stats */}
				<Card>
					<CardHeader>
						<CardTitle>Quick Stats</CardTitle>
					</CardHeader>
					<CardContent>
						<div className="space-y-4">
							<div className="flex justify-between items-center p-3 bg-blue-50 rounded-lg border border-blue-100">
								<span className="text-gray-600">Appointments</span>
								<span className="font-bold text-lg text-blue-600">{upcomingAppointments.length}</span>
							</div>
							<div className="flex justify-between items-center p-3 bg-green-50 rounded-lg border border-green-100">
								<span className="text-gray-600">Active Medications</span>
								<span className="font-bold text-lg text-green-600">{activeMedications}</span>
							</div>
							<div className="flex justify-between items-center p-3 bg-purple-50 rounded-lg border border-purple-100">
								<span className="text-gray-600">Health Records</span>
								<span className="font-bold text-lg text-purple-600">{totalHealthRecords}</span>
							</div>
						</div>
					</CardContent>
				</Card>
			</div>
		</div>
	);
}
