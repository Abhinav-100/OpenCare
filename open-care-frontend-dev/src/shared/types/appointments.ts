import type { Profile, DoctorResponse } from "./doctors";
import type { Hospital } from "./hospitals";

export type AppointmentType = "ONLINE" | "OFFLINE";
export type AppointmentStatus = "PENDING" | "CONFIRMED" | "CANCELLED" | "COMPLETED" | "NO_SHOW";
export type PaymentStatus = "PENDING" | "PAID" | "FAILED" | "REFUNDED";

export interface AppointmentTypeResponse {
  value: AppointmentType;
  displayName: string;
}

export interface AppointmentStatusResponse {
  value: AppointmentStatus;
  displayName: string;
}

export interface PaymentStatusResponse {
  value: PaymentStatus;
  displayName: string;
}

export interface DoctorWorkplaceResponse {
  id: number;
  hospital?: Hospital;
  position?: string;
}

export interface Appointment {
  id: number;
  appointmentNumber: string;
  patientProfile: Profile;
  doctor: DoctorResponse;
  appointmentType: AppointmentTypeResponse;
  appointmentDate: string;
  startTime: string;
  endTime: string;
  durationMinutes: number;
  status: AppointmentStatusResponse;
  consultationFee: number;
  paymentStatus: PaymentStatusResponse;
  paymentTransactionId?: string;
  hospital?: Hospital;
  doctorWorkplace?: DoctorWorkplaceResponse;
  meetingLink?: string;
  symptoms?: string;
  notes?: string;
  cancellationReason?: string;
  cancelledBy?: string;
  cancelledAt?: string;
  reminderSent: boolean;
  createdBy: string;
  createdAt: string;
  updatedBy: string;
  updatedAt: string;
}

export interface AppointmentListResponse {
  appointments: Appointment[];
  currentPage: number;
  totalItems: number;
  totalPages: number;
}

export interface AvailableSlot {
  startTime: string;
  endTime: string;
  isAvailable: boolean;
}

export interface CreateAppointmentRequest {
  doctorId: number;
  appointmentType: AppointmentType;
  appointmentDate: string;
  startTime: string;
  endTime?: string;
  durationMinutes?: number;
  consultationFee: number;
  hospitalId?: number;
  doctorWorkplaceId?: number;
  meetingLink?: string;
  symptoms?: string;
  notes?: string;
}
