package com.ruoyi.web.controller.live;

import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.ruoyi.system.domain.LiveRecords;

import com.ruoyi.system.vo.LiveRecordsExportVo;
import com.ruoyi.system.vo.LiveRecordsVo;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.service.ILiveRecordsService;

import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 上播时间Controller
 *
 * @author ruoyi
 * @date 2025-06-17
 */
@RestController
@RequestMapping("/live/records")
public class LiveRecordsController extends BaseController
{
    @Autowired
    private ILiveRecordsService liveRecordsService;


    /**
     * 查询上播时间列表
     */
    @PreAuthorize("@ss.hasPermi('system:records:list')")
    @GetMapping("/list")
    public TableDataInfo list(@ModelAttribute LiveRecordsVo vo) {
        startPage();
        List<LiveRecordsVo> list = liveRecordsService.selectLiveRecordsList(vo);
        return getDataTable(list);
    }

    /**
     * 导出上播时间列表
     */
    @PostMapping("/export")
    public void export(HttpServletResponse response, @ModelAttribute LiveRecordsVo vo)
    {
        List<LiveRecordsVo> list = liveRecordsService.selectLiveRecordsList(vo);
        List<LiveRecordsExportVo> exportList = new ArrayList<>();
        for (LiveRecordsVo record : list) {
            LiveRecordsExportVo exportVo = new LiveRecordsExportVo();
            exportVo.setSubmitTime(formatTimestamp(record.getSubmitTime(), "yyyy-MM-dd HH:mm:ss"));
            exportVo.setFillId(record.getFillId());
            exportVo.setAnswerTime(record.getAnswerTime());
            exportVo.setNickname(record.getNickname());
            exportVo.setRoomName(record.getRoomName());
            exportVo.setLiveDate(formatTimestamp(record.getLiveDate(), "yyyy-MM-dd"));
            exportVo.setStartTime(formatMinute(record.getStartTime()));
            exportVo.setEndTime(formatMinute(record.getEndTime()));
            exportVo.setLiveMinutes(record.getLiveMinutes());
            exportVo.setOfflineMinutes(record.getOfflineMinutes());
            exportVo.setTotalViewers(record.getTotalViewers());
            exportVo.setRoomViewers(record.getRoomViewers());
            exportVo.setDealUsers(record.getDealUsers());
            exportVo.setSalesAmount(record.getSalesAmount());
            exportVo.setExposure(record.getExposure());
            exportVo.setAnchorExposure(record.getAnchorExposure());
            exportVo.setNewFollowers(record.getNewFollowers());
            exportVo.setRemark(record.getRemark());
            exportVo.setTotalDeals(record.getTotalDeals());
            exportList.add(exportVo);
        }
        ExcelUtil<LiveRecordsExportVo> util = new ExcelUtil<>(LiveRecordsExportVo.class);
        util.exportExcel(response, exportList, "上播时间数据");
    }

    private static String formatTimestamp(Long timestamp, String pattern) {
        if (timestamp == null) return "";
        java.time.format.DateTimeFormatter fmt = java.time.format.DateTimeFormatter.ofPattern(pattern);
        return java.time.Instant.ofEpochSecond(timestamp)
                .atZone(java.time.ZoneId.systemDefault())
                .format(fmt);
    }

    private static String formatMinute(Integer minutes) {
        if (minutes == null) return "";
        int h = minutes / 60;
        int m = minutes % 60;
        return String.format("%02d:%02d", h, m);
    }

    /**
     * 获取上播时间详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:records:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(liveRecordsService.selectLiveRecordsById(id));
    }

    /**
     * 新增上播时间
     */
    @PreAuthorize("@ss.hasPermi('system:records:add')")
    @Log(title = "上播时间", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody LiveRecords liveRecords)
    {
        return toAjax(liveRecordsService.insertLiveRecords(liveRecords));
    }

    /**
     * 修改上播时间
     */
    @PreAuthorize("@ss.hasPermi('system:records:edit')")
    @Log(title = "上播时间", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody LiveRecords liveRecords)
    {
        return toAjax(liveRecordsService.updateLiveRecords(liveRecords));
    }

    /**
     * 删除上播时间
     */
    @PreAuthorize("@ss.hasPermi('system:records:remove')")
    @Log(title = "上播时间", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(liveRecordsService.deleteLiveRecordsByIds(ids));
    }
}