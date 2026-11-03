import { render, screen } from "@testing-library/react";
import { MemoryRouter } from "react-router-dom";
import { beforeEach, describe, expect, it, vi } from "vitest";
import App from "./App";

describe("React app authentication routing", () => {
  beforeEach(() => {
    localStorage.clear();
    vi.restoreAllMocks();
    vi.unstubAllGlobals();
  });

  it("redirects a logged-out user away from the protected Today page", async () => {
    render(
      <MemoryRouter initialEntries={["/today"]}>
        <App />
      </MemoryRouter>
    );

    expect(
      await screen.findByRole("heading", { name: /good to see you again/i })
    ).toBeInTheDocument();
    expect(screen.getByRole("button", { name: /sign in/i })).toBeInTheDocument();
  });

  it("allows an authenticated user to open the protected Today page", async () => {
    localStorage.setItem("imtps.authToken", "test-token");
    localStorage.setItem(
      "imtps.currentUser",
      JSON.stringify({
        id: "test-user",
        name: "Test User",
        email: "test@example.com",
      })
    );

    vi.stubGlobal(
      "fetch",
      vi.fn().mockImplementation(async (input: RequestInfo | URL) => {
        const url = String(input);

        if (url.includes("/schedules/current")) {
          return {
            ok: false,
            status: 404,
            json: async () => ({ message: "No saved schedule" }),
          };
        }

        return {
          ok: true,
          status: 200,
          json: async () => [],
        };
      })
    );

    render(
      <MemoryRouter initialEntries={["/today"]}>
        <App />
      </MemoryRouter>
    );

    expect(
      await screen.findByRole("heading", { name: /good morning,\s*test/i })
    ).toBeInTheDocument();
    expect(screen.queryByRole("button", { name: /sign in/i })).not.toBeInTheDocument();
  });
});
