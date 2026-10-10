import { render, screen, waitFor } from "@testing-library/react";
import { MemoryRouter } from "react-router-dom";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";
import TodayPage from "./TodayPage";

function todayString() {
  const d = new Date();
  return [d.getFullYear(), String(d.getMonth() + 1).padStart(2, "0"), String(d.getDate()).padStart(2, "0")].join("-");
}

const user = {
  id: "user-a",
  fullName: "Test Student",
  email: "test@example.com",
  createdAt: "2026-09-20T00:00:00",
};

beforeEach(() => {
  localStorage.setItem("imtps.authToken", "test-token");
  localStorage.setItem("imtps.currentUser", JSON.stringify(user));
});

afterEach(() => {
  vi.unstubAllGlobals();
});

describe("Today dashboard", () => {
  it("shows the logged-in user's first name", async () => {
    vi.stubGlobal("fetch", vi.fn()
      .mockResolvedValueOnce(new Response("", { status: 404 }))
      .mockResolvedValueOnce(Response.json([])));

    render(<MemoryRouter><TodayPage /></MemoryRouter>);

    expect(await screen.findByText(/Test/)).toBeInTheDocument();
  });

  it("shows a saved schedule, strategy, focus and next task", async () => {
    const schedule = {
      strategy: "deadline",
      strategyName: "Earliest Deadline First",
      focusCategory: "ACADEMIC",
      date: todayString(),
      totalScheduledMinutes: 60,
      deadlineClashes: 0,
      items: [{
        task: { id: "task-1", title: "Study Algorithms", category: "ACADEMIC", durationMinutes: 60 },
        startTime: `${todayString()}T09:00:00`,
        endTime: `${todayString()}T10:00:00`,
      }],
    };

    vi.stubGlobal("fetch", vi.fn()
      .mockResolvedValueOnce(Response.json(schedule))
      .mockResolvedValueOnce(Response.json([{ id: "task-1", status: "TO_DO" }])));

    render(<MemoryRouter><TodayPage /></MemoryRouter>);

    expect((await screen.findAllByText("Study Algorithms")).length).toBeGreaterThanOrEqual(2);
    expect(screen.getByText(/Currently using: Earliest Deadline First/)).toBeInTheDocument();
    expect(screen.getByText("Academic", { selector: "h3" })).toBeInTheDocument();
    expect(screen.getByText("0/1")).toBeInTheDocument();
  });

  it("updates today's progress from completed task data", async () => {
    const schedule = {
      strategy: "deadline",
      strategyName: "Earliest Deadline First",
      focusCategory: "ACADEMIC",
      date: todayString(),
      totalScheduledMinutes: 60,
      deadlineClashes: 0,
      items: [{
        task: { id: "task-1", title: "Finished Task", category: "ACADEMIC", durationMinutes: 60 },
        startTime: `${todayString()}T09:00:00`,
        endTime: `${todayString()}T10:00:00`,
      }],
    };

    vi.stubGlobal("fetch", vi.fn()
      .mockResolvedValueOnce(Response.json(schedule))
      .mockResolvedValueOnce(Response.json([{ id: "task-1", status: "DONE" }])));

    render(<MemoryRouter><TodayPage /></MemoryRouter>);

    await waitFor(() => expect(screen.getByText("1/1")).toBeInTheDocument());
    expect(screen.getByText("100% of today's tasks")).toBeInTheDocument();
    expect(screen.getByText("0 remaining")).toBeInTheDocument();
  });

  it("handles an empty dashboard without crashing", async () => {
    vi.stubGlobal("fetch", vi.fn()
      .mockResolvedValueOnce(new Response("", { status: 404 }))
      .mockResolvedValueOnce(Response.json([])));

    render(<MemoryRouter><TodayPage /></MemoryRouter>);

    expect(await screen.findByText("No task scheduled")).toBeInTheDocument();
    expect(screen.getByText("0/0")).toBeInTheDocument();
    expect(screen.getByText("No focus selected")).toBeInTheDocument();
  });
});
