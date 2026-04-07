"use server";

import { redirect } from "next/navigation";

// Auth flow: This file handles session/token actions used by login/logout/refresh flows.
export async function redirectToHome() {
	redirect("/");
}

export async function redirectTo(path: string) {
	redirect(path);
}
