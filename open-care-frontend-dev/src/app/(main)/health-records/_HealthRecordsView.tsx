"use client";

import { useState } from "react";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import {
  Activity,
  Stethoscope,
  AlertCircle,
  Pill,
  Plus,
  Calendar,
  User,
  Clock,
  FileText,
  Trash2,
} from "lucide-react";
import { useAuth } from "@/modules/access/context/auth-context";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/modules/platform/components/ui/tabs";
import { Card, CardContent, CardHeader, CardTitle } from "@/modules/platform/components/ui/card";
import { Button } from "@/modules/platform/components/ui/button";
import { Badge } from "@/modules/platform/components/ui/badge";
import {
  fetchMyEncounters,
  fetchMyConditions,
  fetchMyMedications,
  deleteEncounter,
  deleteCondition,
  deleteMedication,
} from "@/modules/clinical/api/health-records";
import {
  HealthEncounter,
  HealthCondition,
  HealthMedication,
  HealthRecordsTab,
} from "@/shared/types/health-records";
import AddEncounterModal from "@/modules/clinical/components/health-records/AddEncounterModal";
import AddConditionModal from "@/modules/clinical/components/health-records/AddConditionModal";
import AddMedicationModal from "@/modules/clinical/components/health-records/AddMedicationModal";

export default function HealthRecordsView() {
  const { isAuthenticated, isLoading: authLoading } = useAuth();
  const [activeTab, setActiveTab] = useState<HealthRecordsTab>("encounters");
  const [showEncounterModal, setShowEncounterModal] = useState(false);
  const [showConditionModal, setShowConditionModal] = useState(false);
  const [showMedicationModal, setShowMedicationModal] = useState(false);
  const queryClient = useQueryClient();

  // Fetch encounters
  const {
    data: encounters = [],
    isLoading: encountersLoading,
  } = useQuery({
    queryKey: ["health-encounters"],
    queryFn: fetchMyEncounters,
    enabled: isAuthenticated,
  });

  // Fetch conditions
  const {
    data: conditions = [],
    isLoading: conditionsLoading,
  } = useQuery({
    queryKey: ["health-conditions"],
    queryFn: fetchMyConditions,
    enabled: isAuthenticated,
  });

  // Fetch medications
  const {
    data: medications = [],
    isLoading: medicationsLoading,
  } = useQuery({
    queryKey: ["health-medications"],
    queryFn: fetchMyMedications,
    enabled: isAuthenticated,
  });

  // Delete mutations
  const deleteEncounterMutation = useMutation({
    mutationFn: deleteEncounter,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["health-encounters"] });
    },
  });

  const deleteConditionMutation = useMutation({
    mutationFn: deleteCondition,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["health-conditions"] });
    },
  });

  const deleteMedicationMutation = useMutation({
    mutationFn: deleteMedication,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["health-medications"] });
    },
  });

  if (authLoading) {
    return (
      <div className="flex items-center justify-center min-h-[400px]">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary"></div>
      </div>
    );
  }

  if (!isAuthenticated) {
    return (
      <div className="container mx-auto px-4 py-12">
        <Card className="max-w-lg mx-auto">
          <CardContent className="pt-6 text-center">
            <AlertCircle className="h-12 w-12 mx-auto text-yellow-500 mb-4" />
            <h2 className="text-xl font-semibold mb-2">Login Required</h2>
            <p className="text-muted-foreground mb-4">
              Please log in to view and manage your health records.
            </p>
            <Button asChild>
              <a href="/login">Login</a>
            </Button>
          </CardContent>
        </Card>
      </div>
    );
  }

  return (
    <div className="container mx-auto px-4 py-8">
      <div className="mb-8">
        <h1 className="text-3xl font-bold mb-2">My Health Records</h1>
        <p className="text-muted-foreground">
          Track and manage your medical history, conditions, and medications
        </p>
      </div>

      <Tabs
        value={activeTab}
        onValueChange={(v) => setActiveTab(v as HealthRecordsTab)}
      >
        <TabsList className="grid w-full grid-cols-4 lg:w-auto lg:inline-flex mb-6">
          <TabsTrigger value="encounters" className="gap-2">
            <Stethoscope className="h-4 w-4 hidden sm:block" />
            Visits
          </TabsTrigger>
          <TabsTrigger value="conditions" className="gap-2">
            <AlertCircle className="h-4 w-4 hidden sm:block" />
            Conditions
          </TabsTrigger>
          <TabsTrigger value="medications" className="gap-2">
            <Pill className="h-4 w-4 hidden sm:block" />
            Medications
          </TabsTrigger>
          <TabsTrigger value="vitals" className="gap-2">
            <Activity className="h-4 w-4 hidden sm:block" />
            Vitals
          </TabsTrigger>
        </TabsList>

        {/* Encounters Tab */}
        <TabsContent value="encounters">
          <div className="flex justify-between items-center mb-4">
            <h2 className="text-xl font-semibold">Doctor Visits</h2>
            <Button onClick={() => setShowEncounterModal(true)}>
              <Plus className="h-4 w-4 mr-2" />
              Add Visit
            </Button>
          </div>

          {encountersLoading ? (
            <div className="text-center py-8">Loading...</div>
          ) : encounters.length === 0 ? (
            <Card>
              <CardContent className="py-12 text-center">
                <Stethoscope className="h-12 w-12 mx-auto text-muted-foreground mb-4" />
                <p className="text-muted-foreground">No visits recorded yet</p>
                <Button
                  variant="outline"
                  className="mt-4"
                  onClick={() => setShowEncounterModal(true)}
                >
                  Add Your First Visit
                </Button>
              </CardContent>
            </Card>
          ) : (
            <div className="space-y-4">
              {encounters.map((encounter: HealthEncounter) => (
                <EncounterCard
                  key={encounter.id}
                  encounter={encounter}
                  onDelete={() => deleteEncounterMutation.mutate(encounter.id)}
                />
              ))}
            </div>
          )}
        </TabsContent>

        {/* Conditions Tab */}
        <TabsContent value="conditions">
          <div className="flex justify-between items-center mb-4">
            <h2 className="text-xl font-semibold">
              Conditions, Allergies & Diagnoses
            </h2>
            <Button onClick={() => setShowConditionModal(true)}>
              <Plus className="h-4 w-4 mr-2" />
              Add Condition
            </Button>
          </div>

          {conditionsLoading ? (
            <div className="text-center py-8">Loading...</div>
          ) : conditions.length === 0 ? (
            <Card>
              <CardContent className="py-12 text-center">
                <AlertCircle className="h-12 w-12 mx-auto text-muted-foreground mb-4" />
                <p className="text-muted-foreground">
                  No conditions recorded yet
                </p>
                <Button
                  variant="outline"
                  className="mt-4"
                  onClick={() => setShowConditionModal(true)}
                >
                  Add Your First Condition
                </Button>
              </CardContent>
            </Card>
          ) : (
            <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
              {conditions.map((condition: HealthCondition) => (
                <ConditionCard
                  key={condition.id}
                  condition={condition}
                  onDelete={() => deleteConditionMutation.mutate(condition.id)}
                />
              ))}
            </div>
          )}
        </TabsContent>

        {/* Medications Tab */}
        <TabsContent value="medications">
          <div className="flex justify-between items-center mb-4">
            <h2 className="text-xl font-semibold">Medications</h2>
            <Button onClick={() => setShowMedicationModal(true)}>
              <Plus className="h-4 w-4 mr-2" />
              Add Medication
            </Button>
          </div>

          {medicationsLoading ? (
            <div className="text-center py-8">Loading...</div>
          ) : medications.length === 0 ? (
            <Card>
              <CardContent className="py-12 text-center">
                <Pill className="h-12 w-12 mx-auto text-muted-foreground mb-4" />
                <p className="text-muted-foreground">
                  No medications recorded yet
                </p>
                <Button
                  variant="outline"
                  className="mt-4"
                  onClick={() => setShowMedicationModal(true)}
                >
                  Add Your First Medication
                </Button>
              </CardContent>
            </Card>
          ) : (
            <div className="space-y-4">
              {medications.map((medication: HealthMedication) => (
                <MedicationCard
                  key={medication.id}
                  medication={medication}
                  onDelete={() => deleteMedicationMutation.mutate(medication.id)}
                />
              ))}
            </div>
          )}
        </TabsContent>

        {/* Vitals Tab */}
        <TabsContent value="vitals">
          <Card>
            <CardHeader>
              <CardTitle className="flex items-center gap-2">
                <Activity className="h-5 w-5" />
                Vitals Tracking
              </CardTitle>
            </CardHeader>
            <CardContent className="py-12 text-center">
              <Activity className="h-12 w-12 mx-auto text-muted-foreground mb-4" />
              <p className="text-muted-foreground">
                Vitals tracking coming soon. You will be able to log blood
                pressure, heart rate, weight, and more.
              </p>
            </CardContent>
          </Card>
        </TabsContent>
      </Tabs>

      {/* Modals */}
      <AddEncounterModal
        open={showEncounterModal}
        onClose={() => setShowEncounterModal(false)}
      />
      <AddConditionModal
        open={showConditionModal}
        onClose={() => setShowConditionModal(false)}
      />
      <AddMedicationModal
        open={showMedicationModal}
        onClose={() => setShowMedicationModal(false)}
      />
    </div>
  );
}

