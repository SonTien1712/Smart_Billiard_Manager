import React, { useState, useEffect, useMemo } from 'react';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '../ui/card';
import { Button } from '../ui/button';
import { Input } from '../ui/input';
import { Label } from '../ui/label';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '../ui/select';
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '../ui/table';
import { Badge } from '../ui/badge';
import { Switch } from '../ui/switch';
import { Dialog, DialogContent, DialogDescription, DialogFooter, DialogHeader, DialogTitle } from '../ui/dialog';
import { Alert, AlertDescription } from '../ui/alert';
import {
    Package, Plus, Edit, Trash2, DollarSign, Archive,
    Search, RefreshCw, AlertCircle, Loader2
} from 'lucide-react';
import { customerService } from '../../services/customerService';
import { useApi } from '../../hooks/useApi';
import { useAuth } from '../AuthProvider';
import { toast } from 'sonner';

// Form validation helper
const validateProductForm = (formData) => {
    const errors = {};

    if (!formData.name?.trim()) {
        errors.name = 'Product name is required';
    } else if (formData.name.length > 255) {
        errors.name = 'Product name must not exceed 255 characters';
    }

    if (!formData.price || formData.price <= 0) {
        errors.price = 'Price must be greater than 0';
    }

    if (formData.costPrice && formData.costPrice < 0) {
        errors.costPrice = 'Cost price cannot be negative';
    }

    if (formData.category && formData.category.length > 100) {
        errors.category = 'Category must not exceed 100 characters';
    }

    if (formData.productUrl && formData.productUrl.length > 500) {
        errors.productUrl = 'Product URL must not exceed 500 characters';
    }

    return errors;
};

