export function LoadingMessage({ children = 'Loading...' }) {
  return (
    <div className="rounded-md border border-slate-200 bg-slate-50 p-4 text-slate-600">
      {children}
    </div>
  )
}
