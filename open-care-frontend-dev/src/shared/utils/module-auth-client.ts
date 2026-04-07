import { ModuleAuthResponse } from "@/shared/types/modules";

const MODULE_SESSION_KEY = "module_user_session";

// Auth flow: This file handles session/token actions used by login/logout/refresh flows.
export function getModuleSession(): ModuleAuthResponse | null {
  if (typeof window === "undefined") return null;

  try {
    const raw = localStorage.getItem(MODULE_SESSION_KEY);
    return raw ? (JSON.parse(raw) as ModuleAuthResponse) : null;
  } catch {
    return null;
  }
}

export function saveModuleSession(session: ModuleAuthResponse): void {
  if (typeof window === "undefined") return;
  localStorage.setItem(MODULE_SESSION_KEY, JSON.stringify(session));
}

export function clearModuleSession(): void {
  if (typeof window === "undefined") return;
  localStorage.removeItem(MODULE_SESSION_KEY);
}

export function getModuleAuthHeader(): { Authorization?: string } {
  const session = getModuleSession();
  if (!session?.accessToken || !session?.tokenType) {
    return {};
  }

  return {
    Authorization: `${session.tokenType} ${session.accessToken}`,
  };
}
