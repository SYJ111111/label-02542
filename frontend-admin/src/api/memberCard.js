import request from '@/utils/request'

export function getMemberCardPage(params) {
  return request.get('/member-cards', { params })
}

export function getMemberCardById(id) {
  return request.get(`/member-cards/${id}`)
}

export function createMemberCard(data) {
  return request.post('/member-cards', data)
}

export function updateMemberCard(id, data) {
  return request.put(`/member-cards/${id}`, data)
}

export function deleteMemberCard(id) {
  return request.delete(`/member-cards/${id}`)
}

export function getDateRangeByCardType(cardTypeId) {
  return request.get(`/member-cards/date-range/${cardTypeId}`)
}
