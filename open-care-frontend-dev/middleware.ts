import { NextRequest, NextResponse } from "next/server";

const CORE_SCOPE_ONLY_ENABLED = process.env.NEXT_PUBLIC_CORE_SCOPE_ONLY !== "false";

const CORE_ADMIN_ALLOWED_PATHS = [
	"/admin",
	"/admin/doctors",
	"/admin/hospitals",
	"/admin/profile",
];

const NON_CORE_PUBLIC_PATHS = [
	"/ambulances",
	"/blood-banks",
	"/health-schemes",
	"/blood",
	"/campaign",
];

function isAllowedCoreAdminPath(pathname: string): boolean {
	return CORE_ADMIN_ALLOWED_PATHS.some((allowed) =>
		pathname === allowed || pathname.startsWith(`${allowed}/`)
	);
}

// Decode JWT payload (without verification - that's done by backend)
function decodeJwtPayload(token: string): Record<string, unknown> | null {
	try {
		const parts = token.split(".");
		if (parts.length !== 3) return null;
		const payload = Buffer.from(parts[1], "base64").toString("utf-8");
		return JSON.parse(payload);
	} catch {
		return null;
	}
}

// Check if user has admin role
function hasAdminRole(token: string): boolean {
	const payload = decodeJwtPayload(token);
	if (!payload) return false;

	// Check realm_access.roles for admin roles
	const realmAccess = payload.realm_access as { roles?: string[] } | undefined;
	const roles = realmAccess?.roles ?? [];

	const adminRoles = ["admin", "superadmin", "ADMIN", "SUPERADMIN", "super-admin"];
	return roles.some((role) => adminRoles.includes(role));
}

export function middleware(request: NextRequest) {
	// Get the access token from cookies
	const accessToken = request.cookies.get("access_token")?.value;

	// Define routes
	const adminRoutes = ["/admin"];
	const protectedRoutes = ["/dashboard", "/profile"]; // Routes that require login (but not admin)
	const authRoutes = ["/login", "/signup"];

	const isAdminRoute = adminRoutes.some((route) =>
		request.nextUrl.pathname.startsWith(route)
	);

	const isProtectedRoute = protectedRoutes.some((route) =>
		request.nextUrl.pathname.startsWith(route)
	);

	const isAuthRoute = authRoutes.some((route) =>
		request.nextUrl.pathname.startsWith(route)
	);

	if (
		CORE_SCOPE_ONLY_ENABLED &&
		NON_CORE_PUBLIC_PATHS.some(
			(path) => request.nextUrl.pathname === path || request.nextUrl.pathname.startsWith(`${path}/`)
		)
	) {
		return NextResponse.redirect(new URL("/hospitals", request.url));
	}

	// If trying to access admin route
	if (isAdminRoute) {
		// No token = redirect to login
		if (!accessToken) {
			return NextResponse.redirect(new URL("/login", request.url));
		}

		// Has token but not admin = redirect to user dashboard
		if (!hasAdminRole(accessToken)) {
			const url = new URL("/dashboard", request.url);
			url.searchParams.set("error", "access_denied");
			return NextResponse.redirect(url);
		}

		if (CORE_SCOPE_ONLY_ENABLED && !isAllowedCoreAdminPath(request.nextUrl.pathname)) {
			return NextResponse.redirect(new URL("/admin/doctors", request.url));
		}
	}

	// If trying to access protected route (dashboard)
	if (isProtectedRoute) {
		// No token = redirect to login
		if (!accessToken) {
			return NextResponse.redirect(new URL("/login", request.url));
		}
	}

	// If already authenticated and trying to access auth routes, redirect based on role
	if (isAuthRoute && accessToken) {
		const isAdmin = hasAdminRole(accessToken);
		const redirectTo = isAdmin ? "/admin" : "/dashboard";
		return NextResponse.redirect(new URL(redirectTo, request.url));
	}

	return NextResponse.next();
}

export const config = {
	matcher: [
		/*
		 * Match all request paths except for the ones starting with:
		 * - api (API routes)
		 * - _next/static (static files)
		 * - _next/image (image optimization files)
		 * - favicon.ico (favicon file)
		 * - public folder
		 */
		"/((?!api|_next/static|_next/image|favicon.ico|public).*)",
	],
};
