import { Suspense } from 'react';
import { Outlet } from 'react-router-dom';
import { LoadingState } from '../States';
import { Sidebar } from './Sidebar';

// Every page after login: sidebar on the left, the page on the right.
// The Suspense here keeps the sidebar visible while the next page is loading.
export function AppLayout() {
  return (
    <div style={{ display: 'flex', flexWrap: 'wrap', minHeight: '100vh' }}>
      <Sidebar />

      <Suspense
        fallback={
          <main className="page">
            <LoadingState />
          </main>
        }
      >
        <Outlet />
      </Suspense>
    </div>
  );
}
