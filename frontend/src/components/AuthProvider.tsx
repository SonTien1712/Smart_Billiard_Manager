import React, { createContext, useContext, useState, useEffect, ReactNode } from 'react';
// @ts-ignore
import { authService } from '../services/authService.js';
// @ts-ignore
import { User, LoginRequest, RegisterRequest, GoogleAuthRequest } from '../types';

export type UserRole = 'ADMIN' | 'CUSTOMER' | 'STAFF';

interface AuthContextType {
  user: User | null;
  loading: boolean;
  login: (credentials: LoginRequest) => Promise<void>;
  register: (userData: RegisterRequest) => Promise<void>;
  loginWithGoogle: (googleData: GoogleAuthRequest) => Promise<void>;
  logout: () => Promise<void>;
  updateProfile: (userData: Partial<User>) => Promise<void>;
  forgotPassword: (email: string) => Promise<void>;
  resetPassword: (token: string, newPassword: string) => Promise<void>;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<User | null>(null);
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

  const login = async (credentials: LoginRequest) => {
    try {
      setLoading(true);
      const authResponse = await authService.login(credentials);
      setUser(authResponse.user);
      authService.setCurrentUser(authResponse.user);
    } catch (error) {
      console.error('Login failed:', error);
      throw error;
    } finally {
      setLoading(false);
    }
  };

  const register = async (userData: RegisterRequest) => {
    try {
      setLoading(true);
      const authResponse = await authService.register(userData);
      setUser(authResponse.user);
      authService.setCurrentUser(authResponse.user);
    } catch (error) {
      console.error('Registration failed:', error);
      throw error;
    } finally {
      setLoading(false);
    }
  };

  const loginWithGoogle = async (googleData: GoogleAuthRequest) => {
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

  const updateProfile = async (userData: Partial<User>) => {
    try {
      const updatedUser = await authService.updateProfile(userData);
      setUser(updatedUser);
      authService.setCurrentUser(updatedUser);
    } catch (error) {
      console.error('Profile update failed:', error);
      throw error;
    }
  };

  const forgotPassword = async (email: string) => {
    try {
      await authService.forgotPassword(email);
    } catch (error) {
      console.error('Forgot password failed:', error);
      throw error;
    }
  };

  const resetPassword = async (token: string, newPassword: string) => {
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