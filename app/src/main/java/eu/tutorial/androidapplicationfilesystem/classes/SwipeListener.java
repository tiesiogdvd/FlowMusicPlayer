package eu.tutorial.androidapplicationfilesystem.classes;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import eu.tutorial.androidapplicationfilesystem.R;
import eu.tutorial.androidapplicationfilesystem.activities.MainActivity;

public class SwipeListener implements View.OnTouchListener {
    GestureDetector gestureDetector;
    LinearLayout linearLayout;
    public SwipeListener(View view, Context context){
        linearLayout = ((MainActivity)context).findViewById(R.id.bottomNavbar);
        int threshold = 300;
        int velocity_threshold = 200;
        GestureDetector.SimpleOnGestureListener listener = new GestureDetector.SimpleOnGestureListener(){

            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

                float xDifference = e2.getX()-e1.getX();
                float yDifference = e2.getY()-e1.getY();

                try{
                    if(Math.abs(xDifference)>Math.abs(yDifference)){
                        if(Math.abs(xDifference)>threshold && Math.abs(velocityX)>velocity_threshold){
                            if(xDifference>0){
                                System.out.println("Swiped right");
                            }else{
                                System.out.println("Swiped left");
                            }
                            return true;
                        }
                    }else{
                        if(Math.abs(yDifference)>threshold && Math.abs(velocityY) > velocity_threshold){
                            if(yDifference>0){
                                System.out.println("Swiped down");
                                linearLayout.setVisibility(View.GONE);
                            }else {

                                System.out.println("Swiped up");
                                linearLayout.setVisibility(View.VISIBLE);
                            }
                            return true;
                        }
                    }
                }catch(Exception e){}
                return false;
            }
        };
        gestureDetector = new GestureDetector(context,listener);
        view.setOnTouchListener(this);

    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }


}