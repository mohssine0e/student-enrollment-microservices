import { useEffect, useMemo, useState } from 'react'
import './App.css'

const routes = [
  { path: 'courses', label: 'Courses', title: 'Course Listing' },
  { path: 'enroll', label: 'Enroll', title: 'Student Enrollment' },
  { path: 'dashboard', label: 'Dashboard', title: 'Student Dashboard' },
]

function getRouteFromHash() {
  const route = window.location.hash.replace('#/', '')
  return routes.some((item) => item.path === route) ? route : 'courses'
}

function App() {
  const [activeRoute, setActiveRoute] = useState(getRouteFromHash)

  useEffect(() => {
    const handleHashChange = () => setActiveRoute(getRouteFromHash())
    window.addEventListener('hashchange', handleHashChange)

    if (!window.location.hash) {
      window.location.hash = '/courses'
    }

    return () => window.removeEventListener('hashchange', handleHashChange)
  }, [])

  const currentRoute = useMemo(
    () => routes.find((route) => route.path === activeRoute) ?? routes[0],
    [activeRoute],
  )

  return (
    <main className="min-h-screen bg-slate-100 px-4 py-6 text-slate-900">
      <section className="mx-auto max-w-6xl">
        <header className="mb-6 border-b border-slate-200 pb-4">
          <p className="text-sm font-medium uppercase tracking-wide text-slate-500">
            Student Enrollment System
          </p>
          <h1 className="mt-2 text-3xl font-semibold">{currentRoute.title}</h1>
        </header>

        <nav className="mb-6 flex flex-wrap gap-2">
          {routes.map((route) => (
            <a
              key={route.path}
              href={`#/${route.path}`}
              className={`rounded-md px-4 py-2 text-sm font-medium ${
                activeRoute === route.path
                  ? 'bg-slate-900 text-white'
                  : 'bg-white text-slate-700 ring-1 ring-slate-200'
              }`}
            >
              {route.label}
            </a>
          ))}
        </nav>

        <section className="rounded-lg bg-white p-6 shadow-sm ring-1 ring-slate-200">
          <p className="text-slate-600">
            {currentRoute.title} page content will be implemented in the next frontend tasks.
          </p>
        </section>
      </section>
    </main>
  )
}

export default App
