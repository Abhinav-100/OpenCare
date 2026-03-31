"use client";

import { MapContainer, TileLayer, Marker, Popup } from "react-leaflet";
import L from "leaflet";
import "leaflet/dist/leaflet.css";
import { Hospital } from "@/shared/types/hospitals";

// Custom teal dot marker — avoids Leaflet's default icon image issues in Next.js
const hospitalIcon = L.divIcon({
	className: "",
	html: '<div style="width:14px;height:14px;background:#0d9488;border-radius:50%;border:2.5px solid white;box-shadow:0 2px 6px rgba(0,0,0,0.35)"></div>',
	iconSize: [14, 14],
	iconAnchor: [7, 7],
	popupAnchor: [0, -10],
});

const emergencyIcon = L.divIcon({
	className: "",
	html: '<div style="width:14px;height:14px;background:#dc2626;border-radius:50%;border:2.5px solid white;box-shadow:0 2px 6px rgba(0,0,0,0.35)"></div>',
	iconSize: [14, 14],
	iconAnchor: [7, 7],
	popupAnchor: [0, -10],
});

interface HospitalMapProps {
	hospitals: Hospital[];
}

export default function HospitalMap({ hospitals }: HospitalMapProps) {
	const mapped = hospitals.filter(
		(h) => h.lat != null && h.lon != null
	);

	return (
		<div className="space-y-2">
			{/* Legend */}
			<div className="flex items-center gap-4 text-xs text-gray-600 px-1">
				<div className="flex items-center gap-1.5">
					<div className="w-3 h-3 rounded-full bg-teal-600 border-2 border-white shadow" />
					<span>Hospital</span>
				</div>
				<div className="flex items-center gap-1.5">
					<div className="w-3 h-3 rounded-full bg-red-600 border-2 border-white shadow" />
					<span>Emergency services</span>
				</div>
				<span className="ml-auto text-gray-400">{mapped.length} locations shown</span>
			</div>

			{/* Map */}
			<div
				className="rounded-xl overflow-hidden border border-gray-200 shadow-sm"
				style={{ height: "calc(100vh - 320px)", minHeight: "480px" }}
			>
				<MapContainer
					center={[20.35, 84.8]}
					zoom={7}
					style={{ height: "100%", width: "100%" }}
					scrollWheelZoom={true}
				>
					<TileLayer
						attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a>'
						url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
					/>
					{mapped.map((hospital) => (
						<Marker
							key={hospital.id}
							position={[Number(hospital.lat), Number(hospital.lon)]}
							icon={hospital.hasEmergencyService ? emergencyIcon : hospitalIcon}
						>
							<Popup>
								<div style={{ minWidth: "190px" }}>
									<p style={{ fontWeight: 700, fontSize: "13px", marginBottom: "2px", color: "#111827" }}>
										{hospital.name}
									</p>
									{hospital.bnName && (
										<p style={{ fontSize: "11px", color: "#0d9488", marginBottom: "8px" }}>
											{hospital.bnName}
										</p>
									)}
									<div style={{ fontSize: "12px", color: "#4b5563", lineHeight: "1.6" }}>
										<p>
											{typeof hospital.hospitalType === "string"
												? hospital.hospitalType
												: hospital.hospitalType?.englishName ?? "Hospital"}
											{" · "}
											{typeof hospital.organizationType === "string"
												? hospital.organizationType
												: hospital.organizationType?.displayName ?? ""}
										</p>
										<p>🛏 {hospital.numberOfBed} beds · {hospital.district.name}</p>
										{hospital.hasEmergencyService && (
											<p style={{ color: "#dc2626" }}>🚨 Emergency services</p>
										)}
										{hospital.hasBloodBank && <p>🩸 Blood bank</p>}
									</div>
									<a
										href={`/hospitals/${hospital.id}`}
										style={{
											display: "inline-block",
											marginTop: "10px",
											fontSize: "12px",
											color: "#0d9488",
											fontWeight: 600,
											textDecoration: "none",
										}}
									>
										View Details →
									</a>
								</div>
							</Popup>
						</Marker>
					))}
				</MapContainer>
			</div>
		</div>
	);
}
