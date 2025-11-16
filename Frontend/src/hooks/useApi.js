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

      console.log('[useApi] Executing API call...');
      const result = await apiFunction(...args);
      console.log('[useApi] API call success, result:', result);
      
      // ✅ Set data TRƯỚC khi gọi onSuccess
      setData(result);

      if (onSuccess) {
        onSuccess(result);
      }

      return result;
    } catch (err) {
      console.error('[useApi] API call failed:', err);
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