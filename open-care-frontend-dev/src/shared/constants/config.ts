const envBaseUrl =
	process.env.NEXT_PUBLIC_API_BASE_URL || process.env.NEXT_PUBLIC_API_URL;

export const baseUrl =
	(envBaseUrl ? envBaseUrl.replace(/\/$/, "") : null) ||
	"http://localhost:6700/api";
