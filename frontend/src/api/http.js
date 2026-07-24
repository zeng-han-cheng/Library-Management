import axios from 'axios'
const http = axios.create({ baseURL: '/api', timeout: 35000 })
http.interceptors.request.use(config => { const token = localStorage.getItem('library_token'); if (token) config.headers.Authorization = `Bearer ${token}`; return config })
http.interceptors.response.use(response => response.data, error => Promise.reject(error))
export default http
