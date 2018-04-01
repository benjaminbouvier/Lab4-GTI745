
import java.awt.Color;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
//import java.lang.Math;
import javax.swing.SwingUtilities;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Iterator;
import java.util.Random;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.io.InputStream;
import java.io.StreamTokenizer;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.URL;


public class SimpleNetworkVisualizer implements Runnable {

	public GraphicsFramework graphicsFramework = null;
	public GraphicsWrapper gw = null;

	Thread thread = null;
	boolean threadSuspended;

	private Network network = null;
	private Node nodeUnderMouseCursor = null;
	private LassoRectNodeSelectorWidget lassoRectNodeSelectorWidget = new LassoRectNodeSelectorWidget();
	private RadialMenuWidget radialMenu = new RadialMenuWidget();

	// The M_ prefix means "mode"
	private static final int M_NEUTRAL = 0;
	private static final int M_MOVING_NODE = 1;
	private static final int M_MOVING_SELECTION = 2;
	private static final int M_PAN = 3;
	private static final int M_ZOOM = 4;
	private int currentMode = M_NEUTRAL;

	// The C_ prefix means "command"
	private static final int C_TOGGLE_NODE_SELECTION = 0;
	private static final int C_TOGGLE_NODE_FIXED = 1;
	private static final int C_CONCENTRIC_LAYOUT = 2;
	private static final int C_TOGGLE_LABELS_OF_SELECTED_NODES = 3;
	private static final int C_TOGGLE_FORCE_SIMULATION = 4;
	private static final int C_FRAME_SELECTED_NODES = 5;
	private static final int C_FRAME_NETWORK = 6;
	private static final int C_TOGGLE_GENRE_BOXES = 7;


	ArrayList< Point2D > polygonDrawnAroundSelection = new ArrayList< Point2D >(); // coordinates stored in world space
	ArrayList<ArrayList< Point2D >> polygonDrawnAroundGenres = new ArrayList<ArrayList< Point2D >>(); // coordinates stored in world space
	private boolean isPolygonUnderMouseCursor = false;

	private int mouse_x, mouse_y, startOfDragX, startOfDragY;
	private boolean isLeftMouseButtonDown = false;
	private boolean isRightMouseButtonDown = false;


	public SimpleNetworkVisualizer( GraphicsFramework gf, GraphicsWrapper gw ) {
		graphicsFramework = gf;
		this.gw = gw;
		graphicsFramework.setProgramName("SimpleNetworkVisualizer");
		graphicsFramework.setAuthorInfo("Michael J. McGuffin, 2009-2011");
		graphicsFramework.setPreferredWindowSize(Constant.INITIAL_WINDOW_WIDTH,Constant.INITIAL_WINDOW_HEIGHT);
		graphicsFramework.setSize(Constant.INITIAL_WINDOW_WIDTH, Constant.INITIAL_WINDOW_HEIGHT);
		gw.setFontHeight( Constant.TEXT_HEIGHT );


		Random random = new Random( 100765 );
		// Next, read or create the network.
		// Do this in one of the ways implemented below; comment the other two ways.
		// FIRST WAY TO GET A NETWORK: generate it randomly
		//network = Network.generateRandomConnectedNetwork( 80, 82, random );
		//network.assignRandomLabelsToAllNodes( random );
		//network.assignRandomColorsToAllNodes( random );
		// SECOND WAY TO GET A NETWORK: create it with hardcoded nodes and edges.
		//generateNetwork( 5 );
		//network.assignRandomLabelsToAllNodes( random );
		//network.assignRandomColorsToAllNodes( random );
		// THIRD WAY TO GET A NETWORK: read it in from a file
		
		readArtistsAsNetwork("data/Artist_influenced_by.txt","data/Artist.txt",1,0,2);


		int w = Constant.INITIAL_WINDOW_WIDTH;
		int h = Constant.INITIAL_WINDOW_HEIGHT;
		float radius = Math.min(w,h)/2.0f * 0.9f;
		network.randomizePositionsOfNodes( null, true, false,
			new AlignedRectangle2D(
				new Point2D(w/2-radius,h/2-radius),
				new Point2D(w/2+radius,h/2+radius)
			)
		);

		network.computeDerivedData();

		radialMenu.setItemLabelAndID( RadialMenuWidget.CENTRAL_ITEM, "", C_TOGGLE_NODE_SELECTION );
		radialMenu.setItemLabelAndID( /* 1 for north */ 1, "Toggle Node Selection", C_TOGGLE_NODE_SELECTION );
		radialMenu.setItemLabelAndID( /* 2 for north-east */ 2, "Toggle Node Fixed", C_TOGGLE_NODE_FIXED );
		radialMenu.setItemLabelAndID( /* 8 for north-west */ 8, "Concentric Layout (radius=3)", C_CONCENTRIC_LAYOUT );
		radialMenu.setItemLabelAndID( 3, "Toggle Labels of Selected Nodes", C_TOGGLE_LABELS_OF_SELECTED_NODES );
		radialMenu.setItemLabelAndID( 4, "Toggle Genre Boxes", C_TOGGLE_GENRE_BOXES );
		radialMenu.setItemLabelAndID( 5, "Toggle Force Simulation", C_TOGGLE_FORCE_SIMULATION );
		radialMenu.setItemLabelAndID( 6, "Frame Selected Nodes", C_FRAME_SELECTED_NODES );
		radialMenu.setItemLabelAndID( 7, "Frame Network", C_FRAME_NETWORK );

	}

