/*
 * Copyright © 2017 Ron de Jong (ron@finalcrypt.org)
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

import java.nio.file.*;
import java.util.*;

public class TypeWriter implements UI
{
//    Usage: typewriter.bash "text to write" [min_delay_ms] [max_delay_ms]

    private Version version;
    private UI ui;
    private String text = "This is a test € é";
    private String sound = "20";
    private boolean soundstate;
    private int mindelay = 20;
    private boolean minset = false;
    private int maxdelay = 100;
    private boolean maxset = false;
    private Random random;
    private int seq;
    private int randomDelay;
    
    public TypeWriter()
    {
        this.ui = this;
	text="";
	sound="20";
	random = new Random();
        version = new Version(this);
        version.checkLocalVersion(this);
    }
    
//  Command line parameter setting
    public TypeWriter(String[] args)
    {
	this();
	
	if ( args.length == 0 ) { usage("Error: no parameters",true); }
        for (int p=0; p < args.length; p++)
        {
//          Options
            if      ( args[p].equals("-h"))	{ usage("",false); }
            else if ( args[p].equals("-t"))	{ if (p+1 < args.length) { text = reformat(args[p+1]); p++; } else { usage("Error: param -t \"value\" missing!",true); }  }
            else if ( args[p].equals("-s"))	{ if (p+1 < args.length) { sound = args[p+1]; p++; } else { usage("Error: param -s \"value\" missing!",true); } }
            else if ( args[p].equals("-min"))	{ if (p+1 < args.length) { if (isInt(args[p+1])) { mindelay = Integer.parseInt(args[p+1]); minset = true; } else { usage("Error: param -min " + args[p+1] + " invalid",false); } p++; } else { usage("Error: param -min \"value\" missing!",true); } }
            else if ( args[p].equals("-max"))	{ if (p+1 < args.length) { if (isInt(args[p+1])) { maxdelay = Integer.parseInt(args[p+1]); maxset = true; } else { usage("Error: param -max " + args[p+1] + " invalid",false); } p++; } else { usage("Error: param -max \"value\" missing!",true); } }
            else { usage("Error: invalid parameter: " + args[p] + "\r\n", true); }
        }

	if	((!minset) && (!maxset)) { mindelay = 20; maxdelay = 100; }
	else if ((!minset) && ( maxset)) { mindelay = 1; }
	else if (( minset) && (!maxset)) { maxdelay = mindelay; }

	if (isInt(sound)) { if (Integer.parseInt(sound) >= Audio.soundArray.length) { usage("Error: sound: " + sound + " not available\r\n\r\n" + Audio.getSounds() + "", true); } }
	
	write(sound);
    }

//  Internal parameter settings
    public TypeWriter(String text)							{ this(); this.text = text; write(sound); }
    public TypeWriter(String text, int min_delay_ms)					{ this(); this.text = text; this.mindelay = min_delay_ms; this.maxdelay = 0; write(""); }
    public TypeWriter(String text, int min_delay_ms, int max_delay_ms)			{ this(); this.text = text; this.mindelay = min_delay_ms; this.maxdelay = max_delay_ms; write(""); }
    public TypeWriter(String text, int min_delay_ms, int max_delay_ms, String sound)	{ this(); this.text = text; this.mindelay = min_delay_ms; this.maxdelay = max_delay_ms; write(sound); }
    
    private void delay()
    {
	if (( mindelay > 0 ) && ( maxdelay > mindelay ))
	{
	    randomDelay = (random.nextInt((maxdelay + 1) - mindelay) + mindelay);
	    try { Thread.sleep(randomDelay); } catch (InterruptedException ex) {System.out.println(ex.getMessage());}
	}
	else if ( mindelay > 0)		{ try { Thread.sleep(mindelay); } catch (InterruptedException ex) {System.out.println(ex.getMessage());} }
	else if ( maxdelay > mindelay)	{ try { Thread.sleep(mindelay + 1); } catch (InterruptedException ex) {System.out.println(ex.getMessage());} }
	else				{  }
    }
    
//  sound param is an internal file or external file
    private void write(String soundParam) // "", "num", 
    {
	soundstate = AudioPlayer.sound_Is_Enabled; AudioPlayer.sound_Is_Enabled = true;
	
	String sound = Audio.SND_TYPEWRITE;
	int codec = Audio.AIFF;
	
	if (soundParam.length() == 0)
	{
	    sound = Audio.SND_TYPEWRITE;
	    codec = Audio.AIFF;
	}
	else
	{
	    if (isInt(soundParam)) // sound is a num
	    {
		if (!soundParam.equals("-1"))
		{
		    sound = Audio.getSound(Integer.parseInt(soundParam));
		    codec = Audio.AIFF;
		} else { sound = soundParam; }
	    }
	    else // Sound probably is a file 
	    {
		if (Files.exists(Paths.get(soundParam))) { sound = soundParam; } else { usage("Error: soundfile: " + soundParam + " dos not exist!", true); }
	    }
	}
	
	AudioPlayer player = new AudioPlayer();
	for(seq = 0; seq<text.length(); seq++)
	{
	    System.out.print(text.charAt(seq));
//	    if (text.subSequence(seq, seq+1).equals("\n")) { player.play(this, Audio.SND_ALARM, codec); }
	    if (( text.length() > 0) && ( mindelay + maxdelay != 0) && (!text.subSequence(seq, seq+1).equals("\r"))) { if ( sound.startsWith("/rdj/audio/") ) { player.play(this, sound, codec); } else { player.playAudioClipFile(this, sound); } }
	    if (( mindelay + maxdelay != 0) && (!text.subSequence(seq, seq+1).equals("\r"))) { delay(); }
	}
//	if (( text.length() > 0) && (! text.equals("\n"))) { if ( sound.startsWith("/rdj/audio/") ) { player.play(this, sound, codec); } else { player.playAudioClipFile(this, sound); } }
	if ( mindelay + maxdelay != 0) { delay(); }

	if (( player != null ) && ( player.audioClip != null)) { while ( player.audioClip.isPlaying() ) { try { Thread.sleep(100); } catch (InterruptedException ex) {System.out.println(ex.getMessage());} } }
	
	AudioPlayer.sound_Is_Enabled = soundstate;
    }

    private boolean isInt(String text) { try { Integer.parseInt(text); return true;} catch (NumberFormatException e) { return false; } }
    
    private static String reformat(String text) { return text.replace("\\t", "\t").replace("\\b", "\b").replace("\\n", "\n").replace("\\r", "\r").replace("\\f", "\f"); }
    
    public static void main(String[] args) { new TypeWriter(args); }
    
    protected void usage(String errorMessage, boolean error)
    {
	if ( errorMessage.length() > 0 )
	{
	    log("\r\n", false, true, false, false, false);
	    log(errorMessage + "\r\n", false, true, false, false, false);
	}

	new TypeWriter("\r\n", 0, 0);
	new TypeWriter("Print to screen like a typewriter\r\n");
	new TypeWriter("\r\n", 0, 0);
	new TypeWriter("Usage: java -cp finalcrypt.jar rdj/TypeWriter -t \"text to write\" [-s sound-nr|\"file\"] [-min delay_ms] [-max delay_ms]\r\n",0,0);
	new TypeWriter("\r\n", 0, 0);
	new TypeWriter("Examples\r\n",0,0);
	new TypeWriter("\r\n",0,0);
	new TypeWriter("java -cp finalcrypt.jar rdj/TypeWriter -t \"type at steady pace\" -min 100;\t\t",0,0);new TypeWriter(" # type at steady pace\r\n",100);
	new TypeWriter("java -cp finalcrypt.jar rdj/TypeWriter -t \"type at random pace\" -min 20 -max 100;\t",0,0);new TypeWriter(" # type at random pace\r\n",20,100);
	new TypeWriter("java -cp finalcrypt.jar rdj/TypeWriter -t \"\" -min 1000;\t\t\t\t\t # just pause 1000 ms",0,0); new TypeWriter("",1000); new TypeWriter("\r\n",0);
	new TypeWriter("java -cp finalcrypt.jar rdj/TypeWriter -t \"\\n\" -min 1000 ;\t\t\t\t # newline with pause",0,0); new TypeWriter("\r\n",0,0);
	new TypeWriter("\r\n",0,0);
	new TypeWriter("java -cp finalcrypt.jar rdj/TypeWriter -t \"built-in sound\" -s 8 -min 20 -max 100;\t",0,0);new TypeWriter(" # built-in sound\r\n",20,100,"8");
	new TypeWriter("java -cp finalcrypt.jar rdj/TypeWriter -t \"ext sound file\" -s file.wav -min 20 -max 100;",0,0);new TypeWriter(" # ext sound file\r\n",20,100,"2");
	new TypeWriter("\r\n");
        System.exit(error ? 1 : 0);
    }

    @Override public void test(String message) { log(message, true, true, false, false, false); }
    
    @Override
    synchronized public void log(String message, boolean status, boolean log, boolean logfile, boolean errfile, boolean print)
    {
	if	((!status) && (!log))   {  }
	else if ((!status) && ( log))   { log(message,errfile); }
	else if (( status) && (!log))   {  }
	else if (( status) && ( log))	{ log(message,errfile); }
	if	(logfile)		{ logfile(message); }
	if	(errfile)		{ errfile(message); }
	if	(print)			{ print(message,errfile); }
    }

    public void status(String message)		    {  }
    public void log(String message, boolean err)    { if ( ! err ) { System.out.print(message); } else { System.err.print(message); } }
    public void logfile(String message)		    {  }
    public void errfile(String message)		    {  }
    public void print(String message, boolean err)  { if ( ! err ) { System.out.print(message); } else { System.err.print(message); } }

    @Override
    public void processGraph(int value)
    {
    }

    @Override
    public void processProgress(int filesProgressPercent, int fileProgressPercent, long bytesTotalParam, long bytesProcessedParam, double bytesPerMiliSecondParam)
    {
    }

    @Override
    public void fileProgress()
    {
    }

    @Override
    public void processFinished(FCPathList<FCPath> openFCPathList, boolean open)
    {
    }

    @Override
    public void processStarted()
    {
    }

    @Override
    public void buildReady(FCPathList<FCPath> fcPathListParam, boolean validBuild)
    {
    }
}
