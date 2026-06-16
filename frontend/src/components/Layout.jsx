import { Navigation } from './Navigation'

export function Layout({ routes, activeRoute, title, children }) {
  return (
    <div className="min-h-screen text-slate-100">
      <header className="glass sticky top-0 z-50 border-b border-slate-700/50 shadow-sm transition-all">
        <div className="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8">
          <div className="flex h-20 items-center justify-between">
            <div className="flex items-center">
              <div className="flex-shrink-0 group flex items-center gap-3">
                <div className="flex h-12 w-12 items-center justify-center rounded-xl bg-gradient-to-br from-indigo-500 to-purple-600 shadow-lg shadow-indigo-500/30">
                  <svg className="h-7 w-7 text-white" viewBox="0 0 24 24" fill="currentColor">
                    <path d="M12 14l9-5-9-5-9 5 9 5z" />
                    <path d="M12 14l6.16-3.422-6.16-3.422-6.16 3.422z" />
                    <path d="M3 12l9 5 9-5-9-5-9 5z" />
                  </svg>
                </div>
                <span className="hidden text-2xl font-bold tracking-tight text-white md:block">
                  Enroll<span className="text-indigo-400">Pro</span>
                </span>
              </div>
              <div className="hidden md:ml-12 md:block">
                <Navigation routes={routes} activeRoute={activeRoute} />
              </div>
            </div>
            <div className="flex items-center gap-4">
              <span className="hidden rounded-full bg-slate-800 px-4 py-1.5 text-sm font-semibold text-slate-300 shadow-sm ring-1 ring-inset ring-slate-700 md:block">
                System Active
              </span>
            </div>
          </div>
        </div>
      </header>
      <main className="py-12">
        <div className="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8">
          <header className="mb-10">
            <h1 className="text-4xl font-extrabold tracking-tight text-white sm:text-5xl">{title}</h1>
            <p className="mt-3 text-lg text-slate-400">Manage and oversee the student enrollment process</p>
          </header>
          <div className="glass-card rounded-2xl p-6 md:p-10">
            {children}
          </div>
        </div>
      </main>
    </div>
  )
}
