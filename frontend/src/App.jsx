import { useEffect, useMemo, useState } from 'react'
import { Layout } from './components/Layout'
import { CourseListPage } from './pages/CourseListPage'
import { DashboardPage } from './pages/DashboardPage'
import { EnrollmentPage } from './pages/EnrollmentPage'
import './App.css'

const routes = [
  { path: 'courses', label: 'Courses', title: 'Course Listing', component: CourseListPage },
  { path: 'enroll', label: 'Enroll', title: 'Student Enrollment', component: EnrollmentPage },
  { path: 'dashboard', label: 'Dashboard', title: 'Student Dashboard', component: DashboardPage },
]

function getRouteFromHash() {
  const route = window.location.hash.replace('#/', '')
  return routes.some((item) => item.path === route) ? route : 'courses'
}

function App() {
  const [activeRoute, setActiveRoute] = useState(getRouteFromHash)

  useEffect(() => {
    const handleHashChange = () => setActiveRoute(getRouteFromHash())
    window.addEventListener('hashchange', handleHashChange)

    if (!window.location.hash) {
      window.location.hash = '/courses'
    }

    return () => window.removeEventListener('hashchange', handleHashChange)
  }, [])

  const currentRoute = useMemo(
    () => routes.find((route) => route.path === activeRoute) ?? routes[0],
    [activeRoute],
  )
  const Page = currentRoute.component

  return (
    <Layout routes={routes} activeRoute={activeRoute} title={currentRoute.title}>
      {Page ? (
        <Page />
      ) : (
        <p className="text-slate-600">
          {currentRoute.title} page content will be implemented in the next frontend tasks.
        </p>
      )}
    </Layout>
  )
}

export default App
