import { Outlet } from 'react-router-dom';
import { Sidebar } from './Sidebar';

// Every page after login: sidebar on the left, the page on the right.
export function AppLayout() {
  return (
    <div style={{ display: 'flex', flexWrap: 'wrap', minHeight: '100vh' }}>
      <Sidebar />
      <Outlet />
    </div>
  );
}
