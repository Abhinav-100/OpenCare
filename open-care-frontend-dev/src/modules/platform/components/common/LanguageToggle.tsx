"use client";

import { Globe } from "lucide-react";
import { useLanguage } from "@/shared/constants/translations";
import { Button } from "@/modules/platform/components/ui/button";

export function LanguageToggle() {
  const { t } = useLanguage();

  return (
    <Button variant="ghost" size="sm" className="gap-2" disabled>
      <Globe className="h-4 w-4" />
      <span className="hidden sm:inline">{t.language.english}</span>
    </Button>
  );
}

// Simple toggle button version (alternative)
export function LanguageToggleSimple() {
  const { t } = useLanguage();

  return (
    <Button variant="outline" size="sm" className="gap-2 font-medium" disabled>
      <Globe className="h-4 w-4" />
      {t.language.english}
    </Button>
  );
}
