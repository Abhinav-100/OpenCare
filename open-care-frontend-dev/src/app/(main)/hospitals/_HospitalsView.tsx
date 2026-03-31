"use client";

import { useState, useCallback } from "react";
import { useQuery } from "@tanstack/react-query";
import dynamic from "next/dynamic";
import { LayoutList, Map, SlidersHorizontal, X } from "lucide-react";
import HospitalFilters from "@/modules/provider/components/hospitals/HospitalFilters";
import HospitalsList from "@/modules/provider/components/hospitals/HospitalsList";
import { fetchHospitals } from "@/modules/provider/api/hospitals";

const HospitalMap = dynamic(
  () => import("@/modules/provider/components/hospitals/HospitalMap"),
  {
    ssr: false,
    loading: () => (
      <div className="text-center py-20 text-gray-500">Loading map...</div>
    ),
  }
);

const HOSPITALS_PER_PAGE = 6;

export default function HospitalsView() {
  const [currentPage, setCurrentPage] = useState(1);
  const [filterParams, setFilterParams] = useState<Record<string, unknown>>({});
  const [view, setView] = useState<"list" | "map">("list");
  const [showMobileFilters, setShowMobileFilters] = useState(false);

  const handleFilterChange = useCallback((params: Record<string, unknown>) => {
    setFilterParams(params);
    setCurrentPage(1);
    setShowMobileFilters(false); // close on mobile after applying
  }, []);

  const { data, isLoading, isError } = useQuery({
    queryKey: ["public-hospitals", currentPage, filterParams],
    queryFn: () =>
      fetchHospitals({
        page: currentPage - 1,
        size: HOSPITALS_PER_PAGE,
        ...filterParams,
      }),
    placeholderData: (prev) => prev,
  });

  const { data: mapData, isLoading: isMapLoading } = useQuery({
    queryKey: ["hospitals-map", filterParams],
    queryFn: () => fetchHospitals({ page: 0, size: 100, ...filterParams }),
    enabled: view === "map",
    staleTime: 2 * 60 * 1000,
  });

  const hospitals = data?.hospitals ?? [];
  const totalResults = data?.totalItems ?? 0;
  const totalPages = data?.totalPages ?? 1;
  const mapHospitals = mapData?.hospitals ?? [];

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Hero */}
      <div className="bg-gradient-to-r from-teal-600 to-teal-700 py-12">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="text-center text-white">
            <h1 className="text-3xl font-bold mb-2">Find Hospitals</h1>
            <p className="text-lg mb-2">
              Search and filter hospitals by location, type, and organization
            </p>
            <p className="text-sm">📍 Odisha • Hospitals available</p>
          </div>
        </div>
      </div>

      {/* Main Content */}
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">

        {/* Mobile: Filters toggle button */}
        <div className="lg:hidden mb-4">
          <button
            onClick={() => setShowMobileFilters((v) => !v)}
            className="flex items-center gap-2 px-4 py-2 bg-white border border-gray-200 rounded-lg text-sm font-medium text-gray-700 shadow-sm"
          >
            {showMobileFilters ? (
              <><X className="w-4 h-4" /> Hide Filters</>
            ) : (
              <><SlidersHorizontal className="w-4 h-4" /> Show Filters</>
            )}
          </button>
        </div>

        <div className="flex flex-col lg:flex-row gap-8">
          {/* Filters Sidebar — always visible on desktop, toggled on mobile */}
          <div className={`w-full lg:w-80 lg:flex-shrink-0 ${showMobileFilters ? "block" : "hidden lg:block"}`}>
            <HospitalFilters onFilterChange={handleFilterChange} />
          </div>

          {/* Content Area */}
          <div className="flex-1 min-w-0">
            {/* View Toggle */}
            <div className="flex items-center justify-between mb-4">
              <p className="text-sm text-gray-600">
                {view === "list"
                  ? "Hospital results"
                  : "Hospital map view"}
              </p>
              <div className="flex items-center gap-1 bg-gray-100 rounded-lg p-1">
                <button
                  onClick={() => setView("list")}
                  className={`flex items-center gap-1.5 px-3 py-1.5 rounded-md text-sm font-medium transition-colors ${
                    view === "list"
                      ? "bg-white text-teal-700 shadow-sm"
                      : "text-gray-500 hover:text-gray-700"
                  }`}
                >
                  <LayoutList className="w-4 h-4" />
                  List
                </button>
                <button
                  onClick={() => setView("map")}
                  className={`flex items-center gap-1.5 px-3 py-1.5 rounded-md text-sm font-medium transition-colors ${
                    view === "map"
                      ? "bg-white text-teal-700 shadow-sm"
                      : "text-gray-500 hover:text-gray-700"
                  }`}
                >
                  <Map className="w-4 h-4" />
                  Map
                </button>
              </div>
            </div>

            {isError && (
              <div className="text-center py-12 text-red-600">
                Failed to load hospitals. Please try again.
              </div>
            )}

            {view === "list" && !isError && (
              <>
                {isLoading && !data && (
                  <div className="text-center py-12 text-gray-500">
                    Loading hospitals...
                  </div>
                )}
                <HospitalsList
                  hospitals={hospitals}
                  totalResults={totalResults}
                  currentPage={currentPage}
                  onPageChange={setCurrentPage}
                  totalPages={totalPages}
                />
              </>
            )}

            {view === "map" && (
              <>
                {isMapLoading ? (
                  <div className="text-center py-20 text-gray-500">
                    Loading map data...
                  </div>
                ) : (
                  <HospitalMap hospitals={mapHospitals} />
                )}
              </>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}
