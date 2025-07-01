package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.LiveNicknames;

/**
 * 主播姓名Service接口
 * 
 * @author ruoyi
 * @date 2025-06-17
 */
public interface ILiveNicknamesService 
{
    /**
     * 查询主播姓名
     * 
     * @param id 主播姓名主键
     * @return 主播姓名
     */
    public LiveNicknames selectLiveNicknamesById(Long id);

    /**
     * 查询主播姓名列表
     * 
     * @param liveNicknames 主播姓名
     * @return 主播姓名集合
     */
    public List<LiveNicknames> selectLiveNicknamesList(LiveNicknames liveNicknames);

    /**
     * 新增主播姓名
     * 
     * @param liveNicknames 主播姓名
     * @return 结果
     */
    public int insertLiveNicknames(LiveNicknames liveNicknames);

    /**
     * 修改主播姓名
     * 
     * @param liveNicknames 主播姓名
     * @return 结果
     */
    public int updateLiveNicknames(LiveNicknames liveNicknames);

    /**
     * 批量删除主播姓名
     * 
     * @param ids 需要删除的主播姓名主键集合
     * @return 结果
     */
    public int deleteLiveNicknamesByIds(Long[] ids);

    /**
     * 删除主播姓名信息
     * 
     * @param id 主播姓名主键
     * @return 结果
     */
    public int deleteLiveNicknamesById(Long id);
}
