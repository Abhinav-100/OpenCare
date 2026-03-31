"use client";

import { useParams, useRouter } from "next/navigation";
import { useQuery } from "@tanstack/react-query";
import { ArrowLeft } from "lucide-react";
import { fetchInstitutionById } from "@/modules/provider/api/institutions";
import { Institution } from "@/shared/types/institutions";
import { AdminHeader } from "@/modules/admin/components/admin-header";
import { Button } from "@/modules/platform/components/ui/button";
import { Card, CardContent, CardHeader } from "@/modules/platform/components/ui/card";
import { Skeleton } from "@/modules/platform/components/ui/skeleton";
import { Alert, AlertDescription } from "@/modules/platform/components/ui/alert";
import { DetailView } from "@/modules/admin/components/detail-view";
import { buildInstitutionDetailSections } from "@/modules/admin/components/build-institution-sections";

export default function InstitutionViewPage() {
	const params = useParams();
	const router = useRouter();
	const institutionId = params.id as string;

	const {
		data: institution,
		isLoading,
		isError,
		error,
	} = useQuery<Institution>({
		queryKey: ["institution", institutionId],
		queryFn: () => fetchInstitutionById(institutionId),
		enabled: !!institutionId,
	});

	if (isLoading) {
		return (
			<div className="flex-1 space-y-4 p-4 md:p-8 pt-6">
				<AdminHeader
					title="Institution Details"
					description="Loading institution information..."
				>
					<Button
						variant="outline"
						onClick={() => router.back()}
						className="flex items-center gap-2"
					>
						<ArrowLeft className="h-4 w-4" />
						Back
					</Button>
				</AdminHeader>

				<div className="grid gap-6">
					<Card>
						<CardHeader>
							<Skeleton className="h-8 w-48" />
							<Skeleton className="h-4 w-32" />
						</CardHeader>
						<CardContent className="space-y-4">
							<Skeleton className="h-4 w-full" />
							<Skeleton className="h-4 w-3/4" />
							<Skeleton className="h-4 w-1/2" />
						</CardContent>
					</Card>
				</div>
			</div>
		);
	}

	if (isError) {
		return (
			<div className="flex-1 space-y-4 p-4 md:p-8 pt-6">
				<AdminHeader
					title="Institution Details"
					description="Error loading institution"
				>
					<Button
						variant="outline"
						onClick={() => router.back()}
						className="flex items-center gap-2"
					>
						<ArrowLeft className="h-4 w-4" />
						Back
					</Button>
				</AdminHeader>

				<Alert variant="destructive">
					<AlertDescription>
						{error instanceof Error
							? error.message
							: "Failed to load institution details"}
					</AlertDescription>
				</Alert>
			</div>
		);
	}

	if (!institution) {
		return (
			<div className="flex-1 space-y-4 p-4 md:p-8 pt-6">
				<AdminHeader
					title="Institution Details"
					description="Institution not found"
				>
					<Button
						variant="outline"
						onClick={() => router.back()}
						className="flex items-center gap-2"
					>
						<ArrowLeft className="h-4 w-4" />
						Back
					</Button>
				</AdminHeader>

				<Alert>
					<AlertDescription>
						Institution not found or may have been deleted.
					</AlertDescription>
				</Alert>
			</div>
		);
	}

	return (
		<div className="flex-1 space-y-4 p-4 md:p-8 pt-6">
			<AdminHeader
				title={institution.name}
				description={`${
					institution.acronym || institution.bnName
				} - Institution Details`}
			>
				<Button
					variant="outline"
					onClick={() => router.back()}
					className="flex items-center gap-2"
				>
					<ArrowLeft className="h-4 w-4" />
					Back
				</Button>
			</AdminHeader>

			<DetailView sections={buildInstitutionDetailSections(institution)} />
		</div>
	);
}
