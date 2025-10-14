import { apiClient } from './api';
import { API_CONFIG } from '../config/api';

export class StaffService {
  // Bill Management
  async getBills(params) {
    const response = await apiClient.get(API_CONFIG.ENDPOINTS.STAFF.BILLS, params);
    return response.data;
  }

  async getBillById(id) {
    const response = await apiClient.get(`${API_CONFIG.ENDPOINTS.STAFF.BILLS}/${id}`);
    return response.data;
  }

  async createBill(billData) {
    const response = await apiClient.post(API_CONFIG.ENDPOINTS.STAFF.BILLS, billData);
    return response.data;
  }

  async updateBill(id, billData) {
    const response = await apiClient.put(`${API_CONFIG.ENDPOINTS.STAFF.BILLS}/${id}`, billData);
    return response.data;
  }

  async completeBill(id) {
    const response = await apiClient.patch(`${API_CONFIG.ENDPOINTS.STAFF.BILLS}/${id}/complete`);
    return response.data;
  }

  async cancelBill(id) {
    const response = await apiClient.patch(`${API_CONFIG.ENDPOINTS.STAFF.BILLS}/${id}/cancel`);
    return response.data;
  }

  // Bill Items Management
  async addBillItem(billId, itemData) {
    const response = await apiClient.post(`${API_CONFIG.ENDPOINTS.STAFF.BILLS}/${billId}/items`, itemData);
    return response.data;
  }

  async updateBillItem(billId, itemId, itemData) {
    const response = await apiClient.put(`${API_CONFIG.ENDPOINTS.STAFF.BILLS}/${billId}/items/${itemId}`, itemData);
    return response.data;
  }

  async removeBillItem(billId, itemId) {
    await apiClient.delete(`${API_CONFIG.ENDPOINTS.STAFF.BILLS}/${billId}/items/${itemId}`);
  }

  // Table Management (Staff View)
  async getTables(status) {
    const response = await apiClient.get(API_CONFIG.ENDPOINTS.STAFF.TABLES, { status });
    return response.data;
  }

  async updateTableStatus(tableId, status) {
    const response = await apiClient.patch(`${API_CONFIG.ENDPOINTS.STAFF.TABLES}/${tableId}/status`, { status });
    return response.data;
  }

  // Work Schedule
  async getSchedule(params) {
    const response = await apiClient.get(API_CONFIG.ENDPOINTS.STAFF.SCHEDULE, params);
    return response.data;
  }

  async getScheduleById(id) {
    const response = await apiClient.get(`${API_CONFIG.ENDPOINTS.STAFF.SCHEDULE}/${id}`);
    return response.data;
  }

  // Attendance Tracking
  async getAttendance(params) {
    const response = await apiClient.get(API_CONFIG.ENDPOINTS.STAFF.ATTENDANCE, params);
    return response.data;
  }

  async checkIn(shiftId) {
    const response = await apiClient.post(`${API_CONFIG.ENDPOINTS.STAFF.ATTENDANCE}/check-in`, { shiftId });
    return response.data;
  }

  async checkOut(attendanceId) {
    const response = await apiClient.patch(`${API_CONFIG.ENDPOINTS.STAFF.ATTENDANCE}/${attendanceId}/check-out`);
    return response.data;
  }

  async updateAttendance(id, notes) {
    const response = await apiClient.put(`${API_CONFIG.ENDPOINTS.STAFF.ATTENDANCE}/${id}`, { notes });
    return response.data;
  }

  // Products (for bill items)
  async getProducts(category) {
    const response = await apiClient.get('/staff/products', { category, isAvailable: true });
    return response.data;
  }
}

export const staffService = new StaffService();
