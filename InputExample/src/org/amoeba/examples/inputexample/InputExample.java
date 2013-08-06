package org.amoeba.examples.inputexample;

import org.amoeba.activity.GameActivity;
import org.amoeba.engine.service.input.InputEvent;
import org.amoeba.entity.sprite.TextSprite;
import org.amoeba.graphics.camera.Camera;
import org.amoeba.graphics.texture.TextOptions;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.opengl.GLES20;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class InputExample extends GameActivity
{
	private static final String TAG = "AmoebaEngine.InputExample";

    private List<TextSprite> information;
    private Queue<TextSprite> eventList;
    private TextOptions eventListOptions;
    private int screenWidth = 0;
    private int screenHeight = 0;

    private TextSprite newEvent = null;

    @Override
    public void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        information = new ArrayList<TextSprite>();
        eventList = new LinkedList<TextSprite>();

        TextOptions informationOptions = new TextOptions(32, Color.BLACK, Align.CENTER, Typeface.DEFAULT, true);
        information.add(getGraphicsService().getTextFactory().createTextSprite(
            "Touch the screen to see data", informationOptions));
        information.add(getGraphicsService().getTextFactory().createTextSprite(
            "on your inputs. Use the HOME", informationOptions));
        information.add(getGraphicsService().getTextFactory().createTextSprite(
            "key to exit the application.", informationOptions));

        eventListOptions = new TextOptions(24, Color.BLACK, Align.CENTER, Typeface.DEFAULT, true);

        //newEvent = getGraphicsService().getTextFactory().createTextSprite(
        //    "testing", eventListOptions);
    }

    @Override
    public void onSurfaceChanged(final int width, final int height)
    {
        GLES20.glClearColor(0.0f, 1.0f, 1.0f, 1.0f);

        int informationSpriteHeight = 20;
        for (TextSprite informationText : information) {
            informationText.setPosition(width/2, informationSpriteHeight);
            informationSpriteHeight += informationText.getHeight();
        }
        
        screenWidth = width;
        screenHeight = height;
    }

    @Override
    public void onDraw(final Camera camera)
    {
        super.onDraw(camera);
    }

    @Override
	public void onInputEvent(final InputEvent event)
	{
        MediaPlayer smbCoinSound = MediaPlayer.create(this, R.raw.smw_coin);
        smbCoinSound.start();

        //If we hit our max size of event items to show, remove the top item.
        //if (eventList.size() > 15) {
        //    eventList.poll();    
        //}

        //Add the new event to the end of the queue. Initialize position to the top of the drawning area,
        //MINUS what we are about to adjust manually by shifting the entire queue.
        //TextSprite newEvent = getGraphicsService().getTextFactory().createTextSprite(
        //    event.getEventType().toString(), eventListOptions);
        newEvent = getGraphicsService().getTextFactory().createTextSprite(
            "test", eventListOptions);
        //newEvent.setPosition(screenWidth/2, screenHeight/4 - newEvent.getHeight());
        //eventList.add(newEvent);

        //Shift the entire queue downwards.
       // for (TextSprite queuedEvent : eventList) {
            //queuedEvent.setPosition(queuedEvent.getPosition().getX(),
            //    queuedEvent.getPosition().getY() + queuedEvent.getHeight());
        //}
	}

    @Override
    public void onBackPressed() {
        MediaPlayer smbCoinSound = MediaPlayer.create(this, R.raw.smw_coin);
        smbCoinSound.start();
    }
    
}
