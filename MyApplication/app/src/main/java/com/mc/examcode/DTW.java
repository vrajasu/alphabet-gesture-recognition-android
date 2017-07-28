package com.mc.examcode;

import java.util.List;

/**
 * Created by lenovo on 6/20/2017.
 */

public class DTW {
    List<float[]> sample,template;
    int s,t;
    public DTW(List<float[]> sample,List<float[]> template)
    {
        this.sample=sample;
        this.template=template;
        this.s=sample.size();
        this.t=template.size();
    }
    public float calculateDTW()
    {
        float[][] dtw=new float[s+1][t+1];
        for(int i=1;i<s+1;i++)
        {
            dtw[i][0]=Float.MAX_VALUE;
        }
        for(int i=1;i<t+1;i++)
        {
            dtw[0][i]=Float.MAX_VALUE;
        }
        dtw[0][0]=0;
//        for i := 1 to n
//        for j := 1 to m
//        cost := d(s[i], t[j])
//        DTW[i, j] := cost + minimum(DTW[i-1, j  ],    // insertion
//        DTW[i  , j-1],    // deletion
//        DTW[i-1, j-1])    // match
        for(int i=1;i<s+1;i++)
        {
            for(int j=1;j<t+1;j++)
            {
                float cost= getDistance(sample.get(i-1),template.get(j-1));
                dtw[i][j]= cost + minimum(dtw[i-1][j],dtw[i][j-1],dtw[i-1][j-1]);
            }
        }
        return dtw[s][t];

    }
    public float getDistance(float[] p1, float[] p2)
    {
        return (float) Math.sqrt(Math.pow(p1[0]-p2[0],2)+Math.pow(p1[1]-p2[1],2));
    }
    public float minimum(float a,float b,float c)
    {
        float min=a;
        if(b<a)
            min=b;
        if(c<a)
            min=c;

        return min;
    }
}
