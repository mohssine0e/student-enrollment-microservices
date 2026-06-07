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
      setMessage(`Enrollment created with id ${enrollment.id}.`)
      setCnie('')
    } catch (err) {
      setError(err.message)
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <div className="grid gap-6 lg:grid-cols-[1fr_320px]">
      <form className="space-y-5" onSubmit={handleSubmit}>
        <div>
          <label className="mb-2 block text-sm font-medium text-slate-700" htmlFor="cnie">
            Student CNIE
          </label>
          <input
            id="cnie"
            value={cnie}
            onChange={(event) => setCnie(event.target.value)}
            className="w-full rounded-md border border-slate-300 px-3 py-2 outline-none focus:border-slate-900 focus:ring-2 focus:ring-slate-200"
            placeholder="Example: E2E-CNIE-001"
            required
          />
        </div>

        <div>
          <label className="mb-2 block text-sm font-medium text-slate-700" htmlFor="courseId">
            Course
          </label>
          <select
            id="courseId"
            value={courseId}
            onChange={(event) => setCourseId(event.target.value)}
            className="w-full rounded-md border border-slate-300 px-3 py-2 outline-none focus:border-slate-900 focus:ring-2 focus:ring-slate-200"
            disabled={loadingCourses || courses.length === 0}
            required
          >
            {courses.map((course) => (
              <option key={course.id} value={course.id}>
                {course.title}
              </option>
            ))}
          </select>
        </div>

        <button
          type="submit"
          disabled={submitting || loadingCourses || courses.length === 0}
          className="rounded-md bg-slate-900 px-4 py-2 font-medium text-white disabled:cursor-not-allowed disabled:bg-slate-400"
        >
          {submitting ? 'Enrolling...' : 'Enroll Student'}
        </button>

        {message && (
          <Alert type="success">{message}</Alert>
        )}
        {error && (
          <Alert type="error">{error}</Alert>
        )}
      </form>

      <aside className="rounded-lg border border-slate-200 bg-slate-50 p-4 text-sm leading-6 text-slate-600">
        <p className="font-medium text-slate-800">Enrollment rule summary</p>
        <p className="mt-2">The backend verifies the student by CNIE and verifies the course by id.</p>
        <p className="mt-2">A course accepts a maximum of three students.</p>
      </aside>
    </div>
  )
}
