import { useState } from 'react'
import { cancelEnrollment, getDashboard } from '../api/client'
import { Alert } from '../components/Alert'
import { LoadingMessage } from '../components/LoadingMessage'

export function DashboardPage() {
  const [cnie, setCnie] = useState('')
  const [dashboard, setDashboard] = useState(null)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')
  const [message, setMessage] = useState('')

  async function loadDashboard(event) {
    event?.preventDefault()
    setLoading(true)
    setError('')
    setMessage('')

    try {
      const data = await getDashboard(cnie.trim())
      setDashboard(data)
    } catch (err) {
      setDashboard(null)
      setError(err.message)
    } finally {
      setLoading(false)
    }
  }

  async function handleCancel(enrollmentId) {
    setError('')
    setMessage('')

    try {
      await cancelEnrollment(enrollmentId)
      setMessage(`Enrollment ${enrollmentId} cancelled.`)
      await loadDashboard()
    } catch (err) {
      setError(err.message)
    }
  }

  return (
    <div className="space-y-6">
      <form className="flex flex-col gap-3 sm:flex-row" onSubmit={loadDashboard}>
        <input
          value={cnie}
          onChange={(event) => setCnie(event.target.value)}
          className="min-w-0 flex-1 rounded-md border border-slate-300 px-3 py-2 outline-none focus:border-slate-900 focus:ring-2 focus:ring-slate-200"
          placeholder="Student CNIE"
          required
        />
        <button
          type="submit"
          disabled={loading}
          className="rounded-md bg-slate-900 px-4 py-2 font-medium text-white disabled:bg-slate-400"
        >
          {loading ? 'Loading...' : 'Load Dashboard'}
        </button>
      </form>

      {message && (
        <Alert type="success">{message}</Alert>
      )}
      {error && (
        <Alert type="error">{error}</Alert>
      )}
      {loading && <LoadingMessage>Loading dashboard...</LoadingMessage>}

      {dashboard && (
        <section className="space-y-4">
          <div className="rounded-lg border border-slate-200 bg-slate-50 p-4">
            <p className="text-sm text-slate-500">Student</p>
            <h2 className="text-xl font-semibold text-slate-900">
              {dashboard.firstName} {dashboard.lastName}
            </h2>
            <p className="text-sm text-slate-600">
              CNIE {dashboard.cnie} · ID {dashboard.studentId}
            </p>
          </div>

          {dashboard.courses.length === 0 ? (
            <Alert>This student has no enrollments.</Alert>
          ) : (
            <div className="grid gap-4">
              {dashboard.courses.map((course) => (
                <article
                  key={course.enrollmentId}
                  className="rounded-lg border border-slate-200 p-4"
                >
                  <div className="flex flex-col gap-3 sm:flex-row sm:items-start sm:justify-between">
                    <div>
                      <h3 className="font-semibold text-slate-900">{course.courseTitle}</h3>
                      <p className="mt-1 text-sm text-slate-600">
                        Course ID {course.courseId} · Enrollment ID {course.enrollmentId}
                      </p>
                      <p className="mt-1 text-sm text-slate-600">
                        Enrolled at {course.enrolledAt}
                      </p>
                    </div>
                    <div className="flex items-center gap-2">
                      <span
                        className={`rounded px-2 py-1 text-xs font-medium ${
                          course.canCancel
                            ? 'bg-emerald-50 text-emerald-700 ring-1 ring-emerald-200'
                            : 'bg-slate-100 text-slate-500 ring-1 ring-slate-200'
                        }`}
                      >
                        {course.canCancel ? 'Can cancel' : 'Locked'}
                      </span>
                      <button
                        type="button"
                        disabled={!course.canCancel}
                        onClick={() => handleCancel(course.enrollmentId)}
                        className="rounded-md border border-slate-300 px-3 py-1.5 text-sm font-medium text-slate-700 disabled:cursor-not-allowed disabled:opacity-50"
                      >
                        Cancel
                      </button>
                    </div>
                  </div>
                </article>
              ))}
            </div>
          )}
        </section>
      )}
    </div>
  )
}
