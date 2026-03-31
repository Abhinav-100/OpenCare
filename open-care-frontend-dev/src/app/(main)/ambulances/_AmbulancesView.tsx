"use client";

import { useState } from "react";
import { useQuery } from "@tanstack/react-query";
import { Phone, Truck, MapPin, Building2, Filter, X } from "lucide-react";
import { fetchAmbulances } from "@/modules/provider/api/ambulances";
import { fetchDistricts, fetchUpazilasByDistrictId } from "@/modules/catalog/api/locations";
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
import type { Ambulance } from "@/shared/types/ambulances";
import type { District, Upazila } from "@/shared/types/locations";

const AMBULANCE_TYPES = [
  { value: "BASIC", label: "Basic Life Support (BLS)" },
  { value: "ADVANCED", label: "Advanced Life Support (ALS)" },
  { value: "NEONATAL", label: "Neonatal Ambulance" },
  { value: "PEDIATRIC", label: "Pediatric Ambulance" },
  { value: "AIR_AMBULANCE", label: "Air Ambulance" },
];

export default function AmbulancesView() {
  const [currentPage, setCurrentPage] = useState(1);
  const [selectedDistrict, setSelectedDistrict] = useState<string>("");
  const [selectedUpazila, setSelectedUpazila] = useState<string>("");
  const [selectedType, setSelectedType] = useState<string>("");
  const [showAvailableOnly, setShowAvailableOnly] = useState(false);
  const [showMobileFilters, setShowMobileFilters] = useState(false);

  const { data: districts = [] } = useQuery({
    queryKey: ["districts"],
    queryFn: fetchDistricts,
  });

  const { data: upazilas = [] } = useQuery({
    queryKey: ["upazilas", selectedDistrict],
    queryFn: () => fetchUpazilasByDistrictId(parseInt(selectedDistrict)),
    enabled: !!selectedDistrict,
  });

  const filterParams: Record<string, string | number | boolean | undefined | null> = {
    page: currentPage - 1,
    size: 12,
    ...(selectedDistrict && { districtId: parseInt(selectedDistrict) }),
    ...(selectedUpazila && { upazilaId: parseInt(selectedUpazila) }),
    ...(selectedType && { type: selectedType }),
    ...(showAvailableOnly && { isAvailable: true }),
  };

  const {
    data,
    isLoading,
    error,
  } = useQuery({
    queryKey: ["ambulances", currentPage, selectedDistrict, selectedUpazila, selectedType, showAvailableOnly],
    queryFn: () => fetchAmbulances(filterParams),
    placeholderData: (prev) => prev,
  });

  const clearFilters = () => {
    setSelectedDistrict("");
    setSelectedUpazila("");
    setSelectedType("");
    setShowAvailableOnly(false);
    setCurrentPage(1);
  };

  const hasFilters = selectedDistrict || selectedUpazila || selectedType || showAvailableOnly;

  return (
    <div className="min-h-screen bg-gray-50 py-8">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        {/* Header */}
        <div className="mb-8">
          <h1 className="text-3xl font-bold text-gray-900">
            Emergency Ambulance Services
          </h1>
          <p className="text-gray-600 mt-2">
            Find available ambulances near you for emergency medical transportation
          </p>
        </div>

        {/* Emergency Banner */}
        <div className="bg-red-50 border border-red-200 rounded-lg p-4 mb-6">
          <div className="flex items-center gap-3">
            <Phone className="w-6 h-6 text-red-600" />
            <div>
              <p className="font-semibold text-red-800">Emergency? Call 108</p>
              <p className="text-sm text-red-600">
                For immediate ambulance assistance, call the national emergency number
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

                {/* District Filter */}
                <div className="space-y-2">
                  <label className="text-sm font-medium text-gray-700">
                    District
                  </label>
                  <Select
                    value={selectedDistrict || "__all__"}
                    onValueChange={(v) => {
                      setSelectedDistrict(v === "__all__" ? "" : v);
                      setSelectedUpazila("");
                      setCurrentPage(1);
                    }}
                  >
                    <SelectTrigger>
                      <SelectValue placeholder="All Districts" />
                    </SelectTrigger>
                    <SelectContent>
                      <SelectItem value="__all__">All Districts</SelectItem>
                      {districts.map((district: District) => (
                        <SelectItem key={district.id} value={district.id.toString()}>
                          {district.name}
                        </SelectItem>
                      ))}
                    </SelectContent>
                  </Select>
                </div>

                {/* Block Filter */}
                <div className="space-y-2">
                  <label className="text-sm font-medium text-gray-700">
                    Block
                  </label>
                  <Select
                    value={selectedUpazila || "__all__"}
                    onValueChange={(v) => {
                      setSelectedUpazila(v === "__all__" ? "" : v);
                      setCurrentPage(1);
                    }}
                    disabled={!selectedDistrict}
                  >
                    <SelectTrigger>
                      <SelectValue placeholder="All Blocks" />
                    </SelectTrigger>
                    <SelectContent>
                      <SelectItem value="__all__">All Blocks</SelectItem>
                      {upazilas.map((upazila: Upazila) => (
                        <SelectItem key={upazila.id} value={upazila.id.toString()}>
                          {upazila.name}
                        </SelectItem>
                      ))}
                    </SelectContent>
                  </Select>
                </div>

                {/* Ambulance Type Filter */}
                <div className="space-y-2">
                  <label className="text-sm font-medium text-gray-700">
                    Ambulance Type
                  </label>
                  <Select
                    value={selectedType || "__all__"}
                    onValueChange={(v) => {
                      setSelectedType(v === "__all__" ? "" : v);
                      setCurrentPage(1);
                    }}
                  >
                    <SelectTrigger>
                      <SelectValue placeholder="All Types" />
                    </SelectTrigger>
                    <SelectContent>
                      <SelectItem value="__all__">All Types</SelectItem>
                      {AMBULANCE_TYPES.map((type) => (
                        <SelectItem key={type.value} value={type.value}>
                          {type.label}
                        </SelectItem>
                      ))}
                    </SelectContent>
                  </Select>
                </div>

                {/* Available Toggle */}
                <div className="flex items-center justify-between py-2">
                  <label className="text-sm font-medium text-gray-700">
                    Available Now Only
                  </label>
                  <button
                    onClick={() => {
                      setShowAvailableOnly(!showAvailableOnly);
                      setCurrentPage(1);
                    }}
                    className={`relative inline-flex h-6 w-11 items-center rounded-full transition-colors ${
                      showAvailableOnly ? "bg-teal-600" : "bg-gray-200"
                    }`}
                  >
                    <span
                      className={`inline-block h-4 w-4 transform rounded-full bg-white transition-transform ${
                        showAvailableOnly ? "translate-x-6" : "translate-x-1"
                      }`}
                    />
                  </button>
                </div>
              </CardContent>
            </Card>
          </div>

          {/* Ambulance List */}
          <div className="flex-1">
            {isLoading ? (
              <div className="flex items-center justify-center py-12">
                <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-teal-600"></div>
              </div>
            ) : error ? (
              <Card>
                <CardContent className="py-12 text-center">
                  <p className="text-red-600">
                    Failed to load ambulances. Please try again.
                  </p>
                </CardContent>
              </Card>
            ) : data?.ambulances.length === 0 ? (
              <Card>
                <CardContent className="py-12 text-center">
                  <Truck className="w-12 h-12 text-gray-300 mx-auto mb-4" />
                  <h3 className="text-lg font-medium text-gray-900 mb-2">
                    No ambulances found
                  </h3>
                  <p className="text-gray-500">
                    Try adjusting your filters or search in a different area.
                  </p>
                </CardContent>
              </Card>
            ) : (
              <>
                <div className="mb-4 text-sm text-gray-500">
                  Showing {data?.ambulances.length} of {data?.totalItems} ambulances
                </div>
                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                  {data?.ambulances.map((ambulance) => (
                    <AmbulanceCard key={ambulance.id} ambulance={ambulance} />
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

function AmbulanceCard({ ambulance }: { ambulance: Ambulance }) {
  return (
    <Card className="hover:shadow-md transition-shadow">
      <CardContent className="p-5">
        <div className="flex items-start justify-between mb-3">
          <div className="flex items-center gap-3">
            <div className="w-12 h-12 rounded-full bg-red-100 flex items-center justify-center">
              <Truck className="w-6 h-6 text-red-600" />
            </div>
            <div>
              <h3 className="font-semibold text-gray-900">
                {ambulance.vehicleNumber}
              </h3>
              <p className="text-sm text-gray-500">
                {ambulance.type?.displayName || "Ambulance"}
              </p>
            </div>
          </div>
          <Badge
            variant={ambulance.isAvailable ? "default" : "secondary"}
            className={
              ambulance.isAvailable
                ? "bg-green-100 text-green-800 hover:bg-green-100"
                : "bg-gray-100 text-gray-600"
            }
          >
            {ambulance.isAvailable ? "Available" : "Busy"}
          </Badge>
        </div>

        <div className="space-y-2 text-sm">
          <div className="flex items-center gap-2 text-gray-600">
            <MapPin className="w-4 h-4" />
            <span>
              {ambulance.upazila?.name || ambulance.district?.name || "Location not specified"}
              {ambulance.district?.name && ambulance.upazila?.name && `, ${ambulance.district.name}`}
            </span>
          </div>

          {ambulance.hospital && (
            <div className="flex items-center gap-2 text-gray-600">
              <Building2 className="w-4 h-4" />
              <span>{ambulance.hospital.name}</span>
            </div>
          )}
        </div>

        <div className="mt-4 pt-4 border-t flex items-center justify-between">
          <div>
            <p className="text-sm text-gray-500">Driver</p>
            <p className="font-medium">{ambulance.driverName || "Not assigned"}</p>
          </div>
          {ambulance.driverPhone && (
            <a
              href={`tel:${ambulance.driverPhone}`}
              className="flex items-center gap-2 px-4 py-2 bg-teal-600 text-white rounded-lg hover:bg-teal-700 transition-colors"
            >
              <Phone className="w-4 h-4" />
              <span>Call Now</span>
            </a>
          )}
        </div>
      </CardContent>
    </Card>
  );
}
