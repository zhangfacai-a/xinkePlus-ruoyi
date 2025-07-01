package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.LiveRooms;

/**
 * 直播间名Mapper接口
 * 
 * @author ruoyi
 * @date 2025-06-17
 */
public interface LiveRoomsMapper 
{
    /**
     * 查询直播间名
     * 
     * @param id 直播间名主键
     * @return 直播间名
     */
    public LiveRooms selectLiveRoomsById(Long id);

    /**
     * 查询直播间名列表
     * 
     * @param liveRooms 直播间名
     * @return 直播间名集合
     */
    public List<LiveRooms> selectLiveRoomsList(LiveRooms liveRooms);

    /**
     * 新增直播间名
     * 
     * @param liveRooms 直播间名
     * @return 结果
     */
    public int insertLiveRooms(LiveRooms liveRooms);

    /**
     * 修改直播间名
     * 
     * @param liveRooms 直播间名
     * @return 结果
     */
    public int updateLiveRooms(LiveRooms liveRooms);

    /**
     * 删除直播间名
     * 
     * @param id 直播间名主键
     * @return 结果
     */
    public int deleteLiveRoomsById(Long id);

    /**
     * 批量删除直播间名
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteLiveRoomsByIds(Long[] ids);
}
