// ============================
// BASE URL (frontend uses NGINX → proxy to /api/* )
// ============================
const BASE = '/api';

const getToken = () => localStorage.getItem('authToken');

const jsonHeaders = (token) => ({
  'Content-Type': 'application/json',
  ...(token ? { Authorization: `Bearer ${token}` } : {})
});

// ============================
// AUTH API
// ============================

export const authApi = {
  async login(email, password) {
    const res = await fetch(`${BASE}/auth/login`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8' },
      body: new URLSearchParams({ email, password })
    });

    const data = await res.json().catch(() => ({}));

    if (!res.ok) throw new Error(data.error || 'Login failed');

    return data;
  },

  async register(name, email, password) {
    const res = await fetch(`${BASE}/auth/register`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8' },
      body: new URLSearchParams({ name, email, password })
    });

    const data = await res.json().catch(() => ({}));

    if (!res.ok) throw new Error(data.error || 'Register failed');

    return data;
  }
};

// ============================
// USER API (OPTIONAL)
// ============================

export const userApi = {
  // Now works same as backend
  async register(username, email, password) {
    const res = await fetch(`${BASE}/auth/register`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8' },
      body: new URLSearchParams({ name: username, email, password })
    });

    const data = await res.json().catch(() => ({}));

    if (!res.ok) throw new Error(data.error || 'Registration failed');

    return data;
  }
};

// ============================
// FILES API
// ============================

export const filesApi = {
  async list() {
    const res = await fetch(`${BASE}/files`, {
      headers: { Authorization: `Bearer ${getToken()}` }
    });

    if (!res.ok) throw new Error('Failed to list files');
    return res.json();
  },

  async upload(file, folderId) {
    const form = new FormData();
    form.append('file', file);
    if (folderId) form.append('folderId', String(folderId));

    const res = await fetch(`${BASE}/files`, {
      method: 'POST',
      body: form,
      headers: { Authorization: `Bearer ${getToken()}` }
    });

    if (!res.ok) throw new Error('Upload failed');
    return res.json();
  },

  async update(id, { name, isStarred }) {
    const params = new URLSearchParams();
    if (name) params.append('name', name);
    if (isStarred != null) params.append('starred', String(!!isStarred));

    const res = await fetch(`${BASE}/files/${id}?${params.toString()}`, {
      method: 'PATCH',
      headers: { Authorization: `Bearer ${getToken()}` }
    });

    if (!res.ok) throw new Error('Update failed');
    return res.json();
  },

  async remove(id) {
    const res = await fetch(`${BASE}/files/${id}`, {
      method: 'DELETE',
      headers: { Authorization: `Bearer ${getToken()}` }
    });

    if (!res.ok) throw new Error('Delete failed');
  },

  downloadUrl(id) {
    return `${BASE}/files/${id}/download`;
  }
};

// ============================
// FOLDERS API
// ============================

export const foldersApi = {
  async list() {
    const res = await fetch(`${BASE}/folders`, {
      headers: { Authorization: `Bearer ${getToken()}` }
    });

    if (!res.ok) throw new Error('Failed to list folders');
    return res.json();
  },

  async create(name, parentId) {
    const params = new URLSearchParams({ name });
    if (parentId) params.append('parentId', String(parentId));

    const res = await fetch(`${BASE}/folders?${params.toString()}`, {
      method: 'POST',
      headers: { Authorization: `Bearer ${getToken()}` }
    });

    if (!res.ok) throw new Error('Create folder failed');
    return res.json();
  },

  async rename(id, name) {
    const params = new URLSearchParams({ name });

    const res = await fetch(`${BASE}/folders/${id}?${params.toString()}`, {
      method: 'PATCH',
      headers: { Authorization: `Bearer ${getToken()}` }
    });

    if (!res.ok) throw new Error('Rename failed');
    return res.json();
  },

  async remove(id) {
    const res = await fetch(`${BASE}/folders/${id}`, {
      method: 'DELETE',
      headers: { Authorization: `Bearer ${getToken()}` }
    });

    if (!res.ok) throw new Error('Delete failed');
  }
};

// ============================
// SHARES API
// ============================

