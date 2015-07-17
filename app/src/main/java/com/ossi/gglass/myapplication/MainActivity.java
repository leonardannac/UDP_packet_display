package com.ossi.gglass.myapplication;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.google.android.glass.media.Sounds;
import com.google.android.glass.widget.CardBuilder;
import com.google.android.glass.widget.CardScrollAdapter;
import com.google.android.glass.widget.CardScrollView;

import java.io.IOException;
import java.net.BindException;
import java.net.ConnectException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.NoRouteToHostException;
import java.net.PortUnreachableException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * An {@link Activity} showing a tuggable "Hello World!" card.
 * <p/>
 * The main content view is composed of a one-card {@link CardScrollView} that provides tugging
 * feedback to the user when swipe gestures are detected.
 * If your Glassware intends to intercept swipe gestures, you should set the content view directly
 * and use a {@link com.google.android.glass.touchpad.GestureDetector}.
 *
 * @see <a href="https://developers.google.com/glass/develop/gdk/touch">GDK Developer Guide</a>
 */
public class MainActivity extends Activity implements ViewSwitcher.ViewFactory, View.OnClickListener{

//    TextView text;
    private TextSwitcher mSwitcher1, mSwitcher2;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        startThread();

        setContentView(R.layout.activity_main);
        Log.i("setContentView", " ");

        mSwitcher1 = (TextSwitcher) findViewById(R.id.switcher1);
//        mSwitcher2 = (TextSwitcher) findViewById(R.id.switcher2);
        mSwitcher1.setFactory(this);
        mSwitcher1.setText("there is some text here.");
//        mSwitcher2.setFactory(this);

//        mSwitcher1.setFactory(new ViewSwitcher.ViewFactory() {
////            @Override
//            public View makeView() {
//                TextView tv = new TextView(MainActivity.this);
//                tv.setTextSize(20);
//                Log.i("mS1 makeView()"," ");
//                return tv;
//            }
//        });
//        mSwitcher2.setFactory(new ViewSwitcher.ViewFactory() {
////            @Override
//            public View makeView() {
//                TextView tv = new TextView(MainActivity.this);
//                tv.setTextSize(20);
//                Log.i("mS2 makeView()", " ");
//                return tv;
//            }
//        });
        Log.i("mS.setFactory"," ");

    }

    @Override
    protected void onResume() {
//        Log.i("onResume"," ");
        super.onResume();
        //        mCardScroller.activate();
    }

    @Override
    protected void onPause() {
//        mCardScroller.deactivate();
//        Log.i("onPause"," ");
        super.onPause();
    }

//    private void updateResults(){
//        mSwitcher.setText();
//    }

    public View makeView() {
        TextView t = new TextView(this);
        t.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
        t.setTextSize(36);
        return t;
    }

    public void onClick(View v) {
        AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        am.playSoundEffect(Sounds.DISALLOWED);
    }

    public void startThread(){

        Log.i("startThread() called", " ");

        try {
            Log.i("Entered try block.", " ");
            Thread thread = new Thread(new Runnable() {
                private DatagramSocket mSocket = new DatagramSocket(61557, InetAddress.getByName("10.0.0.15")); //Use Glass IP address here
                private DatagramPacket mPacket;

                @Override
                public void run() {

                    Log.i("thread.run.start"," ");

                    while (true) {

                        byte[] buf = new byte[56];
                        mPacket = new DatagramPacket(buf, buf.length);
                        Log.i("mPacket created"," ");

                        try {
                            Thread.sleep(10, 0);
                            Log.i("thread.sleep"," ");
                            mSocket.receive(mPacket);
                            Log.i("mSocket.receive()", " ");

                            final double[] jointDoubleArray = new double[7];
                            final String[] jointStringArray = new String[7];
                            for(int i=0; i<7; i++){
                                jointDoubleArray[i] = ByteBuffer.wrap(mPacket.getData()).order(ByteOrder.LITTLE_ENDIAN).getDouble();
                                jointStringArray[i] = String.valueOf(Math.toRadians(jointDoubleArray[i]));
                            }

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Log.i("Joint 1", jointStringArray[0]);
                                    Log.i("Joint 2", jointStringArray[1]);
                                    Log.i("Joint 3", jointStringArray[2]);
                                    Log.i("Joint 4", jointStringArray[3]);
                                    Log.i("Joint 5", jointStringArray[4]);
                                    Log.i("Joint 6", jointStringArray[5]);
                                    Log.i("Joint 7", jointStringArray[6]);

                                    mSwitcher1.setText(jointStringArray[0]);
//                                    mSwitcher2.setText(jointStringArray[1]);

                                }
                            });

                        } catch (IOException e) {
                            Log.i("IOException ", e.getMessage());
                        } catch (InterruptedException e) {
                            Log.i("InterruptedException ", e.getMessage());
                        }
                    }

                }

            });
            thread.start();
        } catch (BindException e) {
            Log.i("BindEx.",  e.getMessage());
        } catch (ConnectException e) {
            Log.i("ConnectEx.",  e.getMessage());
        } catch (NoRouteToHostException e) {
            Log.i("NoRouteToHostException.",  e.getMessage());
        } catch (PortUnreachableException e) {
            Log.i("PrtUnreachbleException.",  e.getMessage());
        } catch (SocketException e) {
            Log.i("SocketException",  e.getMessage());
        } catch (UnknownHostException e) {
            Log.i("UnknownHostException", e.getMessage());
        }
    }

}
