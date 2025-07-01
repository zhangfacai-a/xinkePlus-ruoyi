package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.LiveRecords;
import com.ruoyi.system.vo.LiveRecordsVo;


/**
 * 上播时间Mapper接口
 */
public interface LiveRecordsMapper
{
    /**
     * 查询上播时间（返回 VO）
     */
    public LiveRecordsVo selectLiveRecordsById(Long id);

    /**
     * 查询上播时间列表（返回 VO 列表）
     * @param vo 查询条件（VO，含模糊查/区间查等参数）
     * @return 上播时间集合（VO列表）
     */
    public List<LiveRecordsVo> selectLiveRecordsList(LiveRecordsVo vo);

    /**
     * 新增上播时间
     */
    public int insertLiveRecords(LiveRecords liveRecords);

    /**
     * 修改上播时间
     */
    public int updateLiveRecords(LiveRecords liveRecords);

    /**
     * 删除上播时间
     */
    public int deleteLiveRecordsById(Long id);

    /**
     * 批量删除上播时间
     */
    public int deleteLiveRecordsByIds(Long[] ids);
}