	private void generateNetwork( int networkID ) {
		int i;
		switch ( networkID ) {
		case 1:
			network = new Network();
			for ( i = 0; i < 13; ++i )
				network.addNode( new Node() );
			network.addEdge(0,1);
			network.addEdge(0,2);
			network.addEdge(1,3);
			network.addEdge(2,4);
			network.addEdge(4,5);
			network.addEdge(5,3);
			network.addEdge(5,6);
			network.addEdge(6,7);
			network.addEdge(7,8);
			network.addEdge(7,9);
			network.addEdge(7,10);
			network.addEdge(8,5);
			network.addEdge(9,11);
			network.addEdge(10,12);
			network.addEdge(11,12);
			break;
		case 2:
			network = new Network();
			for ( i = 0; i < 12; ++i )
				network.addNode( new Node() );
			network.addEdge(0,1);
			network.addEdge(0,2);
			network.addEdge(0,3);
			network.addEdge(0,4);
			network.addEdge(1,2);
			network.addEdge(1,9);
			network.addEdge(1,10);
			network.addEdge(4,5);
			network.addEdge(5,3);
			network.addEdge(5,6);
			network.addEdge(5,7);
			network.addEdge(7,8);
			network.addEdge(8,6);
			network.addEdge(10,11);
			network.addEdge(11,9);
			break;
		case 3:
			network = new Network();
			for ( i = 0; i < 28; ++i )
				network.addNode( new Node() );
			network.addEdge(0,1);
			network.addEdge(0,2);
			network.addEdge(0,3);
			network.addEdge(0,4);
			network.addEdge(0,5);
			network.addEdge(0,6);
			network.addEdge(0,7);
			network.addEdge(1,2);
			network.addEdge(3,14);
			network.addEdge(3,15);
			network.addEdge(3,16);
			network.addEdge(3,17);
			network.addEdge(4,3);
			network.addEdge(4,24);
			network.addEdge(5,8);
			network.addEdge(5,9);
			network.addEdge(5,10);
			network.addEdge(5,11);
			network.addEdge(5,12);
			network.addEdge(5,13);
			network.addEdge(6,5);
			network.addEdge(9,8);
			network.addEdge(11,10);
			network.addEdge(13,12);
			network.addEdge(16,14);
			network.addEdge(17,15);
			network.addEdge(17,18);
			network.addEdge(17,19);
			network.addEdge(17,20);
			network.addEdge(17,21);
			network.addEdge(17,22);
			network.addEdge(17,23);
			network.addEdge(19,18);
			network.addEdge(21,20);
			network.addEdge(23,22);
			network.addEdge(24,25);
			network.addEdge(25,26);
			network.addEdge(25,27);
			network.addEdge(27,26);
			break;

		case 4:
			network = new Network();
			for ( i = 0; i < 49; ++i )
				network.addNode( new Node() );
			network.addEdge(0,1);
			network.addEdge(0,3);
			network.addEdge(0,4);
			network.addEdge(1,2);
			network.addEdge(1,24);
			network.addEdge(3,11);
			network.addEdge(4,5);
			network.addEdge(4,6);
			network.addEdge(4,7);
			network.addEdge(4,8);
			network.addEdge(4,9);
			network.addEdge(4,10);
			network.addEdge(5,2);
			network.addEdge(5,10);
			network.addEdge(5,9);
			network.addEdge(6,5);
			network.addEdge(7,5);
			network.addEdge(8,5);
			network.addEdge(11,12);
			network.addEdge(11,13);
			network.addEdge(11,14);
			network.addEdge(12,21);
			network.addEdge(12,22);
			network.addEdge(12,23);
			network.addEdge(13,15);
			network.addEdge(13,16);
			network.addEdge(13,17);
			network.addEdge(14,19);
			network.addEdge(14,20);
			network.addEdge(15,18);
			network.addEdge(24,25);
			network.addEdge(24,29);
			network.addEdge(24,40);
			network.addEdge(25,26);
			network.addEdge(25,27);
			network.addEdge(26,28);
			network.addEdge(28,27);
			network.addEdge(29,30);
			network.addEdge(29,31);
			network.addEdge(29,32);
			network.addEdge(32,33);
			network.addEdge(33,31);
			network.addEdge(33,30);
			network.addEdge(33,34);
			network.addEdge(33,35);
			network.addEdge(33,36);
			network.addEdge(33,37);
			network.addEdge(37,38);
			network.addEdge(37,39);
			network.addEdge(38,36);
			network.addEdge(38,34);
			network.addEdge(38,35);
			network.addEdge(39,36);
			network.addEdge(39,34);
			network.addEdge(39,35);
			network.addEdge(40,41);
			network.addEdge(40,42);
			network.addEdge(40,43);
			network.addEdge(40,44);
			network.addEdge(40,45);
			network.addEdge(41,46);
			network.addEdge(43,48);
			network.addEdge(44,48);
			network.addEdge(45,47);
			network.addEdge(45,48);
			network.addEdge(46,44);
			network.addEdge(46,42);
			network.addEdge(46,45);
			network.addEdge(46,43);
			network.addEdge(47,43);
			network.addEdge(47,42);
			network.addEdge(47,41);
			network.addEdge(47,44);
			network.addEdge(48,42);
			network.addEdge(48,41);
			break;

		case 5:
			network = new Network();
			for ( i = 0; i < 130; ++i )
				network.addNode( new Node() );
			network.addEdge(0,1); network.addEdge(0,2); network.addEdge(0,5); network.addEdge(0,9);
			network.addEdge(0,10); network.addEdge(1,3); network.addEdge(1,4); network.addEdge(2,6);
			network.addEdge(2,14); network.addEdge(3,8); network.addEdge(5,7); network.addEdge(5,12);
			network.addEdge(5,21); network.addEdge(7,12); network.addEdge(7,17); network.addEdge(7,19);
			network.addEdge(7,21); network.addEdge(9,11); network.addEdge(9,13); network.addEdge(13,15);
			network.addEdge(13,16); network.addEdge(15,17); network.addEdge(15,18); network.addEdge(15,20);
			network.addEdge(15,56); network.addEdge(16,22); network.addEdge(17,9); network.addEdge(17,70);
			network.addEdge(18,23); network.addEdge(18,25); network.addEdge(23,24); network.addEdge(23,26);
			network.addEdge(23,39); network.addEdge(23,43); network.addEdge(23,47); network.addEdge(24,25);
			network.addEdge(24,67); network.addEdge(25,20); network.addEdge(26,27); network.addEdge(26,37);
			network.addEdge(27,23); network.addEdge(27,28); network.addEdge(27,29); network.addEdge(27,30);
			network.addEdge(28,57); network.addEdge(28,58); network.addEdge(29,34); network.addEdge(30,31);
			network.addEdge(30,32); network.addEdge(30,33); network.addEdge(34,35); network.addEdge(34,36);
			network.addEdge(37,38); network.addEdge(39,40); network.addEdge(40,41); network.addEdge(41,42);
			network.addEdge(43,44); network.addEdge(44,45); network.addEdge(45,46); network.addEdge(47,48);
			network.addEdge(48,49); network.addEdge(49,50); network.addEdge(49,52); network.addEdge(50,51);
			network.addEdge(52,53); network.addEdge(53,54); network.addEdge(54,55); network.addEdge(55,56);
			network.addEdge(58,59); network.addEdge(58,60); network.addEdge(60,61); network.addEdge(60,62);
			network.addEdge(62,63); network.addEdge(63,64); network.addEdge(63,65); network.addEdge(63,66);
			network.addEdge(67,68); network.addEdge(68,69); network.addEdge(70,71); network.addEdge(71,72);
			network.addEdge(72,73); network.addEdge(73,74); network.addEdge(73,75); network.addEdge(73,76);
			network.addEdge(74,81); network.addEdge(74,77); network.addEdge(74,90); network.addEdge(75,79);
			network.addEdge(75,80); network.addEdge(75,76); network.addEdge(75,82); network.addEdge(76,77);
			network.addEdge(76,74); network.addEdge(77,78); network.addEdge(77,81); network.addEdge(78,75);
			network.addEdge(78,79); network.addEdge(81,82); network.addEdge(81,83); network.addEdge(82,77);
			network.addEdge(83,84); network.addEdge(83,85); network.addEdge(85,86); network.addEdge(85,87);
			network.addEdge(87,88); network.addEdge(87,89); network.addEdge(90,91); network.addEdge(91,92);
			network.addEdge(92,93); network.addEdge(92,102); network.addEdge(92,104); network.addEdge(93,94);
			network.addEdge(93,95); network.addEdge(95,96); network.addEdge(96,97); network.addEdge(97,98);
			network.addEdge(98,99); network.addEdge(98,105); network.addEdge(98,107); network.addEdge(99,100);
			network.addEdge(100,101); network.addEdge(102,103); network.addEdge(105,106); network.addEdge(107,108);
			network.addEdge(108,106); network.addEdge(108,109); network.addEdge(108,110); network.addEdge(109,111);
			network.addEdge(111,112); network.addEdge(112,110); network.addEdge(112,113); network.addEdge(112,114);
			network.addEdge(114,115); network.addEdge(114,113); network.addEdge(114,119); network.addEdge(115,116);
			network.addEdge(115,118); network.addEdge(116,113); network.addEdge(116,117); network.addEdge(117,120);
			network.addEdge(120,121); network.addEdge(121,122); network.addEdge(121,124); network.addEdge(122,123);
			network.addEdge(123,117); network.addEdge(124,125); network.addEdge(124,126); network.addEdge(126,127);
			network.addEdge(126,125); network.addEdge(127,125); network.addEdge(127,128); network.addEdge(127,129);
			network.addEdge(128,129);
			break;
		case 6:
			network = new Network();
			for ( i = 0; i < 171; ++i )
				network.addNode( new Node() );
			network.addEdge(0,1); network.addEdge(0,3); network.addEdge(0,13); network.addEdge(1,4);
			network.addEdge(1,5); network.addEdge(1,7); network.addEdge(1,17); network.addEdge(2,168);
			network.addEdge(4,6); network.addEdge(4,9); network.addEdge(4,10); network.addEdge(6,5);
			network.addEdge(6,1); network.addEdge(7,8); network.addEdge(8,1); network.addEdge(10,11);
			network.addEdge(10,12); network.addEdge(14,15); network.addEdge(14,16); network.addEdge(17,18);
			network.addEdge(17,22); network.addEdge(18,19); network.addEdge(19,20); network.addEdge(19,21);
			network.addEdge(19,17); network.addEdge(19,27); network.addEdge(20,21); network.addEdge(20,17);
			network.addEdge(20,18); network.addEdge(20,28); network.addEdge(20,31); network.addEdge(21,17);
			network.addEdge(21,18); network.addEdge(22,23); network.addEdge(23,24); network.addEdge(24,25);
			network.addEdge(24,17); network.addEdge(24,54); network.addEdge(25,26); network.addEdge(25,22);
			network.addEdge(26,17); network.addEdge(26,23); network.addEdge(28,29); network.addEdge(28,30);
			network.addEdge(30,32); network.addEdge(30,33); network.addEdge(31,39); network.addEdge(31,41);
			network.addEdge(34,121); network.addEdge(35,36); network.addEdge(36,37); network.addEdge(36,49);
			network.addEdge(37,38); network.addEdge(37,35); network.addEdge(37,43); network.addEdge(37,48);
			network.addEdge(38,42); network.addEdge(39,40); network.addEdge(43,44); network.addEdge(44,45);
			network.addEdge(45,46); network.addEdge(45,47); network.addEdge(49,50); network.addEdge(49,51);
			network.addEdge(51,52); network.addEdge(51,53); network.addEdge(54,55); network.addEdge(55,56);
			network.addEdge(55,60); network.addEdge(55,62); network.addEdge(55,61); network.addEdge(56,57);
			network.addEdge(57,58); network.addEdge(58,54); network.addEdge(59,60); network.addEdge(59,61);
			network.addEdge(59,62); network.addEdge(61,64); network.addEdge(62,63); network.addEdge(62,103);
			network.addEdge(63,61); network.addEdge(63,60); network.addEdge(64,69); network.addEdge(64,70);
			network.addEdge(65,67); network.addEdge(66,70); network.addEdge(66,67); network.addEdge(66,73);
			network.addEdge(67,68); network.addEdge(68,66); network.addEdge(68,72); network.addEdge(69,65);
			network.addEdge(70,71); network.addEdge(71,64); network.addEdge(71,66); network.addEdge(72,77);
			network.addEdge(72,79); network.addEdge(73,74); network.addEdge(73,76); network.addEdge(74,75);
			network.addEdge(76,80); network.addEdge(77,78); network.addEdge(78,92); network.addEdge(79,87);
			network.addEdge(80,84); network.addEdge(81,17); network.addEdge(81,35); network.addEdge(81,37);
			network.addEdge(81,38); network.addEdge(82,14); network.addEdge(82,1); network.addEdge(82,3);
			network.addEdge(83,85); network.addEdge(84,83); network.addEdge(85,80); network.addEdge(86,79);
			network.addEdge(87,89); network.addEdge(88,86); network.addEdge(89,88); network.addEdge(91,147);
			network.addEdge(92,93); network.addEdge(92,95); network.addEdge(93,94); network.addEdge(95,96);
			network.addEdge(95,98); network.addEdge(96,97); network.addEdge(98,99); network.addEdge(98,100);
			network.addEdge(99,101); network.addEdge(101,102); network.addEdge(102,99); network.addEdge(103,104);
			network.addEdge(103,105); network.addEdge(105,107); network.addEdge(106,108); network.addEdge(106,109);
			network.addEdge(106,113); network.addEdge(106,120); network.addEdge(107,110); network.addEdge(108,111);
			network.addEdge(108,116); network.addEdge(109,105); network.addEdge(109,112); network.addEdge(110,106);
			network.addEdge(111,109); network.addEdge(111,117); network.addEdge(113,114); network.addEdge(113,115);
			network.addEdge(117,118); network.addEdge(117,119); network.addEdge(120,121); network.addEdge(120,125);
			network.addEdge(121,149); network.addEdge(123,124); network.addEdge(123,125); network.addEdge(123,126);
			network.addEdge(124,122); network.addEdge(125,124); network.addEdge(125,129); network.addEdge(125,137);
			network.addEdge(126,122); network.addEdge(127,130); network.addEdge(128,125); network.addEdge(129,131);
			network.addEdge(129,133); network.addEdge(130,128); network.addEdge(131,127); network.addEdge(131,132);
			network.addEdge(133,134); network.addEdge(133,136); network.addEdge(134,135); network.addEdge(137,138);
			network.addEdge(137,139); network.addEdge(139,140); network.addEdge(139,141); network.addEdge(141,142);
			network.addEdge(141,143); network.addEdge(143,144); network.addEdge(143,146); network.addEdge(143,90);
			network.addEdge(144,145); network.addEdge(145,143); network.addEdge(146,90); network.addEdge(147,78);
			network.addEdge(148,34); network.addEdge(148,149); network.addEdge(149,150); network.addEdge(149,151);
			network.addEdge(150,148); network.addEdge(150,159); network.addEdge(150,162); network.addEdge(151,152);
			network.addEdge(151,153); network.addEdge(152,149); network.addEdge(153,154); network.addEdge(153,157);
			network.addEdge(153,158); network.addEdge(154,151); network.addEdge(154,155); network.addEdge(154,156);
			network.addEdge(156,155); network.addEdge(157,158); network.addEdge(159,160); network.addEdge(159,161);
			network.addEdge(162,170); network.addEdge(163,167); network.addEdge(164,162); network.addEdge(165,169);
			network.addEdge(166,163); network.addEdge(167,164); network.addEdge(168,166); network.addEdge(169,2);
			network.addEdge(170,165);
			break;
		default:
			assert false;
			break;



		}
	}

