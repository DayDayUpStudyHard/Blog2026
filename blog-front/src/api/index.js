import axios from 'axios'

const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE || '',
  timeout: 10000
})

export function getArticles(params) {
  return api.get('/api/articles', { params })
}

export function getArticleDetail(id) {
  return api.get(`/api/articles/${id}`)
}

export function getCategories() {
  return api.get('/api/categories')
}

export function getTags() {
  return api.get('/api/tags')
}

export function getArticleNav(id) {
  return api.get(`/api/articles/${id}/nav`)
}

export function getComments(articleId, params) {
  return api.get(`/api/articles/${articleId}/comments`, { params })
}

export function addComment(articleId, data) {
  return api.post(`/api/articles/${articleId}/comments`, data)
}

export function getSiteInfo() {
  return api.get('/api/site/info')
}

export function getMoments(params) {
  return api.get('/api/moments', { params })
}

export function getGuestbookMessages(params) {
  return api.get('/api/guestbook', { params })
}

export function addGuestbookMessage(data) {
  return api.post('/api/guestbook', data)
}

export function getAbout() {
  return api.get('/api/about')
}

export default api
