export function Navigation({ routes, activeRoute }) {
  return (
    <nav className="flex flex-wrap gap-2" aria-label="Primary navigation">
      {routes.map((route) => (
        <a
          key={route.path}
          href={`#/${route.path}`}
          className={`rounded-md px-4 py-2 text-sm font-medium transition ${
            activeRoute === route.path
              ? 'bg-slate-900 text-white'
              : 'bg-white text-slate-700 ring-1 ring-slate-200 hover:bg-slate-50'
          }`}
        >
          {route.label}
        </a>
      ))}
    </nav>
  )
}
