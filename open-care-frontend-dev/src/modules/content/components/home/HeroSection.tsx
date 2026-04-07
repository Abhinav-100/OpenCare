"use client";

import Link from "next/link";
import Image from "next/image";
import {
	Stethoscope,
	Ambulance,
} from "lucide-react";
import { Button } from "@/modules/platform/components/ui/button";

export default function HeroSection() {
	return (
		<section className="bg-gradient-to-br from-teal-50 via-emerald-50 to-emerald-100 py-20 lg:py-24">
			<div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
				<div className="grid lg:grid-cols-[0.85fr_1.15fr] gap-12 items-center">
					{/* Hero Content */}
					<div className="lg:pl-12 xl:pl-20 lg:order-2">
						<div className="p-2 lg:p-4">
							<div className="space-y-6">
								<h1 className="text-4xl lg:text-5xl xl:text-6xl font-semibold leading-tight tracking-tight">
									<span className="text-slate-900">Your Health,</span>
									<br />
									<span className="text-teal-600 font-semibold">Our Priority</span>
								</h1>
								<p className="text-lg lg:text-xl text-slate-700 mt-4 leading-relaxed max-w-xl">
									Connect with certified doctors, find hospitals, access blood banks, and manage your healthcare journey all in one platform.
								</p>
							</div>

							{/* CTA Buttons */}
							<div className="flex flex-wrap gap-4 mt-8">
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
					</div>

					{/* Hero Visual */}
					<div className="relative w-full max-w-[520px] lg:mr-auto lg:order-1">
						<div className="w-full p-4 lg:p-6">
							<div className="flex flex-col items-center sm:items-start gap-6 w-full">
							<Image
								src="/logo.png"
								alt="OpenCare - Healthcare for Odisha"
								width={200}
								height={200}
								className="object-contain shrink-0 w-36 h-36 sm:w-44 sm:h-44 lg:w-52 lg:h-52"
							/>
							<div className="text-center sm:text-left">
								<h2 className="text-3xl lg:text-4xl font-semibold text-teal-600 leading-none">OpenCare</h2>
								<p className="text-gray-600 mt-3 text-lg max-w-xs leading-relaxed mx-auto sm:mx-0">
									Healthcare Platform for Odisha
								</p>
							</div>
							</div>
						</div>
					</div>
				</div>

			</div>
		</section>
	);
}
