"use client";

import { useState, KeyboardEvent } from "react";
import { useRouter } from "next/navigation";
import Link from "next/link";
import Image from "next/image";
import {
	Search,
	Stethoscope,
	Ambulance,
	Pill,
	UserCheck,
} from "lucide-react";
import { Button } from "@/modules/platform/components/ui/button";
import { Card, CardContent } from "@/modules/platform/components/ui/card";
import { Input } from "@/modules/platform/components/ui/input";

export default function HeroSection() {
	const router = useRouter();
	const [searchQuery, setSearchQuery] = useState("");

	const handleSearch = () => {
		const q = searchQuery.trim().toLowerCase();
		if (!q) return;

		// Smart routing based on keywords
		const doctorKeywords = ["doctor", "doctors", "physician", "specialist"];
		const hospitalKeywords = ["hospital", "hospitals", "clinic", "medical center"];

		if (doctorKeywords.some((kw) => q === kw || q.includes(kw))) {
			router.push("/doctors");
			return;
		}

		if (hospitalKeywords.some((kw) => q === kw || q.includes(kw))) {
			router.push("/hospitals");
			return;
		}

		// Search for doctors by name
		router.push(`/doctors?name=${encodeURIComponent(searchQuery.trim())}`);
	};

	const handleKeyDown = (e: KeyboardEvent<HTMLInputElement>) => {
		if (e.key === "Enter") handleSearch();
	};

	return (
		<section className="bg-gradient-to-br from-teal-50 to-emerald-100 py-20">
			<div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
				<div className="grid lg:grid-cols-[1.05fr_0.95fr] gap-12 items-center">
					{/* Hero Content */}
					<div className="space-y-8 lg:pr-4">
						<div>
							<h1 className="text-5xl lg:text-6xl font-bold">
								<span className="text-gray-900">Your Health,</span>
								<br />
								<span className="text-teal-600">Our Priority</span>
							</h1>
							<p className="text-xl text-gray-600 mt-6 leading-relaxed">
								Connect with certified doctors, find hospitals,
								<br />
								access blood banks, and manage your healthcare
								<br />
								journey — all in one platform.
							</p>
						</div>

						{/* CTA Buttons */}
						<div className="flex flex-wrap gap-4">
							<Link href="/doctors">
								<Button size="lg" className="bg-teal-600 hover:bg-teal-700">
									<Stethoscope className="w-4 h-4 mr-2" />
									Find a Doctor
								</Button>
							</Link>
							<Link href="/hospitals">
								<Button
									size="lg"
									variant="outline"
									className="border-teal-600 text-teal-600 hover:bg-teal-50"
								>
									<Ambulance className="w-4 h-4 mr-2" />
									Find a Hospital
								</Button>
							</Link>
						</div>
					</div>

					{/* Hero Visual */}
					<div className="relative w-full max-w-[640px] lg:ml-auto">
						<Card className="w-full h-96 border-2 border-teal-600 bg-gradient-to-br from-white to-teal-50 overflow-hidden">
							<CardContent className="flex flex-col items-center justify-center h-full p-8">
								<Image
									src="/logo.png"
									alt="OpenCare - Healthcare for Odisha"
									width={200}
									height={200}
									className="object-contain mb-4 mx-auto"
								/>
								<h2 className="text-3xl font-bold text-teal-600">OpenCare</h2>
								<p className="text-gray-600 text-center mt-2">
									Healthcare Platform for Odisha
								</p>
							</CardContent>
						</Card>

						{/* Floating Icons */}
						<div className="absolute -top-4 -left-4 w-12 h-12 bg-teal-50 border border-teal-600 rounded-full flex items-center justify-center shadow-md">
							<UserCheck className="w-6 h-6 text-teal-600" />
						</div>
						<div className="absolute -top-4 -right-4 w-12 h-12 bg-teal-50 border border-teal-600 rounded-full flex items-center justify-center shadow-md">
							<Pill className="w-6 h-6 text-teal-600" />
						</div>
						<div className="absolute -bottom-4 -left-4 w-12 h-12 bg-teal-50 border border-teal-600 rounded-full flex items-center justify-center shadow-md">
							<Stethoscope className="w-6 h-6 text-teal-600" />
						</div>
						<div className="absolute -bottom-4 -right-4 w-12 h-12 bg-teal-50 border border-teal-600 rounded-full flex items-center justify-center shadow-md">
							<Ambulance className="w-6 h-6 text-teal-600" />
						</div>
					</div>
				</div>

				{/* Search Bar */}
				<div className="mt-12 max-w-2xl">
					<div className="relative">
						<Search className="absolute left-4 top-1/2 transform -translate-y-1/2 text-gray-400 w-5 h-5" />
						<Input
							value={searchQuery}
							onChange={(e) => setSearchQuery(e.target.value)}
							onKeyDown={handleKeyDown}
							placeholder="Search: Dr. Rajan, Cardiology, hospitals..."
							className="pl-12 pr-20 h-14 rounded-full border-2 border-teal-600 text-lg"
						/>
						<Button
							onClick={handleSearch}
							className="absolute right-2 top-1/2 transform -translate-y-1/2 bg-teal-600 hover:bg-teal-700 rounded-full"
						>
							Search
						</Button>
					</div>
					<p className="text-xs text-gray-500 mt-2 ml-4">
						Try: doctor names, specialities like &quot;Cardiology&quot;, or type &quot;hospitals&quot; to browse all
					</p>
				</div>
			</div>
		</section>
	);
}
