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
import { createCondition } from "@/modules/clinical/api/health-records";
import {
  CreateConditionRequest,
  ConditionType,
  ConditionSeverity,
  ConditionStatus,
} from "@/shared/types/health-records";

interface AddConditionModalProps {
  open: boolean;
  onClose: () => void;
}

export default function AddConditionModal({
  open,
  onClose,
}: AddConditionModalProps) {
  const queryClient = useQueryClient();
  const [formData, setFormData] = useState<CreateConditionRequest>({
    conditionType: "DIAGNOSIS",
    name: "",
    severity: undefined,
    reaction: "",
    diagnosisDate: "",
    status: "ACTIVE",
    notes: "",
  });

  const mutation = useMutation({
    mutationFn: createCondition,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["health-conditions"] });
      onClose();
      setFormData({
        conditionType: "DIAGNOSIS",
        name: "",
        severity: undefined,
        reaction: "",
        diagnosisDate: "",
        status: "ACTIVE",
        notes: "",
      });
    },
  });

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    mutation.mutate(formData);
  };

  const conditionTypes: { value: ConditionType; label: string }[] = [
    { value: "ALLERGY", label: "Allergy" },
    { value: "DIAGNOSIS", label: "Diagnosis" },
    { value: "CHRONIC", label: "Chronic Condition" },
  ];

  const severityOptions: { value: ConditionSeverity; label: string }[] = [
    { value: "MILD", label: "Mild" },
    { value: "MODERATE", label: "Moderate" },
    { value: "SEVERE", label: "Severe" },
  ];

  const statusOptions: { value: ConditionStatus; label: string }[] = [
    { value: "ACTIVE", label: "Active" },
    { value: "MANAGED", label: "Managed" },
    { value: "RESOLVED", label: "Resolved" },
  ];

  return (
    <Dialog open={open} onOpenChange={onClose}>
      <DialogContent className="max-w-lg max-h-[90vh] overflow-y-auto">
        <DialogHeader>
          <DialogTitle>Add Condition</DialogTitle>
        </DialogHeader>

        <form onSubmit={handleSubmit} className="space-y-4">
          <div className="grid grid-cols-2 gap-4">
            <div>
              <Label htmlFor="conditionType">Type *</Label>
              <Select
                value={formData.conditionType}
                onValueChange={(v) =>
                  setFormData({
                    ...formData,
                    conditionType: v as ConditionType,
                  })
                }
              >
                <SelectTrigger>
                  <SelectValue />
                </SelectTrigger>
                <SelectContent>
                  {conditionTypes.map((type) => (
                    <SelectItem key={type.value} value={type.value}>
                      {type.label}
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
            </div>

            <div>
              <Label htmlFor="status">Status</Label>
              <Select
                value={formData.status}
                onValueChange={(v) =>
                  setFormData({ ...formData, status: v as ConditionStatus })
                }
              >
                <SelectTrigger>
                  <SelectValue />
                </SelectTrigger>
                <SelectContent>
                  {statusOptions.map((opt) => (
                    <SelectItem key={opt.value} value={opt.value}>
                      {opt.label}
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
            </div>
          </div>

          <div>
            <Label htmlFor="name">
              {formData.conditionType === "ALLERGY"
                ? "Allergen *"
                : "Condition Name *"}
            </Label>
            <Input
              id="name"
              placeholder={
                formData.conditionType === "ALLERGY"
                  ? "e.g., Penicillin, Peanuts, Dust"
                  : "e.g., Diabetes Type 2, Hypertension"
              }
              value={formData.name}
              onChange={(e) =>
                setFormData({ ...formData, name: e.target.value })
              }
              required
            />
          </div>

          <div className="grid grid-cols-2 gap-4">
            <div>
              <Label htmlFor="severity">Severity</Label>
              <Select
                value={formData.severity}
                onValueChange={(v) =>
                  setFormData({
                    ...formData,
                    severity: v as ConditionSeverity,
                  })
                }
              >
                <SelectTrigger>
                  <SelectValue placeholder="Select..." />
                </SelectTrigger>
                <SelectContent>
                  {severityOptions.map((opt) => (
                    <SelectItem key={opt.value} value={opt.value}>
                      {opt.label}
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
            </div>

            <div>
              <Label htmlFor="diagnosisDate">Diagnosis Date</Label>
              <Input
                id="diagnosisDate"
                type="date"
                value={formData.diagnosisDate}
                onChange={(e) =>
                  setFormData({ ...formData, diagnosisDate: e.target.value })
                }
              />
            </div>
          </div>

          {formData.conditionType === "ALLERGY" && (
            <div>
              <Label htmlFor="reaction">Reaction</Label>
              <Input
                id="reaction"
                placeholder="e.g., Rash, Swelling, Anaphylaxis"
                value={formData.reaction}
                onChange={(e) =>
                  setFormData({ ...formData, reaction: e.target.value })
                }
              />
            </div>
          )}

          <div>
            <Label htmlFor="icdCode">ICD Code (optional)</Label>
            <Input
              id="icdCode"
              placeholder="e.g., E11.9"
              value={formData.icdCode || ""}
              onChange={(e) =>
                setFormData({ ...formData, icdCode: e.target.value })
              }
            />
          </div>

          <div>
            <Label htmlFor="notes">Notes</Label>
            <Textarea
              id="notes"
              placeholder="Any additional information..."
              value={formData.notes}
              onChange={(e) =>
                setFormData({ ...formData, notes: e.target.value })
              }
              rows={2}
            />
          </div>

          <div className="flex justify-end gap-2 pt-4">
            <Button type="button" variant="outline" onClick={onClose}>
              Cancel
            </Button>
            <Button type="submit" disabled={mutation.isPending}>
              {mutation.isPending ? "Saving..." : "Save Condition"}
            </Button>
          </div>
        </form>
      </DialogContent>
    </Dialog>
  );
}
