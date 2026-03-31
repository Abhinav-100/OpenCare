"use client";

import { useState } from "react";
import { useQuery } from "@tanstack/react-query";
import { useRouter } from "next/navigation";
import { Plus } from "lucide-react";
import { fetchInstitutions } from "@/modules/provider/api/institutions";
import { InstitutionListResponse } from "@/shared/types/institutions";
import { AdminHeader } from "@/modules/admin/components/admin-header";
import { Button } from "@/modules/platform/components/ui/button";
import { Card, CardContent } from "@/modules/platform/components/ui/card";
import { Skeleton } from "@/modules/platform/components/ui/skeleton";
import { usePermissions } from "@/modules/platform/hooks/use-permissions";
import { columns } from "./columns";
import { DataTable } from "./data-table";

export default function InstitutionsPage() {
  const [currentPage, setCurrentPage] = useState(1);
  const router = useRouter();
  const { hasPermission } = usePermissions();
  const canCreateInstitution = hasPermission("create-institution");

  const {
    data: institutionsData,
    isLoading,
    isError,
    error,
  } = useQuery<InstitutionListResponse>({
    queryKey: ["institutions", currentPage],
    queryFn: () =>
      fetchInstitutions({
        page: currentPage - 1, // UI is 1-based, API is 0-based
        size: 10,
      }),
    placeholderData: (previousData) => previousData,
  });

  const handlePageChange = (page: number) => {
    setCurrentPage(page);
  };

  if (isError) {
    return (
      <Card>
        <CardContent className="pt-6">
          <div className="text-center text-red-600">
            Error loading institutions:{" "}
            {(error as Error)?.message || "Unknown error"}
          </div>
        </CardContent>
      </Card>
    );
  }

  return (
    <div className="flex flex-col">
      <AdminHeader
        title="Institutions Management"
        description="Manage and monitor educational institutions and their information"
      >
        {canCreateInstitution && (
          <Button onClick={() => router.push("/admin/institutions/add")}>
            <Plus className="mr-2 h-4 w-4" />
            Add Institution
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
                data={institutionsData?.institutions || []}
                totalItems={institutionsData?.totalItems}
                currentPage={institutionsData?.currentPage !== undefined ? institutionsData.currentPage + 1 : currentPage}
                totalPages={institutionsData?.totalPages}
                onPageChange={handlePageChange}
              />
            )}
          </CardContent>
        </Card>
      </div>
    </div>
  );
}
