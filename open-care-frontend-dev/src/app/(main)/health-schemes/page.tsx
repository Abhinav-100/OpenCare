import { Shield, CheckCircle2, Phone, ExternalLink, Users, IndianRupee, HeartPulse, Hospital } from "lucide-react";
import { Badge } from "@/modules/platform/components/ui/badge";
import { Card, CardContent } from "@/modules/platform/components/ui/card";
import { EligibilityChecker } from "@/modules/clinical/components/health-schemes/EligibilityChecker";
import type { Metadata } from "next";

export const metadata: Metadata = {
	title: "Health Schemes in Odisha — BSKY & Ayushman Bharat",
	description:
		"Learn about BSKY (Biju Swasthya Kalyan Yojana) and Ayushman Bharat PM-JAY — free cashless health insurance schemes for Odisha residents. Check eligibility, benefits, and how to apply.",
	openGraph: {
		title: "Health Schemes in Odisha — BSKY & Ayushman Bharat | OpenCare",
		description:
			"Free cashless healthcare for Odisha residents. Coverage up to ₹10 lakh per family under BSKY and Ayushman Bharat.",
	},
};

const bskyBenefits = [
  "₹5 lakh cashless coverage per family per year",
  "₹10 lakh coverage for women members",
  "Covers 200+ day-care procedures",
  "Valid at all government hospitals in Odisha",
  "Valid at 230+ empanelled private hospitals",
  "No premium — fully free for beneficiaries",
  "Smart Health Card (yellow card) for access",
];

const abBenefits = [
  "₹5 lakh cashless coverage per family per year",
  "1,500+ medical packages covered",
  "Pre and post-hospitalisation expenses included",
  "Portable — usable across all states in India",
  "Valid at 25,000+ empanelled hospitals nationwide",
  "Covers both government and private hospitals",
  "No cap on family size",
];

const bskyEligibility = [
  "Below Poverty Line (BPL) families",
  "Antyodaya Anna Yojana (AAY) card holders",
  "KALIA / SATA scheme beneficiaries",
  "Construction workers registered with BOCW Board",
  "Odisha Unorganised Workers Social Security Board members",
  "All women of Odisha (special provision)",
];

const abEligibility = [
  "Families listed in SECC 2011 database",
  "Rural families meeting deprivation criteria",
  "Occupational category workers (auto-rickshaw drivers, construction workers, etc.)",
  "BPL ration card holders (in some states)",
  "Not required to have a ration card",
];

const steps = [
  {
    step: "1",
    title: "Check Eligibility",
    desc: "Visit the nearest Common Service Centre (CSC) or check online using your Aadhaar number.",
  },
  {
    step: "2",
    title: "Collect Smart Health Card",
    desc: "Get your BSKY Smart Health Card or Ayushman Card from your local CSC, hospital, or Gram Panchayat.",
  },
  {
    step: "3",
    title: "Visit Empanelled Hospital",
    desc: "Go to any empanelled government or private hospital. Show your card at the Aarogya Mitra desk.",
  },
  {
    step: "4",
    title: "Get Cashless Treatment",
    desc: "The hospital directly files the claim. You pay nothing for covered procedures.",
  },
];

