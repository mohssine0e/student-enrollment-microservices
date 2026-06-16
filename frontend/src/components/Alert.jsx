const styles = {
  error: 'border-red-500/20 bg-red-500/10 text-red-300',
  success: 'border-emerald-500/20 bg-emerald-500/10 text-emerald-300',
  info: 'border-slate-700 bg-slate-800/80 text-slate-200',
}

export function Alert({ type = 'info', children }) {
  return <div className={`rounded-xl border p-4 text-base font-medium ${styles[type]}`}>{children}</div>
}
