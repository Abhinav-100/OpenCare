"use client";

import { useActionState, useState, useEffect, useRef } from "react";
import Link from "next/link";
import { useSearchParams } from "next/navigation";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { Eye, EyeOff, Mail, Lock } from "lucide-react";
import { Card, CardContent, CardHeader, CardTitle } from "@/modules/platform/components/ui/card";
import { Button } from "@/modules/platform/components/ui/button";
import { Input } from "@/modules/platform/components/ui/input";
import { Label } from "@/modules/platform/components/ui/label";
import { loginSchema, LoginFormData } from "@/modules/access/validations/login-schema";
import { saveUserSession, hasAdminRole } from "@/shared/utils/auth-client";
import { useAuth } from "@/modules/access/context/auth-context";
import { submitLogin } from "./actions";

export default function LoginView() {
	const [showPassword, setShowPassword] = useState(false);
	const formLoadTime = useRef(Date.now());
	const [state, action, isLoading] = useActionState(submitLogin, {
		success: false,
	});
	const { refetchUser } = useAuth();
	const searchParams = useSearchParams();
	const successMessage = searchParams.get("message");

	// Handle redirect on successful login
	useEffect(() => {
		if (state.success && state.data) {
			// Save session to localStorage for client-side access
			saveUserSession(state.data);

			// Refetch user in auth context so all components update immediately
			refetchUser();

			if (state.shouldRedirect) {
				// Check if user is admin and redirect accordingly
				const isAdmin = hasAdminRole(state.data.access_token);
				const redirectTo = isAdmin ? "/admin" : "/dashboard";
				window.location.href = redirectTo;
			}
		}
	}, [state.success, state.shouldRedirect, state.data, refetchUser]);

	const {
		register,
		formState: { errors },
	} = useForm<LoginFormData>({
		resolver: zodResolver(loginSchema),
	});

	return (
		<div className="min-h-screen bg-gray-50 flex items-center justify-center py-12 px-4 sm:px-6 lg:px-8">
			<div className="max-w-md w-full space-y-8">
				{/* Header */}
				<div className="text-center">
					<h1 className="text-3xl font-bold text-gray-900 mb-2">
						Welcome Back
					</h1>
					<p className="text-gray-600">Sign in to your OpenCare account</p>
				</div>

				{/* Success Message from URL */}
				{successMessage && (
					<div className="bg-green-50 border border-green-200 rounded-md p-3">
						<p className="text-sm text-green-700">{successMessage}</p>
					</div>
				)}

				{/* Login Card */}
				<Card className="border border-gray-200 shadow-lg">
					<CardHeader className="space-y-1 pb-6">
						<CardTitle className="text-2xl font-bold text-center text-teal-700">
							Sign In
						</CardTitle>
					</CardHeader>
					<CardContent>
						<form action={action} className="space-y-6">
							{/* Honeypot field - hidden from users, bots will fill it */}
							<div className="hidden" aria-hidden="true">
								<label htmlFor="website_url">Website</label>
								<input
									type="text"
									id="website_url"
									name="website_url"
									tabIndex={-1}
									autoComplete="off"
								/>
							</div>

							{/* Hidden field for form load time (anti-bot) */}
							<input
								type="hidden"
								name="_formLoadTime"
								value={formLoadTime.current}
							/>

							{/* Server Error Display */}
							{state.error && (
								<div className="bg-red-50 border border-red-200 rounded-md p-3">
									<p className="text-sm text-red-600">{state.error}</p>
								</div>
							)}

							{/* Success Message */}
							{state.success && (
								<div className="bg-green-50 border border-green-200 rounded-md p-3">
									<p className="text-sm text-green-600">Login successful!</p>
								</div>
							)}

							{/* E-mail Field */}
							<div className="space-y-2">
								<Label
									htmlFor="username"
									className="text-sm font-medium text-gray-700"
								>
									E-mail
								</Label>
								<div className="relative">
									<Mail className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 w-5 h-5" />
									<Input
										id="username"
										type="text"
										placeholder="Enter your e-mail"
										className={`pl-10 h-12 border-gray-300 focus:border-teal-500 focus:ring-teal-500 ${
											errors.username
												? "border-red-500 focus:border-red-500"
												: ""
										}`}
										{...register("username")}
									/>
								</div>
								{errors.username && (
									<p className="text-sm text-red-600">
										{errors.username.message}
									</p>
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
										placeholder="Enter your password"
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

							{/* Remember Me & Forgot Password */}
							<div className="flex items-center justify-between">
								<div className="flex items-center">
									<input
										id="remember-me"
										name="remember-me"
										type="checkbox"
										className="h-4 w-4 text-teal-600 focus:ring-teal-500 border-gray-300 rounded"
									/>
									<label
										htmlFor="remember-me"
										className="ml-2 block text-sm text-gray-700"
									>
										Remember me
									</label>
								</div>
								<Link
									href="/forgot-password"
									className="text-sm text-teal-600 hover:text-teal-500"
								>
									Forgot password?
								</Link>
							</div>

							{/* Submit Button */}
							<Button
								type="submit"
								disabled={isLoading}
								className="w-full h-12 bg-teal-600 hover:bg-teal-700 text-white font-medium"
							>
								{isLoading ? "Signing In..." : "Sign In"}
							</Button>

							{/* Sign Up Link */}
							<div className="text-center">
								<p className="text-sm text-gray-600">
									Don&apos;t have an account?{" "}
									<Link
										href="/signup"
										className="text-teal-600 hover:text-teal-500 font-medium"
									>
										Sign up here
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
