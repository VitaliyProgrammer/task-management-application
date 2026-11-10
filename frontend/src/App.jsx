import { Navigate, Route, Routes } from 'react-router-dom';
import { GuestOnly, RequireAdmin, RequireAuth } from './auth/guards';
import { AppLayout } from './components/layout/AppLayout';
import { LandingPage } from './pages/landing/LandingPage';
import { LoginPage } from './pages/auth/LoginPage';
import { RegisterPage } from './pages/auth/RegisterPage';
import { DashboardPage } from './pages/dashboard/DashboardPage';
import { BoardPage } from './pages/tasks/BoardPage';
import { TaskListPage } from './pages/tasks/TaskListPage';
import { TaskDetailsPage } from './pages/tasks/TaskDetailsPage';
import { ProjectsPage } from './pages/projects/ProjectsPage';
import { LabelsPage } from './pages/labels/LabelsPage';
import { UsersPage } from './pages/users/UsersPage';
import { ProfilePage } from './pages/profile/ProfilePage';
import { NotFoundPage } from './pages/NotFoundPage';

export function App() {
  return (
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
  );
}
