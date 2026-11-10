import { createContext, useCallback, useContext, useEffect, useMemo, useState } from 'react';
import { useQuery, useQueryClient } from '@tanstack/react-query';
import { LOGOUT_EVENT } from '../api/client';
import { tokenStorage } from '../api/tokenStorage';
import { authApi, profileApi } from '../api/endpoints';
import { isAdmin } from '../lib/people';

const AuthContext = createContext(null);

export const PROFILE_QUERY_KEY = ['profile'];

export function AuthProvider({ children }) {
  const queryClient = useQueryClient();

  // The token is kept in React state as well, so login/logout re-render the app.
  const [token, setToken] = useState(() => tokenStorage.get());

  // The profile tells us who is logged in and which roles they have.
  const profileQuery = useQuery({
    queryKey: PROFILE_QUERY_KEY,
    queryFn: profileApi.get,
    enabled: Boolean(token),
    retry: false,
  });

  const logout = useCallback(() => {
    tokenStorage.clear();
    setToken(null);
    queryClient.clear();
  }, [queryClient]);

  const login = useCallback(async (credentials) => {
    const response = await authApi.login(credentials);

    tokenStorage.set(response.token);
    setToken(response.token);
  }, []);

  // The API client fires this event on any 401 response.
  useEffect(() => {
    window.addEventListener(LOGOUT_EVENT, logout);

    return () => window.removeEventListener(LOGOUT_EVENT, logout);
  }, [logout]);

  const user = token ? profileQuery.data : undefined;

  const value = useMemo(
    () => ({
      user,
      isAdmin: isAdmin(user),
      isAuthenticated: Boolean(user),
      isLoading: Boolean(token) && profileQuery.isPending,
      login,
      logout,
    }),
    [user, token, profileQuery.isPending, login, logout],
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

// eslint-disable-next-line react-refresh/only-export-components
export function useAuth() {
  const context = useContext(AuthContext);

  if (!context) {
    throw new Error('useAuth must be used inside <AuthProvider>');
  }

  return context;
}
