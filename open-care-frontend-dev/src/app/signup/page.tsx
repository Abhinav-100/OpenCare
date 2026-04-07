"use client";

import { useState, useRef } from "react";
import Link from "next/link";
import { useRouter } from "next/navigation";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { useQuery } from "@tanstack/react-query";
import { Eye, EyeOff, User, Mail, Lock, Shield } from "lucide-react";
import { Card, CardContent, CardHeader, CardTitle } from "@/modules/platform/components/ui/card";
import { Button } from "@/modules/platform/components/ui/button";
import { Input } from "@/modules/platform/components/ui/input";
import { Label } from "@/modules/platform/components/ui/label";
import { Alert, AlertDescription } from "@/modules/platform/components/ui/alert";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/modules/platform/components/ui/select";
import { signupSchema, SignupFormData } from "@/modules/access/validations/signup-schema";
import { fetchDistricts } from "@/modules/catalog/api/locations";
import { fetchBloodGroups } from "@/modules/blood/api/blood-groups";
import { fetchGenders } from "@/modules/catalog/api/gender";
import { register as registerUser } from "@/modules/access/api/auth";
import type { District } from "@/shared/types/locations";
import type { BloodGroup } from "@/shared/types/blood-groups";
import type { Gender } from "@/shared/types/gender";

