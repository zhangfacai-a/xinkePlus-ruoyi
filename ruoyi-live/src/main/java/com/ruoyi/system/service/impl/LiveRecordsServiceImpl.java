package com.ruoyi.system.service.impl;

import java.util.List;

import com.ruoyi.common.annotation.DataSource;
import com.ruoyi.common.enums.DataSourceType;

import com.ruoyi.system.vo.LiveRecordsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruoyi.system.mapper.LiveRecordsMapper;
import com.ruoyi.system.domain.LiveRecords;

import com.ruoyi.system.service.ILiveRecordsService;

/**
 * 上播时间Service业务层处理
 *
 * @author ruoyi
 * @date 2025-06-17
 */
@Service
@DataSource(DataSourceType.SLAVE)
public class LiveRecordsServiceImpl implements ILiveRecordsService
{
    @Autowired
    private LiveRecordsMapper liveRecordsMapper;

    /**
     * 查询上播时间
     *
     * @param id 上播时间主键
     * @return 上播时间（VO）
     */
    @Override
    public LiveRecordsVo selectLiveRecordsById(Long id)
    {
        return liveRecordsMapper.selectLiveRecordsById(id);
    }

    /**
     * 查询上播时间列表
     *
     * @return 上播时间集合（VO 列表）
     */
    @Override
    public List<LiveRecordsVo> selectLiveRecordsList(LiveRecordsVo vo)
    {
        return liveRecordsMapper.selectLiveRecordsList(vo);
    }
//新增
    @Override
    public int insertLiveRecords(LiveRecords liveRecords) {
        liveRecords.setLiveMinutes(calcLiveMinutes(liveRecords));
        return liveRecordsMapper.insertLiveRecords(liveRecords);
    }
//修改
    @Override
    public int updateLiveRecords(LiveRecords liveRecords) {
        liveRecords.setLiveMinutes(calcLiveMinutes(liveRecords));
        return liveRecordsMapper.updateLiveRecords(liveRecords);
    }

    /**
     * 自动计算直播时长（分钟）：考虑跨天和断播时间
     */
    private Integer calcLiveMinutes(LiveRecords rec) {
        Integer start = rec.getStartTime();
        Integer end = rec.getEndTime();
        Integer offline = rec.getOfflineMinutes() == null ? 0 : rec.getOfflineMinutes();

        if (start == null || end == null) return null; // 无法计算
        int total;
        if (end >= start) {
            total = end - start;
        } else {
            total = (end + 24 * 60) - start; // 跨天补24小时
        }
        int result = total - offline;
        return Math.max(result, 0); // 最少0分钟
    }


    /**
     * 批量删除上播时间
     *
     * @param ids 需要删除的上播时间主键
     * @return 结果
     */
    @Override
    public int deleteLiveRecordsByIds(Long[] ids)
    {
        return liveRecordsMapper.deleteLiveRecordsByIds(ids);
    }

    /**
     * 删除上播时间信息
     *
     * @param id 上播时间主键
     * @return 结果
     */
    @Override
    public int deleteLiveRecordsById(Long id)
    {
        return liveRecordsMapper.deleteLiveRecordsById(id);
    }
}