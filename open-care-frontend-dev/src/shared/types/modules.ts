export interface ModuleAuthResponse {
  accessToken: string;
  tokenType: string;
  expiresInSeconds: number;
  email: string;
  roles: string[];
}

export interface ModuleLoginRequest {
  email: string;
  password: string;
}

export interface ModuleRegisterRequest {
  email: string;
  password: string;
  firstName: string;
  lastName: string;
  phone?: string;
  role?: string;
}

export interface ModuleOverviewResponse {
  module: string;
  status: string;
  capabilities: string[];
}

export interface GenerateModuleBillRequest {
  patientId: number;
  appointmentId?: number;
  totalAmount: number;
  discountAmount?: number;
  taxAmount?: number;
  dueDate?: string;
  notes?: string;
}

export interface BillResponse {
  billId: number;
  billNumber: string;
  patientId: number;
  appointmentId?: number;
  billDate: string;
  totalAmount: number;
  discountAmount?: number;
  taxAmount?: number;
  netAmount: number;
  status: string;
  dueDate?: string;
}

export interface RecordModulePaymentRequest {
  billId: number;
  amount: number;
  paymentMethod: string;
  gateway?: string;
  gatewayTxnId?: string;
}

export interface PaymentTrackResponse {
  paymentId: number;
  billId: number;
  paymentReference: string;
  amount: number;
  paymentMethod: string;
  paymentStatus: string;
  paidAt: string;
  gateway?: string;
  gatewayTxnId?: string;
}

export interface DoctorLabTestRequest {
  appointmentId: number;
  testCode: string;
  testName: string;
  notes?: string;
}

export interface DoctorLabTestResponse {
  id: number;
  appointmentId: number;
  patientId: number;
  doctorId: number;
  testCode: string;
  testName: string;
  status: string;
  requestedAt: string;
}

export interface LabReportUploadRequest {
  labTestId: number;
  reportType: string;
  fileUrl: string;
  summary?: string;
}

export interface LabReportResponse {
  reportId: number;
  labTestId: number;
  patientId: number;
  reportType: string;
  fileUrl: string;
  status: string;
  reportedAt: string;
}
