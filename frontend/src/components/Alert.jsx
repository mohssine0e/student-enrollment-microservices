const styles = {
  error: 'border-red-200 bg-red-50 text-red-700',
  success: 'border-emerald-200 bg-emerald-50 text-emerald-700',
  info: 'border-slate-200 bg-slate-50 text-slate-600',
}

export function Alert({ type = 'info', children }) {
  return <div className={`rounded-md border p-4 ${styles[type]}`}>{children}</div>
}
