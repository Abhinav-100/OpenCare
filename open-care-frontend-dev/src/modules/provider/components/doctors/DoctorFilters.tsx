"use client";

import { useState } from "react";
import { useQuery } from "@tanstack/react-query";
import {
	Search,
	Stethoscope,
	Calendar,
	DollarSign,
	Star,
} from "lucide-react";
import { Input } from "@/modules/platform/components/ui/input";
import {
	Select,
	SelectContent,
	SelectItem,
	SelectTrigger,
	SelectValue,
} from "@/modules/platform/components/ui/select";
import { fetchAllMedicalSpecialities } from "@/modules/clinical/api/medical-specialities";
import { MedicalSpeciality } from "@/shared/types/medical-specialities";

interface DoctorFiltersProps {
	onFilterChange: (params: Record<string, unknown>) => void;
	initialName?: string;
}

export default function DoctorFilters({ onFilterChange, initialName = "" }: DoctorFiltersProps) {
	const [name, setName] = useState(initialName);
	const [specialityId, setSpecialityId] = useState("");

	// Fetch all specialities from API
	const { data: specialities = [] } = useQuery<MedicalSpeciality[]>({
		queryKey: ["all-medical-specialities"],
		queryFn: fetchAllMedicalSpecialities,
	});

	const buildParams = (n: string, s: string): Record<string, unknown> => ({
		...(n.trim() && { name: n.trim() }),
		...(s && { specialityId: Number(s) }),
	});

	const handleNameKeyDown = (e: React.KeyboardEvent<HTMLInputElement>) => {
		if (e.key === "Enter") {
			onFilterChange(buildParams(name, specialityId));
		}
	};

	const handleSpecialityChange = (value: string) => {
		const s = value === "all" ? "" : value;
		setSpecialityId(s);
		onFilterChange(buildParams(name, s));
	};

	return (
		<div className="bg-white border border-gray-200 rounded-xl p-5 mb-6">
			<div className="flex flex-col lg:flex-row gap-4 items-center">
				{/* Search Bar — fires on Enter */}
				<div className="relative flex-1 max-w-md">
					<Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 w-5 h-5" />
					<Input
						placeholder="Search doctors by name..."
						value={name}
						onChange={(e) => setName(e.target.value)}
						onKeyDown={handleNameKeyDown}
						className="pl-10 h-10 border-gray-300 focus:border-teal-500"
					/>
				</div>

				{/* Filter Dropdowns */}
				<div className="flex flex-wrap gap-3">
					{/* Specialty — from API, fires immediately */}
					<Select
						value={specialityId || "all"}
						onValueChange={handleSpecialityChange}
					>
						<SelectTrigger className="w-36 h-10 border-teal-600 text-teal-600">
							<Stethoscope className="w-4 h-4 mr-2" />
							<SelectValue placeholder="Specialties" />
						</SelectTrigger>
						<SelectContent>
							<SelectItem value="all">All Specialties</SelectItem>
							{specialities.map((s: MedicalSpeciality) => (
								<SelectItem key={s.id} value={String(s.id)}>
									{s.name}
								</SelectItem>
							))}
						</SelectContent>
					</Select>

					{/* Availability — cosmetic only (backend support TBD) */}
					<Select>
						<SelectTrigger className="w-32 h-10 border-teal-600 text-teal-600">
							<Calendar className="w-4 h-4 mr-2" />
							<SelectValue placeholder="Available" />
						</SelectTrigger>
						<SelectContent>
							<SelectItem value="all">All Times</SelectItem>
							<SelectItem value="today">Available Today</SelectItem>
							<SelectItem value="tomorrow">Available Tomorrow</SelectItem>
							<SelectItem value="this-week">This Week</SelectItem>
						</SelectContent>
					</Select>

					{/* Fee Range — cosmetic only */}
					<Select>
						<SelectTrigger className="w-32 h-10 border-teal-600 text-teal-600">
							<DollarSign className="w-4 h-4 mr-2" />
							<SelectValue placeholder="Fee Range" />
						</SelectTrigger>
						<SelectContent>
							<SelectItem value="all">All Fees</SelectItem>
							<SelectItem value="0-500">₹0 - ₹500</SelectItem>
							<SelectItem value="500-1000">₹500 - ₹1,000</SelectItem>
							<SelectItem value="1000-1500">₹1,000 - ₹1,500</SelectItem>
							<SelectItem value="1500+">₹1,500+</SelectItem>
						</SelectContent>
					</Select>

					{/* Rating — cosmetic only */}
					<Select>
						<SelectTrigger className="w-24 h-10 border-teal-600 text-teal-600">
							<Star className="w-4 h-4 mr-2" />
							<SelectValue placeholder="Rating" />
						</SelectTrigger>
						<SelectContent>
							<SelectItem value="all">All</SelectItem>
							<SelectItem value="4.5+">4.5+</SelectItem>
							<SelectItem value="4.0+">4.0+</SelectItem>
							<SelectItem value="3.5+">3.5+</SelectItem>
						</SelectContent>
					</Select>
				</div>

				{/* Sort — cosmetic only */}
				<Select>
					<SelectTrigger className="w-40 h-10 bg-teal-600 text-white border-teal-600">
						<SelectValue placeholder="Sort by Rating" />
					</SelectTrigger>
					<SelectContent>
						<SelectItem value="rating">Sort by Rating</SelectItem>
						<SelectItem value="fee-low">Fee: Low to High</SelectItem>
						<SelectItem value="fee-high">Fee: High to Low</SelectItem>
						<SelectItem value="experience">Experience</SelectItem>
					</SelectContent>
				</Select>
			</div>
		</div>
	);
}
