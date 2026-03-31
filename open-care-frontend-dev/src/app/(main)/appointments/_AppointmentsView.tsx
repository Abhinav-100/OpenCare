"use client";

import { useState } from "react";
import { useQuery } from "@tanstack/react-query";
import { Calendar, Clock, User, Video, MapPin } from "lucide-react";
import Link from "next/link";
import { useAuth } from "@/modules/access/context/auth-context";
import { fetchMyAppointments } from "@/modules/clinical/api/appointments";
import { Card, CardContent } from "@/modules/platform/components/ui/card";
import { Button } from "@/modules/platform/components/ui/button";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/modules/platform/components/ui/tabs";
import type { Appointment } from "@/shared/types/appointments";

export default function AppointmentsView() {
  const { isAuthenticated, isLoading: isAuthLoading } = useAuth();
  const [activeTab, setActiveTab] = useState("upcoming");

  const {
    data: appointments = [],
    isLoading,
    error,
    isError,
  } = useQuery({
    queryKey: ["my-appointments"],
    queryFn: fetchMyAppointments,
    enabled: isAuthenticated,
    retry: false, // Don't retry on failure
  });

  if (isAuthLoading) {
    return (
      <div className="flex items-center justify-center min-h-[60vh]">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary"></div>
      </div>
    );
  }

  if (!isAuthenticated) {
    return (
      <div className="min-h-screen bg-gray-50 py-12">
        <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 text-center">
          <h1 className="text-2xl font-bold text-gray-900 mb-4">
            Please Sign In
          </h1>
          <p className="text-gray-600 mb-6">
            You need to be signed in to view your appointments.
          </p>
          <Link href="/login">
            <Button className="bg-teal-600 hover:bg-teal-700">Sign In</Button>
          </Link>
        </div>
      </div>
    );
  }

  const today = new Date();
  today.setHours(0, 0, 0, 0);

  const upcomingAppointments = appointments.filter((apt) => {
    const aptDate = new Date(apt.appointmentDate);
    return (
      aptDate >= today &&
      apt.status.value !== "CANCELLED" &&
      apt.status.value !== "COMPLETED"
    );
  });

  const pastAppointments = appointments.filter((apt) => {
    const aptDate = new Date(apt.appointmentDate);
    return (
      aptDate < today ||
      apt.status.value === "COMPLETED" ||
      apt.status.value === "CANCELLED"
    );
  });

  return (
    <div className="min-h-screen bg-gray-50 py-8">
      <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8">
        {/* Header */}
        <div className="flex justify-between items-center mb-8">
          <div>
            <h1 className="text-3xl font-bold text-gray-900">My Appointments</h1>
            <p className="text-gray-600 mt-1">
              Manage your upcoming and past appointments
            </p>
          </div>
          <Link href="/doctors">
            <Button className="bg-teal-600 hover:bg-teal-700">
              Book New Appointment
            </Button>
          </Link>
        </div>

        {isLoading ? (
          <div className="flex items-center justify-center py-12">
            <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-teal-600"></div>
          </div>
        ) : error || isError ? (
          <Card>
            <CardContent className="py-12 text-center">
              <p className="text-red-600 mb-2">
                Failed to load appointments.
              </p>
              <p className="text-sm text-gray-500">
                {error instanceof Error ? error.message : "Please try again later."}
              </p>
            </CardContent>
          </Card>
        ) : (
          <Tabs value={activeTab} onValueChange={setActiveTab}>
            <TabsList className="mb-6">
              <TabsTrigger value="upcoming">
                Upcoming ({upcomingAppointments.length})
              </TabsTrigger>
              <TabsTrigger value="past">
                Past ({pastAppointments.length})
              </TabsTrigger>
            </TabsList>

            <TabsContent value="upcoming">
              {upcomingAppointments.length === 0 ? (
                <Card>
                  <CardContent className="py-12 text-center">
                    <Calendar className="w-12 h-12 text-gray-400 mx-auto mb-4" />
                    <h3 className="text-lg font-medium text-gray-900 mb-2">
                      No upcoming appointments
                    </h3>
                    <p className="text-gray-500 mb-4">
                      Book an appointment with a doctor to get started.
                    </p>
                    <Link href="/doctors">
                      <Button className="bg-teal-600 hover:bg-teal-700">
                        Find a Doctor
                      </Button>
                    </Link>
                  </CardContent>
                </Card>
              ) : (
                <div className="space-y-4">
                  {upcomingAppointments.map((appointment) => (
                    <AppointmentCard
                      key={appointment.id}
                      appointment={appointment}
                    />
                  ))}
                </div>
              )}
            </TabsContent>

            <TabsContent value="past">
              {pastAppointments.length === 0 ? (
                <Card>
                  <CardContent className="py-12 text-center">
                    <p className="text-gray-500">No past appointments</p>
                  </CardContent>
                </Card>
              ) : (
                <div className="space-y-4">
                  {pastAppointments.map((appointment) => (
                    <AppointmentCard
                      key={appointment.id}
                      appointment={appointment}
                    />
                  ))}
                </div>
              )}
            </TabsContent>
          </Tabs>
        )}
      </div>
    </div>
  );
}

