import { useState } from "react";
import { API_BASE_URL } from "@/config";
export function Shortener() {
  const [url, setUrl] = useState("");
  const [shortUrl, setShortUrl] = useState("");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [copied, setCopied] = useState(false);

  const isValidUrl = (value) => {
    try {
      const u = new URL(value);
      return u.protocol === "http:" || u.protocol === "https:";
    } catch {
      return false;
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    setError("");
    setShortUrl("");
    setCopied(false);

    const trimmed = url.trim();

    if (!trimmed) {
      setError("Please enter a URL.");
      return;
    }

    if (!isValidUrl(trimmed)) {
      setError("Please enter a valid http(s) URL.");
      return;
    }

    setLoading(true);

    try {
      const res = await fetch(
        `${API_BASE_URL}/shorten?url=${encodeURIComponent(trimmed)}`,
        {
          method: "POST",
        }
      );

      if (!res.ok) {
        throw new Error(`Request failed (${res.status})`);
      }

      // Backend returns only shortcode
      const shortCode = (await res.text()).trim();

      // Build frontend-facing URL
      const generatedUrl = `${window.location.origin}/${shortCode}`;

      setShortUrl(generatedUrl);
    } catch (err) {
      setError(err.message || "Something went wrong.");
    } finally {
      setLoading(false);
    }
  };

  const handleCopy = async () => {
    try {
      await navigator.clipboard.writeText(shortUrl);

      setCopied(true);

      setTimeout(() => {
        setCopied(false);
      }, 1500);
    } catch {
      setError("Could not copy to clipboard.");
    }
  };

  return (
    <section className="rounded-md border border-border bg-card p-6">
      <h2 className="text-base font-semibold text-card-foreground">
        Shorten a URL
      </h2>

      <p className="mt-1 text-sm text-muted-foreground">
        Paste a long URL and get a short link.
      </p>

      <form
        onSubmit={handleSubmit}
        className="mt-4 flex flex-col gap-3 sm:flex-row"
      >
        <input
          type="text"
          value={url}
          onChange={(e) => setUrl(e.target.value)}
          placeholder="https://example.com/very/long/path"
          className="flex-1 rounded-md border border-input bg-background px-3 py-2 text-sm text-foreground outline-none placeholder:text-muted-foreground focus:border-ring"
          disabled={loading}
        />

        <button
          type="submit"
          disabled={loading}
          className="rounded-md bg-primary px-4 py-2 text-sm font-medium text-primary-foreground hover:bg-primary/90 disabled:opacity-60"
        >
          {loading ? "Shortening..." : "Shorten"}
        </button>
      </form>

      {error && (
        <p className="mt-3 text-sm text-destructive">
          {error}
        </p>
      )}

      {shortUrl && (
        <div className="mt-4 space-y-2 rounded-md border border-border bg-muted/40 p-3">
          <div className="flex flex-col gap-2 sm:flex-row sm:items-center sm:justify-between">
            <a
              href={shortUrl}
              target="_blank"
              rel="noreferrer"
              className="break-all text-sm text-foreground underline underline-offset-2"
            >
              {shortUrl}
            </a>

            <button
              type="button"
              onClick={handleCopy}
              className="shrink-0 rounded-md border border-border bg-background px-3 py-1.5 text-xs font-medium text-foreground hover:bg-accent"
            >
              {copied ? "Copied" : "Copy"}
            </button>
          </div>

          <p className="text-sm text-muted-foreground">
            The first redirect may take a few seconds.
          </p>
        </div>
      )}
    </section>
  );
}