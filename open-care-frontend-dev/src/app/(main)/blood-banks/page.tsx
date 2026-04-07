import { Suspense } from "react";
import BloodBanksView from "./_BloodBanksView";
import type { Metadata } from "next";

export const metadata: Metadata = {
  title: "Blood Bank Services",
  description:
    "Find blood banks in Odisha. Check blood availability, contact blood banks, and request blood for emergencies.",
  keywords: ["blood bank", "blood donation", "blood availability", "Odisha", "healthcare"],
};

// Page flow: This route renders a screen entry and delegates business/data logic to module components.
export default function BloodBanksPage() {
  return (
    <Suspense
      fallback={
        <div className="flex items-center justify-center min-h-[60vh]">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-teal-600"></div>
        </div>
      }
    >
      <BloodBanksView />
    </Suspense>
  );
}
