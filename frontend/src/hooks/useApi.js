import { useState, useCallback } from 'react';
import { handleApiError } from '../utils/errorHandler';

/**
 * @typedef {Object} UseApiOptions
 * @property {(data:any)=>void} [onSuccess]
 * @property {(error:any)=>void} [onError]
 * @property {boolean} [showSuccessToast]
 * @property {boolean} [showErrorToast]
 * @property {string} [successMessage]
 * @property {string} [errorMessage]
 */

/**
 * Generic API hook.
 * @param {function(...any): Promise<any>} apiFunction
 * @param {UseApiOptions} [options]
 */
export function useApi(apiFunction, options = {}) {
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const {
    onSuccess,
    onError,
    showErrorToast = true,
    errorMessage
  } = options;

  const execute = useCallback(async (...args) => {
    try {
      setLoading(true);
      setError(null);
      
      const result = await apiFunction(...args);
      setData(result);
      
      if (onSuccess) {
        onSuccess(result);
      }
      
      return result;
    } catch (err) {
      setError(err);
      
      if (showErrorToast) {
        handleApiError(err, errorMessage);
      }
      
      if (onError) {
        onError(err);
      }
      
      throw err;
    } finally {
      setLoading(false);
    }
  }, [apiFunction, onSuccess, onError, showErrorToast, errorMessage]);

  const reset = useCallback(() => {
    setData(null);
    setError(null);
    setLoading(false);
  }, []);

  return {
    data,
    loading,
    error,
    execute,
    reset
  };
}

// Hook for handling paginated data
/**
 * Hook for handling paginated data.
 * @template T
 * @param {(params: any) => Promise<{ content: T[]; totalElements: number; totalPages: number }>} apiFunction
 * @param {any} [initialParams={}] 
 */
export function usePaginatedApi(apiFunction, initialParams = {}) {
  const [data, setData] = useState([]);
  const [totalElements, setTotalElements] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [currentPage, setCurrentPage] = useState(0);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const fetchData = useCallback(async (params = {}) => {
    try {
      setLoading(true);
      setError(null);
      
      const mergedParams = { ...initialParams, ...params };
      const result = await apiFunction(mergedParams);
      
      setData(result.content);
      setTotalElements(result.totalElements);
      setTotalPages(result.totalPages);
      setCurrentPage(mergedParams.page || 0);
      
      return result;
    } catch (err) {
      setError(err);
      handleApiError(err);
      throw err;
    } finally {
      setLoading(false);
    }
  }, [apiFunction, initialParams]);

  const loadMore = useCallback(async () => {
    if (currentPage + 1 < totalPages) {
      const nextPageData = await fetchData({ page: currentPage + 1 });
      setData(prev => [...prev, ...nextPageData.content]);
    }
  }, [currentPage, totalPages, fetchData]);

  const refresh = useCallback(() => {
    return fetchData({ page: 0 });
  }, [fetchData]);

  return {
    data,
    totalElements,
    totalPages,
    currentPage,
    loading,
    error,
    fetchData,
    loadMore,
    refresh,
    hasMore: currentPage + 1 < totalPages
  };
}