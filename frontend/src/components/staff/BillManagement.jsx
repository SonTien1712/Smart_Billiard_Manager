import React, { useEffect, useMemo, useState } from 'react';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '../ui/card';
import { Button } from '../ui/button';
import { Badge } from '../ui/badge';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '../ui/tabs';
import { PageType } from '../Dashboard';
import { Receipt, Plus, Play, Calculator, CreditCard, Clock } from 'lucide-react';
import { formatVND } from '../../utils/currency';
import { staffService } from '../../services/staffService';
import { useAuth } from '../AuthProvider';


export function BillManagement({ onPageChange }) {
  const { user } = useAuth();
  const [activeTab, setActiveTab] = useState('tables');
  const [selectedTable, setSelectedTable] = useState(null);
  const [currentOrder, setCurrentOrder] = useState([]);
  const [products, setProducts] = useState([]);

  const [tables, setTables] = useState([]);

  const fetchProducts = async () => {
    try {
      const data = await staffService.getProducts(clubId ? { clubId } : undefined);
      const normalized = (Array.isArray(data) ? data : []).map(p => ({
        id: p.id,
        name: p.productName,
        price: Number(p.price || 0),
        category: p.category || ''
      }));
      setProducts(normalized);
    } catch (e) {
      console.error('load products failed', e);
      setProducts([]);
    }
  };

  const [recentBills, setRecentBills] = useState([]);

  const clubId = useMemo(() => user?.clubId || user?.club?.id || user?.employeeClubId || user?.employee?.clubId, [user]);

  useEffect(() => {
    const load = async () => {
      try {
        const [tbls, bills] = await Promise.all([
          staffService.getTables(clubId ? { clubId } : undefined),
          staffService.getBills({ limit: 5, status: 'Paid', ...(clubId ? { clubId } : {}) })
        ]);
        setTables(Array.isArray(tbls) ? tbls : []);
        setRecentBills(Array.isArray(bills) ? bills : []);
        await fetchProducts();
      } catch (e) {
        console.error('Failed to load tables/bills', e);
        setTables([]);
        setRecentBills([]);
      }
    };
    load();
  }, [clubId]);

  const handleTableOpen = async (table) => {
    try {
      // Attempt to create/open an active bill for this table
      await staffService.openTable({
        tableId: table.id,
        clubId: user?.clubId || user?.club?.id,
        customerId: user?.customerId || user?.customer?.id || 1,
        employeeId: user?.employeeId
      });
      setSelectedTable(table);
      setActiveTab('order');
      // refresh tables to reflect occupied state
      const refreshed = await staffService.getTables(clubId ? { clubId } : undefined);
      setTables(Array.isArray(refreshed) ? refreshed : []);
    } catch (e) {
      console.error('Open table failed', e);
      alert('Bàn đã có phiên đang mở hoặc lỗi kết nối.');
    }
  };

  const addToOrder = (product) => {
    const existingItem = currentOrder.find((item) => item.id === product.id);
    if (existingItem) {
      setCurrentOrder(currentOrder.map((item) => 
        item.id === product.id ? { ...item, quantity: item.quantity + 1 } : item
      ));
    } else {
      setCurrentOrder([...currentOrder, { ...product, quantity: 1 }]);
    }
  };

  const removeFromOrder = (productId) => {
    setCurrentOrder(currentOrder.filter((item) => item.id !== productId));
  };

  const calculateTotal = () => {
    return currentOrder.reduce((total, item) => total + (item.price * item.quantity), 0);
  };

  const getTableStatusColor = (status) => {
    switch (status) {
      case 'available': return 'default';
      case 'occupied': return 'destructive';
      case 'maintenance': return 'secondary';
      default: return 'outline';
    }
  };

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-3xl font-semibold">Bill Management</h1>
        <p className="text-muted-foreground">Manage table bookings and process orders</p>
      </div>

      <Tabs value={activeTab} onValueChange={setActiveTab}>
        <TabsList>
          <TabsTrigger value="tables">Open Table</TabsTrigger>
          <TabsTrigger value="order" disabled={!selectedTable}>Order</TabsTrigger>
          <TabsTrigger value="checkout" disabled={!selectedTable}>Checkout</TabsTrigger>
        </TabsList>

        <TabsContent value="tables" className="space-y-6">
          <Card>
            <CardHeader>
              <CardTitle>Available Tables</CardTitle>
              <CardDescription>Select a table to start a new session</CardDescription>
            </CardHeader>
            <CardContent>
              <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-4">
                {tables.map((table) => (
                  <Card 
                    key={table.id} 
                    className={`cursor-pointer transition-colors ${
                      table.status === 'available' ? 'hover:bg-accent border-primary/20' : 'opacity-50'
                    }`}
                    onClick={() => table.status === 'available' && handleTableOpen(table)}
                  >
                    <CardContent className="p-4">
                      <div className="space-y-2">
                        <div className="flex items-center justify-between">
                          <h3 className="font-semibold">{table.name}</h3>
                          <Badge variant={getTableStatusColor(table.status)}>
                            {table.status}
                          </Badge>
                        </div>
                        <p className="text-sm text-muted-foreground">{table.type}</p>
                        <p className="text-sm font-medium">{formatVND(table.hourlyRate)}/hour</p>
                        {table.status === 'occupied' && (
                          <div className="text-xs text-muted-foreground">
                            {table.customer && <p>Customer: {table.customer}</p>}
                            {table.startedAt && (
                              <p>Started: {new Date(table.startedAt).toLocaleTimeString()}
                              </p>
                            )}
                          </div>
                        )}
                        {table.status === 'available' && (
                          <Button size="sm" className="w-full">
                            <Play className="h-4 w-4 mr-2" />
                            Open Table
                          </Button>
                        )}
                      </div>
                    </CardContent>
                  </Card>
                ))}
              </div>
            </CardContent>
          </Card>

          <Card>
            <CardHeader>
              <CardTitle>Recent Bills</CardTitle>
              <CardDescription>Recently completed transactions</CardDescription>
            </CardHeader>
            <CardContent>
              <div className="space-y-4">
                {recentBills.map((bill) => (
                  <div key={bill.id} className="flex items-center justify-between p-4 border rounded-lg">
                    <div className="space-y-1">
                      <p className="font-medium">#{String(bill.id).padStart(3,'0')}</p>
                      <p className="text-sm text-muted-foreground">{bill.tableName || '—'}</p>
                      {bill.createdAt && (
                        <p className="text-xs text-muted-foreground">{new Date(bill.createdAt).toLocaleTimeString()}</p>
                      )}
                    </div>
                    <div className="text-right">
                      <p className="font-medium">{formatVND(bill.amount)}</p>
                      <Badge variant="outline">{(bill.status || '').toLowerCase()}</Badge>
                    </div>
                  </div>
                ))}
              </div>
            </CardContent>
          </Card>
        </TabsContent>

        <TabsContent value="order" className="space-y-6">
          {selectedTable && (
            <Card>
              <CardHeader>
                <CardTitle>Order for {selectedTable.name}</CardTitle>
                <CardDescription>Add products to the customer's order</CardDescription>
              </CardHeader>
              <CardContent>
                <div className="grid gap-6 md:grid-cols-2">
                  <div>
                    <h3 className="font-medium mb-4">Available Products</h3>
                    <div className="space-y-2">
                      {products.map((product) => (
                        <div key={product.id} className="flex items-center justify-between p-3 border rounded-lg">
                          <div>
                            <p className="font-medium">{product.name}</p>
                            <p className="text-sm text-muted-foreground">{product.category}</p>
                            <p className="text-sm font-medium">{formatVND(product.price)}</p>
                          </div>
                          <Button size="sm" onClick={() => addToOrder(product)}>
                            <Plus className="h-4 w-4" />
                          </Button>
                        </div>
                      ))}
                    </div>
                  </div>

                  <div>
                    <h3 className="font-medium mb-4">Current Order</h3>
                    {currentOrder.length > 0 ? (
                      <div className="space-y-2">
                        {currentOrder.map((item) => (
                          <div key={item.id} className="flex items-center justify-between p-3 border rounded-lg">
                            <div>
                              <p className="font-medium">{item.name}</p>
                              <p className="text-sm text-muted-foreground">
                                {formatVND(item.price)} x {item.quantity}
                              </p>
                            </div>
                            <div className="flex items-center space-x-2">
                              <span className="font-medium">${(item.price * item.quantity).toFixed(2)}</span>
                              <Button size="sm" variant="outline" onClick={() => removeFromOrder(item.id)}>
                                Remove
                              </Button>
                            </div>
                          </div>
                        ))}
                        <div className="border-t pt-2">
                          <div className="flex justify-between font-medium">
                            <span>Total:</span>
                            <span>{formatVND(calculateTotal())}</span>
                          </div>
                        </div>
                      </div>
                    ) : (
                      <p className="text-muted-foreground">No items added yet</p>
                    )}
                  </div>
                </div>
                
                <div className="flex justify-end space-x-2 mt-6">
                  <Button variant="outline" onClick={() => setActiveTab('tables')}>
                    Back to Tables
                  </Button>
                  <Button onClick={() => setActiveTab('checkout')}>
                    <Calculator className="h-4 w-4 mr-2" />
                    Proceed to Checkout
                  </Button>
                </div>
              </CardContent>
            </Card>
          )}
        </TabsContent>

        <TabsContent value="checkout" className="space-y-6">
          {selectedTable && (
            <Card>
              <CardHeader>
                <CardTitle>Checkout - {selectedTable.name}</CardTitle>
                <CardDescription>Calculate total and process payment</CardDescription>
              </CardHeader>
              <CardContent>
                <div className="space-y-6">
                  <div className="grid gap-4 md:grid-cols-2">
                    <div className="space-y-4">
                      <h3 className="font-medium">Table Time</h3>
                      <div className="p-4 border rounded-lg">
                        <div className="flex items-center space-x-2 mb-2">
                          <Clock className="h-4 w-4 text-primary" />
                          <span>Playing Time: 2 hours 30 minutes</span>
                        </div>
                        <p className="text-sm text-muted-foreground">
                          Rate: {formatVND(selectedTable.hourlyRate)}/hour
                        </p>
                        <p className="font-medium">
                          Subtotal: {formatVND(selectedTable.hourlyRate * 2.5)}
                        </p>
                      </div>
                    </div>

                    <div className="space-y-4">
                      <h3 className="font-medium">Products</h3>
                      <div className="p-4 border rounded-lg">
                        {currentOrder.length > 0 ? (
                          <div className="space-y-2">
                            {currentOrder.map((item) => (
                              <div key={item.id} className="flex justify-between text-sm">
                                <span>{item.name} x{item.quantity}</span>
                                <span>{formatVND(item.price * item.quantity)}</span>
                              </div>
                            ))}
                            <div className="border-t pt-2 font-medium">
                              <div className="flex justify-between">
                                <span>Products Total:</span>
                                <span>{formatVND(calculateTotal())}</span>
                              </div>
                            </div>
                          </div>
                        ) : (
                          <p className="text-sm text-muted-foreground">No products ordered</p>
                        )}
                      </div>
                    </div>
                  </div>

                  <div className="p-4 bg-muted/50 rounded-lg">
                    <div className="space-y-2">
                      <div className="flex justify-between">
                        <span>Table Time:</span>
                        <span>{formatVND(selectedTable.hourlyRate * 2.5)}</span>
                      </div>
                      <div className="flex justify-between">
                        <span>Products:</span>
                        <span>{formatVND(calculateTotal())}</span>
                      </div>
                      <div className="flex justify-between">
                        <span>Tax (10%):</span>
                        <span>{formatVND((selectedTable.hourlyRate * 2.5 + calculateTotal()) * 0.1)}</span>
                      </div>
                      <div className="border-t pt-2 flex justify-between font-bold text-lg">
                        <span>Total:</span>
                        <span>{formatVND((selectedTable.hourlyRate * 2.5 + calculateTotal()) * 1.1)}</span>
                      </div>
                    </div>
                  </div>

                  <div className="flex justify-end space-x-2">
                    <Button variant="outline" onClick={() => setActiveTab('order')}>
                      Back to Order
                    </Button>
                    <Button className="bg-green-600 hover:bg-green-700">
                      <CreditCard className="h-4 w-4 mr-2" />
                      Process Payment
                    </Button>
                  </div>
                </div>
              </CardContent>
            </Card>
          )}
        </TabsContent>
      </Tabs>
    </div>
  );
}
