package com.ruoyi.system.vo;

import com.ruoyi.common.annotation.Excel;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class LiveRecordsExportVo {
    @Excel(name = "提交时间", width = 20)
    private String submitTime;

    @Excel(name = "填写ID", width = 20)
    private String fillId;

    @Excel(name = "答题时间", width = 20)
    private String answerTime;

    @Excel(name = "昵称")
    private String nickname;

    @Excel(name = "直播间")
    private String roomName;

    @Excel(name = "直播日期", width = 15)
    private String liveDate;

    @Excel(name = "开始时间", width = 10)
    private String startTime;

    @Excel(name = "结束时间", width = 10)
    private String endTime;

    @Excel(name = "直播时长（分钟）", width = 10)
    private Integer liveMinutes;

    @Excel(name = "断播时长（分钟）", width = 10)
    private Integer offlineMinutes;

    @Excel(name = "累计观看人数")
    private Long totalViewers;

    @Excel(name = "主播场观")
    private Long roomViewers;

    @Excel(name = "成交人数")
    private Long dealUsers;

    @Excel(name = "销售金额（元）")
    private BigDecimal salesAmount;

    @Excel(name = "曝光人数")
    private Long exposure;

    @Excel(name = "主播曝光人数")
    private Long anchorExposure;

    @Excel(name = "转粉数")
    private Long newFollowers;

    @Excel(name = "备注")
    private String remark;

    @Excel(name = "成交总数")
    private Long totalDeals;
}
