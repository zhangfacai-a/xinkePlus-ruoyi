package com.ruoyi.system.service;

import java.util.Map;

public interface IMailImportService {
    /**
     * 拉取指定唯一数字邮件，自动入库，返回插入信息
     * @param mailKey 邮件唯一标记（param1数字）
     * @return 处理和插入结果
     */
    Map<String, Object> importRecordFromMail(String mailKey);
}
