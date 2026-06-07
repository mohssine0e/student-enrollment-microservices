import { Navigation } from './Navigation'

export function Layout({ routes, activeRoute, title, children }) {
  return (
    <main className="min-h-screen bg-slate-100 px-4 py-6 text-slate-900">
      <section className="mx-auto max-w-6xl">
        <header className="mb-6 border-b border-slate-200 pb-4">
          <p className="text-sm font-medium uppercase tracking-wide text-slate-500">
            Student Enrollment System
          </p>
          <h1 className="mt-2 text-3xl font-semibold">{title}</h1>
        </header>

        <div className="mb-6">
          <Navigation routes={routes} activeRoute={activeRoute} />
        </div>

        <section className="rounded-lg bg-white p-6 shadow-sm ring-1 ring-slate-200">
          {children}
        </section>
      </section>
    </main>
  )
}
