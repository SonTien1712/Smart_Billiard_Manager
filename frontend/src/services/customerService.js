import { apiClient } from './api';
import { API_CONFIG } from '../config/api';
import { MockService } from './mockService';

const USE_MOCK_DATA = false; // Set to false when you have a real backend

export class CustomerService {
  // Club Management
  async getClubsByCustomer(customerId) {
  if (USE_MOCK_DATA) {
    return MockService.getClubsByCustomer(customerId);
  }

  try {
    const endpoint = API_CONFIG.ENDPOINTS.CUSTOMER.CLUBS_BY_CUSTOMER(customerId);
    console.log('[API Club] Fetching from:', endpoint);

    const data = await apiClient.get(endpoint);

    console.log('[API Club] Raw data:', data);
    let clubData;

    if (Array.isArray(data)) {
      clubData = data;
    } else if (data && Array.isArray(data.data)) {
      clubData = data.data;
    } else if (data && Array.isArray(data.clubs)) {
      clubData = data.clubs;
    } else {
      console.warn('[API Club] Unexpected response:', data);
      clubData = [];
    }

    console.log('[API Club] Final club data:', clubData);
    return clubData;

  } catch (error) {
    const status = error.response?.status;
    const errData = error.response?.data || error.message;

    console.error('[API Club] FAILED:', error);
    console.error('[API Club] Status:', status || 'Network/CORS');
    console.error('[API Club] Error data:', errData);

    return [];
  }
}


  async createClub(clubData) {
    if (USE_MOCK_DATA) {
      return MockService.createClub(clubData);
    }

    const response = await apiClient.post(
      API_CONFIG.ENDPOINTS.CUSTOMER.CLUBS,
      clubData
    );
    return response.data;
  }

  async updateClub(id, clubData) {
    if (USE_MOCK_DATA) {
      return MockService.updateClub(id, clubData);
    }

    const response = await apiClient.put(
      `${API_CONFIG.ENDPOINTS.CUSTOMER.CLUBS}/${id}`,
      clubData
    );
    return response.data;
  }

  async deleteClub(id) {
    if (USE_MOCK_DATA) {
      return MockService.deleteClub(id);
    }

    await apiClient.delete(`${API_CONFIG.ENDPOINTS.CUSTOMER.CLUBS}/${id}`);
  }

  // Table Management
  async getTables(clubId, params) {
    const query = { clubId, ...(params || {}) };
    const response = await apiClient.get(API_CONFIG.ENDPOINTS.CUSTOMER.TABLES, query);
    return response.data;
  }

  async createTable(tableData) {
    const response = await apiClient.post(API_CONFIG.ENDPOINTS.CUSTOMER.TABLES, tableData);
    return response.data;
  }

  async updateTable(id, tableData) {
    const response = await apiClient.put(
      `${API_CONFIG.ENDPOINTS.CUSTOMER.TABLES}/${id}`,
      tableData
    );
    return response.data;
  }

  async deleteTable(id) {
    await apiClient.delete(`${API_CONFIG.ENDPOINTS.CUSTOMER.TABLES}/${id}`);
  }

  // Staff Management
  async getStaff(clubId, params) {
    const query = { clubId, ...(params || {}) };
    const response = await apiClient.get(API_CONFIG.ENDPOINTS.CUSTOMER.STAFF, query);
    return response.data;
  }

  async createStaff(staffData) {
    const response = await apiClient.post(API_CONFIG.ENDPOINTS.CUSTOMER.STAFF, staffData);
    return response.data;
  }

  async updateStaff(id, staffData) {
    const response = await apiClient.put(
      `${API_CONFIG.ENDPOINTS.CUSTOMER.STAFF}/${id}`,
      staffData
    );
    return response.data;
  }

  async deleteStaff(id) {
    await apiClient.delete(`${API_CONFIG.ENDPOINTS.CUSTOMER.STAFF}/${id}`);
  }

  // Staff Account Management
  async getStaffAccounts(clubId, params) {
    const query = { clubId, ...(params || {}) };
    const response = await apiClient.get(API_CONFIG.ENDPOINTS.CUSTOMER.STAFF_ACCOUNTS, query);
    return response.data;
  }

  async createStaffAccount(accountData) {
    const response = await apiClient.post(API_CONFIG.ENDPOINTS.CUSTOMER.STAFF_ACCOUNTS, accountData);
    return response.data;
  }

  async updateStaffAccount(id, accountData) {
    const response = await apiClient.put(
      `${API_CONFIG.ENDPOINTS.CUSTOMER.STAFF_ACCOUNTS}/${id}`,
      accountData
    );
    return response.data;
  }

  async deleteStaffAccount(id) {
    await apiClient.delete(`${API_CONFIG.ENDPOINTS.CUSTOMER.STAFF_ACCOUNTS}/${id}`);
  }

  // Shift Management
  async getShifts(clubId, params) {
    const query = { clubId, ...(params || {}) };
    const response = await apiClient.get(API_CONFIG.ENDPOINTS.CUSTOMER.SHIFTS, query);
    return response.data;
  }

  async createShift(shiftData) {
    const response = await apiClient.post(API_CONFIG.ENDPOINTS.CUSTOMER.SHIFTS, shiftData);
    return response.data;
  }

  async updateShift(id, shiftData) {
    const response = await apiClient.put(
      `${API_CONFIG.ENDPOINTS.CUSTOMER.SHIFTS}/${id}`,
      shiftData
    );
    return response.data;
  }

  async deleteShift(id) {
    await apiClient.delete(`${API_CONFIG.ENDPOINTS.CUSTOMER.SHIFTS}/${id}`);
  }

  // Promotion Management
  async getPromotions(clubId, params) {
    const query = { clubId, ...(params || {}) };
    const response = await apiClient.get(API_CONFIG.ENDPOINTS.CUSTOMER.PROMOTIONS, query);
    return response.data;
  }

  async createPromotion(promotionData) {
    const response = await apiClient.post(API_CONFIG.ENDPOINTS.CUSTOMER.PROMOTIONS, promotionData);
    return response.data;
  }

  async updatePromotion(id, promotionData) {
    const response = await apiClient.put(
      `${API_CONFIG.ENDPOINTS.CUSTOMER.PROMOTIONS}/${id}`,
      promotionData
    );
    return response.data;
  }

  async deletePromotion(id) {
    await apiClient.delete(`${API_CONFIG.ENDPOINTS.CUSTOMER.PROMOTIONS}/${id}`);
  }

  // Product Management
  async getProducts(clubId, params) {
    const query = { clubId, ...(params || {}) };
    const response = await apiClient.get(API_CONFIG.ENDPOINTS.CUSTOMER.PRODUCTS, query);
    return response.data;
  }

  async createProduct(productData) {
    const response = await apiClient.post(API_CONFIG.ENDPOINTS.CUSTOMER.PRODUCTS, productData);
    return response.data;
  }

  async updateProduct(id, productData) {
    const response = await apiClient.put(
      `${API_CONFIG.ENDPOINTS.CUSTOMER.PRODUCTS}/${id}`,
      productData
    );
    return response.data;
  }

  async deleteProduct(id) {
    await apiClient.delete(`${API_CONFIG.ENDPOINTS.CUSTOMER.PRODUCTS}/${id}`);
  }
}

export const customerService = new CustomerService();