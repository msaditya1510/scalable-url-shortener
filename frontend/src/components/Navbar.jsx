export function Navbar({ activeTab, onTabChange }) {
  return (
    <nav className="border-b border-slate-200 bg-white">
      <div className="mx-auto max-w-3xl px-4 sm:px-6 lg:px-8">
        <div className="flex items-center justify-between h-16">
          <div className="text-lg font-semibold text-slate-900">Routr</div>
          <div className="flex gap-4">
            <button
              onClick={() => onTabChange("shorten")}
              className={`px-3 py-2 text-sm font-medium rounded-md transition-colors ${
                activeTab === "shorten"
                  ? "bg-slate-100 text-slate-900"
                  : "text-slate-600 hover:text-slate-900"
              }`}
            >
              Shorten URL
            </button>
            <button
              onClick={() => onTabChange("analytics")}
              className={`px-3 py-2 text-sm font-medium rounded-md transition-colors ${
                activeTab === "analytics"
                  ? "bg-slate-100 text-slate-900"
                  : "text-slate-600 hover:text-slate-900"
              }`}
            >
              Analytics
            </button>
          </div>
        </div>
      </div>
    </nav>
  );
}
