import axios from 'axios'
import { useRouter } from 'vue-router'

const api = axios.create({
  baseURL: 'http://localhost:8080',
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
      window.location.href = '/login'
    }
    return Promise.reject(err)
  }
)

export function login(data) { return api.post('/api/auth/login', data) }
export function getUserInfo() { return api.get('/api/auth/info') }

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

export default api
