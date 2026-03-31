"use client";

import { useState } from "react";
import { useQuery } from "@tanstack/react-query";
import { Input } from "@/modules/platform/components/ui/input";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/modules/platform/components/ui/select";
import { Button } from "@/modules/platform/components/ui/button";
import { fetchDistricts, fetchUpazilas } from "@/modules/catalog/api/locations";
import { fetchHospitalTypes, fetchOrganizationTypes } from "@/modules/provider/api/hospitals";
import { District, Upazila } from "@/shared/types/locations";
import { ICommonEnum } from "@/shared/types/common";

interface HospitalFiltersProps {
  onFilterChange: (params: Record<string, unknown>) => void;
}

export default function HospitalFilters({
  onFilterChange,
}: HospitalFiltersProps) {
  const [name, setName] = useState("");
  const [districtId, setDistrictId] = useState("");
  const [upazilaId, setUpazilaId] = useState("");
  const [hospitalType, setHospitalType] = useState("");
  const [organizationType, setOrganizationType] = useState("");

  // Fetch all 30 Odisha districts from API
  const { data: districts = [] } = useQuery<District[]>({
    queryKey: ["districts"],
    queryFn: fetchDistricts,
  });

  // Fetch all 148 blocks from API — filter client-side by selected district
  const { data: allBlocks = [] } = useQuery<Upazila[]>({
    queryKey: ["upazilas"],
    queryFn: fetchUpazilas,
  });

  // Fetch hospital types and org types from API
  const { data: hospitalTypes = [] } = useQuery<ICommonEnum[]>({
    queryKey: ["hospitalTypes"],
    queryFn: fetchHospitalTypes,
  });

  const { data: orgTypes = [] } = useQuery<ICommonEnum[]>({
    queryKey: ["organizationTypes"],
    queryFn: fetchOrganizationTypes,
  });

  // Only show blocks relevant to the selected district
  const blocks = districtId
    ? allBlocks.filter((u) => u.district.id === Number(districtId))
    : [];

  // Build params object — only include keys that have a value
  const buildParams = (
    n: string,
    d: string,
    u: string,
    ht: string,
    ot: string
  ): Record<string, unknown> => ({
    ...(n && { name: n }),
    ...(d && { districtId: Number(d) }),
    ...(u && { upazilaId: Number(u) }),
    ...(ht && { hospitalType: ht }),
    ...(ot && { organizationType: ot }),
  });

  const handleDistrictChange = (value: string) => {
    const d = value === "all" ? "" : value;
    setDistrictId(d);
    setUpazilaId(""); // reset block when district changes
    onFilterChange(buildParams(name, d, "", hospitalType, organizationType));
  };

  const handleUpazilaChange = (value: string) => {
    const u = value === "all" ? "" : value;
    setUpazilaId(u);
    onFilterChange(buildParams(name, districtId, u, hospitalType, organizationType));
  };

  const handleTypeChange = (value: string) => {
    const t = value === "all" ? "" : value;
    setHospitalType(t);
    onFilterChange(buildParams(name, districtId, upazilaId, t, organizationType));
  };

  const handleOrgChange = (value: string) => {
    const o = value === "all" ? "" : value;
    setOrganizationType(o);
    onFilterChange(buildParams(name, districtId, upazilaId, hospitalType, o));
  };

  // Name only fires on Apply button (avoids API call per keystroke)
  const handleApply = () => {
    onFilterChange(buildParams(name, districtId, upazilaId, hospitalType, organizationType));
  };

  const handleClear = () => {
    setName("");
    setDistrictId("");
    setUpazilaId("");
    setHospitalType("");
    setOrganizationType("");
    onFilterChange({});
  };

  return (
    <div className="bg-white border border-teal-200 rounded-xl p-6 h-fit sticky top-24">
      <h3 className="text-lg font-bold text-teal-700 mb-6">Filters</h3>

      <div className="space-y-5">
        {/* Hospital Name — fires on Apply */}
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-2">
            Hospital Name
          </label>
          <Input
            placeholder="Enter hospital name"
            value={name}
            onChange={(e) => setName(e.target.value)}
            onKeyDown={(e) => e.key === "Enter" && handleApply()}
            className="border-gray-300 focus:border-teal-500"
          />
        </div>

        {/* District — fires immediately */}
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-2">
            District
          </label>
          <Select
            value={districtId || "all"}
            onValueChange={handleDistrictChange}
          >
            <SelectTrigger className="border-gray-300 focus:border-teal-500">
              <SelectValue placeholder="All Districts" />
            </SelectTrigger>
            <SelectContent>
              <SelectItem value="all">All Districts</SelectItem>
              {districts.map((d: District) => (
                <SelectItem key={d.id} value={String(d.id)}>
                  {d.name}
                </SelectItem>
              ))}
            </SelectContent>
          </Select>
        </div>

        {/* Block — disabled until district is selected */}
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-2">
            Block
          </label>
          <Select
            value={upazilaId || "all"}
            onValueChange={handleUpazilaChange}
            disabled={!districtId}
          >
            <SelectTrigger className="border-gray-300 focus:border-teal-500">
              <SelectValue
                placeholder={
                  districtId ? "All Blocks" : "Select a district first"
                }
              />
            </SelectTrigger>
            <SelectContent>
              <SelectItem value="all">All Blocks</SelectItem>
              {blocks.map((u: Upazila) => (
                <SelectItem key={u.id} value={String(u.id)}>
                  {u.name}
                </SelectItem>
              ))}
            </SelectContent>
          </Select>
        </div>

        {/* Hospital Type — from API */}
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-2">
            Hospital Type
          </label>
          <Select
            value={hospitalType || "all"}
            onValueChange={handleTypeChange}
          >
            <SelectTrigger className="border-gray-300 focus:border-teal-500">
              <SelectValue placeholder="All Types" />
            </SelectTrigger>
            <SelectContent>
              <SelectItem value="all">All Types</SelectItem>
              {hospitalTypes.map((t: ICommonEnum) => (
                <SelectItem
                  key={t.value ?? t.englishName}
                  value={t.value ?? t.englishName}
                >
                  {t.englishName}
                </SelectItem>
              ))}
            </SelectContent>
          </Select>
        </div>

        {/* Organization Type — from API */}
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-2">
            Organization Type
          </label>
          <Select
            value={organizationType || "all"}
            onValueChange={handleOrgChange}
          >
            <SelectTrigger className="border-gray-300 focus:border-teal-500">
              <SelectValue placeholder="All Organizations" />
            </SelectTrigger>
            <SelectContent>
              <SelectItem value="all">All Organizations</SelectItem>
              {orgTypes.map((o: ICommonEnum) => (
                <SelectItem
                  key={o.value ?? o.displayName}
                  value={o.value ?? o.displayName ?? ""}
                >
                  {o.displayName ?? o.englishName}
                </SelectItem>
              ))}
            </SelectContent>
          </Select>
        </div>

        {/* Buttons */}
        <div className="flex gap-3 pt-4">
          <Button
            onClick={handleApply}
            className="bg-teal-600 hover:bg-teal-700 text-white flex-1"
          >
            Apply
          </Button>
          <Button
            variant="outline"
            onClick={handleClear}
            className="border-gray-300 text-gray-600 hover:bg-gray-50"
          >
            Clear
          </Button>
        </div>
      </div>
    </div>
  );
}
