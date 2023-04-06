package com.nowcoder.community.utils;

import jakarta.annotation.PostConstruct;
import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@Component
public class SensitiveFilter {
    private Logger logger = LoggerFactory.getLogger(getClass());
    private TreeNode root = new TreeNode();
    private final String REPLACE_CHARACTER = "***"; //  替换敏感词的字符串

    class TreeNode {
        private Character character;
        private boolean isEnd;
        private Map<Character, TreeNode> subNodes = new HashMap<>();
        TreeNode() {}

        TreeNode(Character ch, boolean isEnd) {
            this.character = ch;
            this.isEnd = isEnd;
        }
        boolean isContain(Character ch) { return character.equals(ch); }
        boolean isEnd() { return isEnd; }
        TreeNode getSubNode(Character ch) { return subNodes.get(ch); }
        TreeNode addSubNode(Character ch, TreeNode node) {
            if (!subNodes.containsKey(ch)) {
                subNodes.put(ch, node);
            }
            return subNodes.get(ch);
        }
        void setIsEnd(boolean isEnd) { this.isEnd = isEnd; }
    }

    @PostConstruct
    void init() {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("sensitive-keywords.txt");
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        try {
            String text;
            while ((text = bufferedReader.readLine()) != null) {
                addKeyWord(text);
            }
        } catch (IOException e) {
            logger.error("读取敏感词配置文件失败: ", e.getMessage());
            throw new RuntimeException(e);
        }
    }
    void addKeyWord(String keyword) {
        if (StringUtils.isBlank(keyword)) {
            return;
        }
        TreeNode temp = root;
        for (int i = 0; i < keyword.length(); i++) {
            Character ch = keyword.charAt(i);
            TreeNode next = temp.getSubNode(ch);
            if (next == null) {
                temp.addSubNode(ch, new TreeNode(ch, i == keyword.length() - 1 ? true : false));
            } else if (i == keyword.length() - 1) {
                next.setIsEnd(true);
            }
            temp = temp.getSubNode(ch);
        }
    }

    public String filterMatch(String text) {
        if (StringUtils.isBlank(text)) {
            return null;
        }
        TreeNode temp = root;
        int begin = 0;
        int end = begin;
        StringBuilder sb = new StringBuilder();
        while (end < text.length()) {
            Character ch = text.charAt(end);
            if (isSymbol(ch)) {
                //  还未有匹配的敏感词
                if (temp == root) {
                    sb.append(text.charAt(begin));
                    begin++;
                }
                end++;
                continue;
            }
            temp = temp.getSubNode(ch);
            if (temp == null) { //  匹配失败, 重新匹配
                sb.append(text.charAt(begin));
                end = ++begin;
                temp = root;
            } else if (temp.isEnd()) { //  匹配成功, 重新匹配
                sb.append(REPLACE_CHARACTER);
                begin = ++end;
                temp = root;
            } else {  //  继续匹配
                if (end == text.length() - 1) { //  解决出现敏感词为caa和a时, 字符串为bca时的漏检情况
                    sb.append(text.charAt(begin));
                    temp = root;
                    end = ++begin;
                } else {
                    end++;
                }
            }
        }
        return sb.toString();
    }

    boolean isSymbol(Character ch) {
        return !CharUtils.isAsciiAlphanumeric(ch) && (ch < 0x2E80 || ch > 0x9FFF);
    }

}
