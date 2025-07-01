package com.ruoyi.system.service.impl;

import java.util.List;

import com.ruoyi.common.annotation.DataSource;
import com.ruoyi.common.enums.DataSourceType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.LiveRoomsMapper;
import com.ruoyi.system.domain.LiveRooms;
import com.ruoyi.system.service.ILiveRoomsService;

/**
 * 直播间名Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-06-17
 */
@Service
public class LiveRoomsServiceImpl implements ILiveRoomsService 
{
    @Autowired
    private LiveRoomsMapper liveRoomsMapper;

    /**
     * 查询直播间名
     * 
     * @param id 直播间名主键
     * @return 直播间名
     */
    @Override
    public LiveRooms selectLiveRoomsById(Long id)
    {
        return liveRoomsMapper.selectLiveRoomsById(id);
    }

    /**
     * 查询直播间名列表
     * 
     * @param liveRooms 直播间名
     * @return 直播间名
     */
    @Override
    public List<LiveRooms> selectLiveRoomsList(LiveRooms liveRooms)
    {
        return liveRoomsMapper.selectLiveRoomsList(liveRooms);
    }

    /**
     * 新增直播间名
     * 
     * @param liveRooms 直播间名
     * @return 结果
     */
    @Override
    public int insertLiveRooms(LiveRooms liveRooms)
    {
        return liveRoomsMapper.insertLiveRooms(liveRooms);
    }

    /**
     * 修改直播间名
     * 
     * @param liveRooms 直播间名
     * @return 结果
     */
    @Override
    public int updateLiveRooms(LiveRooms liveRooms)
    {
        return liveRoomsMapper.updateLiveRooms(liveRooms);
    }

    /**
     * 批量删除直播间名
     * 
     * @param ids 需要删除的直播间名主键
     * @return 结果
     */
    @Override
    public int deleteLiveRoomsByIds(Long[] ids)
    {
        return liveRoomsMapper.deleteLiveRoomsByIds(ids);
    }

    /**
     * 删除直播间名信息
     * 
     * @param id 直播间名主键
     * @return 结果
     */
    @Override
    public int deleteLiveRoomsById(Long id)
    {
        return liveRoomsMapper.deleteLiveRoomsById(id);
    }
}
