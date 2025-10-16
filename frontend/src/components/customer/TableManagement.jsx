import React, { useState } from "react"
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

export function TableManagement({ onPageChange }) {
  const [tables, setTables] = useState([
    {
      id: "1",
      name: "Table 1",
      type: "Pool",
      hourlyRate: 25,
      status: "available",
      club: "Downtown Billiards Club"
    },
    {
      id: "2",
      name: "Table 2",
      type: "Snooker",
      hourlyRate: 30,
      status: "occupied",
      club: "Downtown Billiards Club"
    },
    {
      id: "3",
      name: "Table 3",
      type: "Pool",
      hourlyRate: 25,
      status: "maintenance",
      club: "Downtown Billiards Club"
    },
    {
      id: "4",
      name: "Table 4",
      type: "Carom",
      hourlyRate: 35,
      status: "available",
      club: "Uptown Pool Hall"
    }
  ])

  const [isDialogOpen, setIsDialogOpen] = useState(false)
  const [editingTable, setEditingTable] = useState(null)
  const [formData, setFormData] = useState({
    name: "",
    type: "Pool",
    hourlyRate: 25,
    status: "available",
    club: "Downtown Billiards Club"
  })

  const handleEdit = table => {
    setEditingTable(table)
    setFormData(table)
    setIsDialogOpen(true)
  }

  const handleAdd = () => {
    setEditingTable(null)
    setFormData({
      name: "",
      type: "Pool",
      hourlyRate: 25,
      status: "available",
      club: "Downtown Billiards Club"
    })
    setIsDialogOpen(true)
  }

  const handleSave = () => {
    if (editingTable) {
      setTables(
        tables.map(table =>
          table.id === editingTable.id
            ? { ...formData, id: editingTable.id }
            : table
        )
      )
    } else {
      setTables([...tables, { ...formData, id: Date.now().toString() }])
    }
    setIsDialogOpen(false)
  }

  const handleDelete = id => {
    setTables(tables.filter(table => table.id !== id))
  }

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
                      <span className="font-medium">{table.name}</span>
                    </div>
                  </TableCell>
                  <TableCell>
                    <Badge variant="outline">{table.type}</Badge>
                  </TableCell>
                  <TableCell className="text-sm">{table.club}</TableCell>
                  <TableCell>
                    <div className="flex items-center space-x-1">
                      <DollarSign className="h-4 w-4 text-muted-foreground" />
                      <span>${table.hourlyRate}/hr</span>
                    </div>
                  </TableCell>
                  <TableCell>
                    <Badge variant={getStatusColor(table.status)}>
                      {table.status}
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
