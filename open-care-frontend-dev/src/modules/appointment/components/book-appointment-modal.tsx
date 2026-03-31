"use client";

import { useState } from "react";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { useRouter } from "next/navigation";
import {
	Clock,
	Calendar as CalendarIcon,
	Video,
	Building2,
	IndianRupee,
	Loader2,
	CheckCircle2,
} from "lucide-react";
import { toast } from "sonner";
import { format } from "date-fns";
import { Calendar } from "@/modules/platform/components/ui/calendar";
import {
	Dialog,
	DialogContent,
	DialogDescription,
	DialogHeader,
	DialogTitle,
} from "@/modules/platform/components/ui/dialog";
import { Button } from "@/modules/platform/components/ui/button";
import { Badge } from "@/modules/platform/components/ui/badge";
import { Textarea } from "@/modules/platform/components/ui/textarea";
import { Label } from "@/modules/platform/components/ui/label";
import { fetchDoctorSlots, createAppointment } from "@/modules/clinical/api/appointments";
import type { DoctorDetailsResponse, DoctorResponse } from "@/shared/types/doctors";
import type { CreateAppointmentRequest, AvailableSlot } from "@/shared/types/appointments";

interface BookAppointmentModalProps {
	open: boolean;
	onOpenChange: (open: boolean) => void;
	doctor: DoctorResponse | DoctorDetailsResponse;
}

