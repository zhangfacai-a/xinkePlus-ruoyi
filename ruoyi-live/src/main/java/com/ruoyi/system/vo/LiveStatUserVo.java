package com.ruoyi.system.vo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LiveStatUserVo implements Serializable {
    private String nickname;
    private String roomNames;
    private String livePeriods;
    private Integer totalMinutes;
    private String remarks;
}