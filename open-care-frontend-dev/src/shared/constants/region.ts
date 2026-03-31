export const REGION_CONFIG = {
  country: "India",
  countryCode: "IN",
  countryValue: "INDIA",
  currency: "INR",
  currencySymbol: "₹",
  locale: "en-IN",
  dialCode: "+91",
} as const;

// Accepts +91XXXXXXXXXX or a 10-digit Indian mobile number starting with 6-9.
export const INDIAN_PHONE_REGEX = /^(\+91[6-9]\d{9}|[6-9]\d{9})$/;

export const INDIAN_PHONE_HINT =
  "Use +91XXXXXXXXXX or a 10-digit Indian mobile number";
