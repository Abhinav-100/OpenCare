import {
	Shield,
	Clock,
	Globe,
	Star,
	CheckCircle,
	HeartPulse,
} from "lucide-react";
import { Card, CardContent } from "@/modules/platform/components/ui/card";

export default function WhyChooseUsSection() {
	const features = [
		{
			icon: CheckCircle,
			title: "Verified Doctors",
			desc: "All healthcare professionals are verified and licensed",
		},
		{
			icon: Shield,
			title: "Secure Platform",
			desc: "Your medical data is protected with modern security",
		},
		{
			icon: Clock,
			title: "24/7 Access",
			desc: "Browse hospitals, doctors, and services anytime",
		},
		{
			icon: Globe,
			title: "Easy to Use",
			desc: "Simple and intuitive web interface for everyone",
		},
		{
			icon: HeartPulse,
			title: "Odisha Focused",
			desc: "Comprehensive coverage across all districts of Odisha",
		},
		{
			icon: Star,
			title: "Quality Care",
			desc: "Find the best healthcare services in your area",
		},
	];

	return (
		<section className="py-20 bg-slate-50">
			<div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
				<div className="text-center mb-16">
					<h2 className="text-4xl font-bold text-gray-900 mb-4">
						Why Choose OpenCare?
					</h2>
					<p className="text-xl text-gray-600">
						Healthcare made simple, accessible, and reliable for everyone
					</p>
				</div>

				<div className="grid md:grid-cols-2 lg:grid-cols-3 gap-6">
					{features.map((feature, index) => {
						const IconComponent = feature.icon;
						return (
							<Card
								key={index}
								className="text-center border-teal-600 hover:shadow-lg transition-shadow"
							>
								<CardContent className="py-6">
									<IconComponent className="w-8 h-8 text-teal-600 mx-auto mb-4" />
									<h3 className="text-lg font-bold text-gray-900 mb-2">
										{feature.title}
									</h3>
									<p className="text-gray-600 text-sm">{feature.desc}</p>
								</CardContent>
							</Card>
						);
					})}
				</div>
			</div>
		</section>
	);
}