	private void readInTabDelimitedFileAsNetwork(
		String relPath, // Example: "file:///home/..."

		// useful for skipping over a header in the file
		int numLinesToSkip,

		// These should be 0 for first column (i.e. before any tabs), 1 for 2nd (after 1st tab), etc.
		int indexOfColumn1,
		int indexOfColumn2
	) {
		InputStream is = null;
		int lineIndex = -1;
		ArrayList<String> arrayListOfAllNodes = new ArrayList<String>();
		ArrayList<String> arrayListOfFirstNodes = new ArrayList<String>();
		ArrayList<String> arrayListOfSecondNodes = new ArrayList<String>();
		try {
			FileInputStream fstream = new FileInputStream(relPath);
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			//is = new URL( URLOfFile ).openStream();
			//BufferedReader br = new BufferedReader( new InputStreamReader(is) );

			String s;
			while ((s = br.readLine()) != null) {
				++ lineIndex;
				if ( lineIndex < numLinesToSkip ) continue;
				String fields[] = s.split(""+(char)/*tab*/9);

				if ( fields.length > Math.max(indexOfColumn1,indexOfColumn2) ) {
					arrayListOfAllNodes.add(fields[indexOfColumn1]);
					arrayListOfAllNodes.add(fields[indexOfColumn2]);
					arrayListOfFirstNodes.add(fields[indexOfColumn1]);
					arrayListOfSecondNodes.add(fields[indexOfColumn2]);
				}
			}
			is.close();
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}

		//System.out.println("initial size (should be twice number of edges): "+arrayListOfAllNodes.size());
		// Eliminate duplicate strings
		HashSet<String> hashSet = new HashSet(arrayListOfAllNodes);
		arrayListOfAllNodes.clear();
		arrayListOfAllNodes.addAll( hashSet );
		//System.out.println("number of nodes (after eliminating duplicates): "+arrayListOfAllNodes.size());

		// Sort in alphabetical order
		Collections.sort(arrayListOfAllNodes);

		// add nodes to network
		assert network == null;
		network = new Network();
		int i;
		for ( i = 0; i < arrayListOfAllNodes.size(); ++i ) {
			String nodeLabel = arrayListOfAllNodes.get(i);
			network.addNode( new Node( nodeLabel ) );
		}

		String [] arrayOfNodes = arrayListOfAllNodes.toArray( new String[ arrayListOfAllNodes.size() ] );

		// add edges to network
		//System.out.println("size of other array lists (should both be number of edges): "+arrayListOfFirstNodes.size()+","+arrayListOfSecondNodes.size());
		assert arrayListOfFirstNodes.size() == arrayListOfSecondNodes.size();
		for ( i = 0; i < arrayListOfFirstNodes.size(); ++i ) {
			int nodeIndex1 = Arrays.binarySearch( arrayOfNodes, arrayListOfFirstNodes.get(i) );
			int nodeIndex2 = Arrays.binarySearch( arrayOfNodes, arrayListOfSecondNodes.get(i) );
			assert 0 <= nodeIndex1 && nodeIndex1 < network.getNumNodes();
			assert 0 <= nodeIndex2 && nodeIndex2 < network.getNumNodes();
			network.addEdge( nodeIndex1, nodeIndex2 );
		}

	}

