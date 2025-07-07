package com.ruoyi.system.service;

import com.ruoyi.system.vo.LiveStatUserVo;
import java.util.List;

public interface ILiveRecordsStatService {
    List<LiveStatUserVo> getYesterdayLiveStat();
}
