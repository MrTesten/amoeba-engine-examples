package org.amoeba.examples.collision;

import java.util.ArrayList;

import org.amoeba.activity.GameActivity;
import org.amoeba.engine.service.input.InputEvent;
import org.amoeba.entity.shape.Rectangle2D;
import org.amoeba.entity.shape.ShapeFactory;

import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;

public class Rectangles extends GameActivity
{
	private ShapeFactory shapeFactory;
	private ArrayList<Rectangle2D> rectangles;
	private int defaultColor;
	private int collidingColor;
	private int defaultWidth, defaultHeight;

	@Override
	protected void onCreate(final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		rectangles = new ArrayList<Rectangle2D>();

		shapeFactory = getGraphicsService().getShapeFactory();
		defaultColor = Color.BLUE;
		collidingColor = Color.RED;

		defaultWidth = 50;
		defaultHeight = 50;
	}

	@Override
	public void onUpdate()
	{
		super.onUpdate();

		checkCollision();
	}

	@Override
	public void onInputEvent(final InputEvent event)
	{
		super.onInputEvent(event);

		if(event.getEventType() == InputEvent.EventType.SINGLETAP)
		{
			MotionEvent touchPoint = event.getMotionEvent();
			createRectangle(touchPoint.getX(), touchPoint.getY());
		}
	}

	private void createRectangle(final float x, final float y)
	{
		Rectangle2D rectangle = shapeFactory.createRectangle(x, y, defaultWidth, defaultHeight);
		rectangle.setColor(defaultColor);
		rectangles.add(rectangle);
	}

	private void checkCollision()
	{
		for(Rectangle2D rectangle1 : rectangles)
		{
			for(Rectangle2D rectangle2 : rectangles)
			{
				if(rectangle1 != rectangle2 && rectangle1.isColliding(rectangle2))
				{
					rectangle1.setColor(collidingColor);
				}
			}
		}
	}
}