export const sharesApi = {
  async list() {
    const res = await fetch(`${BASE}/shares`, {
      headers: { Authorization: `Bearer ${getToken()}` }
    });

    if (!res.ok) throw new Error('Failed to list shares');
    return res.json();
  },

  async create({ fileId, shareType, permissions, expiryEpochMs, password, createdBy }) {
    const params = new URLSearchParams({ fileId: String(fileId), shareType });

    if (permissions?.length) params.append('permissions', permissions.join(','));
    if (expiryEpochMs) params.append('expiryEpochMs', String(expiryEpochMs));
    if (password) params.append('password', password);
    if (createdBy) params.append('createdBy', createdBy);

    const res = await fetch(`${BASE}/shares?${params.toString()}`, {
      method: 'POST',
      headers: { Authorization: `Bearer ${getToken()}` }
    });

    if (!res.ok) throw new Error('Create share failed');
    return res.json();
  },

  async update(id, { shareType, permissions, expiryEpochMs, password }) {
    const params = new URLSearchParams();
    if (shareType) params.append('shareType', shareType);
    if (permissions?.length) params.append('permissions', permissions.join(','));
    if (expiryEpochMs) params.append('expiryEpochMs', String(expiryEpochMs));
    if (password) params.append('password', password);

    const res = await fetch(`${BASE}/shares/${id}?${params.toString()}`, {
      method: 'PATCH',
      headers: { Authorization: `Bearer ${getToken()}` }
    });

    if (!res.ok) throw new Error('Update share failed');
    return res.json();
  },

  async remove(id) {
    const res = await fetch(`${BASE}/shares/${id}`, {
      method: 'DELETE',
      headers: { Authorization: `Bearer ${getToken()}` }
    });

    if (!res.ok) throw new Error('Delete share failed');
  }
};

// ============================
// PASSHARE API
// ============================

export const passhareApi = {
  async createSession() {
    const res = await fetch(`${BASE}/passhare/sessions`, {
      method: 'POST',
      headers: { Authorization: `Bearer ${getToken()}` }
    });

    if (!res.ok) {
      const data = await res.json().catch(() => ({}));
      throw new Error(data.error || 'Failed to create session');
    }

    return res.json();
  },

  async joinSession(code) {
    const params = new URLSearchParams({ code });

    const res = await fetch(`${BASE}/passhare/sessions/join?${params}`, {
      method: 'POST',
      headers: { Authorization: `Bearer ${getToken()}` }
    });

    if (!res.ok) throw new Error('Failed to join session');
    return res.json();
  },

  async getSession(sessionId) {
    const res = await fetch(`${BASE}/passhare/sessions/${sessionId}`, {
      headers: { Authorization: `Bearer ${getToken()}` }
    });

    if (!res.ok) throw new Error('Failed to get session');
    return res.json();
  },

  async shareFile(sessionId, fileId) {
    const params = new URLSearchParams({ fileId });

    const res = await fetch(`${BASE}/passhare/sessions/${sessionId}/files?${params}`, {
      method: 'POST',
      headers: { Authorization: `Bearer ${getToken()}` }
    });

    if (!res.ok) throw new Error('Failed to share file');
    return res.json();
  },

  async getSessionFiles(sessionId) {
    const res = await fetch(`${BASE}/passhare/sessions/${sessionId}/files`, {
      headers: { Authorization: `Bearer ${getToken()}` }
    });

    if (!res.ok) throw new Error('Failed to get session files');
    return res.json();
  },

  async getSessionParticipants(sessionId) {
    const res = await fetch(`${BASE}/passhare/sessions/${sessionId}/participants`, {
      headers: { Authorization: `Bearer ${getToken()}` }
    });

    if (!res.ok) throw new Error('Failed to load participants');
    return res.json();
  },

  async leaveSession(sessionId) {
    const res = await fetch(`${BASE}/passhare/sessions/${sessionId}/leave`, {
      method: 'POST',
      headers: { Authorization: `Bearer ${getToken()}` }
    });

    if (!res.ok) throw new Error('Failed to leave session');
    return res.json();
  },

  async endSession(sessionId) {
    const res = await fetch(`${BASE}/passhare/sessions/${sessionId}/end`, {
      method: 'POST',
      headers: { Authorization: `Bearer ${getToken()}` }
    });

    if (!res.ok) throw new Error('Failed to end session');
    return res.json();
  },

  async removeSharedFile(sessionId, sessionFileId) {
    const res = await fetch(`${BASE}/passhare/sessions/${sessionId}/files/${sessionFileId}`, {
      method: 'DELETE',
      headers: { Authorization: `Bearer ${getToken()}` }
    });

    if (!res.ok) throw new Error('Failed to remove file');
    return res.json();
  },

  downloadSessionFileUrl(sessionId, sessionFileId) {
    return `${BASE}/passhare/sessions/${sessionId}/files/${sessionFileId}/download`;
  }
};