	private void readArtistsAsNetwork(
		String linkPath, // Path to the CSV file containing the links between artists
		String artistsPath, // Path to the CSV file containing the Artist informations

		// useful for skipping over a header in the file
		int numLinesToSkip,

		// These should be 0 for first column (i.e. before any tabs), 1 for 2nd (after 1st tab), etc.
		int indexOfColumn1,
		int indexOfColumn2
	) {
		InputStream is = null;
		int lineIndex = -1;
		ArrayList<String> arrayListOfAllNodes = new ArrayList<String>();
		ArrayList<String> arrayListOfFirstNodes = new ArrayList<String>();
		ArrayList<String> arrayListOfSecondNodes = new ArrayList<String>();
		ArrayList<String> arrayListOfGenres = new ArrayList<String>();
		ArrayList<String> extraArtistIds = new ArrayList<String>();
		ArrayList<String> extraArtistNames = new ArrayList<String>();
		ArrayList<ArrayList<String>> artists = new ArrayList<ArrayList<String>>();
		// Reading link file and adding to arrays
		try {
			FileInputStream fstream = new FileInputStream(linkPath);
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			//is = new URL( URLOfFile ).openStream();
			//BufferedReader br = new BufferedReader( new InputStreamReader(is) );

			String s;
			while ((s = br.readLine()) != null) {
				++ lineIndex;
				if ( lineIndex < numLinesToSkip ) continue;
				String fields[] = s.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
				
				for(int i = 0; i < fields.length; i++) {
					fields[i] = fields[i].replace("\"", "");
				}

				if ( fields.length > Math.max(indexOfColumn1,indexOfColumn2)  && !fields[indexOfColumn1].isEmpty() && !fields[indexOfColumn2].isEmpty() ) {
					if(!extraArtistIds.contains(fields[indexOfColumn2])) {
						extraArtistIds.add(fields[indexOfColumn2]);
						extraArtistNames.add(fields[1]);
						
					}
					arrayListOfAllNodes.add(fields[indexOfColumn1]);
					arrayListOfAllNodes.add(fields[indexOfColumn2]);
					arrayListOfFirstNodes.add(fields[indexOfColumn1]);
					arrayListOfSecondNodes.add(fields[indexOfColumn2]);
					arrayListOfGenres.add(fields[3]);
				}
			}
			is.close();
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}
		
		// Reading Artists information and adding to array
		try {
			FileInputStream fstream = new FileInputStream(artistsPath);
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			//is = new URL( URLOfFile ).openStream();
			//BufferedReader br = new BufferedReader( new InputStreamReader(is) );

			String s;
			while ((s = br.readLine()) != null) {
				++ lineIndex;
				ArrayList<String> artist = new ArrayList<String>();
				if ( lineIndex < numLinesToSkip ) continue;
				String fields[] = s.split(",");
				
				for(int i = 0; i < fields.length; i++) {
					fields[i] = fields[i].replace("\"", "");
					artist.add(fields[i]);
				}
				artists.add(artist);
			}
			is.close();
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}

		//System.out.println("initial size (should be twice number of edges): "+arrayListOfAllNodes.size());
		// Eliminate duplicate strings
		HashSet<String> hashSet = new HashSet(arrayListOfAllNodes);
		arrayListOfAllNodes.clear();
		arrayListOfAllNodes.addAll( hashSet );
		//System.out.println("number of nodes (after eliminating duplicates): "+arrayListOfAllNodes.size());

		// Sort in alphabetical order
		Collections.sort(arrayListOfAllNodes);

		// add nodes to network
		assert network == null;
		network = new Network();
		int i;
		for ( i = 0; i < arrayListOfAllNodes.size(); ++i ) {
			String nodeLabel = arrayListOfAllNodes.get(i);
			network.addNode( new Node( nodeLabel ) );
		}
		

		String [] arrayOfNodes = arrayListOfAllNodes.toArray( new String[ arrayListOfAllNodes.size() ] );

		// add edges to network
		//System.out.println("size of other array lists (should both be number of edges): "+arrayListOfFirstNodes.size()+","+arrayListOfSecondNodes.size());
		assert arrayListOfFirstNodes.size() == arrayListOfSecondNodes.size();
		for ( i = 0; i < arrayListOfFirstNodes.size(); ++i ) {
			int nodeIndex1 = Arrays.binarySearch( arrayOfNodes, arrayListOfFirstNodes.get(i) );
			int nodeIndex2 = Arrays.binarySearch( arrayOfNodes, arrayListOfSecondNodes.get(i) );
			assert 0 <= nodeIndex1 && nodeIndex1 < network.getNumNodes();
			assert 0 <= nodeIndex2 && nodeIndex2 < network.getNumNodes();
			network.addEdge( nodeIndex1, nodeIndex2 , arrayListOfGenres.get(i));
		}

		// populate nodes with artist information
		network.populateNodes(artists, extraArtistIds, extraArtistNames);

	}
	
