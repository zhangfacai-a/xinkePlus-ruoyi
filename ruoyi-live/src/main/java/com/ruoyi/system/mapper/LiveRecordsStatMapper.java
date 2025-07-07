package com.ruoyi.system.mapper;
import java.util.List;
import com.ruoyi.system.vo.LiveStatUserVo;

public interface LiveRecordsStatMapper {
    List<LiveStatUserVo> getYesterdayLiveStat();
}
