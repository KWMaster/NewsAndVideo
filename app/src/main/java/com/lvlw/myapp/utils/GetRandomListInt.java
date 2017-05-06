package com.lvlw.myapp.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Wantrer on 2017/4/11 0011.
 */

public class GetRandomListInt {
    private List list;

    public GetRandomListInt(List list) {
        this.list = list;
    }

    public List<Integer> getRandom(List<Integer> carouseTitles){
        List<Integer> pictitles=new ArrayList<>();
        int r1= (int) ((list.size()-1)*Math.random());
        if (carouseTitles.size()==0){
            int r2= (int) ((list.size()-1)*Math.random());
            if (r2!=r1){
                int r3= (int) ((list.size()-1)*Math.random());
                if (r3!=r1&&r3!=r2){
                    pictitles.add(r1);
                    pictitles.add(r2);
                    pictitles.add(r3);
                }else {
                    getRandom(carouseTitles);
                }
            }else {
                getRandom(carouseTitles);
            }
        }else if (carouseTitles.size()==1){
            if (r1!=carouseTitles.get(0)){
                int r2= (int) ((list.size()-1)*Math.random());
                if (r2!=r1&&r2!=carouseTitles.get(0)){
                    pictitles.add(r1);
                    pictitles.add(r2);
                }else {
                    getRandom(carouseTitles);
                }
            }else {
                getRandom(carouseTitles);
            }
        }else if (carouseTitles.size()==2){
            if (r1!=carouseTitles.get(0)&&r1!=carouseTitles.get(1)){
                pictitles.add(r1);
            }else {
                getRandom(carouseTitles);
            }
        }
        return pictitles;
    }
}
