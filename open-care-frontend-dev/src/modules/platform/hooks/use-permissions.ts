"use client";

import { useCallback, useEffect, useState } from "react";
import { getUserSession } from "@/shared/utils/auth-client";
import { getPermissionsFromToken, hasPermission } from "@/shared/utils/permissions";

export const usePermissions = () => {
  const [permissions, setPermissions] = useState<string[]>([]);

  useEffect(() => {
    const token = getUserSession()?.access_token;
    setPermissions(getPermissionsFromToken(token));
  }, []);

  const can = useCallback(
    (permission: string) => hasPermission(permission, permissions),
    [permissions]
  );

  return { permissions, hasPermission: can };
};
