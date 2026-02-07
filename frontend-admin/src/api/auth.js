import request from '@/utils/request'

export function login(data) {
  return request.post('/auth/login', {
    ...data,
    password: btoa(data.password)
  })
}

export function getUserInfo() {
  return request.get('/auth/info')
}

export function logout() {
  return request.post('/auth/logout')
}
