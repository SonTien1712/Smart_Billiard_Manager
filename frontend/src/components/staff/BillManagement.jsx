import React, { useEffect, useMemo, useState } from 'react';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '../ui/card';
import { Button } from '../ui/button';
import { Badge } from '../ui/badge';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '../ui/tabs';
import { PageType } from '../Dashboard';
import { Receipt, Plus, Play, Calculator, CreditCard, Clock } from 'lucide-react';
import { formatVND } from '../../utils/currency';
import { staffService } from '../../services/staffService';
import { Dialog, DialogContent, DialogDescription, DialogHeader, DialogTitle } from '../ui/dialog';
import { useAuth } from '../AuthProvider';


export function BillManagement({ onPageChange }) {
  const { user } = useAuth();
  const [activeTab, setActiveTab] = useState('tables');
  const [selectedTable, setSelectedTable] = useState(null);
  const [activeBillId, setActiveBillId] = useState(null);
  const [currentOrder, setCurrentOrder] = useState([]);
  const [products, setProducts] = useState([]);

      const [tables, setTables] = useState([]);

  const fetchProducts = async () => {
    try {
      const data = await staffService.getProducts(
        (clubId || user?.customerId)
          ? { ...(clubId ? { clubId } : {}), ...(user?.customerId ? { customerId: user.customerId } : {}) }
          : undefined
      );
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
  const [billDetail, setBillDetail] = useState(null);
      const [detailOpen, setDetailOpen] = useState(false);

      const clubId = useMemo(() => user?.clubId || user?.club?.id || user?.employeeClubId || user?.employee?.clubId, [user]);

      // Load items for the current active bill (when resuming), ensure per-bill isolation
      useEffect(() => {
        const loadItems = async () => {
          try {
            if (!activeBillId) {
              return;
            }
            const detail = await staffService.getBillById(activeBillId);
            const items = Array.isArray(detail?.items) ? detail.items : [];
            const mapped = items.map(it => ({
              id: it.productId,
              name: it.productName || `#${it.productId}`,
              price: Number(it.unitPrice || 0),
              quantity: Number(it.quantity || 0),
              detailId: it.id,
            })).filter(x => x.id && x.quantity > 0);
            setCurrentOrder(mapped);
          } catch (e) {
            // If load fails, don't carry over previous order
            setCurrentOrder([]);
          }
        };
        loadItems();
      }, [activeBillId]);

      useEffect(() => {
        const load = async () => {
          try {
            const params = (clubId || user?.customerId)
              ? { ...(clubId ? { clubId } : {}), ...(user?.customerId ? { customerId: user.customerId } : {}), ...(user?.employeeId ? { employeeId: user.employeeId } : {}) }
              : undefined;
            const [tbls, bills] = await Promise.all([
              staffService.getTables(params),
              staffService.getBills({ limit: 5, status: 'Paid', ...(params || {}) })
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
          const opened = await staffService.openTable({
            tableId: table.id,
            clubId: user?.clubId || user?.club?.id,
            customerId: user?.customerId || user?.customer?.id || 1,
            employeeId: user?.employeeId
          });
          // Reset order to avoid leaking items into new bills
          setCurrentOrder([]);
          if (opened?.id) setActiveBillId(opened.id);
          setSelectedTable(table);
          setActiveTab('order');
      // refresh tables to reflect occupied state
      const refreshed = await staffService.getTables(
        (clubId || user?.customerId)
          ? { ...(clubId ? { clubId } : {}), ...(user?.customerId ? { customerId: user.customerId } : {}) }
          : undefined
      );
      const list = Array.isArray(refreshed) ? refreshed : [];
      setTables(list);
      const justOpened = list.find(t => t.id === table.id);
      if (justOpened) {
        setSelectedTable(justOpened);
        if (justOpened.activeBillId && !opened?.id) setActiveBillId(justOpened.activeBillId);
      }
    } catch (e) {
      console.error('Open table failed', e);
      alert('Bàn đã có phiên đang mở hoặc lỗi kết nối.');
    }
  };

  const handleCancelBill = async () => {
    try {
      if (!activeBillId) return;
      await staffService.cancelBill(activeBillId, { employeeId: user?.employeeId });
          const params = (clubId || user?.customerId)
            ? { ...(clubId ? { clubId } : {}), ...(user?.customerId ? { customerId: user.customerId } : {}), ...(user?.employeeId ? { employeeId: user.employeeId } : {}) }
            : undefined;
      const [tbls, bills] = await Promise.all([
        staffService.getTables(params),
        staffService.getBills({ limit: 5, status: 'Paid', ...(params || {}) })
      ]);
      setTables(Array.isArray(tbls) ? tbls : []);
      setRecentBills(Array.isArray(bills) ? bills : []);
      setSelectedTable(null);
      setActiveBillId(null);
      setCurrentOrder([]);
      setActiveTab('tables');
    } catch (e) {
      console.error('Cancel bill failed', e);
      alert('Hủy bàn thất bại.');
    }
  };

  const openBillDetail = async (billId) => {
    try {
      const detail = await staffService.getBillById(billId);
      setBillDetail(detail || null);
      setDetailOpen(true);
    } catch (e) {
      console.error('Load bill detail failed', e);
      setBillDetail(null);
      setDetailOpen(true);
    }
  };

  const handleProcessPayment = async () => {
    try {
      if (!activeBillId) return;
      // Use current UI state for simple product total and 10% tax
      const productsSubtotal = calculateTotal();
      await staffService.completeBill(activeBillId, {
        employeeId: user?.employeeId,
        productTotal: productsSubtotal,
        taxPercent: 10,
        items: currentOrder.map(i => ({ productId: i.id, quantity: i.quantity }))
      });
      // refresh lists and reset state
          const params = (clubId || user?.customerId)
            ? { ...(clubId ? { clubId } : {}), ...(user?.customerId ? { customerId: user.customerId } : {}), ...(user?.employeeId ? { employeeId: user.employeeId } : {}) }
            : undefined;
      const [tbls, bills] = await Promise.all([
        staffService.getTables(params),
        staffService.getBills({ limit: 5, status: 'Paid', ...(params || {}) })
      ]);
      setTables(Array.isArray(tbls) ? tbls : []);
      setRecentBills(Array.isArray(bills) ? bills : []);
      setSelectedTable(null);
      setActiveBillId(null);
      setCurrentOrder([]);
      setActiveTab('tables');
    } catch (e) {
      console.error('Complete bill failed', e);
      alert('Thanh toán thất bại.');
    }
  };

      const addToOrder = async (product) => {
        // Persist to backend so order survives navigation
        if (activeBillId) {
          try {
            const res = await staffService.addBillItem(activeBillId, { productId: product.id, quantity: 1, employeeId: user?.employeeId });
            // Refresh from server to ensure consistency
            const detail = await staffService.getBillById(activeBillId);
            const items = Array.isArray(detail?.items) ? detail.items : [];
            const mapped = items.map(it => ({
              id: it.productId,
              name: it.productName || `#${it.productId}`,
              price: Number(it.unitPrice || 0),
              quantity: Number(it.quantity || 0),
              detailId: it.id,
            })).filter(x => x.id && x.quantity > 0);
            setCurrentOrder(mapped);
          } catch (e) {
            console.error('add item failed', e);
          }
          return;
        }
        // Fallback: local state (should not normally happen because bill opens first)
        const existingItem = currentOrder.find((item) => item.id === product.id);
        if (existingItem) {
          setCurrentOrder(currentOrder.map((item) => item.id === product.id ? { ...item, quantity: item.quantity + 1 } : item));
        } else {
          setCurrentOrder([...currentOrder, { ...product, quantity: 1 }]);
        }
      };

      const decrementItem = async (productId) => {
        const found = currentOrder.find(i => i.id === productId);
        if (!found) return;
        if (activeBillId && found.detailId) {
          try {
            const nextQty = (found.quantity || 0) - 1;
            if (nextQty <= 0) {
              await staffService.removeBillItem(activeBillId, found.detailId, { employeeId: user?.employeeId });
            } else {
              await staffService.updateBillItem(activeBillId, found.detailId, { quantity: nextQty, employeeId: user?.employeeId });
            }
            const detail = await staffService.getBillById(activeBillId);
            const items = Array.isArray(detail?.items) ? detail.items : [];
            const mapped = items.map(it => ({
              id: it.productId,
              name: it.productName || `#${it.productId}`,
              price: Number(it.unitPrice || 0),
              quantity: Number(it.quantity || 0),
              detailId: it.id,
            })).filter(x => x.id && x.quantity > 0);
            setCurrentOrder(mapped);
          } catch (e) {
            console.error('decrement item failed', e);
          }
          return;
        }
        // fallback local
        if (found.quantity <= 1) {
          setCurrentOrder(currentOrder.filter(i => i.id !== productId));
        } else {
          setCurrentOrder(currentOrder.map(i => i.id === productId ? { ...i, quantity: i.quantity - 1 } : i));
        }
      };

      const removeFromOrder = async (productId) => {
        const found = currentOrder.find(i => i.id === productId);
        if (activeBillId && found?.detailId) {
          try {
            await staffService.removeBillItem(activeBillId, found.detailId, { employeeId: user?.employeeId });
            const detail = await staffService.getBillById(activeBillId);
            const items = Array.isArray(detail?.items) ? detail.items : [];
            const mapped = items.map(it => ({
              id: it.productId,
              name: it.productName || `#${it.productId}`,
              price: Number(it.unitPrice || 0),
              quantity: Number(it.quantity || 0),
              detailId: it.id,
            })).filter(x => x.id && x.quantity > 0);
            setCurrentOrder(mapped);
          } catch (e) {
            console.error('remove item failed', e);
          }
          return;
        }
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
                      table.status === 'available' || (table.status === 'occupied' && table.openedByEmployeeId && user?.employeeId && table.openedByEmployeeId === user.employeeId)
                        ? 'hover:bg-accent border-primary/20' : 'opacity-50'
                    }`}
                    onClick={() => {
                      if (table.status === 'available') return handleTableOpen(table);
                      // Allow resume if this employee owns the active bill
                      if (table.status === 'occupied' && table.openedByEmployeeId && user?.employeeId && table.openedByEmployeeId === user.employeeId) {
                        setSelectedTable(table);
                        setActiveBillId(table.activeBillId || null);
                        setActiveTab('order');
                      }
                    }}
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
                        {table.status === 'occupied' && table.openedByEmployeeId && user?.employeeId && table.openedByEmployeeId === user.employeeId && (
                          <Button size="sm" variant="outline" className="w-full">
                            Resume
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
                  <div key={bill.id} className="flex items-center justify-between p-4 border rounded-lg cursor-pointer hover:bg-accent/40" onClick={() => openBillDetail(bill.id)}>
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
          {/* Bill Detail Dialog */}
          <Dialog open={detailOpen} onOpenChange={setDetailOpen}>
            <DialogContent className="max-w-xl">
              <DialogHeader>
                <DialogTitle>Bill #{billDetail?.id}</DialogTitle>
                <DialogDescription>{billDetail?.tableName || ''} — {(billDetail?.status || '').toLowerCase()}</DialogDescription>
              </DialogHeader>
              <div className="space-y-3">
                <div className="grid grid-cols-2 gap-2 text-sm">
                  <div>Start: {billDetail?.startedAt ? new Date(billDetail.startedAt).toLocaleString() : '—'}</div>
                  <div>End: {billDetail?.endedAt ? new Date(billDetail.endedAt).toLocaleString() : '—'}</div>
                  <div>Total Hours: {billDetail?.totalHours ?? 0}</div>
                  <div>Table Cost: {formatVND(Number(billDetail?.totalTableCost || 0))}</div>
                  <div>Products: {formatVND(Number(billDetail?.totalProductCost || 0))}</div>
                  <div>Discount: {formatVND(Number(billDetail?.discount || 0))}</div>
                  <div className="col-span-2 font-semibold">Final: {formatVND(Number(billDetail?.finalAmount || 0))}</div>
                </div>
                <div className="border-t pt-2">
                  <p className="font-medium mb-2">Items</p>
                  {Array.isArray(billDetail?.items) && billDetail.items.length > 0 ? (
                    <div className="space-y-1 text-sm">
                      {billDetail.items.map(it => (
                        <div key={it.id} className="flex justify-between">
                          <span>{it.productName || `#${it.productId}` } x{it.quantity}</span>
                          <span>{formatVND(Number(it.subTotal || 0))}</span>
                        </div>
                      ))}
                    </div>
                  ) : (
                    <p className="text-sm text-muted-foreground">No items</p>
                  )}
                </div>
              </div>
            </DialogContent>
          </Dialog>
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
                              <span className="font-medium">{formatVND(item.price * item.quantity)}</span>
                              <Button size="sm" variant="outline" onClick={() => decrementItem(item.id)}>-</Button>
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
                  <Button variant="destructive" onClick={handleCancelBill} disabled={!activeBillId}>
                    Cancel Bill
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
                          {(() => {
                            const start = selectedTable.startedAt ? new Date(selectedTable.startedAt) : null;
                            let diffMin = 0;
                            if (start) {
                              diffMin = Math.max(0, Math.round((Date.now() - start.getTime()) / 60000));
                            }
                            const rem = diffMin % 6;
                            const rounded = rem >= 3 ? diffMin + (6 - rem) : diffMin - rem;
                            const h = Math.floor(rounded / 60);
                            const m = rounded % 60;
                            return <span>Playing Time: {h} hour{h!==1?'s':''} {m} minutes</span>;
                          })()}
                        </div>
                        <p className="text-sm text-muted-foreground">
                          Rate: {formatVND(selectedTable.hourlyRate)}/hour
                        </p>
                        {(() => {
                          const start = selectedTable.startedAt ? new Date(selectedTable.startedAt) : null;
                          let diffMin = 0;
                          if (start) diffMin = Math.max(0, Math.round((Date.now() - start.getTime()) / 60000));
                          const rem = diffMin % 6;
                          const rounded = rem >= 3 ? diffMin + (6 - rem) : diffMin - rem;
                          const hours = rounded / 60;
                          const subtotal = (selectedTable.hourlyRate || 0) * hours;
                          return <p className="font-medium">Subtotal: {formatVND(subtotal)}</p>;
                        })()}
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

                  {(() => {
                    const start = selectedTable.startedAt ? new Date(selectedTable.startedAt) : null;
                    let diffMin = 0;
                    if (start) diffMin = Math.max(0, Math.round((Date.now() - start.getTime()) / 60000));
                    const rem = diffMin % 6;
                    const rounded = rem >= 3 ? diffMin + (6 - rem) : diffMin - rem;
                    const hours = rounded / 60;
                    const tableSubtotal = (selectedTable.hourlyRate || 0) * hours;
                    const productsSubtotal = calculateTotal();
                    const tax = (tableSubtotal + productsSubtotal) * 0.1;
                    const total = tableSubtotal + productsSubtotal + tax;
                    return (
                      <div className="p-4 bg-muted/50 rounded-lg">
                        <div className="space-y-2">
                          <div className="flex justify-between">
                            <span>Table Time:</span>
                            <span>{formatVND(tableSubtotal)}</span>
                          </div>
                          <div className="flex justify-between">
                            <span>Products:</span>
                            <span>{formatVND(productsSubtotal)}</span>
                          </div>
                          <div className="flex justify-between">
                            <span>Tax (10%):</span>
                            <span>{formatVND(tax)}</span>
                          </div>
                          <div className="border-t pt-2 flex justify-between font-bold text-lg">
                            <span>Total:</span>
                            <span>{formatVND(total)}</span>
                          </div>
                        </div>
                      </div>
                    );
                  })()}

                  <div className="flex justify-end space-x-2">
                    <Button variant="outline" onClick={() => setActiveTab('order')}>
                      Back to Order
                    </Button>
                    <Button variant="destructive" onClick={handleCancelBill} disabled={!activeBillId}>
                      Cancel Bill
                    </Button>
                    <Button className="bg-green-600 hover:bg-green-700" onClick={handleProcessPayment} disabled={!activeBillId}>
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
