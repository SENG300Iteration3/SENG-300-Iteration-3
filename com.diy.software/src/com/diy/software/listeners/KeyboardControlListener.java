package com.diy.software.listeners;

import com.diy.software.controllers.KeyboardControl;

public interface KeyboardControlListener {
	
	public void awaitingKeyboardInput(KeyboardControl kc);
	
	// Gives the text entered so far along with the the most recent key pressed
	public void keyboardInputRecieved(KeyboardControl kc, String text, String key);
}