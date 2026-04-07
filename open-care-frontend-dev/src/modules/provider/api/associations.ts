import { AssociationsListResponse, Association } from "@/shared/types/associations";
import { AddAssociationFormData } from "@/modules/provider/validations/add-association-schema";
import { baseUrl } from "@/shared/constants/config";
import { apiPost, apiPut } from "@/shared/utils/api-client";
import { normalizeApiData } from "@/shared/utils/api-client";

interface QueryParams {
  [key: string]: string | number | boolean | undefined | null;
}

// Helper function to build query string
const buildQueryString = (params: QueryParams): string => {
  const queryParams = new URLSearchParams();

  Object.entries(params).forEach(([key, value]) => {
    if (value !== undefined && value !== null && value !== "") {
      queryParams.append(key, value.toString());
    }
  });

  return queryParams.toString();
};

// API flow: This module wraps backend endpoints and returns typed data for UI/hooks.
export const fetchAssociations = async (
  params: QueryParams
): Promise<AssociationsListResponse> => {
  const queryString = buildQueryString(params);
  const response = await fetch(`${baseUrl}/associations?${queryString}`, {
    method: "GET",
    headers: {
      "Content-Type": "application/json",
    },
  });

  if (!response.ok) {
    throw new Error(
      `Failed to fetch associations: ${response.status} ${response.statusText}`
    );
  }

  const data = normalizeApiData(await response.json());

  // Return the data as is since it matches our expected format
  return {
    associations: data.associations || [],
    totalItems: data.totalItems || 0,
    totalPages: data.totalPages || 0,
    currentPage: data.currentPage || 0,
    message: "Success",
    status: 200,
  };
};

export const fetchAssociationById = async (
  id: number
): Promise<Association> => {
  const response = await fetch(`${baseUrl}/associations/${id}`, {
    method: "GET",
    headers: {
      "Content-Type": "application/json",
    },
  });

  if (!response.ok) {
    throw new Error(
      `Failed to fetch association: ${response.status} ${response.statusText}`
    );
  }

  return normalizeApiData(await response.json());
};

export const addAssociation = async (
  data: AddAssociationFormData
): Promise<Association> => {
  const response = await apiPost<Association>("/associations", data);

  if (!response.data) {
    throw new Error("Failed to add association");
  }

  return response.data;
};

export const updateAssociation = async (
  id: number,
  data: AddAssociationFormData
): Promise<Association> => {
  const response = await apiPut<Association>(`/associations/${id}`, data);

  if (!response.ok || !response.data) {
    throw new Error(response.error || "Failed to update association");
  }

  return response.data;
};

export const fetchAssociationTypes = async (): Promise<
  Array<{ value: string; displayName: string; bnName: string }>
> => {
  const response = await fetch(`${baseUrl}/association-types`, {
    method: "GET",
    headers: {
      "Content-Type": "application/json",
    },
  });

  if (!response.ok) {
    throw new Error(
      `Failed to fetch association types: ${response.status} ${response.statusText}`
    );
  }

  return normalizeApiData(await response.json());
};