	private void simulateOneStepOfForceDirectedLayout() {
		//
		// The spring "force" that we simulate between nodes that share
		// an edge can be described as
		//
		//    F_S = k ( x - x0 )
		//
		// where
		// F_S is the spring force (in force units),
		// k is the spring constant (in force units per length unit),
		// x is the distance between the nodes (in length units),
		// x0 is the rest length of the spring (in length units).
		//
		// Similarly, the repulsive "force" that we simulate between all
		// pairs of nodes can be described as
		//
		//    F_R = alpha / x^2
		//
		// where
		// F_R is the repulsive force (in force units),
		// alpha is in force units * (length units)^2,
		// x is the distance between the nodes (in length units).
		//
		// We define a dimensionless ratio R that expresses the strength
		// of one force with respect to the other, independent of
		// the particular nodes.  Fixing the value of R should fix
		// the shape that the network converges to, even as the
		// spring rest length is varied.
		//
		//    R = (alpha, in force units * (length units)^2)
		//        / (k, in force units per length unit)
		//        / (x0, in length units)^3
		//
		// Solving for alpha,
		//
		//    alpha = R k x0^3
		//
		// To allow the user to control the ratio,
		// we could give the user direct control over R,
		// and use the above equation to compute alpha.
		//
		final float R = 100f;
		final float k = 0.05f;
		final float alpha = R * k * Constant.SPRING_REST_LENGTH * Constant.SPRING_REST_LENGTH * Constant.SPRING_REST_LENGTH;
		final float timeStep = 0.04f;

		ArrayList<Node> nodes = network.getNodes();

		// initialization
		for ( int i = 0; i < nodes.size(); ++i ) {
			Node n = nodes.get(i);
			n.forceX = 0;
			n.forceY = 0;
		}

		// repulsive force between all pairs of nodes
		for ( int i = 0; i < nodes.size(); ++i ) {
			Node n1 = nodes.get(i);
			for ( int j = i+1; j < nodes.size(); ++j ) {
				Node n2 = nodes.get(j);
				float dx = n2.x - n1.x;
				float dy = n2.y - n1.y;
				if ( dx == 0 && dy == 0 ) {
					dx = (float)Math.random()-0.5f;
					dy = (float)Math.random()-0.5f;
				}
				float distanceSquared = dx*dx + dy*dy;
				float distance = (float)Math.sqrt( distanceSquared );
				float force = alpha / distanceSquared;
				dx *= force / distance;
				dy *= force / distance;

				n1.forceX -= dx;
				n1.forceY -= dy;

				n2.forceX += dx;
				n2.forceY += dy;
			}
		}

		// spring force
		for ( int i = 0; i < nodes.size(); ++i ) {
			Node n1 = nodes.get(i);
			for ( int j = 0; j < n1.neighbours.size(); ++j ) {
				Node n2 = n1.neighbours.get(j);
				int n2_index = network.getIndexOfNode( n2 );
				if ( n2_index < i )
					continue;
				float dx = n2.x - n1.x;
				float dy = n2.y - n1.y;
				float distance = (float)Math.sqrt( dx*dx + dy*dy );
				if ( distance > 0 ) {
					float distanceFromRestLength = distance - Constant.SPRING_REST_LENGTH;
					float force = k * distanceFromRestLength;
					dx *= force / distance;
					dy *= force / distance;

					n1.forceX += dx;
					n1.forceY += dy;

					n2.forceX -= dx;
					n2.forceY -= dy;
				}
			}
		}

		// update positions
		for ( int i = 0; i < nodes.size(); ++i ) {
			Node n = nodes.get(i);

			// Don't change positions of nodes that are fixed in place.
			if ( n.isFixed )
				continue;

			// Don't change positions of nodes
			// that the user is currently moving around.
			if (
				(currentMode == M_MOVING_NODE && n == nodeUnderMouseCursor)
				|| (currentMode == M_MOVING_SELECTION && network.isNodeSelected(n))
			)
				continue;

			float dx = timeStep * n.forceX;
			float dy = timeStep * n.forceY;
			float displacementSquared = dx*dx + dy*dy;
			final float MAX_DISPLACEMENT = 10;
			final float MAX_DISPLACEMENT_SQUARED = MAX_DISPLACEMENT * MAX_DISPLACEMENT;
			if ( displacementSquared > MAX_DISPLACEMENT_SQUARED ) {
				float s = MAX_DISPLACEMENT / (float)Math.sqrt( displacementSquared );
				dx *= s;
				dy *= s;
			}
			n.x += dx;
			n.y += dy;
		}
	}

