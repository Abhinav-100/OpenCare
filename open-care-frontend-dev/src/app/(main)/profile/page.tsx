import { Metadata } from "next";
import ProfileView from "./_ProfileView";

export const metadata: Metadata = {
	title: "My Profile",
	description: "View and manage your OpenCare profile, contact information, and account settings.",
};

export default function ProfilePage() {
	return <ProfileView />;
}
