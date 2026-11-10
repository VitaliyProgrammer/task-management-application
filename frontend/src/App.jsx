import { lazy, Suspense } from 'react';
import { Navigate, Route, Routes } from 'react-router-dom';
import { GuestOnly, RequireAdmin, RequireAuth } from './auth/guards';
import { AppLayout } from './components/layout/AppLayout';
import { LoadingState } from './components/States';

// Code splitting: each page is downloaded only when the user opens it,
// so the first screen loads faster. Pages use named exports, hence the .then().
const page = (loader, name) => lazy(() => loader().then((module) => ({ default: module[name] })));

const LandingPage = page(() => import('./pages/landing/LandingPage'), 'LandingPage');
const LoginPage = page(() => import('./pages/auth/LoginPage'), 'LoginPage');
const RegisterPage = page(() => import('./pages/auth/RegisterPage'), 'RegisterPage');
const DashboardPage = page(() => import('./pages/dashboard/DashboardPage'), 'DashboardPage');
const BoardPage = page(() => import('./pages/tasks/BoardPage'), 'BoardPage');
const TaskListPage = page(() => import('./pages/tasks/TaskListPage'), 'TaskListPage');
const TaskDetailsPage = page(() => import('./pages/tasks/TaskDetailsPage'), 'TaskDetailsPage');
const ProjectsPage = page(() => import('./pages/projects/ProjectsPage'), 'ProjectsPage');
const LabelsPage = page(() => import('./pages/labels/LabelsPage'), 'LabelsPage');
const UsersPage = page(() => import('./pages/users/UsersPage'), 'UsersPage');
const ProfilePage = page(() => import('./pages/profile/ProfilePage'), 'ProfilePage');
const NotFoundPage = page(() => import('./pages/NotFoundPage'), 'NotFoundPage');

export function App() {
  return (
    <Suspense fallback={<LoadingState fullScreen />}>
      <Routes>
        {/* Public */}
        <Route path="/" element={<LandingPage />} />

        <Route element={<GuestOnly />}>
          <Route path="/login" element={<LoginPage />} />
          <Route path="/register" element={<RegisterPage />} />
        </Route>

        {/* Logged-in users */}
        <Route element={<RequireAuth />}>
          <Route element={<AppLayout />}>
            <Route path="/dashboard" element={<DashboardPage />} />
            <Route path="/tasks" element={<BoardPage />} />
            <Route path="/tasks/list" element={<TaskListPage />} />
            <Route path="/tasks/:taskId" element={<TaskDetailsPage />} />
            <Route path="/projects" element={<ProjectsPage />} />
            <Route path="/labels" element={<LabelsPage />} />
            <Route path="/profile" element={<ProfilePage />} />

            {/* Administrators only */}
            <Route element={<RequireAdmin />}>
              <Route path="/users" element={<UsersPage />} />
            </Route>
          </Route>
        </Route>

        <Route path="/home" element={<Navigate to="/" replace />} />
        <Route path="*" element={<NotFoundPage />} />
      </Routes>
    </Suspense>
  );
}
