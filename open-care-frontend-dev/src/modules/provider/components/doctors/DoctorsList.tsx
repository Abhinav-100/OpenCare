"use client";

import DoctorCard from "./DoctorCard";
import DoctorPagination from "./DoctorPagination";

interface Doctor {
	id: string;
	name: string;
	image?: string;
	experience: number;
	rating: number;
	reviews: number;
	availability: string;
	responseTime: string;
	degrees: string[];
	specialties: string[];
	hospital: string;
	location: string;
	consultationFee: number;
	nextAvailable: string;
	availabilityStatus: "available" | "busy" | "unavailable";
}

interface DoctorsListProps {
	doctors: Doctor[];
	totalResults: number;
	currentPage?: number;
	totalPages?: number;
	onPageChange?: (page: number) => void;
}

export default function DoctorsList({
	doctors,
	currentPage = 1,
	totalPages = 1,
	onPageChange,
}: DoctorsListProps) {
	return (
		<div>
			{/* Results Header */}
			<div className="mb-6">
				<h2 className="text-lg font-bold text-gray-900">
					Available doctors
				</h2>
				<p className="text-gray-600 text-sm">
					• Filtered by location and availability
				</p>
			</div>

			{/* Doctor Cards */}
			<div className="space-y-4">
				{doctors.map((doctor) => (
					<DoctorCard key={doctor.id} doctor={doctor} />
				))}
			</div>

			{/* Pagination */}
			<DoctorPagination
				currentPage={currentPage}
				totalPages={totalPages}
				onPageChange={onPageChange ?? (() => {})}
			/>
		</div>
	);
}
