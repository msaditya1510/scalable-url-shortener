import { useState } from "react";
import { API_BASE_URL } from "@/config";

export function Stats() {
  const [code, setCode] = useState("");
  const [stats, setStats] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const normalizeShortCode = (value) => {
    const trimmed = value.trim();
    if (!trimmed) {
      return "";
    }

    try {
      const url = new URL(trimmed);
      const segments = url.pathname.split("/").filter(Boolean);
      return segments.length ? segments[segments.length - 1] : "";
    } catch {
      const segments = trimmed.split("/").filter(Boolean);
      return segments.length ? segments[segments.length - 1] : "";
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");
    setStats(null);

    const shortCode = normalizeShortCode(code);
    if (!shortCode) {
      setError("Please enter a short code or shortened URL.");
      return;
    }

    setLoading(true);
    try {
      const res = await fetch(
        `${API_BASE_URL}/stats/${encodeURIComponent(shortCode)}`
      );
      if (res.status === 404) {
        throw new Error("Short code not found.");
      }
      if (!res.ok) {
        throw new Error(`Request failed (${res.status})`);
      }
      const data = await res.json();
      setStats({
        shortCode:
          data.shortCode || data.short_code || data.code || trimmed,
        totalClicks:
          data.totalClicks ?? data.total_clicks ?? data.clicks ?? 0,
      });
    } catch (err) {
      setError(err.message || "Something went wrong.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <section className="rounded-md border border-border bg-card p-6">
      <h2 className="text-base font-semibold text-card-foreground">URL statistics</h2>
      <p className="mt-1 text-sm text-muted-foreground">
        Look up click stats for a short code.
      </p>

      <form onSubmit={handleSubmit} className="mt-4 flex flex-col gap-3 sm:flex-row">
        <input
          type="text"
          value={code}
          onChange={(e) => setCode(e.target.value)}
          placeholder="abc123"
          className="flex-1 rounded-md border border-input bg-background px-3 py-2 text-sm text-foreground outline-none placeholder:text-muted-foreground focus:border-ring"
          disabled={loading}
        />
        <button
          type="submit"
          disabled={loading}
          className="rounded-md bg-primary px-4 py-2 text-sm font-medium text-primary-foreground hover:bg-primary/90 disabled:opacity-60"
        >
          {loading ? "Loading..." : "Get Stats"}
        </button>
      </form>

      {error && <p className="mt-3 text-sm text-destructive">{error}</p>}

      {stats && (
        <dl className="mt-4 divide-y divide-border rounded-md border border-border">
          <div className="flex items-center justify-between px-3 py-2 text-sm">
            <dt className="text-muted-foreground">Short code</dt>
            <dd className="font-mono text-foreground">{stats.shortCode}</dd>
          </div>
          <div className="flex items-center justify-between px-3 py-2 text-sm">
            <dt className="text-muted-foreground">Total clicks</dt>
            <dd className="font-mono text-foreground">{stats.totalClicks}</dd>
          </div>
        </dl>
      )}
    </section>
  );
}
