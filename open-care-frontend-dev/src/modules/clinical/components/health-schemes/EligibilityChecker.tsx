"use client";

import { useState } from "react";
import { CheckCircle2, XCircle, Search, Loader2 } from "lucide-react";
import { Card, CardContent, CardHeader, CardTitle } from "@/modules/platform/components/ui/card";
import { Button } from "@/modules/platform/components/ui/button";
import { Label } from "@/modules/platform/components/ui/label";
import { RadioGroup, RadioGroupItem } from "@/modules/platform/components/ui/radio-group";

type EligibilityResult = {
  bsky: boolean;
  ayushman: boolean;
  reasons: string[];
  coverage: string;
};

export function EligibilityChecker() {
  const [step, setStep] = useState(1);
  const [loading, setLoading] = useState(false);
  const [result, setResult] = useState<EligibilityResult | null>(null);

  // Form data
  const [formData, setFormData] = useState({
    state: "odisha",
    rationCard: "",
    gender: "",
    constructionWorker: "",
    income: "",
  });

  const handleCheck = () => {
    setLoading(true);

    // Simulate API call
    setTimeout(() => {
      const bskyEligible =
        formData.rationCard === "bpl" ||
        formData.rationCard === "aay" ||
        formData.constructionWorker === "yes" ||
        formData.gender === "female";

      const ayushmanEligible =
        formData.rationCard === "bpl" ||
        formData.rationCard === "aay" ||
        formData.income === "below2l";

      const reasons: string[] = [];

      if (formData.rationCard === "bpl") {
        reasons.push("BPL card holder - eligible for both BSKY and Ayushman Bharat");
      }
      if (formData.rationCard === "aay") {
        reasons.push("AAY card holder - eligible for both schemes");
      }
      if (formData.gender === "female" && formData.state === "odisha") {
        reasons.push("All women in Odisha are eligible for BSKY with ₹10 lakh coverage");
      }
      if (formData.constructionWorker === "yes") {
        reasons.push("Registered construction worker - eligible for BSKY");
      }
      if (formData.income === "below2l") {
        reasons.push("Low income family - may be eligible for Ayushman Bharat");
      }
      if (!bskyEligible && !ayushmanEligible) {
        reasons.push("Based on the information provided, you may not be eligible. Please visit a CSC center with your documents for accurate verification.");
      }

      let coverage = "Not eligible for cashless coverage";
      if (bskyEligible && ayushmanEligible) {
        coverage = formData.gender === "female" ? "₹10 Lakh/year (BSKY+Ayushman combined)" : "₹5 Lakh/year (BSKY+Ayushman combined)";
      } else if (bskyEligible) {
        coverage = formData.gender === "female" ? "₹10 Lakh/year (BSKY)" : "₹5 Lakh/year (BSKY)";
      } else if (ayushmanEligible) {
        coverage = "₹5 Lakh/year (Ayushman Bharat)";
      }

      setResult({
        bsky: bskyEligible,
        ayushman: ayushmanEligible,
        reasons,
        coverage,
      });
      setLoading(false);
      setStep(3);
    }, 1500);
  };

  const resetChecker = () => {
    setStep(1);
    setResult(null);
    setFormData({
      state: "odisha",
      rationCard: "",
      gender: "",
      constructionWorker: "",
      income: "",
    });
  };

  return (
    <Card className="border-2 border-teal-200 shadow-lg">
      <CardHeader className="bg-gradient-to-r from-teal-50 to-teal-100">
        <CardTitle className="flex items-center gap-2 text-teal-800">
          <Search className="w-5 h-5" />
          Check Your Eligibility
        </CardTitle>
        <p className="text-sm text-teal-600">
          Answer a few questions to see if you qualify for BSKY or Ayushman Bharat
        </p>
      </CardHeader>
      <CardContent className="p-6">
        {step === 1 && (
          <div className="space-y-6">
            <div>
              <Label className="text-base font-medium">Do you have a ration card?</Label>
              <RadioGroup
                className="mt-3 space-y-2"
                value={formData.rationCard}
                onValueChange={(v) => setFormData({ ...formData, rationCard: v })}
              >
                <div className="flex items-center space-x-2">
                  <RadioGroupItem value="bpl" id="bpl" />
                  <Label htmlFor="bpl" className="font-normal">Yes, BPL (Below Poverty Line) card</Label>
                </div>
                <div className="flex items-center space-x-2">
                  <RadioGroupItem value="aay" id="aay" />
                  <Label htmlFor="aay" className="font-normal">Yes, AAY (Antyodaya Anna Yojana) card</Label>
                </div>
                <div className="flex items-center space-x-2">
                  <RadioGroupItem value="apl" id="apl" />
                  <Label htmlFor="apl" className="font-normal">Yes, APL (Above Poverty Line) card</Label>
                </div>
                <div className="flex items-center space-x-2">
                  <RadioGroupItem value="none" id="none" />
                  <Label htmlFor="none" className="font-normal">No ration card</Label>
                </div>
              </RadioGroup>
            </div>

            <div>
              <Label className="text-base font-medium">Your gender</Label>
              <RadioGroup
                className="mt-3 flex gap-4"
                value={formData.gender}
                onValueChange={(v) => setFormData({ ...formData, gender: v })}
              >
                <div className="flex items-center space-x-2">
                  <RadioGroupItem value="male" id="male" />
                  <Label htmlFor="male" className="font-normal">Male</Label>
                </div>
                <div className="flex items-center space-x-2">
                  <RadioGroupItem value="female" id="female" />
                  <Label htmlFor="female" className="font-normal">Female</Label>
                </div>
                <div className="flex items-center space-x-2">
                  <RadioGroupItem value="other" id="other" />
                  <Label htmlFor="other" className="font-normal">Other</Label>
                </div>
              </RadioGroup>
            </div>

            <Button
              onClick={() => setStep(2)}
              className="w-full bg-teal-600 hover:bg-teal-700"
              disabled={!formData.rationCard || !formData.gender}
            >
              Next
            </Button>
          </div>
        )}

        {step === 2 && (
          <div className="space-y-6">
            <div>
              <Label className="text-base font-medium">Are you a registered construction worker (BOCW)?</Label>
              <RadioGroup
                className="mt-3 flex gap-4"
                value={formData.constructionWorker}
                onValueChange={(v) => setFormData({ ...formData, constructionWorker: v })}
              >
                <div className="flex items-center space-x-2">
                  <RadioGroupItem value="yes" id="cw-yes" />
                  <Label htmlFor="cw-yes" className="font-normal">Yes</Label>
                </div>
                <div className="flex items-center space-x-2">
                  <RadioGroupItem value="no" id="cw-no" />
                  <Label htmlFor="cw-no" className="font-normal">No</Label>
                </div>
              </RadioGroup>
            </div>

            <div>
              <Label className="text-base font-medium">Annual family income</Label>
              <RadioGroup
                className="mt-3 space-y-2"
                value={formData.income}
                onValueChange={(v) => setFormData({ ...formData, income: v })}
              >
                <div className="flex items-center space-x-2">
                  <RadioGroupItem value="below2l" id="below2l" />
                  <Label htmlFor="below2l" className="font-normal">Below ₹2 Lakh</Label>
                </div>
                <div className="flex items-center space-x-2">
                  <RadioGroupItem value="2l-5l" id="2l-5l" />
                  <Label htmlFor="2l-5l" className="font-normal">₹2 Lakh - ₹5 Lakh</Label>
                </div>
                <div className="flex items-center space-x-2">
                  <RadioGroupItem value="above5l" id="above5l" />
                  <Label htmlFor="above5l" className="font-normal">Above ₹5 Lakh</Label>
                </div>
              </RadioGroup>
            </div>

            <div className="flex gap-3">
              <Button variant="outline" onClick={() => setStep(1)} className="flex-1">
                Back
              </Button>
              <Button
                onClick={handleCheck}
                className="flex-1 bg-teal-600 hover:bg-teal-700"
                disabled={!formData.constructionWorker || !formData.income}
              >
                Check Eligibility
              </Button>
            </div>
          </div>
        )}

        {loading && (
          <div className="py-12 text-center">
            <Loader2 className="w-10 h-10 animate-spin text-teal-600 mx-auto mb-4" />
            <p className="text-gray-600">Checking your eligibility...</p>
          </div>
        )}

        {step === 3 && result && !loading && (
          <div className="space-y-6">
            {/* Result Cards */}
            <div className="grid grid-cols-2 gap-4">
              <div className={`p-4 rounded-lg border-2 ${result.bsky ? "border-green-500 bg-green-50" : "border-gray-200 bg-gray-50"}`}>
                <div className="flex items-center gap-2 mb-2">
                  {result.bsky ? (
                    <CheckCircle2 className="w-5 h-5 text-green-600" />
                  ) : (
                    <XCircle className="w-5 h-5 text-gray-400" />
                  )}
                  <span className="font-semibold text-sm">BSKY</span>
                </div>
                <p className={`text-sm ${result.bsky ? "text-green-700" : "text-gray-500"}`}>
                  {result.bsky ? "Eligible" : "Not Eligible"}
                </p>
              </div>
              <div className={`p-4 rounded-lg border-2 ${result.ayushman ? "border-green-500 bg-green-50" : "border-gray-200 bg-gray-50"}`}>
                <div className="flex items-center gap-2 mb-2">
                  {result.ayushman ? (
                    <CheckCircle2 className="w-5 h-5 text-green-600" />
                  ) : (
                    <XCircle className="w-5 h-5 text-gray-400" />
                  )}
                  <span className="font-semibold text-sm">Ayushman Bharat</span>
                </div>
                <p className={`text-sm ${result.ayushman ? "text-green-700" : "text-gray-500"}`}>
                  {result.ayushman ? "Eligible" : "Not Eligible"}
                </p>
              </div>
            </div>

            {/* Coverage */}
            <div className="bg-teal-50 border border-teal-200 rounded-lg p-4 text-center">
              <p className="text-sm text-teal-600 mb-1">Estimated Annual Coverage</p>
              <p className="text-2xl font-bold text-teal-800">{result.coverage}</p>
            </div>

            {/* Reasons */}
            <div className="space-y-2">
              <p className="font-medium text-gray-700">Details:</p>
              <ul className="space-y-1">
                {result.reasons.map((reason, i) => (
                  <li key={i} className="text-sm text-gray-600 flex items-start gap-2">
                    <span className="text-teal-500 mt-0.5">•</span>
                    {reason}
                  </li>
                ))}
              </ul>
            </div>

            {/* Next Steps */}
            {(result.bsky || result.ayushman) && (
              <div className="bg-blue-50 border border-blue-200 rounded-lg p-4">
                <p className="text-sm text-blue-800">
                  <span className="font-semibold">Next Steps:</span> Visit your nearest Common Service Centre (CSC) or Gram Panchayat office with your Aadhaar card and ration card to get your Smart Health Card issued.
                </p>
              </div>
            )}

            <Button onClick={resetChecker} variant="outline" className="w-full">
              Check Again
            </Button>
          </div>
        )}
      </CardContent>
    </Card>
  );
}
