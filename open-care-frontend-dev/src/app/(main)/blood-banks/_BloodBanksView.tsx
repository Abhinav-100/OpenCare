"use client";

import { useState } from "react";
import { useQuery } from "@tanstack/react-query";
import { Phone, MapPin, Clock, Droplets, Building2, Filter, X } from "lucide-react";
import Link from "next/link";
import { fetchBloodBanks } from "@/modules/blood/api/blood-banks";
import { Card, CardContent } from "@/modules/platform/components/ui/card";
import { Button } from "@/modules/platform/components/ui/button";
import { Badge } from "@/modules/platform/components/ui/badge";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/modules/platform/components/ui/select";
import type { BloodBank } from "@/shared/types/blood-banks";

const BLOOD_GROUPS = [
  "A_POSITIVE", "A_NEGATIVE",
  "B_POSITIVE", "B_NEGATIVE",
  "AB_POSITIVE", "AB_NEGATIVE",
  "O_POSITIVE", "O_NEGATIVE",
];

const BLOOD_GROUP_LABELS: Record<string, string> = {
  A_POSITIVE: "A+",
  A_NEGATIVE: "A-",
  B_POSITIVE: "B+",
  B_NEGATIVE: "B-",
  AB_POSITIVE: "AB+",
  AB_NEGATIVE: "AB-",
  O_POSITIVE: "O+",
  O_NEGATIVE: "O-",
};

