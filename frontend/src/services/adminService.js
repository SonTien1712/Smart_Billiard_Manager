import { apiClient } from './api';
import { API_CONFIG } from '../config/api';

/**
 * AdminService - plain JavaScript version (no TypeScript types)
 */
export class AdminService {
  // Customer Management
  async getCustomers({ page = 0, size = 10, sort = 'dateJoined,desc' } = {}) {
    return apiClient.get(API_CONFIG.ENDPOINTS.ADMIN.CUSTOMERS, { page, size, sort });
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
  async getStatistics({ from, to } = {}) {
    const now = new Date();

    const start = from
      ? new Date(from) // cho phép caller truyền string/date
      : new Date(Date.UTC(now.getUTCFullYear(), now.getUTCMonth(), 1, 0, 0, 0));

    const end = to
      ? new Date(to)
      : new Date(Date.UTC(now.getUTCFullYear(), now.getUTCMonth() + 1, 0, 23, 59, 59));

    // ✅ TỰ CHUYỂN ISO STRING trước khi truyền (vì get() sẽ String(value))
    const params = {
      from: start.toISOString(), // -> '2025-10-01T00:00:00.000Z'
      to: end.toISOString(),     // -> '2025-10-31T23:59:59.000Z'
    };

    return apiClient.get(API_CONFIG.ENDPOINTS.ADMIN.STATISTICS, params);
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
