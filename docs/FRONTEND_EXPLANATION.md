# Frontend Explanation

## Purpose

The frontend is a React application for using the student enrollment backend from a browser. It gives simple screens for listing courses, enrolling a student, and viewing a student's dashboard.

The frontend communicates only with the API Gateway at `http://localhost:8080`. It does not call Student Service, Course Service, or Enrollment Service directly.

## Technology

- React: builds the user interface with components.
- Vite: runs the development server and builds the frontend.
- TailwindCSS: provides utility classes for styling.
- Fetch API: sends HTTP requests to the backend through the API Gateway.

## Folder Structure

```text
frontend/
  src/
    api/
      client.js
    components/
      Alert.jsx
      Layout.jsx
      LoadingMessage.jsx
      Navigation.jsx
    pages/
      CourseListPage.jsx
      DashboardPage.jsx
      EnrollmentPage.jsx
    App.jsx
    config.js
    index.css
    main.jsx
```

## React Structure

`main.jsx` starts the React application and renders `App`.

`App.jsx` controls which page is displayed. It uses hash-based routing:

- `#/courses`
- `#/enroll`
- `#/dashboard`

This avoids adding an extra routing dependency and keeps the frontend simple for an academic project.

## Components

### Layout

`Layout.jsx` defines the common page structure:

- page title
- navigation
- white content panel

### Navigation

`Navigation.jsx` displays links between the three pages.

### Alert

`Alert.jsx` displays reusable success, error, and information messages.

### LoadingMessage

`LoadingMessage.jsx` displays a consistent loading message.

## Pages

### CourseListPage

This page calls `GET /courses` through the API Gateway and displays all courses.

### EnrollmentPage

This page lets the user enter:

- student CNIE
- course id selected from available courses

It sends a request to `POST /enrollments` through the API Gateway.

### DashboardPage

This page lets the user enter a CNIE and calls:

```text
GET /enrollments/dashboard/{cnie}
```

It displays enrolled courses, course titles, enrollment ids, and `canCancel`. If `canCancel` is true, the page allows cancellation by calling:

```text
DELETE /enrollments/{id}
```

## API Calls

All API calls are in `src/api/client.js`.

The base URL comes from:

```text
VITE_API_BASE_URL
```

If the variable is not set, the frontend uses:

```text
http://localhost:8080
```

This means all requests go through the API Gateway.

## Tailwind Usage

TailwindCSS is configured in `vite.config.js` with `@tailwindcss/vite`.

The main CSS file imports Tailwind:

```css
@import "tailwindcss";
```

Most styling is done directly in JSX using Tailwind utility classes such as:

- `rounded-md`
- `bg-slate-900`
- `text-slate-600`
- `grid`
- `shadow-sm`

## How The Frontend Communicates With The Backend

The frontend sends requests to the API Gateway:

```text
Frontend -> API Gateway -> Backend Service
```

Examples:

- `GET /courses` goes through the gateway to Course Service.
- `POST /enrollments` goes through the gateway to Enrollment Service.
- `GET /enrollments/dashboard/{cnie}` goes through the gateway to Enrollment Service.

This keeps the frontend simple because it only needs one backend URL.

## Simple Summary

The frontend is a small React app with three pages. It uses Tailwind for styling and calls only the API Gateway. The Gateway then routes requests to the correct backend service.
