package com.ruoyi.web.controller.live;
import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.system.service.IMailImportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class LiveTestController {

    @Autowired
    private IMailImportService mailImportService;

    @Anonymous
    @GetMapping("/live/test")
    public Map<String, Object> testGet(@RequestParam(value = "param1", required = false) String param1) {
        return mailImportService.importRecordFromMail(param1);
    }
}
