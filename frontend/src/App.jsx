import React, { useState } from 'react';
import { Dashboard } from './components/Dashboard.jsx';
import { AuthProvider, useAuth } from './components/AuthProvider.jsx';
import { SignIn } from './components/auth/SignIn.jsx';
import { SignUp } from './components/auth/SignUp.jsx';
import { ForgotPassword } from './components/auth/ForgotPassword.jsx';
import { ProfileUpdate } from './components/auth/ProfileUpdate.jsx';
import { Toaster } from './components/ui/sonner.jsx';

function LoadingSpinner() {
  return (
    <div className="min-h-screen flex items-center justify-center bg-background">
      <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-primary"></div>
    </div>
  );
}

function App() {
  const [currentPage, setCurrentPage] = useState('signin');
  const { user, loading } = useAuth();

  const handlePageChange = (page) => {
    setCurrentPage(page);
  };

  if (loading) {
    return <LoadingSpinner />;
  }

  if (user && currentPage !== 'profile') {
    return <Dashboard onNavigate={handlePageChange} />;
  }

  switch (currentPage) {
    case 'signin':
      return <SignIn onNavigate={handlePageChange} />;
    case 'signup':
      return <SignUp onNavigate={handlePageChange} />;
    case 'forgot-password':
      return <ForgotPassword onNavigate={handlePageChange} />;
    case 'profile':
      return <ProfileUpdate onNavigate={handlePageChange} />;
    default:
      return <SignIn onNavigate={handlePageChange} />;
  }
}

export default function AppWrapper() {
  return (
    <AuthProvider>
      <App />
      <Toaster />
    </AuthProvider>
  );
}