package org.amoeba.examples.entity;

import java.util.ArrayList;
import java.util.List;

import org.amoeba.activity.GameActivity;
import org.amoeba.entity.sprite.TextSprite;
import org.amoeba.graphics.texture.TextOptions;

import android.graphics.Color;
import android.graphics.Paint.Align;
import android.graphics.Typeface;
import android.os.Bundle;

public class TextExample extends GameActivity
{
	private List<TextSprite> textSprites;
	private final static int[] textSizes = {12, 14, 16, 18, 22, 24, 26, 28, 32, 48, 64, 128};

	@Override
	public void onCreate(final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		textSprites = new ArrayList<TextSprite>();
		for (int textSize : textSizes)
		{
			TextOptions options = new TextOptions(textSize, Color.BLUE, Align.CENTER, Typeface.DEFAULT, true);
			textSprites.add(getGraphicsService().getTextFactory().createTextSprite("Text size: " + textSize, options));
		}
	}

	@Override
	public void onSurfaceChanged(final int width, final int height)
	{
		float yPosition = 0.0f;
		for (TextSprite text : textSprites)
		{
			text.setPosition(width / 2, yPosition);
			yPosition += text.getHeight();
		}
	}
}