export function BookAppointmentModal({
	open,
	onOpenChange,
	doctor,
}: BookAppointmentModalProps) {
	const router = useRouter();
	const queryClient = useQueryClient();
	const [selectedDate, setSelectedDate] = useState<Date>();
	const [selectedSlot, setSelectedSlot] = useState<AvailableSlot | null>(null);
	const [appointmentType, setAppointmentType] = useState<"ONLINE" | "OFFLINE">("ONLINE");
	const [symptoms, setSymptoms] = useState("");
	const onlineConsultationFee = doctor.consultationFeeOnline ?? 500;
	const offlineConsultationFee = doctor.consultationFeeOffline ?? 700;
	const doctorSpecializations = doctor.specializations || "General Medicine";
	const doctorExperience = doctor.yearOfExperience ?? 0;

	// Fetch available slots when date is selected
	const {
		data: slots = [],
		isLoading: slotsLoading,
		isError: slotsError,
		error: slotsErrorDetails,
	} = useQuery({
		queryKey: ["doctor-slots", doctor.id, selectedDate],
		queryFn: () =>
			fetchDoctorSlots(doctor.id, format(selectedDate!, "yyyy-MM-dd")),
		enabled: !!selectedDate,
	});

	// Create appointment mutation
	const createAppointmentMutation = useMutation({
		mutationFn: createAppointment,
		onSuccess: (data) => {
			toast.success("Appointment booked successfully!", {
				description: `Your appointment is confirmed for ${format(new Date(data.appointmentDate), "PPP")} at ${data.startTime}`,
				icon: <CheckCircle2 className="h-4 w-4" />,
			});
			queryClient.invalidateQueries({ queryKey: ["my-appointments"] });
			onOpenChange(false);
			// Navigate to appointments page
			setTimeout(() => {
				router.push("/appointments");
			}, 1000);
		},
		onError: (error: Error) => {
			toast.error("Failed to book appointment", {
				description: error.message || "Please try again later",
			});
		},
	});

	const handleBookAppointment = () => {
		if (!selectedDate || !selectedSlot) {
			toast.error("Please select a date and time slot");
			return;
		}

		const consultationFee =
			appointmentType === "ONLINE"
				? Number(onlineConsultationFee)
				: Number(offlineConsultationFee);

		const requestData: CreateAppointmentRequest = {
			doctorId: doctor.id,
			appointmentType,
			appointmentDate: format(selectedDate, "yyyy-MM-dd"),
			startTime: selectedSlot.startTime,
			endTime: selectedSlot.endTime,
			durationMinutes: 30,
			consultationFee,
			symptoms: symptoms || undefined,
		};

		createAppointmentMutation.mutate(requestData);
	};

	const availableSlots = slots.filter((slot) => slot.isAvailable);
	const slotsErrorMessage =
		slotsErrorDetails instanceof Error
			? slotsErrorDetails.message
			: "Failed to load available slots";

	return (
		<Dialog open={open} onOpenChange={onOpenChange}>
			<DialogContent className="max-w-3xl max-h-[90vh] overflow-y-auto">
				<DialogHeader>
					<DialogTitle className="text-2xl">Book Appointment</DialogTitle>
					<DialogDescription>
						Schedule an appointment with {doctor.profile.name}
					</DialogDescription>
				</DialogHeader>

				<div className="space-y-6">
					{/* Doctor Info */}
					<div className="flex items-center gap-4 p-4 bg-gray-50 rounded-lg">
						<div className="w-16 h-16 rounded-full bg-teal-500 flex items-center justify-center text-white text-xl font-bold">
							{doctor.profile.name
								.split(" ")
								.map((n) => n[0])
								.join("")
								.toUpperCase()
								.slice(0, 2)}
						</div>
						<div>
							<h3 className="font-semibold text-lg">{doctor.profile.name}</h3>
							<p className="text-sm text-gray-600">{doctorSpecializations}</p>
							<p className="text-sm text-gray-500">
								{doctorExperience} years experience
							</p>
						</div>
					</div>

					{/* Appointment Type Selection */}
					<div>
						<Label className="text-sm font-medium mb-2 block">
							Consultation Type
						</Label>
						<div className="grid grid-cols-2 gap-3">
							<button
								onClick={() => setAppointmentType("ONLINE")}
								className={`p-4 border-2 rounded-lg transition-all ${
									appointmentType === "ONLINE"
										? "border-teal-600 bg-teal-50"
										: "border-gray-200 hover:border-gray-300"
								}`}
							>
								<div className="flex items-center justify-between mb-2">
									<Video className="w-5 h-5 text-teal-600" />
									{appointmentType === "ONLINE" && (
										<CheckCircle2 className="w-5 h-5 text-teal-600" />
									)}
								</div>
								<div className="text-left">
									<div className="font-medium">Online Consultation</div>
									<div className="text-sm text-gray-500 flex items-center gap-1">
										<IndianRupee className="w-3 h-3" />
										{onlineConsultationFee}
									</div>
								</div>
							</button>

							<button
								onClick={() => setAppointmentType("OFFLINE")}
								className={`p-4 border-2 rounded-lg transition-all ${
									appointmentType === "OFFLINE"
										? "border-teal-600 bg-teal-50"
										: "border-gray-200 hover:border-gray-300"
								}`}
							>
								<div className="flex items-center justify-between mb-2">
									<Building2 className="w-5 h-5 text-teal-600" />
									{appointmentType === "OFFLINE" && (
										<CheckCircle2 className="w-5 h-5 text-teal-600" />
									)}
								</div>
								<div className="text-left">
									<div className="font-medium">In-Person Visit</div>
									<div className="text-sm text-gray-500 flex items-center gap-1">
										<IndianRupee className="w-3 h-3" />
										{offlineConsultationFee}
									</div>
								</div>
							</button>
						</div>
					</div>

					{/* Date Selection */}
					<div>
						<Label className="text-sm font-medium mb-2 block">
							Select Date
						</Label>
						<div className="border rounded-lg p-4">
							<Calendar
								mode="single"
								selected={selectedDate}
								onSelect={setSelectedDate}
								disabled={(date) => date < new Date() || date > new Date(Date.now() + 30 * 24 * 60 * 60 * 1000)}
								className="rounded-md"
							/>
						</div>
					</div>

					{/* Time Slots */}
					{selectedDate && (
						<div>
							<Label className="text-sm font-medium mb-2 block">
								Available Time Slots
							</Label>
							{slotsLoading ? (
								<div className="flex items-center justify-center py-8">
									<Loader2 className="w-6 h-6 animate-spin text-teal-600" />
								</div>
							) : slotsError ? (
								<div className="text-center py-8 text-red-600">
									<CalendarIcon className="w-12 h-12 mx-auto mb-2 text-red-300" />
									<p>Unable to load slots for this date</p>
									<p className="text-sm text-red-500">{slotsErrorMessage}</p>
								</div>
							) : availableSlots.length === 0 ? (
								<div className="text-center py-8 text-gray-500">
									<CalendarIcon className="w-12 h-12 mx-auto mb-2 text-gray-300" />
									<p>No slots available for this date</p>
									<p className="text-sm">Please select another date</p>
								</div>
							) : (
								<div className="grid grid-cols-3 gap-2">
									{availableSlots.map((slot, index) => (
										<button
											key={index}
											onClick={() => setSelectedSlot(slot)}
											className={`p-3 border-2 rounded-lg transition-all ${
												selectedSlot === slot
													? "border-teal-600 bg-teal-50"
													: "border-gray-200 hover:border-gray-300"
											}`}
										>
											<div className="flex items-center justify-center gap-1 text-sm font-medium">
												<Clock className="w-4 h-4" />
												{slot.startTime}
											</div>
										</button>
									))}
								</div>
							)}
						</div>
					)}

					{/* Symptoms (Optional) */}
					{selectedSlot && (
						<div>
							<Label htmlFor="symptoms" className="text-sm font-medium mb-2 block">
								Symptoms or Reason (Optional)
							</Label>
							<Textarea
								id="symptoms"
								placeholder="Describe your symptoms or reason for consultation..."
								value={symptoms}
								onChange={(e) => setSymptoms(e.target.value)}
								rows={3}
							/>
						</div>
					)}

					{/* Booking Summary */}
					{selectedSlot && (
						<div className="bg-teal-50 p-4 rounded-lg border border-teal-200">
							<h4 className="font-medium mb-2">Booking Summary</h4>
							<div className="space-y-1 text-sm">
								<div className="flex justify-between">
									<span className="text-gray-600">Date:</span>
									<span className="font-medium">
										{format(selectedDate!, "PPP")}
									</span>
								</div>
								<div className="flex justify-between">
									<span className="text-gray-600">Time:</span>
									<span className="font-medium">
										{selectedSlot.startTime} - {selectedSlot.endTime}
									</span>
								</div>
								<div className="flex justify-between">
									<span className="text-gray-600">Type:</span>
									<Badge variant="outline">
										{appointmentType === "ONLINE" ? "Online" : "In-Person"}
									</Badge>
								</div>
								<div className="flex justify-between items-center pt-2 border-t border-teal-300">
									<span className="text-gray-600">Consultation Fee:</span>
									<span className="font-semibold text-lg flex items-center">
										<IndianRupee className="w-4 h-4" />
										{appointmentType === "ONLINE"
											? doctor.consultationFeeOnline || 500
											: doctor.consultationFeeOffline || 700}
									</span>
								</div>
							</div>
						</div>
					)}

					{/* Action Buttons */}
					<div className="flex gap-3">
						<Button
							variant="outline"
							onClick={() => onOpenChange(false)}
							className="flex-1"
							disabled={createAppointmentMutation.isPending}
						>
							Cancel
						</Button>
						<Button
							onClick={handleBookAppointment}
							className="flex-1 bg-teal-600 hover:bg-teal-700"
							disabled={
								!selectedDate ||
								!selectedSlot ||
								createAppointmentMutation.isPending
							}
						>
							{createAppointmentMutation.isPending ? (
								<>
									<Loader2 className="w-4 h-4 mr-2 animate-spin" />
									Booking...
								</>
							) : (
								"Confirm Booking"
							)}
						</Button>
					</div>
				</div>
			</DialogContent>
		</Dialog>
	);
}
