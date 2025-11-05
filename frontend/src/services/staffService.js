import { apiClient } from './api';
import { API_CONFIG } from '../config/api';

export class StaffService {
  normalize(resp) { return resp?.data ?? resp; }
  // Bill Management
  async getBills(params) {
    const response = await apiClient.get(API_CONFIG.ENDPOINTS.STAFF.BILLS, params);
    return this.normalize(response);
  }
  async getTodayStats(params) {
    const response = await apiClient.get('/staff/stats/today', params);
    return this.normalize(response);
  }

  async getBillById(id) {
    const response = await apiClient.get(`${API_CONFIG.ENDPOINTS.STAFF.BILLS}/${id}`);
    return this.normalize(response);
  }

  async createBill(billData) {
    const response = await apiClient.post(API_CONFIG.ENDPOINTS.STAFF.BILLS, billData);
    return this.normalize(response);
  }

  async updateBill(id, billData) {
    const response = await apiClient.put(`${API_CONFIG.ENDPOINTS.STAFF.BILLS}/${id}`, billData);
    return this.normalize(response);
  }

  async completeBill(id, opts) {
    const query = new URLSearchParams(opts && opts.employeeId ? { employeeId: opts.employeeId } : {}).toString();
    const body = {};
    if (opts && typeof opts.productTotal !== 'undefined') body.productTotal = opts.productTotal;
    if (opts && typeof opts.taxPercent !== 'undefined') body.taxPercent = opts.taxPercent;
    if (opts && typeof opts.discount !== 'undefined') body.discount = opts.discount;
    if (opts && Array.isArray(opts.items)) body.items = opts.items;
    const response = await apiClient.patch(`${API_CONFIG.ENDPOINTS.STAFF.BILLS}/${id}/complete${query ? `?${query}` : ''}`, body);
    return this.normalize(response);
  }

  async applyPromotion(id, { code, employeeId } = {}) {
    const query = new URLSearchParams({ ...(code ? { code } : {}) , ...(employeeId ? { employeeId } : {}) }).toString();
    const response = await apiClient.post(`${API_CONFIG.ENDPOINTS.STAFF.BILLS}/${id}/apply-promotion${query ? `?${query}` : ''}`);
    return this.normalize(response);
  }


  async finalizeBill(id, opts) {
    const body = {};
    if (opts && typeof opts.taxPercent !== 'undefined') body.taxPercent = opts.taxPercent;
    const response = await apiClient.patch(`${API_CONFIG.ENDPOINTS.STAFF.BILLS}/${id}/finalize`, body);
    return this.normalize(response);
  }

  async unfinalizeBill(id) {
    const response = await apiClient.patch(`${API_CONFIG.ENDPOINTS.STAFF.BILLS}/${id}/unfinalize`);
    return this.normalize(response);
  }

  async finalizeBill(id, opts) {
    const body = {};
    if (opts && typeof opts.taxPercent !== 'undefined') body.taxPercent = opts.taxPercent;
    const response = await apiClient.patch(`${API_CONFIG.ENDPOINTS.STAFF.BILLS}/${id}/finalize`, body);
    return this.normalize(response);
  }

  async cancelBill(id, opts) {
    const query = new URLSearchParams(opts && opts.employeeId ? { employeeId: opts.employeeId } : {}).toString();
    const response = await apiClient.patch(`${API_CONFIG.ENDPOINTS.STAFF.BILLS}/${id}/cancel${query ? `?${query}` : ''}`);
    return this.normalize(response);
  }

  // Bill Items Management
  async addBillItem(billId, itemData) {
    const response = await apiClient.post(`${API_CONFIG.ENDPOINTS.STAFF.BILLS}/${billId}/items${itemData?.employeeId ? `?employeeId=${itemData.employeeId}` : ''}`, itemData);
    return this.normalize(response);
  }

  async updateBillItem(billId, itemId, itemData) {
    const response = await apiClient.put(`${API_CONFIG.ENDPOINTS.STAFF.BILLS}/${billId}/items/${itemId}${itemData?.employeeId ? `?employeeId=${itemData.employeeId}` : ''}`, itemData);
    return this.normalize(response);
  }

  async removeBillItem(billId, itemId, opts) {
    const query = new URLSearchParams(opts && opts.employeeId ? { employeeId: opts.employeeId } : {}).toString();
    await apiClient.delete(`${API_CONFIG.ENDPOINTS.STAFF.BILLS}/${billId}/items/${itemId}${query ? `?${query}` : ''}`);
  }

  // Table Management (Staff View)
  async getTables(arg) {
    // Backward compatible: allow getTables(status) or getTables(params)
    const params = (typeof arg === 'object' && arg !== null) ? arg : { status: arg };
    const response = await apiClient.get(API_CONFIG.ENDPOINTS.STAFF.TABLES, params);
    return this.normalize(response);
  }

  async updateTableStatus(tableId, status) {
    const response = await apiClient.patch(`${API_CONFIG.ENDPOINTS.STAFF.TABLES}/${tableId}/status`, { status });
    return this.normalize(response);
  }
  async openTable({ tableId, clubId, customerId, employeeId }) {
    const query = new URLSearchParams({ clubId, customerId, ...(employeeId ? { employeeId } : {}) }).toString();
    const response = await apiClient.post(`${API_CONFIG.ENDPOINTS.STAFF.TABLES}/${tableId}/open?${query}`);
    return this.normalize(response);
  }

  // Work Schedule
  async getSchedule(params) {
    const response = await apiClient.get(API_CONFIG.ENDPOINTS.STAFF.SCHEDULE, params);
    return this.normalize(response);
  }

  async getScheduleById(id) {
    const response = await apiClient.get(`${API_CONFIG.ENDPOINTS.STAFF.SCHEDULE}/${id}`);
    return this.normalize(response);
  }

  // Attendance Tracking
  async getAttendance(params) {
    const response = await apiClient.get(API_CONFIG.ENDPOINTS.STAFF.ATTENDANCE, params);
    return this.normalize(response);
  }

  async getPayrollSummary(params) {
    const response = await apiClient.get('/staff/payroll/summary', params);
    return this.normalize(response);
  }

  async checkIn(shiftId) {
    const response = await apiClient.post(`${API_CONFIG.ENDPOINTS.STAFF.ATTENDANCE}/check-in`, { shiftId });
    return this.normalize(response);
  }

  async checkOut(attendanceId) {
    const response = await apiClient.patch(`${API_CONFIG.ENDPOINTS.STAFF.ATTENDANCE}/${attendanceId}/check-out`);
    return this.normalize(response);
  }

  async updateAttendance(id, notes) {
    const response = await apiClient.put(`${API_CONFIG.ENDPOINTS.STAFF.ATTENDANCE}/${id}`, { notes });
    return this.normalize(response);
  }

  // Products (for bill items)
  async getProducts(params) {
    const response = await apiClient.get('/staff/products', params);
    return this.normalize(response);
  }
}

export const staffService = new StaffService();
