import React, { useEffect, useState } from 'react';
import axios from 'axios';
import {
    Card, CardContent, CardDescription, CardHeader, CardTitle
} from '../ui/card';
import { Button } from '../ui/button';
import { Input } from '../ui/input';
import { Label } from '../ui/label';
import {
    Table, TableBody, TableCell, TableHead, TableHeader, TableRow
} from '../ui/table';
import { Badge } from '../ui/badge';
import {
    Select, SelectContent, SelectItem, SelectTrigger, SelectValue
} from '../ui/select';
import {
    Dialog, DialogContent, DialogFooter, DialogHeader, DialogTitle
} from '../ui/dialog';
import { Percent, Plus, Edit, Trash2, Calendar, DollarSign } from 'lucide-react';
import { API_CONFIG } from '../config/apiConfig';

export function PromotionManagement() {
    const [promotions, setPromotions] = useState([]);
    const [loading, setLoading] = useState(true);
    const [isDialogOpen, setIsDialogOpen] = useState(false);
    const [editingPromotion, setEditingPromotion] = useState(null);

    const [formData, setFormData] = useState({
        promotionName: '',
        promotionCode: '',
        discountType: 'PERCENTAGE',
        discountValue: 10,
        startDate: '',
        endDate: '',
        isActive: true
    });

    const CLUB_ID = 1; // 👈 Thay bằng ID thật hoặc lấy từ user context

    // 🔹 Lấy danh sách khuyến mãi
    useEffect(() => {
        fetchPromotions();
    }, []);

    const fetchPromotions = async () => {
        setLoading(true);
        try {
            const res = await axios.get(`${API_CONFIG.BASE_URL}/promotions?clubId=${CLUB_ID}`);
            setPromotions(res.data.promotions || []);
        } catch (err) {
            console.error('❌ Lỗi khi tải danh sách khuyến mãi:', err);
        } finally {
            setLoading(false);
        }
    };

    // 🔹 Thêm hoặc cập nhật khuyến mãi
    const handleSave = async () => {
        try {
            const payload = {
                clubId: CLUB_ID,
                promotionName: formData.promotionName,
                promotionCode: formData.promotionCode.toUpperCase(),
                discountType: formData.discountType,
                discountValue: formData.discountValue,
                startDate: new Date(formData.startDate).toISOString(),
                endDate: new Date(formData.endDate).toISOString(),
                isActive: formData.isActive
            };

            if (editingPromotion) {
                await axios.patch(`${API_CONFIG.BASE_URL}/promotions/${editingPromotion.promotionId}`, payload);
            } else {
                await axios.post(`${API_CONFIG.BASE_URL}/promotions`, payload);
            }

            setIsDialogOpen(false);
            fetchPromotions();
        } catch (err) {
            console.error('❌ Lỗi khi lưu khuyến mãi:', err);
            alert('Không thể lưu khuyến mãi. Vui lòng kiểm tra dữ liệu nhập.');
        }
    };

    // 🔹 Xóa khuyến mãi
    const handleDelete = async (id) => {
        if (!window.confirm('Bạn có chắc muốn xóa khuyến mãi này không?')) return;
        try {
            await axios.delete(`${API_CONFIG.BASE_URL}/promotions/${id}`);
            fetchPromotions();
        } catch (err) {
            console.error('❌ Lỗi khi xóa khuyến mãi:', err);
        }
    };

    // 🔹 Mở form chỉnh sửa
    const handleEdit = (promotion) => {
        setEditingPromotion(promotion);
        setFormData({
            promotionName: promotion.promotionName,
            promotionCode: promotion.promotionCode,
            discountType: promotion.discountType,
            discountValue: promotion.discountValue,
            startDate: promotion.startDate?.slice(0, 10),
            endDate: promotion.endDate?.slice(0, 10),
            isActive: promotion.isActive
        });
        setIsDialogOpen(true);
    };

    const handleAdd = () => {
        setEditingPromotion(null);
        setFormData({
            promotionName: '',
            promotionCode: '',
            discountType: 'PERCENTAGE',
            discountValue: 10,
            startDate: '',
            endDate: '',
            isActive: true
        });
        setIsDialogOpen(true);
    };

    const getStatusColor = (isActive) => (isActive ? 'default' : 'secondary');
    const formatDiscount = (type, value) =>
        type === 'PERCENTAGE' ? `${value}%` : `$${value}`;

    if (loading) return <p>Đang tải dữ liệu khuyến mãi...</p>;

    return (
        <div className="space-y-6">
            <div className="flex items-center justify-between">
                <div>
                    <h1 className="text-3xl font-semibold">Promotion Management</h1>
                    <p className="text-muted-foreground">
                        Manage discount codes and promotions
                    </p>
                </div>
                <Button onClick={handleAdd}>
                    <Plus className="h-4 w-4 mr-2" />
                    Add Promotion
                </Button>
            </div>

            {/* Bảng khuyến mãi */}
            <Card>
                <CardHeader>
                    <CardTitle>Promotion List</CardTitle>
                    <CardDescription>List of all promotions</CardDescription>
                </CardHeader>
                <CardContent>
                    <Table>
                        <TableHeader>
                            <TableRow>
                                <TableHead>Name</TableHead>
                                <TableHead>Code</TableHead>
                                <TableHead>Discount</TableHead>
                                <TableHead>Period</TableHead>
                                <TableHead>Status</TableHead>
                                <TableHead>Actions</TableHead>
                            </TableRow>
                        </TableHeader>
                        <TableBody>
                            {promotions.map((promotion) => (
                                <TableRow key={promotion.promotionId}>
                                    <TableCell>{promotion.promotionName}</TableCell>
                                    <TableCell>
                                        <Badge>{promotion.promotionCode}</Badge>
                                    </TableCell>
                                    <TableCell>
                                        {formatDiscount(promotion.discountType, promotion.discountValue)}
                                    </TableCell>
                                    <TableCell>
                                        {promotion.startDate?.slice(0, 10)} → {promotion.endDate?.slice(0, 10)}
                                    </TableCell>
                                    <TableCell>
                                        <Badge variant={getStatusColor(promotion.isActive)}>
                                            {promotion.isActive ? 'Active' : 'Inactive'}
                                        </Badge>
                                    </TableCell>
                                    <TableCell>
                                        <div className="flex space-x-2">
                                            <Button size="sm" variant="ghost" onClick={() => handleEdit(promotion)}>
                                                <Edit className="h-4 w-4" />
                                            </Button>
                                            <Button size="sm" variant="ghost" onClick={() => handleDelete(promotion.promotionId)}>
                                                <Trash2 className="h-4 w-4" />
                                            </Button>
                                        </div>
                                    </TableCell>
                                </TableRow>
                            ))}
                        </TableBody>
                    </Table>
                </CardContent>
            </Card>

            {/* Dialog thêm/sửa */}
            <Dialog open={isDialogOpen} onOpenChange={setIsDialogOpen}>
                <DialogContent>
                    <DialogHeader>
                        <DialogTitle>
                            {editingPromotion ? 'Edit Promotion' : 'Create Promotion'}
                        </DialogTitle>
                    </DialogHeader>

                    <div className="space-y-4">
                        <div className="grid gap-4 md:grid-cols-2">
                            <div>
                                <Label>Name</Label>
                                <Input
                                    value={formData.promotionName}
                                    onChange={(e) =>
                                        setFormData({ ...formData, promotionName: e.target.value })
                                    }
                                />
                            </div>
                            <div>
                                <Label>Code</Label>
                                <Input
                                    value={formData.promotionCode}
                                    onChange={(e) =>
                                        setFormData({
                                            ...formData,
                                            promotionCode: e.target.value.toUpperCase()
                                        })
                                    }
                                />
                            </div>
                        </div>

                        <div className="grid gap-4 md:grid-cols-2">
                            <div>
                                <Label>Discount Type</Label>
                                <Select
                                    value={formData.discountType}
                                    onValueChange={(v) =>
                                        setFormData({ ...formData, discountType: v })
                                    }
                                >
                                    <SelectTrigger>
                                        <SelectValue />
                                    </SelectTrigger>
                                    <SelectContent>
                                        <SelectItem value="PERCENTAGE">Percentage (%)</SelectItem>
                                        <SelectItem value="FIXED">Fixed ($)</SelectItem>
                                    </SelectContent>
                                </Select>
                            </div>
                            <div>
                                <Label>Discount Value</Label>
                                <Input
                                    type="number"
                                    value={formData.discountValue}
                                    onChange={(e) =>
                                        setFormData({
                                            ...formData,
                                            discountValue: parseFloat(e.target.value)
                                        })
                                    }
                                />
                            </div>
                        </div>

                        <div className="grid gap-4 md:grid-cols-2">
                            <div>
                                <Label>Start Date</Label>
                                <Input
                                    type="date"
                                    value={formData.startDate}
                                    onChange={(e) =>
                                        setFormData({ ...formData, startDate: e.target.value })
                                    }
                                />
                            </div>
                            <div>
                                <Label>End Date</Label>
                                <Input
                                    type="date"
                                    value={formData.endDate}
                                    onChange={(e) =>
                                        setFormData({ ...formData, endDate: e.target.value })
                                    }
                                />
                            </div>
                        </div>

                        <div>
                            <Label>Status</Label>
                            <Select
                                value={formData.isActive ? 'active' : 'inactive'}
                                onValueChange={(v) =>
                                    setFormData({ ...formData, isActive: v === 'active' })
                                }
                            >
                                <SelectTrigger>
                                    <SelectValue />
                                </SelectTrigger>
                                <SelectContent>
                                    <SelectItem value="active">Active</SelectItem>
                                    <SelectItem value="inactive">Inactive</SelectItem>
                                </SelectContent>
                            </Select>
                        </div>
                    </div>

                    <DialogFooter>
                        <Button variant="outline" onClick={() => setIsDialogOpen(false)}>
                            Cancel
                        </Button>
                        <Button onClick={handleSave}>
                            {editingPromotion ? 'Update' : 'Create'}
                        </Button>
                    </DialogFooter>
                </DialogContent>
            </Dialog>
        </div>
    );
}
