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
    try {
      const query = { clubId, ...(params || {}) };
      const response = await apiClient.get(API_CONFIG.ENDPOINTS.CUSTOMER.STAFF, query);
      return response.data ?? response;
    } catch (error) {
      console.warn('[API Customer Staff] Fallback: returning empty list. Reason:', error?.message || error);
      return [];
    }
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
  async getStaffByCustomerId(customerId, params) {
    // 1. Tạo endpoint (Lưu ý: tham số query 'params' sẽ được apiClient tự xử lý)
    const url = API_CONFIG.ENDPOINTS.CUSTOMER.STAFF_BY_CUSTOMER(customerId);
    const query = { ...(params || {}) }; // Lấy các params khác nếu có

    console.log('[API Staff] Fetching from:', url);

    try {
      // Gọi API. Biến 'data' ở đây là kết quả trả về từ apiClient.get
      const data = await apiClient.get(url, query);

      console.log('[API Staff] Raw data:', data);
      let staffData;

      // Xử lý response tương tự như getTablesByCustomerId
      if (Array.isArray(data)) {
        // Trường hợp 1: apiClient trả về mảng trực tiếp (vd: [...])
        staffData = data;
      } else if (data && Array.isArray(data.data)) {
        // Trường hợp 2: apiClient trả về object bọc (vd: { data: [...] })
        staffData = data.data;
      } else {
        // Trường hợp 3: Bất kỳ cấu trúc nào khác (undefined, null, object rỗng)
        console.warn('[API Staff] Unexpected response:', data);
        staffData = [];
      }

      console.log('[API Staff] Final staff data:', staffData);
      return staffData;

    } catch (error) {
      // Xử lý lỗi chi tiết hơn (như trong getTablesByCustomerId)
      const status = error.response?.status;
      const errData = error.response?.data || error.message;

      console.error('[API Staff] FAILED:', error);
      console.error('[API Staff] Status:', status || 'Network/CORS');
      console.error('[API Staff] Error data:', errData);

      // Trả về mảng rỗng nếu có lỗi để frontend không bị crash
      return [];
    }
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
    try {
      // Giả sử endpoint của bạn
      const endpoint = `${API_CONFIG.ENDPOINTS.CUSTOMER.STAFF_ACCOUNTS}/${id}`; 
      return await apiClient.delete(endpoint);
    } catch (err) {
      console.error("[API StaffAccount] LỖI khi xóa:", err);
      // SỬA: Phải "ném" lỗi ra để component bắt được
      throw err; 
    }
  }

  // Shift Management
  // async getShifts(clubId, params) {
  //   const query = { clubId, ...(params || {}) };
  //   const response = await apiClient.get(API_CONFIG.ENDPOINTS.CUSTOMER.SHIFTS, query);
  //   return response.data;
  // }
  async getShifts(clubId, params) {
    try {
      const query = { clubId, ...(params || {}) };
      const response = await apiClient.get(API_CONFIG.ENDPOINTS.CUSTOMER.SHIFTS, query);
      return response.data ?? response;
    } catch (error) {
      console.warn('[API Customer Shifts] Fallback: returning empty list. Reason:', error?.message || error);
      return [];
    }
  }

  // async createShift(shiftData) {
  //   const response = await apiClient.post(API_CONFIG.ENDPOINTS.CUSTOMER.SHIFTS, shiftData);
  //   return response.data;
  // }
  async createShift(shiftData) {
    try {
      const response = await apiClient.post(API_CONFIG.ENDPOINTS.CUSTOMER.SHIFTS, shiftData);
      return response.data ?? response;
    } catch (error) {
      // Let caller show a toast; give clearer message
      throw new Error('API tạo lịch chưa sẵn sàng trên server');
    }
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

  // ... bên trong class CustomerService ...

  // --- Staff Account Management ---

  /**
   * 1. ĐỌC (READ): Lấy tất cả tài khoản nhân viên
   */
  async getStaffAccounts() {
    // Bỏ qua MOCK_DATA để tập trung vào API
    try {
      const endpoint = API_CONFIG.ENDPOINTS.CUSTOMER.STAFF_ACCOUNTS;
      console.log('[API StaffAccount] Đang tải danh sách từ:', endpoint);

      const response = await apiClient.get(endpoint);

      // Backend có thể trả về data trong { data: [...] } hoặc chỉ [...]
      const accounts = response.data?.data || response.data || [];

      console.log(`[API StaffAccount] Đã tải ${accounts.length} tài khoản`);
      return accounts;

    } catch (error) {
      console.error('[API StaffAccount] LỖI khi tải danh sách:', error.response?.data || error.message);
      return []; // Trả về mảng rỗng nếu có lỗi
    }
  }

  /**
   * 2. TẠO (CREATE): Tạo một tài khoản nhân viên mới
   * @param {object} accountData - Dữ liệu từ form (formData)
   */
  async createStaffAccount(accountData) {
    try {
      const endpoint = API_CONFIG.ENDPOINTS.CUSTOMER.STAFF_ACCOUNTS;
      console.log('[API StaffAccount] Đang tạo tài khoản tại:', endpoint);

      const response = await apiClient.post(endpoint, accountData);

      console.log('[API StaffAccount] Đã tạo tài khoản:', response.data);
      return response.data; // Trả về tài khoản vừa tạo (kèm ID từ DB)

    } catch (error) {
      console.error('[API StaffAccount] LỖI khi tạo tài khoản:', error.response?.data || error.message);
      throw error; // Ném lỗi ra để component có thể bắt và hiển thị
    }
  }

  /**
   * 3. CẬP NHẬT (UPDATE): Cập nhật thông tin tài khoản
   * @param {string} accountId - ID của tài khoản cần sửa
   * @param {object} accountData - Dữ liệu từ form (formData)
   */
  async updateStaffAccount(accountId, accountData) {
    try {
      // Endpoint sẽ là: /customer/staff-accounts/{accountId}
      const endpoint = `${API_CONFIG.ENDPOINTS.CUSTOMER.STAFF_ACCOUNTS}/${accountId}`;
      console.log('[API StaffAccount] Đang cập nhật tài khoản tại:', endpoint);

      const response = await apiClient.put(endpoint, accountData);

      console.log('[API StaffAccount] Đã cập nhật tài khoản:', response.data);
      return response.data; // Trả về tài khoản vừa cập nhật

    } catch (error) {
      console.error('[API StaffAccount] LỖI khi cập nhật tài khoản:', error.response?.data || error.message);
      throw error;
    }
  }

  /**
   * 4. XÓA (DELETE): Xóa một tài khoản nhân viên
   * @param {string} accountId - ID của tài khoản cần xóa
   */
  async deleteStaffAccount(accountId) {
    try {
      // Endpoint sẽ là: /customer/staff-accounts/{accountId}
      const endpoint = `${API_CONFIG.ENDPOINTS.CUSTOMER.STAFF_ACCOUNTS}/${accountId}`;
      console.log('[API StaffAccount] Đang xóa tài khoản tại:', endpoint);

      const response = await apiClient.delete(endpoint);

      console.log('[API StaffAccount] Đã xóa tài khoản ID:', accountId);
      return response.data; // Thường trả về thông báo thành công

    } catch (error) {
      console.error('[API StaffAccount] LỖI khi xóa tài khoản:', error.response?.data || error.message);
      throw error;
    }
  }

  /**
   * 5. CẬP NHẬT TRẠNG THÁI (PATCH): Bật/tắt tài khoản
   * Hàm này tương ứng với <Switch> trong component của bạn
   * @param {string} accountId - ID của tài khoản
   * @param {string} newStatus - Trạng thái mới ('active' hoặc 'inactive')
   */
  async toggleStaffAccountStatus(accountId, newStatus) {
    try {
      // Ghi chú: Endpoint này là một giả định hợp lý dựa trên REST best practices
      // (tương tự như PRODUCT_TOGGLE_STATUS của bạn).
      // Backend của bạn cần xử lý: PATCH /customer/staff-accounts/{accountId}/status
      const endpoint = `${API_CONFIG.ENDPOINTS.CUSTOMER.STAFF_ACCOUNTS}/${accountId}/status`;

      console.log(`[API StaffAccount] Đang đổi trạng thái tại: ${endpoint} thành ${newStatus}`);

      // Gửi request PATCH chỉ với trạng thái mới
      const response = await apiClient.patch(endpoint, { status: newStatus });

      console.log('[API StaffAccount] Đã đổi trạng thái:', response.data);
      return response.data;

    } catch (error) {
      console.error('[API StaffAccount] LỖI khi đổi trạng thái:', error.response?.data || error.message);
      throw error;
    }
  }

  /**
   * 1. ĐỌC (READ): Lấy tài khoản nhân viên theo Customer ID
   * @param {string | number} customerId ID của chủ sở hữu (customer)
   */
  async getStaffAccountsByCustomerId(customerId) {
    try {
      const endpoint = API_CONFIG.ENDPOINTS.CUSTOMER.STAFF_ACCOUNTS_BY_CUSTOMER(customerId);

      console.log('[API StaffAccount] Đang tải danh sách từ:', endpoint);
      // 1. Đổi tên biến thành 'data' cho nhất quán
      const data = await apiClient.get(endpoint);

      console.log('[API StaffAccount] Raw data:', data);

      // 2. Áp dụng logic kiểm tra an toàn giống hệt hàm 'getStaffByCustomerId'
      let accounts;
      if (Array.isArray(data)) {
        // API trả về [...]
        accounts = data;
      } else if (data && Array.isArray(data.data)) {
        // API trả về { data: [...] } (dự phòng)
        accounts = data.data;
      } else {
        console.warn('[API StaffAccount] Unexpected response:', data);
        accounts = [];
      }

      console.log(`[API StaffAccount] Đã tải ${accounts.length} tài khoản cho customer ${customerId}`);
      return accounts;

    } catch (error) {
      console.error('[API StaffAccount] LỖI khi tải danh sách:', error.response?.data || error.message);
      return []; // Trả về mảng rỗng nếu có lỗi
    }
  }
  /**
   * 1. ĐỌC (READ): Lấy danh sách nhân viên CHƯA có tài khoản
   * @param {string | number} customerId ID của chủ sở hữu (customer)
   */
  async getUnassignedStaff(customerId) {
    try {
      // 1. Lấy URL từ API_CONFIG
      const endpoint = API_CONFIG.ENDPOINTS.CUSTOMER.STAFF_UNASSIGNED(customerId);
      
      console.log('[API Staff] Đang tải danh sách nhân viên chưa gán từ:', endpoint);
      
      // 2. Gọi API
      const data = await apiClient.get(endpoint);
      
      // 3. Xử lý response (dùng logic an toàn như bạn đã làm)
      let staffList = [];
      if (Array.isArray(data)) {
        staffList = data; // Trường hợp 1: API trả về [...]
      } else if (data && Array.isArray(data.data)) {
        staffList = data.data; // Trường hợp 2: API trả về { data: [...] }
      } else {
         console.warn('[API Staff] Cấu trúc response không mong đợi:', data);
         staffList = [];
      }
      
      console.log(`[API Staff] Đã tải ${staffList.length} nhân viên chưa gán.`);
      return staffList;

    } catch (error) {
      console.error('[API Staff] LỖI khi tải nhân viên chưa gán:', error.response?.data || error.message);
      return []; // Luôn trả về mảng rỗng nếu có lỗi
    }
  }




}

export const customerService = new CustomerService();
