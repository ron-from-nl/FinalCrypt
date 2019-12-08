package rdj;

public class Message
{
    protected String message;
    protected int fontsize;
    protected boolean bottomleft;
    protected boolean topleft;
    protected boolean topright;
    protected boolean bottomright;
    protected String audio;
    protected int media_Delay;

    public Message(String message, int fontsize, boolean bottomleft, boolean topleft, boolean topright, boolean bottomright, String audio, int media_Delay)
    {
	this.message = message;
	this.fontsize = fontsize;
	this.bottomleft = bottomleft;
	this.topleft = topleft;
	this.topright=topright;
	this.bottomright = bottomright;
	this.audio = audio;
	this.media_Delay = media_Delay;
    }
}