// ==================== CARD COMPONENTS ====================

function getEnumCode(value: unknown): string {
  if (typeof value === "string") {
    return value;
  }

  if (value && typeof value === "object") {
    const code = (value as { value?: unknown }).value;
    if (typeof code === "string") {
      return code;
    }
  }

  return "";
}

function getEnumLabel(value: unknown): string {
  if (typeof value === "string") {
    return value;
  }

  if (value && typeof value === "object") {
    const displayName = (value as { displayName?: unknown }).displayName;
    if (typeof displayName === "string" && displayName.trim() !== "") {
      return displayName;
    }

    const code = (value as { value?: unknown }).value;
    if (typeof code === "string") {
      return code;
    }
  }

  return "";
}

function EncounterCard({
  encounter,
  onDelete,
}: {
  encounter: HealthEncounter;
  onDelete: () => void;
}) {
  const encounterTypeLabel: Record<string, string> = {
    OPD: "Outpatient",
    IPD: "Inpatient",
    EMERGENCY: "Emergency",
    TELECONSULT: "Teleconsult",
    LAB_VISIT: "Lab Visit",
    FOLLOW_UP: "Follow-up",
  };

  const encounterTypeColor: Record<string, string> = {
    OPD: "bg-blue-100 text-blue-800",
    IPD: "bg-purple-100 text-purple-800",
    EMERGENCY: "bg-red-100 text-red-800",
    TELECONSULT: "bg-green-100 text-green-800",
    LAB_VISIT: "bg-yellow-100 text-yellow-800",
    FOLLOW_UP: "bg-gray-100 text-gray-800",
  };

  const encounterTypeCode = getEnumCode(encounter.encounterType);
  const encounterTypeText =
    encounterTypeLabel[encounterTypeCode] ||
    getEnumLabel(encounter.encounterType) ||
    "Visit";

  return (
    <Card>
      <CardContent className="pt-4">
        <div className="flex justify-between items-start">
          <div className="flex-1">
            <div className="flex items-center gap-2 mb-2">
              <Badge
                className={encounterTypeColor[encounterTypeCode] || ""}
              >
                {encounterTypeText}
              </Badge>
              {encounter.followUpRequired && (
                <Badge variant="outline" className="text-orange-600">
                  Follow-up needed
                </Badge>
              )}
            </div>

            <div className="flex items-center gap-4 text-sm text-muted-foreground mb-2">
              <span className="flex items-center gap-1">
                <Calendar className="h-4 w-4" />
                {new Date(encounter.visitDate).toLocaleDateString("en-IN", {
                  day: "numeric",
                  month: "short",
                  year: "numeric",
                })}
              </span>
              {encounter.location && (
                <span className="flex items-center gap-1">
                  <FileText className="h-4 w-4" />
                  {encounter.location}
                </span>
              )}
            </div>

            {encounter.doctor && (
              <p className="flex items-center gap-1 text-sm mb-2">
                <User className="h-4 w-4" />
                Dr. {encounter.doctor.name}
              </p>
            )}

            {encounter.diagnosisSummary && (
              <p className="text-sm mt-2">{encounter.diagnosisSummary}</p>
            )}

            {encounter.followUpDate && (
              <p className="text-sm text-orange-600 mt-2 flex items-center gap-1">
                <Clock className="h-4 w-4" />
                Follow-up:{" "}
                {new Date(encounter.followUpDate).toLocaleDateString("en-IN")}
              </p>
            )}
          </div>

          <Button
            variant="ghost"
            size="icon"
            className="text-red-500 hover:text-red-700"
            onClick={onDelete}
          >
            <Trash2 className="h-4 w-4" />
          </Button>
        </div>
      </CardContent>
    </Card>
  );
}

