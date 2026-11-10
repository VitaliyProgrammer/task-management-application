import { api, unwrap } from './client';

// One place that knows every backend URL.
// Components never write URLs themselves — they call these functions.

// ===== Auth =====

export const authApi = {
  login: (credentials) => unwrap(api.post('/auth/login', credentials)),

  register: (data) => unwrap(api.post('/auth/registration', data)),
};

// ===== Current user =====

export const profileApi = {
  get: () => unwrap(api.get('/users/me')),

  update: (data) => unwrap(api.put('/users/me', data)),

  saveTelegram: (telegramChatId) => unwrap(api.put('/users/me/telegram', { telegramChatId })),

  saveEmailCredentials: (data) => unwrap(api.put('/users/me/email-credentials', data)),

  getCalendarConnectUrl: () => unwrap(api.get('/oauth/google-calendar/connect')),

  disconnectCalendar: () => unwrap(api.delete('/oauth/google-calendar')),
};

// ===== Users =====

export const usersApi = {
  list: () => unwrap(api.get('/users', { params: { size: 200, sort: 'firstName' } })),

  changeRole: (id, role) => unwrap(api.put(`/users/${id}/role`, { role })),
};

// ===== Tasks =====

export const tasksApi = {
  // Spring reads repeated params as arrays: ?status=A&status=B
  search: (params) =>
    unwrap(api.get('/tasks', { params, paramsSerializer: { indexes: null } })),

  get: (id) => unwrap(api.get(`/tasks/${id}`)),

  create: (data) => unwrap(api.post('/tasks', data)),

  update: (id, data) => unwrap(api.put(`/tasks/${id}`, data)),

  remove: (id) => unwrap(api.delete(`/tasks/${id}`)),
};

// ===== Task notifications =====

export const notificationsApi = {
  email: (taskId, recipientEmail) =>
    unwrap(api.post(`/tasks/${taskId}/notifications/email`, { recipientEmail })),

  telegram: (taskId) => unwrap(api.post(`/tasks/${taskId}/notifications/telegram`)),

  calendar: (taskId, dates) => unwrap(api.post(`/tasks/${taskId}/notifications/calendar`, dates)),
};

// ===== Comments =====

export const commentsApi = {
  list: (taskId) => unwrap(api.get('/comments', { params: { taskId, size: 100, sort: 'timestamp' } })),

  create: (taskId, text) => unwrap(api.post('/comments', { taskId, text })),
};

// ===== Attachments (Dropbox) =====

export const attachmentsApi = {
  list: (taskId) => unwrap(api.get('/dropbox', { params: { taskId, size: 100 } })),

  upload: (taskId, file) => {
    const form = new FormData();
    form.append('file', file);

    return unwrap(api.post('/dropbox/upload', form, { params: { taskId } }));
  },

  download: (attachmentId) =>
    unwrap(api.get(`/dropbox/attachments/${attachmentId}/download`, { responseType: 'blob' })),
};

// ===== Projects =====

export const projectsApi = {
  list: () => unwrap(api.get('/projects', { params: { size: 200, sort: 'name' } })),

  create: (data) => unwrap(api.post('/projects', data)),

  update: (id, data) => unwrap(api.put(`/projects/${id}`, data)),

  remove: (id) => unwrap(api.delete(`/projects/${id}`)),
};

// ===== Labels =====

export const labelsApi = {
  list: () => unwrap(api.get('/labels', { params: { size: 200, sort: 'name' } })),

  create: (data) => unwrap(api.post('/labels', data)),

  update: (id, data) => unwrap(api.put(`/labels/${id}`, data)),

  remove: (id) => unwrap(api.delete(`/labels/${id}`)),
};
