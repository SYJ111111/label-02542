import request from '@/utils/request'

export function getLogPage(params) {
  return request.get('/logs', { params })
}
