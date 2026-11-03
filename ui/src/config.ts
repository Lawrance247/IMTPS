const configuredApiUrl = import.meta.env.VITE_API_URL?.trim();

export const API_BASE_URL = (configuredApiUrl || "http://localhost:8080/api").replace(/\/$/, "");
