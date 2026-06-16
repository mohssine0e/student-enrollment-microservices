import { useEffect, useState } from 'react'
import { createEnrollment, getCourses } from '../api/client'
import { Alert } from '../components/Alert'

export function EnrollmentPage() {
  const [courses, setCourses] = useState([])
  const [cnie, setCnie] = useState('')
  const [courseId, setCourseId] = useState('')
  const [loadingCourses, setLoadingCourses] = useState(true)
  const [submitting, setSubmitting] = useState(false)
  const [message, setMessage] = useState('')
  const [error, setError] = useState('')

  useEffect(() => {
    getCourses()
      .then((data) => {
        setCourses(data)
        if (data.length > 0) {
          setCourseId(String(data[0].id))
        }
      })
      .catch((err) => setError(err.message))
      .finally(() => setLoadingCourses(false))
  }, [])

  async function handleSubmit(event) {
    event.preventDefault()
    setSubmitting(true)
    setMessage('')
    setError('')

    try {
      const enrollment = await createEnrollment({
        cnie: cnie.trim(),
        courseId: Number(courseId),
      })
      setMessage(`Successfully enrolled student into course! Enrollment ID: ${enrollment.id}`)
      setCnie('')
    } catch (err) {
      setError(err.message)
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <div className="grid gap-8 lg:grid-cols-[1fr_380px] xl:grid-cols-[1fr_420px]">
      <div className="rounded-2xl bg-slate-800 p-6 shadow-sm ring-1 ring-slate-700/50 md:p-8">
        <div className="mb-8">
          <h2 className="text-2xl font-extrabold text-white tracking-tight">New Enrollment</h2>
          <p className="mt-2 text-sm text-slate-400">
            Select a course and enter the student's CNIE to process a new enrollment.
          </p>
        </div>

        <form className="space-y-6" onSubmit={handleSubmit}>
          <div>
            <label className="mb-2 block text-sm font-semibold text-slate-300" htmlFor="courseId">
              Select Course
            </label>
            <div className="relative">
              <select
                id="courseId"
                value={courseId}
                onChange={(event) => setCourseId(event.target.value)}
                className="block w-full appearance-none rounded-xl border-0 py-3.5 pl-4 pr-10 bg-slate-900 text-white shadow-sm ring-1 ring-inset ring-slate-700 focus:ring-2 focus:ring-inset focus:ring-indigo-500 sm:text-sm sm:leading-6"
                disabled={loadingCourses || courses.length === 0}
                required
              >
                {courses.length === 0 && !loadingCourses && (
                  <option value="" disabled>No courses available</option>
                )}
                {courses.map((course) => (
                  <option key={course.id} value={course.id}>
                    {course.title} (ID: {course.id})
                  </option>
                ))}
              </select>
              <div className="pointer-events-none absolute inset-y-0 right-0 flex items-center pr-3">
                <svg className="h-5 w-5 text-slate-400" viewBox="0 0 20 20" fill="currentColor" aria-hidden="true">
                  <path fillRule="evenodd" d="M5.23 7.21a.75.75 0 011.06.02L10 11.168l3.71-3.938a.75.75 0 111.08 1.04l-4.25 4.5a.75.75 0 01-1.08 0l-4.25-4.5a.75.75 0 01.02-1.06z" clipRule="evenodd" />
                </svg>
              </div>
            </div>
          </div>

          <div>
            <label className="mb-2 block text-sm font-semibold text-slate-300" htmlFor="cnie">
              Student CNIE
            </label>
            <input
              id="cnie"
              value={cnie}
              onChange={(event) => setCnie(event.target.value)}
              className="block w-full rounded-xl border-0 py-3.5 px-4 bg-slate-900 text-white shadow-sm ring-1 ring-inset ring-slate-700 placeholder:text-slate-500 focus:ring-2 focus:ring-inset focus:ring-indigo-500 sm:text-sm sm:leading-6"
              placeholder="e.g. E2E-CNIE-001"
              required
            />
          </div>

          <div className="pt-4">
            <button
              type="submit"
              disabled={submitting || loadingCourses || courses.length === 0}
              className="flex w-full items-center justify-center gap-2 rounded-xl bg-indigo-600 px-4 py-3.5 text-sm font-semibold text-white shadow-sm hover:bg-indigo-500 focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-indigo-600 disabled:opacity-50 disabled:cursor-not-allowed transition-all"
            >
              {submitting ? (
                <>
                  <svg className="h-5 w-5 animate-spin text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                    <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
                    <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                  </svg>
                  Processing...
                </>
              ) : (
                'Confirm Enrollment'
              )}
            </button>
          </div>

          {message && <Alert type="success">{message}</Alert>}
          {error && <Alert type="error">{error}</Alert>}
        </form>
      </div>

      <aside className="rounded-2xl border border-slate-700 bg-slate-800 p-6 shadow-sm md:p-8 h-fit self-start">
        <div className="mb-6 flex items-center gap-3">
          <div className="flex h-10 w-10 items-center justify-center rounded-lg bg-indigo-500/20 text-indigo-400">
            <svg className="h-5 w-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
              <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z" />
            </svg>
          </div>
          <h3 className="text-lg font-bold text-white">Enrollment Policies</h3>
        </div>
        
        <p className="mb-6 text-sm text-slate-400 leading-relaxed">
          Please review the following system constraints before attempting to process a student enrollment.
        </p>
        
        <div className="space-y-4">
          <div className="flex gap-4 items-start">
            <div className="mt-0.5 flex h-6 w-6 shrink-0 items-center justify-center rounded-full bg-slate-700 text-xs font-bold text-slate-300">
              1
            </div>
            <div>
              <p className="text-sm font-semibold text-white">Identity Verification</p>
              <p className="mt-1 text-xs text-slate-400 leading-relaxed">The system strictly verifies the student via their unique CNIE string identifier.</p>
            </div>
          </div>
          
          <div className="flex gap-4 items-start">
            <div className="mt-0.5 flex h-6 w-6 shrink-0 items-center justify-center rounded-full bg-slate-700 text-xs font-bold text-slate-300">
              2
            </div>
            <div>
              <p className="text-sm font-semibold text-white">Strict Capacity Limit</p>
              <p className="mt-1 text-xs text-slate-400 leading-relaxed">Each course enforces a strict maximum capacity of <span className="font-semibold text-indigo-400">3 students</span>. Exceeding enrollments will be rejected automatically.</p>
            </div>
          </div>
          
          <div className="flex gap-4 items-start">
            <div className="mt-0.5 flex h-6 w-6 shrink-0 items-center justify-center rounded-full bg-slate-700 text-xs font-bold text-slate-300">
              3
            </div>
            <div>
              <p className="text-sm font-semibold text-white">Cancellation Window</p>
              <p className="mt-1 text-xs text-slate-400 leading-relaxed">Cancellations are only permitted within a strict <span className="font-semibold text-indigo-400">24-hour</span> window after the initial enrollment.</p>
            </div>
          </div>
        </div>
      </aside>
    </div>
  )
}
