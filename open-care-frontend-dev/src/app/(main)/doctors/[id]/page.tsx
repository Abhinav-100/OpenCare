"use client";

import { useState } from "react";
import { useParams } from "next/navigation";
import { useQuery } from "@tanstack/react-query";
import Link from "next/link";
import {
	UserCheck,
	MapPin,
	Shield,
	Calendar,
	Award,
	Building2,
	Users,
	ArrowLeft,
	Facebook,
	Linkedin,
	Globe,
	Twitter,
	Star,
	IndianRupee,
} from "lucide-react";
import { Badge } from "@/modules/platform/components/ui/badge";
import { Button } from "@/modules/platform/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/modules/platform/components/ui/card";
import { fetchDoctorDetailsById } from "@/modules/provider/api/doctors";
import { BookAppointmentModal } from "@/modules/appointment/components/book-appointment-modal";

function getDisplayText(value: unknown): string {
	if (typeof value === "string") {
		return value;
	}

	if (value && typeof value === "object") {
		const displayName = (value as { displayName?: unknown }).displayName;
		if (typeof displayName === "string" && displayName.trim() !== "") {
			return displayName;
		}

		const bnName = (value as { bnName?: unknown }).bnName;
		if (typeof bnName === "string" && bnName.trim() !== "") {
			return bnName;
		}

		const valueCode = (value as { value?: unknown }).value;
		if (typeof valueCode === "string" && valueCode.trim() !== "") {
			return valueCode;
		}
	}

	return "";
}

