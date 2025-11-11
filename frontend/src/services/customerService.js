import { apiClient } from './api';
import { API_CONFIG } from '../config/api';
// import { MockService } from './mockService';

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
  async getTablesByCustomerId(customerId) {
  try {
    const endpoint = `${API_CONFIG.ENDPOINTS.CUSTOMER.TABLES_BY_CUSTOMER(customerId)}`;
    console.log('[API Table] Fetching from:', endpoint);

    const data = await apiClient.get(endpoint);

    console.log('[API Table] Raw data:', data);
    let tableData;

    if (Array.isArray(data)) {
      tableData = data;
    } else if (data && Array.isArray(data.data)) {
      tableData = data.data;
    } else {
      console.warn('[API Table] Unexpected response:', data);
      tableData = [];
    }

    console.log('[API Table] Final table data:', tableData);
    return tableData;

  } catch (error) {
    const status = error.response?.status;
    const errData = error.response?.data || error.message;

    console.error('[API Table] FAILED:', error);
    console.error('[API Table] Status:', status || 'Network/CORS');
    console.error('[API Table] Error data:', errData);

    return [];
  }
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
    // backend returns { promotions, currentPage, totalItems, totalPages }
    return response.promotions ?? response.data ?? response;
  }

  async createPromotion(promotionData) {
    const response = await apiClient.post(API_CONFIG.ENDPOINTS.CUSTOMER.PROMOTIONS, promotionData);
    return response; // controller returns DTO directly
  }

  async updatePromotion(id, promotionData) {
    const response = await apiClient.put(
      `${API_CONFIG.ENDPOINTS.CUSTOMER.PROMOTIONS}/${id}`,
      promotionData
    );
    return response;
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

    // Product Management
    async getProducts(clubId, params) {
        try {
            const query = { clubId, ...(params || {}) };
            console.log('[API Product] Fetching products with query:', query);

            const response = await apiClient.get(
                API_CONFIG.ENDPOINTS.CUSTOMER.PRODUCTS,
                query
            );

            console.log('[API Product] Raw response:', response);

            // Handle different response formats
            let productData;
            if (Array.isArray(response)) {
                productData = response;
            } else if (response && Array.isArray(response.data)) {
                productData = response.data;
            } else if (response && Array.isArray(response.products)) {
                productData = response.products;
            } else {
                console.warn('[API Product] Unexpected response:', response);
                productData = [];
            }

            console.log('[API Product] Final product data:', productData);
            return productData;
        } catch (error) {
            console.error('[API Product] Failed to fetch:', error);
            return [];
        }
    }

    async getProductById(id, clubId) {
        try {
            const endpoint = `${API_CONFIG.ENDPOINTS.CUSTOMER.PRODUCTS}/${id}`;
            console.log('[API Product] Fetching product by ID:', id);

            const response = await apiClient.get(endpoint, { clubId });
            return response.data || response;
        } catch (error) {
            console.error('[API Product] Failed to fetch by ID:', error);
            throw error;
        }
    }

    async createProduct(productData) {
        try {
            console.log('[API Product] Creating product:', productData);

            const response = await apiClient.post(
                API_CONFIG.ENDPOINTS.CUSTOMER.PRODUCTS,
                productData
            );

            return response.data || response;
        } catch (error) {
            console.error('[API Product] Failed to create:', error);
            throw error;
        }
    }

    async updateProduct(id, productData) {
        try {
            console.log('[API Product] Updating product:', id, productData);

            const response = await apiClient.put(
                `${API_CONFIG.ENDPOINTS.CUSTOMER.PRODUCTS}/${id}`,
                productData
            );

            return response.data || response;
        } catch (error) {
            console.error('[API Product] Failed to update:', error);
            throw error;
        }
    }

    async deleteProduct(id) {
        try {
            console.log('[API Product] Deleting product:', id);

            await apiClient.delete(
                `${API_CONFIG.ENDPOINTS.CUSTOMER.PRODUCTS}/${id}`
            );
        } catch (error) {
            console.error('[API Product] Failed to delete:', error);
            throw error;
        }
    }

    async toggleProductStatus(id) {
        try {
            console.log('[API Product] Toggling product status:', id);

            const response = await apiClient.patch(
                `${API_CONFIG.ENDPOINTS.CUSTOMER.PRODUCTS}/${id}/toggle-status`
            );

            return response.data || response;
        } catch (error) {
            console.error('[API Product] Failed to toggle status:', error);
            throw error;
        }
    }

    async searchProducts(clubId, keyword) {
        try {
            console.log('[API Product] Searching products:', { clubId, keyword });

            const response = await apiClient.get(
                `${API_CONFIG.ENDPOINTS.CUSTOMER.PRODUCTS}/search`,
                { clubId, keyword }
            );

            let productData;
            if (Array.isArray(response)) {
                productData = response;
            } else if (response && Array.isArray(response.data)) {
                productData = response.data;
            } else {
                productData = [];
            }

            return productData;
        } catch (error) {
            console.error('[API Product] Failed to search:', error);
            return [];
        }
    }
}

export const customerService = new CustomerService();
