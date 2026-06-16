import { useEffect, useState } from 'react'
import { getCourses, createCourse, deleteCourse } from '../api/client'
import { Alert } from '../components/Alert'
import { LoadingMessage } from '../components/LoadingMessage'

export function CourseListPage() {
  const [courses, setCourses] = useState([])
  const [status, setStatus] = useState('loading')
  const [error, setError] = useState('')

  // Form State
  const [isFormOpen, setIsFormOpen] = useState(false)
  const [submitting, setSubmitting] = useState(false)
  const [formError, setFormError] = useState('')
  const [formSuccess, setFormSuccess] = useState('')
  const [formData, setFormData] = useState({
    title: '',
    description: ''
  })

  useEffect(() => {
    fetchCourses()
  }, [])

  function fetchCourses() {
    setStatus('loading')
    getCourses()
      .then((data) => {
        setCourses(data)
        setStatus('success')
      })
      .catch((err) => {
        setError(err.message)
        setStatus('error')
      })
  }

  function handleInputChange(e) {
    const { name, value } = e.target
    setFormData(prev => ({ ...prev, [name]: value }))
  }

  async function handleCreateCourse(e) {
    e.preventDefault()
    setSubmitting(true)
    setFormError('')
    setFormSuccess('')

    try {
      await createCourse({
        title: formData.title.trim(),
        description: formData.description.trim()
      })
      setFormSuccess('Course successfully created!')
      setFormData({ title: '', description: '' })
      setIsFormOpen(false)
      fetchCourses()
    } catch (err) {
      setFormError(err.message)
    } finally {
      setSubmitting(false)
    }
  }

  async function handleDeleteCourse(id) {
    if (!window.confirm('Are you sure you want to delete this course? Note: this may fail if students are enrolled.')) return
    
    try {
      await deleteCourse(id)
      fetchCourses()
    } catch (err) {
      alert(`Failed to delete course: ${err.message}`)
    }
  }

  return (
    <div className="space-y-6">
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4 mb-6">
        <div>
          <h2 className="text-xl font-bold text-white">Available Courses</h2>
          <p className="text-sm text-slate-400">Browse the catalog of courses currently open for enrollment.</p>
        </div>
        <button
          onClick={() => {
            setIsFormOpen(!isFormOpen)
            setFormSuccess('')
            setFormError('')
          }}
          className="inline-flex items-center justify-center gap-2 rounded-xl bg-indigo-600 px-4 py-2.5 text-sm font-semibold text-white shadow-sm hover:bg-indigo-500 transition-colors"
        >
          {isFormOpen ? 'Cancel' : 'Add New Course'}
          <svg className={`h-4 w-4 transition-transform ${isFormOpen ? 'rotate-45' : ''}`} viewBox="0 0 20 20" fill="currentColor">
            <path fillRule="evenodd" d="M10 3a.75.75 0 01.75.75v5.5h5.5a.75.75 0 010 1.5h-5.5v5.5a.75.75 0 01-1.5 0v-5.5h-5.5a.75.75 0 010-1.5h5.5v-5.5A.75.75 0 0110 3z" clipRule="evenodd" />
          </svg>
        </button>
      </div>

      {formSuccess && <Alert type="success">{formSuccess}</Alert>}

      {isFormOpen && (
        <div className="rounded-2xl border border-slate-700 bg-slate-800 p-6 shadow-sm animate-in slide-in-from-top-4 duration-300">
          <h3 className="mb-4 text-lg font-bold text-white">Create New Course</h3>
          {formError && <div className="mb-4"><Alert type="error">{formError}</Alert></div>}
          
          <form onSubmit={handleCreateCourse} className="space-y-4">
            <div className="grid gap-4">
              <div>
                <label className="mb-1.5 block text-sm font-semibold text-slate-300">Course Title</label>
                <input
                  name="title"
                  value={formData.title}
                  onChange={handleInputChange}
                  className="w-full rounded-xl border-0 py-2.5 px-4 bg-slate-900 text-white shadow-sm ring-1 ring-inset ring-slate-700 focus:ring-2 focus:ring-inset focus:ring-indigo-500 sm:text-sm"
                  placeholder="e.g. Introduction to React"
                  required
                />
              </div>
              <div>
                <label className="mb-1.5 block text-sm font-semibold text-slate-300">Description</label>
                <textarea
                  name="description"
                  value={formData.description}
                  onChange={handleInputChange}
                  rows={3}
                  className="w-full rounded-xl border-0 py-2.5 px-4 bg-slate-900 text-white shadow-sm ring-1 ring-inset ring-slate-700 focus:ring-2 focus:ring-inset focus:ring-indigo-500 sm:text-sm resize-none"
                  placeholder="Course details..."
                  required
                />
              </div>
            </div>
            <div className="flex justify-end pt-2">
              <button
                type="submit"
                disabled={submitting}
                className="rounded-xl bg-indigo-600 px-6 py-2.5 text-sm font-semibold text-white shadow-sm hover:bg-indigo-500 disabled:opacity-50"
              >
                {submitting ? 'Creating...' : 'Create Course'}
              </button>
            </div>
          </form>
        </div>
      )}

      {status === 'loading' && !submitting && <LoadingMessage>Loading courses...</LoadingMessage>}
      {status === 'error' && <Alert type="error">{error}</Alert>}

      {status === 'success' && (
        courses.length === 0 ? (
          <Alert>No courses are available yet.</Alert>
        ) : (
          <div className="grid gap-6 md:grid-cols-2 lg:grid-cols-3">
            {courses.map((course) => (
              <article
                key={course.id}
                className="group relative flex flex-col justify-between overflow-hidden rounded-2xl bg-slate-800 p-6 shadow-sm ring-1 ring-slate-700/50 transition-all duration-300 hover:-translate-y-1 hover:shadow-xl hover:shadow-indigo-900/20 hover:ring-indigo-500/50"
              >
                <div className="absolute -right-6 -top-6 h-24 w-24 rounded-full bg-indigo-500 opacity-0 blur-2xl transition-opacity duration-300 group-hover:opacity-10"></div>
                
                <div className="relative z-10">
                  <div className="mb-4 flex items-center justify-between">
                    <span className="inline-flex items-center gap-1.5 rounded-full bg-indigo-500/10 px-3 py-1 text-xs font-semibold text-indigo-400 ring-1 ring-inset ring-indigo-500/20">
                      <svg className="h-3.5 w-3.5" viewBox="0 0 20 20" fill="currentColor">
                        <path fillRule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm.75-11.25a.75.75 0 00-1.5 0v2.5h-2.5a.75.75 0 000 1.5h2.5v2.5a.75.75 0 001.5 0v-2.5h2.5a.75.75 0 000-1.5h-2.5v-2.5z" clipRule="evenodd" />
                      </svg>
                      ID {course.id}
                    </span>
                    <button
                      onClick={() => handleDeleteCourse(course.id)}
                      className="rounded-full p-1.5 text-slate-500 hover:bg-rose-500/10 hover:text-rose-400 transition-colors"
                      title="Delete course"
                    >
                      <svg className="h-4 w-4" viewBox="0 0 20 20" fill="currentColor">
                        <path fillRule="evenodd" d="M8.75 1A2.75 2.75 0 006 3.75v.443c-.795.077-1.584.176-2.365.298a.75.75 0 10.23 1.482l.149-.022.841 10.518A2.75 2.75 0 007.596 19h4.807a2.75 2.75 0 002.742-2.53l.841-10.52.149.023a.75.75 0 00.23-1.482A41.03 41.03 0 0014 4.193V3.75A2.75 2.75 0 0011.25 1h-2.5zM10 4c.84 0 1.673.025 2.5.075V3.75c0-.69-.56-1.25-1.25-1.25h-2.5c-.69 0-1.25.56-1.25 1.25v.325C8.327 4.025 9.16 4 10 4zM8.58 7.72a.75.75 0 00-1.5.06l.3 7.5a.75.75 0 101.5-.06l-.3-7.5zm4.34.06a.75.75 0 10-1.5-.06l-.3 7.5a.75.75 0 101.5.06l.3-7.5z" clipRule="evenodd" />
                      </svg>
                    </button>
                  </div>
                  <h3 className="mb-3 text-xl font-extrabold tracking-tight text-white group-hover:text-indigo-400 transition-colors">
                    {course.title}
                  </h3>
                  <p className="text-sm leading-relaxed text-slate-400 line-clamp-3">
                    {course.description}
                  </p>
                </div>
                
                <div className="relative z-10 mt-6 pt-4 border-t border-slate-700">
                  <a href="#/enroll" className="inline-flex w-full items-center justify-center gap-2 rounded-xl bg-slate-700 px-4 py-2.5 text-sm font-semibold text-white transition-colors group-hover:bg-indigo-600 group-hover:text-white">
                    Enroll Now
                    <svg className="h-4 w-4 transition-transform group-hover:translate-x-1" viewBox="0 0 20 20" fill="currentColor">
                      <path fillRule="evenodd" d="M3 10a.75.75 0 01.75-.75h10.638L10.23 5.29a.75.75 0 111.04-1.08l5.5 5.25a.75.75 0 010 1.08l-5.5 5.25a.75.75 0 11-1.04-1.08l4.158-3.96H3.75A.75.75 0 013 10z" clipRule="evenodd" />
                    </svg>
                  </a>
                </div>
              </article>
            ))}
          </div>
        )
      )}
    </div>
  )
}