export default function BloodBanksView() {
  const [currentPage, setCurrentPage] = useState(1);
  const [selectedBloodGroup, setSelectedBloodGroup] = useState<string>("");
  const [show24x7Only, setShow24x7Only] = useState(false);
  const [showMobileFilters, setShowMobileFilters] = useState(false);

  const filterParams: Record<string, unknown> = {
    page: currentPage - 1,
    size: 12,
    isActive: true,
    ...(selectedBloodGroup && { bloodGroupNeeded: selectedBloodGroup }),
    ...(show24x7Only && { isAlwaysOpen: true }),
  };

  const {
    data,
    isLoading,
    error,
  } = useQuery({
    queryKey: ["blood-banks", currentPage, selectedBloodGroup, show24x7Only],
    queryFn: () => fetchBloodBanks(filterParams),
    placeholderData: (prev) => prev,
  });

  const clearFilters = () => {
    setSelectedBloodGroup("");
    setShow24x7Only(false);
    setCurrentPage(1);
  };

  const hasFilters = selectedBloodGroup || show24x7Only;

  return (
    <div className="min-h-screen bg-gray-50 py-8">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        {/* Header */}
        <div className="mb-8">
          <h1 className="text-3xl font-bold text-gray-900">
            Blood Bank Services
          </h1>
          <p className="text-gray-600 mt-2">
            Find blood banks and check blood availability in your area
          </p>
        </div>

        {/* Emergency Banner */}
        <div className="bg-red-50 border border-red-200 rounded-lg p-4 mb-6">
          <div className="flex items-center gap-3">
            <Droplets className="w-6 h-6 text-red-600" />
            <div>
              <p className="font-semibold text-red-800">Need Blood Urgently?</p>
              <p className="text-sm text-red-600">
                Call the nearest blood bank or visit their website for immediate assistance
              </p>
            </div>
          </div>
        </div>

        {/* Mobile Filter Toggle */}
        <div className="lg:hidden mb-4">
          <Button
            variant="outline"
            onClick={() => setShowMobileFilters(!showMobileFilters)}
            className="w-full"
          >
            <Filter className="w-4 h-4 mr-2" />
            {showMobileFilters ? "Hide Filters" : "Show Filters"}
          </Button>
        </div>

        <div className="flex flex-col lg:flex-row gap-6">
          {/* Filters Sidebar */}
          <div
            className={`lg:w-72 flex-shrink-0 space-y-4 ${
              showMobileFilters ? "block" : "hidden lg:block"
            }`}
          >
            <Card>
              <CardContent className="p-4 space-y-4">
                <div className="flex items-center justify-between">
                  <h3 className="font-semibold">Filters</h3>
                  {hasFilters && (
                    <Button
                      variant="ghost"
                      size="sm"
                      onClick={clearFilters}
                      className="text-red-600 hover:text-red-700"
                    >
                      <X className="w-4 h-4 mr-1" />
                      Clear
                    </Button>
                  )}
                </div>

                {/* Blood Group Filter */}
                <div className="space-y-2">
                  <label className="text-sm font-medium text-gray-700">
                    Blood Group Needed
                  </label>
                  <Select
                    value={selectedBloodGroup || "__all__"}
                    onValueChange={(v) => {
                      setSelectedBloodGroup(v === "__all__" ? "" : v);
                      setCurrentPage(1);
                    }}
                  >
                    <SelectTrigger>
                      <SelectValue placeholder="All Blood Groups" />
                    </SelectTrigger>
                    <SelectContent>
                      <SelectItem value="__all__">All Blood Groups</SelectItem>
                      {BLOOD_GROUPS.map((group) => (
                        <SelectItem key={group} value={group}>
                          {BLOOD_GROUP_LABELS[group]}
                        </SelectItem>
                      ))}
                    </SelectContent>
                  </Select>
                </div>

                {/* 24x7 Toggle */}
                <div className="flex items-center justify-between py-2">
                  <label className="text-sm font-medium text-gray-700">
                    Open 24/7 Only
                  </label>
                  <button
                    onClick={() => {
                      setShow24x7Only(!show24x7Only);
                      setCurrentPage(1);
                    }}
                    className={`relative inline-flex h-6 w-11 items-center rounded-full transition-colors ${
                      show24x7Only ? "bg-teal-600" : "bg-gray-200"
                    }`}
                  >
                    <span
                      className={`inline-block h-4 w-4 transform rounded-full bg-white transition-transform ${
                        show24x7Only ? "translate-x-6" : "translate-x-1"
                      }`}
                    />
                  </button>
                </div>
              </CardContent>
            </Card>
          </div>

          {/* Blood Bank List */}
          <div className="flex-1">
            {isLoading ? (
              <div className="flex items-center justify-center py-12">
                <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-teal-600"></div>
              </div>
            ) : error ? (
              <Card>
                <CardContent className="py-12 text-center">
                  <p className="text-red-600">
                    Failed to load blood banks. Please try again.
                  </p>
                </CardContent>
              </Card>
            ) : data?.bloodBanks.length === 0 ? (
              <Card>
                <CardContent className="py-12 text-center">
                  <Droplets className="w-12 h-12 text-gray-300 mx-auto mb-4" />
                  <h3 className="text-lg font-medium text-gray-900 mb-2">
                    No blood banks found
                  </h3>
                  <p className="text-gray-500">
                    Try adjusting your filters or search in a different area.
                  </p>
                </CardContent>
              </Card>
            ) : (
              <>
                <div className="mb-4 text-sm text-gray-500">
                  Showing {data?.bloodBanks.length} of {data?.totalItems} blood banks
                </div>
                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                  {data?.bloodBanks.map((bloodBank) => (
                    <BloodBankCard key={bloodBank.id} bloodBank={bloodBank} />
                  ))}
                </div>

                {/* Pagination */}
                {data && data.totalPages > 1 && (
                  <div className="mt-6 flex justify-center gap-2">
                    <Button
                      variant="outline"
                      disabled={currentPage === 1}
                      onClick={() => setCurrentPage((p) => p - 1)}
                    >
                      Previous
                    </Button>
                    <span className="px-4 py-2 text-sm text-gray-600">
                      Page {currentPage} of {data.totalPages}
                    </span>
                    <Button
                      variant="outline"
                      disabled={currentPage >= data.totalPages}
                      onClick={() => setCurrentPage((p) => p + 1)}
                    >
                      Next
                    </Button>
                  </div>
                )}
              </>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}

function BloodBankCard({ bloodBank }: { bloodBank: BloodBank }) {
  return (
    <Card className="hover:shadow-md transition-shadow">
      <CardContent className="p-5">
        <div className="flex items-start justify-between mb-3">
          <div className="flex items-center gap-3">
            <div className="w-12 h-12 rounded-full bg-red-100 flex items-center justify-center">
              <Droplets className="w-6 h-6 text-red-600" />
            </div>
            <div>
              <h3 className="font-semibold text-gray-900">{bloodBank.name}</h3>
              {bloodBank.hospital && (
                <p className="text-sm text-gray-500 flex items-center gap-1">
                  <Building2 className="w-3 h-3" />
                  {bloodBank.hospital.name}
                </p>
              )}
            </div>
          </div>
          {bloodBank.isAlwaysOpen && (
            <Badge className="bg-green-100 text-green-800 hover:bg-green-100">
              24/7
            </Badge>
          )}
        </div>

        <div className="space-y-2 text-sm">
          {bloodBank.address && (
            <div className="flex items-start gap-2 text-gray-600">
              <MapPin className="w-4 h-4 mt-0.5 flex-shrink-0" />
              <span className="line-clamp-2">{bloodBank.address}</span>
            </div>
          )}

          {bloodBank.openingHours && !bloodBank.isAlwaysOpen && (
            <div className="flex items-center gap-2 text-gray-600">
              <Clock className="w-4 h-4" />
              <span>{bloodBank.openingHours}</span>
            </div>
          )}
        </div>

        <div className="mt-4 pt-4 border-t flex items-center justify-between">
          {bloodBank.contactNumber ? (
            <a
              href={`tel:${bloodBank.contactNumber}`}
              className="flex items-center gap-2 text-teal-600 hover:text-teal-700"
            >
              <Phone className="w-4 h-4" />
              <span>{bloodBank.contactNumber}</span>
            </a>
          ) : (
            <span className="text-gray-400 text-sm">No phone</span>
          )}

          <Link href={`/blood-banks/${bloodBank.id}`}>
            <Button size="sm" className="bg-teal-600 hover:bg-teal-700">
              Check Availability
            </Button>
          </Link>
        </div>
      </CardContent>
    </Card>
  );
}
