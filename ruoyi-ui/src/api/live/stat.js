import request from '@/utils/request'

// 查询主播姓名列表
export function listNicknames(query) {
    return request({
        url: '/live/stat/yesterday',
        method: 'get',
        params: query
    })
}