	// Called by the framework at startup time,
	// and (in the case of an applet) whenever the browser returns to the webpage containing us.
	public void startBackgroundWork() {
		if ( thread == null ) {
			thread = new Thread( this );
			threadSuspended = false;
			thread.start();
		}
		else {
			if ( threadSuspended ) {
				threadSuspended = false;
				synchronized( this ) {
					notify();
				}
			}
		}
	}
	// In the case of an applet,
	// the framework calls this whenever the browser leaves the webpage containing us.
	public void stopBackgroundWork() {
		threadSuspended = true;
	}

	public void run() {
		try {
			while (true) {

				// Here's where the thread does some work
				if ( network.isForceDirectedLayoutActive() ) {
					synchronized(network) {
						simulateOneStepOfForceDirectedLayout();
						computePolygonSurroundingSelectedNodes();
					}
					graphicsFramework.requestRedraw();
				}

				// Now the thread checks to see if it should suspend itself
				if ( threadSuspended ) {
					synchronized( this ) {
						while ( threadSuspended ) {
							wait();
						}
					}
				}
				thread.sleep( 50 );  // interval given in milliseconds
			}
		}
		catch (InterruptedException e) { }
	}

	public void keyPressed( KeyEvent e ) {
	}
	public void keyReleased( KeyEvent e ) {
	}
	public void keyTyped( KeyEvent e ) {
		synchronized(network) {
			char c = e.getKeyChar();
			if ( c != KeyEvent.CHAR_UNDEFINED ) {
				if ( c=='s' ) {
					System.out.println( network.getStatusString() );
				}
			}
		}
	}

	public void mouseEntered( MouseEvent e ) {
	}
	public void mouseExited( MouseEvent e ) {
	}
	public void mouseClicked( MouseEvent e ) {
	}

	private Node findNearestNodeToGivenPixel( int X_inPixels, int Y_inPixels ) {
		float x = gw.convertPixelsToWorldSpaceUnitsX( X_inPixels );
		float y = gw.convertPixelsToWorldSpaceUnitsY( Y_inPixels );
		return network.findNearestNode( x, y, Constant.MIN_DISTANCE_FOR_PICKING );
	}

	private void computePolygonSurroundingSelectedNodes() {
		ArrayList<Node> selectedNodes = network.getSelectedNodes();
		ArrayList<Point2D> nodePoints = new ArrayList<Point2D>();
		for ( Node n : selectedNodes ) {
			nodePoints.add( new Point2D( n.x, n.y ) );
		}
		polygonDrawnAroundSelection = Point2DUtil.computeExpandedPolygon( Point2DUtil.computeConvexHull(nodePoints), Constant.MIN_DISTANCE_FOR_PICKING );
	}
	
	private void computePolygonSurroundingGenres() {
		ArrayList<ArrayList<Node>> genreGroups = network.getGenreGroups();
		ArrayList<Point2D> nodePoints = new ArrayList<Point2D>();
		polygonDrawnAroundGenres.clear();
		
		for(int i = 0 ; i < genreGroups.size(); i++) {
			nodePoints.clear();
			for(int j = 0; j < genreGroups.get(i).size(); j++) {
				Node n = genreGroups.get(i).get(j);
				nodePoints.add( new Point2D( n.x, n.y ) );
			}
			polygonDrawnAroundGenres.add(Point2DUtil.computeExpandedPolygon( Point2DUtil.computeConvexHull(nodePoints), Constant.MIN_DISTANCE_FOR_PICKING ));
		}
		
		
	}

	private void updateHighlighting() {
		// check if the highlighting of the node has changed
		Node n = findNearestNodeToGivenPixel( mouse_x, mouse_y );
		if (n != nodeUnderMouseCursor) {
			nodeUnderMouseCursor = n;
			graphicsFramework.requestRedraw();
		}

		// check if the highlighting of the polygon has changed
		boolean flag = Point2DUtil.isPointInsidePolygon(
			polygonDrawnAroundSelection,
			gw.convertPixelsToWorldSpaceUnits( new Point2D(mouse_x,mouse_y) )
		);
		if ( flag != isPolygonUnderMouseCursor ) {
			isPolygonUnderMouseCursor = flag;
			graphicsFramework.requestRedraw();
		}
	}


	public void mousePressed(MouseEvent e) {
		startOfDragX = mouse_x = e.getX();
		startOfDragY = mouse_y = e.getY();

		if (SwingUtilities.isLeftMouseButton(e)) {
			isLeftMouseButtonDown = true;
		}
		else if (SwingUtilities.isRightMouseButton(e)) {
			isRightMouseButtonDown = true;
		}

		synchronized(network) {
			if (lassoRectNodeSelectorWidget.IsEngaged()) {
				if (lassoRectNodeSelectorWidget.pressEvent(mouse_x,mouse_y)==PopupWidget.S_REDRAW) {
					graphicsFramework.requestRedraw();
				}
			}
			else if (radialMenu.IsEngaged()) {
				if (radialMenu.pressEvent(mouse_x,mouse_y)==PopupWidget.S_REDRAW) {
					graphicsFramework.requestRedraw();
				}
			}
			else if ( currentMode == M_NEUTRAL ) {
				if ( SwingUtilities.isLeftMouseButton(e) ) {
					if ( e.isShiftDown() ) {
						lassoRectNodeSelectorWidget.initialize(network,gw);
						if (lassoRectNodeSelectorWidget.pressEvent(mouse_x,mouse_y)==PopupWidget.S_REDRAW) {
							graphicsFramework.requestRedraw();
						}
					}
					else {
						// pick node based on best distance from mouse click
						nodeUnderMouseCursor = findNearestNodeToGivenPixel( mouse_x, mouse_y );

						if ( nodeUnderMouseCursor != null ) {
							currentMode = M_MOVING_NODE;
						}
						else if ( isPolygonUnderMouseCursor ) {
							currentMode = M_MOVING_SELECTION;
						}
						else {
							currentMode = M_PAN;
						}
					}
				}
				else if ( SwingUtilities.isRightMouseButton(e) ) {
					if ( e.isShiftDown() ) {
						// Enable or disable items in the menu that only operate on a single node,
						// according to whether there is a single node under the cursor.
						radialMenu.setEnabledByID( nodeUnderMouseCursor != null, C_TOGGLE_NODE_SELECTION );
						radialMenu.setEnabledByID( nodeUnderMouseCursor != null, C_TOGGLE_NODE_FIXED );
						radialMenu.setEnabledByID( nodeUnderMouseCursor != null, C_CONCENTRIC_LAYOUT );

						// Now, popup the menu.
						radialMenu.initialize(mouse_x,mouse_y);
						if (radialMenu.pressEvent(mouse_x,mouse_y)==PopupWidget.S_REDRAW) {
							graphicsFramework.requestRedraw();
						}
					}
					else {
						currentMode = M_ZOOM;
					}
				}
			}

		} // synchronized

		// graphicsFramework.requestRedraw();
	}

