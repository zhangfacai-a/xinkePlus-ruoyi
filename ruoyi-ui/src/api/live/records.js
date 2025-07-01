import request from '@/utils/request'

// 查询上播时间列表
export function listRecords(query) {
  return request({
    url: '/live/records/list',
    method: 'get',
    params: query
  })
}

// 查询上播时间详细
export function getRecords(id) {
  return request({
    url: '/live/records/' + id,
    method: 'get'
  })
}

// 新增上播时间
export function addRecords(data) {
  return request({
    url: '/live/records',
    method: 'post',
    data: data
  })
}

// 修改上播时间
export function updateRecords(data) {
  return request({
    url: '/live/records',
    method: 'put',
    data: data
  })
}

// 删除上播时间
export function delRecords(id) {
  return request({
    url: '/live/records/' + id,
    method: 'delete'
  })
}
// 导出上播时间（推荐 POST + blob）
export function exportRecords(query) {
  return request({
    url: '/live/records/export',
    method: 'post',
    data: query,
    responseType: 'blob' // 关键
  })
}