export default function HealthSchemesPage() {
  return (
    <div className="min-h-screen bg-gray-50">
      {/* Hero */}
      <div className="bg-gradient-to-r from-teal-600 to-teal-700 py-14">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 text-center text-white">
          <div className="flex justify-center mb-4">
            <div className="bg-white/20 rounded-full p-3">
              <Shield className="w-8 h-8" />
            </div>
          </div>
          <h1 className="text-3xl font-bold mb-3">Health Insurance Schemes</h1>
          <p className="text-lg text-teal-100 max-w-2xl mx-auto">
            Odisha residents can access free cashless healthcare through two major
            government schemes — BSKY and Ayushman Bharat PM-JAY.
          </p>
          <div className="flex justify-center gap-4 mt-6">
            <Badge className="bg-white/20 text-white border-white/30 text-sm px-3 py-1">
              State Scheme: BSKY
            </Badge>
            <Badge className="bg-white/20 text-white border-white/30 text-sm px-3 py-1">
              National Scheme: Ayushman Bharat
            </Badge>
          </div>
        </div>
      </div>

      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12 space-y-12">

        {/* Eligibility Checker - Interactive Component */}
        <div className="max-w-xl mx-auto">
          <EligibilityChecker />
        </div>

        {/* Scheme Cards */}
        <div className="grid md:grid-cols-2 gap-8">
          {/* BSKY Card */}
          <Card className="border-0 shadow-md overflow-hidden">
            <div className="bg-gradient-to-r from-teal-500 to-teal-600 px-6 py-5">
              <div className="flex items-center justify-between">
                <div>
                  <p className="text-teal-100 text-sm font-medium uppercase tracking-wide">Odisha State Scheme</p>
                  <h2 className="text-2xl font-bold text-white mt-1">BSKY</h2>
                  <p className="text-teal-100 text-sm mt-0.5">Biju Swasthya Kalyan Yojana</p>
                </div>
                <div className="bg-white/20 rounded-full p-3">
                  <HeartPulse className="w-8 h-8 text-white" />
                </div>
              </div>
              <div className="flex gap-6 mt-5">
                <div>
                  <p className="text-teal-200 text-xs">General Coverage</p>
                  <p className="text-white text-xl font-bold">₹5 Lakh / year</p>
                </div>
                <div>
                  <p className="text-teal-200 text-xs">Women Coverage</p>
                  <p className="text-white text-xl font-bold">₹10 Lakh / year</p>
                </div>
              </div>
            </div>
            <CardContent className="p-6 space-y-6">
              <div>
                <h3 className="font-semibold text-gray-900 mb-3 flex items-center gap-2">
                  <CheckCircle2 className="w-4 h-4 text-teal-600" /> Key Benefits
                </h3>
                <ul className="space-y-2">
                  {bskyBenefits.map((b, i) => (
                    <li key={i} className="flex items-start gap-2 text-sm text-gray-700">
                      <span className="text-teal-500 mt-0.5 shrink-0">✓</span>
                      {b}
                    </li>
                  ))}
                </ul>
              </div>
              <div>
                <h3 className="font-semibold text-gray-900 mb-3 flex items-center gap-2">
                  <Users className="w-4 h-4 text-teal-600" /> Who is Eligible?
                </h3>
                <ul className="space-y-2">
                  {bskyEligibility.map((e, i) => (
                    <li key={i} className="flex items-start gap-2 text-sm text-gray-600">
                      <span className="text-gray-400 mt-0.5 shrink-0">•</span>
                      {e}
                    </li>
                  ))}
                </ul>
              </div>
              <div className="flex items-center gap-3 pt-2 border-t border-gray-100">
                <Phone className="w-4 h-4 text-teal-600" />
                <span className="text-sm text-gray-600">Helpline: <span className="font-semibold text-gray-900">104</span></span>
                <a
                  href="https://bsky.odisha.gov.in"
                  target="_blank"
                  rel="noopener noreferrer"
                  className="ml-auto flex items-center gap-1 text-sm text-teal-600 hover:text-teal-700 font-medium"
                >
                  Official Portal <ExternalLink className="w-3.5 h-3.5" />
                </a>
              </div>
            </CardContent>
          </Card>

          {/* Ayushman Bharat Card */}
          <Card className="border-0 shadow-md overflow-hidden">
            <div className="bg-gradient-to-r from-orange-500 to-amber-500 px-6 py-5">
              <div className="flex items-center justify-between">
                <div>
                  <p className="text-orange-100 text-sm font-medium uppercase tracking-wide">Central Government Scheme</p>
                  <h2 className="text-2xl font-bold text-white mt-1">Ayushman Bharat</h2>
                  <p className="text-orange-100 text-sm mt-0.5">Pradhan Mantri Jan Arogya Yojana (PM-JAY)</p>
                </div>
                <div className="bg-white/20 rounded-full p-3">
                  <Hospital className="w-8 h-8 text-white" />
                </div>
              </div>
              <div className="flex gap-6 mt-5">
                <div>
                  <p className="text-orange-200 text-xs">Coverage per Family</p>
                  <p className="text-white text-xl font-bold">₹5 Lakh / year</p>
                </div>
                <div>
                  <p className="text-orange-200 text-xs">Valid Across</p>
                  <p className="text-white text-xl font-bold">All India</p>
                </div>
              </div>
            </div>
            <CardContent className="p-6 space-y-6">
              <div>
                <h3 className="font-semibold text-gray-900 mb-3 flex items-center gap-2">
                  <CheckCircle2 className="w-4 h-4 text-orange-500" /> Key Benefits
                </h3>
                <ul className="space-y-2">
                  {abBenefits.map((b, i) => (
                    <li key={i} className="flex items-start gap-2 text-sm text-gray-700">
                      <span className="text-orange-400 mt-0.5 shrink-0">✓</span>
                      {b}
                    </li>
                  ))}
                </ul>
              </div>
              <div>
                <h3 className="font-semibold text-gray-900 mb-3 flex items-center gap-2">
                  <Users className="w-4 h-4 text-orange-500" /> Who is Eligible?
                </h3>
                <ul className="space-y-2">
                  {abEligibility.map((e, i) => (
                    <li key={i} className="flex items-start gap-2 text-sm text-gray-600">
                      <span className="text-gray-400 mt-0.5 shrink-0">•</span>
                      {e}
                    </li>
                  ))}
                </ul>
              </div>
              <div className="flex items-center gap-3 pt-2 border-t border-gray-100">
                <Phone className="w-4 h-4 text-orange-500" />
                <span className="text-sm text-gray-600">Helpline: <span className="font-semibold text-gray-900">14555</span></span>
                <a
                  href="https://pmjay.gov.in"
                  target="_blank"
                  rel="noopener noreferrer"
                  className="ml-auto flex items-center gap-1 text-sm text-orange-600 hover:text-orange-700 font-medium"
                >
                  Official Portal <ExternalLink className="w-3.5 h-3.5" />
                </a>
              </div>
            </CardContent>
          </Card>
        </div>

        {/* Comparison Table */}
        <div>
          <h2 className="text-xl font-bold text-gray-900 mb-4">Quick Comparison</h2>
          <div className="bg-white rounded-xl shadow-sm border border-gray-100 overflow-hidden">
            <div className="overflow-x-auto">
            <table className="w-full text-sm min-w-[480px]">
              <thead>
                <tr className="bg-gray-50 border-b border-gray-100">
                  <th className="text-left px-6 py-3 text-gray-600 font-medium w-1/3">Feature</th>
                  <th className="text-left px-6 py-3 text-teal-700 font-semibold">BSKY</th>
                  <th className="text-left px-6 py-3 text-orange-600 font-semibold">Ayushman Bharat</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-gray-50">
                {[
                  ["Administered by", "Odisha State Government", "Central Government (NHA)"],
                  ["Annual coverage", "₹5L (General) / ₹10L (Women)", "₹5 Lakh per family"],
                  ["Portability", "Within Odisha only", "Pan-India"],
                  ["Premium", "Free (no premium)", "Free (no premium)"],
                  ["Hospital network", "230+ private + all govt (Odisha)", "25,000+ hospitals (India)"],
                  ["Procedures covered", "200+ procedures", "1,500+ packages"],
                  ["Launched", "2018", "2018"],
                ].map(([feature, bsky, ab], i) => (
                  <tr key={i} className={i % 2 === 0 ? "bg-white" : "bg-gray-50/50"}>
                    <td className="px-6 py-3 text-gray-600 font-medium">{feature}</td>
                    <td className="px-6 py-3 text-gray-800">{bsky}</td>
                    <td className="px-6 py-3 text-gray-800">{ab}</td>
                  </tr>
                ))}
              </tbody>
            </table>
            </div>
          </div>
        </div>

        {/* How to Apply Steps */}
        <div>
          <h2 className="text-xl font-bold text-gray-900 mb-6">How to Access Benefits</h2>
          <div className="grid sm:grid-cols-2 lg:grid-cols-4 gap-4">
            {steps.map((s) => (
              <div key={s.step} className="bg-white rounded-xl border border-gray-100 shadow-sm p-5">
                <div className="w-9 h-9 bg-teal-600 rounded-full flex items-center justify-center text-white font-bold text-sm mb-4">
                  {s.step}
                </div>
                <h3 className="font-semibold text-gray-900 mb-2">{s.title}</h3>
                <p className="text-sm text-gray-600 leading-relaxed">{s.desc}</p>
              </div>
            ))}
          </div>
        </div>

        {/* Helplines Banner */}
        <div className="bg-gradient-to-r from-teal-50 to-orange-50 rounded-xl border border-teal-100 p-6">
          <h2 className="text-lg font-bold text-gray-900 mb-4 flex items-center gap-2">
            <Phone className="w-5 h-5 text-teal-600" /> Helplines &amp; Resources
          </h2>
          <div className="grid sm:grid-cols-2 lg:grid-cols-3 gap-4">
            {[
              { label: "BSKY Helpline", number: "104", note: "Free, 24×7", color: "teal" },
              { label: "Ayushman Bharat", number: "14555", note: "Free, 24×7", color: "orange" },
              { label: "Odisha Health Dept.", number: "0674-2322100", note: "Office hours", color: "teal" },
            ].map((h, i) => (
              <div key={i} className="flex items-center gap-3 bg-white rounded-lg px-4 py-3 border border-gray-100 shadow-sm">
                <div className={`w-10 h-10 rounded-full flex items-center justify-center flex-shrink-0 ${
                  h.color === "teal" ? "bg-teal-100" : "bg-orange-100"
                }`}>
                  <Phone className={`w-4 h-4 ${h.color === "teal" ? "text-teal-600" : "text-orange-500"}`} />
                </div>
                <div>
                  <p className="text-xs text-gray-500">{h.label}</p>
                  <p className="font-bold text-gray-900">{h.number}</p>
                  <p className="text-xs text-gray-400">{h.note}</p>
                </div>
              </div>
            ))}
          </div>
        </div>

        {/* Notice */}
        <div className="bg-blue-50 border border-blue-200 rounded-lg px-5 py-4">
          <div className="flex gap-3">
            <IndianRupee className="w-5 h-5 text-blue-600 shrink-0 mt-0.5" />
            <p className="text-sm text-blue-800">
              <span className="font-semibold">Important:</span> In Odisha, BSKY and Ayushman Bharat are integrated — if you are eligible for both, you can get the combined benefit of up to ₹10 lakh for women and ₹5 lakh for other family members. Visit any empanelled hospital and present your Smart Health Card or Aadhaar.
            </p>
          </div>
        </div>

      </div>
    </div>
  );
}
