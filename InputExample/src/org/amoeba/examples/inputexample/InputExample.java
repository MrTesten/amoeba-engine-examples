package org.amoeba.examples.inputexample;

import org.amoeba.activity.GameActivity;
import org.amoeba.engine.service.input.InputEvent;
import org.amoeba.entity.shape.Rectangle2D;
import org.amoeba.entity.sprite.TextSprite;
import org.amoeba.graphics.camera.Camera;
import org.amoeba.graphics.texture.TextOptions;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.opengl.GLES20;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;

import java.lang.Math;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class InputExample extends GameActivity implements SensorEventListener
{
	private static final String TAG = "AmoebaEngine.InputExample";

    private static final int EVENT_LIST_SIZE = 39;
    private static final int POINT_SIZE = 10;
    private int eventCount = 0;

    private int screenWidth = 0;
    private int screenHeight = 0;

    private List<TextSprite> information;
    private List<TextSprite> eventTypeList;
    private TextSprite eventDetailsLine1;
    private TextSprite eventDetailsLine2;
    private DecimalFormat eventDetailsFormat;
    private List<Rectangle2D> eventPositionList;
    private TextOptions eventListOptions;

    private TextSprite newEvent = null;

    private SensorManager sensorManager;
    private Sensor rotationSensor;
    private List<TextSprite> rotationDetails;
    private Rectangle2D rotationPosition;
    private float[] orientation;
    private float[] rotationMatrix;
    private DecimalFormat rotationDetailsFormat;

    @Override
    public void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        information = new ArrayList<TextSprite>();
        eventTypeList = new ArrayList<TextSprite>();
        eventPositionList = new ArrayList<Rectangle2D>();
        rotationDetails = new ArrayList<TextSprite>();

        orientation = new float[3];
        rotationMatrix = new float[16];

        eventDetailsFormat = new DecimalFormat("0000.00");
        rotationDetailsFormat = new DecimalFormat("000");

        TextOptions informationOptions = new TextOptions(24, Color.BLACK, Align.RIGHT, Typeface.DEFAULT, true);
        information.add(getGraphicsService().getTextFactory().createTextSprite(
            "Touch the screen to see data", informationOptions));
        information.add(getGraphicsService().getTextFactory().createTextSprite(
            "on your inputs. Use the HOME", informationOptions));
        information.add(getGraphicsService().getTextFactory().createTextSprite(
            "key to exit the application.", informationOptions));

        TextOptions detailsOptions = new TextOptions(24, Color.BLACK, Align.LEFT, Typeface.DEFAULT, true);
        eventDetailsLine1 = getGraphicsService().getTextFactory().createTextSprite(" ", detailsOptions);
        eventDetailsLine2 = getGraphicsService().getTextFactory().createTextSprite(" ", detailsOptions);

        eventListOptions = new TextOptions(24, Color.BLACK, Align.CENTER, Typeface.DEFAULT, true);

        //Prepopulate the event lists
        for (int count = 0; count < EVENT_LIST_SIZE; ++count)
        {
            eventTypeList.add(getGraphicsService().getTextFactory().createTextSprite(" ", eventListOptions));
            Rectangle2D rectangle = getGraphicsService().getShapeFactory().createRectangle(POINT_SIZE, POINT_SIZE);
            rectangle.setColor(Color.BLACK);
            eventPositionList.add(rectangle);
        }

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        //Note: The ROTATION_VECTOR "sensor" only works on devices that have a gyroscope. If not present,
        //we'll need to use the ACCELEROMETER and MAGNETIC_FIELD information.
        rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);

        rotationDetails.add(getGraphicsService().getTextFactory().createTextSprite(
            "X (degrees): ", detailsOptions));
        rotationDetails.add(getGraphicsService().getTextFactory().createTextSprite(
            "Y (degrees): ", detailsOptions));
        rotationDetails.add(getGraphicsService().getTextFactory().createTextSprite(
            "Z (degrees): ", detailsOptions));
        rotationDetails.add(getGraphicsService().getTextFactory().createTextSprite(
            " ", detailsOptions));

        rotationPosition = getGraphicsService().getShapeFactory().createRectangle(POINT_SIZE*2, POINT_SIZE*2);
        rotationPosition.setColor(Color.RED);
    }

    @Override
    public void onSurfaceChanged(final int width, final int height)
    {
        GLES20.glClearColor(0.0f, 1.0f, 1.0f, 1.0f);

        int informationSpriteHeight = 20;
        for (TextSprite informationText : information)
        {
            informationText.setPosition(width*3/5, informationText.getHeight() * information.indexOf(informationText)*3/4 + informationSpriteHeight);
        }

        int detailsVerticalPosition = 200;
        eventDetailsLine1.setPosition(width*4/5, detailsVerticalPosition);
        eventDetailsLine2.setPosition(width*4/5, eventDetailsLine1.getPosition().getY() + eventDetailsLine2.getHeight());

        for (TextSprite event : eventTypeList)
        {
            event.setPosition(width/8, event.getHeight()/2 + event.getHeight() * eventTypeList.indexOf(event));
        }

        for (Rectangle2D point : eventPositionList)
        {
            point.setPosition(width - POINT_SIZE, height - POINT_SIZE);
        }

        for (TextSprite rotationText : rotationDetails)
        {
            rotationText.setPosition(width*2/5, rotationText.getHeight() * rotationDetails.indexOf(rotationText)*3/4 + detailsVerticalPosition);
        }

        rotationPosition.setPosition(width/2, height/2);

        screenWidth = width;
        screenHeight = height;
    }

    @Override
	public void onInputEvent(final InputEvent event)
	{
        //Shift the text values downwards (upwards?).
        for (TextSprite printableEvent : eventTypeList)
        {
            if (!((eventTypeList.indexOf(printableEvent) + 1) >= EVENT_LIST_SIZE)) {
                printableEvent.setText(eventTypeList.get(eventTypeList.indexOf(printableEvent) + 1).getText());
            }
        }

        eventTypeList.get(EVENT_LIST_SIZE - 1).setText(event.getEventType().toString());

        //Increment event counter to cycle through points to adjust.
        if (event.getEventType() == InputEvent.EventType.DOWN ||
            event.getEventType() == InputEvent.EventType.LONGPRESS ||
            event.getEventType() == InputEvent.EventType.SHOWPRESS ||
            event.getEventType() == InputEvent.EventType.SINGLETAP)
        {
            eventPositionList.get(eventCount % EVENT_LIST_SIZE).setPosition(
                event.getMotionEvent().getX(), event.getMotionEvent().getY());

            //Update the details with the current event informaton.
            eventDetailsLine1.setText("X: " + eventDetailsFormat.format(event.getMotionEvent().getX()));
            eventDetailsLine2.setText("Y: " + eventDetailsFormat.format(event.getMotionEvent().getY()));

            ++eventCount;
        }
        else if (event.getEventType() == InputEvent.EventType.FLING ||
            event.getEventType() == InputEvent.EventType.SCROLL)
        {
            eventPositionList.get(eventCount % EVENT_LIST_SIZE).setPosition(
                event.getEndingEvent().getX(), event.getEndingEvent().getY());

            //Update the details with the current event informaton.
            eventDetailsLine1.setText("X: " + eventDetailsFormat.format(event.getEndingEvent().getX()));
            eventDetailsLine2.setText("Y: " + eventDetailsFormat.format(event.getEndingEvent().getY()));

            ++eventCount;
        }
	}

    @Override
    public void onBackPressed()
    {
        MediaPlayer smbCoinSound = MediaPlayer.create(this, R.raw.smw_coin);
        smbCoinSound.start();
    }

    @Override
    public final void onAccuracyChanged(final Sensor sensor, final int accuracy)
    {
        //Nothing to do.
    }

    @Override
    public final void onSensorChanged(final SensorEvent sensorEvent)
    {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR)
        {
            SensorManager.getRotationMatrixFromVector(rotationMatrix, sensorEvent.values);
            SensorManager.getOrientation(rotationMatrix, orientation);

            convertOrientationRadiansToDegrees(orientation);

            rotationDetails.get(0).setText("Azimuth (Z): " + rotationDetailsFormat.format(orientation[0]));
            rotationDetails.get(1).setText("Pitch (X): " + rotationDetailsFormat.format(orientation[1]));
            rotationDetails.get(2).setText("Roll (Y): " + rotationDetailsFormat.format(orientation[2]));

            interpretOrientation(orientation);
            adjustRotationPosition(orientation);
        }
    }

    private void convertOrientationRadiansToDegrees(float[] orientation)
    {
        orientation[0] = (float) Math.toDegrees(orientation[0]);
        orientation[1] = (float) Math.toDegrees(orientation[1]);
        orientation[2] = (float) Math.toDegrees(orientation[2]);
    }

    private void interpretOrientation(final float[] orientation)
    {
        String interpretedHorizontalOrientation = "NEUTRAL";
        String interpretedVerticalOrientation = "NEUTRAL";
        final int THRESHOLD = 5;

        //Determine "left/right" from the roll.
        if (Math.abs(orientation[2]) > THRESHOLD)
        {
            if (orientation[2] > 0)
            {
                interpretedHorizontalOrientation = "RIGHT";
            }
            else
            {
                interpretedHorizontalOrientation = "LEFT";
            }
        }

        //Determine "up/down" from the pitch.
        if (Math.abs(orientation[1]) > THRESHOLD)
        {
            if (orientation[1] > 0)
            {
                interpretedVerticalOrientation = "UP";
            }
            else
            {
                interpretedVerticalOrientation = "DOWN";
            }
        }

        rotationDetails.get(3).setText(interpretedHorizontalOrientation + "/" +
            interpretedVerticalOrientation);
    }

    private void adjustRotationPosition(final float[] orientation)
    {
        float newX = rotationPosition.getPosition().getX() + orientation[2]/2f;
        newX = Math.min(screenWidth, Math.max(0, newX));

        float newY = rotationPosition.getPosition().getY() - orientation[1]/2f;
        newY = Math.min(screenHeight, Math.max(0, newY));

        rotationPosition.setPosition(newX, newY);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (rotationSensor != null)
        {
            sensorManager.registerListener(this, rotationSensor, SensorManager.SENSOR_DELAY_FASTEST);
        }
    }

    @Override
    public void onPause()
    {
        super.onPause();
        if (rotationSensor != null)
        {
            sensorManager.unregisterListener(this);
        }
    }
}
