<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Supplier List – Inventory System</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css">
    <style>
        body { background-color: #f4f6f9; }
        .navbar { background-color: #1a3c5e; }
        .page-header { background: linear-gradient(135deg, #1a3c5e, #2e6da4); color: white; padding: 20px 30px; border-radius: 10px; margin-bottom: 25px; }
        .table-card { background: white; border-radius: 10px; box-shadow: 0 2px 10px rgba(0,0,0,0.08); overflow: hidden; }
        .table thead { background-color: #1a3c5e; color: white; }
        .badge-local { background-color: #28a745; }
        .badge-foreign { background-color: #fd7e14; }
        .badge-active { background-color: #17a2b8; }
        .badge-inactive { background-color: #6c757d; }
        .action-btn { padding: 4px 10px; font-size: 0.82rem; }
        .search-box { max-width: 380px; }
    </style>
</head>
<body>

<!-- Navbar -->
<nav class="navbar navbar-dark px-4 py-3 mb-4">
    <span class="navbar-brand fw-bold fs-5">
        <i class="bi bi-boxes me-2"></i>Inventory &amp; Stock Management
    </span>
    <span class="text-white-50 small">Component 04 – Supplier Management</span>
</nav>

<div class="container-fluid px-4">

    <!-- Page Header -->
    <div class="page-header d-flex justify-content-between align-items-center">
        <div>
            <h4 class="mb-1"><i class="bi bi-truck me-2"></i>Supplier List</h4>
            <small class="opacity-75">Manage all suppliers in the system</small>
        </div>
        <a href="/suppliers/register" class="btn btn-light fw-semibold">
            <i class="bi bi-plus-circle me-1"></i> Add New Supplier
        </a>
    </div>

    <!-- Flash Messages -->
    <c:if test="${not empty successMessage}">
        <div class="alert alert-success alert-dismissible fade show" role="alert">
            <i class="bi bi-check-circle me-2"></i>${successMessage}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    </c:if>
    <c:if test="${not empty errorMessage}">
        <div class="alert alert-danger alert-dismissible fade show" role="alert">
            <i class="bi bi-exclamation-circle me-2"></i>${errorMessage}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    </c:if>

    <!-- Search Bar -->
    <div class="mb-3">
        <form method="get" action="/suppliers/list" class="d-flex gap-2 search-box">
            <input type="text" name="keyword" class="form-control" placeholder="Search by name or category..."
                   value="${keyword}">
            <button type="submit" class="btn btn-primary"><i class="bi bi-search"></i></button>
            <c:if test="${not empty keyword}">
                <a href="/suppliers/list" class="btn btn-outline-secondary">Clear</a>
            </c:if>
        </form>
    </div>

    <!-- Table -->
    <div class="table-card">
        <table class="table table-hover mb-0">
            <thead>
                <tr>
                    <th>Supplier ID</th>
                    <th>Name</th>
                    <th>Email</th>
                    <th>Phone</th>
                    <th>Category</th>
                    <th>Type</th>
                    <th>Lead Time</th>
                    <th>Status</th>
                    <th>Actions</th>
                </tr>
            </thead>
            <tbody>
                <c:choose>
                    <c:when test="${empty suppliers}">
                        <tr>
                            <td colspan="9" class="text-center py-4 text-muted">
                                <i class="bi bi-inbox fs-4 d-block mb-2"></i>
                                No suppliers found.
                                <a href="/suppliers/register">Add the first one</a>.
                            </td>
                        </tr>
                    </c:when>
                    <c:otherwise>
                        <c:forEach var="s" items="${suppliers}">
                            <tr>
                                <td><code>${s.supplierId}</code></td>
                                <td class="fw-semibold">${s.name}</td>
                                <td>${s.email}</td>
                                <td>${s.phone}</td>
                                <td>${s.productCategory}</td>
                                <td>
                                    <c:choose>
                                        <c:when test="${s.supplierType == 'Local'}">
                                            <span class="badge badge-local"><i class="bi bi-geo-alt-fill me-1"></i>Local</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="badge badge-foreign"><i class="bi bi-globe me-1"></i>Foreign</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td>
                                    <span class="text-muted">${s.calculateLeadTimeDays()} days</span>
                                </td>
                                <td>
                                    <c:choose>
                                        <c:when test="${s.status == 'active'}">
                                            <span class="badge badge-active">Active</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="badge badge-inactive">Inactive</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td>
                                    <a href="/suppliers/edit?id=${s.supplierId}" class="btn btn-sm btn-outline-primary action-btn me-1">
                                        <i class="bi bi-pencil"></i> Edit
                                    </a>
                                    <form method="post" action="/suppliers/delete" class="d-inline"
                                          onsubmit="return confirm('Are you sure you want to delete ${s.name}?');">
                                        <input type="hidden" name="id" value="${s.supplierId}">
                                        <button type="submit" class="btn btn-sm btn-outline-danger action-btn">
                                            <i class="bi bi-trash"></i> Delete
                                        </button>
                                    </form>
                                </td>
                            </tr>
                        </c:forEach>
                    </c:otherwise>
                </c:choose>
            </tbody>
        </table>
    </div>

    <div class="mt-3 text-muted small">
        Total suppliers: <strong>${suppliers.size()}</strong>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
