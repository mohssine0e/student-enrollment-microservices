import { useEffect, useState } from 'react'
import { getStudents, createStudent, deleteStudent } from '../api/client'
import { Alert } from '../components/Alert'
import { LoadingMessage } from '../components/LoadingMessage'

export function StudentListPage() {
  const [students, setStudents] = useState([])
  const [status, setStatus] = useState('loading')
  const [error, setError] = useState('')

  // Form State
  const [isFormOpen, setIsFormOpen] = useState(false)
  const [submitting, setSubmitting] = useState(false)
  const [formError, setFormError] = useState('')
  const [formSuccess, setFormSuccess] = useState('')
  const [copiedCnie, setCopiedCnie] = useState(null)
  const [formData, setFormData] = useState({
    cnie: '',
    firstName: '',
    lastName: '',
    email: ''
  })

  useEffect(() => {
    fetchStudents()
  }, [])

  function fetchStudents() {
    setStatus('loading')
    getStudents()
      .then((data) => {
        setStudents(data)
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

  async function handleCreateStudent(e) {
    e.preventDefault()
    setSubmitting(true)
    setFormError('')
    setFormSuccess('')

    try {
      await createStudent({
        cnie: formData.cnie.trim(),
        firstName: formData.firstName.trim(),
        lastName: formData.lastName.trim(),
        email: formData.email.trim()
      })
      setFormSuccess('Student successfully created!')
      setFormData({ cnie: '', firstName: '', lastName: '', email: '' })
      setIsFormOpen(false)
      fetchStudents()
    } catch (err) {
      setFormError(err.message)
    } finally {
      setSubmitting(false)
    }
  }

  async function handleDeleteStudent(id) {
    if (!window.confirm('Are you sure you want to delete this student?')) return
    
    try {
      await deleteStudent(id)
      fetchStudents()
    } catch (err) {
      alert(`Failed to delete student: ${err.message}`)
    }
  }

  function handleCopyCnie(cnie) {
    navigator.clipboard.writeText(cnie).then(() => {
      setCopiedCnie(cnie)
      setTimeout(() => setCopiedCnie(null), 2000)
    })
  }

  return (
    <div className="space-y-6">
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4 mb-6">
        <div>
          <h2 className="text-xl font-bold text-white">Student Directory</h2>
          <p className="text-sm text-slate-400">Manage registered students, add new students, or remove existing ones.</p>
        </div>
        <button
          onClick={() => {
            setIsFormOpen(!isFormOpen)
            setFormSuccess('')
            setFormError('')
          }}
          className="inline-flex items-center justify-center gap-2 rounded-xl bg-indigo-600 px-4 py-2.5 text-sm font-semibold text-white shadow-sm hover:bg-indigo-500 transition-colors"
        >
          {isFormOpen ? 'Cancel' : 'Add New Student'}
          <svg className={`h-4 w-4 transition-transform ${isFormOpen ? 'rotate-45' : ''}`} viewBox="0 0 20 20" fill="currentColor">
            <path fillRule="evenodd" d="M10 3a.75.75 0 01.75.75v5.5h5.5a.75.75 0 010 1.5h-5.5v5.5a.75.75 0 01-1.5 0v-5.5h-5.5a.75.75 0 010-1.5h5.5v-5.5A.75.75 0 0110 3z" clipRule="evenodd" />
          </svg>
        </button>
      </div>

      {formSuccess && <Alert type="success">{formSuccess}</Alert>}
      
      {isFormOpen && (
        <div className="rounded-2xl border border-slate-700 bg-slate-800 p-6 shadow-sm animate-in slide-in-from-top-4 duration-300">
          <h3 className="mb-4 text-lg font-bold text-white">Register New Student</h3>
          {formError && <div className="mb-4"><Alert type="error">{formError}</Alert></div>}
          
          <form onSubmit={handleCreateStudent} className="space-y-4">
            <div className="grid gap-4 sm:grid-cols-2">
              <div>
                <label className="mb-1.5 block text-sm font-semibold text-slate-300">First Name</label>
                <input
                  name="firstName"
                  value={formData.firstName}
                  onChange={handleInputChange}
                  className="w-full rounded-xl border-0 py-2.5 px-4 bg-slate-900 text-white shadow-sm ring-1 ring-inset ring-slate-700 focus:ring-2 focus:ring-inset focus:ring-indigo-500 sm:text-sm"
                  required
                />
              </div>
              <div>
                <label className="mb-1.5 block text-sm font-semibold text-slate-300">Last Name</label>
                <input
                  name="lastName"
                  value={formData.lastName}
                  onChange={handleInputChange}
                  className="w-full rounded-xl border-0 py-2.5 px-4 bg-slate-900 text-white shadow-sm ring-1 ring-inset ring-slate-700 focus:ring-2 focus:ring-inset focus:ring-indigo-500 sm:text-sm"
                  required
                />
              </div>
              <div>
                <label className="mb-1.5 block text-sm font-semibold text-slate-300">CNIE</label>
                <input
                  name="cnie"
                  value={formData.cnie}
                  onChange={handleInputChange}
                  className="w-full rounded-xl border-0 py-2.5 px-4 bg-slate-900 text-white shadow-sm ring-1 ring-inset ring-slate-700 focus:ring-2 focus:ring-inset focus:ring-indigo-500 sm:text-sm uppercase"
                  placeholder="e.g. E2E-CNIE-001"
                  required
                />
              </div>
              <div>
                <label className="mb-1.5 block text-sm font-semibold text-slate-300">Email</label>
                <input
                  type="email"
                  name="email"
                  value={formData.email}
                  onChange={handleInputChange}
                  className="w-full rounded-xl border-0 py-2.5 px-4 bg-slate-900 text-white shadow-sm ring-1 ring-inset ring-slate-700 focus:ring-2 focus:ring-inset focus:ring-indigo-500 sm:text-sm"
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
                {submitting ? 'Creating...' : 'Create Student'}
              </button>
            </div>
          </form>
        </div>
      )}

      {status === 'loading' && !submitting && <LoadingMessage>Loading students...</LoadingMessage>}
      {status === 'error' && <Alert type="error">{error}</Alert>}
      
      {status === 'success' && (
        students.length === 0 ? (
          <Alert>No students are registered yet.</Alert>
        ) : (
          <div className="overflow-hidden rounded-2xl border border-slate-700 bg-slate-800 shadow-sm">
            <div className="overflow-x-auto">
              <table className="min-w-full divide-y divide-slate-700">
                <thead className="bg-slate-900/60">
                  <tr>
                    <th className="px-6 py-4 text-left text-xs font-semibold text-slate-400 uppercase tracking-wider">Student</th>
                    <th className="px-6 py-4 text-left text-xs font-semibold text-slate-400 uppercase tracking-wider">CNIE</th>
                    <th className="px-6 py-4 text-left text-xs font-semibold text-slate-400 uppercase tracking-wider">System ID</th>
                    <th className="px-6 py-4 text-right text-xs font-semibold text-slate-400 uppercase tracking-wider">Actions</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-slate-700 bg-slate-800">
                  {students.map((student) => (
                    <tr key={student.id} className="hover:bg-slate-700/30 transition-colors">
                      <td className="whitespace-nowrap px-6 py-4">
                        <div className="flex items-center">
                          <div className="h-10 w-10 flex-shrink-0 rounded-full bg-indigo-500/20 flex items-center justify-center text-indigo-400 font-bold">
                            {student.firstName[0]}{student.lastName[0]}
                          </div>
                          <div className="ml-4">
                            <div className="font-medium text-white">{student.firstName} {student.lastName}</div>
                            <div className="text-sm text-slate-400">{student.email}</div>
                          </div>
                        </div>
                      </td>
                      <td className="whitespace-nowrap px-6 py-4 text-sm font-medium text-white">
                        <div 
                          className="relative inline-block cursor-pointer rounded-md px-2 py-1 hover:bg-indigo-500/20 hover:text-indigo-400 transition-colors"
                          onDoubleClick={() => handleCopyCnie(student.cnie)}
                          title="Double-click to copy CNIE"
                        >
                          {student.cnie}
                          {copiedCnie === student.cnie && (
                            <div className="absolute -top-8 left-1/2 -translate-x-1/2 px-2 py-1 bg-slate-900 text-white text-xs font-semibold rounded shadow-lg animate-in fade-in slide-in-from-bottom-1 duration-200 whitespace-nowrap z-10">
                              Copied!
                              <div className="absolute -bottom-1 left-1/2 -translate-x-1/2 border-4 border-transparent border-t-slate-900"></div>
                            </div>
                          )}
                        </div>
                      </td>
                      <td className="whitespace-nowrap px-6 py-4">
                        <span className="inline-flex items-center rounded-full bg-slate-700 px-2.5 py-0.5 text-xs font-medium text-slate-300">
                          ID: {student.id}
                        </span>
                      </td>
                      <td className="whitespace-nowrap px-6 py-4 text-right text-sm font-medium">
                        <button
                          onClick={() => handleDeleteStudent(student.id)}
                          className="inline-flex items-center justify-center rounded-lg bg-rose-500/10 px-3 py-1.5 text-sm font-semibold text-rose-400 ring-1 ring-inset ring-rose-500/20 hover:bg-rose-500/20 hover:text-rose-300 transition-colors"
                        >
                          Delete
                        </button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>
        )
      )}
    </div>
  )
}
