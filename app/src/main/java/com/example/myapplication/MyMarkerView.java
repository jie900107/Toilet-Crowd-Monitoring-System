package com.example.myapplication;

import android.content.Context;
import android.util.Log;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;

public class MyMarkerView extends MarkerView {
    /**
     * Constructor. Sets up the MarkerView with a custom layout resource.
     *
     * @param context
     * @param layoutResource the layout resource to use for the MarkerView
     */
    TextView tvcontent;
    public MyMarkerView(Context context, int layoutResource) {
        super(context, R.layout.custommarkerview);
        tvcontent=findViewById(R.id.tvcontent);
    }

    @Override
    public MPPointF getOffset() {
        Log.i("TAG",getWidth() + " " + getHeight());
        return new MPPointF(-(getWidth()/2),-getHeight());
    }

    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        super.refreshContent(e, highlight);
        if(e.getY()<=25)
            tvcontent.setText(String.valueOf((int) e.getX()) + ":00 ,不繁忙");
        else if(e.getY()<=50)
            tvcontent.setText(String.valueOf((int) e.getX()) + ":00 ,有點繁忙");
        else if(e.getY()<=75)
            tvcontent.setText(String.valueOf((int) e.getX()) + ":00 ,略為繁忙");
        else
            tvcontent.setText(String.valueOf((int) e.getX()) + ":00 ,繁忙");
    }
}
