"use client";

import { useState } from "react";
import { useQuery } from "@tanstack/react-query";
import { useRouter } from "next/navigation";
import { Plus } from "lucide-react";
import { fetchHospitals } from "@/modules/provider/api/hospitals";
import { fetchUpazilas, fetchUnionsByUpazila } from "@/modules/catalog/api/locations";
import { HospitalListResponse } from "@/shared/types/hospitals";
import { AdminHeader } from "@/modules/admin/components/admin-header";
import { Button } from "@/modules/platform/components/ui/button";
import { Card, CardContent } from "@/modules/platform/components/ui/card";
import { Skeleton } from "@/modules/platform/components/ui/skeleton";
import { usePermissions } from "@/modules/platform/hooks/use-permissions";
import { columns } from "./columns";
import { DataTable } from "./data-table";

// Page flow: This route renders a screen entry and delegates business/data logic to module components.
export default function HospitalsPage() {
	const [currentPage, setCurrentPage] = useState(1);
	const [filters, setFilters] = useState({
		search: "",
		upazilaId: "",
		unionId: "",
	});
	const router = useRouter();
	const { hasPermission, permissions } = usePermissions();
	const canCreateHospital =
		hasPermission("create-hospital") ||
		permissions.includes("admin") ||
		permissions.includes("super-admin") ||
		permissions.includes("superadmin");

	// Fetch hospitals with filters
	const {
		data: hospitalsData,
		isLoading,
		isError,
		error,
	} = useQuery<HospitalListResponse>({
		queryKey: ["hospitals", currentPage, filters],
		queryFn: () =>
			fetchHospitals({
				page: currentPage - 1, // UI is 1-based, API is 0-based
				size: 10,
				name: filters.search || undefined,
				upazilaId: filters.upazilaId || undefined,
				unionId: filters.unionId || undefined,
			}),
		placeholderData: (previousData) => previousData,
	});

	// Fetch upazilas for filter dropdown
	const { data: upazilas = [], isLoading: isUpazilasLoading } = useQuery({
		queryKey: ["upazilas"],
		queryFn: fetchUpazilas,
	});

	// Fetch unions for filter dropdown
	const { data: unions = [], isLoading: isUnionsLoading } = useQuery({
		queryKey: ["unions", filters.upazilaId],
		queryFn: async () => {
			if (!filters.upazilaId) {
				return [];
			}

			try {
				return await fetchUnionsByUpazila(filters.upazilaId);
			} catch (error) {
				// Some upazilas may legitimately have no union records on backend.
				if (error instanceof Error && error.message.includes("404")) {
					return [];
				}
				throw error;
			}
		},
		enabled: Boolean(filters.upazilaId),
	});

	const handlePageChange = (page: number) => {
		setCurrentPage(page);
	};

	const handleFilterChange = (newFilters: {
		search?: string;
		upazilaId?: string;
		unionId?: string;
	}) => {
		setFilters((prev) => ({ ...prev, ...newFilters }));
		setCurrentPage(1); // Reset to first page when filters change
	};

	if (isError) {
		return (
			<Card>
				<CardContent className="pt-6">
					<div className="text-center text-red-600">
						Error loading hospitals:{" "}
						{(error as Error)?.message || "Unknown error"}
					</div>
				</CardContent>
			</Card>
		);
	}

	return (
		<div className="flex flex-col">
			<AdminHeader
				title="Hospitals Management"
				description="Manage and monitor hospital registrations and information"
			>
				{canCreateHospital && (
					<Button onClick={() => router.push("/admin/hospitals/add")}>
						<Plus className="mr-2 h-4 w-4" />
						Add Hospital
					</Button>
				)}
			</AdminHeader>

			<div className="flex-1 space-y-4 p-4 md:p-8 pt-6">
				<Card>
					<CardContent className="pt-6">
						{isLoading ? (
							<div className="space-y-4">
								<Skeleton className="h-8 w-full" />
								<Skeleton className="h-8 w-full" />
								<Skeleton className="h-8 w-full" />
								<Skeleton className="h-8 w-full" />
								<Skeleton className="h-8 w-full" />
							</div>
						) : (
							<DataTable
								columns={columns}
								data={hospitalsData?.hospitals || []}
								totalItems={hospitalsData?.totalItems}
								currentPage={hospitalsData?.currentPage !== undefined ? hospitalsData.currentPage + 1 : currentPage}
								totalPages={hospitalsData?.totalPages}
								onPageChange={handlePageChange}
								onFilterChange={handleFilterChange}
								upazilas={upazilas}
								unions={unions}
								isUpazilasLoading={isUpazilasLoading}
								isUnionsLoading={isUnionsLoading}
							/>
						)}
					</CardContent>
				</Card>
			</div>
		</div>
	);
}
