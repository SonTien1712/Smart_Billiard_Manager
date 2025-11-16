import React, { createContext, useContext, useState, useEffect } from 'react';
import { authService } from '../services/authService';

const AuthContext = createContext(undefined);

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null);
  const [CUSTOMER_ID, setCustomerId] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    initializeAuth();
  }, []);

  const refreshUser = async () => {
    try {
      const profile = await authService.getProfile();

      console.log('Updated user:', profile);
      setUser(profile);
      setCustomerId(profile?.customerId || profile?.id || null);
      authService.setCurrentUser(profile);

      return profile;
    } catch (error) {
      console.error('Refresh user failed:', error);
    }
  };

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
          setCustomerId(profile?.customerId || profile?.id || null);
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
        name: authResponse?.user?.name ?? authResponse?.user?.username ?? authResponse?.user?.customerName ?? '',
        phone: authResponse?.user?.phone ?? authResponse?.user?.customerPhone ?? authResponse?.user?.phoneNumber ?? '',
        role: serverRole || derivedRole, // ADMIN | STAFF | CUSTOMER
      };
      setUser(normalizedUser);
      setCustomerId(normalizedUser?.customerId || normalizedUser?.id || null); // ✅ Set CUSTOMER_IDauthService.setCurrentUser(normalizedUser);

        console.log('🔍 Login success - CUSTOMER_ID:', normalizedUser?.customerId || normalizedUser?.id);

        return authResponse; // Return the response for SignIn component to use

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
      setCustomerId(null); // ✅ Clear CUSTOMER_ID

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

      setCustomerId(updatedUser?.customerId || updatedUser?.id || null); // ✅ Update CUSTOMER_ID

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

  const handlePayment = async (planId, price) => {
    const amount = price.replace(/\D/g, '');  // Chuyển '499.000đ' thành '499000'
    try {
        const response = await fetch('http://localhost:8080/api/payment/create', {
            method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            body: new URLSearchParams({ amount, customerId: user.id, planId })
        });
        if (response.ok) {
            const payUrl = await response.text();
            window.location.href = payUrl;  // Redirect đến Momo
        } else {
            alert('Lỗi tạo thanh toán: ' + response.statusText);
        }
    } catch (error) {
        alert('Lỗi: ' + error.message);
    }
  };

  return (
    <AuthContext.Provider value={{
      user,
        CUSTOMER_ID,
      loading,
      login,
      register,
      loginWithGoogle,
      logout,
      updateProfile,
      forgotPassword,
      resetPassword,
      handlePayment,
      refreshUser   
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