function ConditionCard({
  condition,
  onDelete,
}: {
  condition: HealthCondition;
  onDelete: () => void;
}) {
  const conditionTypeColor: Record<string, string> = {
    ALLERGY: "bg-red-100 text-red-800",
    DIAGNOSIS: "bg-blue-100 text-blue-800",
    CHRONIC: "bg-purple-100 text-purple-800",
  };

  const severityColor: Record<string, string> = {
    MILD: "text-yellow-600",
    MODERATE: "text-orange-600",
    SEVERE: "text-red-600",
  };

  const conditionTypeCode = getEnumCode(condition.conditionType);
  const conditionTypeText = getEnumLabel(condition.conditionType) || "Condition";
  const conditionStatusText = getEnumLabel(condition.status);
  const severityCode = getEnumCode(condition.severity);
  const severityText = getEnumLabel(condition.severity);

  return (
    <Card>
      <CardContent className="pt-4">
        <div className="flex justify-between items-start">
          <div className="flex-1">
            <div className="flex items-center gap-2 mb-2">
              <Badge
                className={conditionTypeColor[conditionTypeCode] || ""}
              >
                {conditionTypeText}
              </Badge>
              {conditionStatusText && (
                <Badge variant="outline">{conditionStatusText}</Badge>
              )}
            </div>
            <h3 className="font-semibold">{condition.name}</h3>
            {severityText && (
              <p className={`text-sm ${severityColor[severityCode] || ""}`}>
                Severity: {severityText}
              </p>
            )}
            {condition.reaction && (
              <p className="text-sm text-muted-foreground mt-1">
                Reaction: {condition.reaction}
              </p>
            )}
            {condition.diagnosisDate && (
              <p className="text-sm text-muted-foreground mt-1">
                Diagnosed:{" "}
                {new Date(condition.diagnosisDate).toLocaleDateString("en-IN")}
              </p>
            )}
          </div>
          <Button
            variant="ghost"
            size="icon"
            className="text-red-500 hover:text-red-700"
            onClick={onDelete}
          >
            <Trash2 className="h-4 w-4" />
          </Button>
        </div>
      </CardContent>
    </Card>
  );
}

