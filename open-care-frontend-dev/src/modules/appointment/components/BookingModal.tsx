"use client";

import { useState } from "react";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { useRouter } from "next/navigation";
import { Calendar as CalendarIcon, Clock, Video, MapPin, Loader2 } from "lucide-react";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
} from "@/modules/platform/components/ui/dialog";
import { Button } from "@/modules/platform/components/ui/button";
import { Label } from "@/modules/platform/components/ui/label";
import { Textarea } from "@/modules/platform/components/ui/textarea";
import { RadioGroup, RadioGroupItem } from "@/modules/platform/components/ui/radio-group";
import { Calendar } from "@/modules/platform/components/ui/calendar";
import { fetchDoctorSlots, createAppointment } from "@/modules/clinical/api/appointments";
import { useAuth } from "@/modules/access/context/auth-context";
import type { DoctorResponse } from "@/shared/types/doctors";
import type { AvailableSlot, CreateAppointmentRequest } from "@/shared/types/appointments";

interface BookingModalProps {
  isOpen: boolean;
  onClose: () => void;
  doctor: DoctorResponse;
}

export function BookingModal({ isOpen, onClose, doctor }: BookingModalProps) {
  const router = useRouter();
  const queryClient = useQueryClient();
  const { isAuthenticated } = useAuth();

  const [selectedDate, setSelectedDate] = useState<Date | undefined>(undefined);
  const [selectedSlot, setSelectedSlot] = useState<AvailableSlot | null>(null);
  const [appointmentType, setAppointmentType] = useState<"ONLINE" | "OFFLINE">("OFFLINE");
  const [symptoms, setSymptoms] = useState("");
  const [step, setStep] = useState<"date" | "slot" | "details" | "confirm">("date");

  const dateStr = selectedDate?.toISOString().split("T")[0] || "";

  const {
    data: slots = [],
    isLoading: isSlotsLoading,
    error: slotsError,
  } = useQuery({
    queryKey: ["doctor-slots", doctor.id, dateStr],
    queryFn: () => fetchDoctorSlots(doctor.id, dateStr),
    enabled: !!selectedDate && isOpen,
  });

  const bookingMutation = useMutation({
    mutationFn: createAppointment,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["my-appointments"] });
      router.push("/appointments?booked=true");
      onClose();
    },
  });

  const handleDateSelect = (date: Date | undefined) => {
    setSelectedDate(date);
    setSelectedSlot(null);
    if (date) {
      setStep("slot");
    }
  };

  const handleSlotSelect = (slot: AvailableSlot) => {
    if (slot.isAvailable) {
      setSelectedSlot(slot);
      setStep("details");
    }
  };

  const handleSubmit = () => {
    if (!selectedDate || !selectedSlot) return;

    const request: CreateAppointmentRequest = {
      doctorId: doctor.id,
      appointmentType,
      appointmentDate: dateStr,
      startTime: selectedSlot.startTime,
      endTime: selectedSlot.endTime,
      consultationFee: 500,
      symptoms: symptoms || undefined,
    };

    bookingMutation.mutate(request);
  };

  const formatTime = (timeStr: string) => {
    const [hours, minutes] = timeStr.split(":");
    const date = new Date();
    date.setHours(parseInt(hours), parseInt(minutes));
    return date.toLocaleTimeString("en-IN", {
      hour: "numeric",
      minute: "2-digit",
      hour12: true,
    });
  };

  const availableSlots = slots.filter((s) => s.isAvailable);

  if (!isAuthenticated) {
    return (
      <Dialog open={isOpen} onOpenChange={onClose}>
        <DialogContent className="sm:max-w-md">
          <DialogHeader>
            <DialogTitle>Sign In Required</DialogTitle>
          </DialogHeader>
          <div className="py-6 text-center">
            <p className="text-gray-600 mb-4">
              Please sign in to book an appointment.
            </p>
            <Button
              onClick={() => router.push("/login")}
              className="bg-teal-600 hover:bg-teal-700"
            >
              Sign In
            </Button>
          </div>
        </DialogContent>
      </Dialog>
    );
  }

  return (
    <Dialog open={isOpen} onOpenChange={onClose}>
      <DialogContent className="sm:max-w-lg max-h-[90vh] overflow-y-auto">
        <DialogHeader>
          <DialogTitle>Book Appointment with {doctor.profile?.name}</DialogTitle>
        </DialogHeader>

        {/* Progress Steps */}
        <div className="flex items-center justify-between mb-6">
          {["date", "slot", "details", "confirm"].map((s, i) => (
            <div key={s} className="flex items-center">
              <div
                className={`w-8 h-8 rounded-full flex items-center justify-center text-sm font-medium ${
                  step === s
                    ? "bg-teal-600 text-white"
                    : i < ["date", "slot", "details", "confirm"].indexOf(step)
                    ? "bg-teal-100 text-teal-600"
                    : "bg-gray-100 text-gray-400"
                }`}
              >
                {i + 1}
              </div>
              {i < 3 && (
                <div
                  className={`w-12 h-1 mx-1 ${
                    i < ["date", "slot", "details", "confirm"].indexOf(step)
                      ? "bg-teal-600"
                      : "bg-gray-200"
                  }`}
                />
              )}
            </div>
          ))}
        </div>

        {/* Step 1: Select Date */}
        {step === "date" && (
          <div className="space-y-4">
            <Label className="text-base font-medium">Select Date</Label>
            <Calendar
              mode="single"
              selected={selectedDate}
              onSelect={handleDateSelect}
              disabled={(date) => {
                const today = new Date();
                today.setHours(0, 0, 0, 0);
                return date < today;
              }}
              className="rounded-md border mx-auto"
            />
          </div>
        )}

        {/* Step 2: Select Time Slot */}
        {step === "slot" && (
          <div className="space-y-4">
            <div className="flex items-center justify-between">
              <Label className="text-base font-medium">Select Time</Label>
              <Button variant="ghost" size="sm" onClick={() => setStep("date")}>
                Change Date
              </Button>
            </div>

            <p className="text-sm text-gray-500">
              <CalendarIcon className="w-4 h-4 inline mr-1" />
              {selectedDate?.toLocaleDateString("en-IN", {
                weekday: "long",
                month: "long",
                day: "numeric",
              })}
            </p>

            {isSlotsLoading ? (
              <div className="flex items-center justify-center py-8">
                <Loader2 className="w-6 h-6 animate-spin text-teal-600" />
              </div>
            ) : slotsError ? (
              <div className="text-center py-8 text-red-600">
                Failed to load available slots
              </div>
            ) : availableSlots.length === 0 ? (
              <div className="text-center py-8">
                <Clock className="w-12 h-12 text-gray-300 mx-auto mb-2" />
                <p className="text-gray-500">
                  No available slots for this date
                </p>
                <Button
                  variant="outline"
                  className="mt-4"
                  onClick={() => setStep("date")}
                >
                  Choose Another Date
                </Button>
              </div>
            ) : (
              <div className="grid grid-cols-3 gap-2 max-h-48 overflow-y-auto">
                {slots.map((slot, i) => (
                  <button
                    key={i}
                    onClick={() => handleSlotSelect(slot)}
                    disabled={!slot.isAvailable}
                    className={`p-2 text-sm rounded-md border transition-colors ${
                      selectedSlot?.startTime === slot.startTime
                        ? "bg-teal-600 text-white border-teal-600"
                        : slot.isAvailable
                        ? "hover:border-teal-600 hover:text-teal-600"
                        : "bg-gray-100 text-gray-400 cursor-not-allowed"
                    }`}
                  >
                    {formatTime(slot.startTime)}
                  </button>
                ))}
              </div>
            )}
          </div>
        )}

        {/* Step 3: Appointment Details */}
        {step === "details" && (
          <div className="space-y-4">
            <div className="flex items-center justify-between">
              <Label className="text-base font-medium">Appointment Details</Label>
              <Button variant="ghost" size="sm" onClick={() => setStep("slot")}>
                Change Slot
              </Button>
            </div>

            <div className="bg-gray-50 p-3 rounded-md text-sm">
              <p>
                <CalendarIcon className="w-4 h-4 inline mr-2" />
                {selectedDate?.toLocaleDateString("en-IN", {
                  weekday: "long",
                  month: "long",
                  day: "numeric",
                })}
              </p>
              <p className="mt-1">
                <Clock className="w-4 h-4 inline mr-2" />
                {selectedSlot && formatTime(selectedSlot.startTime)} -{" "}
                {selectedSlot && formatTime(selectedSlot.endTime)}
              </p>
            </div>

            <div className="space-y-2">
              <Label>Consultation Type</Label>
              <RadioGroup
                value={appointmentType}
                onValueChange={(v) => setAppointmentType(v as "ONLINE" | "OFFLINE")}
                className="flex gap-4"
              >
                <div className="flex items-center space-x-2">
                  <RadioGroupItem value="OFFLINE" id="offline" />
                  <Label htmlFor="offline" className="flex items-center gap-1">
                    <MapPin className="w-4 h-4" /> In-Person
                  </Label>
                </div>
                <div className="flex items-center space-x-2">
                  <RadioGroupItem value="ONLINE" id="online" />
                  <Label htmlFor="online" className="flex items-center gap-1">
                    <Video className="w-4 h-4" /> Online
                  </Label>
                </div>
              </RadioGroup>
            </div>

            <div className="space-y-2">
              <Label htmlFor="symptoms">Describe your symptoms (optional)</Label>
              <Textarea
                id="symptoms"
                placeholder="Brief description of your symptoms or reason for visit..."
                value={symptoms}
                onChange={(e) => setSymptoms(e.target.value)}
                rows={3}
              />
            </div>

            <Button
              onClick={() => setStep("confirm")}
              className="w-full bg-teal-600 hover:bg-teal-700"
            >
              Continue to Confirmation
            </Button>
          </div>
        )}

        {/* Step 4: Confirm & Book */}
        {step === "confirm" && (
          <div className="space-y-4">
            <Label className="text-base font-medium">Confirm Booking</Label>

            <div className="bg-gray-50 p-4 rounded-md space-y-3">
              <div className="flex justify-between">
                <span className="text-gray-600">Doctor</span>
                <span className="font-medium">{doctor.profile?.name}</span>
              </div>
              <div className="flex justify-between">
                <span className="text-gray-600">Specialization</span>
                <span>{doctor.specializations || "General"}</span>
              </div>
              <div className="flex justify-between">
                <span className="text-gray-600">Date</span>
                <span>
                  {selectedDate?.toLocaleDateString("en-IN", {
                    month: "short",
                    day: "numeric",
                    year: "numeric",
                  })}
                </span>
              </div>
              <div className="flex justify-between">
                <span className="text-gray-600">Time</span>
                <span>
                  {selectedSlot && formatTime(selectedSlot.startTime)}
                </span>
              </div>
              <div className="flex justify-between">
                <span className="text-gray-600">Type</span>
                <span className="flex items-center gap-1">
                  {appointmentType === "ONLINE" ? (
                    <>
                      <Video className="w-4 h-4" /> Online
                    </>
                  ) : (
                    <>
                      <MapPin className="w-4 h-4" /> In-Person
                    </>
                  )}
                </span>
              </div>
              <div className="border-t pt-3 flex justify-between font-medium">
                <span>Consultation Fee</span>
                <span className="text-teal-600">
                  Rs. 500
                </span>
              </div>
            </div>

            {bookingMutation.isError && (
              <div className="bg-red-50 text-red-600 p-3 rounded-md text-sm">
                Failed to book appointment. Please try again.
              </div>
            )}

            <div className="flex gap-3">
              <Button
                variant="outline"
                onClick={() => setStep("details")}
                className="flex-1"
              >
                Back
              </Button>
              <Button
                onClick={handleSubmit}
                disabled={bookingMutation.isPending}
                className="flex-1 bg-teal-600 hover:bg-teal-700"
              >
                {bookingMutation.isPending ? (
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
        )}
      </DialogContent>
    </Dialog>
  );
}
