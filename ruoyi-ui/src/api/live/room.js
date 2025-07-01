import request from '@/utils/request'

// 查询直播间名列表
export function listRooms(query) {
    return request({
        url: '/live/rooms/list',
        method: 'get',
        params: query
    })
}

// 查询直播间名详细
export function getRooms(id) {
    return request({
        url: '/live/rooms/' + id,
        method: 'get'
    })
}

// 新增直播间名
export function addRooms(data) {
    return request({
        url: '/live/rooms',
        method: 'post',
        data: data
    })
}

// 修改直播间名
export function updateRooms(data) {
    return request({
        url: '/live/rooms',
        method: 'put',
        data: data
    })
}

// 删除直播间名
export function delRooms(id) {
    return request({
        url: '/live/rooms/' + id,
        method: 'delete'
    })
}
