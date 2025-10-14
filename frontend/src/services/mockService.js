// Mock service to simulate Spring Boot backend during development
// (Plain JavaScript version - TypeScript types removed)

// Mock data
const mockUsers = [
  {
    id: '1',
    email: 'admin@billard.com',
    firstName: 'Admin',
    lastName: 'User',
    role: 'ADMIN',
    createdAt: new Date().toISOString(),
    updatedAt: new Date().toISOString(),
    isActive: true
  },
  {
    id: '2',
    email: 'customer@billard.com',
    firstName: 'Club',
    lastName: 'Owner',
    role: 'CUSTOMER',
    createdAt: new Date().toISOString(),
    updatedAt: new Date().toISOString(),
    isActive: true
  },
  {
    id: '3',
    email: 'staff@billard.com',
    firstName: 'Staff',
    lastName: 'Member',
    role: 'STAFF',
    createdAt: new Date().toISOString(),
    updatedAt: new Date().toISOString(),
    isActive: true
  }
];

const mockClubs = [
  {
    id: '1',
    name: 'Downtown Billiards Club',
    address: '456 Downtown Ave, City',
    phone: '+1234567890',
    email: 'downtown@billards.com',
    description: 'Premium billiards club in downtown',
    ownerId: '2',
    createdAt: new Date().toISOString(),
    updatedAt: new Date().toISOString(),
    isActive: true
  },
  {
    id: '2',
    name: 'Uptown Pool Hall',
    address: '789 Uptown Blvd, City',
    phone: '+1234567891',
    email: 'uptown@billards.com',
    description: 'Classic pool hall with vintage atmosphere',
    ownerId: '2',
    createdAt: new Date().toISOString(),
    updatedAt: new Date().toISOString(),
    isActive: true
  }
];

// Utility to simulate API delay
const delay = (ms = 500) => new Promise(resolve => setTimeout(resolve, ms));

// Utility to create paginated response
function createPaginatedResponse(data, page = 0, size = 10) {
  const start = page * size;
  const end = start + size;
  const content = data.slice(start, end);

  return {
    content,
    totalElements: data.length,
    totalPages: Math.ceil(data.length / size),
    size,
    number: page,
    first: page === 0,
    last: page >= Math.ceil(data.length / size) - 1
  };
}

export class MockService {
  // Authentication
  static async login(email, password) {
    await delay();

    const user = mockUsers.find(u => u.email === email);
    if (!user) {
      throw new Error('User not found');
    }

    return {
      user,
      accessToken: 'mock-access-token-' + user.id,
      refreshToken: 'mock-refresh-token-' + user.id,
      expiresIn: 3600000 // 1 hour
    };
  }

  static async register(userData) {
    await delay();

    const newUser = {
      id: (mockUsers.length + 1).toString(),
      email: userData.email,
      firstName: userData.firstName,
      lastName: userData.lastName,
      role: userData.role,
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString(),
      isActive: true
    };

    mockUsers.push(newUser);

    return {
      user: newUser,
      accessToken: 'mock-access-token-' + newUser.id,
      refreshToken: 'mock-refresh-token-' + newUser.id,
      expiresIn: 3600000
    };
  }

  static async googleAuth(googleData) {
    await delay();

    const newUser = {
      id: 'google-' + Math.random().toString(36).substr(2, 9),
      email: 'user@gmail.com',
      firstName: 'Google',
      lastName: 'User',
      role: googleData.role || 'CUSTOMER',
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString(),
      isActive: true,
      avatar: 'https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=400&h=400&fit=crop&crop=face'
    };

    return {
      user: newUser,
      accessToken: 'mock-access-token-' + newUser.id,
      refreshToken: 'mock-refresh-token-' + newUser.id,
      expiresIn: 3600000
    };
  }

  // Clubs
  static async getClubs(params = {}) {
    await delay();
    return createPaginatedResponse(mockClubs, params.page, params.size);
  }

  static async createClub(clubData) {
    await delay();

    const newClub = {
      ...clubData,
      id: (mockClubs.length + 1).toString(),
      ownerId: '2', // Mock current user
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString()
    };

    mockClubs.push(newClub);
    return newClub;
  }

  static async updateClub(id, clubData) {
    await delay();

    const index = mockClubs.findIndex(c => c.id === id);
    if (index === -1) {
      throw new Error('Club not found');
    }

    mockClubs[index] = {
      ...mockClubs[index],
      ...clubData,
      updatedAt: new Date().toISOString()
    };

    return mockClubs[index];
  }

  static async deleteClub(id) {
    await delay();

    const index = mockClubs.findIndex(c => c.id === id);
    if (index === -1) {
      throw new Error('Club not found');
    }

    mockClubs.splice(index, 1);
  }

  // Add more mock methods as needed for other entities...

  static async getProfile(userId) {
    await delay();

    const user = mockUsers.find(u => u.id === userId);
    if (!user) {
      throw new Error('User not found');
    }

    return user;
  }

  static async updateProfile(userId, userData) {
    await delay();

    const index = mockUsers.findIndex(u => u.id === userId);
    if (index === -1) {
      throw new Error('User not found');
    }

    mockUsers[index] = {
      ...mockUsers[index],
      ...userData,
      updatedAt: new Date().toISOString()
    };

    return mockUsers[index];
  }

  static async logout() {
    await delay(200); // Shorter delay for logout
    // In a real implementation, this would invalidate the token on the server
    // For mock, we just simulate the API call
    return Promise.resolve();
  }

  static async forgotPassword(email) {
    await delay();
    // Mock forgot password - in real app would send email
    const user = mockUsers.find(u => u.email === email);
    if (!user) {
      throw new Error('User not found');
    }
    return Promise.resolve();
  }

  static async resetPassword(token, newPassword) {
    await delay();
    // Mock reset password - in real app would validate token and update password
    return Promise.resolve();
  }
}