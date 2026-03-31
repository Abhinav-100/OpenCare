import { Metadata } from "next";
import { Suspense } from "react";
import AppointmentsView from "./_AppointmentsView";

export const metadata: Metadata = {
  title: "My Appointments",
  description: "View and manage your doctor appointments on OpenCare.",
};

export default function AppointmentsPage() {
  return (
    <Suspense
      fallback={
        <div className="flex items-center justify-center min-h-[60vh]">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary"></div>
        </div>
      }
    >
      <AppointmentsView />
    </Suspense>
  );
}
