"use client";

import { useState, useCallback } from "react";
import { useQuery } from "@tanstack/react-query";
import { useSearchParams } from "next/navigation";
import DoctorFilters from "@/modules/provider/components/doctors/DoctorFilters";
import DoctorsList from "@/modules/provider/components/doctors/DoctorsList";
import { fetchDoctors } from "@/modules/provider/api/doctors";
import { DoctorResponse } from "@/shared/types/doctors";

function mapDoctor(dr: DoctorResponse) {
	return {
		id: String(dr.id),
		name: dr.profile?.name ?? "Unknown",
		image: dr.profile?.imageUrl ?? undefined,
		experience: dr.yearOfExperience ?? 0,
		rating: 0,
		reviews: 0,
		availability: dr.isActive ? "Available" : "Unavailable",
		responseTime: "",
		degrees: dr.degrees
			? dr.degrees.split(",").map((d) => d.trim())
			: [],
		specialties: dr.specializations
			? dr.specializations.split(",").map((s) => s.trim())
			: [],
		hospital: "",
		location: "",
		consultationFee: dr.consultationFeeOnline ?? dr.consultationFeeOffline ?? 0,
		nextAvailable: "",
		availabilityStatus: (
			dr.isActive ? "available" : "unavailable"
		) as "available" | "busy" | "unavailable",
	};
}

export default function DoctorsView() {
	const searchParams = useSearchParams();
	const initialName = searchParams.get("name") ?? "";

	const [currentPage, setCurrentPage] = useState(1);
	const [filterParams, setFilterParams] = useState<Record<string, unknown>>(
		initialName ? { name: initialName } : {}
	);

	const handleFilterChange = useCallback((params: Record<string, unknown>) => {
		setFilterParams(params);
		setCurrentPage(1);
	}, []);

	const { data, isLoading, isError } = useQuery({
		queryKey: ["public-doctors", currentPage, filterParams],
		queryFn: () =>
			fetchDoctors({
				page: currentPage - 1,
				size: 10,
				...filterParams,
			}),
		placeholderData: (prev) => prev,
	});

	const doctors = (data?.doctors ?? []).map(mapDoctor);
	const totalResults = data?.totalItems ?? 0;
	const totalPages = data?.totalPages ?? 1;

	return (
		<div className="min-h-screen bg-gray-50">
			{/* Header Section */}
			<div className="bg-gradient-to-r from-teal-600 to-teal-700 py-12">
				<div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
					<div className="text-center text-white">
						<h1 className="text-3xl font-bold mb-2">Find Your Doctor</h1>
						<p className="text-lg mb-2">
							Browse through our network of qualified healthcare professionals
						</p>
						<p className="text-sm">📍 Odisha • Verified doctors available</p>
					</div>
				</div>
			</div>

			{/* Main Content */}
			<div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
				<DoctorFilters onFilterChange={handleFilterChange} initialName={initialName} />

				{isError && (
					<div className="text-center py-12 text-red-600">
						Failed to load doctors. Please try again.
					</div>
				)}

				{isLoading && !data && (
					<div className="text-center py-12 text-gray-500">
						Loading doctors...
					</div>
				)}

				{!isError && (
					<DoctorsList
						doctors={doctors}
						totalResults={totalResults}
						currentPage={currentPage}
						totalPages={totalPages}
						onPageChange={setCurrentPage}
					/>
				)}
			</div>
		</div>
	);
}
