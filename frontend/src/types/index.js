// JavaScript type definitions for the billiards management system using JSDoc

/**
 * @typedef {Object} User
 * @property {string} id
 * @property {string} email
 * @property {string} firstName
 * @property {string} lastName
 * @property {'ADMIN' | 'CUSTOMER' | 'STAFF'} role
 * @property {string} [avatar]
 * @property {string} createdAt
 * @property {string} updatedAt
 * @property {boolean} isActive
 */

/**
 * @typedef {Object} AuthResponse
 * @property {User} user
 * @property {string} accessToken
 * @property {string} refreshToken
 * @property {number} expiresIn
 */

/**
 * @typedef {Object} LoginRequest
 * @property {string} email
 * @property {string} password
 */

/**
 * @typedef {Object} RegisterRequest
 * @property {string} email
 * @property {string} password
 * @property {string} firstName
 * @property {string} lastName
 * @property {'CUSTOMER' | 'STAFF'} role
 */

/**
 * @typedef {Object} GoogleAuthRequest
 * @property {string} googleToken
 * @property {'CUSTOMER' | 'STAFF'} [role]
 */

/**
 * @typedef {Object} Club
 * @property {string} id
 * @property {string} name
 * @property {string} address
 * @property {string} phone
 * @property {string} email
 * @property {string} [description]
 * @property {string} ownerId
 * @property {string} createdAt
 * @property {string} updatedAt
 * @property {boolean} isActive
 */

/**
 * @typedef {Object} Table
 * @property {string} id
 * @property {string} number
 * @property {string} clubId
 * @property {'POOL' | 'SNOOKER' | 'CAROM'} type
 * @property {number} hourlyRate
 * @property {'AVAILABLE' | 'OCCUPIED' | 'MAINTENANCE'} status
 * @property {string} createdAt
 * @property {string} updatedAt
 */

/**
 * @typedef {Object} Staff
 * @property {string} id
 * @property {string} userId
 * @property {string} clubId
 * @property {string} position
 * @property {number} salary
 * @property {string} hireDate
 * @property {boolean} isActive
 * @property {User} user
 */

/**
 * @typedef {Object} Shift
 * @property {string} id
 * @property {string} clubId
 * @property {string} staffId
 * @property {string} startTime
 * @property {string} endTime
 * @property {string} date
 * @property {'SCHEDULED' | 'COMPLETED' | 'CANCELLED'} status
 * @property {Staff} staff
 */

/**
 * @typedef {Object} Promotion
 * @property {string} id
 * @property {string} clubId
 * @property {string} title
 * @property {string} description
 * @property {'PERCENTAGE' | 'FIXED_AMOUNT'} promotionType
 * @property {number} promotionValue
 * @property {string} startDate
 * @property {string} endDate
 * @property {boolean} isActive
 */

/**
 * @typedef {Object} Product
 * @property {string} id
 * @property {string} clubId
 * @property {string} name
 * @property {string} [description]
 * @property {number} price
 * @property {'FOOD' | 'BEVERAGE' | 'EQUIPMENT' | 'OTHER'} category
 * @property {number} stock
 * @property {boolean} isAvailable
 */

/**
 * @typedef {Object} Bill
 * @property {string} id
 * @property {string} clubId
 * @property {string} tableId
 * @property {string} staffId
 * @property {string} [customerName]
 * @property {string} [customerPhone]
 * @property {string} startTime
 * @property {string} [endTime]
 * @property {number} hourlyRate
 * @property {number} totalHours
 * @property {number} tableAmount
 * @property {number} productAmount
 * @property {number} discountAmount
 * @property {number} totalAmount
 * @property {'ACTIVE' | 'COMPLETED' | 'CANCELLED'} status
 * @property {BillItem[]} items
 * @property {Table} table
 * @property {Staff} staff
 */

/**
 * @typedef {Object} BillItem
 * @property {string} id
 * @property {string} billId
 * @property {string} productId
 * @property {number} quantity
 * @property {number} unitPrice
 * @property {number} totalPrice
 * @property {Product} product
 */

/**
 * @typedef {Object} Attendance
 * @property {string} id
 * @property {string} staffId
 * @property {string} shiftId
 * @property {string} [checkInTime]
 * @property {string} [checkOutTime]
 * @property {'PENDING' | 'CHECKED_IN' | 'CHECKED_OUT' | 'ABSENT'} status
 * @property {string} [notes]
 * @property {Staff} staff
 * @property {Shift} shift
 */

/**
 * @template T
 * @typedef {Object} ApiResponse
 * @property {boolean} success
 * @property {T} data
 * @property {string} message
 * @property {string} timestamp
 */

/**
 * @typedef {Object} PaginationParams
 * @property {number} page
 * @property {number} size
 * @property {string} [sort]
 * @property {'ASC' | 'DESC'} [direction]
 */

/**
 * @template T
 * @typedef {Object} PaginatedResponse
 * @property {T[]} content
 * @property {number} totalElements
 * @property {number} totalPages
 * @property {number} size
 * @property {number} number
 * @property {boolean} first
 * @property {boolean} last
 */

/**
 * @typedef {Object} ErrorResponse
 * @property {false} success
 * @property {string} message
 * @property {Record<string, string[]>} [errors]
 * @property {string} timestamp
 */

// Export empty object to make this a module
export {};

