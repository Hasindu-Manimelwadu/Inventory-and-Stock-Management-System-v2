package com.category;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AppServer {
    private static CategoryRepository repository = new CategoryRepository();


    private static boolean isLoggedIn = false;

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        server.createContext("/", new UIHandler());
        server.createContext("/login", new LoginPageHandler());
        server.createContext("/auth", new AuthHandler());
        server.createContext("/logout", new LogoutHandler());
        server.createContext("/add", new AddCategoryHandler());
        server.createContext("/update", new UpdateCategoryHandler());
        server.createContext("/delete", new DeleteCategoryHandler());

        server.setExecutor(null);
        System.out.println("🚀 Secured Dark CRUD Server Started: http://localhost:8080/");
        server.start();
    }


    private static Map<String, String> parseFormData(String formData) throws UnsupportedEncodingException {
        Map<String, String> map = new HashMap<>();
        String[] pairs = formData.split("&");
        for (String pair : pairs) {
            String[] kv = pair.split("=");
            if(kv.length > 1) {
                map.put(kv[0], URLDecoder.decode(kv[1], StandardCharsets.UTF_8.toString()));
            } else {
                map.put(kv[0], "");
            }
        }
        return map;
    }

    private static boolean checkAuthAndRedirect(HttpExchange exchange) throws IOException {
        if (!isLoggedIn) {
            exchange.getResponseHeaders().set("Location", "/login");
            exchange.sendResponseHeaders(303, -1);
            return false;
        }
        return true;
    }

    // =========================================================================

    // =========================================================================
    static class LoginPageHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (isLoggedIn) {
                exchange.getResponseHeaders().set("Location", "/");
                exchange.sendResponseHeaders(303, -1);
                return;
            }


            String query = exchange.getRequestURI().getQuery();
            boolean hasError = query != null && query.contains("error=true");

            String html = "<!DOCTYPE html>" +
                    "<html lang='en'>" +
                    "<head>" +
                    "    <meta charset='UTF-8'>" +
                    "    <title>Login | StockFlow</title>" +
                    "    <link href='https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css' rel='stylesheet'>" +
                    "    <style>" +
                    "        body {" +
                    "            background-color: #0f1117;" +
                    "            color: #f5f6fa;" +
                    "            font-family: Arial, sans-serif;" +
                    "        }" +
                    "        .login-wrapper {" +
                    "            min-height: 100vh;" +
                    "            display: flex;" +
                    "            align-items: center;" +
                    "            justify-content: center;" +
                    "        }" +
                    "        .login-card {" +
                    "            width: 430px;" +
                    "            background-color: #1b1f2b;" +
                    "            border: 1px solid #262c3f;" +
                    "            border-radius: 18px;" +
                    "            padding: 35px;" +
                    "            box-shadow: 0 0 30px rgba(124,108,255,0.15);" +
                    "        }" +
                    "        .login-title {" +
                    "            font-size: 28px;" +
                    "            font-weight: 800;" +
                    "            text-align: center;" +
                    "        }" +
                    "        .login-subtitle {" +
                    "            text-align: center;" +
                    "            color: #8b91a7;" +
                    "            font-size: 13px;" +
                    "            margin-bottom: 30px;" +
                    "        }" +
                    "        .form-label {" +
                    "            color: #c9cde0;" +
                    "            font-weight: 700;" +
                    "            font-size: 13px;" +
                    "        }" +
                    "        .dark-input {" +
                    "            background-color: #1b1f2b;" +
                    "            border: 1px solid #2f3650;" +
                    "            color: white;" +
                    "            border-radius: 10px;" +
                    "            padding: 12px;" +
                    "        }" +
                    "        .dark-input::placeholder {" +
                    "            color: #777f99;" +
                    "        }" +
                    "        .dark-input:focus {" +
                    "            background-color: #1b1f2b;" +
                    "            color: white;" +
                    "            border-color: #7c6cff;" +
                    "            box-shadow: 0 0 0 0.15rem rgba(124, 108, 255, 0.25);" +
                    "        }" +
                    "        .btn-purple {" +
                    "            background: #7c6cff;" +
                    "            color: white;" +
                    "            border: none;" +
                    "            border-radius: 12px;" +
                    "            padding: 11px 22px;" +
                    "            box-shadow: 0 0 18px rgba(124, 108, 255, 0.4);" +
                    "            font-weight: bold;" +
                    "            letter-spacing: 1px;" +
                    "            transition: all 0.2s;" +
                    "        }" +
                    "        .btn-purple:hover {" +
                    "            background: #6a5af9;" +
                    "            color: white;" +
                    "        }" +
                    "        .stockflow-error {" +
                    "            background-color: rgba(255, 77, 109, 0.15);" +
                    "            border: 1px solid #ff4d6d;" +
                    "            color: #ff4d6d;" +
                    "            border-radius: 10px;" +
                    "            padding: 12px;" +
                    "            font-size: 13px;" +
                    "            margin-bottom: 20px;" +
                    "            text-align: center;" +
                    "            font-weight: 500;" +
                    "        }" +
                    "    </style>" +
                    "</head>" +
                    "<body>" +
                    "" +
                    "<div class='login-wrapper'>" +
                    "    <div class='login-card'>" +
                    "        <div class='login-title'>📦 StockFlow</div>" +
                    "        <div class='login-subtitle'>Inventory System Login</div>" +
                    "" +

                    (hasError ? "<div class='stockflow-error'>❌ Invalid Username or Password!</div>" : "") +
                    "" +
                    "        <form action='/auth' method='post'>" +
                    "            <div class='mb-3'>" +
                    "                <label class='form-label'>Username</label>" +
                    "                <input type='text' name='username' class='form-control dark-input' placeholder='admin' required>" +
                    "            </div>" +
                    "            " +
                    "            <div class='mb-4'>" +
                    "                <label class='form-label'>Password</label>" +
                    "                <input type='password' name='password' class='form-control dark-input' placeholder='123' required>" +
                    "            </div>" +
                    "            " +
                    "            <button type='submit' class='btn btn-purple w-100'>LOGIN</button>" +
                    "        </form>" +
                    "    </div>" +
                    "</div>" +
                    "" +
                    "</body>" +
                    "</html>";

            byte[] response = html.getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(200, response.length);
            OutputStream os = exchange.getResponseBody();
            os.write(response);
            os.close();
        }
    }

    // =========================================================================
    // 2. AUTHENTICATION HANDLER
    // =========================================================================
    static class AuthHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                BufferedReader br = new BufferedReader(new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8));
                Map<String, String> data = parseFormData(br.readLine());

                String user = data.get("username");
                String pass = data.get("password");


                if ("admin".equals(user) && "123".equals(pass)) {
                    isLoggedIn = true;
                    exchange.getResponseHeaders().set("Location", "/");
                } else {
                    exchange.getResponseHeaders().set("Location", "/login?error=true");
                }
                exchange.sendResponseHeaders(303, -1);
            }
        }
    }

    // =========================================================================
    // 3. LOGOUT HANDLER
    // =========================================================================
    static class LogoutHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            isLoggedIn = false;
            exchange.getResponseHeaders().set("Location", "/login");
            exchange.sendResponseHeaders(303, -1);
        }
    }

    // =========================================================================
    // 4. MAIN DASHBOARD HANDLER
    // =========================================================================
    static class UIHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!checkAuthAndRedirect(exchange)) return;

            List<Category> list = repository.findAll();
            StringBuilder tableRows = new StringBuilder();
            for (Category c : list) {

                tableRows.append(String.format(
                        "<tr style='background-color: #1e293b !important; border-bottom: 1px solid #334155;'>" +
                                "  <td style='background-color: transparent !important; padding: 18px 14px;'><span class='badge bg-dark text-info border border-info p-2 fw-bold shadow-sm' style='min-width: 70px;'>%s</span></td>" +
                                "  <td style='background-color: transparent !important; padding: 18px 14px;'><div class='fw-bold text-white'>%s</div></td>" +
                                "  <td style='background-color: transparent !important; padding: 18px 14px;'><span class='text-secondary'>%s</span></td>" +
                                "  <td style='background-color: transparent !important; padding: 18px 14px;' class='text-center'>" +
                                "    <a href='/delete?id=%s' class='btn btn-outline-danger btn-sm rounded-pill px-3 shadow-sm' " +
                                "       onclick=\"return confirm('Are you sure you want to delete this category?')\">" +
                                "       <i class='fas fa-trash-alt me-1'></i> Delete" +
                                "    </a>" +
                                "  </td>" +
                                "</tr>",
                        c.getId(), c.getName(), c.getDescription(), c.getId()
                ));
            }

            String html = "<html><head><title>Category Control Panel</title>" +
                    "<link href='https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css' rel='stylesheet'>" +
                    "<link href='https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css' rel='stylesheet'>" +
                    "<link href='https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;500;600&display=swap' rel='stylesheet'>" +
                    "<style>" +
                    "  body { font-family: 'Poppins', sans-serif; background-color: #0f172a; color: #e2e8f0; }" +
                    "  .gradient-header { background: linear-gradient(135deg, #059669 0%, #047857 100%); color: white; }" +
                    "  .gradient-update { background: linear-gradient(135deg, #d97706 0%, #b45309 100%); color: white; }" +
                    "  .card { background-color: #1e293b; border: 1px solid #334155; border-radius: 15px; transition: transform 0.2s; }" +
                    "  .card:hover { transform: translateY(-3px); box-shadow: 0 10px 20px rgba(0,0,0,0.3); }" +
                    "  .table-container { border-radius: 15px; overflow: hidden; background: #1e293b; border: 1px solid #334155; }" +
                    "  .form-control { background-color: #0f172a; border: 1px solid #475569; color: white; border-radius: 8px; padding: 10px; }" +
                    "  .form-control:focus { background-color: #0f172a; color: white; box-shadow: 0 0 0 0.25rem rgba(5, 150, 105, 0.25); border-color: #059669; }" +
                    "  .btn-custom { border-radius: 8px; padding: 10px; font-weight: 500; }" +
                    "  .table th { background-color: #1e1b4b !important; color: #38bdf8 !important; border-bottom: 2px solid #334155; padding: 15px !important; }" +
                    "  .text-muted-custom { color: #94a3b8; }" +
                    "  .table-dark-custom td { color: #f5f6fa !important; }" +
                    "  .table-dark-custom tr:hover { background-color: #24334d !important; }" +
                    "</style></head><body>" +

                    "<div class='container py-5'>" +

                    // Dashboard Top bar
                    "<div class='d-flex justify-content-between align-items-center mb-5'>" +
                    "  <div class='text-start'>" +
                    "    <h1 class='fw-bold text-white mb-1'><i class='fas fa-layer-group text-info me-2'></i>Category Dashboard</h1>" +
                    "    <p class='text-muted-custom mb-0'>Secure dark-mode system powered by Java & Notepad DB</p>" +
                    "  </div>" +
                    "  <div>" +
                    "    <a href='/logout' class='btn btn-danger btn-custom shadow-sm px-4'><i class='fas fa-sign-out-alt me-2'></i>Logout</a>" +
                    "  </div>" +
                    "</div>" +

                    "<div class='row g-4'>" +
                    // ADD FORM CARD
                    "<div class='col-lg-6'><div class='card shadow-lg h-100'><div class='card-header gradient-header py-3'><h5 class='mb-0 fw-semibold'><i class='fas fa-plus-circle me-2'></i>Create Category</h5></div>" +
                    "<div class='card-body p-4'><form action='/add' method='POST'>" +
                    "<div class='mb-3'><label class='form-label text-muted-custom'>Category ID</label><input type='text' name='id' class='form-control' placeholder='e.g. CAT-001' required></div>" +
                    "<div class='mb-3'><label class='form-label text-muted-custom'>Category Name</label><input type='text' name='name' class='form-control' placeholder='e.g. Electronics' required></div>" +
                    "<div class='mb-3'><label class='form-label text-muted-custom'>Description</label><textarea name='desc' class='form-control' rows='3' placeholder='Details...' required></textarea></div>" +
                    "<button type='submit' class='btn btn-success btn-custom w-100 mt-2'><i class='fas fa-save me-2'></i>Save Category</button></form></div></div></div>" +

                    // UPDATE FORM CARD
                    "<div class='col-lg-6'><div class='card shadow-lg h-100'><div class='card-header gradient-update py-3'><h5 class='mb-0 fw-semibold'><i class='fas fa-edit me-2'></i>Update Category</h5></div>" +
                    "<div class='card-body p-4'><form action='/update' method='POST'>" +
                    "<div class='mb-3'><label class='form-label text-muted-custom'>Target Category ID</label><input type='text' name='id' class='form-control' placeholder='Enter ID to update' required></div>" +
                    "<div class='mb-3'><label class='form-label text-muted-custom'>New Category Name</label><input type='text' name='name' class='form-control' placeholder='New Name' required></div>" +
                    "<div class='mb-3'><label class='form-label text-muted-custom'>New Description</label><textarea name='desc' class='form-control' rows='3' placeholder='Updated details...' required></textarea></div>" +
                    "<button type='submit' class='btn btn-warning text-white btn-custom w-100 mt-2'><i class='fas fa-sync-alt me-2'></i>Update Category</button></form></div></div></div>" +
                    "</div>" +

                    // DATA TABLE VIEW (Fixed Bootstrap conflict)
                    "<div class='mt-5'><h4 class='mb-3 text-white fw-bold'><i class='fas fa-list me-2 text-info'></i>Available Categories</h4>" +
                    "<div class='table-container shadow-lg'>" +
                    "  <table class='table-dark-custom w-100 mb-0' style='background-color: #1e293b; border-collapse: collapse;'>" +
                    "    <thead>" +
                    "      <tr>" +
                    "        <th style='padding: 15px;'>ID</th>" +
                    "        <th style='padding: 15px;'>Name</th>" +
                    "        <th style='padding: 15px;'>Description</th>" +
                    "        <th style='padding: 15px;' class='text-center'>Actions</th>" +
                    "      </tr>" +
                    "    </thead>" +
                    "    <tbody>" +
                    (tableRows.length() == 0 ? "<tr><td colspan='4' class='text-center py-4 text-muted' style='background-color: #1e293b !important;'>No categories found.</td></tr>" : tableRows.toString()) +
                    "    </tbody>" +
                    "  </table>" +
                    "</div>" +
                    "</div>" +

                    "</div></body></html>";

            byte[] response = html.getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(200, response.length);
            OutputStream os = exchange.getResponseBody();
            os.write(response);
            os.close();
        }
    }

    // CREATE Handler (Secured)
    static class AddCategoryHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!isLoggedIn) { exchange.sendResponseHeaders(403, -1); return; }
            if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                BufferedReader br = new BufferedReader(new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8));
                Map<String, String> data = parseFormData(br.readLine());
                repository.save(new Category(data.get("id"), data.get("name"), data.get("desc")));
                exchange.getResponseHeaders().set("Location", "/");
                exchange.sendResponseHeaders(303, -1);
            }
        }
    }

    // UPDATE Handler (Secured)
    static class UpdateCategoryHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!isLoggedIn) { exchange.sendResponseHeaders(403, -1); return; }
            if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                BufferedReader br = new BufferedReader(new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8));
                Map<String, String> data = parseFormData(br.readLine());
                repository.update(new Category(data.get("id"), data.get("name"), data.get("desc")));
                exchange.getResponseHeaders().set("Location", "/");
                exchange.sendResponseHeaders(303, -1);
            }
        }
    }

    // DELETE Handler (Secured)
    static class DeleteCategoryHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!isLoggedIn) { exchange.sendResponseHeaders(403, -1); return; }
            String query = exchange.getRequestURI().getQuery();
            if (query != null && query.startsWith("id=")) {
                String idToDelete = URLDecoder.decode(query.split("=")[1], StandardCharsets.UTF_8.toString());
                repository.delete(idToDelete);
            }
            exchange.getResponseHeaders().set("Location", "/");
            exchange.sendResponseHeaders(303, -1);
        }
    }
}