function AppointmentCard({ appointment }: { appointment: Appointment }) {
  const statusColors: Record<string, string> = {
    PENDING: "bg-yellow-100 text-yellow-800",
    CONFIRMED: "bg-green-100 text-green-800",
    CANCELLED: "bg-red-100 text-red-800",
    COMPLETED: "bg-blue-100 text-blue-800",
    NO_SHOW: "bg-gray-100 text-gray-800",
  };

  const formatDate = (dateStr: string) => {
    return new Date(dateStr).toLocaleDateString("en-IN", {
      weekday: "long",
      year: "numeric",
      month: "long",
      day: "numeric",
    });
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

  return (
    <Card className="hover:shadow-md transition-shadow">
      <CardContent className="p-6">
        <div className="flex flex-col md:flex-row md:items-start md:justify-between gap-4">
          {/* Left side - Doctor info */}
          <div className="flex gap-4">
            <div className="w-16 h-16 rounded-full bg-teal-100 flex items-center justify-center flex-shrink-0">
              <User className="w-8 h-8 text-teal-600" />
            </div>
            <div>
              <h3 className="font-semibold text-lg text-gray-900">
                {appointment.doctor?.profile?.name || "Doctor"}
              </h3>
              <p className="text-gray-600 text-sm">
                {appointment.doctor?.specializations || "Specialist"}
              </p>
              <div className="flex items-center gap-2 mt-2 text-sm text-gray-500">
                {appointment.appointmentType.value === "ONLINE" ? (
                  <>
                    <Video className="w-4 h-4" />
                    <span>Online Consultation</span>
                  </>
                ) : (
                  <>
                    <MapPin className="w-4 h-4" />
                    <span>
                      {appointment.hospital?.name || "In-Person Visit"}
                    </span>
                  </>
                )}
              </div>
            </div>
          </div>

          {/* Right side - Date/Time and Status */}
          <div className="flex flex-col items-start md:items-end gap-2">
            <span
              className={`px-3 py-1 rounded-full text-xs font-medium ${
                statusColors[appointment.status.value] || "bg-gray-100"
              }`}
            >
              {appointment.status.displayName}
            </span>
            <div className="flex items-center gap-2 text-gray-600">
              <Calendar className="w-4 h-4" />
              <span className="text-sm">
                {formatDate(appointment.appointmentDate)}
              </span>
            </div>
            <div className="flex items-center gap-2 text-gray-600">
              <Clock className="w-4 h-4" />
              <span className="text-sm">
                {formatTime(appointment.startTime)} -{" "}
                {formatTime(appointment.endTime)}
              </span>
            </div>
          </div>
        </div>

        {/* Bottom section */}
        <div className="mt-4 pt-4 border-t flex flex-wrap items-center justify-between gap-4">
          <div className="text-sm text-gray-500">
            <span className="font-medium text-gray-700">
              Rs. {appointment.consultationFee}
            </span>
            <span className="mx-2">|</span>
            <span>#{appointment.appointmentNumber}</span>
          </div>

          <div className="flex gap-2">
            {appointment.status.value === "CONFIRMED" &&
              appointment.appointmentType.value === "ONLINE" &&
              appointment.meetingLink && (
                <a
                  href={appointment.meetingLink}
                  target="_blank"
                  rel="noopener noreferrer"
                >
                  <Button size="sm" className="bg-teal-600 hover:bg-teal-700">
                    <Video className="w-4 h-4 mr-1" />
                    Join Call
                  </Button>
                </a>
              )}
            <Link href={`/appointments/${appointment.id}`}>
              <Button variant="outline" size="sm">
                View Details
              </Button>
            </Link>
          </div>
        </div>
      </CardContent>
    </Card>
  );
}