export default function DoctorDetailPage() {
	const { id } = useParams<{ id: string }>();
	const [showBookingModal, setShowBookingModal] = useState(false);

	const {
		data: doctor,
		isLoading,
		isError,
	} = useQuery({
		queryKey: ["doctor-detail", id],
		queryFn: () => fetchDoctorDetailsById(Number(id)),
		enabled: !!id,
	});

	if (isLoading) return <LoadingSkeleton />;
	if (isError || !doctor) return <ErrorState />;

	const { profile } = doctor;
	const genderText = getDisplayText(profile.gender);
	const bloodGroupText = getDisplayText(profile.bloodGroup);
	const primaryWorkplace = doctor.doctorWorkplaces?.[0];
	const primarySpeciality =
		primaryWorkplace?.medicalSpeciality?.name ||
		(doctor.specializations
			? doctor.specializations.split(",")[0].trim()
			: null);

	// Generate color from name for avatar
	const getAvatarColor = (name: string) => {
		const colors = [
			"bg-blue-500",
			"bg-green-500",
			"bg-purple-500",
			"bg-pink-500",
			"bg-indigo-500",
			"bg-teal-500",
		];
		const index = name.charCodeAt(0) % colors.length;
		return colors[index];
	};

	const getInitials = (name: string) => {
		return name
			.split(" ")
			.map((n) => n[0])
			.join("")
			.toUpperCase()
			.slice(0, 2);
	};

	const handleBookAppointment = () => {
		setShowBookingModal(true);
	};

	return (
		<div className="min-h-screen bg-gray-50">
			{/* Booking Modal */}
			{doctor && (
				<BookAppointmentModal
					open={showBookingModal}
					onOpenChange={setShowBookingModal}
					doctor={doctor}
				/>
			)}
			{/* Back nav */}
			<div className="bg-white border-b px-4 py-3">
				<div className="max-w-5xl mx-auto">
					<Link
						href="/doctors"
						className="inline-flex items-center text-teal-600 hover:text-teal-700 text-sm font-medium"
					>
						<ArrowLeft className="w-4 h-4 mr-1" />
						Back to Doctors
					</Link>
				</div>
			</div>

			<div className="max-w-5xl mx-auto px-4 py-8 space-y-6">
				{/* Hero Card */}
				<Card>
					<CardContent className="p-8">
						<div className="flex gap-8">
							{/* Avatar */}
							<div
								className={`w-32 h-32 rounded-2xl ${getAvatarColor(
									profile.name
								)} border-2 border-white shadow-lg flex items-center justify-center flex-shrink-0`}
							>
								{profile.imageUrl ? (
									// eslint-disable-next-line @next/next/no-img-element
									<img
										src={profile.imageUrl}
										alt={profile.name}
										className="w-full h-full rounded-2xl object-cover"
									/>
								) : (
									<span className="text-4xl font-bold text-white">
										{getInitials(profile.name)}
									</span>
								)}
							</div>

							{/* Info */}
							<div className="flex-1">
								<div className="flex items-start justify-between">
									<div>
										<h1 className="text-3xl font-bold text-gray-900 mb-1">
											{profile.name}
										</h1>
										{primarySpeciality && (
											<p className="text-teal-600 font-medium text-lg mb-2">
												{primarySpeciality}
											</p>
										)}

										{/* Rating */}
										<div className="flex items-center gap-2 mb-3">
											<div className="flex items-center gap-1">
												{[1, 2, 3, 4, 5].map((star) => (
													<Star
														key={star}
														className="w-4 h-4 fill-yellow-400 text-yellow-400"
													/>
												))}
											</div>
											<span className="text-sm text-gray-600">
												5.0 (24 reviews)
											</span>
										</div>

										<div className="flex items-center gap-3 flex-wrap mb-4">
											{doctor.isVerified && (
												<Badge className="bg-teal-600 text-white">
													<Shield className="w-3 h-3 mr-1" />
													Verified
												</Badge>
											)}
											<Badge
												variant="outline"
												className="border-green-500 text-green-700 bg-green-50"
											>
												IMC: {doctor.bmdcNo}
											</Badge>
											{doctor.yearOfExperience != null && (
												<Badge
													variant="outline"
													className="border-gray-400 text-gray-700"
												>
													<Calendar className="w-3 h-3 mr-1" />
													{doctor.yearOfExperience} Yrs Experience
												</Badge>
											)}
										</div>

										{/* Consultation Fees */}
										{(doctor.consultationFeeOnline ||
											doctor.consultationFeeOffline) && (
											<div className="flex items-center gap-4 text-sm">
												{doctor.consultationFeeOnline && (
													<div className="flex items-center gap-1 text-gray-700">
														<IndianRupee className="w-4 h-4" />
														<span className="font-semibold">
															{doctor.consultationFeeOnline}
														</span>
														<span className="text-gray-500">online</span>
													</div>
												)}
												{doctor.consultationFeeOffline && (
													<div className="flex items-center gap-1 text-gray-700">
														<IndianRupee className="w-4 h-4" />
														<span className="font-semibold">
															{doctor.consultationFeeOffline}
														</span>
														<span className="text-gray-500">in-person</span>
													</div>
												)}
											</div>
										)}
									</div>
									<Button
										className="bg-teal-600 hover:bg-teal-700 text-white flex-shrink-0"
										size="lg"
										onClick={handleBookAppointment}
									>
										<Calendar className="w-4 h-4 mr-2" />
										Book Appointment
									</Button>
								</div>

								{/* Location */}
								{(profile.district || profile.upazila) && (
									<div className="flex items-center text-gray-600 text-sm mb-3">
										<MapPin className="w-4 h-4 mr-1 text-teal-500" />
										<span>
											{[profile.upazila?.name, profile.district?.name]
												.filter(Boolean)
												.join(", ")}
										</span>
									</div>
								)}

								{/* Social Links */}
								<div className="flex gap-3 mt-2">
									{profile.facebookProfileUrl && (
										<a
											href={profile.facebookProfileUrl}
											target="_blank"
											rel="noopener noreferrer"
											className="text-gray-400 hover:text-blue-600 transition-colors"
										>
											<Facebook className="w-5 h-5" />
										</a>
									)}
									{profile.linkedinProfileUrl && (
										<a
											href={profile.linkedinProfileUrl}
											target="_blank"
											rel="noopener noreferrer"
											className="text-gray-400 hover:text-blue-700 transition-colors"
										>
											<Linkedin className="w-5 h-5" />
										</a>
									)}
									{profile.researchGateProfileUrl && (
										<a
											href={profile.researchGateProfileUrl}
											target="_blank"
											rel="noopener noreferrer"
											className="text-gray-400 hover:text-teal-600 transition-colors"
										>
											<Globe className="w-5 h-5" />
										</a>
									)}
									{profile.xprofileUrl && (
										<a
											href={profile.xprofileUrl}
											target="_blank"
											rel="noopener noreferrer"
											className="text-gray-400 hover:text-gray-900 transition-colors"
										>
											<Twitter className="w-5 h-5" />
										</a>
									)}
								</div>
							</div>
						</div>
					</CardContent>
				</Card>

				{/* Body Grid */}
				<div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
					{/* Left — main content */}
					<div className="lg:col-span-2 space-y-6">
						{/* About */}
						{doctor.description && (
							<Card>
								<CardHeader>
									<CardTitle className="text-lg text-gray-900">About</CardTitle>
								</CardHeader>
								<CardContent>
									<p className="text-gray-700 leading-relaxed">
										{doctor.description}
									</p>
								</CardContent>
							</Card>
						)}

						{/* Education */}
						{doctor.doctorDegrees.length > 0 && (
							<Card>
								<CardHeader>
									<CardTitle className="text-lg text-gray-900 flex items-center gap-2">
										<Award className="w-5 h-5 text-teal-600" />
										Education & Degrees
									</CardTitle>
								</CardHeader>
								<CardContent>
									<div className="space-y-4">
										{doctor.doctorDegrees.map((dd) => (
											<div
												key={dd.id}
												className="flex items-start gap-4 pb-4 border-b last:border-0 last:pb-0"
											>
												<div className="w-12 h-12 rounded-lg bg-teal-50 border border-teal-100 flex items-center justify-center flex-shrink-0">
													<Award className="w-6 h-6 text-teal-600" />
												</div>
												<div>
													<div className="font-semibold text-gray-900">
														{dd.degree.abbreviation}
														{dd.medicalSpeciality
															? ` — ${dd.medicalSpeciality.name}`
															: ""}
													</div>
													<div className="text-sm text-gray-600">
														{dd.degree.name}
													</div>
													{dd.institution && (
														<div className="text-sm text-gray-500 mt-0.5">
															{dd.institution.name}
															{dd.institution.district
																? `, ${dd.institution.district.name}`
																: ""}
														</div>
													)}
													{(dd.startDate || dd.endDate) && (
														<div className="text-xs text-gray-400 mt-0.5">
															{dd.startDate?.slice(0, 4)}
															{dd.endDate
																? ` — ${dd.endDate.slice(0, 4)}`
																: " — Present"}
														</div>
													)}
												</div>
											</div>
										))}
									</div>
								</CardContent>
							</Card>
						)}

						{/* Associations */}
						{doctor.doctorAssociations?.length > 0 && (
							<Card>
								<CardHeader>
									<CardTitle className="text-lg text-gray-900 flex items-center gap-2">
										<Users className="w-5 h-5 text-teal-600" />
										Professional Memberships
									</CardTitle>
								</CardHeader>
								<CardContent>
									<div className="space-y-4">
										{doctor.doctorAssociations.map((da) => (
											<div
												key={da.id}
												className="flex items-start gap-4 pb-4 border-b last:border-0 last:pb-0"
											>
												<div className="w-12 h-12 rounded-lg bg-gray-50 border flex items-center justify-center flex-shrink-0">
													<Users className="w-6 h-6 text-gray-500" />
												</div>
												<div>
													<div className="font-semibold text-gray-900">
														{da.association.name}
													</div>
													{da.association.shortName && (
														<div className="text-sm text-gray-500">
															{da.association.shortName}
														</div>
													)}
													<div className="flex items-center gap-2 mt-1">
														<Badge variant="outline" className="text-xs">
															{da.membershipType.displayName}
														</Badge>
														{da.isActive && (
															<Badge className="bg-green-100 text-green-700 text-xs border-0">
																Active
															</Badge>
														)}
													</div>
												</div>
											</div>
										))}
									</div>
								</CardContent>
							</Card>
						)}
					</div>

					{/* Right — sidebar */}
					<div className="space-y-6">
						{/* Workplaces */}
						{doctor.doctorWorkplaces.length > 0 && (
							<Card>
								<CardHeader>
									<CardTitle className="text-lg text-gray-900 flex items-center gap-2">
										<Building2 className="w-5 h-5 text-teal-600" />
										Workplaces
									</CardTitle>
								</CardHeader>
								<CardContent>
									<div className="space-y-4">
										{doctor.doctorWorkplaces.map((dw) => (
											<div
												key={dw.id}
												className="pb-4 border-b last:border-0 last:pb-0"
											>
												<div className="font-semibold text-gray-900 text-sm">
													{dw.hospital?.name || dw.institution?.name}
												</div>
												<div className="text-sm text-teal-600">
													{dw.doctorPosition}
												</div>
												{dw.medicalSpeciality && (
													<div className="text-xs text-gray-500 mt-0.5">
														{dw.medicalSpeciality.name}
													</div>
												)}
												{dw.hospital?.district && (
													<div className="flex items-center text-xs text-gray-400 mt-1">
														<MapPin className="w-3 h-3 mr-1" />
														{dw.hospital.district.name}
													</div>
												)}
											</div>
										))}
									</div>
								</CardContent>
							</Card>
						)}

						{/* Quick Info */}
						<Card>
							<CardHeader>
								<CardTitle className="text-lg text-gray-900">
									Quick Info
								</CardTitle>
							</CardHeader>
							<CardContent>
								<div className="space-y-3 text-sm">
									{genderText && (
										<div className="flex justify-between">
											<span className="text-gray-500">Gender</span>
											<span className="font-medium text-gray-900">
												{genderText}
											</span>
										</div>
									)}
									{bloodGroupText && (
										<div className="flex justify-between">
											<span className="text-gray-500">Blood Group</span>
											<span className="font-medium text-gray-900">
												{bloodGroupText}
											</span>
										</div>
									)}
									{profile.phone && (
										<div className="flex justify-between">
											<span className="text-gray-500">Phone</span>
											<span className="font-medium text-gray-900">
												{profile.phone}
											</span>
										</div>
									)}
									{profile.email && (
										<div className="flex justify-between items-start">
											<span className="text-gray-500 flex-shrink-0">
												Email
											</span>
											<span className="font-medium text-gray-900 text-xs break-all text-right ml-2">
												{profile.email}
											</span>
										</div>
									)}
									{doctor.startDate && (
										<div className="flex justify-between">
											<span className="text-gray-500">Practicing Since</span>
											<span className="font-medium text-gray-900">
												{doctor.startDate.slice(0, 4)}
											</span>
										</div>
									)}
								</div>
							</CardContent>
						</Card>
					</div>
				</div>
			</div>
		</div>
	);
}

