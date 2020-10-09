package com.oyf.algorithm;

import java.util.HashMap;

/**
 * @创建者 oyf
 * @创建时间 2020/8/24 16:27
 * @描述
 **/
public class TestLeetcode {

    public static void main(String[] args) {
        String s = "abba";
        int i = lengthOfLongestSubstring(s);
        System.out.println("length=" + i);
    }

    /**
     * 给定一个字符串，请你找出其中不含有重复字符的 最长子串 的长度。
     *
     * @param s
     * @return
     */
    public static int lengthOfLongestSubstring(String s) {
        if (null == s || "".equals(s)) {
            return 0;
        }
        int start = 0;
        int maxLength = 0;
        int end = 0;
        char[] chars = s.toCharArray();
        HashMap<Object, Integer> map = new HashMap<>();
        for (end = 0; end < chars.length; end++) {
            if (map.containsKey(chars[end])) {
                start = Math.max(start, map.get(chars[end]) + 1);
            }
            maxLength = Math.max(maxLength, end - start + 1);
            map.put(chars[end], end);
        }
        return maxLength;
    }

    public ListNode addTwoNumbers(ListNode l1, ListNode l2) {
        ListNode sumNode = new ListNode(0);
        ListNode preNode = sumNode;
        int sum = 0;
        int next = 0;
        while (l1 != null || l2 != null || next != 0) {
            if (l1 != null) {
                sum += l1.val;
                l1 = l1.next;
            }
            if (l2 != null) {
                sum += l2.val;
                l2 = l2.next;
            }
            sum += next;
            next = sum / 10;
            ListNode newNext = new ListNode(sum % 10);
            newNext.next = preNode;
            preNode = newNext;
            sum = 0;
        }
        return sumNode;
    }

    public class ListNode {
        int val;
        ListNode next;

        ListNode(int x) {
            val = x;
        }
    }
}
