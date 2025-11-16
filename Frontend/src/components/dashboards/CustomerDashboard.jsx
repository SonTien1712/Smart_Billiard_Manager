// javascript

import React, { useState, useEffect } from 'react';
import { BarChart, Bar, LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer } from 'recharts';
import { Download, TrendingUp, DollarSign, Users, Calendar, Loader2, AlertCircle } from 'lucide-react';

const API_BASE = 'http://localhost:8080/api';

// Helper: Format currency
const formatCurrency = (value) => {
    const num = typeof value === 'string' ? parseFloat(value) : value;
    return new Intl.NumberFormat('en-US', { style: 'currency', currency: 'USD' }).format(num || 0);
};

export default function CustomerDashboard() {
    // ✅ LẤY customerId từ sessionStorage
    const customer = JSON.parse(sessionStorage.getItem("currentUser"));
    const CUSTOMER_ID = customer?.customerId || customer?.id; // ⚠️ Thử cả 2 field

    const [clubs, setClubs] = useState([]);
    const [selectedClub, setSelectedClub] = useState(null);
    const [dashboardData, setDashboardData] = useState(null);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);

    // ✅ DEBUG LOG
    useEffect(() => {
        console.log('=== DEBUG CUSTOMER_ID ===');
        console.log('1. customer from sessionStorage:', customer);
        console.log('2. CUSTOMER_ID:', CUSTOMER_ID);
        console.log('3. customer?.customerId:', customer?.customerId);
        console.log('4. customer?.id:', customer?.id);
    }, []);

    // ✅ KIỂM TRA CUSTOMER_ID trước khi fetch
    useEffect(() => {
        if (!CUSTOMER_ID) {
            console.error('❌ CUSTOMER_ID is null or undefined!');
            setError('User not authenticated. Please login again.');
            return;
        }

        const fetchClubs = async () => {
            try {
                console.log(`📡 Fetching clubs for CUSTOMER_ID: ${CUSTOMER_ID}`);

                const response = await fetch(`${API_BASE}/customer/clubs/customer/${CUSTOMER_ID}`, {
                    headers: { 'Authorization': `Bearer ${sessionStorage.getItem('accessToken') || ''}` }
                });

                console.log('📡 Response status:', response.status);

                if (!response.ok) throw new Error('Failed to fetch clubs');

                const data = await response.json();
                console.log('✅ Clubs data:', data);

                const clubList = Array.isArray(data) ? data : (data.data || data.clubs || []);

                setClubs(clubList);
                if (clubList.length > 0) {
                    setSelectedClub(clubList[0].id || clubList[0].clubId);
                } else {
                    setSelectedClub(null);
                }
            } catch (err) {
                console.error('❌ Error fetching clubs:', err);
                setError('Failed to load clubs');
            }
        };

        fetchClubs();
    }, [CUSTOMER_ID]);

    // Fetch dashboard data khi chọn club
    useEffect(() => {
        if (!selectedClub || !CUSTOMER_ID) return;

        const fetchDashboardData = async () => {
            setLoading(true);
            setError(null);

            try {
                console.log(`📡 Fetching dashboard for Club ${selectedClub}, Customer ${CUSTOMER_ID}`);

                const response = await fetch(
                    `${API_BASE}/customers/${CUSTOMER_ID}/clubs/${selectedClub}/dashboard`,
                    {
                        headers: { 'Authorization': `Bearer ${sessionStorage.getItem('accessToken') || ''}` }
                    }
                );

                if (!response.ok) throw new Error('Failed to fetch dashboard');

                const data = await response.json();
                console.log('✅ Dashboard data:', data);
                setDashboardData(data);

            } catch (err) {
                console.error('❌ Error fetching dashboard:', err);
                setError('Failed to load dashboard data');
            } finally {
                setLoading(false);
            }
        };

        fetchDashboardData();
    }, [selectedClub, CUSTOMER_ID]);

    // ✅ HIỂN THỊ LỖI NẾU KHÔNG CÓ CUSTOMER_ID
    if (!CUSTOMER_ID) {
        return (
            <div className="flex items-center justify-center h-screen bg-gray-50">
                <div className="text-center">
                    <AlertCircle className="h-12 w-12 text-red-500 mx-auto mb-4" />
                    <p className="text-lg font-semibold text-gray-700">User not authenticated</p>
                    <p className="text-sm text-gray-500 mt-2">Please login again</p>
                    <button
                        onClick={() => window.location.href = '/login'}
                        className="mt-4 px-4 py-2 bg-blue-500 text-white rounded hover:bg-blue-600"
                    >
                        Go to Login
                    </button>
                </div>
            </div>
        );
    }

    // ... rest of export handlers (giữ nguyên)
    const handleExportRevenue = async () => {
        try {
            const url = `${API_BASE}/customers/${CUSTOMER_ID}/clubs/${selectedClub}/export/revenue`;
            const response = await fetch(url, {
                headers: { 'Authorization': `Bearer ${sessionStorage.getItem('accessToken') || ''}` }
            });

            if (!response.ok) throw new Error('Export failed');

            const blob = await response.blob();
            const link = document.createElement('a');
            link.href = URL.createObjectURL(blob);
            link.download = `revenue_club_${selectedClub}.xlsx`;
            link.click();
            URL.revokeObjectURL(link.href);
        } catch (err) {
            alert('Export revenue failed: ' + err.message);
        }
    };

    const handleExportSalaries = async () => {
        try {
            const url = `${API_BASE}/customers/${CUSTOMER_ID}/clubs/${selectedClub}/export/salaries`;
            const response = await fetch(url, {
                headers: { 'Authorization': `Bearer ${sessionStorage.getItem('accessToken') || ''}` }
            });

            if (!response.ok) throw new Error('Export failed');

            const blob = await response.blob();
            const link = document.createElement('a');
            link.href = URL.createObjectURL(blob);
            link.download = `salaries_club_${selectedClub}.xlsx`;
            link.click();
            URL.revokeObjectURL(link.href);
        } catch (err) {
            alert('Export salaries failed: ' + err.message);
        }
    };

    const handleExportTopProducts = async () => {
        try {
            const url = `${API_BASE}/customers/${CUSTOMER_ID}/clubs/${selectedClub}/export/top-products?topN=5`;
            const response = await fetch(url, {
                headers: { 'Authorization': `Bearer ${sessionStorage.getItem('accessToken') || ''}` }
            });

            if (!response.ok) throw new Error('Export failed');

            const blob = await response.blob();
            const link = document.createElement('a');
            link.href = URL.createObjectURL(blob);
            link.download = `top_products_club_${selectedClub}.xlsx`;
            link.click();
            URL.revokeObjectURL(link.href);
        } catch (err) {
            alert('Export top products failed: ' + err.message);
        }
    };

    const handleExportEmployeeSalaries = async () => {
        try {
            const url = `${API_BASE}/customers/${CUSTOMER_ID}/clubs/${selectedClub}/export/employee-salaries`;
            const response = await fetch(url, {
                headers: { 'Authorization': `Bearer ${sessionStorage.getItem('accessToken') || ''}` }
            });

            if (!response.ok) throw new Error('Export failed');

            const blob = await response.blob();
            const link = document.createElement('a');
            link.href = URL.createObjectURL(blob);
            link.download = `employee_salaries_club_${selectedClub}.xlsx`;
            link.click();
            URL.revokeObjectURL(link.href);

            console.log('✅ Employee salaries exported successfully');
        } catch (err) {
            console.error('❌ Export employee salaries failed:', err);
            alert('Export employee salaries failed: ' + err.message);
        }
    };

    // Calculate summary stats
    const summaryStats = dashboardData ? {
        totalRevenue: (dashboardData.revenueByMonth || []).reduce((sum, m) => sum + (m.revenue || 0), 0),
        totalSalary: (dashboardData.salaryByMonth || []).reduce((sum, m) => sum + (m.totalSalary || 0), 0),
        netProfit: (dashboardData.revenueByMonth || []).reduce((sum, m) => sum + (m.revenue || 0), 0) -
            (dashboardData.salaryByMonth || []).reduce((sum, m) => sum + (m.totalSalary || 0), 0),
        topProductProfit: (dashboardData.topProducts || []).reduce((sum, p) => sum + (p.totalProfit || 0), 0)
    } : null;

    if (error && clubs.length === 0) {
        return (
            <div className="flex items-center justify-center h-screen bg-gray-50">
                <div className="text-center">
                    <AlertCircle className="h-12 w-12 text-red-500 mx-auto mb-4" />
                    <p className="text-lg font-semibold text-gray-700">{error}</p>
                    <button
                        onClick={() => window.location.reload()}
                        className="mt-4 px-4 py-2 bg-blue-500 text-white rounded hover:bg-blue-600"
                    >
                        Retry
                    </button>
                </div>
            </div>
        );
    }

    return (
        <div className="min-h-screen bg-gray-50 p-6">
            <div className="max-w-7xl mx-auto space-y-6">
                {/* Rest of JSX - giữ nguyên */}
                <div className="bg-white rounded-lg shadow p-6">
                    <h1 className="text-3xl font-bold text-gray-800 mb-2">Club Dashboard</h1>
                    <p className="text-gray-600">Analytics & Reports</p>
                </div>

                {/* Rest of your JSX code... */}
            </div>
        </div>
    );
}