	public void mouseReleased(MouseEvent e) {
		mouse_x = e.getX();
		mouse_y = e.getY();

		if (SwingUtilities.isLeftMouseButton(e)) {
			isLeftMouseButtonDown = false;
		}
		else if (SwingUtilities.isRightMouseButton(e)) {
			isRightMouseButtonDown = false;
		}

		synchronized(network) {
			if (lassoRectNodeSelectorWidget.IsEngaged()) {
				if (lassoRectNodeSelectorWidget.releaseEvent(mouse_x,mouse_y)==PopupWidget.S_REDRAW) {
				}
				if ( ! lassoRectNodeSelectorWidget.IsEngaged() ) {
					ArrayList< Node > set = lassoRectNodeSelectorWidget.getSetOfSpecifiedNodes();
					if ( set.size() > 0 ) {
						network.deselectAllNodes();
						for ( Node n : set ) {
							network.selectNode( n );
						}
						computePolygonSurroundingSelectedNodes();
					}
				}
			}
			else if (radialMenu.IsEngaged()) {
				if (radialMenu.releaseEvent(mouse_x,mouse_y)==PopupWidget.S_REDRAW) {
				}
				if ( ! radialMenu.IsEngaged() ) {
					switch ( radialMenu.getIDOfSelection() ) {
					case C_TOGGLE_NODE_SELECTION:
						if ( nodeUnderMouseCursor != null ) {
							if ( network.isNodeSelected( nodeUnderMouseCursor ) )
								network.deselectNode( nodeUnderMouseCursor );
							else
								network.selectNode( nodeUnderMouseCursor );
							computePolygonSurroundingSelectedNodes();
						}
						break;
					case C_TOGGLE_NODE_FIXED:
						if ( nodeUnderMouseCursor != null ) {
							nodeUnderMouseCursor.isFixed = ! nodeUnderMouseCursor.isFixed;
						}
						break;
					case C_CONCENTRIC_LAYOUT:
						if ( nodeUnderMouseCursor != null ) {
							ArrayList<Node> centralNodes = new ArrayList<Node>();
							centralNodes.add( nodeUnderMouseCursor );
							network.performConcentricCircleLayout( centralNodes, 3, /* fix the nodes? */ false );
							computePolygonSurroundingSelectedNodes();
						}
						break;
					case C_TOGGLE_LABELS_OF_SELECTED_NODES:
						network.toggleShowLabelsOfNodes( network.getSelectedNodes() );
						break;
					case C_TOGGLE_GENRE_BOXES:
						network.setGenreBoxesActive( ! network.isGenreBoxesActive() );
						break;
					case C_TOGGLE_FORCE_SIMULATION:
						network.setForceDirectedLayoutActive( ! network.isForceDirectedLayoutActive() );
						break;
					case C_FRAME_SELECTED_NODES:
						gw.frame(network.getBoundingRectangle(network.getSelectedNodes()),true);
						break;
					case C_FRAME_NETWORK:
						gw.frame(network.getBoundingRectangle(null),true);
						break;
					}
				}
			}
			else if (
				currentMode == M_MOVING_NODE
				|| currentMode == M_MOVING_SELECTION
				|| currentMode == M_PAN
				|| currentMode == M_ZOOM
			) {
				currentMode = M_NEUTRAL;
			}

			if ( ! lassoRectNodeSelectorWidget.IsEngaged() && ! radialMenu.IsEngaged() ) {
				updateHighlighting();
			}
		}

		graphicsFramework.requestRedraw();
	}


	public void mouseDragged(MouseEvent e) {

		int delta_x = e.getX() - mouse_x;
		int delta_y = e.getY() - mouse_y;
		mouse_x = e.getX();
		mouse_y = e.getY();

		synchronized(network) {
			if (lassoRectNodeSelectorWidget.IsEngaged()) {
				if (lassoRectNodeSelectorWidget.dragEvent(mouse_x,mouse_y)==PopupWidget.S_REDRAW) {
				}
			}
			else if (radialMenu.IsEngaged()) {
				if (radialMenu.dragEvent(mouse_x,mouse_y)==PopupWidget.S_REDRAW) {
				}
			}
			else if ( currentMode == M_MOVING_NODE ) {
				if ( nodeUnderMouseCursor != null ) {
					nodeUnderMouseCursor.x += delta_x * gw.getScaleFactorInWorldSpaceUnitsPerPixel();
					nodeUnderMouseCursor.y += delta_y * gw.getScaleFactorInWorldSpaceUnitsPerPixel();
					computePolygonSurroundingSelectedNodes();
				}
			}
			else if ( currentMode == M_MOVING_SELECTION ) {
				ArrayList<Node> nodes = network.getSelectedNodes();
				for ( Node n : nodes ) {
					n.x += delta_x * gw.getScaleFactorInWorldSpaceUnitsPerPixel();
					n.y += delta_y * gw.getScaleFactorInWorldSpaceUnitsPerPixel();
				}
				computePolygonSurroundingSelectedNodes();
			}
			else if ( currentMode == M_PAN ) {
				gw.pan( delta_x, delta_y );
			}
			else if ( currentMode == M_ZOOM ) {
				gw.zoomIn( (float)Math.pow(Constant.zoomFactorPerPixelDragged,delta_x-delta_y), startOfDragX, startOfDragY );
			}
		}

		graphicsFramework.requestRedraw();
	}


