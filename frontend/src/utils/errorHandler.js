import { toast } from "sonner";

/**
 * Custom API Error class
 */
export class ApiError extends Error {
  /**
   * @param {string} message - Error message
   * @param {number} status - HTTP status code
   * @param {Record<string, string[]>} [errors] - Validation errors
   */
  constructor(message, status, errors) {
    super(message);
    this.name = 'ApiError';
    this.status = status;
    this.errors = errors;
  }
}

/**
 * Handle API errors and display appropriate toast messages
 * @param {any} error - The error object
 * @param {string} [customMessage] - Optional custom error message
 */
export const handleApiError = (error, customMessage) => {
  console.error('API Error:', error);

  if (error instanceof ApiError) {
    if (error.errors) {
      // Handle validation errors
      Object.entries(error.errors).forEach(([field, messages]) => {
        messages.forEach(message => {
          toast.error(`${field}: ${message}`);
        });
      });
    } else {
      toast.error(customMessage || error.message);
    }
  } else if (error.message) {
    toast.error(customMessage || error.message);
  } else {
    toast.error(customMessage || 'An unexpected error occurred');
  }
};

/**
 * Display success toast message
 * @param {string} message - Success message to display
 */
export const handleSuccess = (message) => {
  toast.success(message);
};

/**
 * Display info toast message
 * @param {string} message - Info message to display
 */
export const handleInfo = (message) => {
  toast.info(message);
};

/**
 * Display warning toast message
 * @param {string} message - Warning message to display
 */
export const handleWarning = (message) => {
  toast.warning(message);
};
