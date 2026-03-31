"use client";

import { Card, CardContent } from "@/modules/platform/components/ui/card";
import {
	Select,
	SelectContent,
	SelectItem,
	SelectTrigger,
	SelectValue,
} from "@/modules/platform/components/ui/select";
import { Hospital } from "@/shared/types/hospitals";
import HospitalCard from "./HospitalCard";
import HospitalPagination from "./HospitalPagination";

interface HospitalsListProps {
	hospitals: (Hospital & {
		doctors?: string;
		services?: string;
	})[];
	totalResults: number;
	currentPage: number;
	onPageChange: (page: number) => void;
	totalPages: number;
}

export default function HospitalsList({
	hospitals,
	currentPage,
	onPageChange,
	totalPages,
}: HospitalsListProps) {
	return (
		<div className="space-y-6">
			{/* Results Header */}
			<Card className="border border-gray-200">
				<CardContent className="p-6">
					<div className="flex justify-between items-center">
						<div>
							<h2 className="text-lg font-bold text-gray-900 mb-1">
								Search Results
							</h2>
							<p className="text-sm text-gray-600">
								Hospitals matching your criteria
							</p>
						</div>

						{/* Sort Options */}
						<div className="flex items-center gap-2">
							<span className="text-sm font-medium text-gray-700">
								Sort by:
							</span>
							<Select defaultValue="relevance">
								<SelectTrigger className="w-32 border-gray-300">
									<SelectValue placeholder="Sort by" />
								</SelectTrigger>
								<SelectContent>
									<SelectItem value="relevance">Relevance</SelectItem>
									<SelectItem value="name">Name</SelectItem>
									<SelectItem value="beds">Number of Beds</SelectItem>
									<SelectItem value="location">Location</SelectItem>
								</SelectContent>
							</Select>
						</div>
					</div>
				</CardContent>
			</Card>

			{/* Hospitals Grid */}
			<div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
				{hospitals.map((hospital) => (
					<HospitalCard key={hospital.id} hospital={hospital} />
				))}
			</div>

			{/* Pagination */}
			<Card className="border border-gray-200">
				<CardContent className="p-6">
					<div className="flex justify-between items-center">
						<p className="text-sm text-gray-600">
							Use pagination to browse hospitals
						</p>

						<HospitalPagination
							currentPage={currentPage}
							totalPages={totalPages}
							onPageChange={onPageChange}
						/>
					</div>
				</CardContent>
			</Card>
		</div>
	);
}
