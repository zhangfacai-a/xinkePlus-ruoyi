package com.ruoyi.web.controller.live;

import com.ruoyi.system.service.ILiveRecordsStatService;
import com.ruoyi.system.vo.LiveStatUserVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class LiveRecordsStatController {

    @Autowired
    private ILiveRecordsStatService statService;

    @GetMapping("/live/stat/yesterday")
    public List<LiveStatUserVo> getYesterdayLiveStat() {
        return statService.getYesterdayLiveStat();
    }
}