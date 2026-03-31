import { z } from "zod";
import { INDIAN_PHONE_HINT, INDIAN_PHONE_REGEX } from "@/shared/constants/region";

export const addAssociationSchema = z.object({
  // Basic Info
  name: z.string().min(1, "Association name is required"),
  bnName: z.string().optional().or(z.literal("")),
  shortName: z.string().optional().or(z.literal("")),
  description: z.string().optional().or(z.literal("")),

  // Association Details
  associationType: z.string().min(1, "Association type is required"),
  medicalSpecialityId: z.number().optional().nullable(),
  domain: z.string().min(1, "Domain is required"),

  // Dates
  foundedDate: z.string().optional().or(z.literal("")),

  // Media
  logoUrl: z.string().url("Invalid logo URL").optional().or(z.literal("")),

  // Social Media
  websiteUrl: z
    .string()
    .url("Invalid website URL")
    .optional()
    .or(z.literal("")),
  facebookUrl: z
    .string()
    .url("Invalid Facebook URL")
    .optional()
    .or(z.literal("")),
  twitterUrl: z
    .string()
    .url("Invalid Twitter URL")
    .optional()
    .or(z.literal("")),
  linkedinUrl: z
    .string()
    .url("Invalid LinkedIn URL")
    .optional()
    .or(z.literal("")),
  youtubeUrl: z
    .string()
    .url("Invalid YouTube URL")
    .optional()
    .or(z.literal("")),

  // Contact Info
  email: z.string().email("Invalid email").optional().or(z.literal("")),
  phone: z
    .string()
    .optional()
    .or(z.literal(""))
    .refine((val) => !val || INDIAN_PHONE_REGEX.test(val), INDIAN_PHONE_HINT),

  // Status
  isAffiliated: z.boolean(),
  isActive: z.boolean(),

  // Location
  divisionId: z.number().optional().nullable(),
  districtId: z.number().optional().nullable(),
  upazilaId: z.number().optional().nullable(),
  originCountry: z.string().min(1, "Origin country is required"),
});

export type AddAssociationFormData = z.infer<typeof addAssociationSchema>;
