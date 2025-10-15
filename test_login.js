// Test script for login functionality
const API_BASE = 'http://localhost:8080/api';

async function testAPI() {
    console.log('🔍 Testing Database Status...');
    
    try {
        // Test database status
        const statusResponse = await fetch(`${API_BASE}/test/database-status`);
        const statusData = await statusResponse.json();
        console.log('📊 Database Status:', statusData);
        
        if (!statusData.success) {
            console.error('❌ Database connection failed');
            return;
        }
        
        // Create sample data if needed
        if (statusData.adminCount === 0) {
            console.log('🔧 Creating sample data...');
            const createResponse = await fetch(`${API_BASE}/test/create-sample-data`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' }
            });
            const createData = await createResponse.json();
            console.log('✅ Sample data created:', createData);
        }
        
        // Test admin login
        console.log('🔐 Testing Admin Login...');
        const adminLoginResponse = await fetch(`${API_BASE}/test/test-login`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                email: 'admin@billiard.com',
                password: '123456'
            })
        });
        const adminLoginData = await adminLoginResponse.json();
        console.log('👤 Admin Login Result:', adminLoginData);
        
        // Test actual auth endpoint
        console.log('🔐 Testing Auth Endpoint...');
        const authResponse = await fetch(`${API_BASE}/auth/login`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                email: 'admin@billiard.com',
                password: '123456'
            })
        });
        const authData = await authResponse.json();
        console.log('🎯 Auth Endpoint Result:', authData);
        
        // Test customer login
        console.log('🔐 Testing Customer Login...');
        const customerLoginResponse = await fetch(`${API_BASE}/auth/login`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                email: 'customer@test.com',
                password: '123456'
            })
        });
        const customerLoginData = await customerLoginResponse.json();
        console.log('👤 Customer Login Result:', customerLoginData);
        
    } catch (error) {
        console.error('❌ Test failed:', error.message);
    }
}

// Wait a bit for backend to start, then run test
setTimeout(testAPI, 5000);
