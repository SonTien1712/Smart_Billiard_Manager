import React, { useState, useEffect } from 'react';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '../ui/card';
import { Button } from '../ui/button';
import { Input } from '../ui/input';
import { Label } from '../ui/label';
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '../ui/table';
import { Badge } from '../ui/badge';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '../ui/select';
import { Dialog, DialogContent, DialogDescription, DialogFooter, DialogHeader, DialogTitle } from '../ui/dialog';
// import { PageType } from '../Dashboard'; // Bạn có thể giữ lại nếu cần
import { Percent, Plus, Edit, Trash2, Calendar, DollarSign } from 'lucide-react';

// === IMPORT PHẦN CẦN THIẾT ===
import { toast } from 'sonner';
import { customerService } from '@/services/customerService';
import { useAuth } from '@/components/AuthProvider';
// =============================

// Trạng thái form ban đầu, khớp với DTO
const initialFormData = {
    code: '',
    description: '', // Đổi từ name sang description
    discountType: 'PERCENTAGE',
    discountValue: 10,
    maxUsage: 100, // Thêm trường maxUsage
    startDate: '',
    endDate: '',
};

export function PromotionManagement({ onPageChange }) {
    // === STATE ĐÃ CẬP NHẬT ===
    const [promotions, setPromotions] = useState([]);
    const [loading, setLoading] = useState(true);
    const { user } = useAuth(); // Lấy user từ AuthProvider

    const [isDialogOpen, setIsDialogOpen] = useState(false);
    const [editingPromotion, setEditingPromotion] = useState(null);
    const [formData, setFormData] = useState(initialFormData);
    // ==========================

    // === HÀM LẤY DỮ LIỆU TỪ API ===
    const fetchPromotions = async () => {
        if (!user?.clubId) {
            toast.error('Không tìm thấy thông tin câu lạc bộ.');
            setLoading(false);
            return;
        }

        setLoading(true);
        try {
            const data = await customerService.getPromotionsByClub(user.clubId);
            setPromotions(data || []);
        } catch (error) {
            console.error('Lỗi khi tải promotions:', error);
            toast.error('Lỗi khi tải danh sách khuyến mãi.');
        } finally {
            setLoading(false);
        }
    };

    // Tự động gọi API khi component được tải
    useEffect(() => {
        fetchPromotions();
    }, [user]); // Chạy lại khi user thay đổi
    // =============================

    const handleEdit = (promotion) => {
        setEditingPromotion(promotion);
        // Đảm bảo map đúng dữ liệu từ promo sang form
        setFormData({
            code: promotion.code,
            description: promotion.description,
            discountType: promotion.discountType,
            discountValue: promotion.discountValue,
            maxUsage: promotion.maxUsage || 100, // Dùng maxUsage (hoặc currentUsage nếu bạn muốn)
            startDate: promotion.startDate.split('T')[0], // Format lại ngày cho input type="date"
            endDate: promotion.endDate.split('T')[0],
        });
        setIsDialogOpen(true);
    };

    const handleAdd = () => {
        setEditingPromotion(null);
        setFormData(initialFormData); // Reset về form ban đầu
        setIsDialogOpen(true);
    };

    // === HÀM SUBMIT FORM (LƯU) ĐÃ CẬP NHẬT ===
    const handleSubmit = async () => {
        if (!user?.clubId) {
            toast.error('Lỗi xác thực: Không tìm thấy Club ID.');
            return;
        }

        // Chuẩn bị dữ liệu gửi đi, gán clubId
        const promotionData = {
            ...formData,
            clubId: user.clubId,
            discountValue: parseFloat(formData.discountValue),
            maxUsage: parseInt(formData.maxUsage, 10),
        };

        try {
            if (editingPromotion) {
                // Chế độ Cập nhật
                await customerService.updatePromotion(editingPromotion.id, promotionData);
                toast.success('Cập nhật khuyến mãi thành công!');
            } else {
                // Chế độ Tạo mới
                await customerService.createPromotion(promotionData);
                toast.success('Tạo khuyến mãi thành công!');
            }
            setIsDialogOpen(false);
            fetchPromotions(); // Tải lại dữ liệu
        } catch (error) {
            console.error('Lỗi khi lưu promotion:', error);
            const errMsg = error.response?.data?.message || 'Đã xảy ra lỗi khi lưu.';
            toast.error(errMsg);
        }
    };
    // ==========================================

    // === HÀM XÓA ĐÃ CẬP NHẬT ===
    const handleDelete = async (id) => {
        if (window.confirm('Bạn có chắc muốn xóa khuyến mãi này?')) {
            try {
                await customerService.deletePromotion(id);
                toast.success('Xóa khuyến mãi thành công!');
                fetchPromotions(); // Tải lại dữ liệu
            } catch (error) {
                console.error('Lỗi khi xóa promotion:', error);
                toast.error('Lỗi khi xóa khuyến mãi.');
            }
        }
    };
    // ============================

    const getStatusColor = (status) => {
        // Backend DTO trả về status (vd: "ACTIVE", "EXPIRED", "UPCOMING")
        // Chúng ta sẽ điều chỉnh logic này
        if (status === 'ACTIVE') return 'default'; // 'default' là màu xanh (hoặc chính)
        if (status === 'EXPIRED') return 'destructive'; // 'destructive' là màu đỏ
        return 'secondary'; // 'secondary' là màu xám cho "UPCOMING" hoặc "INACTIVE"
    };

    const formatDiscount = (type, value) => {
        // Khớp với Enum backend
        return type === 'PERCENTAGE' ? `${value}%` : `${value.toLocaleString()} VND`;
    };

    const handleFormChange = (e) => {
        const { id, value } = e.target;
        // Chuyển đổi code sang chữ hoa
        if (id === 'code') {
            setFormData({ ...formData, [id]: value.toUpperCase() });
        } else {
            setFormData({ ...formData, [id]: value });
        }
    };

    const handleSelectChange = (id, value) => {
        setFormData({ ...formData, [id]: value });
    };


    return (
        <div className="space-y-6">
            <div className="flex items-center justify-between">
                <div>
                    <h1 className="text-3xl font-semibold">Promotion Management</h1>
                    <p className="text-muted-foreground">Manage discount codes and promotions</p>
                </div>
                <Button onClick={handleAdd}>
                    <Plus className="h-4 w-4 mr-2" />
                    Add Promotion
                </Button>
            </div>

            <Card>
                <CardHeader>
                    <CardTitle>Active Promotions</CardTitle>
                    <CardDescription>Manage discount codes and promotional offers</CardDescription>
                </CardHeader>
                <CardContent>
                    {loading ? (
                        <p>Đang tải dữ liệu...</p>
                    ) : (
                        <Table>
                            <TableHeader>
                                <TableRow>
                                    <TableHead>Promotion</TableHead>
                                    <TableHead>Code</TableHead>
                                    <TableHead>Discount</TableHead>
                                    <TableHead>Valid Period</TableHead>
                                    <TableHead>Usage</TableHead>
                                    <TableHead>Status</TableHead>
                                    <TableHead>Actions</TableHead>
                                </TableRow>
                            </TableHeader>
                            <TableBody>
                                {promotions.map((promotion) => (
                                    <TableRow key={promotion.id}>
                                        <TableCell>
                                            <div className="space-y-1">
                                                {/* Dùng description từ DTO */}
                                                <p className="font-medium">{promotion.description}</p>
                                                <div className="flex items-center space-x-2">
                                                    <Percent className="h-3 w-3 text-primary" />
                                                    <Badge variant="outline" className="text-xs">
                                                        {promotion.code}
                                                    </Badge>
                                                </div>
                                            </div>
                                        </TableCell>
                                        <TableCell>
                                            <code className="bg-muted px-2 py-1 rounded text-sm">{promotion.code}</code>
                                        </TableCell>
                                        <TableCell>
                                            <div className="flex items-center space-x-1">
                                                {promotion.discountType === 'PERCENTAGE' ? (
                                                    <Percent className="h-4 w-4 text-green-600" />
                                                ) : (
                                                    <DollarSign className="h-4 w-4 text-green-600" />
                                                )}
                                                <span className="font-medium text-green-600">
                          {formatDiscount(promotion.discountType, promotion.discountValue)}
                        </span>
                                            </div>
                                        </TableCell>
                                        <TableCell>
                                            <div className="text-sm space-y-1">
                                                <div className="flex items-center space-x-1">
                                                    <Calendar className="h-3 w-3 text-muted-foreground" />
                                                    {/* Format lại ngày cho đẹp */}
                                                    <span>{new Date(promotion.startDate).toLocaleDateString()}</span>
                                                </div>
                                                <div className="text-muted-foreground">
                                                    to {new Date(promotion.endDate).toLocaleDateString()}
                                                </div>
                                            </div>
                                        </TableCell>
                                        <TableCell>
                                            {/* Dùng currentUsage từ DTO */}
                                            <Badge variant="outline">{promotion.currentUsage || 0} / {promotion.maxUsage}</Badge>
                                        </TableCell>
                                        <TableCell>
                                            <Badge variant={getStatusColor(promotion.status)}>
                                                {promotion.status}
                                            </Badge>
                                        </TableCell>
                                        <TableCell>
                                            <div className="flex items-center space-x-2">
                                                <Button variant="ghost" size="sm" onClick={() => handleEdit(promotion)}>
                                                    <Edit className="h-4 w-4" />
                                                </Button>
                                                <Button variant="ghost" size="sm" onClick={() => handleDelete(promotion.id)}>
                                                    <Trash2 className="h-4 w-4" />
                                                </Button>
                                            </div>
                                        </TableCell>
                                    </TableRow>
                                ))}
                            </TableBody>
                        </Table>
                    )}
                </CardContent>
            </Card>

            <Dialog open={isDialogOpen} onOpenChange={setIsDialogOpen}>
                <DialogContent>
                    <DialogHeader>
                        <DialogTitle>{editingPromotion ? 'Edit Promotion' : 'Create New Promotion'}</DialogTitle>
                        <DialogDescription>
                            {editingPromotion ? 'Update promotion details' : 'Create a new discount promotion'}
                        </DialogDescription>
                    </DialogHeader>
                    <div className="space-y-4">
                        <div className="grid gap-4 md:grid-cols-2">
                            <div className="space-y-2">
                                {/* Đổi htmlFor và id thành "description" */}
                                <Label htmlFor="description">Promotion Name</Label>
                                <Input
                                    id="description"
                                    value={formData.description}
                                    onChange={handleFormChange}
                                    placeholder="Enter promotion name/description"
                                />
                            </div>
                            <div className="space-y-2">
                                <Label htmlFor="code">Promo Code</Label>
                                <Input
                                    id="code"
                                    value={formData.code}
                                    onChange={handleFormChange}
                                    placeholder="Enter promo code"
                                />
                            </div>
                        </div>

                        <div className="grid gap-4 md:grid-cols-2">
                            <div className="space-y-2">
                                <Label htmlFor="discountType">Discount Type</Label>
                                {/* Cập nhật value cho SelectItem */}
                                <Select value={formData.discountType} onValueChange={(value) => handleSelectChange('discountType', value)}>
                                    <SelectTrigger>
                                        <SelectValue placeholder="Select discount type" />
                                    </SelectTrigger>
                                    <SelectContent>
                                        <SelectItem value="PERCENTAGE">Percentage (%)</SelectItem>
                                        <SelectItem value="FIXED_AMOUNT">Fixed Amount (VND)</SelectItem>
                                    </SelectContent>
                                </Select>
                            </div>
                            <div className="space-y-2">
                                <Label htmlFor="discountValue">
                                    {/* Cập nhật Label */}
                                    Discount Value {formData.discountType === 'PERCENTAGE' ? '(%)' : '(VND)'}
                                </Label>
                                <Input
                                    id="discountValue"
                                    type="number"
                                    value={formData.discountValue}
                                    onChange={handleFormChange}
                                    placeholder="Enter discount value"
                                />
                            </div>
                        </div>

                        {/* THÊM TRƯỜNG MAX USAGE */}
                        <div className="space-y-2">
                            <Label htmlFor="maxUsage">Max Usage</Label>
                            <Input
                                id="maxUsage"
                                type="number"
                                value={formData.maxUsage}
                                onChange={handleFormChange}
                                placeholder="Enter max usage count"
                            />
                        </div>

                        <div className="grid gap-4 md:grid-cols-2">
                            <div className="space-y-2">
                                <Label htmlFor="startDate">Start Date</Label>
                                <Input
                                    id="startDate"
                                    type="date"
                                    value={formData.startDate}
                                    onChange={handleFormChange}
                                />
                            </div>
                            <div className="space-y-2">
                                <Label htmlFor="endDate">End Date</Label>
                                <Input
                                    id="endDate"
                                    type="date"
                                    value={formData.endDate}
                                    onChange={handleFormChange}
                                />
                            </div>
                        </div>

                        {/* BỎ TRƯỜNG STATUS */}

                    </div>
                    <DialogFooter>
                        <Button variant="outline" onClick={() => setIsDialogOpen(false)}>
                            Cancel
                        </Button>
                        {/* Gọi handleSubmit khi bấm lưu */}
                        <Button onClick={handleSubmit}>
                            {editingPromotion ? 'Update' : 'Create'} Promotion
                        </Button>
                    </DialogFooter>
                </DialogContent>
            </Dialog>

            {/* Summary Stats (Phần này giữ nguyên, nó tự động cập nhật khi state 'promotions' thay đổi) */}
            <div className="grid gap-4 md:grid-cols-3">
                <Card>
                    <CardHeader className="pb-2">
                        <CardTitle className="text-base">Total Promotions</CardTitle>
                    </CardHeader>
                    <CardContent>
                        <div className="text-2xl font-bold">{promotions.length}</div>
                    </CardContent>
                </Card>

                <Card>
                    <CardHeader className="pb-2">
                        <CardTitle className="text-base">Active Promotions</CardTitle>
                    </CardHeader>
                    <CardContent>
                        <div className="text-2xl font-bold text-green-600">
                            {promotions.filter(p => p.status === 'ACTIVE').length}
                        </div>
                    </CardContent>
                </Card>

                <Card>
                    <CardHeader className="pb-2">
                        <CardTitle className="text-base">Total Usage</CardTitle>
                    </CardHeader>
                    <CardContent>
                        <div className="text-2xl font-bold">
                            {promotions.reduce((sum, p) => sum + (p.currentUsage || 0), 0)}
                        </div>
                    </CardContent>
                </Card>
            </div>
        </div>
    );
}

// Export default nếu file của bạn dùng 'export default'
// export default PromotionManagement;