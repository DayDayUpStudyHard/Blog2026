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

export function getComments(articleId) {
  return api.get(`/api/articles/${articleId}/comments`)
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

export function toggleLike(articleId) {
  return api.post(`/api/articles/${articleId}/like`)
}

export function getArticleLikes(articleId) {
  return api.get(`/api/articles/${articleId}/likes`)
}

export function searchArticles(keyword, params = {}) {
  return api.get('/api/articles/search', { params: { keyword, ...params } })
}

export function getArchive() {
  return api.get('/api/articles/archive')
}

export default api
