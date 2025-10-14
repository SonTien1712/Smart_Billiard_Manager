import React, { useState, useEffect } from 'react';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '../ui/card';
import { Button } from '../ui/button';
import { Input } from '../ui/input';
import { Label } from '../ui/label';
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '../ui/table';
import { Badge } from '../ui/badge';
import { Dialog, DialogContent, DialogDescription, DialogFooter, DialogHeader, DialogTitle, DialogTrigger } from '../ui/dialog';
import { Textarea } from '../ui/textarea';
import { PageType } from '../Dashboard';
import { Building, Plus, Edit, Trash2, MapPin, Phone, Mail } from 'lucide-react';
import { customerService } from '../../services/customerService';
import { useApi, usePaginatedApi } from '../../hooks/useApi';
import { handleSuccess, handleApiError } from '../../utils/errorHandler';


/**
 * @typedef {import('../../types').Club} Club
 */

export function ClubManagement({ onPageChange }) {
  const [isDialogOpen, setIsDialogOpen] = useState(false);
  const [editingClub, setEditingClub] = useState(null);
  const [formData, setFormData] = useState({
    name: '',
    address: '',
    phone: '',
    email: '',
    description: '',
    isActive: true
  });

  // Paginated API hook for fetching clubs
  const {
    data: clubs,
    loading: clubsLoading,
    fetchData: fetchClubs,
    refresh: refreshClubs
  } = usePaginatedApi(customerService.getClubs.bind(customerService));

  // API hooks for CRUD operations
  const { execute: createClub, loading: createLoading } = useApi(
    customerService.createClub.bind(customerService),
    {
      onSuccess: (newClub) => {
        handleSuccess('Club created successfully');
        setIsDialogOpen(false);
        refreshClubs();
      }
    }
  );

  const { execute: updateClub, loading: updateLoading } = useApi(
    customerService.updateClub.bind(customerService),
    {
      onSuccess: () => {
        handleSuccess('Club updated successfully');
        setIsDialogOpen(false);
        refreshClubs();
      }
    }
  );

  const { execute: deleteClub, loading: deleteLoading } = useApi(
    customerService.deleteClub.bind(customerService),
    {
      onSuccess: () => {
        handleSuccess('Club deleted successfully');
        refreshClubs();
      }
    }
  );

  useEffect(() => {
    fetchClubs({ page: 0, size: 10 });
  }, [fetchClubs]);

  const handleEdit = (club) => {
    setEditingClub(club);
    setFormData({
      name: club.name,
      address: club.address,
      phone: club.phone,
      email: club.email,
      description: club.description || '',
      isActive: club.isActive
    });
    setIsDialogOpen(true);
  };

  const handleAdd = () => {
    setEditingClub(null);
    setFormData({
      name: '',
      address: '',
      phone: '',
      email: '',
      description: '',
      isActive: true
    });
    setIsDialogOpen(true);
  };

  const handleSave = async () => {
    try {
      if (editingClub) {
        await updateClub(editingClub.id, formData);
      } else {
        await createClub(formData);
      }
    } catch (error) {
      // Error is already handled by the useApi hook
    }
  };

  const handleDelete = async (clubId) => {
    if (window.confirm('Are you sure you want to delete this club?')) {
      try {
        await deleteClub(clubId);
      } catch (error) {
        // Error is already handled by the useApi hook
      }
    }
  };

  const isLoading = createLoading || updateLoading || deleteLoading;

  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <div>
          <h1 className="text-3xl font-bold">Club Management</h1>
          <p className="text-muted-foreground">Manage your billiards clubs</p>
        </div>
        <Button onClick={handleAdd} className="gap-2">
          <Plus className="h-4 w-4" />
          Add New Club
        </Button>
      </div>

      <Card>
        <CardHeader>
          <CardTitle className="flex items-center gap-2">
            <Building className="h-5 w-5" />
            Your Clubs
          </CardTitle>
          <CardDescription>
            Manage and monitor your billiards club locations
          </CardDescription>
        </CardHeader>
        <CardContent>
          {clubsLoading ? (
            <div className="flex items-center justify-center py-8">
              <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-primary"></div>
            </div>
          ) : clubs.length === 0 ? (
            <div className="text-center py-8">
              <Building className="h-12 w-12 mx-auto text-muted-foreground mb-4" />
              <h3 className="text-lg font-medium">No clubs found</h3>
              <p className="text-muted-foreground mb-4">Create your first billiards club to get started.</p>
              <Button onClick={handleAdd} className="gap-2">
                <Plus className="h-4 w-4" />
                Add Your First Club
              </Button>
            </div>
          ) : (
            <Table>
              <TableHeader>
                <TableRow>
                  <TableHead>Club Name</TableHead>
                  <TableHead>Contact Info</TableHead>
                  <TableHead>Address</TableHead>
                  <TableHead>Status</TableHead>
                  <TableHead>Actions</TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                {clubs.map((club) => (
                  <TableRow key={club.id}>
                    <TableCell>
                      <div>
                        <div className="font-medium">{club.name}</div>
                        {club.description && (
                          <div className="text-sm text-muted-foreground">{club.description}</div>
                        )}
                      </div>
                    </TableCell>
                    <TableCell>
                      <div className="space-y-1">
                        <div className="flex items-center gap-1 text-sm">
                          <Phone className="h-3 w-3" />
                          {club.phone}
                        </div>
                        <div className="flex items-center gap-1 text-sm">
                          <Mail className="h-3 w-3" />
                          {club.email}
                        </div>
                      </div>
                    </TableCell>
                    <TableCell>
                      <div className="flex items-center gap-1">
                        <MapPin className="h-4 w-4 text-muted-foreground" />
                        <span className="text-sm">{club.address}</span>
                      </div>
                    </TableCell>
                    <TableCell>
                      <Badge variant={club.isActive ? "default" : "secondary"}>
                        {club.isActive ? 'Active' : 'Inactive'}
                      </Badge>
                    </TableCell>
                    <TableCell>
                      <div className="flex space-x-2">
                        <Button
                          variant="outline"
                          size="sm"
                          onClick={() => handleEdit(club)}
                          className="gap-1"
                        >
                          <Edit className="h-3 w-3" />
                          Edit
                        </Button>
                        <Button
                          variant="outline"
                          size="sm"
                          onClick={() => handleDelete(club.id)}
                          className="gap-1 text-destructive hover:text-destructive"
                          disabled={isLoading}
                        >
                          <Trash2 className="h-3 w-3" />
                          Delete
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
        <DialogContent className="max-w-md">
          <DialogHeader>
            <DialogTitle>
              {editingClub ? 'Edit Club' : 'Add New Club'}
            </DialogTitle>
            <DialogDescription>
              {editingClub ? 'Update club information' : 'Create a new billiards club'}
            </DialogDescription>
          </DialogHeader>
          
          <div className="space-y-4">
            <div className="space-y-2">
              <Label htmlFor="name">Club Name</Label>
              <Input
                id="name"
                value={formData.name}
                onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                placeholder="Enter club name"
                required
              />
            </div>
            
            <div className="space-y-2">
              <Label htmlFor="email">Email</Label>
              <Input
                id="email"
                type="email"
                value={formData.email}
                onChange={(e) => setFormData({ ...formData, email: e.target.value })}
                placeholder="Enter club email"
                required
              />
            </div>
            
            <div className="space-y-2">
              <Label htmlFor="phone">Phone</Label>
              <Input
                id="phone"
                value={formData.phone}
                onChange={(e) => setFormData({ ...formData, phone: e.target.value })}
                placeholder="Enter phone number"
                required
              />
            </div>
            
            <div className="space-y-2">
              <Label htmlFor="address">Address</Label>
              <Textarea
                id="address"
                value={formData.address}
                onChange={(e) => setFormData({ ...formData, address: e.target.value })}
                placeholder="Enter club address"
                required
              />
            </div>
            
            <div className="space-y-2">
              <Label htmlFor="description">Description (Optional)</Label>
              <Textarea
                id="description"
                value={formData.description}
                onChange={(e) => setFormData({ ...formData, description: e.target.value })}
                placeholder="Enter club description"
              />
            </div>
          </div>
          
          <DialogFooter>
            <Button
              variant="outline"
              onClick={() => setIsDialogOpen(false)}
              disabled={isLoading}
            >
              Cancel
            </Button>
            <Button
              onClick={handleSave}
              disabled={isLoading || !formData.name || !formData.email || !formData.phone || !formData.address}
            >
              {isLoading ? 'Saving...' : editingClub ? 'Update Club' : 'Create Club'}
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </div>
  );
}