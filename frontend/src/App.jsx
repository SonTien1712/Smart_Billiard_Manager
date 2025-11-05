import React from 'react';
import { AuthProvider } from './components/AuthProvider.jsx';
import AppRouter from './routes/routes.jsx';

export default function AppWrapper() {
  return (
    <AuthProvider>
      <AppRouter />
    </AuthProvider>
  );
}
