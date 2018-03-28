
public class PopupWidget {

	// These are status codes returned to a client.
	public static final int S_ERROR = 0;
	public static final int S_DONT_REDRAW = 1; // status is okay, but no need to redraw
	public static final int S_REDRAW = 2; // status is okay, and please redraw

	protected boolean isEngaged = false;

	public boolean IsEngaged() { return isEngaged; }
	public void forceDisengagement() { isEngaged = false; }

	// Each of these returns a status code.
	public int pressEvent( int x, int y ) { return S_DONT_REDRAW; }
	public int moveEvent( int x, int y ) { return S_DONT_REDRAW; }
	public int dragEvent( int x, int y ) { return S_DONT_REDRAW; }
	public int releaseEvent( int x, int y ) { return S_DONT_REDRAW; }
	public void draw( GraphicsWrapper gw ) { }
}