// Page flow: This route renders a screen entry and delegates business/data logic to module components.
export default function SignupPage() {
  const [showPassword, setShowPassword] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState("");
  const [botError, setBotError] = useState<string | null>(null);
  const formLoadTime = useRef(Date.now());
  const honeypotRef = useRef<HTMLInputElement>(null);
  const router = useRouter();

  const {
    register,
    handleSubmit,
    setValue,
    formState: { errors },
  } = useForm<SignupFormData>({
    resolver: zodResolver(signupSchema),
  });

  const onSubmit = async (data: SignupFormData) => {
    setBotError(null);
    setError("");

    // Honeypot check - if filled, it's a bot
    if (honeypotRef.current?.value) {
      setBotError("Submission blocked. Please try again.");
      return;
    }

    // Timing check - humans take at least 3 seconds to fill a signup form
    const timeSpent = Date.now() - formLoadTime.current;
    if (timeSpent < 3000) {
      setBotError("Please slow down. Try again in a moment.");
      return;
    }

    setIsLoading(true);

    try {
      const result = await registerUser(data);

      if (!result.ok) {
        setError(result.error ?? "An error occurred. Please try again.");
        return;
      }

      router.push("/login?message=Account created successfully! Please sign in.");
    } catch {
      setError("Could not connect to server. Please check your connection.");
    } finally {
      setIsLoading(false);
    }
  };

  // Fetch blood groups from API
  const {
    data: bloodGroups = [],
    isLoading: isLoadingBloodGroups,
    isError: isBloodGroupsError,
  } = useQuery<BloodGroup[]>({
    queryKey: ["bloodGroups"],
    queryFn: fetchBloodGroups,
  });

  // Fetch genders from API
  const {
    data: genders = [],
    isLoading: isLoadingGenders,
    isError: isGendersError,
  } = useQuery<Gender[]>({
    queryKey: ["genders"],
    queryFn: fetchGenders,
  });

  // Fetch districts from API
  const {
    data: districts = [],
    isLoading: isLoadingDistricts,
    isError: isDistrictsError,
  } = useQuery<District[]>({
    queryKey: ["districts"],
    queryFn: fetchDistricts,
  });

  return (
    <div className="min-h-screen bg-gray-50 flex items-center justify-center py-12 px-4 sm:px-6 lg:px-8">
      <div className="max-w-2xl w-full space-y-8">
        {/* Header */}
        <div className="text-center">
          <h1 className="text-3xl font-bold text-gray-900 mb-2">
            Join OpenCare
          </h1>
          <p className="text-gray-600">
            Create your account to access healthcare services
          </p>
        </div>

        {/* Signup Form */}
        <Card className="border border-gray-200 shadow-lg">
          <CardHeader className="space-y-1 pb-6">
            <CardTitle className="text-2xl font-bold text-center text-teal-700">
              Create Account
            </CardTitle>
          </CardHeader>
          <CardContent>
            <form onSubmit={handleSubmit(onSubmit)} className="space-y-6">
              {/* Honeypot field - hidden from users, bots will fill it */}
              <div className="hidden" aria-hidden="true">
                <label htmlFor="company_website">Company Website</label>
                <input
                  type="text"
                  id="company_website"
                  name="company_website"
                  ref={honeypotRef}
                  tabIndex={-1}
                  autoComplete="off"
                />
              </div>

              {/* Bot Error Display */}
              {botError && (
                <Alert className="border-yellow-200 bg-yellow-50">
                  <AlertDescription className="text-yellow-700 flex items-center gap-2">
                    <Shield className="w-4 h-4" />
                    {botError}
                  </AlertDescription>
                </Alert>
              )}

              {error && (
                <Alert className="border-red-200 bg-red-50">
                  <AlertDescription className="text-red-700">
                    {error}
                  </AlertDescription>
                </Alert>
              )}

              {/* Name Fields */}
              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                {/* First Name */}
                <div className="space-y-2">
                  <Label
                    htmlFor="firstName"
                    className="text-sm font-medium text-gray-700"
                  >
                    First Name
                  </Label>
                  <div className="relative">
                    <User className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 w-5 h-5" />
                    <Input
                      id="firstName"
                      type="text"
                      placeholder="Enter first name"
                      className={`pl-10 h-12 border-gray-300 focus:border-teal-500 focus:ring-teal-500 ${
                        errors.firstName
                          ? "border-red-500 focus:border-red-500"
                          : ""
                      }`}
                      {...register("firstName")}
                    />
                  </div>
                  {errors.firstName && (
                    <p className="text-sm text-red-600">
                      {errors.firstName.message}
                    </p>
                  )}
                </div>

                {/* Last Name */}
                <div className="space-y-2">
                  <Label
                    htmlFor="lastName"
                    className="text-sm font-medium text-gray-700"
                  >
                    Last Name
                  </Label>
                  <div className="relative">
                    <User className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 w-5 h-5" />
                    <Input
                      id="lastName"
                      type="text"
                      placeholder="Enter last name"
                      className={`pl-10 h-12 border-gray-300 focus:border-teal-500 focus:ring-teal-500 ${
                        errors.lastName
                          ? "border-red-500 focus:border-red-500"
                          : ""
                      }`}
                      {...register("lastName")}
                    />
                  </div>
                  {errors.lastName && (
                    <p className="text-sm text-red-600">
                      {errors.lastName.message}
                    </p>
                  )}
                </div>
              </div>

              {/* Email Field */}
              <div className="space-y-2">
                <Label
                  htmlFor="email"
                  className="text-sm font-medium text-gray-700"
                >
                  Email Address
                </Label>
                <div className="relative">
                  <Mail className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 w-5 h-5" />
                  <Input
                    id="email"
                    type="email"
                    placeholder="Enter your email"
                    className={`pl-10 h-12 border-gray-300 focus:border-teal-500 focus:ring-teal-500 ${
                      errors.email ? "border-red-500 focus:border-red-500" : ""
                    }`}
                    {...register("email")}
                  />
                </div>
                {errors.email && (
                  <p className="text-sm text-red-600">{errors.email.message}</p>
                )}
              </div>

              {/* Password Field */}
              <div className="space-y-2">
                <Label
                  htmlFor="password"
                  className="text-sm font-medium text-gray-700"
                >
                  Password
                </Label>
                <div className="relative">
                  <Lock className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 w-5 h-5" />
                  <Input
                    id="password"
                    type={showPassword ? "text" : "password"}
                    placeholder="Create a strong password"
                    className={`pl-10 pr-12 h-12 border-gray-300 focus:border-teal-500 focus:ring-teal-500 ${
                      errors.password
                        ? "border-red-500 focus:border-red-500"
                        : ""
                    }`}
                    {...register("password")}
                  />
                  <button
                    type="button"
                    onClick={() => setShowPassword(!showPassword)}
                    className="absolute right-3 top-1/2 transform -translate-y-1/2 text-gray-400 hover:text-gray-600"
                  >
                    {showPassword ? (
                      <EyeOff className="w-5 h-5" />
                    ) : (
                      <Eye className="w-5 h-5" />
                    )}
                  </button>
                </div>
                {errors.password && (
                  <p className="text-sm text-red-600">
                    {errors.password.message}
                  </p>
                )}
              </div>

              {/* Personal Info Grid */}
              <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                {/* Blood Group */}
                <div className="space-y-2">
                  <Label className="text-sm font-medium text-gray-700">
                    Blood Group
                  </Label>
                  <Select
                    onValueChange={(value) => setValue("bloodGroup", value)}
                    disabled={isLoadingBloodGroups || isBloodGroupsError}
                  >
                    <SelectTrigger
                      className={`h-12 border-gray-300 focus:border-teal-500 ${
                        errors.bloodGroup
                          ? "border-red-500 focus:border-red-500"
                          : ""
                      }`}
                    >
                      <SelectValue
                        placeholder={
                          isLoadingBloodGroups
                            ? "Loading blood groups..."
                            : isBloodGroupsError
                            ? "Failed to load blood groups"
                            : "Select blood group"
                        }
                      />
                    </SelectTrigger>
                    <SelectContent>
                      {bloodGroups.map((group: BloodGroup) => (
                        <SelectItem key={group.value} value={group.value}>
                          {group.displayName}
                        </SelectItem>
                      ))}
                    </SelectContent>
                  </Select>
                  {errors.bloodGroup && (
                    <p className="text-sm text-red-600">
                      {errors.bloodGroup.message}
                    </p>
                  )}
                </div>

                {/* Gender */}
                <div className="space-y-2">
                  <Label className="text-sm font-medium text-gray-700">
                    Gender
                  </Label>
                  <Select
                    onValueChange={(value) => setValue("gender", value)}
                    disabled={isLoadingGenders || isGendersError}
                  >
                    <SelectTrigger
                      className={`h-12 border-gray-300 focus:border-teal-500 ${
                        errors.gender
                          ? "border-red-500 focus:border-red-500"
                          : ""
                      }`}
                    >
                      <SelectValue
                        placeholder={
                          isLoadingGenders
                            ? "Loading genders..."
                            : isGendersError
                            ? "Failed to load genders"
                            : "Select gender"
                        }
                      />
                    </SelectTrigger>
                    <SelectContent>
                      {genders.map((gender: Gender) => (
                        <SelectItem key={gender.value} value={gender.value}>
                          {gender.displayName}
                        </SelectItem>
                      ))}
                    </SelectContent>
                  </Select>
                  {errors.gender && (
                    <p className="text-sm text-red-600">
                      {errors.gender.message}
                    </p>
                  )}
                </div>

                {/* District */}
                <div className="space-y-2">
                  <Label className="text-sm font-medium text-gray-700">
                    District
                  </Label>
                  <Select
                    onValueChange={(value) =>
                      setValue("districtId", parseInt(value))
                    }
                    disabled={isLoadingDistricts || isDistrictsError}
                  >
                    <SelectTrigger
                      className={`h-12 border-gray-300 focus:border-teal-500 ${
                        errors.districtId
                          ? "border-red-500 focus:border-red-500"
                          : ""
                      }`}
                    >
                      <SelectValue
                        placeholder={
                          isLoadingDistricts
                            ? "Loading districts..."
                            : isDistrictsError
                            ? "Failed to load districts"
                            : "Select district"
                        }
                      />
                    </SelectTrigger>
                    <SelectContent>
                      {districts.map((district: District) => (
                        <SelectItem
                          key={district.id}
                          value={district.id.toString()}
                        >
                          {district.name}
                        </SelectItem>
                      ))}
                    </SelectContent>
                  </Select>
                  {errors.districtId && (
                    <p className="text-sm text-red-600">
                      {errors.districtId.message}
                    </p>
                  )}
                </div>
              </div>

              {/* Terms Notice */}
              <p className="text-sm text-gray-500 text-center">
                By creating an account, you agree to our{" "}
                <Link
                  href="/terms"
                  className="text-teal-600 hover:text-teal-500"
                >
                  Terms of Service
                </Link>{" "}
                and{" "}
                <Link
                  href="/privacy"
                  className="text-teal-600 hover:text-teal-500"
                >
                  Privacy Policy
                </Link>
              </p>

              {/* Submit Button */}
              <Button
                type="submit"
                disabled={
                  isLoading ||
                  isLoadingDistricts ||
                  isLoadingBloodGroups ||
                  isLoadingGenders
                }
                className="w-full h-12 bg-teal-600 hover:bg-teal-700 text-white font-medium"
              >
                {isLoading ? "Creating Account..." : "Create Account"}
              </Button>

              {/* Login Link */}
              <div className="text-center">
                <p className="text-sm text-gray-600">
                  Already have an account?{" "}
                  <Link
                    href="/login"
                    className="text-teal-600 hover:text-teal-500 font-medium"
                  >
                    Sign in here
                  </Link>
                </p>
              </div>
            </form>
          </CardContent>
        </Card>
      </div>
    </div>
  );
}
