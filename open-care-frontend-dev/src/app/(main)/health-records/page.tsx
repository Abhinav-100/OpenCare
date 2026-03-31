import { Metadata } from "next";
import { Suspense } from "react";
import HealthRecordsView from "./_HealthRecordsView";

export const metadata: Metadata = {
  title: "Health Records",
  description:
    "Manage your personal health records - track vitals, doctor visits, conditions, allergies, and medications all in one place.",
  keywords: [
    "health records",
    "medical history",
    "vitals",
    "medications",
    "allergies",
    "doctor visits",
    "OpenCare",
  ],
};

export default function HealthRecordsPage() {
  return (
    <Suspense
      fallback={
        <div className="flex items-center justify-center min-h-[400px]">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary"></div>
        </div>
      }
    >
      <HealthRecordsView />
    </Suspense>
  );
}
