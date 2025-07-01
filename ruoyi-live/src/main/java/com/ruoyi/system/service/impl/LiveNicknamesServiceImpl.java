package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.LiveNicknamesMapper;
import com.ruoyi.system.domain.LiveNicknames;
import com.ruoyi.system.service.ILiveNicknamesService;

/**
 * 主播姓名Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-06-17
 */
@Service
public class LiveNicknamesServiceImpl implements ILiveNicknamesService 
{
    @Autowired
    private LiveNicknamesMapper liveNicknamesMapper;

    /**
     * 查询主播姓名
     * 
     * @param id 主播姓名主键
     * @return 主播姓名
     */
    @Override
    public LiveNicknames selectLiveNicknamesById(Long id)
    {
        return liveNicknamesMapper.selectLiveNicknamesById(id);
    }

    /**
     * 查询主播姓名列表
     * 
     * @param liveNicknames 主播姓名
     * @return 主播姓名
     */
    @Override
    public List<LiveNicknames> selectLiveNicknamesList(LiveNicknames liveNicknames)
    {
        return liveNicknamesMapper.selectLiveNicknamesList(liveNicknames);
    }

    /**
     * 新增主播姓名
     * 
     * @param liveNicknames 主播姓名
     * @return 结果
     */
    @Override
    public int insertLiveNicknames(LiveNicknames liveNicknames)
    {
        return liveNicknamesMapper.insertLiveNicknames(liveNicknames);
    }

    /**
     * 修改主播姓名
     * 
     * @param liveNicknames 主播姓名
     * @return 结果
     */
    @Override
    public int updateLiveNicknames(LiveNicknames liveNicknames)
    {
        return liveNicknamesMapper.updateLiveNicknames(liveNicknames);
    }

    /**
     * 批量删除主播姓名
     * 
     * @param ids 需要删除的主播姓名主键
     * @return 结果
     */
    @Override
    public int deleteLiveNicknamesByIds(Long[] ids)
    {
        return liveNicknamesMapper.deleteLiveNicknamesByIds(ids);
    }

    /**
     * 删除主播姓名信息
     * 
     * @param id 主播姓名主键
     * @return 结果
     */
    @Override
    public int deleteLiveNicknamesById(Long id)
    {
        return liveNicknamesMapper.deleteLiveNicknamesById(id);
    }
}
