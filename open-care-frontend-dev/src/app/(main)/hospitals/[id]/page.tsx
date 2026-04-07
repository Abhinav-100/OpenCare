"use client";

import { useParams } from "next/navigation";
import { useQuery } from "@tanstack/react-query";
import Link from "next/link";
import {
	Building2,
	MapPin,
	Phone,
	Mail,
	Globe,
	Facebook,
	Twitter,
	Bed,
	Shield,
	Ambulance,
	HeartPulse,
	FlaskConical,
	ArrowLeft,
	Stethoscope,
	CheckCircle2,
	XCircle,
} from "lucide-react";
import { Badge } from "@/modules/platform/components/ui/badge";
import { Button } from "@/modules/platform/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/modules/platform/components/ui/card";
import { fetchHospitalDetailsById } from "@/modules/provider/api/hospitals";

// Page flow: This route renders a screen entry and delegates business/data logic to module components.
export default function HospitalDetailPage() {
	const { id } = useParams<{ id: string }>();

	const {
		data: hospital,
		isLoading,
		isError,
	} = useQuery({
		queryKey: ["hospital-detail", id],
		queryFn: () => fetchHospitalDetailsById(Number(id)),
		enabled: !!id,
	});

	if (isLoading) return <LoadingSkeleton />;
	if (isError || !hospital) return <ErrorState />;

	const orgType =
		typeof hospital.organizationType === "string"
			? hospital.organizationType
			: hospital.organizationType?.displayName;

	const hospType =
		typeof hospital.hospitalType === "string"
			? hospital.hospitalType
			: hospital.hospitalType?.englishName;

	const orgTypeValue =
		typeof hospital.organizationType === "string"
			? hospital.organizationType
			: hospital.organizationType?.value;

	const orgColor =
		orgTypeValue === "GOVERNMENT"
			? "bg-teal-600 text-white"
			: orgTypeValue === "PRIVATE"
			? "bg-emerald-600 text-white"
			: "bg-gray-500 text-white";

	return (
		<div className="min-h-screen bg-gray-50">
			{/* Back nav */}
			<div className="bg-white border-b px-4 py-3">
				<div className="max-w-5xl mx-auto">
					<Link
						href="/hospitals"
						className="inline-flex items-center text-teal-600 hover:text-teal-700 text-sm font-medium"
					>
						<ArrowLeft className="w-4 h-4 mr-1" />
						Back to Hospitals
					</Link>
				</div>
			</div>

			<div className="max-w-5xl mx-auto px-4 py-8 space-y-6">
				{/* Hero Card */}
				<Card>
					<CardContent className="p-8">
						<div className="flex gap-6">
							{/* Icon */}
							<div className="w-24 h-24 rounded-2xl bg-teal-50 border-2 border-teal-600 flex items-center justify-center flex-shrink-0">
								{hospital.imageUrl ? (
									// eslint-disable-next-line @next/next/no-img-element
									<img
										src={hospital.imageUrl}
										alt={hospital.name}
										className="w-full h-full rounded-2xl object-cover"
									/>
								) : (
									<Building2 className="w-12 h-12 text-teal-600" />
								)}
							</div>

							{/* Info */}
							<div className="flex-1">
								<div className="flex items-start justify-between gap-4">
									<div>
										<h1 className="text-2xl font-bold text-gray-900 mb-0.5">
											{hospital.name}
										</h1>
										{hospital.bnName && (
											<p className="text-teal-600 font-medium text-base mb-3">
												{hospital.bnName}
											</p>
										)}
										<div className="flex items-center gap-2 flex-wrap mb-3">
											{hospital.isVerified && (
												<Badge className="bg-teal-600 text-white">
													<Shield className="w-3 h-3 mr-1" />
													Verified
												</Badge>
											)}
											<Badge variant="outline" className="border-gray-400 text-gray-700">
												{hospType}
											</Badge>
											<Badge className={orgColor}>{orgType}</Badge>
											<Badge variant="outline" className="border-gray-400 text-gray-700">
												<Bed className="w-3 h-3 mr-1" />
												{hospital.numberOfBed} Beds
											</Badge>
										</div>
									</div>
								</div>

								{/* Location */}
								{(hospital.address || hospital.district) && (
									<div className="flex items-start text-gray-600 text-sm mb-3">
										<MapPin className="w-4 h-4 mr-1 mt-0.5 text-teal-500 flex-shrink-0" />
										<span>
											{[
												hospital.address,
												hospital.upazila?.name,
												hospital.district?.name,
											]
												.filter(Boolean)
												.join(", ")}
										</span>
									</div>
								)}

								{/* Social / web links */}
								<div className="flex gap-3 mt-1">
									{hospital.websiteUrl && (
										<a
											href={hospital.websiteUrl.startsWith("http") ? hospital.websiteUrl : `https://${hospital.websiteUrl}`}
											target="_blank"
											rel="noopener noreferrer"
											className="text-gray-400 hover:text-teal-600 transition-colors"
										>
											<Globe className="w-5 h-5" />
										</a>
									)}
									{hospital.facebookPageUrl && (
										<a
											href={hospital.facebookPageUrl}
											target="_blank"
											rel="noopener noreferrer"
											className="text-gray-400 hover:text-blue-600 transition-colors"
										>
											<Facebook className="w-5 h-5" />
										</a>
									)}
									{hospital.twitterProfileUrl && (
										<a
											href={hospital.twitterProfileUrl}
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

						{/* Services row */}
						<div className="mt-6 pt-6 border-t flex flex-wrap gap-3">
							<ServiceBadge
								label="Emergency Service"
								active={!!hospital.hasEmergencyService}
								icon={<HeartPulse className="w-4 h-4" />}
								activeColor="bg-red-100 text-red-700 border-red-300"
							/>
							<ServiceBadge
								label="Ambulance"
								active={!!hospital.hasAmbulanceService}
								icon={<Ambulance className="w-4 h-4" />}
								activeColor="bg-blue-100 text-blue-700 border-blue-300"
							/>
							<ServiceBadge
								label="Blood Bank"
								active={!!hospital.hasBloodBank}
								icon={<FlaskConical className="w-4 h-4" />}
								activeColor="bg-rose-100 text-rose-700 border-rose-300"
							/>
							<ServiceBadge
								label="Affiliated"
								active={!!hospital.isAffiliated}
								icon={<Shield className="w-4 h-4" />}
								activeColor="bg-purple-100 text-purple-700 border-purple-300"
							/>
						</div>
					</CardContent>
				</Card>

				{/* Body Grid */}
				<div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
					{/* Left — main content */}
					<div className="lg:col-span-2 space-y-6">
						{/* Doctors on Staff */}
						{hospital.doctors && hospital.doctors.length > 0 ? (
							<Card>
								<CardHeader>
									<CardTitle className="text-lg text-gray-900 flex items-center gap-2">
										<Stethoscope className="w-5 h-5 text-teal-600" />
										Doctors on Staff
										<span className="text-sm font-normal text-gray-500 ml-1">
											({hospital.doctors.length})
										</span>
									</CardTitle>
								</CardHeader>
								<CardContent>
									<div className="space-y-3">
										{hospital.doctors.map((doc) => {
											const name = doc.profile?.name ?? "Unknown";
											const speciality =
												doc.doctorWorkplaces?.[0]?.medicalSpeciality?.name ??
												doc.specializations?.split(",")[0]?.trim() ??
												null;
											return (
												<Link
													key={doc.id}
													href={`/doctors/${doc.id}`}
													className="flex items-center gap-4 p-3 rounded-lg border border-gray-100 hover:bg-teal-50 hover:border-teal-200 transition-colors"
												>
													<div className="w-10 h-10 rounded-full bg-teal-50 border border-teal-200 flex items-center justify-center flex-shrink-0">
														{doc.profile?.photo ? (
															// eslint-disable-next-line @next/next/no-img-element
															<img
																src={doc.profile.photo}
																alt={name}
																className="w-full h-full rounded-full object-cover"
															/>
														) : (
															<Stethoscope className="w-5 h-5 text-teal-600" />
														)}
													</div>
													<div className="flex-1">
														<div className="font-semibold text-gray-900 text-sm">
															{name}
														</div>
														{speciality && (
															<div className="text-xs text-teal-600">{speciality}</div>
														)}
													</div>
													{doc.isVerified && (
														<Badge className="bg-teal-600 text-white text-xs">
															Verified
														</Badge>
													)}
												</Link>
											);
										})}
									</div>
								</CardContent>
							</Card>
						) : (
							<Card>
								<CardHeader>
									<CardTitle className="text-lg text-gray-900 flex items-center gap-2">
										<Stethoscope className="w-5 h-5 text-teal-600" />
										Doctors on Staff
									</CardTitle>
								</CardHeader>
								<CardContent>
									<p className="text-gray-500 text-sm">
										No doctors listed for this hospital yet.
									</p>
								</CardContent>
							</Card>
						)}

						{/* Medical Tests */}
						{hospital.tests && hospital.tests.length > 0 && (
							<Card>
								<CardHeader>
									<CardTitle className="text-lg text-gray-900 flex items-center gap-2">
										<FlaskConical className="w-5 h-5 text-teal-600" />
										Available Tests
										<span className="text-sm font-normal text-gray-500 ml-1">
											({hospital.tests.length})
										</span>
									</CardTitle>
								</CardHeader>
								<CardContent>
									<div className="space-y-2">
										{hospital.tests.map((t) => (
											<div
												key={t.id}
												className="flex items-center justify-between py-2 border-b last:border-0"
											>
												<div>
													<div className="text-sm font-medium text-gray-900">
														{t.name || t.medicalTest?.name}
													</div>
													{t.category && (
														<div className="text-xs text-gray-500">{t.category}</div>
													)}
												</div>
												<div className="flex items-center gap-3">
													{t.price > 0 && (
														<span className="text-sm font-semibold text-teal-700">
															₹{t.price}
														</span>
													)}
													<span
														className={`text-xs px-2 py-0.5 rounded-full ${
															t.isAvailable
																? "bg-green-100 text-green-700"
																: "bg-gray-100 text-gray-500"
														}`}
													>
														{t.isAvailable ? "Available" : "Unavailable"}
													</span>
												</div>
											</div>
										))}
									</div>
								</CardContent>
							</Card>
						)}

						{/* Amenities */}
						{hospital.amenities && hospital.amenities.length > 0 && (
							<Card>
								<CardHeader>
									<CardTitle className="text-lg text-gray-900 flex items-center gap-2">
										<Building2 className="w-5 h-5 text-teal-600" />
										Amenities
									</CardTitle>
								</CardHeader>
								<CardContent>
									<div className="grid grid-cols-2 gap-3">
										{hospital.amenities.map((a) => (
											<div
												key={a.id}
												className="flex items-center gap-2 p-3 rounded-lg bg-gray-50 border"
											>
												<CheckCircle2 className="w-4 h-4 text-teal-600 flex-shrink-0" />
												<div className="text-sm text-gray-900">{a.name}</div>
											</div>
										))}
									</div>
								</CardContent>
							</Card>
						)}
					</div>

					{/* Right — sidebar */}
					<div className="space-y-6">
						{/* Quick Info */}
						<Card>
							<CardHeader>
								<CardTitle className="text-lg text-gray-900">Quick Info</CardTitle>
							</CardHeader>
							<CardContent>
								<div className="space-y-3 text-sm">
									{hospital.registrationCode && (
										<div className="flex justify-between">
											<span className="text-gray-500">Reg. Code</span>
											<span className="font-medium text-gray-900 font-mono text-xs">
												{hospital.registrationCode}
											</span>
										</div>
									)}
									<div className="flex justify-between">
										<span className="text-gray-500">Total Beds</span>
										<span className="font-medium text-gray-900">
											{hospital.numberOfBed}
										</span>
									</div>
									<div className="flex justify-between">
										<span className="text-gray-500">Type</span>
										<span className="font-medium text-gray-900 text-xs text-right max-w-[120px]">
											{hospType}
										</span>
									</div>
									<div className="flex justify-between">
										<span className="text-gray-500">Organisation</span>
										<span className="font-medium text-gray-900">{orgType}</span>
									</div>
									{hospital.district && (
										<div className="flex justify-between">
											<span className="text-gray-500">District</span>
											<span className="font-medium text-gray-900">
												{hospital.district.name}
											</span>
										</div>
									)}
									{hospital.upazila && (
										<div className="flex justify-between">
											<span className="text-gray-500">Block</span>
											<span className="font-medium text-gray-900">
												{hospital.upazila.name}
											</span>
										</div>
									)}
								</div>
							</CardContent>
						</Card>

						{/* Contact */}
						{(hospital.phone || hospital.email || hospital.websiteUrl) && (
							<Card>
								<CardHeader>
									<CardTitle className="text-lg text-gray-900">Contact</CardTitle>
								</CardHeader>
								<CardContent>
									<div className="space-y-3 text-sm">
										{hospital.phone && (
											<div className="flex items-center gap-2 text-gray-700">
												<Phone className="w-4 h-4 text-teal-500 flex-shrink-0" />
												<a
													href={`tel:${hospital.phone}`}
													className="hover:text-teal-600"
												>
													{hospital.phone}
												</a>
											</div>
										)}
										{hospital.email && (
											<div className="flex items-center gap-2 text-gray-700">
												<Mail className="w-4 h-4 text-teal-500 flex-shrink-0" />
												<a
													href={`mailto:${hospital.email}`}
													className="hover:text-teal-600 break-all text-xs"
												>
													{hospital.email}
												</a>
											</div>
										)}
										{hospital.websiteUrl && (
											<div className="flex items-center gap-2 text-gray-700">
												<Globe className="w-4 h-4 text-teal-500 flex-shrink-0" />
												<a
													href={hospital.websiteUrl.startsWith("http") ? hospital.websiteUrl : `https://${hospital.websiteUrl}`}
													target="_blank"
													rel="noopener noreferrer"
													className="hover:text-teal-600 break-all text-xs"
												>
													{hospital.websiteUrl}
												</a>
											</div>
										)}
									</div>
								</CardContent>
							</Card>
						)}

						{/* Tags */}
						{hospital.tags && hospital.tags.length > 0 && (
							<Card>
								<CardHeader>
									<CardTitle className="text-lg text-gray-900">Tags</CardTitle>
								</CardHeader>
								<CardContent>
									<div className="flex flex-wrap gap-2">
										{hospital.tags.map((tag) => (
											<Badge key={tag.id} variant="outline" className="text-xs">
												{tag.displayName || tag.name}
											</Badge>
										))}
									</div>
								</CardContent>
							</Card>
						)}
					</div>
				</div>
			</div>
		</div>
	);
}

// ─── Service Badge ────────────────────────────────────────────────────────────
function ServiceBadge({
	label,
	active,
	icon,
	activeColor,
}: {
	label: string;
	active: boolean;
	icon: React.ReactNode;
	activeColor: string;
}) {
	return (
		<div
			className={`flex items-center gap-1.5 px-3 py-1.5 rounded-full border text-sm font-medium ${
				active ? activeColor : "bg-gray-100 text-gray-400 border-gray-200"
			}`}
		>
			{active ? icon : <XCircle className="w-4 h-4" />}
			{label}
		</div>
	);
}

// ─── Loading Skeleton ─────────────────────────────────────────────────────────
function LoadingSkeleton() {
	return (
		<div className="min-h-screen bg-gray-50">
			<div className="bg-white border-b px-4 py-3">
				<div className="max-w-5xl mx-auto h-5 w-36 bg-gray-200 rounded animate-pulse" />
			</div>
			<div className="max-w-5xl mx-auto px-4 py-8 space-y-6">
				<div className="bg-white rounded-xl p-8 border">
					<div className="flex gap-6">
						<div className="w-24 h-24 rounded-2xl bg-gray-200 animate-pulse flex-shrink-0" />
						<div className="flex-1 space-y-3">
							<div className="h-7 w-72 bg-gray-200 rounded animate-pulse" />
							<div className="h-5 w-48 bg-gray-200 rounded animate-pulse" />
							<div className="flex gap-2">
								<div className="h-6 w-20 bg-gray-200 rounded-full animate-pulse" />
								<div className="h-6 w-24 bg-gray-200 rounded-full animate-pulse" />
								<div className="h-6 w-20 bg-gray-200 rounded-full animate-pulse" />
							</div>
						</div>
					</div>
				</div>
				<div className="grid grid-cols-3 gap-6">
					<div className="col-span-2 space-y-6">
						<div className="bg-white rounded-xl p-6 border h-64 animate-pulse" />
						<div className="bg-white rounded-xl p-6 border h-48 animate-pulse" />
					</div>
					<div className="space-y-6">
						<div className="bg-white rounded-xl p-6 border h-48 animate-pulse" />
						<div className="bg-white rounded-xl p-6 border h-32 animate-pulse" />
					</div>
				</div>
			</div>
		</div>
	);
}

// ─── Error State ──────────────────────────────────────────────────────────────
function ErrorState() {
	return (
		<div className="min-h-screen bg-gray-50 flex items-center justify-center">
			<div className="text-center">
				<Building2 className="w-16 h-16 text-gray-300 mx-auto mb-4" />
				<h2 className="text-xl font-semibold text-gray-700 mb-2">
					Hospital not found
				</h2>
				<p className="text-gray-500 mb-6">
					We couldn&apos;t find the hospital you&apos;re looking for.
				</p>
				<Link href="/hospitals">
					<Button
						variant="outline"
						className="border-teal-600 text-teal-600 hover:bg-teal-50"
					>
						<ArrowLeft className="w-4 h-4 mr-2" />
						Back to Hospitals
					</Button>
				</Link>
			</div>
		</div>
	);
}
