import { useEffect, useState } from 'react'
import { cancelEnrollment, getDashboard, getCourses, getStudents } from '../api/client'
import { Alert } from '../components/Alert'
import { LoadingMessage } from '../components/LoadingMessage'

export function DashboardPage() {
  const [stats, setStats] = useState(null)
  const [statsStatus, setStatsStatus] = useState('loading')
  const [statsError, setStatsError] = useState('')

  const [cnie, setCnie] = useState('')
  const [dashboard, setDashboard] = useState(null)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')
  const [message, setMessage] = useState('')

  useEffect(() => {
    let ignore = false
    
    Promise.all([getStudents(), getCourses()])
      .then(([studentsData, coursesData]) => {
        if (!ignore) {
          setStats({
            totalStudents: studentsData.length,
            totalCourses: coursesData.length
          })
          setStatsStatus('success')
        }
      })
      .catch((err) => {
        if (!ignore) {
          setStatsError(err.message)
          setStatsStatus('error')
        }
      })

    return () => { ignore = true }
  }, [])

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
    <div className="space-y-10">
      {/* Metrics Section */}
      <section>
        <h2 className="mb-4 text-xl font-bold text-white">System Metrics</h2>
        {statsStatus === 'loading' && <LoadingMessage>Loading statistics...</LoadingMessage>}
        {statsStatus === 'error' && <Alert type="error">{statsError}</Alert>}
        {statsStatus === 'success' && stats && (
          <div className="grid gap-6 md:grid-cols-2">
            <div className="relative overflow-hidden rounded-2xl bg-slate-800 p-6 shadow-sm ring-1 ring-slate-700/50 transition-transform hover:-translate-y-1 hover:shadow-md">
              <div className="absolute -right-6 -top-6 h-24 w-24 rounded-full bg-indigo-500 opacity-20 blur-2xl"></div>
              <p className="text-sm font-semibold uppercase tracking-wider text-slate-400">Total Students</p>
              <p className="mt-2 text-5xl font-extrabold text-indigo-400">{stats.totalStudents}</p>
            </div>
            
            <div className="relative overflow-hidden rounded-2xl bg-slate-800 p-6 shadow-sm ring-1 ring-slate-700/50 transition-transform hover:-translate-y-1 hover:shadow-md">
              <div className="absolute -right-6 -top-6 h-24 w-24 rounded-full bg-emerald-500 opacity-20 blur-2xl"></div>
              <p className="text-sm font-semibold uppercase tracking-wider text-slate-400">Available Courses</p>
              <p className="mt-2 text-5xl font-extrabold text-emerald-400">{stats.totalCourses}</p>
            </div>
          </div>
        )}
      </section>

      {/* Student Lookup Section */}
      <section className="rounded-2xl bg-slate-800/40 p-6 ring-1 ring-slate-700/50">
        <h2 className="mb-4 text-xl font-bold text-white">Student Dashboard Lookup</h2>
        <form className="flex flex-col gap-3 sm:flex-row" onSubmit={loadDashboard}>
          <input
            value={cnie}
            onChange={(event) => setCnie(event.target.value)}
            className="min-w-0 flex-1 rounded-xl border-0 py-3 px-4 bg-slate-800 text-white shadow-sm ring-1 ring-inset ring-slate-700 placeholder:text-slate-500 focus:ring-2 focus:ring-inset focus:ring-indigo-500 sm:text-sm sm:leading-6"
            placeholder="Enter Student CNIE (e.g. E2E-CNIE-001)"
            required
          />
          <button
            type="submit"
            disabled={loading}
            className="flex items-center justify-center rounded-xl bg-indigo-600 px-6 py-3 text-sm font-semibold text-white shadow-sm hover:bg-indigo-500 focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-indigo-600 disabled:opacity-50 disabled:cursor-not-allowed transition-all"
          >
            {loading ? 'Searching...' : 'Search Records'}
          </button>
        </form>

        <div className="mt-6">
          {message && <Alert type="success">{message}</Alert>}
          {error && <Alert type="error">{error}</Alert>}
          {loading && <LoadingMessage>Fetching student dashboard...</LoadingMessage>}

          {dashboard && (
            <div className="space-y-6 animate-in fade-in slide-in-from-bottom-4 duration-500">
              <div className="flex items-center gap-4 rounded-xl border border-indigo-500/20 bg-slate-800 p-5 shadow-sm">
                <div className="flex h-12 w-12 items-center justify-center rounded-full bg-indigo-500/20 text-xl font-bold text-indigo-400">
                  {dashboard.firstName[0]}{dashboard.lastName[0]}
                </div>
                <div>
                  <h3 className="text-lg font-bold text-white">
                    {dashboard.firstName} {dashboard.lastName}
                  </h3>
                  <p className="text-sm font-medium text-slate-400">
                    CNIE: <span className="text-slate-300">{dashboard.cnie}</span> &bull; ID: <span className="text-slate-300">{dashboard.studentId}</span>
                  </p>
                </div>
              </div>

              {dashboard.courses.length === 0 ? (
                <div className="rounded-xl border border-slate-700 bg-slate-800 p-8 text-center shadow-sm">
                  <p className="text-slate-400">This student is not currently enrolled in any courses.</p>
                </div>
              ) : (
                <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
                  {dashboard.courses.map((course) => (
                    <article
                      key={course.enrollmentId}
                      className="group relative flex flex-col justify-between rounded-xl border border-slate-700 bg-slate-800 p-5 shadow-sm transition-all hover:shadow-md hover:border-indigo-500/50"
                    >
                      <div>
                        <div className="mb-2 flex items-center justify-between">
                          <span className="rounded-full bg-slate-700 px-2.5 py-0.5 text-xs font-semibold text-slate-300 group-hover:bg-indigo-500/20 group-hover:text-indigo-400 transition-colors">
                            Course #{course.courseId}
                          </span>
                          <span
                            className={`rounded-full px-2.5 py-0.5 text-xs font-semibold ${
                              course.canCancel
                                ? 'bg-emerald-500/10 text-emerald-400 ring-1 ring-inset ring-emerald-500/20'
                                : 'bg-rose-500/10 text-rose-400 ring-1 ring-inset ring-rose-500/20'
                            }`}
                          >
                            {course.canCancel ? 'Cancellable' : 'Locked'}
                          </span>
                        </div>
                        <h4 className="text-base font-bold text-white leading-tight mb-2">
                          {course.courseTitle}
                        </h4>
                        <p className="text-xs text-slate-400 mb-4">
                          Enrolled: {new Date(course.enrolledAt).toLocaleString()}
                        </p>
                      </div>
                      <div className="mt-auto pt-4 border-t border-slate-700">
                        <button
                          type="button"
                          disabled={!course.canCancel}
                          onClick={() => handleCancel(course.enrollmentId)}
                          className="w-full rounded-lg bg-slate-700 px-3 py-2 text-sm font-semibold text-white shadow-sm ring-1 ring-inset ring-slate-600 hover:bg-slate-600 disabled:opacity-50 disabled:cursor-not-allowed transition-all"
                        >
                          Cancel Enrollment
                        </button>
                      </div>
                    </article>
                  ))}
                </div>
              )}
            </div>
          )}
        </div>
      </section>
    </div>
  )
}