export function ProductManagement({ onPageChange }) {
    const { user } = useAuth();

    // Resolve club info from user
    const directClubId = useMemo(() =>
            user?.clubId || user?.club?.id || user?.customerClubId,
        [user]
    );

    // State
    const [products, setProducts] = useState([]);
    const [clubs, setClubs] = useState([]);
    const [effectiveClubId, setEffectiveClubId] = useState(null);
    const [isDialogOpen, setIsDialogOpen] = useState(false);
    const [editingProduct, setEditingProduct] = useState(null);
    const [formData, setFormData] = useState({
        name: '',
        category: 'Beverages',
        price: 0,
        costPrice: 0,
        description: '',
        productUrl: '',
        active: true
    });
    const [formErrors, setFormErrors] = useState({});
    const [searchKeyword, setSearchKeyword] = useState('');
    const [showActiveOnly, setShowActiveOnly] = useState(false);

    const categories = ['Beverages', 'Snacks', 'Alcohol', 'Food', 'Other'];

    // API Hooks
    const fetchProductsApi = useApi(
        (cId, params) => customerService.getProducts(cId, params),
        {
            onSuccess: (data) => {
                console.log('[ProductManagement] Products loaded:', data);
                setProducts(data || []);
            },
            onError: (err) => {
                console.error('[ProductManagement] Failed to load products:', err);
                setProducts([]);
            },
            showErrorToast: true,
            errorMessage: 'Failed to load products'
        }
    );

    const createProductApi = useApi(
        (productData) => customerService.createProduct(productData),
        {
            onSuccess: (newProduct) => {
                console.log('[ProductManagement] Product created:', newProduct);
                setProducts(prev => [...prev, newProduct]);
                toast.success('Product created successfully');
                setIsDialogOpen(false);
                resetForm();
            },
            showErrorToast: true,
            errorMessage: 'Failed to create product'
        }
    );

    const updateProductApi = useApi(
        (id, productData) => customerService.updateProduct(id, productData),
        {
            onSuccess: (updatedProduct) => {
                console.log('[ProductManagement] Product updated:', updatedProduct);
                setProducts(prev =>
                    prev.map(p => p.id === updatedProduct.id ? updatedProduct : p)
                );
                toast.success('Product updated successfully');
                setIsDialogOpen(false);
                resetForm();
            },
            showErrorToast: true,
            errorMessage: 'Failed to update product'
        }
    );

    const deleteProductApi = useApi(
        (id) => customerService.deleteProduct(id),
        {
            onSuccess: (_, id) => {
                console.log('[ProductManagement] Product deleted:', id);
                setProducts(prev => prev.filter(p => p.id !== id));
                toast.success('Product deleted successfully');
            },
            showErrorToast: true,
            errorMessage: 'Failed to delete product'
        }
    );

    const toggleStatusApi = useApi(
        (id) => customerService.toggleProductStatus(id),
        {
            onSuccess: (updatedProduct) => {
                console.log('[ProductManagement] Status toggled:', updatedProduct);
                setProducts(prev =>
                    prev.map(p => p.id === updatedProduct.id ? updatedProduct : p)
                );
                toast.success(`Product ${updatedProduct.active ? 'activated' : 'deactivated'}`);
            },
            showErrorToast: true,
            errorMessage: 'Failed to toggle product status'
        }
    );

    const searchProductsApi = useApi(
        (cId, keyword) => customerService.searchProducts(cId, keyword),
        {
            onSuccess: (data) => {
                console.log('[ProductManagement] Search results:', data);
                setProducts(data || []);
            },
            showErrorToast: true,
            errorMessage: 'Search failed'
        }
    );

    // Resolve effective clubId: prefer directClubId, else fetch by customer id
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
                    // Use directClubId if available, otherwise use first club
                    const initialClubId = directClubId || normalized[0].id;
                    setEffectiveClubId(String(initialClubId));
                    return;
                }

                if (directClubId) {
                    setClubs([{ id: directClubId, name: `Club #${directClubId}` }]);
                    setEffectiveClubId(String(directClubId));
                }
            } catch (err) {
                console.error('[ProductManagement] Failed to resolve clubs:', err);
                if (directClubId) {
                    setClubs([{ id: directClubId, name: `Club #${directClubId}` }]);
                    setEffectiveClubId(String(directClubId));
                }
            }
        };
        resolveClub();
    }, [directClubId, user]);

    // Fetch products when effectiveClubId or filters change
    useEffect(() => {
        if (effectiveClubId) {
            console.log('[ProductManagement] Fetching products for club:', effectiveClubId);
            fetchProductsApi.execute(Number(effectiveClubId), { activeOnly: showActiveOnly });
        }
    }, [effectiveClubId, showActiveOnly]);

    // Form helpers
    const resetForm = () => {
        setFormData({
            name: '',
            category: 'Beverages',
            price: 0,
            costPrice: 0,
            description: '',
            productUrl: '',
            active: true
        });
        setFormErrors({});
        setEditingProduct(null);
    };

    // Handlers
    const handleAdd = () => {
        resetForm();
        setIsDialogOpen(true);
    };

    const handleEdit = (product) => {
        setEditingProduct(product);
        setFormData({
            name: product.name || '',
            category: product.category || 'Beverages',
            price: product.price || 0,
            costPrice: product.costPrice || 0,
            description: product.description || '',
            productUrl: product.productUrl || '',
            active: product.active !== undefined ? product.active : true
        });
        setFormErrors({});
        setIsDialogOpen(true);
    };

    const handleSave = async () => {
        // Validate
        const errors = validateProductForm(formData);
        if (Object.keys(errors).length > 0) {
            setFormErrors(errors);
            toast.error('Please fix form errors');
            return;
        }

        if (!effectiveClubId) {
            toast.error('Please select a club');
            return;
        }

        // Prepare data
        const productData = {
            clubId: Number(effectiveClubId),
            customerId: user?.customerId || user?.id,
            name: formData.name.trim(),
            category: formData.category,
            price: parseFloat(formData.price),
            costPrice: formData.costPrice ? parseFloat(formData.costPrice) : 0,
            description: formData.description?.trim() || null,
            productUrl: formData.productUrl?.trim() || null,
            active: formData.active
        };

        try {
            if (editingProduct) {
                await updateProductApi.execute(editingProduct.id, productData);
            } else {
                await createProductApi.execute(productData);
            }
        } catch (error) {
            console.error('[ProductManagement] Save failed:', error);
        }
    };

    const handleDelete = async (id, productName) => {
        if (!window.confirm(`Are you sure you want to delete "${productName}"?`)) {
            return;
        }

        try {
            await deleteProductApi.execute(id);
        } catch (error) {
            console.error('[ProductManagement] Delete failed:', error);
        }
    };

    const handleToggleActive = async (id) => {
        try {
            await toggleStatusApi.execute(id);
        } catch (error) {
            console.error('[ProductManagement] Toggle failed:', error);
        }
    };

    const handleSearch = () => {
        if (!effectiveClubId) return;

        if (searchKeyword.trim()) {
            searchProductsApi.execute(Number(effectiveClubId), searchKeyword.trim());
        } else {
            fetchProductsApi.execute(Number(effectiveClubId), { activeOnly: showActiveOnly });
        }
    };

    const handleRefresh = () => {
        setSearchKeyword('');
        if (effectiveClubId) {
            fetchProductsApi.execute(Number(effectiveClubId), { activeOnly: showActiveOnly });
        }
    };

    // Calculate stats
    const stats = useMemo(() => ({
        total: products.length,
        active: products.filter(p => p.active).length,
        inactive: products.filter(p => !p.active).length,
        totalValue: products.reduce((sum, p) => sum + (p.price || 0), 0)
    }), [products]);

    // Loading state
    const isLoading = fetchProductsApi.loading ||
        createProductApi.loading ||
        updateProductApi.loading ||
        deleteProductApi.loading ||
        toggleStatusApi.loading ||
        searchProductsApi.loading;

    const isSaving = createProductApi.loading || updateProductApi.loading;

    return (
        <div className="space-y-6">
            {/* Header */}
            <div className="flex items-center justify-between">
                <div>
                    <h1 className="text-3xl font-semibold">Product Management</h1>
                    <p className="text-muted-foreground">Manage your inventory and pricing</p>
                </div>
                <div className="flex items-center gap-3">
                    {/* Club Selector */}
                    <div className="min-w-[220px]">
                        <Label htmlFor="club-select" className="text-sm mb-1 block">Club</Label>
                        <Select
                            value={effectiveClubId ?? ''}
                            onValueChange={(v) => setEffectiveClubId(v)}
                            disabled={isLoading}
                        >
                            <SelectTrigger id="club-select">
                                <SelectValue placeholder="Select a club" />
                            </SelectTrigger>
                            <SelectContent>
                                {clubs.map(c => (
                                    <SelectItem key={c.id} value={String(c.id)}>
                                        {c.name}
                                    </SelectItem>
                                ))}
                            </SelectContent>
                        </Select>
                    </div>
                    <div className="flex items-center gap-2 mt-6">
                        <Button
                            variant="outline"
                            size="icon"
                            onClick={handleRefresh}
                            disabled={isLoading}
                        >
                            <RefreshCw className={`h-4 w-4 ${isLoading ? 'animate-spin' : ''}`} />
                        </Button>
                        <Button onClick={handleAdd} disabled={isLoading || !effectiveClubId}>
                            <Plus className="h-4 w-4 mr-2" />
                            Add Product
                        </Button>
                    </div>
                </div>
            </div>

            {/* Error Alert */}
            {fetchProductsApi.error && (
                <Alert variant="destructive">
                    <AlertCircle className="h-4 w-4" />
                    <AlertDescription>
                        {fetchProductsApi.error.message || 'Failed to load products'}
                    </AlertDescription>
                </Alert>
            )}

            {/* Search & Filter */}
            <Card>
                <CardContent className="pt-6">
                    <div className="flex items-center gap-4">
                        <div className="flex-1 flex items-center gap-2">
                            <Search className="h-4 w-4 text-muted-foreground" />
                            <Input
                                placeholder="Search products by name or category..."
                                value={searchKeyword}
                                onChange={(e) => setSearchKeyword(e.target.value)}
                                onKeyDown={(e) => e.key === 'Enter' && handleSearch()}
                                disabled={isLoading}
                            />
                            <Button onClick={handleSearch} disabled={isLoading || !effectiveClubId}>
                                Search
                            </Button>
                        </div>
                        <div className="flex items-center gap-2">
                            <Label htmlFor="active-only" className="text-sm whitespace-nowrap">
                                Active only
                            </Label>
                            <Switch
                                id="active-only"
                                checked={showActiveOnly}
                                onCheckedChange={setShowActiveOnly}
                                disabled={isLoading}
                            />
                        </div>
                    </div>
                </CardContent>
            </Card>

            {/* Product Table */}
            <Card>
                <CardHeader>
                    <CardTitle>Product Inventory</CardTitle>
                    <CardDescription>
                        {products.length} product{products.length !== 1 ? 's' : ''} found
                    </CardDescription>
                </CardHeader>
                <CardContent>
                    <div className="rounded-md border">
                        <Table>
                            <TableHeader>
                                <TableRow>
                                    <TableHead>Product</TableHead>
                                    <TableHead>Category</TableHead>
                                    <TableHead>Price</TableHead>
                                    <TableHead>Cost</TableHead>
                                    <TableHead>Margin</TableHead>
                                    <TableHead>Status</TableHead>
                                    <TableHead>Actions</TableHead>
                                </TableRow>
                            </TableHeader>
                            <TableBody>
                                {fetchProductsApi.loading && products.length === 0 ? (
                                    <TableRow>
                                        <TableCell colSpan={7} className="text-center py-8">
                                            <Loader2 className="h-6 w-6 animate-spin mx-auto mb-2" />
                                            <p className="text-muted-foreground">Loading products...</p>
                                        </TableCell>
                                    </TableRow>
                                ) : products.length === 0 ? (
                                    <TableRow>
                                        <TableCell colSpan={7} className="text-center text-muted-foreground py-8">
                                            {effectiveClubId
                                                ? 'No products found. Click "Add Product" to create one.'
                                                : 'Please select a club to view products.'
                                            }
                                        </TableCell>
                                    </TableRow>
                                ) : (
                                    products.map((product) => (
                                        <TableRow key={product.id}>
                                            <TableCell>
                                                <div className="flex items-center space-x-2">
                                                    <Package className="h-4 w-4 text-primary" />
                                                    <div>
                                                        <p className="font-medium">{product.name}</p>
                                                        {product.description && (
                                                            <p className="text-xs text-muted-foreground truncate max-w-[200px]">
                                                                {product.description}
                                                            </p>
                                                        )}
                                                    </div>
                                                </div>
                                            </TableCell>
                                            <TableCell>
                                                <Badge variant="outline">{product.category || 'N/A'}</Badge>
                                            </TableCell>
                                            <TableCell>
                                                <div className="flex items-center space-x-1">
                                                    <DollarSign className="h-4 w-4 text-muted-foreground" />
                                                    <span>${product.price?.toFixed(2) || '0.00'}</span>
                                                </div>
                                            </TableCell>
                                            <TableCell>
                                                <span className="text-muted-foreground">
                                                    ${product.costPrice?.toFixed(2) || '0.00'}
                                                </span>
                                            </TableCell>
                                            <TableCell>
                                                {product.profitMargin != null ? (
                                                    <Badge
                                                        variant={product.profitMargin > 50 ? 'default' : 'secondary'}
                                                    >
                                                        {product.profitMargin.toFixed(1)}%
                                                    </Badge>
                                                ) : (
                                                    <span className="text-muted-foreground">N/A</span>
                                                )}
                                            </TableCell>
                                            <TableCell>
                                                <Switch
                                                    checked={product.active}
                                                    onCheckedChange={() => handleToggleActive(product.id)}
                                                    disabled={isLoading}
                                                />
                                            </TableCell>
                                            <TableCell>
                                                <div className="flex items-center space-x-2">
                                                    <Button
                                                        variant="ghost"
                                                        size="sm"
                                                        onClick={() => handleEdit(product)}
                                                        disabled={isLoading}
                                                    >
                                                        <Edit className="h-4 w-4" />
                                                    </Button>
                                                    <Button
                                                        variant="ghost"
                                                        size="sm"
                                                        onClick={() => handleDelete(product.id, product.name)}
                                                        disabled={isLoading}
                                                    >
                                                        <Trash2 className="h-4 w-4 text-destructive" />
                                                    </Button>
                                                </div>
                                            </TableCell>
                                        </TableRow>
                                    ))
                                )}
                            </TableBody>
                        </Table>
                    </div>
                </CardContent>
            </Card>

            {/* Product Dialog */}
            <Dialog open={isDialogOpen} onOpenChange={setIsDialogOpen}>
                <DialogContent className="max-w-2xl max-h-[90vh] overflow-y-auto">
                    <DialogHeader>
                        <DialogTitle>
                            {editingProduct ? 'Edit Product' : 'Add New Product'}
                        </DialogTitle>
                        <DialogDescription>
                            {editingProduct
                                ? 'Update product information'
                                : 'Create a new product for sale'}
                        </DialogDescription>
                    </DialogHeader>

                    <div className="space-y-4 py-4">
                        {/* Product Name */}
                        <div className="space-y-2">
                            <Label htmlFor="name">
                                Product Name <span className="text-destructive">*</span>
                            </Label>
                            <Input
                                id="name"
                                value={formData.name}
                                onChange={(e) => {
                                    setFormData({ ...formData, name: e.target.value });
                                    setFormErrors({ ...formErrors, name: undefined });
                                }}
                                placeholder="Enter product name"
                                className={formErrors.name ? 'border-destructive' : ''}
                                disabled={isSaving}
                            />
                            {formErrors.name && (
                                <p className="text-sm text-destructive">{formErrors.name}</p>
                            )}
                        </div>

                        {/* Category & Price */}
                        <div className="grid gap-4 md:grid-cols-2">
                            <div className="space-y-2">
                                <Label htmlFor="category">Category</Label>
                                <select
                                    id="category"
                                    value={formData.category}
                                    onChange={(e) => setFormData({ ...formData, category: e.target.value })}
                                    className="w-full px-3 py-2 border border-input rounded-md bg-background"
                                    disabled={isSaving}
                                >
                                    {categories.map(cat => (
                                        <option key={cat} value={cat}>{cat}</option>
                                    ))}
                                </select>
                            </div>

                            <div className="space-y-2">
                                <Label htmlFor="price">
                                    Selling Price ($) <span className="text-destructive">*</span>
                                </Label>
                                <Input
                                    id="price"
                                    type="number"
                                    step="0.01"
                                    min="0"
                                    value={formData.price}
                                    onChange={(e) => {
                                        setFormData({ ...formData, price: parseFloat(e.target.value) || 0 });
                                        setFormErrors({ ...formErrors, price: undefined });
                                    }}
                                    placeholder="0.00"
                                    className={formErrors.price ? 'border-destructive' : ''}
                                    disabled={isSaving}
                                />
                                {formErrors.price && (
                                    <p className="text-sm text-destructive">{formErrors.price}</p>
                                )}
                            </div>
                        </div>

                        {/* Cost Price & URL */}
                        <div className="grid gap-4 md:grid-cols-2">
                            <div className="space-y-2">
                                <Label htmlFor="costPrice">Cost Price ($)</Label>
                                <Input
                                    id="costPrice"
                                    type="number"
                                    step="0.01"
                                    min="0"
                                    value={formData.costPrice}
                                    onChange={(e) => {
                                        setFormData({ ...formData, costPrice: parseFloat(e.target.value) || 0 });
                                        setFormErrors({ ...formErrors, costPrice: undefined });
                                    }}
                                    placeholder="0.00"
                                    className={formErrors.costPrice ? 'border-destructive' : ''}
                                    disabled={isSaving}
                                />
                                {formErrors.costPrice && (
                                    <p className="text-sm text-destructive">{formErrors.costPrice}</p>
                                )}
                            </div>

                            <div className="space-y-2">
                                <Label htmlFor="productUrl">Image URL</Label>
                                <Input
                                    id="productUrl"
                                    value={formData.productUrl}
                                    onChange={(e) => {
                                        setFormData({ ...formData, productUrl: e.target.value });
                                        setFormErrors({ ...formErrors, productUrl: undefined });
                                    }}
                                    placeholder="https://example.com/image.jpg"
                                    className={formErrors.productUrl ? 'border-destructive' : ''}
                                    disabled={isSaving}
                                />
                                {formErrors.productUrl && (
                                    <p className="text-sm text-destructive">{formErrors.productUrl}</p>
                                )}
                            </div>
                        </div>

                        {/* Description */}
                        <div className="space-y-2">
                            <Label htmlFor="description">Description</Label>
                            <textarea
                                id="description"
                                value={formData.description}
                                onChange={(e) => setFormData({ ...formData, description: e.target.value })}
                                placeholder="Enter product description"
                                className="w-full min-h-[80px] px-3 py-2 border border-input rounded-md bg-background resize-none"
                                rows={3}
                                disabled={isSaving}
                            />
                        </div>

                        {/* Active Status */}
                        <div className="space-y-2">
                            <Label htmlFor="active">Product Status</Label>
                            <div className="flex items-center space-x-2">
                                <Switch
                                    id="active"
                                    checked={formData.active}
                                    onCheckedChange={(checked) => setFormData({ ...formData, active: checked })}
                                    disabled={isSaving}
                                />
                                <span className="text-sm">
                                    {formData.active ? 'Active' : 'Inactive'}
                                </span>
                            </div>
                        </div>
                    </div>

                    <DialogFooter>
                        <Button
                            variant="outline"
                            onClick={() => {
                                setIsDialogOpen(false);
                                resetForm();
                            }}
                            disabled={isSaving}
                        >
                            Cancel
                        </Button>
                        <Button onClick={handleSave} disabled={isSaving}>
                            {isSaving ? (
                                <>
                                    <Loader2 className="h-4 w-4 mr-2 animate-spin" />
                                    Saving...
                                </>
                            ) : (
                                <>{editingProduct ? 'Update' : 'Create'} Product</>
                            )}
                        </Button>
                    </DialogFooter>
                </DialogContent>
            </Dialog>

            {/* Summary Stats */}
            <div className="grid gap-4 md:grid-cols-4">
                <Card>
                    <CardHeader className="pb-2">
                        <CardTitle className="text-base">Total Products</CardTitle>
                    </CardHeader>
                    <CardContent>
                        <div className="text-2xl font-bold">{stats.total}</div>
                    </CardContent>
                </Card>

                <Card>
                    <CardHeader className="pb-2">
                        <CardTitle className="text-base">Active Products</CardTitle>
                    </CardHeader>
                    <CardContent>
                        <div className="text-2xl font-bold text-green-600">
                            {stats.active}
                        </div>
                    </CardContent>
                </Card>

                <Card>
                    <CardHeader className="pb-2">
                        <CardTitle className="text-base">Inactive Products</CardTitle>
                    </CardHeader>
                    <CardContent>
                        <div className="text-2xl font-bold text-orange-600">
                            {stats.inactive}
                        </div>
                    </CardContent>
                </Card>

                <Card>
                    <CardHeader className="pb-2">
                        <CardTitle className="text-base">Total Value</CardTitle>
                    </CardHeader>
                    <CardContent>
                        <div className="text-2xl font-bold text-blue-600">
                            ${stats.totalValue.toFixed(2)}
                        </div>
                    </CardContent>
                </Card>
            </div>
        </div>
    );
}