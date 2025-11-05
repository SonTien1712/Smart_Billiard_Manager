import { apiClient } from './api';
import { API_CONFIG } from '../config/api';
// import { MockService } from './mockService';

const USE_MOCK_DATA = false; // Set to false when you have a real backend

export class AuthService {
  
  async login(credentials) {
    const response = await apiClient.post(
      API_CONFIG.ENDPOINTS.AUTH.LOGIN,
      credentials
    );

    // Chuẩn hóa: nếu dùng axios thì response.data, còn không thì chính response là data
    const data = response?.data ?? response;
    
    if (data?.success === false) {
      throw new Error(data.message || 'Đăng nhập thất bại');
    }

    if (data?.success) {
      apiClient.setToken(data.accessToken);
      localStorage.setItem('refreshToken', data.refreshToken || '');
    }
    
    return data;
  }
  async register(userData) {
    const response = await apiClient.post(
      API_CONFIG.ENDPOINTS.AUTH.REGISTER,
      userData
    );

    console.log('Register response:', response);
    return response;
  }

  async googleAuth(googleData) {
    const response = await apiClient.post(
      API_CONFIG.ENDPOINTS.AUTH.GOOGLE_AUTH,
      googleData
    );
    
    if (response.success) {
      apiClient.setToken(response.data.accessToken);
      localStorage.setItem('refreshToken', response.data.refreshToken);
    }
    
    return response.data;
  }

  async logout() {
    try {
      await apiClient.post(API_CONFIG.ENDPOINTS.AUTH.LOGOUT);
    } catch (error) {
      console.error('Logout error:', error);
    } finally {
      apiClient.removeToken();
    }
  }

  async forgotPassword(email) {
    return await apiClient.post(
      API_CONFIG.ENDPOINTS.AUTH.FORGOT_PASSWORD,
      { email }
    );
  }

  async verifyResetToken(token) {
    // BE trả {valid: boolean}
    return apiClient.post(API_CONFIG.ENDPOINTS.AUTH.VERIFY_RESET_TOKEN, { token });
  }

  async resetPassword(token, newPassword) {
    await apiClient.post(
      API_CONFIG.ENDPOINTS.AUTH.RESET_PASSWORD,
      { token, newPassword }
    );
  }

  async getProfile() {
    const response = await apiClient.get(
      API_CONFIG.ENDPOINTS.AUTH.PROFILE
    );
    return response;
  }

  async updateProfile(userData) {
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