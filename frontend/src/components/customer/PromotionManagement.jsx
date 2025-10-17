import React, { useState } from 'react';
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


export function PromotionManagement({ onPageChange }) {
  const [promotions, setPromotions] = useState([
    { id: '1', code: 'HAPPY20', name: 'Happy Hour 20%', discountType: 'percentage', discountValue: 20, startDate: '2024-01-01', endDate: '2024-03-31', status: 'active', usageCount: 45 },
    { id: '2', code: 'STUDENT15', name: 'Student Discount', discountType: 'percentage', discountValue: 15, startDate: '2024-01-01', endDate: '2024-12-31', status: 'active', usageCount: 23 },
    { id: '3', code: 'WEEKEND10', name: 'Weekend Special', discountType: 'fixed', discountValue: 10, startDate: '2024-01-01', endDate: '2024-06-30', status: 'inactive', usageCount: 12 },
  ]);

  const [isDialogOpen, setIsDialogOpen] = useState(false);
    const [editingPromotion, setEditingPromotion] = useState(null);
  const [formData, setFormData] = useState({
    code: '',
    name: '',
    discountType: 'percentage',
    discountValue: 10,
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
    setFormData({ code: '', name: '', discountType: 'percentage', discountValue: 10, startDate: '', endDate: '', status: 'active' });
    setIsDialogOpen(true);
  };

  const handleSave = () => {
    if (editingPromotion) {
      setPromotions(promotions.map(promo => promo.id === editingPromotion.id ? { ...formData, id: editingPromotion.id, usageCount: promo.usageCount } : promo));
    } else {
      setPromotions([...promotions, { ...formData, id: Date.now().toString(), usageCount: 0 }]);
    }
    setIsDialogOpen(false);
  };

  const handleDelete = (id) => {
    setPromotions(promotions.filter(promo => promo.id !== id));
  };

  const getStatusColor = (status) => {
    return status === 'active' ? 'default' : 'secondary';
  };

  const formatDiscount = (type, value) => {
    return type === 'percentage' ? `${value}%` : `$${value}`;
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
                      {promotion.discountType === 'percentage' ? (
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
              {editingPromotion ? 'Update promotion details' : 'Create a new discount promotion'}
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
                <Label htmlFor="discountType">Discount Type</Label>
                <Select value={formData.discountType} onValueChange={(value) => setFormData({ ...formData, discountType: value })}>
                  <SelectTrigger>
                    <SelectValue placeholder="Select discount type" />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="percentage">Percentage (%)</SelectItem>
                    <SelectItem value="fixed">Fixed Amount ($)</SelectItem>
                  </SelectContent>
                </Select>
              </div>
              <div className="space-y-2">
                <Label htmlFor="discountValue">
                  Discount Value {formData.discountType === 'percentage' ? '(%)' : '($)'}
                </Label>
                <Input
                  id="discountValue"
                  type="number"
                  value={formData.discountValue}
                  onChange={(e) => setFormData({ ...formData, discountValue: parseInt(e.target.value) })}
                  placeholder="Enter discount value"
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