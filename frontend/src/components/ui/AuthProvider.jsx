import React, { createContext, useContext, useState } from 'react';

const AuthContext = createContext();

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null);

  const login = async (email, password) => {
    // Mock authentication - in real app, this would call an API
    const mockUsers = {
      'admin@billard.com': { id: '1', name: 'Admin User', email: 'admin@billard.com', role: 'admin' },
      'customer@billard.com': { id: '2', name: 'Club Owner', email: 'customer@billard.com', role: 'customer' },
      'staff@billard.com': { id: '3', name: 'Staff Member', email: 'staff@billard.com', role: 'staff' }
    };

    const foundUser = mockUsers[email];
    if (foundUser) {
      setUser(foundUser);
    } else {
      throw new Error('Invalid credentials');
    }
  };

  const loginWithGoogle = async () => {
    // Mock Google authentication - in real app, this would integrate with Google OAuth
    return new Promise((resolve) => {
      setTimeout(() => {
        const mockGoogleUser = {
          id: 'google_' + Math.random().toString(36).substr(2, 9),
          name: 'Google User',
          email: 'user@gmail.com',
          role: 'customer',
          avatar: 'https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=400&h=400&fit=crop&crop=face'
        };
        setUser(mockGoogleUser);
        resolve();
      }, 1500);
    });
  };

  const logout = () => {
    setUser(null);
  };

  const updateProfile = (userData) => {
    if (user) {
      setUser({ ...user, ...userData });
    }
  };

  return (
    <AuthContext.Provider value={{ user, login, loginWithGoogle, logout, updateProfile }}>
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
