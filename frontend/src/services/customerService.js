// Updated customerService.js
import { apiClient } from './api';
import { API_CONFIG } from '../config/api';

const USE_MOCK_DATA = false;

export class CustomerService {

    // ==================== DASHBOARD ====================

    /**
     * Get dashboard statistics for current customer
     */
    async getDashboardStats() {
        try {
            console.log('[API Dashboard] Fetching dashboard stats...');

            // Use API_CONFIG endpoint (consistent with api.js)
            const endpoint = API_CONFIG.ENDPOINTS.CUSTOMER.DASHBOARD_STATS;
            const data = await apiClient.get(endpoint);

            console.log('[API Dashboard] Stats received:', data);
            // Normalize structure: if backend wraps { data: ... } return inner
            if (data && typeof data === 'object') return data.data ?? data;
            return data;
        } catch (error) {
            console.error('[API Dashboard] Failed to fetch stats:', error);
            throw error;
        }
    }

    /**
     * Get dashboard statistics for specific customer (admin use)
     */
    async getDashboardStatsByCustomerId(customerId) {
        try {
            console.log('[API Dashboard] Fetching stats for customer:', customerId);

            const endpoint = API_CONFIG.ENDPOINTS.CUSTOMER.DASHBOARD_STATS_BY_ID(customerId);
            const data = await apiClient.get(endpoint);

            if (data && typeof data === 'object') {
                return data.data || data;
            }
            return data;
        } catch (error) {
            console.error('[API Dashboard] Failed to fetch stats for customer:', error);
            throw error;
        }
    }

    // ==================== EXPORTS (Excel) ====================
    // These functions call backend export endpoints and download blobs.

    async exportRevenueExcel(customerId, clubId, from, to) {
        try {
            const base = API_CONFIG.BASE_URL.replace(/\/+$/,'');
            const params = [];
            if (from) params.push(`from=${encodeURIComponent(from)}`);
            if (to) params.push(`to=${encodeURIComponent(to)}`);
            const query = params.length ? `?${params.join('&')}` : '';
            const url = `${base}/customers/${customerId}/clubs/${clubId}/export/revenue${query}`;
            console.log('[API Export] Downloading revenue excel from:', url);

            const res = await fetch(url, {
                method: 'GET',
                credentials: 'include' // keep if server expects cookie auth; otherwise remove
            });
            if (!res.ok) throw new Error(`Export failed: ${res.status} ${res.statusText}`);
            const blob = await res.blob();
            const filename = `revenue_club_${clubId}.xlsx`;
            const link = document.createElement('a');
            link.href = window.URL.createObjectURL(blob);
            link.download = filename;
            document.body.appendChild(link);
            link.click();
            link.remove();
            window.URL.revokeObjectURL(link.href);
        } catch (err) {
            console.error('[API Export] exportRevenueExcel error:', err);
            throw err;
        }
    }

    async exportSalariesExcel(customerId, clubId, from, to) {
        try {
            const base = API_CONFIG.BASE_URL.replace(/\/+$/,'');
            const params = [];
            if (from) params.push(`from=${encodeURIComponent(from)}`);
            if (to) params.push(`to=${encodeURIComponent(to)}`);
            const query = params.length ? `?${params.join('&')}` : '';
            const url = `${base}/customers/${customerId}/clubs/${clubId}/export/salaries${query}`;
            console.log('[API Export] Downloading salaries excel from:', url);

            const res = await fetch(url, {
                method: 'GET',
                credentials: 'include'
            });
            if (!res.ok) throw new Error(`Export failed: ${res.status} ${res.statusText}`);
            const blob = await res.blob();
            const filename = `salaries_club_${clubId}.xlsx`;
            const link = document.createElement('a');
            link.href = window.URL.createObjectURL(blob);
            link.download = filename;
            document.body.appendChild(link);
            link.click();
            link.remove();
            window.URL.revokeObjectURL(link.href);
        } catch (err) {
            console.error('[API Export] exportSalariesExcel error:', err);
            throw err;
        }
    }

    async exportTopProductsExcel(customerId, clubId, topN = 5, from, to) {
        try {
            const base = API_CONFIG.BASE_URL.replace(/\/+$/,'');
            const params = [`topN=${encodeURIComponent(topN)}`];
            if (from) params.push(`from=${encodeURIComponent(from)}`);
            if (to) params.push(`to=${encodeURIComponent(to)}`);
            const query = params.length ? `?${params.join('&')}` : '';
            const url = `${base}/customers/${customerId}/clubs/${clubId}/export/top-products${query}`;
            console.log('[API Export] Downloading top-products excel from:', url);

            const res = await fetch(url, {
                method: 'GET',
                credentials: 'include'
            });
            if (!res.ok) throw new Error(`Export failed: ${res.status} ${res.statusText}`);
            const blob = await res.blob();
            const filename = `top_products_club_${clubId}.xlsx`;
            const link = document.createElement('a');
            link.href = window.URL.createObjectURL(blob);
            link.download = filename;
            document.body.appendChild(link);
            link.click();
            link.remove();
            window.URL.revokeObjectURL(link.href);
        } catch (err) {
            console.error('[API Export] exportTopProductsExcel error:', err);
            throw err;
        }
    }

    // ==================== CLUB MANAGEMENT ====================

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

    // ... rest of file unchanged (tables, staff, promotions, products) ...
    // For brevity, keep remaining methods as in original file (they remain compatible).
}

export const customerService = new CustomerService();
