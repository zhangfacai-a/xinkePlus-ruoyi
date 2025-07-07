
package com.ruoyi.system.service.impl;

import com.ruoyi.system.mapper.LiveRecordsStatMapper;
import com.ruoyi.system.service.ILiveRecordsStatService;
import com.ruoyi.system.vo.LiveStatUserVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LiveRecordsStatServiceImpl implements ILiveRecordsStatService {
    @Autowired
    private LiveRecordsStatMapper statMapper;

    @Override
    public List<LiveStatUserVo> getYesterdayLiveStat() {
        return statMapper.getYesterdayLiveStat();
    }
}