function LoadingSkeleton() {
	return (
		<div className="min-h-screen bg-gray-50">
			<div className="bg-white border-b px-4 py-3">
				<div className="max-w-5xl mx-auto h-5 w-32 bg-gray-200 rounded animate-pulse" />
			</div>
			<div className="max-w-5xl mx-auto px-4 py-8 space-y-6">
				<div className="bg-white rounded-xl p-8 border">
					<div className="flex gap-8">
						<div className="w-32 h-32 rounded-2xl bg-gray-200 animate-pulse flex-shrink-0" />
						<div className="flex-1 space-y-3">
							<div className="h-8 w-64 bg-gray-200 rounded animate-pulse" />
							<div className="h-5 w-40 bg-gray-200 rounded animate-pulse" />
							<div className="flex gap-2">
								<div className="h-6 w-20 bg-gray-200 rounded-full animate-pulse" />
								<div className="h-6 w-28 bg-gray-200 rounded-full animate-pulse" />
							</div>
						</div>
					</div>
				</div>
				<div className="grid grid-cols-3 gap-6">
					<div className="col-span-2 space-y-6">
						<div className="bg-white rounded-xl p-6 border h-48 animate-pulse" />
						<div className="bg-white rounded-xl p-6 border h-64 animate-pulse" />
					</div>
					<div className="space-y-6">
						<div className="bg-white rounded-xl p-6 border h-48 animate-pulse" />
					</div>
				</div>
			</div>
		</div>
	);
}

function ErrorState() {
	return (
		<div className="min-h-screen bg-gray-50 flex items-center justify-center">
			<div className="text-center">
				<UserCheck className="w-16 h-16 text-gray-300 mx-auto mb-4" />
				<h2 className="text-xl font-semibold text-gray-700 mb-2">
					Doctor not found
				</h2>
				<p className="text-gray-500 mb-6">
					We couldn&apos;t find the doctor you&apos;re looking for.
				</p>
				<Link href="/doctors">
					<Button
						variant="outline"
						className="border-teal-600 text-teal-600 hover:bg-teal-50"
					>
						<ArrowLeft className="w-4 h-4 mr-2" />
						Back to Doctors
					</Button>
				</Link>
			</div>
		</div>
	);
}
