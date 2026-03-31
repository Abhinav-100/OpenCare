"use client";

import { useState } from "react";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
} from "@/modules/platform/components/ui/dialog";
import { Button } from "@/modules/platform/components/ui/button";
import { Input } from "@/modules/platform/components/ui/input";
import { Label } from "@/modules/platform/components/ui/label";
import { Textarea } from "@/modules/platform/components/ui/textarea";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/modules/platform/components/ui/select";
import { createEncounter } from "@/modules/clinical/api/health-records";
import { CreateEncounterRequest, EncounterType } from "@/shared/types/health-records";
import { Checkbox } from "@/modules/platform/components/ui/checkbox";

interface AddEncounterModalProps {
  open: boolean;
  onClose: () => void;
}

export default function AddEncounterModal({
  open,
  onClose,
}: AddEncounterModalProps) {
  const queryClient = useQueryClient();
  const [formData, setFormData] = useState<CreateEncounterRequest>({
    encounterType: "OPD",
    visitDate: new Date().toISOString().split("T")[0],
    location: "",
    diagnosisSummary: "",
    procedures: "",
    followUpRequired: false,
    followUpDate: "",
    notes: "",
  });

  const mutation = useMutation({
    mutationFn: createEncounter,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["health-encounters"] });
      onClose();
      setFormData({
        encounterType: "OPD",
        visitDate: new Date().toISOString().split("T")[0],
        location: "",
        diagnosisSummary: "",
        procedures: "",
        followUpRequired: false,
        followUpDate: "",
        notes: "",
      });
    },
  });

  const errorMessage =
    mutation.error instanceof Error
      ? mutation.error.message
      : "Failed to save visit. Please try again.";

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    mutation.mutate(formData);
  };

  const encounterTypes: { value: EncounterType; label: string }[] = [
    { value: "OPD", label: "Outpatient (OPD)" },
    { value: "IPD", label: "Inpatient (IPD)" },
    { value: "EMERGENCY", label: "Emergency" },
    { value: "TELECONSULT", label: "Teleconsultation" },
    { value: "LAB_VISIT", label: "Lab Visit" },
    { value: "FOLLOW_UP", label: "Follow-up" },
  ];

  return (
    <Dialog open={open} onOpenChange={onClose}>
      <DialogContent className="max-w-lg max-h-[90vh] overflow-y-auto">
        <DialogHeader>
          <DialogTitle>Add Doctor Visit</DialogTitle>
        </DialogHeader>

        <form onSubmit={handleSubmit} className="space-y-4">
          <div className="grid grid-cols-2 gap-4">
            <div>
              <Label htmlFor="encounterType">Visit Type *</Label>
              <Select
                value={formData.encounterType}
                onValueChange={(v) =>
                  setFormData({ ...formData, encounterType: v as EncounterType })
                }
              >
                <SelectTrigger>
                  <SelectValue />
                </SelectTrigger>
                <SelectContent>
                  {encounterTypes.map((type) => (
                    <SelectItem key={type.value} value={type.value}>
                      {type.label}
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
            </div>

            <div>
              <Label htmlFor="visitDate">Visit Date *</Label>
              <Input
                id="visitDate"
                type="date"
                value={formData.visitDate}
                onChange={(e) =>
                  setFormData({ ...formData, visitDate: e.target.value })
                }
                required
              />
            </div>
          </div>

          <div>
            <Label htmlFor="location">Location / Hospital</Label>
            <Input
              id="location"
              placeholder="e.g., AIIMS Bhubaneswar"
              value={formData.location}
              onChange={(e) =>
                setFormData({ ...formData, location: e.target.value })
              }
            />
          </div>

          <div>
            <Label htmlFor="diagnosisSummary">Diagnosis Summary</Label>
            <Textarea
              id="diagnosisSummary"
              placeholder="Brief description of diagnosis..."
              value={formData.diagnosisSummary}
              onChange={(e) =>
                setFormData({ ...formData, diagnosisSummary: e.target.value })
              }
              rows={2}
            />
          </div>

          <div>
            <Label htmlFor="procedures">Procedures / Tests Done</Label>
            <Input
              id="procedures"
              placeholder="e.g., Blood test, X-ray, ECG"
              value={formData.procedures}
              onChange={(e) =>
                setFormData({ ...formData, procedures: e.target.value })
              }
            />
          </div>

          <div className="flex items-center space-x-2">
            <Checkbox
              id="followUpRequired"
              checked={formData.followUpRequired}
              onCheckedChange={(checked) =>
                setFormData({ ...formData, followUpRequired: !!checked })
              }
            />
            <Label htmlFor="followUpRequired">Follow-up Required</Label>
          </div>

          {formData.followUpRequired && (
            <div>
              <Label htmlFor="followUpDate">Follow-up Date</Label>
              <Input
                id="followUpDate"
                type="date"
                value={formData.followUpDate}
                onChange={(e) =>
                  setFormData({ ...formData, followUpDate: e.target.value })
                }
              />
            </div>
          )}

          <div>
            <Label htmlFor="notes">Additional Notes</Label>
            <Textarea
              id="notes"
              placeholder="Any other important information..."
              value={formData.notes}
              onChange={(e) =>
                setFormData({ ...formData, notes: e.target.value })
              }
              rows={2}
            />
          </div>

          {mutation.isError && (
            <p className="text-sm text-red-600">{errorMessage}</p>
          )}

          <div className="flex justify-end gap-2 pt-4">
            <Button type="button" variant="outline" onClick={onClose}>
              Cancel
            </Button>
            <Button type="submit" disabled={mutation.isPending}>
              {mutation.isPending ? "Saving..." : "Save Visit"}
            </Button>
          </div>
        </form>
      </DialogContent>
    </Dialog>
  );
}
