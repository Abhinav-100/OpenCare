import { apiGet, apiPost, apiPatch, buildUrl } from "@/shared/utils/api-client";
import type {
  Appointment,
  AppointmentListResponse,
  AvailableSlot,
  CreateAppointmentRequest,
} from "@/shared/types/appointments";

export const fetchAppointments = async (
  params: Record<string, unknown> = {}
): Promise<AppointmentListResponse> => {
  const url = buildUrl("/appointments", params);
  const response = await apiGet<AppointmentListResponse>(url);
  if (!response.ok) throw new Error(response.error || "Failed to fetch appointments");
  return response.data as AppointmentListResponse;
};

export const fetchMyAppointments = async (): Promise<Appointment[]> => {
  const response = await apiGet<Appointment[]>("/appointments/my");
  if (!response.ok) throw new Error(response.error || "Failed to fetch your appointments");
  return response.data as Appointment[];
};

export const fetchAppointmentById = async (id: number): Promise<Appointment> => {
  const response = await apiGet<Appointment>(`/appointments/${id}`);
  if (!response.ok) throw new Error(response.error || "Failed to fetch appointment");
  return response.data as Appointment;
};

export const fetchDoctorSlots = async (
  doctorId: number,
  date: string
): Promise<AvailableSlot[]> => {
  const response = await apiGet<AvailableSlot[]>(
    `/appointments/doctor/${doctorId}/slots?date=${date}`
  );
  if (!response.ok) throw new Error(response.error || "Failed to fetch available slots");
  return response.data as AvailableSlot[];
};

export const createAppointment = async (
  data: CreateAppointmentRequest
): Promise<Appointment> => {
  const response = await apiPost<Appointment>("/appointments", data);
  if (!response.ok) throw new Error(response.error || "Failed to create appointment");
  return response.data as Appointment;
};

export const cancelAppointment = async (id: number, reason?: string): Promise<void> => {
  const url = reason
    ? `/appointments/${id}/cancel?reason=${encodeURIComponent(reason)}`
    : `/appointments/${id}/cancel`;
  const response = await apiPost<void>(url, {});
  if (!response.ok) throw new Error(response.error || "Failed to cancel appointment");
};

export const updateAppointmentStatus = async (
  id: number,
  status: string
): Promise<Appointment> => {
  const response = await apiPatch<Appointment>(
    `/appointments/${id}/status?status=${status}`,
    {}
  );
  if (!response.ok) throw new Error(response.error || "Failed to update appointment status");
  return response.data as Appointment;
};
