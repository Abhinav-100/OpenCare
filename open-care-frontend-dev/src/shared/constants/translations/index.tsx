"use client";

import React, { createContext, useContext, ReactNode } from "react";
import { en, Translations } from "./en";

type Language = "en" | "or";

interface LanguageContextType {
  language: Language;
  setLanguage: (lang: Language) => void;
  t: Translations;
  toggleLanguage: () => void;
}

const LanguageContext = createContext<LanguageContextType | undefined>(undefined);

interface LanguageProviderProps {
  children: ReactNode;
}

export function LanguageProvider({ children }: LanguageProviderProps) {
  const setLanguage = (lang: Language) => {
    void lang;
    // English-only mode: ignore language changes.
  };

  const toggleLanguage = () => {
    // English-only mode: disable toggling.
  };

  return (
    <LanguageContext.Provider
      value={{
        language: "en",
        setLanguage,
        t: en,
        toggleLanguage,
      }}
    >
      {children}
    </LanguageContext.Provider>
  );
}

export function useLanguage() {
  const context = useContext(LanguageContext);
  if (context === undefined) {
    throw new Error("useLanguage must be used within a LanguageProvider");
  }
  return context;
}

// Export types for external use
export type { Language, Translations };
