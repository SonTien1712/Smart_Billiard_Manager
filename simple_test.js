// Simple test script for login functionality
const API_BASE = 'http://localhost:8080/api';

async function testLogin() {
    console.log('🔍 Testing Login Endpoint...');
    
    try {
        // Test admin login
        console.log('🔐 Testing Admin Login...');
        const adminResponse = await fetch(`${API_BASE}/auth/login`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                email: 'admin@billiard.com',
                password: '123456'
            })
        });
        const adminData = await adminResponse.json();
        console.log('👤 Admin Login Result:', adminData);
        
        // Test customer login
        console.log('🔐 Testing Customer Login...');
        const customerResponse = await fetch(`${API_BASE}/auth/login`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                email: 'customer@test.com',
                password: '123456'
            })
        });
        const customerData = await customerResponse.json();
        console.log('👤 Customer Login Result:', customerData);
        
        // Test with invalid credentials
        console.log('🔐 Testing Invalid Login...');
        const invalidResponse = await fetch(`${API_BASE}/auth/login`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                email: 'invalid@test.com',
                password: 'wrongpassword'
            })
        });
        const invalidData = await invalidResponse.json();
        console.log('❌ Invalid Login Result:', invalidData);
        
    } catch (error) {
        console.error('❌ Test failed:', error.message);
    }
}

// Wait for backend to start, then run test
setTimeout(testLogin, 8000);
