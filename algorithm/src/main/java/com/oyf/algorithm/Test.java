package com.oyf.algorithm;

import java.util.Arrays;

public class Test {
    public static void main(String[] args) {
        int str[] = new int[]{9};
        System.out.println(Arrays.toString(str));
        mergeSort(str, 0, str.length - 1);
        //merge(str,0,2,5);
        System.out.println(Arrays.toString(str));
    }

    /**
     * 归并排序1
     * 时间复杂度：   O(nlog2n)
     * max时间复杂度：O(nlog2n)
     * min时间复杂度：O(nlog2n)
     * 空间复杂：    O(n)
     * 稳定较复杂
     *
     * @param str
     * @param start
     * @param end
     */
    public static void mergeSort(int[] str, int start, int end) {
        //取中位数
        int middle = (end + start) / 2;
        if (start < end) {
            mergeSort(str, start, middle);
            mergeSort(str, middle + 1, end);
            merge(str, start, middle, end);
        }
    }

    /**
     * 归并排序2
     *
     * @param arr
     * @param low
     * @param middle
     * @param high
     */
    public static void merge(int arr[], int low, int middle, int high) {
        //创建一个新的数组
        int newStr[] = new int[high - low + 1];
        //前面数据开始得下标
        int i = low;
        //后面数据得下标
        int j = middle + 1;
        //插入得新数组得下标
        int index = 0;
        while (i <= middle && j <= high) {
            //如果前面得数组得数据小，则将前面得数据插入新数组中
            if (arr[i] <= arr[j]) {
                newStr[index] = arr[i];
                i++;
            } else {
                newStr[index] = arr[j];
                j++;
            }
            index++;
        }
        //当其中一个数组循环到最后得时候   退出 ，并且长得数组得数据还没有放到新数组中
        //此时将未取出得数据放到新数组中
        while (i <= middle) {
            newStr[index] = arr[i];
            index++;
            i++;
        }
        while (j <= high) {
            newStr[index] = arr[j];
            index++;
            j++;
        }
        //新数组中已经全是排序好的数据了,将新数组中数据放入原数组
        for (int i1 = 0; i1 < newStr.length; i1++) {
            arr[low + i1] = newStr[i1];
        }
    }

    /**
     * 希尔排序
     * 时间复杂度：   O(nlog2n)
     * max时间复杂度：O(n2)
     * min时间复杂度：O(n1.3)
     * 空间复杂：    O(1)
     * 不稳定 较复杂
     *
     * @param str
     */
    public static void shellSort(int[] str) {
        //所有步长，循环
        for (int d = str.length / 2; d > 0; d = d / 2) {
            //从第一个步长开始，每一个数据向前进行步长为d的插入排序
            for (int i = d; i < str.length; i++) {
                //插入排序，去判断当前的这个j 与j-d去比较
                for (int j = i - d; j >= 0; j = j - d) {
                    if (str[j] > str[j + d]) {
                        int temp = str[j];
                        str[j] = str[j + d];
                        str[j + d] = temp;
                    }
                }
            }
        }
    }


    /**
     * 快速排序
     * 时间复杂度：   O(nlog2n)
     * max时间复杂度：O(n2)
     * min时间复杂度：O(nlog2n)
     * 空间复杂：    O(nlog2n)
     * 不稳定 较复杂
     *
     * @param str
     * @param start
     * @param end
     */
    public static void quickSort(int[] str, int start, int end) {
        if (start >= end) {
            return;
        }
        //找出基准数

        int temp = str[start];
        int low = start;
        int high = end;
        //当low等于high的时候，此时数据已经将大或者小的放入基准数的左右了
        while (low < high) {
            //先判断高位数据是否比基准数大
            //如果大则继续，high--；直到等于low后退出
            while (low < high && temp <= str[high]) {
                high--;
            }
            //如果high小于基准数，那么数据应该要被放入基准数的左边
            //此时str[low]也就是基准数，暂时可以不考虑
            str[low] = str[high];
            //此时则取判断low位的数，low的数小于则继续low++，知道等于high退出
            while (low < high && str[low] <= temp) {
                low++;
            }
            //此时发现low有数据比基准数大，则将数据存入高位
            str[high] = str[low];
        }
        //全部分类完毕后，将基准数放入low位，此时low=high
        str[low] = temp;
        //此时只是将一次的数据用基准数分离开来
        //则需要将基准数前面或后面的的数分别再次进行排序，使用递归

        //排小于基准数
        quickSort(str, start, low);
        //排大于基准数
        quickSort(str, high + 1, end);
    }

    /**
     * 插入排序
     * 时间复杂度：   O(n2)
     * max时间复杂度：O(n2)
     * min时间复杂度：O(n)
     * 空间复杂：    O(1)
     * 稳定	简单
     *
     * @param str
     */
    public static void insertSort(int[] str) {
        for (int i = 1; i < str.length; i++) {
            int temp = str[i];
            int index = i;
            for (int j = i - 1; j >= 0; j--) {
                if (temp < str[j]) {
                    str[j + 1] = str[j];
                    index = j;
                } else {
                    break;
                }
            }
            str[index] = temp;
        }
    }

    /**
     * 选择排序
     * 时间复杂度：   O(n2)
     * max时间复杂度：O(n2)
     * min时间复杂度：O(n2)
     * 空间复杂：    O(1)
     * 不稳定	简单
     *
     * @param str
     */
    public static void selectSort(int[] str) {
        int index = 0;
        int minData;
        for (int i = 0; i < str.length - 1; i++) {
            minData = str[i];
            for (int j = i + 1; j < str.length - 1; j++) {
                if (minData > str[j]) {
                    minData = str[j];
                    index = j;
                }
            }
            if (minData != str[i]) {
                str[index] = str[i];
                str[i] = minData;
            }
        }
    }

    /**
     * 冒泡排序
     * 时间复杂度：   O(n2)
     * max时间复杂度：O(n2)
     * min时间复杂度：O(n)
     * 空间复杂：    O(1)
     * 稳定	简单
     *
     * @param str
     */
    public static void bubbleSort(int[] str) {
        int count = 0;
        int temp;
        boolean flag = false;
        for (int i = 0; i < str.length - 1; i++) {
            for (int j = 0; j < str.length - 1 - i; j++) {
                if (str[j] > str[j + 1]) {
                    flag = true;
                    temp = str[j];
                    str[j] = str[j + 1];
                    str[j + 1] = temp;
                }
                count++;
            }
            if (flag) {
                flag = false;
                continue;
            } else {
                break;
            }
        }
        System.out.println("执行次数=" + count);
    }
}


