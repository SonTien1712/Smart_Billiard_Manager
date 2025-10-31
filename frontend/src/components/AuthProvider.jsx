import React, { createContext, useContext, useState, useEffect } from 'react';
import { authService } from '../services/authService';

const AuthContext = createContext(undefined);

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    initializeAuth();
  }, []);

  const initializeAuth = async () => {
    try {
      if (authService.isAuthenticated()) {
        const currentUser = authService.getCurrentUser();
        if (currentUser) {
          setUser(currentUser);
        } else {
          // Try to fetch user profile from server
          const profile = await authService.getProfile();
          setUser(profile);
          authService.setCurrentUser(profile);
        }
      }
    } catch (error) {
      console.error('Auth initialization failed:', error);
      // Clear invalid tokens
      authService.removeToken();
      authService.removeCurrentUser();
    } finally {
      setLoading(false);
    }
  };

  const getRoleFromToken = (token) => {
    if (!token) return undefined;
    if (token.includes('ADMIN')) return 'ADMIN';
    if (token.includes('STAFF')) return 'STAFF';
    if (token.includes('CUSTOMER')) return 'CUSTOMER';
    return undefined;
  };

  const login = async (credentials) => {
    try {
      setLoading(true);
      const authResponse = await authService.login(credentials);

      // lấy & lưu token (để isAuthenticated() hoạt động sau reload)
      const token = authResponse?.accessToken || '';
      localStorage.setItem('accessToken', token);

      // Ưu tiên role từ server (nếu có), sau đó mới suy ra từ token
      const serverRole = authResponse?.user?.role;
      const derivedRole =
        getRoleFromToken(token)
        || (/\badmin\b/i.test(authResponse?.message || '') ? 'ADMIN' : undefined)
        || 'CUSTOMER';

      const normalizedUser = {
        ...(authResponse?.user || {}),
        role: serverRole || derivedRole, // ADMIN | STAFF | CUSTOMER
      };
      setUser(normalizedUser);
      authService.setCurrentUser(normalizedUser);

    } catch (error) {
      console.error('Login failed:', error);
      throw error;
    } finally {
      setLoading(false);
    }
  };

  const register = async (userData) => {
    try {
      setLoading(true);
      const authResponse = await authService.register(userData);
      return authResponse;
    } catch (error) {
      console.error('Registration failed:', error);
      return { success: false, message: 'Đăng ký thất bại' };
    } finally {
      setLoading(false);
    }
  };

  const loginWithGoogle = async (googleData) => {
    try {
      setLoading(true);
      const authResponse = await authService.googleAuth(googleData);
      setUser(authResponse.user);
      authService.setCurrentUser(authResponse.user);
    } catch (error) {
      console.error('Google login failed:', error);
      throw error;
    } finally {
      setLoading(false);
    }
  };

  const logout = async () => {
    try {
      setLoading(true);
      await authService.logout();
      setUser(null);
      authService.removeCurrentUser();
      authService.removeToken();
    } catch (error) {
      console.error('Logout failed:', error);
    } finally {
      setLoading(false);
    }
  };

  const updateProfile = async (userData) => {
    try {
      const updatedUser = await authService.updateProfile(userData);
      setUser(updatedUser);
      authService.setCurrentUser(updatedUser);
    } catch (error) {
      console.error('Profile update failed:', error);
      throw error;
    }
  };

  const forgotPassword = async (email) => {
    try {
      await authService.forgotPassword(email);
    } catch (error) {
      console.error('Forgot password failed:', error);
      throw error;
    }
  };

  const resetPassword = async (token, newPassword) => {
    try {
      await authService.resetPassword(token, newPassword);
    } catch (error) {
      console.error('Reset password failed:', error);
      throw error;
    }
  };

  return (
    <AuthContext.Provider value={{
      user,
      loading,
      login,
      register,
      loginWithGoogle,
      logout,
      updateProfile,
      forgotPassword,
      resetPassword
    }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (context === undefined) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
}
