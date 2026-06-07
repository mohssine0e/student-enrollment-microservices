import { useEffect, useState } from 'react'
import { getCourses } from '../api/client'

export function CourseListPage() {
  const [courses, setCourses] = useState([])
  const [status, setStatus] = useState('loading')
  const [error, setError] = useState('')

  useEffect(() => {
    let ignore = false

    getCourses()
      .then((data) => {
        if (!ignore) {
          setCourses(data)
          setStatus('success')
        }
      })
      .catch((err) => {
        if (!ignore) {
          setError(err.message)
          setStatus('error')
        }
      })

    return () => {
      ignore = true
    }
  }, [])

  if (status === 'loading') {
    return <p className="text-slate-600">Loading courses...</p>
  }

  if (status === 'error') {
    return (
      <div className="rounded-md border border-red-200 bg-red-50 p-4 text-red-700">
        {error}
      </div>
    )
  }

  return (
    <div className="space-y-4">
      {courses.length === 0 ? (
        <p className="text-slate-600">No courses are available yet.</p>
      ) : (
        <div className="grid gap-4 md:grid-cols-2">
          {courses.map((course) => (
            <article
              key={course.id}
              className="rounded-lg border border-slate-200 bg-slate-50 p-4"
            >
              <div className="mb-2 flex items-center justify-between gap-3">
                <h2 className="text-lg font-semibold text-slate-900">{course.title}</h2>
                <span className="rounded bg-white px-2 py-1 text-xs font-medium text-slate-500 ring-1 ring-slate-200">
                  ID {course.id}
                </span>
              </div>
              <p className="text-sm leading-6 text-slate-600">{course.description}</p>
            </article>
          ))}
        </div>
      )}
    </div>
  )
}
