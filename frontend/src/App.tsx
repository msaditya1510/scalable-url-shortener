import { useEffect, useState } from "react";
import { Shortener } from "./components/Shortener.jsx";
import { Stats } from "./components/Stats.jsx";
import { Navbar } from "./components/Navbar.jsx";
import { API_BASE_URL } from "@/config";

export default function App() {
  const [activeTab, setActiveTab] = useState("shorten");
  const [redirectError, setRedirectError] = useState<string | null>(null);

  const path = window.location.pathname.replace(/^\//, "").replace(/\/$/, "");
  const pathSegments = path.split("/").filter(Boolean);
  const reservedRoutes = new Set(["", "shorten", "analytics", "stats"]);
  const shouldRedirect = pathSegments.length === 1 && !reservedRoutes.has(pathSegments[0]);
  const shortCode = shouldRedirect ? pathSegments[0] : "";

  useEffect(() => {
  if (!shouldRedirect) {
    return;
  }

  let active = true;

  setRedirectError(null);

 const tryRedirect = async () => {
  try {
    const response = await fetch(
      `${API_BASE_URL}/${encodeURIComponent(shortCode)}`,
      {
        method: "GET",
        redirect: "manual",
      }
    );

    if (!active) {
      return;
    }

    // Check if backend returned an error JSON response
    const contentType =
      response.headers.get("content-type") || "";

    if (contentType.includes("application/json")) {
      const data = await response.json();

      if (data?.success === false) {
        setRedirectError(
          data.message || "Link not found."
        );

        return;
      }
    }

    // If no error from backend, redirect to the short link
    window.location.href =
      `${API_BASE_URL}/${encodeURIComponent(shortCode)}`;

  } catch (error) {
    if (!active) {
      return;
    }

    setRedirectError(
      "Unable to reach the server."
    );
  }
};

  tryRedirect();

  return () => {
    active = false;
  };
}, [shouldRedirect, shortCode]);

  if (shouldRedirect && !redirectError) {
    return null;
  }

  if (shouldRedirect && redirectError) {
    return (
      <div className="min-h-screen bg-slate-50 text-slate-900">
        <main className="mx-auto max-w-3xl px-4 py-8 sm:px-6 lg:px-8">
          <div className="rounded-3xl border border-slate-200 bg-white p-10 text-center shadow-sm shadow-slate-200/50">
            <p className="text-sm font-semibold uppercase tracking-[0.2em] text-slate-500">
              Link not found
            </p>
            <h1 className="mt-4 text-3xl font-semibold tracking-tight text-slate-900 sm:text-4xl">
              We couldn’t find that short link.
            </h1>
            <p className="mt-4 text-sm text-slate-600">
              {redirectError} Please check the code or go back to the homepage.
            </p>
            <a
              href="/"
              className="mt-6 inline-flex rounded-md bg-primary px-4 py-2 text-sm font-medium text-primary-foreground hover:bg-primary/90"
            >
              Go home
            </a>
          </div>
        </main>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-slate-50 text-slate-900">
      <Navbar activeTab={activeTab} onTabChange={setActiveTab} />
      
      <main className="mx-auto max-w-3xl px-4 py-8 sm:px-6 lg:px-8">
        <div className="text-center">
          <p className="text-sm font-semibold uppercase tracking-[0.2em] text-slate-500">
            URL Shortener
          </p>
          <h1 className="mt-3 text-3xl font-semibold tracking-tight text-slate-900 sm:text-4xl">
            A simple tool to shorten links and check their click counts.
          </h1>
        </div>

        <div className="mt-10 rounded-3xl border border-slate-200 bg-white shadow-sm shadow-slate-200/50">
          <div className="p-6 sm:p-8">
            {activeTab === "shorten" && <Shortener />}
            {activeTab === "analytics" && <Stats />}
          </div>
        </div>
      </main>
    </div>
  );
}
