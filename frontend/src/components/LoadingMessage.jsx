export function LoadingMessage({ children = 'Loading...' }) {
  return (
    <div className="rounded-xl border border-slate-700 bg-slate-800/60 p-4 text-slate-300 font-medium animate-pulse">
      {children}
    </div>
  )
}
