<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Edit Supplier – Inventory System</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css">
    <style>
        body { background-color: #f4f6f9; }
        .navbar { background-color: #1a3c5e; }
        .form-card { background: white; border-radius: 12px; box-shadow: 0 2px 12px rgba(0,0,0,0.08); padding: 35px; max-width: 700px; margin: 0 auto; }
        .form-card h4 { color: #1a3c5e; border-bottom: 2px solid #e0e7ef; padding-bottom: 12px; margin-bottom: 25px; }
        .btn-primary { background-color: #1a3c5e; border-color: #1a3c5e; }
        .supplier-id-badge { background: #e0e7ef; color: #1a3c5e; font-weight: 600; padding: 6px 14px; border-radius: 6px; font-size: 0.9rem; }
    </style>
</head>
<body>

<nav class="navbar navbar-dark px-4 py-3 mb-4">
    <span class="navbar-brand fw-bold fs-5">
        <i class="bi bi-boxes me-2"></i>Inventory &amp; Stock Management
    </span>
</nav>

<div class="container py-2">

    <nav aria-label="breadcrumb" class="mb-4">
        <ol class="breadcrumb">
            <li class="breadcrumb-item"><a href="/suppliers/list">Suppliers</a></li>
            <li class="breadcrumb-item active">Edit Supplier</li>
        </ol>
    </nav>

    <div class="form-card">
        <div class="d-flex justify-content-between align-items-center mb-4" style="border-bottom: 2px solid #e0e7ef; padding-bottom: 12px;">
            <h4 class="mb-0"><i class="bi bi-pencil-square me-2"></i>Edit Supplier Details</h4>
            <span class="supplier-id-badge">${supplier.supplierId}</span>
        </div>

        <form method="post" action="/suppliers/edit">
            <input type="hidden" name="supplierId" value="${supplier.supplierId}">

            <!-- Supplier Type -->
            <div class="mb-4">
                <label class="form-label fw-semibold">Supplier Type <span class="text-danger">*</span></label>
                <div class="d-flex gap-4">
                    <div class="form-check">
                        <input class="form-check-input" type="radio" name="type" id="typeLocal" value="Local"
                            ${supplier.supplierType == 'Local' ? 'checked' : ''} onchange="updateExtraField()">
                        <label class="form-check-label" for="typeLocal">
                            <i class="bi bi-geo-alt-fill text-success me-1"></i> Local Supplier
                        </label>
                    </div>
                    <div class="form-check">
                        <input class="form-check-input" type="radio" name="type" id="typeForeign" value="Foreign"
                            ${supplier.supplierType == 'Foreign' ? 'checked' : ''} onchange="updateExtraField()">
                        <label class="form-check-label" for="typeForeign">
                            <i class="bi bi-globe text-warning me-1"></i> Foreign Supplier
                        </label>
                    </div>
                </div>
            </div>

            <div class="row g-3">
                <!-- Name -->
                <div class="col-md-6">
                    <label for="name" class="form-label fw-semibold">Supplier Name <span class="text-danger">*</span></label>
                    <input type="text" id="name" name="name" class="form-control" value="${supplier.name}" required>
                </div>

                <!-- Email -->
                <div class="col-md-6">
                    <label for="email" class="form-label fw-semibold">Email Address <span class="text-danger">*</span></label>
                    <input type="email" id="email" name="email" class="form-control" value="${supplier.email}" required>
                </div>

                <!-- Phone -->
                <div class="col-md-6">
                    <label for="phone" class="form-label fw-semibold">Phone Number <span class="text-danger">*</span></label>
                    <input type="tel" id="phone" name="phone" class="form-control" value="${supplier.phone}" required>
                </div>

                <!-- Product Category -->
                <div class="col-md-6">
                    <label for="productCategory" class="form-label fw-semibold">Product Category <span class="text-danger">*</span></label>
                    <select id="productCategory" name="productCategory" class="form-select" required>
                        <c:forEach var="cat" items="${['Electronics','Food & Beverages','Clothing','Stationery','Hardware','Pharmaceuticals','Other']}">
                            <option value="${cat}" ${supplier.productCategory == cat ? 'selected' : ''}>${cat}</option>
                        </c:forEach>
                    </select>
                </div>

                <!-- Address -->
                <div class="col-12">
                    <label for="address" class="form-label fw-semibold">Address <span class="text-danger">*</span></label>
                    <textarea id="address" name="address" class="form-control" rows="2" required>${supplier.address}</textarea>
                </div>

                <!-- Dynamic extra field -->
                <div class="col-md-6">
                    <label for="extraField" class="form-label fw-semibold" id="extraFieldLabel">
                        ${supplier.supplierType == 'Local' ? 'District' : 'Country'}
                    </label>
                    <input type="text" id="extraField" name="extraField" class="form-control"
                           value="${supplier.supplierType == 'Local' ? supplier.district : supplier.country}">
                </div>

                <!-- Status -->
                <div class="col-md-6">
                    <label for="status" class="form-label fw-semibold">Status</label>
                    <select id="status" name="status" class="form-select">
                        <option value="active" ${supplier.status == 'active' ? 'selected' : ''}>Active</option>
                        <option value="inactive" ${supplier.status == 'inactive' ? 'selected' : ''}>Inactive</option>
                    </select>
                </div>
            </div>

            <!-- Lead Time Info -->
            <div class="alert alert-info mt-4 py-2">
                <i class="bi bi-clock me-2"></i>
                Estimated lead time for this supplier: <strong>${supplier.calculateLeadTimeDays()} days</strong>
            </div>

            <div class="d-flex gap-2 mt-3">
                <button type="submit" class="btn btn-primary px-4">
                    <i class="bi bi-save me-1"></i> Save Changes
                </button>
                <a href="/suppliers/list" class="btn btn-outline-secondary px-4">Cancel</a>
            </div>
        </form>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<script>
    function updateExtraField() {
        const isLocal = document.getElementById('typeLocal').checked;
        document.getElementById('extraFieldLabel').textContent = isLocal ? 'District' : 'Country';
        document.getElementById('extraField').placeholder = isLocal ? 'e.g. Colombo' : 'e.g. India';
    }
</script>
</body>
</html>
