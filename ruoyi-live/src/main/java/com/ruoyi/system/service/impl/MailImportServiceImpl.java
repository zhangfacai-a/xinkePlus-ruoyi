package com.ruoyi.system.service.impl;

import com.ruoyi.system.domain.LiveRecords;
import com.ruoyi.system.domain.LiveNicknames;
import com.ruoyi.system.domain.LiveRooms;
import com.ruoyi.system.mapper.LiveRecordsMapper;
import com.ruoyi.system.mapper.LiveNicknamesMapper;
import com.ruoyi.system.mapper.LiveRoomsMapper;
import com.ruoyi.system.service.IMailImportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.MimeUtility;
import java.math.BigDecimal;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

@Service
public class MailImportServiceImpl implements IMailImportService {

    @Autowired
    private LiveRecordsMapper liveRecordsMapper;
    @Autowired
    private LiveNicknamesMapper liveNicknamesMapper;
    @Autowired
    private LiveRoomsMapper liveRoomsMapper;

    @Override
    public Map<String, Object> importRecordFromMail(String mailKey) {
        Map<String, Object> result = new HashMap<>();
        result.put("param1", mailKey);

        String host = "imap.qq.com";
        String user = "605609868@qq.com";
        String password = "uoqbbefmqarabcej";

        try {
            Thread.sleep(3000);

            Properties props = new Properties();
            props.setProperty("mail.store.protocol", "imap");
            props.setProperty("mail.imap.host", host);
            props.setProperty("mail.imap.port", "993");
            props.setProperty("mail.imap.ssl.enable", "true");

            Session session = Session.getDefaultInstance(props);
            Store store = session.getStore("imap");
            store.connect(host, user, password);

            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_ONLY);

            Message[] messages = inbox.getMessages();
            boolean found = false;
            String content = null;
            Message matchedMsg = null;
            for (int i = messages.length - 1; i >= 0; i--) {
                Message msg = messages[i];
                content = getTextFromMessage(msg);
                if (content != null && content.contains(mailKey)) {
                    matchedMsg = msg;
                    found = true;
                    break;
                }
            }

            if (!found) {
                result.put("mail_content", "未找到包含指定数字的邮件。");
                inbox.close(false);
                store.close();
                result.put("msg", "failed");
                return result;
            }

            // 解析正文为Map
            Map<String, String> fields = parseMailContent(content);

            // 自动查/插昵称和直播间
            Long nicknameId = getOrInsertNickname(fields.get("姓名"));
            Long roomId = getOrInsertRoom(fields.get("直播间"));

            // ====== 解析时间相关字段 ======
            Long submitTime = parseDateTimeToTimestamp(fields.get("提交时间"));
            Long liveDate = parseDateToTimestamp(fields.get("日期"));
            Integer startTime = parseTimeToMinutes(fields.get("开始时间"));
            Integer endTime = parseTimeToMinutes(fields.get("结束时间"));
            Integer offlineMinutes = parseInt(fields.get("断播时长(分钟)"));

            // 封装实体
            LiveRecords record = new LiveRecords();
            record.setFillId(fields.get("填写ID"));
            record.setAnswerTime(fields.get("答题时间")); // 新增，直接赋值
            record.setNicknameId(nicknameId);
            record.setRoomId(roomId);

            record.setSubmitTime(submitTime);
            record.setLiveDate(liveDate);
            record.setStartTime(startTime);
            record.setEndTime(endTime);
            record.setOfflineMinutes(offlineMinutes);

            // 直播时长 = 计算得出
            record.setLiveMinutes(calcLiveMinutes(record));

            // 其它原有字段
            record.setTotalViewers(parseInt(fields.get("累计观看人数")));
            record.setRoomViewers(parseInt(fields.get("主播场观")));
            record.setDealUsers(parseInt(fields.get("成交人数")));
            record.setSalesAmount(parseBigDecimal(fields.get("销售金额")));
            record.setExposure(parseInt(fields.get("曝光人数")));
            record.setAnchorExposure(parseInt(fields.get("主播曝光人数")));
            record.setNewFollowers(parseInt(fields.get("新增粉丝")));
            record.setRemark(fields.get("备注"));
            record.setTotalDeals(parseInt(fields.get("成交总人数")));

            int insertResult = liveRecordsMapper.insertLiveRecords(record);

            // 控制台打印
            System.out.println("======= 匹配到邮件并插入 =======");
            System.out.println("主题: " + decodeText(matchedMsg.getSubject()));
            System.out.println("发件人: " + decodeText(matchedMsg.getFrom()[0].toString()));
            System.out.println("正文: " + content);
            System.out.println("存入ID: " + record.getId());
            System.out.println("==============================");

            result.put("mail_subject", decodeText(matchedMsg.getSubject()));
            result.put("mail_from", decodeText(matchedMsg.getFrom()[0].toString()));
            result.put("mail_content", content);
            result.put("inserted", insertResult == 1);
            result.put("record_id", record.getId());
            result.put("msg", "success");

            inbox.close(false);
            store.close();

        } catch (Exception e) {
            result.put("error", e.getMessage());
            e.printStackTrace();
        }
        return result;
    }

    // 解析邮件内容
    private Map<String, String> parseMailContent(String content) {
        Map<String, String> fields = new HashMap<>();
        if (content == null) return fields;
        String pure = content.replaceAll("<br>", "\n").replaceAll("<[^>]+>", "");
        for (String line : pure.split("\n")) {
            String[] parts = line.split(":", 2);
            if (parts.length == 2) fields.put(parts[0].trim(), parts[1].trim());
        }
        return fields;
    }

    // 查/插昵称
    private Long getOrInsertNickname(String nickname) {
        if (nickname == null) return null;
        List<LiveNicknames> existList = liveNicknamesMapper.selectLiveNicknamesList(new LiveNicknames() {{ setNickname(nickname); }});
        if (existList != null && !existList.isEmpty()) {
            return existList.get(0).getId();
        }
        LiveNicknames entity = new LiveNicknames();
        entity.setNickname(nickname);
        liveNicknamesMapper.insertLiveNicknames(entity);
        return entity.getId();
    }

    // 查/插直播间
    private Long getOrInsertRoom(String roomName) {
        if (roomName == null) return null;
        List<LiveRooms> existList = liveRoomsMapper.selectLiveRoomsList(new LiveRooms() {{ setRoomName(roomName); }});
        if (existList != null && !existList.isEmpty()) {
            return existList.get(0).getId();
        }
        LiveRooms entity = new LiveRooms();
        entity.setRoomName(roomName);
        liveRoomsMapper.insertLiveRooms(entity);
        return entity.getId();
    }

    // 解析邮件文本
    private String getTextFromMessage(Part part) throws Exception {
        if (part.isMimeType("text/plain")) {
            return part.getContent().toString();
        } else if (part.isMimeType("text/html")) {
            return part.getContent().toString();
        } else if (part.isMimeType("multipart/*")) {
            Multipart multipart = (Multipart) part.getContent();
            for (int i = 0; i < multipart.getCount(); i++) {
                String text = getTextFromMessage(multipart.getBodyPart(i));
                if (text != null && !text.trim().isEmpty()) {
                    return text;
                }
            }
        }
        return "";
    }

    private String decodeText(String text) {
        try {
            return text == null ? "" : MimeUtility.decodeText(text);
        } catch (Exception e) {
            return text;
        }
    }

    private Integer parseInt(String s) {
        try { return s == null ? null : Integer.parseInt(s.replaceAll("[^0-9]", "")); } catch (Exception e) { return null; }
    }

    private BigDecimal parseBigDecimal(String s) {
        try { return s == null ? null : new BigDecimal(s.replaceAll("[^0-9.]", "")); } catch (Exception e) { return null; }
    }

    // ======= 时间和分钟数解析方法 =======
    // "2025/7/6 13:41" -> 时间戳（秒）
    private Long parseDateTimeToTimestamp(String dateTimeStr) {
        if (dateTimeStr == null || dateTimeStr.isEmpty()) return null;
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/M/d HH:mm");
            LocalDateTime ldt = LocalDateTime.parse(dateTimeStr.trim(), formatter);
            return ldt.atZone(ZoneId.systemDefault()).toEpochSecond();
        } catch (DateTimeParseException e) {
            return null;
        }
    }
    // "2025/7/6" -> 时间戳（当天0点秒）
    private Long parseDateToTimestamp(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) return null;
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/M/d");
            LocalDate ld = LocalDate.parse(dateStr.trim(), formatter);
            return ld.atStartOfDay(ZoneId.systemDefault()).toEpochSecond();
        } catch (DateTimeParseException e) {
            return null;
        }
    }
    // "11:38" -> 698 (11*60+38)
    private Integer parseTimeToMinutes(String timeStr) {
        if (timeStr == null || !timeStr.contains(":")) return null;
        try {
            String[] arr = timeStr.trim().split(":");
            int hour = Integer.parseInt(arr[0]);
            int min = Integer.parseInt(arr[1]);
            return hour * 60 + min;
        } catch (Exception e) {
            return null;
        }
    }

    // ===== 直播时长计算方法 =====
    private Integer calcLiveMinutes(LiveRecords rec) {
        Integer start = rec.getStartTime();
        Integer end = rec.getEndTime();
        Integer offline = rec.getOfflineMinutes() == null ? 0 : rec.getOfflineMinutes();

        if (start == null || end == null) return null;
        int total;
        if (end >= start) {
            total = end - start;
        } else {
            total = (end + 24 * 60) - start; // 跨天
        }
        int result = total - offline;
        return Math.max(result, 0);
    }
}
