import { apiClient } from './api';
import { API_CONFIG } from '../config/api';
import { MockService } from './mockService';

const USE_MOCK_DATA = false; // Set to false when you have a real backend


export class AuthService {
  
  async login(credentials) {
    if (USE_MOCK_DATA) {
      const authResponse = await MockService.login(credentials.email, credentials.password);
      apiClient.setToken(authResponse.accessToken);
      localStorage.setItem('refreshToken', authResponse.refreshToken);
      return authResponse;
    }
    
    const response = await apiClient.post(
      API_CONFIG.ENDPOINTS.AUTH.LOGIN,
      credentials
    );

    // Chuẩn hóa: nếu dùng axios thì response.data, còn không thì chính response là data
    const data = response?.data ?? response;
    
    if (data?.success) {
      apiClient.setToken(data.accessToken);
      localStorage.setItem('refreshToken', data.refreshToken || '');
    }
    
    return data;
  }
  async register(userData) {
    if (USE_MOCK_DATA) {
      const authResponse = await MockService.register(userData);
      apiClient.setToken(authResponse.accessToken);
      localStorage.setItem('refreshToken', authResponse.refreshToken);
      return authResponse;
    }
    
    const response = await apiClient.post(
      API_CONFIG.ENDPOINTS.AUTH.REGISTER,
      userData
    );

    console.log('Register response:', response);
    return response;
  }

  // authService.js
  async googleAuth(googleData) {
    if (USE_MOCK_DATA) {
      const authResponse = await MockService.googleAuth(googleData);
      apiClient.setToken(authResponse.accessToken);
      localStorage.setItem('refreshToken', authResponse.refreshToken || '');
      return authResponse;
    }

    // Ưu tiên code-flow; fallback idToken
    const payload = {
      role: googleData?.role || 'CUSTOMER',
    };
    if (googleData?.authCode) payload.code = googleData.authCode;
    if (googleData?.idToken)  payload.idToken = googleData.idToken;

    // Dùng 1 endpoint duy nhất nếu BE bạn đang là /api/auth/google
    const response = await apiClient.post(API_CONFIG.ENDPOINTS.AUTH.GOOGLE_AUTH, payload);

    const data = response?.data ?? response;
    const accessToken = data?.accessToken || data?.data?.accessToken || '';
    const refreshToken = data?.refreshToken || data?.data?.refreshToken || '';
    if (accessToken) apiClient.setToken(accessToken);
    if (refreshToken) localStorage.setItem('refreshToken', refreshToken);

    return { ...(data?.data || data) };
  }


  async logout() {
    try {
      if (USE_MOCK_DATA) {
        await MockService.logout();
      } else {
        await apiClient.post(API_CONFIG.ENDPOINTS.AUTH.LOGOUT);
      }
    } catch (error) {
      console.error('Logout error:', error);
    } finally {
      apiClient.removeToken();
    }
  }

  async forgotPassword(email) {
    if (USE_MOCK_DATA) {
      return MockService.forgotPassword(email);
    }
    
    await apiClient.post(
      API_CONFIG.ENDPOINTS.AUTH.FORGOT_PASSWORD,
      { email }
    );
  }

  async resetPassword(token, newPassword) {
    if (USE_MOCK_DATA) {
      return MockService.resetPassword(token, newPassword);
    }
    
    await apiClient.post(
      API_CONFIG.ENDPOINTS.AUTH.RESET_PASSWORD,
      { token, newPassword }
    );
  }

  async getProfile() {
    if (USE_MOCK_DATA) {
      const currentUser = this.getCurrentUser();
      if (currentUser) {
        return MockService.getProfile(currentUser.id);
      }
      throw new Error('No current user');
    }
    
    const response = await apiClient.get(
      API_CONFIG.ENDPOINTS.AUTH.PROFILE
    );
    return response.data;
  }

  async updateProfile(userData) {
    if (USE_MOCK_DATA) {
      const currentUser = this.getCurrentUser();
      if (currentUser) {
        return MockService.updateProfile(currentUser.id, userData);
      }
      throw new Error('No current user');
    }
    
    const response = await apiClient.put(
      API_CONFIG.ENDPOINTS.AUTH.PROFILE,
      userData
    );
    return response.data;
  }

  isAuthenticated() {
    return !!localStorage.getItem('accessToken');
  }

  getCurrentUser() {
    const userStr = localStorage.getItem('currentUser');
    return userStr ? JSON.parse(userStr) : null;
  }

  setCurrentUser(user) {
    localStorage.setItem('currentUser', JSON.stringify(user));
  }

  removeCurrentUser() {
    localStorage.removeItem('currentUser');
  }

  removeToken() {
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
    apiClient.removeToken();
  }
}

export const authService = new AuthService();