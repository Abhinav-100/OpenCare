import {
	Card,
	CardContent,
	CardDescription,
	CardHeader,
	CardTitle,
} from "@/modules/platform/components/ui/card";

const chartData = [
	{ month: "Jan", appointments: 432, revenue: 45000 },
	{ month: "Feb", appointments: 389, revenue: 42000 },
	{ month: "Mar", appointments: 498, revenue: 52000 },
	{ month: "Apr", appointments: 445, revenue: 48000 },
	{ month: "May", appointments: 512, revenue: 55000 },
	{ month: "Jun", appointments: 478, revenue: 51000 },
];

const departmentData = [
	{ department: "Cardiology", percentage: 25, color: "bg-teal-500" },
	{ department: "Emergency", percentage: 20, color: "bg-red-500" },
	{ department: "Pediatrics", percentage: 18, color: "bg-emerald-500" },
	{ department: "Neurology", percentage: 15, color: "bg-purple-500" },
	{ department: "Orthopedics", percentage: 12, color: "bg-amber-500" },
	{ department: "Others", percentage: 10, color: "bg-gray-400" },
];

const maxAppointments = Math.max(...chartData.map((d) => d.appointments));

export function AnalyticsCharts() {
	return (
		<div className="grid gap-4 md:grid-cols-2">
			{/* Monthly Trends */}
			<Card>
				<CardHeader>
					<CardTitle>Monthly Trends</CardTitle>
					<CardDescription>
						Appointments and revenue over the last 6 months
					</CardDescription>
				</CardHeader>
				<CardContent>
					<div className="space-y-4">
						{chartData.map((item) => {
							const pct = Math.round(
								(item.appointments / maxAppointments) * 100
							);
							return (
								<div key={item.month} className="space-y-1">
									<div className="flex justify-between text-xs text-gray-600">
										<span className="font-medium w-8">{item.month}</span>
										<span>{item.appointments} appts</span>
										<span className="text-teal-700 font-medium">
											₹{item.revenue.toLocaleString()}
										</span>
									</div>
									<div className="w-full bg-gray-100 rounded-full h-2">
										<div
											className="h-2 rounded-full bg-teal-500 transition-all"
											style={{ width: `${pct}%` }}
										/>
									</div>
								</div>
							);
						})}
					</div>
				</CardContent>
			</Card>

			{/* Department Distribution */}
			<Card>
				<CardHeader>
					<CardTitle>Department Distribution</CardTitle>
					<CardDescription>
						Patient distribution across departments
					</CardDescription>
				</CardHeader>
				<CardContent>
					<div className="space-y-4">
						{departmentData.map((dept, index) => (
							<div key={index} className="space-y-2">
								<div className="flex items-center justify-between">
									<div className="flex items-center gap-2">
										<div className={`w-3 h-3 rounded-full ${dept.color}`} />
										<span className="text-sm font-medium">
											{dept.department}
										</span>
									</div>
									<span className="text-sm text-muted-foreground">
										{dept.percentage}%
									</span>
								</div>
								<div className="w-full bg-gray-200 rounded-full h-2">
									<div
										className={`h-2 rounded-full ${dept.color}`}
										style={{ width: `${dept.percentage}%` }}
									/>
								</div>
							</div>
						))}
					</div>

					<div className="mt-6 pt-4 border-t">
						<p className="text-xs text-muted-foreground text-center">
							Total patients distributed across all departments
						</p>
					</div>
				</CardContent>
			</Card>
		</div>
	);
}
