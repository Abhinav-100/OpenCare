"use client";

import { useEffect, useState, useTransition } from "react";
import {
  Settings,
  Home,
  LogOut,
  Bell,
  Stethoscope,
  Building2,
} from "lucide-react";
import Link from "next/link";
import { Avatar, AvatarFallback, AvatarImage } from "@/modules/platform/components/ui/avatar";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuLabel,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from "@/modules/platform/components/ui/dropdown-menu";
import {
  Sidebar,
  SidebarContent,
  SidebarFooter,
  SidebarGroup,
  SidebarGroupContent,
  SidebarGroupLabel,
  SidebarHeader,
  SidebarMenu,
  SidebarMenuButton,
  SidebarMenuItem,
} from "@/modules/platform/components/ui/sidebar";
import { getUserSession, clearUserSession } from "@/shared/utils/auth-client";
import { logout } from "@/app/logout/actions";

function getTokenInfo(token?: string): { username: string; email: string } {
  if (!token) return { username: "Admin", email: "" };
  try {
    const payload = JSON.parse(
      atob(token.split(".")[1].replace(/-/g, "+").replace(/_/g, "/"))
    );
    return {
      username:
        payload.preferred_username ?? payload.name ?? payload.sub ?? "Admin",
      email: payload.email ?? "",
    };
  } catch {
    return { username: "Admin", email: "" };
  }
}

// Menu items
const menuItems = [
  {
    title: "Dashboard",
    url: "/admin",
    icon: Home,
  },
  {
    title: "Doctors",
    url: "/admin/doctors",
    icon: Stethoscope,
  },
  {
    title: "Hospitals",
    url: "/admin/hospitals",
    icon: Building2,
  },
];

export function AdminSidebar() {
  const [isPending, startTransition] = useTransition();
  const [userInfo, setUserInfo] = useState<{ username: string; email: string }>({
    username: "Admin",
    email: "",
  });

  useEffect(() => {
    const session = getUserSession();
    setUserInfo(getTokenInfo(session?.access_token));
  }, []);

  const { username, email } = userInfo;
  const initials = username.slice(0, 2).toUpperCase();

  const handleLogout = () => {
    clearUserSession();
    startTransition(() => {
      logout();
    });
  };

  return (
    <Sidebar variant="inset">
      <SidebarHeader>
        <div className="flex items-center gap-2 px-4 py-2">
          <div className="flex aspect-square size-8 items-center justify-center rounded-lg bg-sidebar-primary text-sidebar-primary-foreground">
            <Home className="size-4" />
          </div>
          <div className="grid flex-1 text-left text-sm leading-tight">
            <span className="truncate font-semibold">OpenCare Admin</span>
            <span className="truncate text-xs">Healthcare Management</span>
          </div>
        </div>
      </SidebarHeader>

      <SidebarContent>
        <SidebarGroup>
          <SidebarGroupLabel>Main Menu</SidebarGroupLabel>
          <SidebarGroupContent>
            <SidebarMenu>
              {menuItems.map((item) => (
                <SidebarMenuItem key={item.title}>
                  <Link href={item.url}>
                    <SidebarMenuButton>
                      <item.icon />
                      <span>{item.title}</span>
                    </SidebarMenuButton>
                  </Link>
                </SidebarMenuItem>
              ))}
            </SidebarMenu>
          </SidebarGroupContent>
        </SidebarGroup>
      </SidebarContent>

      <SidebarFooter>
        <SidebarMenu>
          <SidebarMenuItem>
            <DropdownMenu>
              <DropdownMenuTrigger asChild>
                <SidebarMenuButton
                  size="lg"
                  className="data-[state=open]:bg-sidebar-accent data-[state=open]:text-sidebar-accent-foreground"
                >
                  <Avatar className="h-8 w-8 rounded-lg">
                    <AvatarImage src="/avatars/01.png" alt={username} />
                    <AvatarFallback className="rounded-lg" suppressHydrationWarning>
                      {initials}
                    </AvatarFallback>
                  </Avatar>
                  <div className="grid flex-1 text-left text-sm leading-tight">
                    <span className="truncate font-semibold">{username}</span>
                    <span className="truncate text-xs">{email}</span>
                  </div>
                </SidebarMenuButton>
              </DropdownMenuTrigger>
              <DropdownMenuContent
                className="w-[--radix-dropdown-menu-trigger-width] min-w-56 rounded-lg"
                side="bottom"
                align="end"
                sideOffset={4}
              >
                <DropdownMenuLabel className="p-0 font-normal">
                  <div className="flex items-center gap-2 px-1 py-1.5 text-left text-sm">
                    <Avatar className="h-8 w-8 rounded-lg">
                      <AvatarImage src="/avatars/01.png" alt={username} />
                      <AvatarFallback className="rounded-lg" suppressHydrationWarning>
                        {initials}
                      </AvatarFallback>
                    </Avatar>
                    <div className="grid flex-1 text-left text-sm leading-tight">
                      <span className="truncate font-semibold">{username}</span>
                      <span className="truncate text-xs">{email}</span>
                    </div>
                  </div>
                </DropdownMenuLabel>
                <DropdownMenuSeparator />
                <DropdownMenuItem>
                  <Settings />
                  Account Settings
                </DropdownMenuItem>
                <DropdownMenuItem>
                  <Bell />
                  Notifications
                </DropdownMenuItem>
                <DropdownMenuSeparator />
                <DropdownMenuItem onClick={handleLogout} disabled={isPending}>
                  <LogOut />
                  {isPending ? "Logging out..." : "Log out"}
                </DropdownMenuItem>
              </DropdownMenuContent>
            </DropdownMenu>
          </SidebarMenuItem>
        </SidebarMenu>
      </SidebarFooter>
    </Sidebar>
  );
}
