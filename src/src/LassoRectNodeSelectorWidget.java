package src;
import java.util.ArrayList;

public class LassoRectNodeSelectorWidget extends PopupWidget {

	private Network network = null;
	private GraphicsWrapper gw = null;
	private int x0 = 0, y0 = 0, mouse_x, mouse_y;

	private ArrayList<Point2D> lassoPoints = new ArrayList<Point2D>();

	private float lengthOfStrokeMeasuredAlongStroke;

	private static final int LASSO_ONLY_MODE = 0;
	private static final int RECT_ONLY_MODE = 1;
	private static final int LASSO_OR_RECT_MODE = 2;
	private int mode;

	public void initialize( Network net, GraphicsWrapper gw ) {
		network = net;
		this.gw = gw;
		lassoPoints.clear();
		lengthOfStrokeMeasuredAlongStroke = 0;
		mode = LASSO_OR_RECT_MODE;
	}

	// returns list of instances of Node
	public ArrayList<Node> getSetOfSpecifiedNodes() {
		ArrayList<Node> v = new ArrayList<Node>();

		ArrayList<Node> nodes = network.getNodes();
		if ( mode == LASSO_ONLY_MODE ) {
			// We assume the user wanted to perform lasso selection.

			// First, find a bounding box on the lasso
			Point2D point = lassoPoints.get(0);
			int minx = Math.round( point.x() );
			int maxx = Math.round( point.x() );
			int miny = Math.round( point.y() );
			int maxy = Math.round( point.y() );
			int i;
			for (i = 0; i < lassoPoints.size(); ++i ) {
				point = lassoPoints.get(i);
				if ( point.x() < minx ) minx = Math.round( point.x() );
				else if ( point.x() > maxx ) maxx = Math.round( point.x() );
				if ( point.y() < miny ) miny = Math.round( point.y() );
				else if ( point.y() > maxy ) maxy = Math.round( point.y() );
			}

			// Next, for each node, see if it is in the lasso
			for ( Node n : nodes ) {
				Point2D nodePositionInPixels = gw.convertWorldSpaceUnitsToPixels( new Point2D(n.x,n.y) );
				if (
					minx <= nodePositionInPixels.x() && nodePositionInPixels.x() <= maxx
					&& miny <= nodePositionInPixels.y() && nodePositionInPixels.y() <= maxy
					&& Point2DUtil.isPointInsidePolygon( lassoPoints, nodePositionInPixels )
				) {
					v.add(n);
				}
			}
		}
		else {
			// We assume the user wanted to perform rectangle selection.

			float right = gw.convertPixelsToWorldSpaceUnitsX( Math.max(x0, mouse_x) );
			float left = gw.convertPixelsToWorldSpaceUnitsX( Math.min(x0, mouse_x) );
			float bottom = gw.convertPixelsToWorldSpaceUnitsY( Math.max(y0, mouse_y) );
			float top = gw.convertPixelsToWorldSpaceUnitsY( Math.min(y0, mouse_y) );
			for ( Node n : nodes ) {
				if ((n.x >= left) && (n.x <= right) && (n.y >= top) && (n.y <= bottom)) {
					v.add(n);
				}
			}
		}

		return v;

	}

	// Returns a status code.
	public int pressEvent( int x, int y ) {
		x0 = mouse_x = x;
		y0 = mouse_y = y;
		lassoPoints.add( new Point2D( x0, y0 ) );
		isEngaged = true;
		return S_REDRAW;
	}
	public int moveEvent( int x, int y ) {
		mouse_x = x;
		mouse_y = y;
		return S_DONT_REDRAW;
	}
	public int dragEvent( int x, int y ) {
		mouse_x = x;
		mouse_y = y;
		lassoPoints.add( new Point2D( mouse_x, mouse_y ) );
		Point2D firstPoint = lassoPoints.get(0);
		Point2D lastPoint = lassoPoints.get(lassoPoints.size()-1);
		Point2D secondLastPoint = lassoPoints.get(lassoPoints.size()-2);
		lengthOfStrokeMeasuredAlongStroke += lastPoint.distance( secondLastPoint );
		if ( mode == LASSO_OR_RECT_MODE ) {
			// The meaning of the input stroke is still open to interpretation.
			// See if we can resolve the ambiguity at this point.

			float straightLineDistanceFromStartToEndOfStroke
				= lastPoint.distance(firstPoint);
			if (
				straightLineDistanceFromStartToEndOfStroke > 0
				&& lengthOfStrokeMeasuredAlongStroke / straightLineDistanceFromStartToEndOfStroke > 2.5f
			) {
				// The stroke looks more circular than straight,
				// so we'll assume the user wants to draw a lasso,
				// and not a rectangle.
				mode = LASSO_ONLY_MODE;
			}
		}

		return S_DONT_REDRAW;
	}
	public int releaseEvent( int x, int y ) {
		isEngaged = false;
		if ( mode == LASSO_OR_RECT_MODE ) {
			// We assume the user wanted to perform rectangle selection.
			mode = RECT_ONLY_MODE;
		}
		return S_REDRAW;
	}
	public void draw(
		GraphicsWrapper gw
	) {
		// draw stuff
		if ( mode != RECT_ONLY_MODE ) {
			// draw the lasso

			// first, draw a black outline
			gw.setLineWidth( 4 );
			gw.setColor( 0, 0, 0, mode == LASSO_ONLY_MODE ? 1 : 0.75f );
			gw.drawPolyline( lassoPoints, mode == LASSO_ONLY_MODE, false );

			// now, draw the inside
			gw.setLineWidth( 2 );
			gw.setColor( 1.0f, 0.0f, 0.0f, mode == LASSO_ONLY_MODE ? 1 : 0.75f );
			gw.drawPolyline( lassoPoints, mode == LASSO_ONLY_MODE, false );
			gw.setLineWidth( 1 );
		}
		if ( mode != LASSO_ONLY_MODE ) {
			// draw the rectangle

			// first, draw a black outline
			gw.setLineWidth( 4 );
			gw.setColor( 0, 0, 0, 1 );
			gw.drawRect(
				Math.min(x0,mouse_x), Math.min(y0,mouse_y),
				Math.abs(mouse_x-x0), Math.abs(mouse_y-y0)
			);

			gw.setLineWidth( 2 );
			gw.setColor( 1.0f, 0.0f, 0.0f, 1 );
			gw.drawRect(
				Math.min(x0,mouse_x), Math.min(y0,mouse_y),
				Math.abs(mouse_x-x0), Math.abs(mouse_y-y0)
			);
			gw.setLineWidth( 1 );
		}
	}

}

