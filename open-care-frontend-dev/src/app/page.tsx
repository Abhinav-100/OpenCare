import HeroSection from "@/modules/content/components/home/HeroSection";
import ServicesSection from "@/modules/content/components/home/ServicesSection";
import HowItWorksSection from "@/modules/content/components/home/HowItWorksSection";
import WhyChooseUsSection from "@/modules/content/components/home/WhyChooseUsSection";
import CTASection from "@/modules/content/components/home/CTASection";

// Page flow: This route renders a screen entry and delegates business/data logic to module components.
export default function Home() {
	return (
		<div className="min-h-screen bg-slate-50">
			<HeroSection />
			<ServicesSection />
			<HowItWorksSection />
			<WhyChooseUsSection />
			<CTASection />
		</div>
	);
}
