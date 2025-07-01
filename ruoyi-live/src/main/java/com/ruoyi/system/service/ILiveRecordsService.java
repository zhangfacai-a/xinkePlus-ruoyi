package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.LiveRecords;
import com.ruoyi.system.vo.LiveRecordsVo;


/**
 * 上播时间Service接口
 *
 * @author ruoyi
 * @date 2025-06-17
 */
public interface ILiveRecordsService
{
    /**
     * 查询上播时间
     *
     * @param id 上播时间主键
     * @return 上播时间（VO）
     */
    LiveRecordsVo selectLiveRecordsById(Long id);

    /**
     * 查询上播时间列表
     *
     * @param vo 上播时间查询条件（VO，含模糊/区间/分页参数）
     * @return 上播时间集合（VO 列表）
     */
    List<LiveRecordsVo> selectLiveRecordsList(LiveRecordsVo vo);

    /**
     * 新增上播时间
     *
     * @param liveRecords 上播时间
     * @return 结果
     */
    public int insertLiveRecords(LiveRecords liveRecords);

    /**
     * 修改上播时间
     *
     * @param liveRecords 上播时间
     * @return 结果
     */
    public int updateLiveRecords(LiveRecords liveRecords);

    /**
     * 批量删除上播时间
     *
     * @param ids 需要删除的上播时间主键集合
     * @return 结果
     */
    public int deleteLiveRecordsByIds(Long[] ids);

    /**
     * 删除上播时间信息
     *
     * @param id 上播时间主键
     * @return 结果
     */
    public int deleteLiveRecordsById(Long id);
}