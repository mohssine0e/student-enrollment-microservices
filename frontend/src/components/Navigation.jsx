export function Navigation({ routes, activeRoute }) {
  return (
    <nav className="flex items-center space-x-3" aria-label="Primary navigation">
      {routes.map((route) => (
        <a
          key={route.path}
          href={`#/${route.path}`}
          className={`relative rounded-full px-5 py-2.5 text-base font-semibold transition-all duration-200 ${
            activeRoute === route.path
              ? 'bg-indigo-600 text-white shadow-md shadow-indigo-900/50 ring-1 ring-indigo-500'
              : 'text-slate-300 hover:bg-slate-800/80 hover:text-white ring-1 ring-transparent hover:ring-slate-700'
          }`}
        >
          {route.label}
        </a>
      ))}
    </nav>
  )
}
