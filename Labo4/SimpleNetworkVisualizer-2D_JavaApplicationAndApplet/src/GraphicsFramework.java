
//Fichier java GraphicsFramework applet
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;



import java.applet.Applet;
import java.awt.Image;
import java.awt.Graphics;








public class GraphicsFramework
	extends Applet
	implements KeyListener, MouseListener, MouseMotionListener
{
	private GraphicsWrapper gw = new GraphicsWrapper();
	private SimpleNetworkVisualizer client = null;

	private String programName = null;
	private String authorInfo = null;
	private int preferredWidth, preferredHeight;
	private int width, height;

	private Image backbuffer;
	private Graphics backg;
	private boolean _painted = false;
	private boolean _isBackbufferDirty = true;

	public void setProgramName( String name ) {
		programName = name;
	}
	public String getProgramName() {
		return programName;
	}
	public void setAuthorInfo( String info ) {
		authorInfo = info;
	}
	public void setPreferredWindowSize( int w, int h ) {
		preferredWidth = w;
		preferredHeight = h;
	}

	private void createClient() {
		client = new SimpleNetworkVisualizer(this,gw);
	}

	public void init() {
		createClient();

		width = getSize().width;
		height = getSize().height;

		backbuffer = createImage( width, height );
		backg = backbuffer.getGraphics();
		gw.set( backg );
		gw.resize(width,height);

		addKeyListener( this );
		addMouseListener( this );
		addMouseMotionListener( this );
	}

	public void start() {
		client.startBackgroundWork();
	}

	public void stop() {
		client.stopBackgroundWork();
	}

	// NOTE: calling e.consume() within these methods
	// could prevent us from receiving key events,
	// so we don't call it.
	//
	public void keyPressed( KeyEvent e ) {
		client.keyPressed(e);
	}
	public void keyReleased( KeyEvent e ) {
		client.keyReleased(e);
	}
	public void keyTyped( KeyEvent e ) {
		client.keyTyped(e);
	}
	public void mouseEntered( MouseEvent e ) {
		client.mouseEntered(e);
	}
	public void mouseExited( MouseEvent e ) {
		client.mouseExited(e);
	}
	public void mouseClicked( MouseEvent e ) {
		client.mouseClicked(e);
	}
	public void mousePressed( MouseEvent e ) {
		client.mousePressed(e);
	}
	public void mouseReleased( MouseEvent e ) {
		client.mouseReleased( e );
	}
	public void mouseMoved( MouseEvent e ) {
		client.mouseMoved( e );
	}
	public void mouseDragged( MouseEvent e ) {
		client.mouseDragged( e );
	}

	public void requestRedraw() {
		_isBackbufferDirty = true;
		if ( _painted ) {
			_painted = false;
			repaint();
		}
	}

	public void update( Graphics g ) {
		if ( _isBackbufferDirty ) {
			client.draw();
			_isBackbufferDirty = false;
		}
		g.drawImage( backbuffer, 0, 0, this );
		_painted = true;
	}

	public void paint( Graphics g ) {
		update( g );
	}

	public String getAppletInfo() {
		if ( programName != null ) {
			if ( authorInfo != null )
				return programName + " written by " + authorInfo;
			else
				return programName;
		}
		else if ( authorInfo != null )
			return "Written by " + authorInfo;
		return "";
	}



}


