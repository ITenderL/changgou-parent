import org.apache.commons.collections.CollectionUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import sun.security.util.Length;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.TreeSet;

/**
 * @Author: HeWei·Yuan
 * @CreateTime: 2021-04-26 10:56
 * @Description:
 */
@RunWith(SpringRunner.class)
public class test {
    public static void main(String[] args) {
        //String s = "pwwkew";
        //lengthOfLongestSubstring(s);
        //int[] nums1 = {1, 2};
        //int[] nums2 = {3, 4};
        //double result = findMedianSortedArrays(nums1, nums2);
        //System.out.println(result);
        System.out.println(7%5);
    }

    @Test
    public static void lengthOfLongestSubstring(String s) {
        char arr[] = s.toCharArray();
        HashSet<Character> set = new HashSet<>();
        for(int i = 0; i < arr.length; i++) {
            set.add(arr[i]);
        }
        System.out.println(set.size());

    }

    @Test
    public static double findMedianSortedArrays(int[] nums1, int[] nums2) {
        double result = (double)0;
        TreeSet<Integer> set = new TreeSet<>();
        for(int i = 0; i < nums1.length; i++ ) {
            set.add(nums1[i]);
        }
        for(int i = 0; i < nums2.length; i++ ) {
            set.add(nums2[i]);
        }
        ArrayList<Integer> list = new ArrayList<>(set);
        Collections.sort(list);
        if(list.size() == 1) {
            result = list.get(0);
        }else {
            if(list.size()%2 == 0) {
                result = (double)(list.get(list.size()/2 - 1) + list.get(list.size()/2));
            }else{
                result = (double)list.get((int) Math.floor(list.size()/2));
            }
        }
        return result;
    }
}
