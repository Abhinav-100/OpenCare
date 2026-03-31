import { AdminHeader } from "@/modules/admin/components/admin-header";
import { AnalyticsCharts } from "@/modules/admin/components/analytics-charts";
import { AnalyticsStats } from "@/modules/admin/components/analytics-stats";

export default function AnalyticsPage() {
	return (
		<div className="flex flex-col">
			<AdminHeader
				title="Analytics & Reports"
				description="Comprehensive analytics for your healthcare facility"
			/>

			<div className="flex-1 space-y-4 p-4 md:p-8 pt-6">
				<AnalyticsStats />
				<AnalyticsCharts />
			</div>
		</div>
	);
}
