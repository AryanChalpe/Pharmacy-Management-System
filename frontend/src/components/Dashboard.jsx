import { useState, useEffect, useContext } from 'react';
import axios from 'axios';
import { AuthContext } from '../context/AuthContext';

const API_BASE = `${import.meta.env.VITE_API_BASE_URL}/api/medicines`;

function Dashboard() {
    const [medicines, setMedicines] = useState([]);
    const [loading, setLoading] = useState(true);
    const [modalMode, setModalMode] = useState(null); // 'add', 'edit', 'sell', 'billing', null
    const [showModal, setShowModal] = useState(false);
    const [currentMedicine, setCurrentMedicine] = useState(null);
    const [error, setError] = useState('');

    // Pagination State
    const [currentPage, setCurrentPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);
    const [pageSize, setPageSize] = useState(8);

    const { logout, user } = useContext(AuthContext);

    // Form State
    const [formData, setFormData] = useState({
        name: '',
        description: '',
        price: '',
        quantity: '',
        expiryDate: ''
    });
    const [billingData, setBillingData] = useState({
        medicineName: '',
        quantity: 1,
        userEmail: ''
    });
    // derived state for billing availability
    const [availableStock, setAvailableStock] = useState(0);
    const [sellQuantity, setSellQuantity] = useState(1);
    const [searchTerm, setSearchTerm] = useState('');

    // Sales State
    const [salesData, setSalesData] = useState([]);
    const [salesStats, setSalesStats] = useState({ totalRevenue: 0, byMedicine: [] });

    // Suppliers State
    const [suppliers, setSuppliers] = useState([]);
    const [supplierForm, setSupplierForm] = useState({ name: '', contactNumber: '', email: '', address: '' });

    // Advanced Filtering & Sorting
    const [filterStatus, setFilterStatus] = useState('All'); // All, Available, Low Stock, Out of Stock, Expired
    const [sortConfig, setSortConfig] = useState({ key: 'name', direction: 'asc' });

    const fetchSales = async () => {
        try {
            const response = await axios.get(`${import.meta.env.VITE_API_BASE_URL}/api/sales`, authConfig);
            const sales = response.data;

            const totalRevenue = sales.reduce((sum, sale) => sum + sale.totalPrice, 0);

            const byMedicine = Object.values(sales.reduce((acc, sale) => {
                if (!acc[sale.medicineName]) {
                    acc[sale.medicineName] = { name: sale.medicineName, totalQty: 0, totalRev: 0 };
                }
                acc[sale.medicineName].totalQty += sale.quantity;
                acc[sale.medicineName].totalRev += sale.totalPrice;
                return acc;
            }, {}));

            setSalesStats({ totalRevenue, byMedicine });
            setSalesData(sales);
        } catch (error) {
            console.error("Error fetching sales:", error);
        }
    };

    const isExpired = (med) => {
        if (med.expired) return true;
        if (!med.expiryDate) return false;
        const expiryDate = new Date(med.expiryDate);
        const today = new Date();
        today.setHours(0, 0, 0, 0);
        return expiryDate < today;
    };

    const handleSort = (key) => {
        let direction = 'asc';
        if (sortConfig.key === key && sortConfig.direction === 'asc') {
            direction = 'desc';
        }
        setSortConfig({ key, direction });
    };

    const sortedMedicines = [...medicines].sort((a, b) => {
        if (a[sortConfig.key] < b[sortConfig.key]) {
            return sortConfig.direction === 'asc' ? -1 : 1;
        }
        if (a[sortConfig.key] > b[sortConfig.key]) {
            return sortConfig.direction === 'asc' ? 1 : -1;
        }
        return 0;
    });

    const filteredMedicines = sortedMedicines.filter(med => {
        const matchesSearch = med.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
            med.description.toLowerCase().includes(searchTerm.toLowerCase());

        const expired = isExpired(med);
        const lowStock = med.quantity > 0 && med.quantity <= 10;
        const outOfStock = med.quantity === 0;

        let matchesStatus = true;
        if (filterStatus === 'Available') matchesStatus = !expired && med.quantity > 0;
        else if (filterStatus === 'Low Stock') matchesStatus = !expired && lowStock;
        else if (filterStatus === 'Out of Stock') matchesStatus = outOfStock;
        else if (filterStatus === 'Expired') matchesStatus = expired;

        return matchesSearch && matchesStatus;
    });

    // Add auth header to requests
    const authConfig = {
        headers: { Authorization: `Bearer ${sessionStorage.getItem('token')}` }
    };

    const fetchSaleStats = async () => {
        try {
            const token = sessionStorage.getItem('token');
            const response = await axios.get(`${import.meta.env.VITE_API_BASE_URL}/api/sales`, {
                headers: { 'Authorization': `Bearer ${token}` }
            });
            const sales = response.data;
            const totalRevenue = sales.reduce((acc, curr) => acc + curr.totalPrice, 0);
            const medicineMap = {};
            sales.forEach(sale => {
                if (!medicineMap[sale.medicineName]) {
                    medicineMap[sale.medicineName] = { totalQty: 0, totalRev: 0 };
                }
                const current = medicineMap[sale.medicineName];
                medicineMap[sale.medicineName] = {
                    totalQty: current.totalQty + sale.quantity,
                    totalRev: current.totalRev + sale.totalPrice
                };
            });
            const byMedicine = Object.keys(medicineMap).map(name => ({
                name,
                ...medicineMap[name]
            }));
            setSalesStats({ totalRevenue, byMedicine });
        } catch (error) {
            console.error('Error fetching sales:', error);
        }
    };

    const fetchSuppliers = async () => {
        try {
            const token = sessionStorage.getItem('token');
            const response = await axios.get(`${import.meta.env.VITE_API_BASE_URL}/api/suppliers`, {
                headers: { 'Authorization': `Bearer ${token}` }
            });
            setSuppliers(response.data);
        } catch (error) {
            console.error('Error fetching suppliers:', error);
        }
    };

    const handleSupplierSubmit = async (e) => {
        e.preventDefault();
        try {
            const token = sessionStorage.getItem('token');
            await axios.post(`${import.meta.env.VITE_API_BASE_URL}/api/suppliers`, supplierForm, {
                headers: { 'Authorization': `Bearer ${token}` }
            });
            fetchSuppliers();
            setSupplierForm({ name: '', contactNumber: '', email: '', address: '' });
        } catch (error) {
            console.error('Error adding supplier:', error);
            alert('Error adding supplier: ' + (error.response?.data?.message || error.message));
        }
    };

    const handleDeleteSupplier = async (id) => {
        if (!window.confirm('Are you sure you want to delete this supplier?')) return;
        try {
            const token = sessionStorage.getItem('token');
            await axios.delete(`${import.meta.env.VITE_API_BASE_URL}/api/suppliers/${id}`, {
                headers: { 'Authorization': `Bearer ${token}` }
            });
            fetchSuppliers();
        } catch (error) {
            console.error('Error deleting supplier:', error);
            alert('Error deleting supplier: ' + (error.response?.data?.message || error.message));
        }
    };

    const openModal = async (mode, medicine = null) => {
        setModalMode(mode);
        setShowModal(true);
        setError('');

        if (mode === 'sales') {
            await fetchSaleStats();
        } else if (mode === 'suppliers') {
            await fetchSuppliers();
        } else if (mode === 'edit' && medicine) {
            setCurrentMedicine(medicine);
            setFormData({
                name: medicine.name,
                description: medicine.description,
                price: medicine.price,
                quantity: medicine.quantity,
                expiryDate: medicine.expiryDate || ''
            });
        } else if (mode === 'sell' && medicine) {
            setCurrentMedicine(medicine);
            setSellQuantity(1);
        } else if (mode === 'billing') {
            setBillingData({ medicineName: '', quantity: 1, userEmail: '' });
            setAvailableStock(0);
        } else { // 'add' mode
            setCurrentMedicine(null);
            setFormData({ name: '', description: '', price: '', quantity: '', expiryDate: '' });
        }
    };

    useEffect(() => {
        fetchMedicines();
    }, [currentPage, pageSize]);

    // Reset page when filtering, searching, or changing page size
    useEffect(() => {
        setCurrentPage(0);
    }, [searchTerm, filterStatus, pageSize]);

    const fetchMedicines = async () => {
        try {
            const response = await axios.get(`${API_BASE}?paginate=true&page=${currentPage}&size=${pageSize}`, authConfig);
            setMedicines(response.data.content);
            setTotalPages(response.data.totalPages);
        } catch (error) {
            console.error('Error fetching data:', error);
            if (error.response && error.response.status === 403) {
                alert("Session expired or unauthorized. Please login again.");
                logout();
            }
        } finally {
            setLoading(false);
        }
    };

    const handleInputChange = (e) => {
        setFormData({ ...formData, [e.target.name]: e.target.value });
    };

    const closeModal = () => {
        setModalMode(null);
        setCurrentMedicine(null); // Changed from selectedMedicine
        setShowModal(false); // Hide modal
        setError(''); // Clear error
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            if (modalMode === 'add') {
                await axios.post(API_BASE, formData, authConfig);
            } else if (modalMode === 'edit') {
                await axios.put(`${API_BASE}/${currentMedicine.id}`, formData, authConfig); // Changed from selectedMedicine
            } else if (modalMode === 'sell') {
                await axios.post(`${API_BASE}/${currentMedicine.id}/sell?quantity=${sellQuantity}`, {}, authConfig); // Changed from selectedMedicine
            } else if (modalMode === 'billing') {
                await axios.post(`${API_BASE}/bill`, billingData, authConfig);
            }
            fetchMedicines();
            closeModal();
        } catch (error) {
            alert('Operation failed: ' + (error.response?.data?.message || error.message));
        }
    };

    // Watch logic to update available stock when medicine name is selected in billing
    useEffect(() => {
        if (modalMode === 'billing' && billingData.medicineName) {
            const med = medicines.find(m => m.name === billingData.medicineName);
            if (med) {
                setAvailableStock(med.quantity);
            } else {
                setAvailableStock(0);
            }
        }
    }, [billingData.medicineName, medicines, modalMode]);

    const handleDelete = async (id) => {
        if (confirm('Are you sure you want to delete this medicine?')) {
            try {
                await axios.delete(`${API_BASE}/${id}`, authConfig);
                fetchMedicines();
            } catch (error) {
                alert('Delete failed');
            }
        }
    };


    return (
        <div className="container">
            <header style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', gap: '2rem' }}>
                <h1 style={{ whiteSpace: 'nowrap', flexShrink: 0 }}>💊 Pharmacy Manager</h1>
                <div style={{ display: 'flex', gap: '1rem', alignItems: 'center', flexWrap: 'nowrap' }}>
                    <div style={{ marginRight: '1rem', textAlign: 'right', flexShrink: 0, minWidth: 'fit-content' }}>
                        <div style={{ fontSize: '0.9rem', color: 'var(--text-light)', whiteSpace: 'nowrap' }}>Welcome back,</div>
                        <div style={{ fontWeight: 'bold', whiteSpace: 'nowrap' }}>{user?.username} <span className="badge badge-success" style={{ fontSize: '0.6rem', verticalAlign: 'middle' }}>{user?.role}</span></div>
                    </div>

                    <button className="btn btn-sales" onClick={() => openModal('sales')}>
                        💰 Sales
                    </button>
                    <button className="btn btn-suppliers" onClick={() => openModal('suppliers')}>
                        🚚 Suppliers
                    </button>
                    <button className="btn btn-billing" onClick={() => openModal('billing')}>
                        🧾 Billing
                    </button>
                    <button className="btn btn-add" onClick={() => openModal('add')}>
                        + Add Medicine
                    </button>
                    <button className="btn btn-logout" onClick={() => { logout(); navigate('/login'); }}>
                        Logout
                    </button>
                </div>
            </header>

            {loading ? (
                <p>Loading...</p>
            ) : (
                <div className="dashboard-content">
                    <div style={{ marginBottom: '1rem', display: 'flex', justifyContent: 'space-between', alignItems: 'center', gap: '1rem' }}>
                        <div style={{ display: 'flex', gap: '1rem', alignItems: 'center' }}>
                            <div style={{ display: 'flex', gap: '0.5rem', alignItems: 'center' }}>
                                <span style={{ fontSize: '0.9rem', color: 'var(--text-light)', fontWeight: '600' }}>Filter:</span>
                                <select
                                    value={filterStatus}
                                    onChange={(e) => setFilterStatus(e.target.value)}
                                    className="filter-dropdown"
                                >
                                    <option value="All">All Medicines</option>
                                    <option value="Available">Available</option>
                                    <option value="Low Stock">Low Stock</option>
                                    <option value="Out of Stock">Out of Stock</option>
                                    <option value="Expired">Expired</option>
                                </select>
                            </div>
                            <div style={{ display: 'flex', gap: '0.5rem', alignItems: 'center' }}>
                                <span style={{ fontSize: '0.9rem', color: 'var(--text-light)', fontWeight: '600' }}>Show:</span>
                                <select
                                    value={pageSize}
                                    onChange={(e) => setPageSize(parseInt(e.target.value))}
                                    className="filter-dropdown"
                                    style={{ minWidth: '80px' }}
                                >
                                    <option value="8">8</option>
                                    <option value="15">15</option>
                                    <option value="30">30</option>
                                    <option value="50">50</option>
                                </select>
                            </div>
                        </div>
                        <input
                            type="text"
                            placeholder="🔍 Search medicines..."
                            value={searchTerm}
                            onChange={(e) => setSearchTerm(e.target.value)}
                            className="search-bar"
                            style={{ margin: 0 }}
                        />
                    </div>

                    <div className="table-container">
                        <table className="table">
                            <thead>
                                <tr>
                                    <th onClick={() => handleSort('name')} className="sortable">
                                        Name {sortConfig.key === 'name' && (sortConfig.direction === 'asc' ? '↑' : '↓')}
                                    </th>
                                    <th>Description</th>
                                    <th onClick={() => handleSort('expiryDate')} className="sortable">
                                        Expiry Date {sortConfig.key === 'expiryDate' && (sortConfig.direction === 'asc' ? '↑' : '↓')}
                                    </th>
                                    <th onClick={() => handleSort('price')} className="sortable">
                                        Price {sortConfig.key === 'price' && (sortConfig.direction === 'asc' ? '↑' : '↓')}
                                    </th>
                                    <th onClick={() => handleSort('quantity')} className="sortable">
                                        Stock {sortConfig.key === 'quantity' && (sortConfig.direction === 'asc' ? '↑' : '↓')}
                                    </th>
                                    <th>Actions</th>
                                </tr>
                            </thead>
                            <tbody>
                                {filteredMedicines.length > 0 ? (
                                    filteredMedicines.map((med) => (
                                        <tr key={med.id}>
                                            <td>
                                                <strong>{med.name}</strong>
                                            </td>
                                            <td style={{ color: 'var(--text-sub)' }}>{med.description}</td>
                                            <td style={{ color: 'var(--text-sub)' }}>{med.expiryDate}</td>
                                            <td style={{ fontWeight: 'bold', color: 'var(--primary)' }}>₹{med.price}</td>
                                            <td>
                                                <span className={`badge ${isExpired(med) ? 'badge-danger' : med.quantity > 10 ? 'badge-success' : med.quantity > 0 ? 'badge-warning' : 'badge-danger'}`}>
                                                    {isExpired(med) ? 'EXPIRED' : med.quantity}
                                                </span>
                                            </td>
                                            <td>
                                                <button className="btn btn-sm btn-edit" onClick={() => openModal('edit', med)}>Edit</button>
                                                <button
                                                    className="btn btn-sm btn-sell"
                                                    onClick={() => openModal('sell', med)}
                                                    disabled={isExpired(med)}
                                                    style={isExpired(med) ? { opacity: 0.5, cursor: 'not-allowed' } : {}}
                                                >
                                                    {isExpired(med) ? 'Expired' : 'Sell'}
                                                </button>
                                                <button className="btn btn-sm btn-delete" onClick={() => handleDelete(med.id)}>Delete</button>
                                            </td>
                                        </tr>
                                    ))
                                ) : (
                                    <tr>
                                        <td colSpan="6" style={{ textAlign: 'center', padding: '2rem', color: 'var(--text-sub)' }}> {/* Colspan changed to 6 */}
                                            No medicines found matching "{searchTerm}"
                                        </td>
                                    </tr>
                                )}
                            </tbody>
                        </table>
                    </div>

                    {/* Pagination Controls */}
                    {totalPages > 1 && (
                        <div className="pagination" style={{ marginTop: '1.5rem', display: 'flex', justifyContent: 'center', gap: '0.5rem', alignItems: 'center' }}>
                            <button
                                className="btn btn-sm btn-edit"
                                onClick={() => setCurrentPage(prev => Math.max(0, prev - 1))}
                                disabled={currentPage === 0}
                                style={currentPage === 0 ? { opacity: 0.5, cursor: 'not-allowed' } : {}}
                            >
                                Previous
                            </button>

                            <div style={{ display: 'flex', gap: '0.25rem' }}>
                                {[...Array(totalPages)].map((_, i) => (
                                    <button
                                        key={i}
                                        className={`btn btn-sm ${currentPage === i ? 'btn-add' : 'btn-edit'}`}
                                        onClick={() => setCurrentPage(i)}
                                        style={{ minWidth: '32px', justifyContent: 'center' }}
                                    >
                                        {i + 1}
                                    </button>
                                ))}
                            </div>

                            <button
                                className="btn btn-sm btn-edit"
                                onClick={() => setCurrentPage(prev => Math.min(totalPages - 1, prev + 1))}
                                disabled={currentPage === totalPages - 1}
                                style={currentPage === totalPages - 1 ? { opacity: 0.5, cursor: 'not-allowed' } : {}}
                            >
                                Next
                            </button>
                        </div>
                    )}
                </div>
            )}

            {/* Modal */}
            {showModal && (
                <div className="modal-overlay" onClick={(e) => e.target.className === 'modal-overlay' && closeModal()}>
                    <div className={`modal modal-${modalMode}`}>
                        <div className="modal-header">
                            <h2 style={{ color: '#1a237e', marginTop: 0 }}>
                                {modalMode === 'add' ? 'Add New Medicine' :
                                    modalMode === 'edit' ? 'Edit Medicine' :
                                        modalMode === 'sell' ? 'Sell Medicine' :
                                            modalMode === 'sales' ? 'Sales Dashboard' :
                                                modalMode === 'suppliers' ? 'Supplier Management' :
                                                    'Medicine Billing'}
                            </h2>
                            <button className="close-btn" onClick={closeModal}>&times;</button>
                        </div>

                        {modalMode === 'sales' ? (
                            <div style={{ minWidth: '400px' }}>
                                <div style={{
                                    background: 'linear-gradient(135deg, #dcfce7 0%, #f0fdf4 100%)',
                                    padding: '2rem',
                                    borderRadius: '1rem',
                                    marginBottom: '2rem',
                                    textAlign: 'center',
                                    border: '1px solid #86efac',
                                    boxShadow: '0 4px 6px -1px rgba(0, 0, 0, 0.05)'
                                }}>
                                    <h3 style={{ margin: 0, color: '#166534', fontSize: '1.1rem', textTransform: 'uppercase', letterSpacing: '0.05em' }}>Total Revenue</h3>
                                    <div style={{ fontSize: '3rem', fontWeight: '800', color: '#15803d', margin: '0.5rem 0 0 0' }}>
                                        ₹{salesStats.totalRevenue.toFixed(2)}
                                    </div>
                                </div>

                                <h4>Sales by Medicine</h4>
                                <table className="table" style={{ fontSize: '0.9rem' }}>
                                    <thead>
                                        <tr>
                                            <th>Medicine</th>
                                            <th>Qty Sold</th>
                                            <th>Revenue</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        {salesStats.byMedicine.length > 0 ? (
                                            salesStats.byMedicine.map((stat, idx) => (
                                                <tr key={idx}>
                                                    <td>{stat.name}</td>
                                                    <td>{stat.totalQty}</td>
                                                    <td>₹{stat.totalRev.toFixed(2)}</td>
                                                </tr>
                                            ))
                                        ) : (
                                            <tr><td colSpan="3" style={{ textAlign: 'center' }}>No sales yet</td></tr>
                                        )}
                                    </tbody>
                                </table>
                            </div>
                        ) : modalMode === 'suppliers' ? (
                            <div style={{ marginTop: '1rem' }}>
                                {/* Add Supplier Form */}
                                <form onSubmit={handleSupplierSubmit} style={{ background: 'rgba(251, 146, 60, 0.05)', padding: '1.5rem', borderRadius: '1rem', marginBottom: '2rem', border: '1px solid rgba(251, 146, 60, 0.2)' }}>
                                    <h3 style={{ marginTop: 0, marginBottom: '1.5rem', color: '#9a3412', fontSize: '1.25rem', fontWeight: '800' }}>Add New Supplier</h3>
                                    <div style={{ display: 'grid', gridTemplateColumns: 'repeat(2, 1fr)', gap: '1.25rem' }}>
                                        <div className="input-group" style={{ marginBottom: 0 }}>
                                            <input
                                                placeholder="Supplier Name"
                                                value={supplierForm.name}
                                                onChange={e => setSupplierForm({ ...supplierForm, name: e.target.value })}
                                                required
                                            />
                                        </div>
                                        <div className="input-group" style={{ marginBottom: 0 }}>
                                            <input
                                                placeholder="Contact Number"
                                                value={supplierForm.contactNumber}
                                                onChange={e => setSupplierForm({ ...supplierForm, contactNumber: e.target.value })}
                                                required
                                            />
                                        </div>
                                        <div className="input-group" style={{ marginBottom: 0 }}>
                                            <input
                                                placeholder="Email"
                                                type="email"
                                                value={supplierForm.email}
                                                onChange={e => setSupplierForm({ ...supplierForm, email: e.target.value })}
                                            />
                                        </div>
                                        <div className="input-group" style={{ marginBottom: 0 }}>
                                            <input
                                                placeholder="Address"
                                                value={supplierForm.address}
                                                onChange={e => setSupplierForm({ ...supplierForm, address: e.target.value })}
                                            />
                                        </div>
                                    </div>
                                    <button type="submit" className="btn btn-suppliers" style={{ marginTop: '1.5rem', width: '100%', justifyContent: 'center' }}>
                                        + Add Supplier
                                    </button>
                                </form>

                                <div style={{ marginTop: '2.5rem', marginBottom: '1rem', borderTop: '1px solid rgba(154, 52, 18, 0.1)', paddingTop: '1.5rem' }}>
                                    <h3 style={{ color: '#9a3412', fontSize: '1.1rem', fontWeight: '700', marginBottom: '1rem' }}>Existing Suppliers</h3>
                                </div>

                                {/* Suppliers List */}
                                <div style={{ overflowX: 'auto' }}>
                                    <table className="table">
                                        <thead>
                                            <tr>
                                                <th>Name</th>
                                                <th>Contact</th>
                                                <th>Email</th>
                                                <th>Address</th>
                                                <th>Action</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            {suppliers.length > 0 ? (
                                                suppliers.map(sup => (
                                                    <tr key={sup.id}>
                                                        <td><strong>{sup.name}</strong></td>
                                                        <td>{sup.contactNumber}</td>
                                                        <td>{sup.email}</td>
                                                        <td>{sup.address}</td>
                                                        <td>
                                                            <button
                                                                onClick={() => handleDeleteSupplier(sup.id)}
                                                                style={{
                                                                    background: '#fee2e2',
                                                                    color: '#ef4444',
                                                                    border: 'none',
                                                                    padding: '0.4rem 0.8rem',
                                                                    borderRadius: '0.3rem',
                                                                    cursor: 'pointer',
                                                                    fontSize: '0.85rem'
                                                                }}
                                                            >
                                                                Delete
                                                            </button>
                                                        </td>
                                                    </tr>
                                                ))
                                            ) : (
                                                <tr>
                                                    <td colSpan="5" style={{ textAlign: 'center', padding: '2rem', color: '#64748b' }}>
                                                        No suppliers found. Add one above!
                                                    </td>
                                                </tr>
                                            )}
                                        </tbody>
                                    </table>
                                </div>
                            </div>
                        ) : (
                            <form onSubmit={handleSubmit}>
                                {modalMode === 'sell' ? (
                                    <div className="input-group">
                                        <label>Quantity to Sell (Max: {currentMedicine.quantity})</label>
                                        <input
                                            type="number"
                                            min="1"
                                            max={currentMedicine.quantity}
                                            value={sellQuantity}
                                            onChange={(e) => setSellQuantity(e.target.value)}
                                            required
                                        />
                                    </div>
                                ) : modalMode === 'billing' ? (
                                    <>
                                        <div className="input-group">
                                            <label>Medicine Name</label>
                                            <select
                                                value={billingData.medicineName}
                                                onChange={(e) => setBillingData({ ...billingData, medicineName: e.target.value })}
                                                required
                                                style={{ width: '100%', padding: '0.75rem', borderRadius: '0.5rem', border: '1px solid var(--border)' }}
                                            >
                                                <option value="">-- Select Medicine --</option>
                                                {medicines.filter(m => !isExpired(m)).map(m => (
                                                    <option key={m.id} value={m.name}>{m.name} (Stock: {m.quantity})</option>
                                                ))}
                                            </select>
                                        </div>
                                        <div className="input-group">
                                            <label>Quantity {billingData.medicineName && `(Available: ${availableStock})`}</label>
                                            <input
                                                type="number"
                                                min="1"
                                                max={availableStock}
                                                value={billingData.quantity}
                                                onChange={(e) => setBillingData({ ...billingData, quantity: parseInt(e.target.value) })}
                                                required
                                            />
                                        </div>
                                        <div className="input-group">
                                            <label>Customer Email</label>
                                            <input
                                                type="email"
                                                value={billingData.userEmail}
                                                onChange={(e) => setBillingData({ ...billingData, userEmail: e.target.value })}
                                                required
                                                placeholder="customer@example.com"
                                            />
                                        </div>
                                    </>
                                ) : (
                                    <>
                                        <div className="input-group">
                                            <label>Name</label>
                                            <input name="name" value={formData.name} onChange={handleInputChange} required />
                                        </div>
                                        <div className="input-group">
                                            <label>Description</label>
                                            <input name="description" value={formData.description} onChange={handleInputChange} />
                                        </div>
                                        <div className="input-group">
                                            <label>Price</label>
                                            <input name="price" type="number" step="0.01" value={formData.price} onChange={handleInputChange} required />
                                        </div>
                                        <div className="input-group">
                                            <label>Stock Quantity</label>
                                            <input name="quantity" type="number" value={formData.quantity} onChange={handleInputChange} required />
                                        </div>
                                        <div className="input-group">
                                            <label>Expiry Date</label>
                                            <input name="expiryDate" type="date" value={formData.expiryDate} onChange={handleInputChange} required />
                                        </div>
                                    </>
                                )}

                                <button type="submit" className={`btn ${modalMode === 'billing' ? 'btn-billing' :
                                    modalMode === 'sell' ? 'btn-sales' :
                                        'btn-add'
                                    }`} style={{ width: '100%', marginTop: '1.5rem', justifyContent: 'center' }}>
                                    {modalMode === 'sell' ? 'Confirm Sale' : modalMode === 'billing' ? 'Generate Bill' : 'Save Medicine'}
                                </button>
                            </form>
                        )}
                    </div>
                </div>
            )}
        </div>
    );
}

export default Dashboard;
