"use client";

import { useState } from "react";
import Link from "next/link";
import { Mail, ArrowLeft, Loader2 } from "lucide-react";
import { Card, CardContent, CardHeader, CardTitle } from "@/modules/platform/components/ui/card";
import { Button } from "@/modules/platform/components/ui/button";
import { Input } from "@/modules/platform/components/ui/input";
import { Label } from "@/modules/platform/components/ui/label";
import { forgotPassword } from "@/modules/access/api/auth";

export default function ForgotPasswordPage() {
	const [email, setEmail] = useState("");
	const [submitted, setSubmitted] = useState(false);
	const [loading, setLoading] = useState(false);
	const [error, setError] = useState("");

	const handleSubmit = async (e: React.FormEvent) => {
		e.preventDefault();
		setError("");
		setLoading(true);
		try {
			const result = await forgotPassword(email);
			if (!result.ok) {
				setError(result.error ?? "Something went wrong. Please try again.");
			} else {
				setSubmitted(true);
			}
		} catch {
			setError("Could not connect to the server. Please try again.");
		} finally {
			setLoading(false);
		}
	};

	return (
		<div className="min-h-screen bg-gray-50 flex items-center justify-center py-12 px-4 sm:px-6 lg:px-8">
			<div className="max-w-md w-full space-y-8">
				{/* Header */}
				<div className="text-center">
					<h1 className="text-3xl font-bold text-gray-900 mb-2">
						Reset Password
					</h1>
					<p className="text-gray-600">
						Enter your email and we&apos;ll send you a reset link
					</p>
				</div>

				<Card className="border border-gray-200 shadow-lg">
					<CardHeader className="space-y-1 pb-6">
						<CardTitle className="text-2xl font-bold text-center text-teal-700">
							Forgot Password
						</CardTitle>
					</CardHeader>
					<CardContent>
						{submitted ? (
							<div className="space-y-4">
								<div className="bg-green-50 border border-green-200 rounded-md p-4 text-center">
									<p className="text-sm text-green-700 font-medium">
										If an account exists for{" "}
										<span className="font-bold">{email}</span>, you will receive
										a password reset email shortly.
									</p>
								</div>
								<Link href="/login">
									<Button
										variant="outline"
										className="w-full border-teal-600 text-teal-600 hover:bg-teal-50"
									>
										<ArrowLeft className="w-4 h-4 mr-2" />
										Back to Sign In
									</Button>
								</Link>
							</div>
						) : (
							<form onSubmit={handleSubmit} className="space-y-6">
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
											required
											placeholder="Enter your email address"
											value={email}
											onChange={(e) => setEmail(e.target.value)}
											className="pl-10 h-12 border-gray-300 focus:border-teal-500 focus:ring-teal-500"
										/>
									</div>
								</div>

								{error && (
									<p className="text-sm text-red-600 bg-red-50 border border-red-200 rounded-md px-3 py-2">
										{error}
									</p>
								)}

								<Button
									type="submit"
									disabled={loading}
									className="w-full h-12 bg-teal-600 hover:bg-teal-700 text-white font-medium"
								>
									{loading ? (
										<><Loader2 className="w-4 h-4 mr-2 animate-spin" />Sending...</>
									) : (
										"Send Reset Link"
									)}
								</Button>

								<div className="text-center">
									<Link
										href="/login"
										className="text-sm text-teal-600 hover:text-teal-500 flex items-center justify-center gap-1"
									>
										<ArrowLeft className="w-4 h-4" />
										Back to Sign In
									</Link>
								</div>
							</form>
						)}
					</CardContent>
				</Card>
			</div>
		</div>
	);
}
