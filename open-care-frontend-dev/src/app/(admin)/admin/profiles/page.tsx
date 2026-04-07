"use client";

import { useState } from "react";
import { useQuery } from "@tanstack/react-query";
import { useRouter } from "next/navigation";
import { Plus } from "lucide-react";
import { getProfiles } from "@/modules/access/api/profile";
import { ProfilesResponse } from "@/modules/access/api/profile";
import { AdminHeader } from "@/modules/admin/components/admin-header";
import { Button } from "@/modules/platform/components/ui/button";
import { Card, CardContent } from "@/modules/platform/components/ui/card";
import { Skeleton } from "@/modules/platform/components/ui/skeleton";
import { getUserSession } from "@/shared/utils/auth-client";
import { usePermissions } from "@/modules/platform/hooks/use-permissions";
import { columns } from "./columns";
import { DataTable } from "./data-table";

// Page flow: This route renders a screen entry and delegates business/data logic to module components.
export default function ProfilesPage() {
  const [currentPage, setCurrentPage] = useState(1);
  const router = useRouter();
  const { hasPermission } = usePermissions();
  const canCreateProfile = hasPermission("create-profile");

  const {
    data: profilesData,
    isLoading,
    isError,
    error,
  } = useQuery<ProfilesResponse>({
    queryKey: ["profiles", currentPage],
    queryFn: () => {
      const session = getUserSession();
      const token = session?.access_token;
      return getProfiles(currentPage - 1, 10, "id", "ASC", token); // UI is 1-based, API is 0-based
    },
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
            Error loading profiles:{" "}
            {(error as Error)?.message || "Unknown error"}
          </div>
        </CardContent>
      </Card>
    );
  }

  return (
    <div className="flex flex-col">
      <AdminHeader
        title="Profile Management"
        description="Manage and monitor user profiles across the platform"
      >
        {canCreateProfile && (
          <Button onClick={() => router.push("/admin/profiles/new")}>
            <Plus className="mr-2 h-4 w-4" />
            Add Profile
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
                data={profilesData?.profiles || []}
                totalItems={profilesData?.totalItems}
                currentPage={profilesData?.currentPage !== undefined ? profilesData.currentPage + 1 : currentPage}
                totalPages={profilesData?.totalPages}
                onPageChange={handlePageChange}
              />
            )}
          </CardContent>
        </Card>
      </div>
    </div>
  );
}
