package org.amoeba.examples.inputexample;

import org.amoeba.activity.GameActivity;
import org.amoeba.engine.service.input.InputEvent;
import org.amoeba.entity.shape.Rectangle2D;
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
import java.util.List;

public class InputExample extends GameActivity
{
	private static final String TAG = "AmoebaEngine.InputExample";

    private static final int EVENT_LIST_SIZE = 39;
    private static final int POINT_SIZE = 10;
    private int eventCount = 0;

    private List<TextSprite> information;
    private List<TextSprite> eventTypeList;
    private List<Rectangle2D> eventPositionList;
    private TextOptions eventListOptions;

    private TextSprite newEvent = null;

    @Override
    public void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        information = new ArrayList<TextSprite>();
        eventTypeList = new ArrayList<TextSprite>();
        eventPositionList = new ArrayList<Rectangle2D>();

        TextOptions informationOptions = new TextOptions(24, Color.BLACK, Align.RIGHT, Typeface.DEFAULT, true);
        information.add(getGraphicsService().getTextFactory().createTextSprite(
            "Touch the screen to see data", informationOptions));
        information.add(getGraphicsService().getTextFactory().createTextSprite(
            "on your inputs. Use the HOME", informationOptions));
        information.add(getGraphicsService().getTextFactory().createTextSprite(
            "key to exit the application.", informationOptions));

        eventListOptions = new TextOptions(24, Color.BLACK, Align.CENTER, Typeface.DEFAULT, true);
        //Prepopulate the event lists
        for (int count = 0; count < EVENT_LIST_SIZE; ++count) {
            eventTypeList.add(getGraphicsService().getTextFactory().createTextSprite(" " + count, eventListOptions));
            Rectangle2D rectangle = getGraphicsService().getShapeFactory().createRectangle(POINT_SIZE, POINT_SIZE);
            rectangle.setColor(Color.BLACK);
            eventPositionList.add(rectangle);
        }
    }

    @Override
    public void onSurfaceChanged(final int width, final int height)
    {
        GLES20.glClearColor(0.0f, 1.0f, 1.0f, 1.0f);

        int informationSpriteHeight = 20;
        for (TextSprite informationText : information) {
            informationText.setPosition(width*3/5, informationText.getHeight() * information.indexOf(informationText)*3/4 + informationSpriteHeight);
        }

        for (TextSprite event : eventTypeList) {
            event.setPosition(width/5, event.getHeight()/2 + event.getHeight() * eventTypeList.indexOf(event));
        }

        for (Rectangle2D point : eventPositionList) {
            point.setPosition(width - POINT_SIZE, height - POINT_SIZE);
        }
    }

    @Override
    public void onDraw(final Camera camera)
    {
        super.onDraw(camera);
    }

    @Override
	public void onInputEvent(final InputEvent event)
	{
        //Shift the text values downwards (upwards?).
        for (TextSprite printableEvent : eventTypeList) {
            if (!((eventTypeList.indexOf(printableEvent) + 1) >= EVENT_LIST_SIZE)) {
                printableEvent.setText(eventTypeList.get(eventTypeList.indexOf(printableEvent) + 1).getText());
            }
        }

        eventTypeList.get(EVENT_LIST_SIZE - 1).setText(event.getEventType().toString());

        //Increment event counter to cycle through points to adjust.
        if (event.getEventType() == InputEvent.EventType.DOWN ||
            event.getEventType() == InputEvent.EventType.LONGPRESS ||
            event.getEventType() == InputEvent.EventType.SHOWPRESS ||
            event.getEventType() == InputEvent.EventType.SINGLETAP) {
            eventPositionList.get(eventCount % EVENT_LIST_SIZE).setPosition(
                event.getMotionEvent().getX(), event.getMotionEvent().getY());
            ++eventCount;
        }
        else if (event.getEventType() == InputEvent.EventType.FLING ||
            event.getEventType() == InputEvent.EventType.SCROLL) {
            eventPositionList.get(eventCount % EVENT_LIST_SIZE).setPosition(
                event.getEndingEvent().getX(), event.getEndingEvent().getY());
            ++eventCount;
        }
	}

    @Override
    public void onBackPressed() {
        MediaPlayer smbCoinSound = MediaPlayer.create(this, R.raw.smw_coin);
        smbCoinSound.start();
    }
    
}
