<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Register Supplier – Inventory System</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css">
    <style>
        body { background-color: #f4f6f9; }
        .navbar { background-color: #1a3c5e; }
        .form-card { background: white; border-radius: 12px; box-shadow: 0 2px 12px rgba(0,0,0,0.08); padding: 35px; max-width: 700px; margin: 0 auto; }
        .form-card h4 { color: #1a3c5e; border-bottom: 2px solid #e0e7ef; padding-bottom: 12px; margin-bottom: 25px; }
        .btn-primary { background-color: #1a3c5e; border-color: #1a3c5e; }
        .btn-primary:hover { background-color: #2e6da4; border-color: #2e6da4; }
        #extraFieldGroup { transition: all 0.3s ease; }
    </style>
</head>
<body>

<nav class="navbar navbar-dark px-4 py-3 mb-4">
    <span class="navbar-brand fw-bold fs-5">
        <i class="bi bi-boxes me-2"></i>Inventory &amp; Stock Management
    </span>
</nav>

<div class="container py-2">

    <!-- Breadcrumb -->
    <nav aria-label="breadcrumb" class="mb-4">
        <ol class="breadcrumb">
            <li class="breadcrumb-item"><a href="/suppliers/list">Suppliers</a></li>
            <li class="breadcrumb-item active">Register New Supplier</li>
        </ol>
    </nav>

    <div class="form-card">
        <h4><i class="bi bi-person-plus me-2"></i>Register New Supplier</h4>

        <form method="post" action="/suppliers/register">

            <!-- Supplier Type -->
            <div class="mb-4">
                <label class="form-label fw-semibold">Supplier Type <span class="text-danger">*</span></label>
                <div class="d-flex gap-4">
                    <div class="form-check">
                        <input class="form-check-input" type="radio" name="type" id="typeLocal" value="Local" checked onchange="updateExtraField()">
                        <label class="form-check-label" for="typeLocal">
                            <i class="bi bi-geo-alt-fill text-success me-1"></i> Local Supplier
                        </label>
                    </div>
                    <div class="form-check">
                        <input class="form-check-input" type="radio" name="type" id="typeForeign" value="Foreign" onchange="updateExtraField()">
                        <label class="form-check-label" for="typeForeign">
                            <i class="bi bi-globe text-warning me-1"></i> Foreign Supplier
                        </label>
                    </div>
                </div>
                <small class="text-muted">Local: ~3 day lead time &nbsp;|&nbsp; Foreign: ~14 day lead time</small>
            </div>

            <div class="row g-3">
                <!-- Name -->
                <div class="col-md-6">
                    <label for="name" class="form-label fw-semibold">Supplier Name <span class="text-danger">*</span></label>
                    <input type="text" id="name" name="name" class="form-control" placeholder="e.g. Perera Traders" required>
                </div>

                <!-- Email -->
                <div class="col-md-6">
                    <label for="email" class="form-label fw-semibold">Email Address <span class="text-danger">*</span></label>
                    <input type="email" id="email" name="email" class="form-control" placeholder="supplier@email.com" required>
                </div>

                <!-- Phone -->
                <div class="col-md-6">
                    <label for="phone" class="form-label fw-semibold">Phone Number <span class="text-danger">*</span></label>
                    <input type="tel" id="phone" name="phone" class="form-control" placeholder="+94 77 123 4567" required>
                </div>

                <!-- Product Category -->
                <div class="col-md-6">
                    <label for="productCategory" class="form-label fw-semibold">Product Category <span class="text-danger">*</span></label>
                    <select id="productCategory" name="productCategory" class="form-select" required>
                        <option value="">-- Select Category --</option>
                        <option value="Electronics">Electronics</option>
                        <option value="Food & Beverages">Food &amp; Beverages</option>
                        <option value="Clothing">Clothing</option>
                        <option value="Stationery">Stationery</option>
                        <option value="Hardware">Hardware</option>
                        <option value="Pharmaceuticals">Pharmaceuticals</option>
                        <option value="Other">Other</option>
                    </select>
                </div>

                <!-- Address -->
                <div class="col-12">
                    <label for="address" class="form-label fw-semibold">Address <span class="text-danger">*</span></label>
                    <textarea id="address" name="address" class="form-control" rows="2" placeholder="Street, City" required></textarea>
                </div>

                <!-- Dynamic extra field -->
                <div class="col-md-6" id="extraFieldGroup">
                    <label for="extraField" class="form-label fw-semibold" id="extraFieldLabel">District</label>
                    <input type="text" id="extraField" name="extraField" class="form-control" id="extraFieldInput" placeholder="e.g. Colombo">
                </div>

                <!-- Status -->
                <div class="col-md-6">
                    <label for="status" class="form-label fw-semibold">Status</label>
                    <select id="status" name="status" class="form-select">
                        <option value="active">Active</option>
                        <option value="inactive">Inactive</option>
                    </select>
                </div>
            </div>

            <div class="d-flex gap-2 mt-4">
                <button type="submit" class="btn btn-primary px-4">
                    <i class="bi bi-save me-1"></i> Register Supplier
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
