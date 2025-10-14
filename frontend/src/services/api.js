import { API_CONFIG } from '../config/api';

export class ApiClient {
  #baseURL;
  #token = null;
  #isProduction = false; // Set to true when you have a real backend

  constructor() {
    this.#baseURL = API_CONFIG.BASE_URL;
    this.#token = localStorage.getItem('accessToken');
  }

  setToken(token) {
    this.#token = token;
    localStorage.setItem('accessToken', token);
  }

  removeToken() {
    this.#token = null;
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
  }

  getHeaders() {
    const headers = {
      'Content-Type': 'application/json',
    };

    if (this.#token) {
      headers.Authorization = `Bearer ${this.#token}`;
    }

    return headers;
  }

  async #handleResponse(response) {
    if (!response.ok) {
      if (response.status === 401) {
        // Token expired, try to refresh
        const refreshed = await this.#refreshToken();
        if (!refreshed) {
          this.removeToken();
          window.location.href = '/signin';
          throw new Error('Authentication failed');
        }
      }

      const errorData = await response.json().catch(() => ({
        message: `HTTP ${response.status}: ${response.statusText}`
      }));
      
      throw new Error(errorData.message || 'An error occurred');
    }

    return response.json();
  }

  async #refreshToken() {
    try {
      const refreshToken = localStorage.getItem('refreshToken');
      if (!refreshToken) return false;

      const response = await fetch(`${this.#baseURL}${API_CONFIG.ENDPOINTS.AUTH.REFRESH}`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ refreshToken })
      });

      if (response.ok) {
        const data = await response.json();
        this.setToken(data.accessToken);
        localStorage.setItem('refreshToken', data.refreshToken);
        return true;
      }
    } catch (error) {
      console.error('Token refresh failed:', error);
    }
    
    return false;
  }

  async get(endpoint, params) {
    const url = new URL(`${this.#baseURL}${endpoint}`);
    
    if (params) {
      Object.entries(params).forEach(([key, value]) => {
        if (value !== undefined && value !== null) {
          url.searchParams.append(key, String(value));
        }
      });
    }

    const response = await fetch(url.toString(), {
      method: 'GET',
      headers: this.getHeaders(),
    });

    return this.#handleResponse(response);
  }

  async post(endpoint, data) {
    const response = await fetch(`${this.#baseURL}${endpoint}`, {
      method: 'POST',
      headers: this.getHeaders(),
      body: data ? JSON.stringify(data) : undefined,
    });

    return this.#handleResponse(response);
  }

  async put(endpoint, data) {
    const response = await fetch(`${this.#baseURL}${endpoint}`, {
      method: 'PUT',
      headers: this.getHeaders(),
      body: data ? JSON.stringify(data) : undefined,
    });

    return this.#handleResponse(response);
  }

  async delete(endpoint) {
    const response = await fetch(`${this.#baseURL}${endpoint}`, {
      method: 'DELETE',
      headers: this.getHeaders(),
    });

    return this.#handleResponse(response);
  }

  async patch(endpoint, data) {
    const response = await fetch(`${this.#baseURL}${endpoint}`, {
      method: 'PATCH',
      headers: this.getHeaders(),
      body: data ? JSON.stringify(data) : undefined,
    });

    return this.#handleResponse(response);
  }
}

export const apiClient = new ApiClient();