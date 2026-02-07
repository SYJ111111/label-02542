import request from '@/utils/request'

export function getMemberPage(params) {
  return request.get('/members', { params })
}

export function getMemberById(id) {
  return request.get(`/members/${id}`)
}

export function createMember(data) {
  return request.post('/members', data)
}

export function updateMember(id, data) {
  return request.put(`/members/${id}`, data)
}

export function deleteMember(id) {
  return request.delete(`/members/${id}`)
}
