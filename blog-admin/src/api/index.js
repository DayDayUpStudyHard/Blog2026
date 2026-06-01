import axios from 'axios'

const api = axios.create({
  // 开发环境: http://localhost:8080，生产环境: / (nginx 反向代理)
  baseURL: import.meta.env.VITE_API_BASE || '/',
  timeout: 10000
})

api.interceptors.request.use(config => {
  const token = localStorage.getItem('blog-token')
  if (token) config.headers['blog-token'] = token
  return config
})

api.interceptors.response.use(
  res => res,
  err => {
    if (err.response && err.response.status === 401) {
      localStorage.removeItem('blog-token')
      // 使用 BASE_URL 适配子路径部署
      window.location.href = import.meta.env.BASE_URL + 'login'
    }
    return Promise.reject(err)
  }
)

export function login(data) { return api.post('/api/auth/login', data) }
export function getUserInfo() { return api.get('/api/auth/info') }
export function updateProfile(data) { return api.put('/api/auth/profile', data) }
export function updatePassword(data) { return api.put('/api/auth/password', data) }

export function getAdminArticles(params) { return api.get('/api/admin/articles', { params }) }
export function createArticle(data) { return api.post('/api/admin/articles', data) }
export function updateArticle(id, data) { return api.put(`/api/admin/articles/${id}`, data) }
export function getAdminArticleDetail(id) { return api.get(`/api/admin/articles/${id}`) }
export function deleteArticle(id) { return api.delete(`/api/admin/articles/${id}`) }

export function getCategories() { return api.get('/api/categories') }
export function createCategory(data) { return api.post('/api/admin/categories', data) }
export function updateCategory(id, data) { return api.put(`/api/admin/categories/${id}`, data) }
export function deleteCategory(id) { return api.delete(`/api/admin/categories/${id}`) }

export function getTags() { return api.get('/api/tags') }
export function createTag(data) { return api.post('/api/admin/tags', data) }
export function updateTag(id, data) { return api.put(`/api/admin/tags/${id}`, data) }
export function deleteTag(id) { return api.delete(`/api/admin/tags/${id}`) }

export function getAdminComments(params) { return api.get('/api/admin/comments', { params }) }
export function updateCommentStatus(id, data) { return api.put(`/api/admin/comments/${id}/status`, data) }
export function deleteComment(id) { return api.delete(`/api/admin/comments/${id}`) }

export function uploadFile(file) {
  const formData = new FormData()
  formData.append('file', file)
  return api.post('/api/upload', formData, { headers: { 'Content-Type': 'multipart/form-data' } })
}

export function getAdminMoments(params) { return api.get('/api/admin/moments', { params }) }
export function createMoment(data) { return api.post('/api/admin/moments', data) }
export function updateMoment(id, data) { return api.put(`/api/admin/moments/${id}`, data) }
export function deleteMoment(id) { return api.delete(`/api/admin/moments/${id}`) }

export function getAbout() { return api.get('/api/admin/about') }
export function updateAbout(data) { return api.put('/api/admin/about', data) }

export default api
