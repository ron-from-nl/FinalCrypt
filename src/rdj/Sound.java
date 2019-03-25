/*
 * Copyright © 2017 Ron de Jong (ronuitzaandam@gmail.com).
 * 
 * This is free software; you can redistribute it 
 * under the terms of the Creative Commons License
 * Creative Commons License: (CC BY-NC-ND 4.0) as published by
 * https://creativecommons.org/licenses/by-nc-nd/4.0/ either
 * version 4.0 of the License, or (at your option) any later version.
 * 
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the 
 * Creative Commons Attribution-NonCommercial-NoDerivatives 4.0
 * International Public License for more details.
 * 
 * You should have received a copy called: "LICENSE" of the 
 * Creative Commons Public License along with this software;
 */
package rdj;

import javafx.scene.media.*;

public class Sound
{
    public final Media ALERT_SND =		new Media(getClass().getResource("/rdj/sounds/alert.mp3").toExternalForm());
    public final Media BUTTON_SND =		new Media(getClass().getResource("/rdj/sounds/button.mp3").toExternalForm());
    public final Media ENCRYPTFILES_SND =	new Media(getClass().getResource("/rdj/sounds/encryptfiles.mp3").toExternalForm());
    public final Media ERROR_SND =		new Media(getClass().getResource("/rdj/sounds/error.mp3").toExternalForm());
    public final Media MESSAGE_SND =		new Media(getClass().getResource("/rdj/sounds/message.mp3").toExternalForm());
    public final Media OFF_SND =		new Media(getClass().getResource("/rdj/sounds/off.mp3").toExternalForm());
    public final Media ON_SND =			new Media(getClass().getResource("/rdj/sounds/on.mp3").toExternalForm());
    public final Media SCANFILES_SND =		new Media(getClass().getResource("/rdj/sounds/scanfiles.mp3").toExternalForm());
    public final Media SELECTFILES_SND =	new Media(getClass().getResource("/rdj/sounds/selectfiles.mp3").toExternalForm());
    public final Media SELECTKEY_SND =		new Media(getClass().getResource("/rdj/sounds/selectkey.mp3").toExternalForm());
    public final Media SHUTDOWN_SND =		new Media(getClass().getResource("/rdj/sounds/shutdown.mp3").toExternalForm());
    public final Media STARTUP_SND =		new Media(getClass().getResource("/rdj/sounds/startup.mp3").toExternalForm());
    public final Media TYPEWRITER_SND =		new Media(getClass().getResource("/rdj/sounds/typewriter.mp3").toExternalForm());
    public final Media WRONGPASSWORD_SND =	new Media(getClass().getResource("/rdj/sounds/wrongpassword.mp3").toExternalForm());
//    private AudioClip player;
    
//    public Sound() { }
//    public void play(Media media) { player = new AudioClip(media.getSource()); player.play(); }
}
