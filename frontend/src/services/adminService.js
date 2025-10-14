import { apiClient } from './api';
import { API_CONFIG } from '../config/api';

/**
 * AdminService - plain JavaScript version (no TypeScript types)
 */
export class AdminService {
  // Customer Management
  async getCustomers(params) {
    const response = await apiClient.get(API_CONFIG.ENDPOINTS.ADMIN.CUSTOMERS, params);
    return response.data;
  }

  async getCustomerById(id) {
    const response = await apiClient.get(`${API_CONFIG.ENDPOINTS.ADMIN.CUSTOMERS}/${id}`);
    return response.data;
  }

  async updateCustomerStatus(id, isActive) {
    const response = await apiClient.patch(
      `${API_CONFIG.ENDPOINTS.ADMIN.CUSTOMERS}/${id}/status`,
      { isActive }
    );
    return response.data;
  }

  async deleteCustomer(id) {
    await apiClient.delete(`${API_CONFIG.ENDPOINTS.ADMIN.CUSTOMERS}/${id}`);
  }

  // Admin Management
  async getAdmins(params) {
    const response = await apiClient.get(API_CONFIG.ENDPOINTS.ADMIN.ADMINS, params);
    return response.data;
  }

  async createAdmin(adminData) {
    const response = await apiClient.post(API_CONFIG.ENDPOINTS.ADMIN.ADMINS, adminData);
    return response.data;
  }

  async updateAdmin(id, adminData) {
    const response = await apiClient.put(
      `${API_CONFIG.ENDPOINTS.ADMIN.ADMINS}/${id}`,
      adminData
    );
    return response.data;
  }

  async deleteAdmin(id) {
    await apiClient.delete(`${API_CONFIG.ENDPOINTS.ADMIN.ADMINS}/${id}`);
  }

  // Statistics
  async getStatistics() {
    const response = await apiClient.get(API_CONFIG.ENDPOINTS.ADMIN.STATISTICS);
    return response.data;
  }

  // Customer's clubs (for admin view)
  async getCustomerClubs(customerId) {
    const response = await apiClient.get(
      `${API_CONFIG.ENDPOINTS.ADMIN.CUSTOMERS}/${customerId}/clubs`
    );
    return response.data;
  }
}

export const adminService = new AdminService();
