import { Suspense } from "react";
import AmbulancesView from "./_AmbulancesView";
import type { Metadata } from "next";

export const metadata: Metadata = {
  title: "Emergency Ambulance Services",
  description:
    "Find emergency ambulances near you in Odisha. 24/7 ambulance services including Basic Life Support, Advanced Life Support, and Neonatal ambulances.",
  keywords: ["ambulance", "emergency", "medical transport", "Odisha", "healthcare"],
};

// Page flow: This route renders a screen entry and delegates business/data logic to module components.
export default function AmbulancesPage() {
  return (
    <Suspense
      fallback={
        <div className="flex items-center justify-center min-h-[60vh]">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-teal-600"></div>
        </div>
      }
    >
      <AmbulancesView />
    </Suspense>
  );
}
