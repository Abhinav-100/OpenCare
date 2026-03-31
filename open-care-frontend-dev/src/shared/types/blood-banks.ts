import type { Hospital } from "./hospitals";

export interface BloodBank {
  id: number;
  name: string;
  licenseNo?: string;
  contactNumber?: string;
  email?: string;
  address?: string;
  latitude?: number;
  longitude?: number;
  openingHours?: string;
  isAlwaysOpen: boolean;
  isActive: boolean;
  establishedDate?: string;
  hospital?: Hospital;
  createdBy: string;
  createdAt: string;
  updatedBy: string;
  updatedAt: string;
}

export interface BloodInventoryItem {
  id: number;
  bloodBank: BloodBank;
  bloodGroup: {
    value: string;
    name: string;
  };
  component: {
    value: string;
    name: string;
  };
  availableUnits: number;
  lastUpdated: string;
}

export interface BloodBankListResponse {
  bloodBanks: BloodBank[];
  currentPage: number;
  totalItems: number;
  totalPages: number;
}