function MedicationCard({
  medication,
  onDelete,
}: {
  medication: HealthMedication;
  onDelete: () => void;
}) {
  return (
    <Card>
      <CardContent className="pt-4">
        <div className="flex justify-between items-start">
          <div className="flex-1">
            <div className="flex items-center gap-2 mb-2">
              <h3 className="font-semibold">{medication.medicationName}</h3>
              {medication.isActive ? (
                <Badge className="bg-green-100 text-green-800">Active</Badge>
              ) : (
                <Badge variant="outline">Stopped</Badge>
              )}
            </div>

            <div className="grid grid-cols-2 gap-2 text-sm text-muted-foreground">
              {medication.dosageAmount && (
                <p>
                  Dosage: {medication.dosageAmount} {medication.dosageUnit}
                </p>
              )}
              {medication.frequency && <p>Frequency: {medication.frequency}</p>}
              <p>
                Started:{" "}
                {new Date(medication.startDate).toLocaleDateString("en-IN")}
              </p>
              {medication.endDate && (
                <p>
                  Ended:{" "}
                  {new Date(medication.endDate).toLocaleDateString("en-IN")}
                </p>
              )}
            </div>

            {medication.prescribedBy && (
              <p className="text-sm mt-2 flex items-center gap-1">
                <User className="h-4 w-4" />
                Prescribed by: Dr. {medication.prescribedBy.name}
              </p>
            )}

            {medication.prescriptionNotes && (
              <p className="text-sm text-muted-foreground mt-2">
                {medication.prescriptionNotes}
              </p>
            )}
          </div>

          <Button
            variant="ghost"
            size="icon"
            className="text-red-500 hover:text-red-700"
            onClick={onDelete}
          >
            <Trash2 className="h-4 w-4" />
          </Button>
        </div>
      </CardContent>
    </Card>
  );
}
