export function getAuthToken() {
  return localStorage.getItem("imtps.authToken");
}

export function authHeaders(includeJson = false): Record<string, string> {
  const token = getAuthToken();
  const headers: Record<string, string> = {};

  if (includeJson) headers["Content-Type"] = "application/json";
  if (token) headers.Authorization = `Bearer ${token}`;

  return headers;
}

export function clearAuth() {
  localStorage.removeItem("imtps.authToken");
  localStorage.removeItem("imtps.currentUser");
  localStorage.removeItem("imtps.currentSchedule");
}
