package com.nowcoder.community.controller;

import com.alibaba.fastjson2.JSONObject;
import com.nowcoder.community.Annotation.LoginRequired;
import com.nowcoder.community.entity.Event;
import com.nowcoder.community.entity.Message;
import com.nowcoder.community.entity.Page;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.MessageService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.utils.CommunityUtil;
import com.nowcoder.community.utils.Constants;
import com.nowcoder.community.utils.UserHolder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.lang.constant.Constable;
import java.util.*;

@Controller()
@RequestMapping(path = "/message")
public class MessageController {
    @Autowired
    private MessageService messageService;
    @Autowired
    private UserHolder userHolder;
    @Autowired
    private UserService userService;
    @RequestMapping(path = "/letter", method = RequestMethod.GET)
    @LoginRequired
    public String getLetter(Model model, Page page) {
        User user = userHolder.get();
        page.setTotalRows(messageService.findConversationRows(user.getId()));
        page.setPath("/message/letter");
        page.setLimit(5);
        List<Message> conversations = messageService.findConversationList(user.getId(),
                page.getOffset(), page.getLimit());
        List<Map<String, Object>> maps = new ArrayList<>();
        for (Message message : conversations) {
            Map<String, Object> map = new HashMap<>();
            int targetId = message.getFromId() == user.getId() ? message.getToId() : message.getFromId();
            User targetUser = userService.findUserById(targetId);
            int unRead = messageService.findLetterUnReadCount(user.getId(), message.getConversationId());
            int totalLetters = messageService.findLetterRows(message.getConversationId());
            map.put("message", message);
            map.put("unRead", unRead);
            map.put("targetUser", targetUser);
            map.put("total", totalLetters);
            maps.add(map);
        }
        model.addAttribute("maps", maps);
        model.addAttribute("letterUnReadCnt", messageService.findLetterUnReadCount(user.getId(), null));
        model.addAttribute("noticeUnReadCnt", messageService.findNoticeUnReadCount(user.getId(), null));
        return "site/letter";
    }
    @RequestMapping(path = "/send", method = RequestMethod.POST)
    @ResponseBody
    public String sendLetter(String username, String content) {
        if (StringUtils.isBlank(username) || StringUtils.isBlank(content)) {
            return CommunityUtil.toJSONObject(400, "私信目标和内容不能为空！");
        }
        User toUser = userService.findUserByName(username);
        if (toUser == null) {
            return CommunityUtil.toJSONObject(400, "私信用户不存在！");
        }
        User user = userHolder.get();
        int fromId = user.getId();
        int toId = toUser.getId();
        String conversationId = Math.min(fromId, toId) + "_" +
                String.valueOf(Math.max(fromId, toId));
        Message message = new Message();
        message.setFromId(user.getId());
        message.setToId(toId);
        message.setConversationId(conversationId);
        message.setContent(content);
        message.setCreateTime(new Date());
        messageService.addMessage(message);
        return CommunityUtil.toJSONObject(200, "ok！");
    }
    @RequestMapping(path = "/letter/detail/{conversationId}", method = RequestMethod.GET)
    public String getLetterDetail(@PathVariable("conversationId") String conversationId, Model model, Page page) {
        if (StringUtils.isBlank(conversationId)) {
            return "redirect:/message/letter";
        }
        User user = userHolder.get();
        List<String> ids = Arrays.asList(conversationId.split("_"));
        if (!ids.contains(String.valueOf(user.getId()))) {
            return "redirect:/message/letter";
        }
        int peerId = -1;
        for (String id : ids) {
            if (!id.equals(String.valueOf(user.getId()))) {
                peerId = Integer.valueOf(id);
                break;
            }
        }
        User peer = userService.findUserById(peerId);
        //  非法请求
        if (peer == null) {
            return "redirect:/message/letter";
        }
        int unRead = messageService.findLetterUnReadCount(user.getId(), conversationId);
        //  清空会话的所有私信未读状态
        if (unRead > 0) {
            messageService.updateConversationStatus(user.getId(), 1, conversationId);
        }
        page.setTotalRows(messageService.findLetterRows(conversationId));
        page.setPath("/message/letter/detail/" + conversationId + "");
        page.setLimit(5);
        List<Message> letters = messageService.findLetters(conversationId, page.getOffset(), page.getLimit());
        model.addAttribute("letters", letters);
        model.addAttribute("user", user);
        model.addAttribute("peer", peer);
        return "site/letter-detail";
    }
    @RequestMapping(path = "/notice", method = RequestMethod.GET)
    public String getNotice(Model model) {
        User loginUser = userHolder.get();
        List<Message> notices = messageService.findNoticeList(loginUser.getId());
        Map<String, Object> map = new HashMap<>();
        map.put("commentEventCnt", 0);
        map.put("commentUnReadCnt", 0);
        map.put("likeEventCnt", 0);
        map.put("likeUnReadCnt", 0);
        map.put("followEventCnt", 0);
        map.put("followUnReadCnt", 0);
        for (Message notice : notices) {
            Map<String, Object> content = JSONObject.parseObject(notice.getContent(), Map.class);
            User user = userService.findUserById((int) content.get("userId"));
            //  eg: commentEventCnt
            String eventType = notice.getConversationId();
            map.replace(eventType + "EventCnt", messageService.findNoticeRows(loginUser.getId(), notice.getConversationId()));
            map.replace(eventType + "UnReadCnt", messageService.findNoticeUnReadCount(loginUser.getId(), notice.getConversationId()));
            map.put(eventType + "Notice", notice);
            map.put(eventType + "User", user);
        }
        map.put("letterUnReadCnt", messageService.findLetterUnReadCount(loginUser.getId(), null));
        map.put("noticeUnReadCnt", messageService.findNoticeUnReadCount(loginUser.getId(), null));
        model.addAttribute("map", map);
        return "site/notice";
    }
    @RequestMapping(path = "/notice/detail/{conversationId}", method = RequestMethod.GET)
    public String getNoticeDetail(@PathVariable("conversationId") String conversationId, Model model, Page page) {
        User loginUser = userHolder.get();
        page.setPath("message/notice/detail/" + conversationId);
        page.setTotalRows(messageService.findNoticeRows(loginUser.getId(), conversationId));
        List<Message> notices = messageService.findNotices(loginUser.getId(), conversationId, page.getOffset(), page.getLimit());
        List<Map<String, Object>> noticeList = new ArrayList<>();
        for (Message notice : notices) {
            Map<String, Object> map = new HashMap<>();
            Map<String, Object> content = JSONObject.parseObject(notice.getContent(), Map.class);
            User user = userService.findUserById((int) content.get("userId"));
            map.put("user", user);
            map.put("notice", notice);
            map.put("content", content);
            map.put("loginUser", loginUser);
            noticeList.add(map);
        }
        model.addAttribute("noticeList", noticeList);
        messageService.updateConversationStatus(loginUser.getId(), 1, conversationId);
        return "site/notice-detail";
    }
}
