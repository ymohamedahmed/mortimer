package ui.controllers;

import javafx.scene.paint.Color;

// Enumeration for the board colour
// Stores the two colours used in theme
// Classic theme is default $\label{code:BoardColour}$
public enum BoardColour {
	CLASSIC, MOSS_GREEN, GREY;
	public String getColourName() {
		if (this == BoardColour.CLASSIC) {
			return "Classic";
		} else if (this == BoardColour.MOSS_GREEN) {
			return "Moss Green";
		} else if (this == BoardColour.GREY) {
			return "Grey";
		} else {
			return "";
		}
	}

	// Colour for the half the squares
	public Color getColourPrimary() {
		if (this == BoardColour.CLASSIC) {
			return Color.rgb(140, 82, 66);
		} else if (this == BoardColour.MOSS_GREEN) {
			return Color.rgb(175, 212, 144);
		} else if (this == BoardColour.GREY) {
			return Color.rgb(167, 171, 164);
		} else {
			return BoardColour.CLASSIC.getColourPrimary();
		}
	}

	// Colour for the other half of the squares
	public Color getColourSecondary() {
		if (this == BoardColour.CLASSIC) {
			return Color.rgb(255, 255, 206);
		} else if (this == BoardColour.MOSS_GREEN) {
			return Color.rgb(255, 255, 255);
		} else if (this == BoardColour.GREY) {
			return Color.rgb(255, 255, 255);
		} else {
			return BoardColour.CLASSIC.getColourSecondary();
		}
	}

}