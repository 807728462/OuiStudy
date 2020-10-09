package com.oyf.algorithm;

/**
 * @创建者 oyf
 * @创建时间 2020/8/19 16:46
 * @描述
 **/
public class TestWrite {
    public static void main(String[] args) {
        System.out.println("--------------------------------");
        int[] str = new int[]{2, 2, 5, 4, 3, 1, 8, 7, 9, 6, 10, 0, 0};
        quickSort(str, 0, str.length);
        for (int i : str) {
            System.out.print(i + "-");
        }
        System.out.println("");
        System.out.println("--------------------------------");
    }

    /**
     * 快速排序
     * 时间复杂度：   O(nlog2n)
     * max时间复杂度：O(n2)
     * min时间复杂度：O(nlog2n)
     * 空间复杂：    O(nlog2n)
     * 不稳定 较复杂
     * 思想，找出一个基准数，然后大的在右边，小的在左边，，然后递归
     *
     * @param str
     * @param start
     * @param end
     */
    public static void quickSort(int[] str, int start, int end) {
        if (start >= end) return;
        int temp = str[start];
        int low = start;
        int high = end - 1;
        while (low < high) {
            while (low < high && temp <= str[high]) {
                high--;
            }
            str[low] = str[high];

            while (low < high && temp >= str[low]) {
                low++;
            }
            str[high] = str[low];
        }
        str[low] = temp;
        quickSort(str, start, low);
        quickSort(str, high + 1, end);
    }

    /**
     * 选择排序
     * 时间复杂度：   O(n2)
     * max时间复杂度：O(n2)
     * min时间复杂度：O(n2)
     * 选出第一个值，然后依次去对比大小，交换基准值，找出最小的
     *
     * @param str
     */
    public static void selectSort(int[] str) {
        for (int i = 0; i < str.length - 1; i++) {
            int inedx = i;
            for (int j = i + 1; j < str.length; j++) {
                if (str[inedx] > str[j]) {
                    inedx = j;
                }
            }
            int temp = str[i];
            str[i] = str[inedx];
            str[inedx] = temp;
        }
    }

    /**
     * 插入排序
     * <p>
     * 时间复杂度：   O(n2)
     * max时间复杂度：O(n2)
     * min时间复杂度：O(n)
     * 每次拿当前值，去跟前面的每一个比大小，如果大，则前面的放在当前位置，继续像前比较，找出合适的位置插入进去
     *
     * @param str
     */
    public static void insertSort(int[] str) {
        for (int i = 1; i < str.length; i++) {
            int temp = str[i];
            for (int j = i - 1; j >= 0; j--) {
                if (temp < str[j]) {
                    str[j + 1] = str[j];
                    str[j] = temp;
                } else {
                    str[j + 1] = temp;
                    break;
                }
            }
        }
    }

    /**
     * 冒泡排序
     * <p>
     * 时间复杂度：   O(n2)
     * max时间复杂度：O(n2)
     * min时间复杂度：O(n)
     * 每次比较相邻的两个，小的放到前面
     *
     * @param str
     */
    public static void bubbleSort(int[] str) {
        int temp;
        for (int i = 0; i < str.length - 1; i++) {
            for (int j = 0; j < str.length - 1 - i; j++) {
                if (str[j] > str[j + 1]) {
                    temp = str[j];
                    str[j] = str[j + 1];
                    str[j + 1] = temp;
                }
            }
        }
    }


}
