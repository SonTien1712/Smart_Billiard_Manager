import React, { useEffect, useMemo, useState } from 'react';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '../ui/card';
import { Button } from '../ui/button';
import { Input } from '../ui/input';
import { Label } from '../ui/label';
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '../ui/table';
import { Badge } from '../ui/badge';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '../ui/select';
import { Dialog, DialogContent, DialogDescription, DialogFooter, DialogHeader, DialogTitle } from '../ui/dialog';
import { PageType } from '../Dashboard';
import { Percent, Plus, Edit, Trash2, Calendar, DollarSign } from 'lucide-react';
import { customerService } from '../../services/customerService';
import { useAuth } from '../AuthProvider';


export function PromotionManagement({ onPageChange }) {
  const { user } = useAuth();
  const directClubId = useMemo(() => user?.clubId || user?.club?.id || user?.customerClubId, [user]);
  const [effectiveClubId, setEffectiveClubId] = useState(null);

  const [promotions, setPromotions] = useState([]);
  const [clubs, setClubs] = useState([]);

  const [isDialogOpen, setIsDialogOpen] = useState(false);
    const [editingPromotion, setEditingPromotion] = useState(null);
  const [formData, setFormData] = useState({
    code: '',
    name: '',
    promotionType: 'percentage',
    promotionValue: 10,
    startDate: '',
    endDate: '',
    status: 'active'
  });

  const handleEdit = (promotion) => {
    setEditingPromotion(promotion);
    setFormData(promotion);
    setIsDialogOpen(true);
  };

  const handleAdd = () => {
    setEditingPromotion(null);
    setFormData({ code: '', name: '', promotionType: 'percentage', promotionValue: 10, startDate: '', endDate: '', status: 'active' });
    setIsDialogOpen(true);
  };

  // Resolve effective clubId: prefer directClubId, else fetch by customer id and take first
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
          name: c.clubName ?? c.name ?? `Club #${c.id ?? c.clubId}`,
        })).filter(x => x.id);
        if (normalized.length > 0) {
          setClubs(normalized);
          setEffectiveClubId(String(normalized[0].id));
          return;
        }
        if (directClubId) {
          setClubs([{ id: directClubId, name: `Club #${directClubId}` }]);
          setEffectiveClubId(String(directClubId));
        }
      } catch (_) {
        if (directClubId) {
          setClubs([{ id: directClubId, name: `Club #${directClubId}` }]);
          setEffectiveClubId(String(directClubId));
        }
      }
    };
    resolveClub();
  }, [directClubId, user]);

  useEffect(() => {
    const load = async () => {
      try {
        if (!effectiveClubId) return;
        const list = await customerService.getPromotions(Number(effectiveClubId), { page: 0, size: 50, sortBy: 'id', sortDir: 'desc' });
        const mapped = (Array.isArray(list) ? list : []).map(p => ({
          id: p.promotionId ?? p.id,
          code: p.promotionCode,
          name: p.promotionName,
          promotionType: (() => { const t = ((p.promotionType||'')+ '').toLowerCase(); return t.includes('fixed') ? 'fixed' : 'percentage'; })(),
          promotionValue: Number(p.promotionValue || 0),
          startDate: p.startDate ? new Date(p.startDate).toISOString().slice(0,10) : '',
          endDate: p.endDate ? new Date(p.endDate).toISOString().slice(0,10) : '',
          status: p.isActive ? 'active' : 'inactive',
          usageCount: Number(p.usedCount || 0)
        }));
        setPromotions(mapped);
      } catch (e) {
        console.error('Load promotions failed', e);
        setPromotions([]);
      }
    };
    load();
  }, [effectiveClubId]);

  const handleSave = async () => {
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
        usageLimit: 0,
        description: ''
      };

      if (editingPromotion && editingPromotion.id) {
        const res = await customerService.updatePromotion(editingPromotion.id, payload);
        const p = res || {};
        const updated = {
          id: p.promotionId ?? editingPromotion.id,
          code: p.promotionCode ?? formData.code,
          name: p.promotionName ?? formData.name,
          promotionType: (() => { const t = ((p.promotionType||payload.promotionType)||'')+''; return t.toLowerCase().includes('fixed') ? 'fixed' : 'percentage'; })(),
          promotionValue: Number(((p.promotionValue ?? formData.promotionValue) ?? 0)),
          startDate: (p.startDate ? new Date(p.startDate) : new Date(formData.startDate)).toISOString().slice(0,10),
          endDate: (p.endDate ? new Date(p.endDate) : new Date(formData.endDate)).toISOString().slice(0,10),
          status: (p.isActive ?? true) ? 'active' : 'inactive',
          usageCount: Number(p.usedCount ?? 0)
        };
        setPromotions(promotions.map(x => x.id === editingPromotion.id ? updated : x));
      } else {
        const p = await customerService.createPromotion(payload);
        const created = {
          id: p.promotionId ?? p.id ?? Date.now().toString(),
          code: p.promotionCode ?? payload.promotionCode,
          name: p.promotionName ?? payload.promotionName,
          promotionType: (() => { const t = ((p.promotionType||payload.promotionType)||'')+''; return t.toLowerCase().includes('fixed') ? 'fixed' : 'percentage'; })(),
          promotionValue: Number(((p.promotionValue ?? payload.promotionValue) ?? 0)),
          startDate: (p.startDate ? new Date(p.startDate) : new Date(formData.startDate)).toISOString().slice(0,10),
          endDate: (p.endDate ? new Date(p.endDate) : new Date(formData.endDate)).toISOString().slice(0,10),
          status: (p.isActive ?? true) ? 'active' : 'inactive',
          usageCount: Number(p.usedCount ?? 0)
        };
        setPromotions([...promotions, created]);
      }
      setIsDialogOpen(false);
    } catch (e) {
      console.error('Save promotion failed', e);
      alert('Lưu khuyến mãi thất bại');
    }
  };

  const handleDelete = async (id) => {
    try {
      await customerService.deletePromotion(id);
      setPromotions(promotions.filter(promo => promo.id !== id));
    } catch (e) {
      console.error('Delete promotion failed', e);
      alert('Xóa khuyến mãi thất bại');
    }
  };

  const getStatusColor = (status) => {
    return status === 'active' ? 'default' : 'secondary';
  };

  const formatPromotion = (type, value) => {
    return type === 'percentage' ? `${value}%` : `$${value}`;
  };

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-semibold">Promotion Management</h1>
          <p className="text-muted-foreground">Manage promotion codes and offers</p>
        </div>
        <div className="flex items-center gap-3">
          <div className="min-w-[220px]">
            <Label htmlFor="club-select">Club</Label>
            <Select value={effectiveClubId ?? ''} onValueChange={(v) => setEffectiveClubId(v)}>
              <SelectTrigger id="club-select">
                <SelectValue placeholder="Select a club" />
              </SelectTrigger>
              <SelectContent>
                {clubs.map(c => (
                  <SelectItem key={c.id} value={String(c.id)}>{c.name}</SelectItem>
                ))}
              </SelectContent>
            </Select>
          </div>
          <Button onClick={handleAdd}>
            <Plus className="h-4 w-4 mr-2" />
            Add Promotion
          </Button>
        </div>
      </div>

      <Card>
        <CardHeader>
          <CardTitle>Active Promotions</CardTitle>
          <CardDescription>Manage promotion codes and promotional offers</CardDescription>
        </CardHeader>
        <CardContent>
          <Table>
            <TableHeader>
              <TableRow>
                <TableHead>Promotion</TableHead>
                <TableHead>Code</TableHead>
                <TableHead>Promotion</TableHead>
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
                      <p className="font-medium">{promotion.name}</p>
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
                      {promotion.promotionType === 'percentage' ? (
                        <Percent className="h-4 w-4 text-green-600" />
                      ) : (
                        <DollarSign className="h-4 w-4 text-green-600" />
                      )}
                      <span className="font-medium text-green-600">
                        {formatPromotion(promotion.promotionType, promotion.promotionValue)}
                      </span>
                    </div>
                  </TableCell>
                  <TableCell>
                    <div className="text-sm space-y-1">
                      <div className="flex items-center space-x-1">
                        <Calendar className="h-3 w-3 text-muted-foreground" />
                        <span>{promotion.startDate}</span>
                      </div>
                      <div className="text-muted-foreground">
                        to {promotion.endDate}
                      </div>
                    </div>
                  </TableCell>
                  <TableCell>
                    <Badge variant="outline">{promotion.usageCount} uses</Badge>
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
        </CardContent>
      </Card>

      <Dialog open={isDialogOpen} onOpenChange={setIsDialogOpen}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>{editingPromotion ? 'Edit Promotion' : 'Create New Promotion'}</DialogTitle>
            <DialogDescription>
              {editingPromotion ? 'Update promotion details' : 'Create a new promotion'}
            </DialogDescription>
          </DialogHeader>
          <div className="space-y-4">
            <div className="grid gap-4 md:grid-cols-2">
              <div className="space-y-2">
                <Label htmlFor="name">Promotion Name</Label>
                <Input
                  id="name"
                  value={formData.name}
                  onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                  placeholder="Enter promotion name"
                />
              </div>
              <div className="space-y-2">
                <Label htmlFor="code">Promo Code</Label>
                <Input
                  id="code"
                  value={formData.code}
                  onChange={(e) => setFormData({ ...formData, code: e.target.value.toUpperCase() })}
                  placeholder="Enter promo code"
                />
              </div>
            </div>
            
            <div className="grid gap-4 md:grid-cols-2">
              <div className="space-y-2">
                <Label htmlFor="promotionType">Promotion Type</Label>
                <Select value={formData.promotionType} onValueChange={(value) => setFormData({ ...formData, promotionType: value })}>
                  <SelectTrigger>
                    <SelectValue placeholder="Select Promotion Type" />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="percentage">Percentage (%)</SelectItem>
                    <SelectItem value="fixed">Fixed Amount ($)</SelectItem>
                  </SelectContent>
                </Select>
              </div>
              <div className="space-y-2">
                <Label htmlFor="promotionValue">
                  Promotion Value {formData.promotionType === 'percentage' ? '(%)' : '($)'}
                </Label>
                <Input
                  id="promotionValue"
                  type="number"
                  value={formData.promotionValue === '' ? '' : String(formData.promotionValue)}
                  onChange={(e) => {
                    const v = e.target.value;
                    setFormData({ ...formData, promotionValue: v === '' ? '' : Number(v) });
                  }}
                  placeholder="Enter Promotion Value"
                />
              </div>
            </div>

            <div className="grid gap-4 md:grid-cols-2">
              <div className="space-y-2">
                <Label htmlFor="startDate">Start Date</Label>
                <Input
                  id="startDate"
                  type="date"
                  value={formData.startDate}
                  onChange={(e) => setFormData({ ...formData, startDate: e.target.value })}
                />
              </div>
              <div className="space-y-2">
                <Label htmlFor="endDate">End Date</Label>
                <Input
                  id="endDate"
                  type="date"
                  value={formData.endDate}
                  onChange={(e) => setFormData({ ...formData, endDate: e.target.value })}
                />
              </div>
            </div>

            <div className="space-y-2">
              <Label htmlFor="status">Status</Label>
              <Select value={formData.status} onValueChange={(value) => setFormData({ ...formData, status: value })}>
                <SelectTrigger>
                  <SelectValue placeholder="Select status" />
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
              {editingPromotion ? 'Update' : 'Create'} Promotion
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>

      {/* Summary Stats */}
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
              {promotions.filter(p => p.status === 'active').length}
            </div>
          </CardContent>
        </Card>
        
        <Card>
          <CardHeader className="pb-2">
            <CardTitle className="text-base">Total Usage</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">
              {promotions.reduce((sum, p) => sum + p.usageCount, 0)}
            </div>
          </CardContent>
        </Card>
      </div>
    </div>
  );
}






