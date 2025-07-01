package com.ruoyi.web.controller.live;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.LiveNicknames;
import com.ruoyi.system.service.ILiveNicknamesService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 主播姓名Controller
 * 
 * @author ruoyi
 * @date 2025-06-17
 */
@RestController
@RequestMapping("/live/nicknames")
public class LiveNicknamesController extends BaseController
{
    @Autowired
    private ILiveNicknamesService liveNicknamesService;

    /**
     * 查询主播姓名列表
     */
    @PreAuthorize("@ss.hasPermi('system:nicknames:list')")
    @GetMapping("/list")
    public TableDataInfo list(LiveNicknames liveNicknames)
    {
        startPage();
        List<LiveNicknames> list = liveNicknamesService.selectLiveNicknamesList(liveNicknames);
        return getDataTable(list);
    }

    /**
     * 导出主播姓名列表
     */
    @PreAuthorize("@ss.hasPermi('system:nicknames:export')")
    @Log(title = "主播姓名", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, LiveNicknames liveNicknames)
    {
        List<LiveNicknames> list = liveNicknamesService.selectLiveNicknamesList(liveNicknames);
        ExcelUtil<LiveNicknames> util = new ExcelUtil<LiveNicknames>(LiveNicknames.class);
        util.exportExcel(response, list, "主播姓名数据");
    }

    /**
     * 获取主播姓名详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:nicknames:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(liveNicknamesService.selectLiveNicknamesById(id));
    }

    /**
     * 新增主播姓名
     */
    @PreAuthorize("@ss.hasPermi('system:nicknames:add')")
    @Log(title = "主播姓名", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody LiveNicknames liveNicknames)
    {
        return toAjax(liveNicknamesService.insertLiveNicknames(liveNicknames));
    }

    /**
     * 修改主播姓名
     */
    @PreAuthorize("@ss.hasPermi('system:nicknames:edit')")
    @Log(title = "主播姓名", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody LiveNicknames liveNicknames)
    {
        return toAjax(liveNicknamesService.updateLiveNicknames(liveNicknames));
    }

    /**
     * 删除主播姓名
     */
    @PreAuthorize("@ss.hasPermi('system:nicknames:remove')")
    @Log(title = "主播姓名", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(liveNicknamesService.deleteLiveNicknamesByIds(ids));
    }
}
