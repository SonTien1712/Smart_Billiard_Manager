import React, { useEffect, useMemo, useState } from 'react';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '../ui/card';
import { Button } from '../ui/button';
import { Input } from '../ui/input';
import { Label } from '../ui/label';
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '../ui/table';
import { Badge } from '../ui/badge';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '../ui/select';
import { Dialog, DialogContent, DialogDescription, DialogFooter, DialogHeader, DialogTitle } from '../ui/dialog';
import { Percent, Plus, Edit, Trash2, Calendar, DollarSign, Users, Clock, Search, Filter, AlertCircle, CheckCircle2, XCircle } from 'lucide-react';
import { customerService } from '../../services/customerService';
import { useAuth } from '../AuthProvider';

// Helper: Format currency VNĐ
const formatCurrency = (value) => {
    const num = typeof value === 'string' ? parseFloat(value) : value;
    return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(num || 0);
};

// Helper: Format date to Vietnamese
const formatDate = (dateStr) => {
    if (!dateStr) return '';
    const date = new Date(dateStr);
    return date.toLocaleDateString('vi-VN', { day: '2-digit', month: '2-digit', year: 'numeric' });
};

// Helper: Check if promotion is valid/active
const getPromotionStatus = (promotion) => {
    const now = new Date();
    const start = new Date(promotion.startDate);
    const end = new Date(promotion.endDate);

    if (!promotion.status || promotion.status === 'inactive') {
        return { label: 'Đã tắt', color: 'secondary', icon: XCircle };
    }

    if (now < start) {
        return { label: 'Chưa bắt đầu', color: 'warning', icon: Clock };
    }

    if (now > end) {
        return { label: 'Hết hạn', color: 'destructive', icon: XCircle };
    }

    // Check usage limit
    if (promotion.usageLimit > 0 && promotion.usageCount >= promotion.usageLimit) {
        return { label: 'Hết lượt', color: 'destructive', icon: XCircle };
    }

    return { label: 'Đang hoạt động', color: 'success', icon: CheckCircle2 };
};

