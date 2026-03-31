import { ChevronRight } from "lucide-react";

export default function HowItWorksSection() {
	const steps = [
		{
			number: 1,
			title: "Choose Service",
			description: "Select from doctors, ambulance, or blood bank",
		},
		{
			number: 2,
			title: "Book and Pay",
			description: "Schedule appointment and make secure payment",
		},
		{
			number: 3,
			title: "Get Care",
			description: "Receive quality healthcare service",
		},
		{
			number: 4,
			title: "Follow Up",
			description: "Access records and continue your care",
		},
	];

	return (
		<section className="py-20 bg-white">
			<div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
				<div className="text-center mb-16">
					<h2 className="text-4xl font-bold text-gray-900 mb-4">
						How OpenCare Works
					</h2>
					<p className="text-xl text-gray-600">
						Getting healthcare has never been this simple
					</p>
				</div>

				<div className="flex flex-col md:flex-row items-center justify-center gap-4 md:gap-2">
					{steps.map((step, index) => (
						<div key={step.number} className="flex items-center">
							{/* Step */}
							<div className="text-center px-4">
								<div className="w-20 h-20 bg-teal-600 rounded-full flex items-center justify-center mx-auto mb-6">
									<span className="text-2xl font-bold text-white">
										{step.number}
									</span>
								</div>
								<h3 className="text-xl font-bold text-gray-900 mb-2">
									{step.title}
								</h3>
								<p className="text-gray-600 max-w-[180px] mx-auto">
									{step.description}
								</p>
							</div>

							{/* Arrow (not after last step) */}
							{index < steps.length - 1 && (
								<div className="hidden md:flex items-center justify-center mx-2">
									<ChevronRight className="w-8 h-8 text-teal-400" />
								</div>
							)}
						</div>
					))}
				</div>
			</div>
		</section>
	);
}
