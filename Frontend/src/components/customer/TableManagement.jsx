import React, { useState, useEffect, useCallback } from "react"
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle
} from "../ui/card"
import { Button } from "../ui/button"
import { Input } from "../ui/input"
import { Label } from "../ui/label"
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow
} from "../ui/table"
import { Badge } from "../ui/badge"
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue
} from "../ui/select"
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle
} from "../ui/dialog"
import {
  Table as TableIcon,
  Plus,
  Edit,
  Trash2,
  DollarSign
} from "lucide-react"
import { customerService } from '../../services/customerService';
import { useApi } from '../../hooks/useApi';

export function TableManagement({ onPageChange }) {
  const [isDialogOpen, setIsDialogOpen] = useState(false)
  const [editingTable, setEditingTable] = useState(null)
  const [formData, setFormData] = useState({
    name: "",
    type: "Pool",
    hourlyRate: 25,
    status: "available",
    club: "Downtown Billiards Club"
  })

  const customer = JSON.parse(localStorage.getItem("currentUser"));
  const customerId = customer?.id;

  // ✅ Tạo API function ổn định
  const getTablesFunction = useCallback(() => {
    return customerService.getTablesByCustomerId(customerId);
  }, [customerId]);

  // ✅ Gọi API bằng hook useApi
  const {
    data: tablesData,
    loading: tablesLoading,
    error: tablesError,
    execute: fetchTables
  } = useApi(getTablesFunction);

  const tables = tablesData || [];

  const {
  data: clubsData,
  loading: clubsLoading,
  error: clubsError,
  execute: fetchClubs
} = useApi(() => customerService.getClubsByCustomer(customerId));

useEffect(() => {
  if (customerId) {
    fetchClubs();
  }
}, [ customerId]);

const clubs = clubsData || [];

  // ✅ Gọi API khi component mount
  useEffect(() => {
    if (customerId) {
      fetchTables();
    }
  }, [fetchTables, customerId]);

  const { execute: createTable, loading: createLoading } = useApi(
    async (data) => customerService.createTable(data),
    {
      onSuccess: (newTable) => {
        handleSuccess('Table created successfully');
        setIsDialogOpen(false);
        fetchTables();
      }
    }
  );

  const { execute: updateTable, loading: updateLoading } = useApi(
    async (id, data) => customerService.updateTable(id, data),
    {
      onSuccess: () => {
        handleSuccess('Table updated successfully');
        setIsDialogOpen(false);
        fetchTables();
      }
    }
  );

  const { execute: deleteTable, loading: deleteLoading } = useApi(
    async (id) => customerService.deleteTable(id),
    {
      onSuccess: () => {
        handleSuccess('Club deleted successfully');
        fetchTables();
      }
    }
  );
  const handleEdit = (table) => {
    setEditingTable(table);
    setFormData({
      name: table.tableName,
      type: table.tableType,
      hourlyRate: table.hourlyRate,
      status: table.tableStatus,
      club: table.clubId
    });
    setIsDialogOpen(true);
  };

  const handleAdd = () => {
    setEditingTable(null)
    setFormData({
      name: "",
      type: "Pool",
      hourlyRate: 0,
      status: "available",
      club: ""
    })
    setIsDialogOpen(true)
  }

  const handleSave = async () => {
  const newTable = {
    tableName: formData.name,
    tableType: formData.type,
    hourlyRate: formData.hourlyRate,
    tableStatus: formData.status,
    clubId: formData.club
  };

  try {
    if (editingTable) {
      await customerService.updateTable(editingTable.id, newTable);
      
    } else {
      await customerService.createTable(newTable);
      
    }

    setIsDialogOpen(false);
    fetchTables(); // reload danh sách
  } catch (error) {
    
  }
};

  const handleDelete = async (id) => {
  if (window.confirm("Are you sure you want to delete this table?")) {
    try {
      await customerService.deleteTable(id);
      fetchTables(); // reload danh sách
    } catch (error) {
      console.error(error);
    }
  }
};

  const getStatusColor = status => {
    switch (status) {
      case "available":
        return "default"
      case "occupied":
        return "destructive"
      case "maintenance":
        return "secondary"
      default:
        return "outline"
    }
  }

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-semibold">Table Management</h1>
          <p className="text-muted-foreground">Manage your billiard tables</p>
        </div>
        <Button onClick={handleAdd}>
          <Plus className="h-4 w-4 mr-2" />
          Add Table
        </Button>
      </div>

      <Card>
        <CardHeader>
          <CardTitle>Billiard Tables</CardTitle>
          <CardDescription>Manage tables across all your clubs</CardDescription>
        </CardHeader>
        <CardContent>
          <Table>
            <TableHeader>
              <TableRow>
                <TableHead>Table</TableHead>
                <TableHead>Type</TableHead>
                <TableHead>Club</TableHead>
                <TableHead>Hourly Rate</TableHead>
                <TableHead>Status</TableHead>
                <TableHead>Actions</TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {tables.map(table => (
                <TableRow key={table.id}>
                  <TableCell>
                    <div className="flex items-center space-x-2">
                      <TableIcon className="h-4 w-4 text-primary" />
                      <span className="font-medium">{table.tableName}</span>
                    </div>
                  </TableCell>
                  <TableCell>
                    <Badge variant="outline">{table.tableType}</Badge>
                  </TableCell>
                  <TableCell className="text-sm">{table.clubName}</TableCell>
                  <TableCell>
                    <div className="flex items-center space-x-1">
                      <DollarSign className="h-4 w-4 text-muted-foreground" />
                      <span>{table.hourlyRate}/hr</span>
                    </div>
                  </TableCell>
                  <TableCell>
                    <Badge variant={getStatusColor(table.tableStatus)}>
                      {table.tableStatus}
                    </Badge>
                  </TableCell>
                  <TableCell>
                    <div className="flex items-center space-x-2">
                      <Button
                        variant="ghost"
                        size="sm"
                        onClick={() => handleEdit(table)}
                      >
                        <Edit className="h-4 w-4" />
                      </Button>
                      <Button
                        variant="ghost"
                        size="sm"
                        onClick={() => handleDelete(table.id)}
                      >
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
            <DialogTitle>
              {editingTable ? "Edit Table" : "Add New Table"}
            </DialogTitle>
            <DialogDescription>
              {editingTable
                ? "Update table information"
                : "Create a new billiard table"}
            </DialogDescription>
          </DialogHeader>
          <div className="space-y-4">
            <div className="space-y-2">
              <Label htmlFor="name">Table Name</Label>
              <Input
                id="name"
                value={formData.name}
                onChange={e =>
                  setFormData({ ...formData, name: e.target.value })
                }
                placeholder="Enter table name"
              />
            </div>
            <div className="space-y-2">
              <Label htmlFor="type">Table Type</Label>
              <Select
                value={formData.type}
                onValueChange={value =>
                  setFormData({ ...formData, type: value })
                }
              >
                <SelectTrigger>
                  <SelectValue placeholder="Select table type" />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="Pool">Pool</SelectItem>
                  <SelectItem value="Snooker">Snooker</SelectItem>
                  <SelectItem value="Carom">Carom</SelectItem>
                </SelectContent>
              </Select>
            </div>
            <div className="space-y-2">
              <Label htmlFor="club">Club</Label>
              <Select
                value={formData.club}
                onValueChange={(value) => setFormData({ ...formData, club: value })}
              >
                <SelectTrigger>
                  <SelectValue placeholder="Select a club" />
                </SelectTrigger>
                <SelectContent>
                  {clubs.map((club) => (
                    <SelectItem key={club.id} value={club.id}>
                      {club.clubName}
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
            </div>

            <div className="space-y-2">
              <Label htmlFor="hourlyRate">Hourly Rate ($)</Label>
              <Input
                id="hourlyRate"
                type="number"
                value={formData.hourlyRate}
                onChange={e =>
                  setFormData({
                    ...formData,
                    hourlyRate: parseInt(e.target.value)
                  })
                }
                placeholder="Enter hourly rate"
              />
            </div>
            <div className="space-y-2">
              <Label htmlFor="status">Status</Label>
              <Select
                value={formData.status}
                onValueChange={value =>
                  setFormData({ ...formData, status: value })
                }
              >
                <SelectTrigger>
                  <SelectValue placeholder="Select status" />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="available">Available</SelectItem>
                  <SelectItem value="occupied">Occupied</SelectItem>
                  <SelectItem value="maintenance">Maintenance</SelectItem>
                </SelectContent>
              </Select>
            </div>
          </div>
          <DialogFooter>
            <Button variant="outline" onClick={() => setIsDialogOpen(false)}>
              Cancel
            </Button>
            <Button onClick={handleSave}>
              {editingTable ? "Update" : "Create"} Table
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </div>
  )
}
