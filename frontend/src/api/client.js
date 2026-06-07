import { API_BASE_URL } from '../config'

async function request(path, options = {}) {
  const response = await fetch(`${API_BASE_URL}${path}`, {
    headers: {
      'Content-Type': 'application/json',
      ...options.headers,
    },
    ...options,
  })

  if (response.status === 204) {
    return null
  }

  const text = await response.text()
  const data = text ? JSON.parse(text) : null

  if (!response.ok) {
    const message = data?.message ?? `Request failed with status ${response.status}`
    throw new Error(message)
  }

  return data
}

export function getCourses() {
  return request('/courses')
}

export function createEnrollment(payload) {
  return request('/enrollments', {
    method: 'POST',
    body: JSON.stringify(payload),
  })
}

export function getDashboard(cnie) {
  return request(`/enrollments/dashboard/${encodeURIComponent(cnie)}`)
}

export function cancelEnrollment(enrollmentId) {
  return request(`/enrollments/${enrollmentId}`, {
    method: 'DELETE',
  })
}
