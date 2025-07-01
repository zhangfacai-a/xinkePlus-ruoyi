package com.ruoyi.system.domain;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 上播记录对象 live_records
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class LiveRecords extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Long id;

    /** 提交时间（时间戳） */
    @Excel(name = "提交时间")
    private Long submitTime;

    /** 填写ID */
    @Excel(name = "填写ID")
    private String fillId;

    /** 答题时间（字符串：如“1分30秒”） */
    @Excel(name = "答题时间")
    private String answerTime;

    /** 昵称ID（外键） */
    @Excel(name = "昵称ID")
    private Long nicknameId;

    /** 直播间ID（外键） */
    @Excel(name = "直播间ID")
    private Long roomId;

    /** 直播日期（时间戳，表示当天0点） */
    @Excel(name = "直播日期")
    private Long liveDate;

    /** 开始时间（从0点起的分钟数） */
    @Excel(name = "开始时间（分钟）")
    private Integer startTime;

    /** 结束时间（从0点起的分钟数） */
    @Excel(name = "结束时间（分钟）")
    private Integer endTime;

    /** 直播时长（分钟） */
    @Excel(name = "直播时长(分钟)")
    private Integer liveMinutes;

    /** 断播时长（分钟） */
    @Excel(name = "断播时长(分钟)")
    private Integer offlineMinutes;

    /** 累计观看人数 */
    @Excel(name = "累计观看人数")
    private Integer totalViewers;

    /** 主播场观 */
    @Excel(name = "主播场观")
    private Integer roomViewers;

    /** 成交人数 */
    @Excel(name = "成交人数")
    private Integer dealUsers;

    /** 销售金额（元） */
    @Excel(name = "销售金额(元)")
    private BigDecimal salesAmount;

    /** 曝光人数 */
    @Excel(name = "曝光人数")
    private Integer exposure;

    /** 主播曝光人数 */
    @Excel(name = "主播曝光人数")
    private Integer anchorExposure;

    /** 转粉数 */
    @Excel(name = "转粉数")
    private Integer newFollowers;

    /** 成交总人数 */
    @Excel(name = "成交总人数")
    private Integer totalDeals;

    /** 备注 */
    @Excel(name = "备注")
    private String remark;

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("submitTime", getSubmitTime())
                .append("fillId", getFillId())
                .append("answerTime", getAnswerTime())
                .append("nicknameId", getNicknameId())
                .append("roomId", getRoomId())
                .append("liveDate", getLiveDate())
                .append("startTime", getStartTime())
                .append("endTime", getEndTime())
                .append("liveMinutes", getLiveMinutes())
                .append("offlineMinutes", getOfflineMinutes())
                .append("totalViewers", getTotalViewers())
                .append("roomViewers", getRoomViewers())
                .append("dealUsers", getDealUsers())
                .append("salesAmount", getSalesAmount())
                .append("exposure", getExposure())
                .append("anchorExposure", getAnchorExposure())
                .append("newFollowers", getNewFollowers())
                .append("totalDeals", getTotalDeals())
                .append("remark", getRemark())
                .toString();
    }
}
