import request from '@/utils/request'

// 查询主播姓名列表
export function listNicknames(query) {
    return request({
        url: '/live/nicknames/list',
        method: 'get',
        params: query
    })
}

// 查询主播姓名详细
export function getNicknames(id) {
    return request({
        url: '/live/nicknames/' + id,
        method: 'get'
    })
}

// 新增主播姓名
export function addNicknames(data) {
    return request({
        url: '/live/nicknames',
        method: 'post',
        data: data
    })
}

// 修改主播姓名
export function updateNicknames(data) {
    return request({
        url: '/live/nicknames',
        method: 'put',
        data: data
    })
}

// 删除主播姓名
export function delNicknames(id) {
    return request({
        url: '/live/nicknames/' + id,
        method: 'delete'
    })
}