export function PromotionManagement({ onPageChange }) {
    const { user } = useAuth();
    const directClubId = useMemo(() => user?.clubId || user?.club?.id || user?.customerClubId, [user]);
    const [effectiveClubId, setEffectiveClubId] = useState(null);

    const [promotions, setPromotions] = useState([]);
    const [filteredPromotions, setFilteredPromotions] = useState([]);
    const [clubs, setClubs] = useState([]);

    const [isDialogOpen, setIsDialogOpen] = useState(false);
    const [editingPromotion, setEditingPromotion] = useState(null);
    const [searchTerm, setSearchTerm] = useState('');
    const [statusFilter, setStatusFilter] = useState('all');

    const [formData, setFormData] = useState({
        code: '',
        name: '',
        promotionType: 'percentage',
        promotionValue: 10,
        startDate: '',
        endDate: '',
        usageLimit: 0,
        description: '',
        status: 'active'
    });

    const [errors, setErrors] = useState({});

    // Validate form
    const validateForm = () => {
        const newErrors = {};

        if (!formData.name?.trim()) {
            newErrors.name = 'Tên khuyến mãi không được để trống';
        } else if (formData.name.length < 3) {
            newErrors.name = 'Tên khuyến mãi phải có ít nhất 3 ký tự';
        }

        if (!formData.code?.trim()) {
            newErrors.code = 'Mã khuyến mãi không được để trống';
        } else if (!/^[A-Z0-9_-]+$/.test(formData.code)) {
            newErrors.code = 'Mã chỉ chứa chữ in hoa, số, gạch dưới và gạch ngang';
        }

        if (!formData.promotionValue || formData.promotionValue <= 0) {
            newErrors.promotionValue = 'Giá trị khuyến mãi phải lớn hơn 0';
        } else if (formData.promotionType === 'percentage' && formData.promotionValue > 100) {
            newErrors.promotionValue = 'Giá trị phần trăm không được vượt quá 100';
        }

        if (!formData.startDate) {
            newErrors.startDate = 'Vui lòng chọn ngày bắt đầu';
        }

        if (!formData.endDate) {
            newErrors.endDate = 'Vui lòng chọn ngày kết thúc';
        } else if (formData.startDate && new Date(formData.endDate) <= new Date(formData.startDate)) {
            newErrors.endDate = 'Ngày kết thúc phải sau ngày bắt đầu';
        }

        if (formData.usageLimit < 0) {
            newErrors.usageLimit = 'Giới hạn sử dụng không được âm';
        }

        setErrors(newErrors);
        return Object.keys(newErrors).length === 0;
    };

    const handleEdit = (promotion) => {
        setEditingPromotion(promotion);
        setFormData({
            code: promotion.code,
            name: promotion.name,
            promotionType: promotion.promotionType,
            promotionValue: promotion.promotionValue,
            startDate: promotion.startDate,
            endDate: promotion.endDate,
            usageLimit: promotion.usageLimit || 0,
            description: promotion.description || '',
            status: promotion.status
        });
        setErrors({});
        setIsDialogOpen(true);
    };

    const handleAdd = () => {
        setEditingPromotion(null);
        setFormData({
            code: '',
            name: '',
            promotionType: 'percentage',
            promotionValue: 10,
            startDate: '',
            endDate: '',
            usageLimit: 0,
            description: '',
            status: 'active'
        });
        setErrors({});
        setIsDialogOpen(true);
    };

    // Resolve effective clubId
    useEffect(() => {
        const resolveClub = async () => {
            const custId = user?.customerId || user?.id;
            try {
                let list = [];
                if (custId) {
                    list = await customerService.getClubsByCustomer(custId);
                }
                const normalized = (Array.isArray(list) ? list : []).map(c => ({
                    id: c.id ?? c.clubId ?? c.ClubID ?? null,
                    name: c.clubName ?? c.name ?? `Câu lạc bộ #${c.id ?? c.clubId}`,
                })).filter(x => x.id);
                if (normalized.length > 0) {
                    setClubs(normalized);
                    setEffectiveClubId(String(normalized[0].id));
                    return;
                }
                if (directClubId) {
                    setClubs([{ id: directClubId, name: `Câu lạc bộ #${directClubId}` }]);
                    setEffectiveClubId(String(directClubId));
                }
            } catch (_) {
                if (directClubId) {
                    setClubs([{ id: directClubId, name: `Câu lạc bộ #${directClubId}` }]);
                    setEffectiveClubId(String(directClubId));
                }
            }
        };
        resolveClub();
    }, [directClubId, user]);

    // Load promotions
    useEffect(() => {
        const load = async () => {
            try {
                if (!effectiveClubId) return;
                const list = await customerService.getPromotions(Number(effectiveClubId), {
                    page: 0,
                    size: 100,
                    sortBy: 'id',
                    sortDir: 'desc'
                });
                const mapped = (Array.isArray(list) ? list : []).map(p => ({
                    id: p.promotionId ?? p.id,
                    code: p.promotionCode,
                    name: p.promotionName,
                    promotionType: (() => {
                        const t = ((p.promotionType||'')+ '').toLowerCase();
                        return t.includes('fixed') ? 'fixed' : 'percentage';
                    })(),
                    promotionValue: Number(p.promotionValue || 0),
                    startDate: p.startDate ? new Date(p.startDate).toISOString().slice(0,10) : '',
                    endDate: p.endDate ? new Date(p.endDate).toISOString().slice(0,10) : '',
                    status: p.isActive ? 'active' : 'inactive',
                    usageCount: Number(p.usedCount || 0),
                    usageLimit: Number(p.usageLimit || 0),
                    description: p.description || ''
                }));
                setPromotions(mapped);
                setFilteredPromotions(mapped);
            } catch (e) {
                console.error('Load promotions failed', e);
                setPromotions([]);
                setFilteredPromotions([]);
            }
        };
        load();
    }, [effectiveClubId]);

    // Filter promotions
    useEffect(() => {
        let filtered = [...promotions];

        // Search filter
        if (searchTerm) {
            filtered = filtered.filter(p =>
                p.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
                p.code.toLowerCase().includes(searchTerm.toLowerCase())
            );
        }

        // Status filter
        if (statusFilter !== 'all') {
            filtered = filtered.filter(p => {
                const status = getPromotionStatus(p);
                if (statusFilter === 'active') return status.label === 'Đang hoạt động';
                if (statusFilter === 'expired') return status.label === 'Hết hạn';
                if (statusFilter === 'upcoming') return status.label === 'Chưa bắt đầu';
                if (statusFilter === 'disabled') return status.label === 'Đã tắt';
                if (statusFilter === 'full') return status.label === 'Hết lượt';
                return true;
            });
        }

        setFilteredPromotions(filtered);
    }, [searchTerm, statusFilter, promotions]);

    const handleSave = async () => {
        if (!validateForm()) {
            return;
        }

        try {
            if (!effectiveClubId) {
                alert('Chưa xác định được Club. Vui lòng kiểm tra tài khoản.');
                return;
            }

            const payload = {
                customerId: (user?.customerId ?? user?.id),
                clubId: Number(effectiveClubId),
                promotionName: formData.name,
                promotionCode: formData.code,
                promotionType: formData.promotionType === 'percentage' ? 'PERCENTAGE' : 'FIXED_AMOUNT',
                promotionValue: Number(formData.promotionValue || 0),
                startDate: formData.startDate ? new Date(formData.startDate + 'T00:00:00Z').toISOString() : null,
                endDate: formData.endDate ? new Date(formData.endDate + 'T00:00:00Z').toISOString() : null,
                usageLimit: Number(formData.usageLimit || 0),
                description: formData.description || ''
            };

            if (editingPromotion && editingPromotion.id) {
                const res = await customerService.updatePromotion(editingPromotion.id, payload);
                const p = res || {};
                const updated = {
                    id: p.promotionId ?? editingPromotion.id,
                    code: p.promotionCode ?? formData.code,
                    name: p.promotionName ?? formData.name,
                    promotionType: (() => {
                        const t = ((p.promotionType||payload.promotionType)||'')+'';
                        return t.toLowerCase().includes('fixed') ? 'fixed' : 'percentage';
                    })(),
                    promotionValue: Number(((p.promotionValue ?? formData.promotionValue) ?? 0)),
                    startDate: (p.startDate ? new Date(p.startDate) : new Date(formData.startDate)).toISOString().slice(0,10),
                    endDate: (p.endDate ? new Date(p.endDate) : new Date(formData.endDate)).toISOString().slice(0,10),
                    status: (p.isActive ?? true) ? 'active' : 'inactive',
                    usageCount: Number(p.usedCount ?? editingPromotion.usageCount ?? 0),
                    usageLimit: Number(p.usageLimit ?? formData.usageLimit ?? 0),
                    description: p.description ?? formData.description ?? ''
                };
                setPromotions(promotions.map(x => x.id === editingPromotion.id ? updated : x));
            } else {
                const p = await customerService.createPromotion(payload);
                const created = {
                    id: p.promotionId ?? p.id ?? Date.now().toString(),
                    code: p.promotionCode ?? payload.promotionCode,
                    name: p.promotionName ?? payload.promotionName,
                    promotionType: (() => {
                        const t = ((p.promotionType||payload.promotionType)||'')+'';
                        return t.toLowerCase().includes('fixed') ? 'fixed' : 'percentage';
                    })(),
                    promotionValue: Number(((p.promotionValue ?? payload.promotionValue) ?? 0)),
                    startDate: (p.startDate ? new Date(p.startDate) : new Date(formData.startDate)).toISOString().slice(0,10),
                    endDate: (p.endDate ? new Date(p.endDate) : new Date(formData.endDate)).toISOString().slice(0,10),
                    status: (p.isActive ?? true) ? 'active' : 'inactive',
                    usageCount: Number(p.usedCount ?? 0),
                    usageLimit: Number(p.usageLimit ?? formData.usageLimit ?? 0),
                    description: p.description ?? formData.description ?? ''
                };
                setPromotions([created, ...promotions]);
            }
            setIsDialogOpen(false);
            setErrors({});
        } catch (e) {
            console.error('Save promotion failed', e);
            alert('Lưu khuyến mãi thất bại: ' + (e.message || 'Lỗi không xác định'));
        }
    };

    const handleDelete = async (id) => {
        if (!window.confirm('Bạn có chắc chắn muốn xóa khuyến mãi này?')) {
            return;
        }

        try {
            await customerService.deletePromotion(id);
            setPromotions(promotions.filter(promo => promo.id !== id));
        } catch (e) {
            console.error('Delete promotion failed', e);
            alert('Xóa khuyến mãi thất bại');
        }
    };

    const formatPromotionValue = (type, value) => {
        return type === 'percentage'
            ? `${value}%`
            : formatCurrency(value);
    };

    // Stats calculation
    const stats = useMemo(() => {
        const now = new Date();
        return {
            total: promotions.length,
            active: promotions.filter(p => {
                const status = getPromotionStatus(p);
                return status.label === 'Đang hoạt động';
            }).length,
            expired: promotions.filter(p => new Date(p.endDate) < now).length,
            totalUsage: promotions.reduce((sum, p) => sum + p.usageCount, 0)
        };
    }, [promotions]);

    return (
        <div className="space-y-6">
            {/* Header */}
            <div className="flex items-center justify-between">
                <div>
                    <h1 className="text-3xl font-semibold">Quản lý Khuyến mãi</h1>
                    <p className="text-muted-foreground">Quản lý mã giảm giá và chương trình khuyến mãi</p>
                </div>
                <div className="flex items-center gap-3">
                    <div className="min-w-[220px]">
                        <Label htmlFor="club-select">Câu lạc bộ</Label>
                        <Select value={effectiveClubId ?? ''} onValueChange={(v) => setEffectiveClubId(v)}>
                            <SelectTrigger id="club-select">
                                <SelectValue placeholder="Chọn câu lạc bộ" />
                            </SelectTrigger>
                            <SelectContent>
                                {clubs.map(c => (
                                    <SelectItem key={c.id} value={String(c.id)}>{c.name}</SelectItem>
                                ))}
                            </SelectContent>
                        </Select>
                    </div>
                    <Button onClick={handleAdd} className="mt-6">
                        <Plus className="h-4 w-4 mr-2" />
                        Thêm khuyến mãi
                    </Button>
                </div>
            </div>

            {/* Summary Stats */}
            <div className="grid gap-4 md:grid-cols-4">
                <Card>
                    <CardHeader className="pb-2">
                        <CardTitle className="text-base">Tổng khuyến mãi</CardTitle>
                    </CardHeader>
                    <CardContent>
                        <div className="text-2xl font-bold">{stats.total}</div>
                    </CardContent>
                </Card>

                <Card>
                    <CardHeader className="pb-2">
                        <CardTitle className="text-base">Đang hoạt động</CardTitle>
                    </CardHeader>
                    <CardContent>
                        <div className="text-2xl font-bold text-green-600">{stats.active}</div>
                    </CardContent>
                </Card>

                <Card>
                    <CardHeader className="pb-2">
                        <CardTitle className="text-base">Đã hết hạn</CardTitle>
                    </CardHeader>
                    <CardContent>
                        <div className="text-2xl font-bold text-orange-600">{stats.expired}</div>
                    </CardContent>
                </Card>

                <Card>
                    <CardHeader className="pb-2">
                        <CardTitle className="text-base">Tổng lượt sử dụng</CardTitle>
                    </CardHeader>
                    <CardContent>
                        <div className="text-2xl font-bold">{stats.totalUsage}</div>
                    </CardContent>
                </Card>
            </div>

            {/* Filters */}
            <Card>
                <CardContent className="pt-6">
                    <div className="flex gap-4">
                        <div className="flex-1">
                            <div className="relative">
                                <Search className="absolute left-3 top-3 h-4 w-4 text-muted-foreground" />
                                <Input
                                    placeholder="Tìm kiếm theo tên hoặc mã..."
                                    value={searchTerm}
                                    onChange={(e) => setSearchTerm(e.target.value)}
                                    className="pl-10"
                                />
                            </div>
                        </div>
                        <div className="w-[200px]">
                            <Select value={statusFilter} onValueChange={setStatusFilter}>
                                <SelectTrigger>
                                    <Filter className="h-4 w-4 mr-2" />
                                    <SelectValue placeholder="Lọc trạng thái" />
                                </SelectTrigger>
                                <SelectContent>
                                    <SelectItem value="all">Tất cả</SelectItem>
                                    <SelectItem value="active">Đang hoạt động</SelectItem>
                                    <SelectItem value="upcoming">Chưa bắt đầu</SelectItem>
                                    <SelectItem value="expired">Hết hạn</SelectItem>
                                    <SelectItem value="full">Hết lượt</SelectItem>
                                    <SelectItem value="disabled">Đã tắt</SelectItem>
                                </SelectContent>
                            </Select>
                        </div>
                    </div>
                </CardContent>
            </Card>

            {/* Promotions Table */}
            <Card>
                <CardHeader>
                    <CardTitle>Danh sách Khuyến mãi</CardTitle>
                    <CardDescription>
                        Hiển thị {filteredPromotions.length} / {promotions.length} khuyến mãi
                    </CardDescription>
                </CardHeader>
                <CardContent>
                    <Table>
                        <TableHeader>
                            <TableRow>
                                <TableHead>Thông tin</TableHead>
                                <TableHead>Mã</TableHead>
                                <TableHead>Giá trị</TableHead>
                                <TableHead>Thời gian</TableHead>
                                <TableHead>Sử dụng</TableHead>
                                <TableHead>Trạng thái</TableHead>
                                <TableHead>Thao tác</TableHead>
                            </TableRow>
                        </TableHeader>
                        <TableBody>
                            {filteredPromotions.length === 0 ? (
                                <TableRow>
                                    <TableCell colSpan={7} className="text-center text-muted-foreground py-8">
                                        Không có khuyến mãi nào
                                    </TableCell>
                                </TableRow>
                            ) : (
                                filteredPromotions.map((promotion) => {
                                    const status = getPromotionStatus(promotion);
                                    const StatusIcon = status.icon;

                                    return (
                                        <TableRow key={promotion.id}>
                                            <TableCell>
                                                <div className="space-y-1">
                                                    <p className="font-medium">{promotion.name}</p>
                                                    {promotion.description && (
                                                        <p className="text-xs text-muted-foreground line-clamp-1">
                                                            {promotion.description}
                                                        </p>
                                                    )}
                                                </div>
                                            </TableCell>
                                            <TableCell>
                                                <code className="bg-muted px-2 py-1 rounded text-sm font-mono">
                                                    {promotion.code}
                                                </code>
                                            </TableCell>
                                            <TableCell>
                                                <div className="flex items-center space-x-1">
                                                    {promotion.promotionType === 'percentage' ? (
                                                        <Percent className="h-4 w-4 text-green-600" />
                                                    ) : (
                                                        <DollarSign className="h-4 w-4 text-green-600" />
                                                    )}
                                                    <span className="font-medium text-green-600">
                            {formatPromotionValue(promotion.promotionType, promotion.promotionValue)}
                          </span>
                                                </div>
                                            </TableCell>
                                            <TableCell>
                                                <div className="text-sm space-y-1">
                                                    <div className="flex items-center space-x-1">
                                                        <Calendar className="h-3 w-3 text-muted-foreground" />
                                                        <span>{formatDate(promotion.startDate)}</span>
                                                    </div>
                                                    <div className="text-muted-foreground">
                                                        đến {formatDate(promotion.endDate)}
                                                    </div>
                                                </div>
                                            </TableCell>
                                            <TableCell>
                                                <div className="space-y-1">
                                                    <div className="flex items-center gap-1">
                                                        <Users className="h-3 w-3 text-muted-foreground" />
                                                        <span className="text-sm font-medium">
                              {promotion.usageCount}
                            </span>
                                                        {promotion.usageLimit > 0 && (
                                                            <span className="text-xs text-muted-foreground">
                                / {promotion.usageLimit}
                              </span>
                                                        )}
                                                    </div>
                                                    {promotion.usageLimit > 0 && (
                                                        <div className="w-full bg-muted rounded-full h-1.5">
                                                            <div
                                                                className="bg-primary h-1.5 rounded-full transition-all"
                                                                style={{
                                                                    width: `${Math.min(100, (promotion.usageCount / promotion.usageLimit) * 100)}%`
                                                                }}
                                                            />
                                                        </div>
                                                    )}
                                                    {promotion.usageLimit === 0 && (
                                                        <span className="text-xs text-muted-foreground">Không giới hạn</span>
                                                    )}
                                                </div>
                                            </TableCell>
                                            <TableCell>
                                                <Badge
                                                    variant={status.color === 'success' ? 'default' :
                                                        status.color === 'warning' ? 'outline' :
                                                            'secondary'}
                                                    className="flex items-center gap-1 w-fit"
                                                >
                                                    <StatusIcon className="h-3 w-3" />
                                                    {status.label}
                                                </Badge>
                                            </TableCell>
                                            <TableCell>
                                                <div className="flex items-center space-x-2">
                                                    <Button
                                                        variant="ghost"
                                                        size="sm"
                                                        onClick={() => handleEdit(promotion)}
                                                    >
                                                        <Edit className="h-4 w-4" />
                                                    </Button>
                                                    <Button
                                                        variant="ghost"
                                                        size="sm"
                                                        onClick={() => handleDelete(promotion.id)}
                                                    >
                                                        <Trash2 className="h-4 w-4 text-destructive" />
                                                    </Button>
                                                </div>
                                            </TableCell>
                                        </TableRow>
                                    );
                                })
                            )}
                        </TableBody>
                    </Table>
                </CardContent>
            </Card>

            {/* Create/Edit Dialog */}
            <Dialog open={isDialogOpen} onOpenChange={setIsDialogOpen}>
                <DialogContent className="max-w-2xl max-h-[90vh] overflow-y-auto">
                    <DialogHeader>
                        <DialogTitle>
                            {editingPromotion ? 'Chỉnh sửa Khuyến mãi' : 'Tạo Khuyến mãi mới'}
                        </DialogTitle>
                        <DialogDescription>
                            {editingPromotion ? 'Cập nhật thông tin khuyến mãi' : 'Tạo mã giảm giá và chương trình khuyến mãi mới'}
                        </DialogDescription>
                    </DialogHeader>

                    <div className="space-y-4">
                        {/* Promotion Name & Code */}
                        <div className="grid gap-4 md:grid-cols-2">
                            <div className="space-y-2">
                                <Label htmlFor="name">
                                    Tên khuyến mãi <span className="text-destructive">*</span>
                                </Label>
                                <Input
                                    id="name"
                                    value={formData.name}
                                    onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                                    placeholder="VD: Giảm giá cuối tuần"
                                    className={errors.name ? 'border-destructive' : ''}
                                />
                                {errors.name && (
                                    <p className="text-xs text-destructive flex items-center gap-1">
                                        <AlertCircle className="h-3 w-3" />
                                        {errors.name}
                                    </p>
                                )}
                            </div>

                            <div className="space-y-2">
                                <Label htmlFor="code">
                                    Mã khuyến mãi <span className="text-destructive">*</span>
                                </Label>
                                <Input
                                    id="code"
                                    value={formData.code}
                                    onChange={(e) => setFormData({ ...formData, code: e.target.value.toUpperCase() })}
                                    placeholder="VD: SUMMER2024"
                                    className={errors.code ? 'border-destructive' : ''}
                                />
                                {errors.code && (
                                    <p className="text-xs text-destructive flex items-center gap-1">
                                        <AlertCircle className="h-3 w-3" />
                                        {errors.code}
                                    </p>
                                )}
                            </div>
                        </div>

                        {/* Promotion Type & Value */}
                        <div className="grid gap-4 md:grid-cols-2">
                            <div className="space-y-2">
                                <Label htmlFor="promotionType">
                                    Loại khuyến mãi <span className="text-destructive">*</span>
                                </Label>
                                <Select
                                    value={formData.promotionType}
                                    onValueChange={(value) => setFormData({ ...formData, promotionType: value })}
                                >
                                    <SelectTrigger>
                                        <SelectValue placeholder="Chọn loại khuyến mãi" />
                                    </SelectTrigger>
                                    <SelectContent>
                                        <SelectItem value="percentage">Giảm theo phần trăm (%)</SelectItem>
                                        <SelectItem value="fixed">Giảm số tiền cố định (VNĐ)</SelectItem>
                                    </SelectContent>
                                </Select>
                            </div>

                            <div className="space-y-2">
                                <Label htmlFor="promotionValue">
                                    Giá trị khuyến mãi {formData.promotionType === 'percentage' ? '(%)' : '(VNĐ)'} <span className="text-destructive">*</span>
                                </Label>
                                <Input
                                    id="promotionValue"
                                    type="number"
                                    min="0"
                                    max={formData.promotionType === 'percentage' ? 100 : undefined}
                                    value={formData.promotionValue === '' ? '' : String(formData.promotionValue)}
                                    onChange={(e) => {
                                        const v = e.target.value;
                                        setFormData({ ...formData, promotionValue: v === '' ? '' : Number(v) });
                                    }}
                                    placeholder={formData.promotionType === 'percentage' ? 'VD: 20' : 'VD: 50000'}
                                    className={errors.promotionValue ? 'border-destructive' : ''}
                                />
                                {errors.promotionValue && (
                                    <p className="text-xs text-destructive flex items-center gap-1">
                                        <AlertCircle className="h-3 w-3" />
                                        {errors.promotionValue}
                                    </p>
                                )}
                                {formData.promotionValue > 0 && (
                                    <p className="text-xs text-muted-foreground">
                                        Giảm: {formatPromotionValue(formData.promotionType, formData.promotionValue)}
                                    </p>
                                )}
                            </div>
                        </div>

                        {/* Start Date & End Date */}
                        <div className="grid gap-4 md:grid-cols-2">
                            <div className="space-y-2">
                                <Label htmlFor="startDate">
                                    Ngày bắt đầu <span className="text-destructive">*</span>
                                </Label>
                                <Input
                                    id="startDate"
                                    type="date"
                                    value={formData.startDate}
                                    onChange={(e) => setFormData({ ...formData, startDate: e.target.value })}
                                    className={errors.startDate ? 'border-destructive' : ''}
                                />
                                {errors.startDate && (
                                    <p className="text-xs text-destructive flex items-center gap-1">
                                        <AlertCircle className="h-3 w-3" />
                                        {errors.startDate}
                                    </p>
                                )}
                            </div>

                            <div className="space-y-2">
                                <Label htmlFor="endDate">
                                    Ngày kết thúc <span className="text-destructive">*</span>
                                </Label>
                                <Input
                                    id="endDate"
                                    type="date"
                                    value={formData.endDate}
                                    onChange={(e) => setFormData({ ...formData, endDate: e.target.value })}
                                    className={errors.endDate ? 'border-destructive' : ''}
                                />
                                {errors.endDate && (
                                    <p className="text-xs text-destructive flex items-center gap-1">
                                        <AlertCircle className="h-3 w-3" />
                                        {errors.endDate}
                                    </p>
                                )}
                            </div>
                        </div>

                        {/* Usage Limit */}
                        <div className="space-y-2">
                            <Label htmlFor="usageLimit">
                                Giới hạn số lần sử dụng
                            </Label>
                            <Input
                                id="usageLimit"
                                type="number"
                                min="0"
                                value={formData.usageLimit}
                                onChange={(e) => setFormData({ ...formData, usageLimit: Number(e.target.value) || 0 })}
                                placeholder="0 = Không giới hạn"
                                className={errors.usageLimit ? 'border-destructive' : ''}
                            />
                            {errors.usageLimit && (
                                <p className="text-xs text-destructive flex items-center gap-1">
                                    <AlertCircle className="h-3 w-3" />
                                    {errors.usageLimit}
                                </p>
                            )}
                            <p className="text-xs text-muted-foreground">
                                {formData.usageLimit === 0 || formData.usageLimit === ''
                                    ? 'Khuyến mãi có thể được sử dụng không giới hạn'
                                    : `Khuyến mãi chỉ có thể được sử dụng ${formData.usageLimit} lần`}
                            </p>
                        </div>

                        {/* Description */}
                        <div className="space-y-2">
                            <Label htmlFor="description">Mô tả</Label>
                            <textarea
                                id="description"
                                value={formData.description}
                                onChange={(e) => setFormData({ ...formData, description: e.target.value })}
                                placeholder="Nhập mô tả chi tiết về khuyến mãi..."
                                className="flex min-h-[80px] w-full rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-50"
                                rows={3}
                                maxLength={1000}
                            />
                            <p className="text-xs text-muted-foreground text-right">
                                {formData.description?.length || 0} / 1000 ký tự
                            </p>
                        </div>

                        {/* Status */}
                        <div className="space-y-2">
                            <Label htmlFor="status">Trạng thái</Label>
                            <Select
                                value={formData.status}
                                onValueChange={(value) => setFormData({ ...formData, status: value })}
                            >
                                <SelectTrigger>
                                    <SelectValue placeholder="Chọn trạng thái" />
                                </SelectTrigger>
                                <SelectContent>
                                    <SelectItem value="active">Kích hoạt</SelectItem>
                                    <SelectItem value="inactive">Tạm dừng</SelectItem>
                                </SelectContent>
                            </Select>
                        </div>

                        {/* Preview Summary */}
                        {formData.name && formData.code && formData.promotionValue && (
                            <div className="p-4 bg-muted rounded-lg space-y-2">
                                <p className="text-sm font-medium">Xem trước:</p>
                                <div className="space-y-1 text-sm">
                                    <p><span className="font-medium">Tên:</span> {formData.name}</p>
                                    <p><span className="font-medium">Mã:</span> <code className="bg-background px-2 py-0.5 rounded">{formData.code}</code></p>
                                    <p>
                                        <span className="font-medium">Giảm:</span>{' '}
                                        <span className="text-green-600 font-semibold">
                      {formatPromotionValue(formData.promotionType, formData.promotionValue)}
                    </span>
                                    </p>
                                    {formData.startDate && formData.endDate && (
                                        <p>
                                            <span className="font-medium">Thời gian:</span>{' '}
                                            {formatDate(formData.startDate)} - {formatDate(formData.endDate)}
                                        </p>
                                    )}
                                    <p>
                                        <span className="font-medium">Giới hạn:</span>{' '}
                                        {formData.usageLimit === 0 ? 'Không giới hạn' : `${formData.usageLimit} lần`}
                                    </p>
                                </div>
                            </div>
                        )}
                    </div>

                    <DialogFooter>
                        <Button
                            variant="outline"
                            onClick={() => {
                                setIsDialogOpen(false);
                                setErrors({});
                            }}
                        >
                            Hủy
                        </Button>
                        <Button onClick={handleSave}>
                            {editingPromotion ? 'Cập nhật' : 'Tạo mới'}
                        </Button>
                    </DialogFooter>
                </DialogContent>
            </Dialog>
        </div>
    );
}