	public void mouseMoved(MouseEvent e) {
		mouse_x = e.getX();
		mouse_y = e.getY();

		synchronized(network) {
			if (lassoRectNodeSelectorWidget.IsEngaged()) {
				if (lassoRectNodeSelectorWidget.moveEvent(mouse_x,mouse_y)==PopupWidget.S_REDRAW) {
					graphicsFramework.requestRedraw();
				}
			}
			else if (radialMenu.IsEngaged()) {
				if (radialMenu.moveEvent(mouse_x,mouse_y)==PopupWidget.S_REDRAW) {
					graphicsFramework.requestRedraw();
				}
			}
			else {
				updateHighlighting();
			}
		}
	}




	private void drawPartOfNode(
		Node node, float radius, boolean isFilled, Color c
	) {
		ArrayList<Point2D> points = null;

		float x = node.x;
		float y = node.y;
		gw.setColor( c );
		gw.drawCircle(
			x-radius, y-radius, radius, isFilled
		);
	}
	private void drawLabelOfNode(
		String label, float x, float y, float radius, Color c
	) {
		if ( label == null || label.isEmpty() ) return;
		gw.setColor( c );
		float x0 = x + 2*radius;
		float y0 = y + RadialMenuWidget.textHeight/2;
		gw.drawString( x0, y0, label );
	}
	private void drawBackgroundOfLabelOfNode(
		String label, float x, float y, float radius, boolean isFilled, Color c
	) {
		if ( label == null || label.isEmpty() ) return;
		gw.setColor( c );
		float stringWidth = gw.stringWidth( label );
		float x0 = x + radius;
		float width = 2*radius + stringWidth;
		float y0 = y - RadialMenuWidget.textHeight/2 - radius/2;
		float height = radius + RadialMenuWidget.textHeight;
		gw.drawRect(
			x0, y0, width, height, isFilled
		);
	}

	private void drawNetwork() {
		gw.setColor( 0,0,0 );
		for ( int i = 0; i < network.getNumNodes(); ++i ) {
			Node n = network.getNode(i);
			for ( int j = 0; j < n.neighbours.size(); ++j ) {
				Node n2 = n.neighbours.get(j);
				int n2_index = network.getIndexOfNode( n2 );
				if ( n2_index > i ) {
					gw.drawLine( n.x, n.y, n2.x, n2.y );
				}
			}
		}
		Color hiliteColor = new Color(1.0f,0.0f,0.0f);
		Color foregroundColor = new Color(0.0f,0.0f,0.0f);
		Color opaqueForegroundColor = new Color(0,0,0);
		Color halfOpaqueBackgroundColor = Constant.USE_ALPHA_COMPOSITING ? new Color(255,255,255,128) : new Color(255,255,255);

		for ( int i = 0; i < network.getNumNodes(); ++i ) {
			Node n = network.getNode(i);
			if ( n == nodeUnderMouseCursor ) {
				drawPartOfNode( n, (Constant.NODE_RADIUS+n.neighbours.size()/12)+2, true, hiliteColor );
			}
			drawPartOfNode( n, (Constant.NODE_RADIUS+n.neighbours.size()/12), true,
				Constant.USE_ALPHA_COMPOSITING
					? new Color(n.color_r,n.color_g,n.color_b,1.0f)
					: new Color(n.color_r,n.color_g,n.color_b)
			);
			drawPartOfNode( n, (Constant.NODE_RADIUS+n.neighbours.size()/12), false, foregroundColor );
		}
		for ( int i = 0; i < network.getNumNodes(); ++i ) {
			Node n = network.getNode(i);
			if ( n.showLabel || n == nodeUnderMouseCursor ) {
				String label = n.label;
				if ( Constant.SUFFIX_NODE_LABELS_WITH_METADATA ) {
					label = label
						+ "[deg=" + n.neighbours.size();
					if ( network.isClusteringCoefficientDefinedForNode(n) )
						label = label
							+ ";cc=" + n.clusteringCoefficient;
					label = label + "]";
				}
				drawBackgroundOfLabelOfNode( label, n.x, n.y, (Constant.NODE_RADIUS+n.neighbours.size()/12), true, halfOpaqueBackgroundColor );
				if ( n == nodeUnderMouseCursor )
					drawBackgroundOfLabelOfNode( label, n.x, n.y, (Constant.NODE_RADIUS+n.neighbours.size()/12), false, opaqueForegroundColor );
				drawLabelOfNode( label, n.x, n.y, (Constant.NODE_RADIUS+n.neighbours.size()/12), foregroundColor );
			}
		}
		for ( int i = 0; i < network.getNumNodes(); ++i ) {
			Node n = network.getNode(i);
			if ( network.isNodeSelected(n) )
				drawPartOfNode( n, (Constant.NODE_RADIUS+n.neighbours.size()/12)+2, false, hiliteColor );
		}

	}

	public void draw() {
		gw.clear(1,1,1);
		gw.setupForDrawing();

		synchronized(network) {
			gw.setCoordinateSystemToWorldSpaceUnits();
			if ( Constant.USE_ALPHA_COMPOSITING ) gw.enableAlphaBlending();

			// draw polygon surrounding the selected nodes
			gw.setColor( 0, 1, 1, 0.3f );
			gw.fillPolygon( polygonDrawnAroundSelection );
			gw.setLineWidth(3);
			if ( isPolygonUnderMouseCursor ) {
				gw.setColor( 1, 0, 0 );
				gw.drawPolygon( polygonDrawnAroundSelection );
			}
			else {
				gw.setColor( 0, 1, 1 );
				gw.drawPolygon( polygonDrawnAroundSelection );
			}
			gw.setLineWidth(1);
			
			if(network.isGenreBoxesActive()) {
				computePolygonSurroundingGenres();
				for(int i = 0; i < polygonDrawnAroundGenres.size(); i++) {
					ArrayList<Float> colors = network.colorPicker(i);
					gw.setColor(colors.get(0), colors.get(1), colors.get(2), 0.3f);
					gw.fillPolygon( polygonDrawnAroundGenres.get(i) );
					gw.setLineWidth(3);
					gw.setColor(colors.get(0), colors.get(1), colors.get(2));
					gw.drawPolygon(polygonDrawnAroundGenres.get(i));
				}
			}

			gw.setLineWidth(1);

			drawNetwork();


			gw.setCoordinateSystemToPixels();

			gw.enableAlphaBlending();
			if (lassoRectNodeSelectorWidget.IsEngaged())
				lassoRectNodeSelectorWidget.draw(gw);
			if (radialMenu.IsEngaged())
				radialMenu.draw(gw);
		}

	}


}


