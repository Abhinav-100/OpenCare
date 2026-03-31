import { Rocket, Mail } from "lucide-react";
import Link from "next/link";
import { Button } from "@/modules/platform/components/ui/button";

export default function CTASection() {
	return (
		<section className="py-20 bg-gradient-to-r from-teal-600 to-teal-800">
			<div className="max-w-4xl mx-auto text-center px-4 sm:px-6 lg:px-8">
				<h2 className="text-4xl font-bold text-white mb-4">
					Ready to Take Control of Your Health?
				</h2>
				<p className="text-xl text-teal-100 mb-8">
					Start your healthcare journey with OpenCare today
				</p>
				<div className="flex flex-wrap justify-center gap-4 mb-8">
					<Link href="/signup">
						<Button
							size="lg"
							className="bg-white text-teal-600 hover:bg-teal-50"
						>
							<Rocket className="w-4 h-4 mr-2" />
							Get Started Now
						</Button>
					</Link>
				</div>
				<div className="flex items-center justify-center gap-2 text-teal-100">
					<Mail className="w-5 h-5" />
					<a
						href="mailto:support@opencare.in"
						className="hover:text-white transition-colors"
					>
						support@opencare.in
					</a>
				</div>
			</div>
		</section>
	);
}
