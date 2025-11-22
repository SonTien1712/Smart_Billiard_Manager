import React, { useState, useEffect } from 'react';
import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer } from 'recharts';
import { Download, TrendingUp, DollarSign, Users, Calendar, Loader2, AlertCircle } from 'lucide-react';

const API_BASE = 'http://localhost:8080/api';

// Helper: Format currency (VND - Vietnamese Dong)
const formatCurrency = (value) => {
    const num = typeof value === 'string' ? parseFloat(value) : value;
    return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(num || 0);
};

// Helper: Format currency for Y-axis (shorter format - VND)
const formatCurrencyShort = (value) => {
    if (value >= 1000000000) {
        return `${(value / 1000000000).toFixed(1)} tỷ`;
    } else if (value >= 1000000) {
        return `${(value / 1000000).toFixed(1)} tr`;
    } else if (value >= 1000) {
        return `${(value / 1000).toFixed(0)}K`;
    }
    return `${value.toLocaleString('vi-VN')}`;
};

export default function CustomerDashboard() {
    // ✅ Lấy customerId từ sessionStorage (field 'id')
    const customer = JSON.parse(sessionStorage.getItem("currentUser"));
    const CUSTOMER_ID = customer?.id;

    const [clubs, setClubs] = useState([]);
    const [selectedClub, setSelectedClub] = useState(null);
    const [dashboardData, setDashboardData] = useState(null);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);

    // Fetch danh sách clubs từ backend
    useEffect(() => {
        if (!CUSTOMER_ID) {
            setError('User not authenticated. Please login again.');
            return;
        }

        const fetchClubs = async () => {
            try {
                // ✅ Fixed: Added parentheses for template literal
                const response = await fetch(`${API_BASE}/customer/clubs/customer/${CUSTOMER_ID}`, {
                    headers: { 'Authorization': `Bearer ${sessionStorage.getItem('accessToken') || ''}` }
                });
                if (!response.ok) throw new Error('Failed to fetch clubs');
                const data = await response.json();
                const clubList = Array.isArray(data) ? data : (data.data || data.clubs || []);
                setClubs(clubList);
                if (clubList.length > 0) {
                    setSelectedClub(clubList[0].id || clubList[0].clubId);
                } else {
                    setSelectedClub(null);
                }
            } catch (err) {
                console.error('Error fetching clubs:', err);
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
                const response = await fetch(
                    `${API_BASE}/customers/${CUSTOMER_ID}/clubs/${selectedClub}/dashboard`,
                    {
                        headers: { 'Authorization': `Bearer ${sessionStorage.getItem('accessToken') || ''}` }
                    }
                );
                if (!response.ok) throw new Error('Failed to fetch dashboard');
                const data = await response.json();
                setDashboardData(data);
            } catch (err) {
                console.error('Error fetching dashboard:', err);
                setError('Failed to load dashboard data');
            } finally {
                setLoading(false);
            }
        };

        fetchDashboardData();
    }, [selectedClub, CUSTOMER_ID]);

    // Export handlers
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
        } catch (err) {
            console.error('Export employee salaries failed:', err);
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

    // ✅ Hiển thị lỗi nếu không có CUSTOMER_ID
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
                {/* Header */}
                <div className="bg-white rounded-lg shadow p-6">
                    <h1 className="text-3xl font-bold text-gray-800 mb-2">Club Dashboard</h1>
                    <p className="text-gray-600">Analytics & Reports</p>
                </div>

                {/* Club Selector */}
                <div className="bg-white rounded-lg shadow p-6">
                    <label className="block text-sm font-medium text-gray-700 mb-2">Select Club</label>
                    <select
                        value={selectedClub || ''}
                        onChange={(e) => setSelectedClub(parseInt(e.target.value))}
                        className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                    >
                        {clubs.map(club => (
                            <option key={club.id || club.clubId} value={club.id || club.clubId}>
                                {club.clubName || `Club ${club.id || club.clubId}`}
                            </option>
                        ))}
                    </select>
                </div>

                {/* Loading State */}
                {loading ? (
                    <div className="flex items-center justify-center py-20">
                        <Loader2 className="h-10 w-10 animate-spin text-blue-500" />
                        <span className="ml-3 text-gray-600">Loading dashboard...</span>
                    </div>
                ) : dashboardData ? (
                    <>
                        {/* Summary Cards */}
                        {summaryStats && (
                            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
                                <div className="bg-white rounded-lg shadow p-6">
                                    <div className="flex items-center justify-between mb-2">
                                        <span className="text-sm font-medium text-gray-600">Total Revenue</span>
                                        <DollarSign className="h-5 w-5 text-green-500" />
                                    </div>
                                    <p className="text-2xl font-bold text-gray-800">{formatCurrency(summaryStats.totalRevenue)}</p>
                                    <p className="text-xs text-gray-500 mt-1">Last 3 months</p>
                                </div>
                                <div className="bg-white rounded-lg shadow p-6">
                                    <div className="flex items-center justify-between mb-2">
                                        <span className="text-sm font-medium text-gray-600">Total Salary</span>
                                        <Users className="h-5 w-5 text-blue-500" />
                                    </div>
                                    <p className="text-2xl font-bold text-gray-800">{formatCurrency(summaryStats.totalSalary)}</p>
                                    <p className="text-xs text-gray-500 mt-1">Employee costs</p>
                                </div>
                                <div className="bg-white rounded-lg shadow p-6">
                                    <div className="flex items-center justify-between mb-2">
                                        <span className="text-sm font-medium text-gray-600">Net Profit</span>
                                        <TrendingUp className="h-5 w-5 text-purple-500" />
                                    </div>
                                    <p className="text-2xl font-bold text-gray-800">{formatCurrency(summaryStats.netProfit)}</p>
                                    <p className="text-xs text-gray-500 mt-1">Revenue - Salary</p>
                                </div>
                                <div className="bg-white rounded-lg shadow p-6">
                                    <div className="flex items-center justify-between mb-2">
                                        <span className="text-sm font-medium text-gray-600">Product Profit</span>
                                        <Calendar className="h-5 w-5 text-orange-500" />
                                    </div>
                                    <p className="text-2xl font-bold text-gray-800">{formatCurrency(summaryStats.topProductProfit)}</p>
                                    <p className="text-xs text-gray-500 mt-1">Top 5 products</p>
                                </div>
                            </div>
                        )}

                        {/* Monthly Revenue Chart - ✅ Changed to BarChart */}
                        <div className="bg-white rounded-lg shadow p-6">
                            <div className="flex items-center justify-between mb-4">
                                <h2 className="text-xl font-semibold text-gray-800">Monthly Revenue</h2>
                                <button
                                    onClick={handleExportRevenue}
                                    className="flex items-center px-4 py-2 bg-green-500 text-white rounded-lg hover:bg-green-600 transition"
                                >
                                    <Download className="h-4 w-4 mr-2" />
                                    Export Excel
                                </button>
                            </div>
                            <ResponsiveContainer width="100%" height={350}>
                                <BarChart data={dashboardData.revenueByMonth} margin={{ top: 20, right: 30, left: 20, bottom: 60 }}>
                                    <CartesianGrid strokeDasharray="3 3" />
                                    <XAxis
                                        dataKey="month"
                                        label={{ value: 'Tháng ', position: 'insideBottom', offset: -10, style: { fontWeight: 'bold' } }}
                                        tick={{ fontSize: 12 }}
                                        angle={-45}
                                        textAnchor="end"
                                        height={80}
                                    />
                                    <YAxis
                                        tickFormatter={formatCurrencyShort}
                                        label={{ value: 'Doanh thu ', angle: -90, position: 'insideLeft', style: { fontWeight: 'bold' } }}
                                        tick={{ fontSize: 12 }}
                                        width={80}
                                    />
                                    <Tooltip
                                        formatter={(value) => formatCurrency(value)}
                                        labelStyle={{ fontWeight: 'bold' }}
                                    />
                                    <Legend
                                        wrapperStyle={{ paddingTop: '10px' }}
                                        iconType="rect"
                                    />
                                    <Bar dataKey="revenue" fill="#10b981" name="Doanh thu (Revenue)" radius={[8, 8, 0, 0]} />
                                </BarChart>
                            </ResponsiveContainer>
                        </div>

                        {/* Monthly Salaries Chart - ✅ Enhanced with labels */}
                        <div className="bg-white rounded-lg shadow p-6">
                            <div className="flex items-center justify-between mb-4">
                                <h2 className="text-xl font-semibold text-gray-800">Monthly Salaries</h2>
                                <button
                                    onClick={handleExportEmployeeSalaries}
                                    className="flex items-center px-4 py-2 bg-blue-500 text-white rounded-lg hover:bg-blue-600 transition"
                                >
                                    <Download className="h-4 w-4 mr-2" />
                                    Export Excel
                                </button>
                            </div>
                            <ResponsiveContainer width="100%" height={350}>
                                <BarChart data={dashboardData.salaryByMonth} margin={{ top: 20, right: 30, left: 20, bottom: 60 }}>
                                    <CartesianGrid strokeDasharray="3 3" />
                                    <XAxis
                                        dataKey="month"
                                        label={{ value: 'Tháng ', position: 'insideBottom', offset: -10, style: { fontWeight: 'bold' } }}
                                        tick={{ fontSize: 12 }}
                                        angle={-45}
                                        textAnchor="end"
                                        height={80}
                                    />
                                    <YAxis
                                        tickFormatter={formatCurrencyShort}
                                        label={{ value: 'Lương ', angle: -90, position: 'insideLeft', style: { fontWeight: 'bold' } }}
                                        tick={{ fontSize: 12 }}
                                        width={80}
                                    />
                                    <Tooltip
                                        formatter={(value) => formatCurrency(value)}
                                        labelStyle={{ fontWeight: 'bold' }}
                                    />
                                    <Legend
                                        wrapperStyle={{ paddingTop: '10px' }}
                                        iconType="rect"
                                    />
                                    <Bar dataKey="totalSalary" fill="#3b82f6" name="Tổng lương (Total Salary)" radius={[8, 8, 0, 0]} />
                                </BarChart>
                            </ResponsiveContainer>
                        </div>

                        {/* Top Products Table */}
                        <div className="bg-white rounded-lg shadow p-6">
                            <div className="flex items-center justify-between mb-4">
                                <h2 className="text-xl font-semibold text-gray-800">Top Products by Profit</h2>
                                <button
                                    onClick={handleExportTopProducts}
                                    className="flex items-center px-4 py-2 bg-purple-500 text-white rounded-lg hover:bg-purple-600 transition"
                                >
                                    <Download className="h-4 w-4 mr-2" />
                                    Export Excel
                                </button>
                            </div>
                            <div className="overflow-x-auto">
                                <table className="w-full text-left">
                                    <thead>
                                    <tr className="border-b">
                                        <th className="pb-3 text-sm font-medium text-gray-600">Product</th>
                                        <th className="pb-3 text-sm font-medium text-gray-600">Category</th>
                                        <th className="pb-3 text-sm font-medium text-gray-600 text-right">Qty Sold</th>
                                        <th className="pb-3 text-sm font-medium text-gray-600 text-right">Profit/Unit</th>
                                        <th className="pb-3 text-sm font-medium text-gray-600 text-right">Total Profit</th>
                                    </tr>
                                    </thead>
                                    <tbody>
                                    {dashboardData.topProducts.map((product) => (
                                        <tr key={product.productId} className="border-b hover:bg-gray-50">
                                            <td className="py-3 text-sm text-gray-800">{product.productName}</td>
                                            <td className="py-3 text-sm text-gray-600">{product.category}</td>
                                            <td className="py-3 text-sm text-gray-800 text-right">{product.qtySold}</td>
                                            <td className="py-3 text-sm text-gray-800 text-right">{formatCurrency(product.profitPerUnit)}</td>
                                            <td className="py-3 text-sm font-semibold text-green-600 text-right">{formatCurrency(product.totalProfit)}</td>
                                        </tr>
                                    ))}
                                    </tbody>
                                </table>
                            </div>
                        </div>
                    </>
                ) : (
                    <div className="bg-white rounded-lg shadow p-12 text-center">
                        <p className="text-gray-500">Select a club to view dashboard</p>
                    </div>
                )}
            </div>
        </div>
    );
}