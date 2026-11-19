import { createContext, useContext, useState, useCallback } from 'react';
import api from '../api/axios';

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [user, setUser] = useState(() => {
    try { return JSON.parse(localStorage.getItem('bugforge_user')); }
    catch { return null; }
  });

  const login = useCallback(async (mail, password) => {
    // 1. Get the JWT token string from the backend
    const { data: token } = await api.post('/login', { mail, password });

    // 2. Persist token BEFORE fetching profile so the interceptor can pick it up
    localStorage.setItem('bugforge_token', token);

    // 3. Fetch profile — pass token explicitly too, in case interceptor hasn't
    //    re-read localStorage yet (micro-task timing in some browsers)
    const { data: profile } = await api.get('/user', {
      headers: { Authorization: `Bearer ${token}` },
    });

    localStorage.setItem('bugforge_user', JSON.stringify(profile));
    setUser(profile);
    return profile;
  }, []);

  const register = useCallback(async (username, mail, password) => {
    await api.post('/registeruser', { username, mail, password });
  }, []);

  const logout = useCallback(() => {
    localStorage.removeItem('bugforge_token');
    localStorage.removeItem('bugforge_user');
    setUser(null);
  }, []);

  const refreshUser = useCallback(async () => {
    const { data: profile } = await api.get('/user');
    localStorage.setItem('bugforge_user', JSON.stringify(profile));
    setUser(profile);
    return profile;
  }, []);

  return (
    <AuthContext.Provider value={{ user, login, register, logout, refreshUser }}>
      {children}
    </AuthContext.Provider>
  );
}

export const useAuth = () => useContext(AuthContext);
