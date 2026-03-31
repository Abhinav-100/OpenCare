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
import { Checkbox } from "@/modules/platform/components/ui/checkbox";
import { createMedication } from "@/modules/clinical/api/health-records";
import { CreateMedicationRequest } from "@/shared/types/health-records";

interface AddMedicationModalProps {
  open: boolean;
  onClose: () => void;
}

export default function AddMedicationModal({
  open,
  onClose,
}: AddMedicationModalProps) {
  const queryClient = useQueryClient();
  const [formData, setFormData] = useState<CreateMedicationRequest>({
    medicationName: "",
    dosageAmount: "",
    dosageUnit: "mg",
    frequency: "",
    startDate: new Date().toISOString().split("T")[0],
    endDate: "",
    prescriptionNotes: "",
    isActive: true,
  });

  const mutation = useMutation({
    mutationFn: createMedication,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["health-medications"] });
      onClose();
      setFormData({
        medicationName: "",
        dosageAmount: "",
        dosageUnit: "mg",
        frequency: "",
        startDate: new Date().toISOString().split("T")[0],
        endDate: "",
        prescriptionNotes: "",
        isActive: true,
      });
    },
  });

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    mutation.mutate(formData);
  };

  const dosageUnits = ["mg", "ml", "mcg", "g", "IU", "tablet", "capsule", "drops"];

  const frequencies = [
    "Once daily",
    "Twice daily",
    "Three times daily",
    "Four times daily",
    "Every 4 hours",
    "Every 6 hours",
    "Every 8 hours",
    "Every 12 hours",
    "Once weekly",
    "As needed",
    "Before meals",
    "After meals",
    "At bedtime",
  ];

  return (
    <Dialog open={open} onOpenChange={onClose}>
      <DialogContent className="max-w-lg max-h-[90vh] overflow-y-auto">
        <DialogHeader>
          <DialogTitle>Add Medication</DialogTitle>
        </DialogHeader>

        <form onSubmit={handleSubmit} className="space-y-4">
          <div>
            <Label htmlFor="medicationName">Medication Name *</Label>
            <Input
              id="medicationName"
              placeholder="e.g., Metformin, Amlodipine, Paracetamol"
              value={formData.medicationName}
              onChange={(e) =>
                setFormData({ ...formData, medicationName: e.target.value })
              }
              required
            />
          </div>

          <div className="grid grid-cols-2 gap-4">
            <div>
              <Label htmlFor="dosageAmount">Dosage Amount</Label>
              <Input
                id="dosageAmount"
                placeholder="e.g., 500"
                value={formData.dosageAmount}
                onChange={(e) =>
                  setFormData({ ...formData, dosageAmount: e.target.value })
                }
              />
            </div>

            <div>
              <Label htmlFor="dosageUnit">Unit</Label>
              <Select
                value={formData.dosageUnit}
                onValueChange={(v) =>
                  setFormData({ ...formData, dosageUnit: v })
                }
              >
                <SelectTrigger>
                  <SelectValue />
                </SelectTrigger>
                <SelectContent>
                  {dosageUnits.map((unit) => (
                    <SelectItem key={unit} value={unit}>
                      {unit}
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
            </div>
          </div>

          <div>
            <Label htmlFor="frequency">Frequency</Label>
            <Select
              value={formData.frequency}
              onValueChange={(v) => setFormData({ ...formData, frequency: v })}
            >
              <SelectTrigger>
                <SelectValue placeholder="Select frequency..." />
              </SelectTrigger>
              <SelectContent>
                {frequencies.map((freq) => (
                  <SelectItem key={freq} value={freq}>
                    {freq}
                  </SelectItem>
                ))}
              </SelectContent>
            </Select>
          </div>

          <div className="grid grid-cols-2 gap-4">
            <div>
              <Label htmlFor="startDate">Start Date *</Label>
              <Input
                id="startDate"
                type="date"
                value={formData.startDate}
                onChange={(e) =>
                  setFormData({ ...formData, startDate: e.target.value })
                }
                required
              />
            </div>

            <div>
              <Label htmlFor="endDate">End Date</Label>
              <Input
                id="endDate"
                type="date"
                value={formData.endDate}
                onChange={(e) =>
                  setFormData({ ...formData, endDate: e.target.value })
                }
              />
            </div>
          </div>

          <div className="flex items-center space-x-2">
            <Checkbox
              id="isActive"
              checked={formData.isActive}
              onCheckedChange={(checked) =>
                setFormData({ ...formData, isActive: !!checked })
              }
            />
            <Label htmlFor="isActive">Currently taking this medication</Label>
          </div>

          <div>
            <Label htmlFor="rxnormCode">RxNorm Code (optional)</Label>
            <Input
              id="rxnormCode"
              placeholder="e.g., 860975"
              value={formData.rxnormCode || ""}
              onChange={(e) =>
                setFormData({ ...formData, rxnormCode: e.target.value })
              }
            />
          </div>

          <div>
            <Label htmlFor="prescriptionNotes">Prescription Notes</Label>
            <Textarea
              id="prescriptionNotes"
              placeholder="e.g., Take with food, avoid alcohol..."
              value={formData.prescriptionNotes}
              onChange={(e) =>
                setFormData({ ...formData, prescriptionNotes: e.target.value })
              }
              rows={2}
            />
          </div>

          <div className="flex justify-end gap-2 pt-4">
            <Button type="button" variant="outline" onClick={onClose}>
              Cancel
            </Button>
            <Button type="submit" disabled={mutation.isPending}>
              {mutation.isPending ? "Saving..." : "Save Medication"}
            </Button>
          </div>
        </form>
      </DialogContent>
    </Dialog>
  );
}
