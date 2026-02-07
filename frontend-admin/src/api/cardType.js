import request from '@/utils/request'

export function getCardTypePage(params) {
  return request.get('/card-types', { params })
}

export function getCardTypeList() {
  return request.get('/card-types/list')
}

export function getCardTypeById(id) {
  return request.get(`/card-types/${id}`)
}

export function createCardType(data) {
  return request.post('/card-types', data)
}

export function updateCardType(id, data) {
  return request.put(`/card-types/${id}`, data)
}

export function deleteCardType(id) {
  return request.delete(`/card-types/${id}`)
}
