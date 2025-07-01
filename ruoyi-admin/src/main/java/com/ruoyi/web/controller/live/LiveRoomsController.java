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
import com.ruoyi.system.domain.LiveRooms;
import com.ruoyi.system.service.ILiveRoomsService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 直播间名Controller
 * 
 * @author ruoyi
 * @date 2025-06-17
 */
@RestController
@RequestMapping("/live/rooms")
public class LiveRoomsController extends BaseController
{
    @Autowired
    private ILiveRoomsService liveRoomsService;

    /**
     * 查询直播间名列表
     */
    @PreAuthorize("@ss.hasPermi('system:rooms:list')")
    @GetMapping("/list")
    public TableDataInfo list(LiveRooms liveRooms)
    {
        startPage();
        List<LiveRooms> list = liveRoomsService.selectLiveRoomsList(liveRooms);
        return getDataTable(list);
    }


    /**
     * 获取直播间名详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:rooms:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(liveRoomsService.selectLiveRoomsById(id));
    }

    /**
     * 新增直播间名
     */
    @PreAuthorize("@ss.hasPermi('system:rooms:add')")
    @Log(title = "直播间名", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody LiveRooms liveRooms)
    {
        return toAjax(liveRoomsService.insertLiveRooms(liveRooms));
    }

    /**
     * 修改直播间名
     */
    @PreAuthorize("@ss.hasPermi('system:rooms:edit')")
    @Log(title = "直播间名", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody LiveRooms liveRooms)
    {
        return toAjax(liveRoomsService.updateLiveRooms(liveRooms));
    }

    /**
     * 删除直播间名
     */
    @PreAuthorize("@ss.hasPermi('system:rooms:remove')")
    @Log(title = "直播间名", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(liveRoomsService.deleteLiveRoomsByIds(ids));
    }
}
