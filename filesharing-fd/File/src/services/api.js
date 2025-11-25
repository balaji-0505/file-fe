// ============================
// DIRECT BACKEND URL
// ============================
const BASE = "http://localhost:30082/api";  
// FULL URL: http://localhost:30082/api/auth/login
//           http://localhost:30082/api/auth/register

const getToken = () => localStorage.getItem("authToken");

// ============================
// AUTH API
// ============================
export const authApi = {
  async register(name, email, password) {
    const res = await fetch(`${BASE}/auth/register`, {
      method: "POST",
      headers: {
        "Content-Type": "application/x-www-form-urlencoded",
      },
      body: new URLSearchParams({ name, email, password }),
    });

    const data = await res.json().catch(() => ({}));
    if (!res.ok) throw new Error(data.error || "Register failed");
    return data;
  },

  async login(email, password) {
    const res = await fetch(`${BASE}/auth/login`, {
      method: "POST",
      headers: {
        "Content-Type": "application/x-www-form-urlencoded",
      },
      body: new URLSearchParams({ email, password }),
    });

    const data = await res.json().catch(() => ({}));
    if (!res.ok) throw new Error(data.error || "Login failed");
    return data;
  },
};

// ============================
// FILES API
// ============================
export const filesApi = {
  async list() {
    const res = await fetch(`${BASE}/files`, {
      headers: { Authorization: `Bearer ${getToken()}` },
    });
    if (!res.ok) throw new Error("Failed to list files");
    return res.json();
  },

  async upload(file, folderId) {
    const form = new FormData();
    form.append("file", file);
    if (folderId) form.append("folderId", folderId);

    const res = await fetch(`${BASE}/files`, {
      method: "POST",
      body: form,
      headers: { Authorization: `Bearer ${getToken()}` },
    });

    if (!res.ok) throw new Error("Upload failed");
    return res.json();
  },

  downloadUrl(id) {
    return `${BASE}/files/${id}/download`;
  },
};

// ============================
// FOLDERS API
// ============================
export const foldersApi = {
  async list() {
    const res = await fetch(`${BASE}/folders`, {
      headers: { Authorization: `Bearer ${getToken()}` },
    });
    if (!res.ok) throw new Error("Failed to list folders");
    return res.json();
  },

  async create(name, parentId) {
    const params = new URLSearchParams({ name });
    if (parentId) params.append("parentId", parentId);

    const res = await fetch(`${BASE}/folders?${params}`, {
      method: "POST",
      headers: { Authorization: `Bearer ${getToken()}` },
    });

    if (!res.ok) throw new Error("Create folder failed");
    return res.json();
  },
};

// ============================
// SHARES API
// ============================
export const sharesApi = {
  async list() {
    const res = await fetch(`${BASE}/shares`, {
      headers: { Authorization: `Bearer ${getToken()}` },
    });
    if (!res.ok) throw new Error("Failed to list shares");
    return res.json();
  },
};

// ============================
// PASSHARE API
// ============================
export const passhareApi = {
  async createSession() {
    const res = await fetch(`${BASE}/passhare/sessions`, {
      method: "POST",
      headers: { Authorization: `Bearer ${getToken()}` },
    });
    if (!res.ok) throw new Error("Failed to create session");
    return res.json();
  },
};
