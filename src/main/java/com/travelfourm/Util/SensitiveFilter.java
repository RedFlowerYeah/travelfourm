package com.travelfourm.Util;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * 敏感词过滤器
 * 这是一棵树，用来组合成一个句子或者其他的敏感词进行过滤
 * @author 34612*/

@Component
public class SensitiveFilter {

    private static final Logger logger= LoggerFactory.getLogger(SensitiveFilter.class);

    /**
     * 替换符*/
    private static final String REPLACEMENT = "***";

    /**
     * 根节点*/
    private TreeNode rootNode = new TreeNode();

    /**
     * 初始化前缀Tree*/
    @PostConstruct
    public void init(){
        try (
                InputStream inputStream=this.getClass().getClassLoader().getResourceAsStream("sensitive-words.txt");
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                ){
            String keyword;

            /**
             * 一行中的字符*/
            while ((keyword = bufferedReader.readLine()) != null){
                /**
                 * 添加前缀树*/
                this.addkeyword(keyword);
            }
        }catch (IOException e){
            logger.error("加载敏感词文件失败!" + e.getMessage());
        }

    }

    /**
     * 将敏感词加载到Tree中*/
    private void addkeyword(String keyword){

        /**
         * 构建一颗树节点开始对前缀树进行生成*/
        TreeNode tempNode = rootNode;
        for (int i = 0; i < keyword.length() ; i++){

            /**
             * 得到一个字符*/
            char c=keyword.charAt(i);

            /**
             * 查找子节点*/
            TreeNode subNode = tempNode.getSubNode(c);

            if (subNode == null){
                /**
                 * 初始化子节点*/
                subNode = new TreeNode();
                tempNode.addSubNode(c,subNode);
            }

            /**
             * 指向子节点进行下一轮循环*/
            tempNode = subNode;

            /**
             * 设置结束标识*/
            if (i == keyword.length()-1){
                tempNode.setKeywordEnd(true);
            }
        }
    }

    /**
     * 过滤敏感词
     * @Param text 待过滤的文本
     * @return 过滤后的文本*/

    public String filter(String text){
        if (StringUtils.isBlank(text)){
            return null;
        }

        /**
         * 指针1指向根节点*/
        TreeNode tempNode = rootNode;

        /**
         * 指针2：
         * 索引指针（疑似敏感词开头指针）*/
        int begin = 0;

        /**
         * 指针3：
         * 位置指针*/
        int position = 0;

        /**
         * 过滤结果*/
        StringBuffer stringBuffer = new StringBuffer();

        while (position < text.length()){
            char c = text.charAt(position);

            /**
             * 如果开头为特殊符号，则跳过字符串中间的符号*/
            if (isSymbol(c)){
                /**
                 * 若指针1处于根节点，将此符号计入结果，让指针2向下走一步*/
                if (tempNode == rootNode){
                    stringBuffer.append(c);
                    begin++;
                }
                /**
                 * 无论符号符号在开头或中间，指针3都需要向下走一步*/
                position++;
                continue;
            }

            /**
             * 检查下级节点*/
            tempNode = tempNode.getSubNode(c);
            if (tempNode == null){
                /**
                 * 以begin开头的字符串不是敏感词*/
                stringBuffer.append(text.charAt(begin));
                /**
                 * begin++*/
                position = ++begin;
                //重新指向根节点
                tempNode = rootNode;
            }else if (tempNode.isKeywordEnd()){
                /**
                 * 发现敏感词，将begin--position字符串替换掉*/
                stringBuffer.append(REPLACEMENT);
                /**
                 * position++ */
                begin = ++position;
                //重新指向根节点
                tempNode = rootNode;
            }else {
                //检查下一个字符
                position++;
            }
        }

        //将最后一批字符串计入结果
        stringBuffer.append(text.substring(begin));

        return stringBuffer.toString();
    }

    /**
     * 判断字符串中间是否有特殊符号等*/
    private boolean isSymbol(Character character){
        /**0x2E80~0x9FFF 是符号范围*/
        return !CharUtils.isAsciiAlphanumeric(character) && (character < 0x2E80 || character > 0x9FFF);
    }

    /**
     * 前缀树*/
    private class TreeNode{

        /**
         * 关键词结束标识*/
        private boolean isKeywordEnd = false;

        /**
         * 子节点(key是下级字符，value是下级节点)
         * Character类用于对单个字符进行操作*/
        private Map<Character,TreeNode> subNodes = new HashMap<>();

        private boolean isKeywordEnd(){
            return isKeywordEnd;
        }

        public void setKeywordEnd(boolean keywordEnd){
            isKeywordEnd = keywordEnd;
        }

        /**
         * 添加子节点*/
        public void addSubNode(Character character,TreeNode node){
            subNodes.put(character,node);
        }

        /**
         * 获取子节点*/
        public TreeNode getSubNode(Character character){
            return subNodes.get(character);
        }
    }
}