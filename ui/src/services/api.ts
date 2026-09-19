import { API_BASE_URL } from "../config";
import { authHeaders, handleUnauthorized } from "../auth";
export type Category = "ACADEMIC" | "FAMILY" | "HEALTH" | "LEISURE";
export type Weight = "LOW" | "MEDIUM" | "HIGH";
export type TaskStatus = "TO_DO" | "IN_PROGRESS" | "DONE";

export type Task = {
  id: string;
  title: string;
  description: string;
  category: Category;
  deadline: string;
  durationMinutes: number;
  importance: Weight;
  effort: Weight;
  status: TaskStatus;
};

export type TaskInput = Omit<Task, "id" | "status"> & { status?: TaskStatus };

export type ScheduledItem = {
  task: Task;
  startTime: string;
  endTime: string;
  deadlineClash: boolean;
};

export type ScheduleResponse = {
  strategy: string;
  items: ScheduledItem[];
  totalScheduledMinutes: number;
  deadlineClashes: number;
};


async function request<T>(path: string, options?: RequestInit): Promise<T> {
  const response = await fetch(`${API_BASE_URL}${path}`, {
    ...options,
    headers: { ...authHeaders(true), ...options?.headers },
  });

  if (!response.ok) {
    if (handleUnauthorized(response)) throw new Error("Your session has expired.");

    const body = await response.json().catch(() => ({ error: "Request failed" }));
    throw new Error(body.error || "Request failed");
  }

  if (response.status === 204) return undefined as T;
  return response.json();
}

export const taskApi = {
  getAll: () => request<Task[]>("/tasks"),
  create: (task: TaskInput) =>
    request<Task>("/tasks", { method: "POST", body: JSON.stringify(task) }),
  update: (task: Task) =>
    request<Task>(`/tasks/${task.id}`, { method: "PUT", body: JSON.stringify(task) }),
  remove: (id: string) => request<void>(`/tasks/${id}`, { method: "DELETE" }),
};

export const scheduleApi = {
  generate: (strategy: "deadline" | "focus" | "balance", focusCategory = "ACADEMIC") =>
    request<ScheduleResponse>("/schedules/generate", {
      method: "POST",
      body: JSON.stringify({ strategy, focusCategory, dayStart: "08:00", dayEnd: "20:00" }),
    }),
};
