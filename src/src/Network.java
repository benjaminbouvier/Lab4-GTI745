package src;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Random;
// import java.awt.Color;

public class Network {

	private ArrayList<Node> nodeArray = new ArrayList<Node>();
	private ArrayList<Node> selectedNodes = new ArrayList<Node>();
	private boolean isForceDirectedLayoutActive = Constant.IS_FORCE_DIRECTED_LAYOUT_ACTIVE_INITIALLY;




	private static String randomString( int minLength, int maxLength, Random r ) {
		char [] vowelArray = new char[5];
		vowelArray[0] = 'a';
		vowelArray[1] = 'e';
		vowelArray[2] = 'i';
		vowelArray[3] = 'o';
		vowelArray[4] = 'u';
		char [] consonantArray = new char[16];
		consonantArray[ 0] = 'b';
		consonantArray[ 1] = 'd';
		consonantArray[ 2] = 'f';
		consonantArray[ 3] = 'g';
		consonantArray[ 4] = 'h';
		consonantArray[ 5] = 'j';
		consonantArray[ 6] = 'k';
		consonantArray[ 7] = 'm';
		consonantArray[ 8] = 'n';
		consonantArray[ 9] = 'p';
		consonantArray[10] = 'r';
		consonantArray[11] = 's';
		consonantArray[12] = 't';
		consonantArray[13] = 'w';
		consonantArray[14] = 'y';
		consonantArray[15] = 'z';

		int length = minLength + r.nextInt(maxLength-minLength+1);
		String s = "";

		// We don't want strings to ever end in a consonant.
		// So, even-length strings start with a consonant,
		// and odd-length strings start with a vowel.
		boolean isVowel = (length%2==1);

		for ( int i = 0; i < length; ++i ) {
			//s = s + (char)('a'+r.nextInt(26));
			if (isVowel)
				s = s + vowelArray[r.nextInt(vowelArray.length)];
			else
				s = s + consonantArray[r.nextInt(consonantArray.length)];

			isVowel = ! isVowel;
		}
		return s;
	}
	private int indexOfRandomlyChosenNode(
		int indexOfNodeToNotChoose, // -1 if any node may be chosen
		Random random
	) {
		if ( getNumNodes() == 0 ) return -1;
		if ( getNumNodes() == 1 && indexOfNodeToNotChoose == 0 ) return -1;
		int index = random.nextInt( getNumNodes() );
		if ( index == indexOfNodeToNotChoose ) {
			++ index;
			if ( index == getNumNodes() )
				// wrap around
				index = 0;
		}
		return index;
	}
	public static Network generateRandomConnectedNetwork( int numNodes, int numEdges, Random random ) {
		Network net = new Network();
		if ( numNodes > 0 && numEdges > 0 ) {
			int maxNumNodes = numNodes * (numNodes-1) / 2;
			if ( numEdges > maxNumNodes )
				numEdges = maxNumNodes;
		}
		for ( int i = 0; i < numNodes; ++i ) {
			Node newNode = new Node();
			net.addNode( newNode );
			if ( i > 0 ) {
				Node existingNode = net.nodeArray.get( net.indexOfRandomlyChosenNode( i, random ) );
				net.addEdge( existingNode, newNode );
				-- numEdges;
			}
		}
		if ( net.getNumNodes() > 2 ) {
			while ( numEdges > 0 ) {
				int indexOfN1 = net.indexOfRandomlyChosenNode( -1, random );
				int indexOfN2 = net.indexOfRandomlyChosenNode( indexOfN1, random );
				Node n1 = net.nodeArray.get( indexOfN1 );
				Node n2 = net.nodeArray.get( indexOfN2 );
				// Note that n1 and n2 may already have an edge between them
				net.addEdge( n1, n2 );
				-- numEdges;
			}
		}
		return net;
	}
	public void assignRandomLabelsToAllNodes( Random random ) {
		for ( int i = 0; i < nodeArray.size(); ++i ) {
			nodeArray.get(i).label = randomString(4,10,random);
		}
	}
	public void assignRandomColorsToAllNodes( Random random ) {
		for ( int i = 0; i < nodeArray.size(); ++i ) {
			Node n = nodeArray.get(i);
			/*
			// Color c = new Color( Color.HSBtoRGB( random.nextFloat(), 1, 1 ) );
			Color c = new Color( Color.HSBtoRGB( random.nextInt(6)/6.0f, 1, 1 ) );
			n.color_r = c.getRed()/255.0f;
			n.color_g = c.getGreen()/255.0f;
			n.color_b = c.getBlue()/255.0f;
			*/
			int threeBits = random.nextInt(7); // a value between 0(black) and 6(yellow), but not 7(white)
			n.color_r = ( threeBits & 4 )!=0 ? 1.0f : 0;
			n.color_g = ( threeBits & 2 )!=0 ? 1.0f : 0;
			n.color_b = ( threeBits & 1 )!=0 ? 1.0f : 0;
		}
	}

	public Network() {
	}
	
	//debut modification
	public void initIndex(){
		for(int i=0;i<getNumNodes();i++){  
			nodeArray.get(i).setIndex(i);
		}
	}
	
	public void UpdateHeuristiqueNetwork(int i,Node n){
		nodeArray.set(i, n);
	}
	
	public void UpdateNodePosition(float x,float y,int index){
		Node n=getNode(index);
		n.x=x;
		n.y=y;
	}
	//fin modification
	
	
	
	public boolean isForceDirectedLayoutActive() {
		return this.isForceDirectedLayoutActive;
	}
	public void setForceDirectedLayoutActive( boolean flag ) {
		isForceDirectedLayoutActive = flag;
	}

	public int getNumNodes() {
		return nodeArray.size();
	}
	public Node getNode( int index ) {
		if ( index < 0 || index >= nodeArray.size() )
			return null;
		return nodeArray.get( index );
	}
	public ArrayList<Node> getNodes() { return nodeArray; }
	public void addNode( Node n ) {
		if ( ! nodeArray.contains(n) )
			nodeArray.add( n );
	}
	public void addEdge( Node n1, Node n2 ) {
		if ( n1 == n2 || n1 == null || n2 == null ) return;
		if ( ! n1.neighbours.contains(n2) ) {
			n1.neighbours.add( n2 );
		}
		if ( ! n2.neighbours.contains(n1) ) {
			n2.neighbours.add( n1 );
		}
	}
	public void addEdge( int nodeIndex1, int nodeIndex2 ) {
		addEdge( getNode(nodeIndex1), getNode(nodeIndex2) );
	}

	public boolean areNodesAdjacent( Node n1, Node n2 ) {
		if ( n1.neighbours.size() > n2.neighbours.size() ) {
			return n2.neighbours.contains(n1);
		}
		return n1.neighbours.contains(n2);
	}

	public boolean isClusteringCoefficientDefinedForNode( Node n ) {
		return n.neighbours.size() >= 2;
	}
	public float computeClusteringCoefficientOfNode( Node n ) {
		if ( ! isClusteringCoefficientDefinedForNode(n) ) return 0;
		int numAdjacentNeighbourPairs = 0;
		for ( int i = 0; i < n.neighbours.size()-1; ++i ) {
			Node neighbour1 = n.neighbours.get(i);
			for ( int j = i+1; j < n.neighbours.size(); ++j ) {
				Node neighbour2 = n.neighbours.get(j);
				if ( areNodesAdjacent(neighbour1,neighbour2) )
					++ numAdjacentNeighbourPairs;
			}
		}
		int totalPossible = n.neighbours.size() * ( n.neighbours.size() - 1 ) / 2;
		return numAdjacentNeighbourPairs / (float) totalPossible;
	}

	// Note that this won't remove the node from the set of selected nodes;
	// that must be done by the caller, if so desired.
	public void deleteNode( Node n ) {
		for ( int i = 0; i < n.neighbours.size(); ++i ) {
			Node otherNode = n.neighbours.get(i);
			otherNode.neighbours.remove( n );
		}
		n.neighbours.clear();
		nodeArray.remove( n );
	}
	public void removeEdge( Node n1, Node n2 ) {
		if ( n1 == n2 || n1 == null || n2 == null ) return;
		if ( n1.neighbours.contains(n2) ) {
			n1.neighbours.remove( n2 );
		}
		if ( n2.neighbours.contains(n1) ) {
			n2.neighbours.remove( n1 );
		}
	}

	public void addNeighbouringNodeToEachOfSelectedNodes(
		float probability, // pass 1.0 to add neighbours to all selected nodes
		boolean deselectOriginalNodes,
		boolean selectNewNodes
	) {
		int numSelectedNodes = selectedNodes.size();
		if ( numSelectedNodes == 0 )
			return;
		int numNodes = nodeArray.size();
		int i;
		for ( i = 0; i < numSelectedNodes; ++i ) {
			if ( Math.random() <= probability ) {
				Node n = selectedNodes.get(i);
				Node n2 = new Node();
				n2.x = n.x + Constant.SPRING_REST_LENGTH * (float)(Math.random()-0.5f);
				n2.y = n.y + Constant.SPRING_REST_LENGTH * (float)(Math.random()-0.5f);
				addNode( n2 );
				addEdge( n, n2 );
			}
		}
		if ( deselectOriginalNodes ) {
			deselectAllNodes();
		}
		if ( selectNewNodes ) {
			for ( i = numNodes; i < nodeArray.size(); ++i ) {
				selectNode( nodeArray.get(i) );
			}
		}
	}
	public void addEdgeBetweenEachPairOfSelectedNodes(
		float probability // pass 1.0 to add edges to all pairs
	) {
		int numSelectedNodes = selectedNodes.size();
		if ( numSelectedNodes < 2 )
			return;
		for ( int i = 0; i < numSelectedNodes-1; ++i ) {
			Node n1 = selectedNodes.get(i);
			for ( int j = i+1; j < numSelectedNodes; ++j ) {
				if ( Math.random() <= probability ) {
					Node n2 = selectedNodes.get(j);
					addEdge( n1, n2 );
				}
			}
		}
	}
	public void removeEdgeBetweenEachPairOfSelectedNodes() {
		int numSelectedNodes = selectedNodes.size();
		if ( numSelectedNodes < 2 )
			return;
		for ( int i = 0; i < numSelectedNodes-1; ++i ) {
			Node n1 = selectedNodes.get(i);
			for ( int j = i+1; j < numSelectedNodes; ++j ) {
				Node n2 = selectedNodes.get(j);
				removeEdge( n1, n2 );
			}
		}
	}

	public int getNumSelectedNodes() { return selectedNodes.size(); }
	public boolean isNodeSelected( Node node ) { return selectedNodes.contains( node ); }
	public ArrayList<Node> getSelectedNodes() { return selectedNodes; }
	public void selectNode( Node node ) {
		if ( ! isNodeSelected( node ) )
			selectedNodes.add( node );
	}
	public void deselectNode( Node node ) {
		selectedNodes.remove( node );
	}
	public void selectByLabel( String s ) {
		for ( int j = 0; j < nodeArray.size(); ++j ) {
			Node n = nodeArray.get(j);
			if ( n.label.equals(s) )
				selectNode( n );
		}
	}
	public void growSelection( int radius ) {
		ArrayList<Node> setOfNodesToSelect = new ArrayList<Node>();
		findNeighbourhoodOfNodes(
			selectedNodes, radius, setOfNodesToSelect
		);
		for ( int i = 0; i < setOfNodesToSelect.size(); ++i ) {
			selectNode( setOfNodesToSelect.get(i) );
		}
	}
	public void selectAllNodes() {
		selectedNodes.clear();
		for ( int i = 0; i < nodeArray.size(); ++i )
			selectedNodes.add( nodeArray.get(i) );
	}
	public void deselectAllNodes() {
		selectedNodes.clear();
	}
	public void invertSelectedNodes() {
		ArrayList<Node> newlySelectedNodes = new ArrayList<Node>();
		for ( int i = 0; i < nodeArray.size(); ++i ) {
			Node n = nodeArray.get(i);
			if ( ! selectedNodes.contains( n ) )
				newlySelectedNodes.add( n );
		}
		selectedNodes = newlySelectedNodes;
	}



	public void deleteSelectedNodes() {
		for ( int i = 0; i < selectedNodes.size(); ++i ) {
			deleteNode( selectedNodes.get(i) );
		}
		selectedNodes.clear();
	}


	// The return value of this can be used to index into nodeArray.
	public int getIndexOfNode( Node n ) {
		assert n != null;

		// If the cached index is NOT correct ...
		int index = n.getIndex();
		if ( index < 0 || index >= nodeArray.size() || getNode(index) != n ) {
			// ... recompute all the indices
			// (this takes linear time, but subsequent calls
			// for this and other nodes will be fast,
			// until the next time the cached indices are
			// made invalid because some nodes have been
			// added or deleted)
			for ( int i = 0; i < nodeArray.size(); ++i ) {
				nodeArray.get(i).setIndex(i);
			}
		}

		// assert that the cached index IS now correct
		assert getNode(n.getIndex()) == n;

		// return the cached index
		return n.getIndex();
	}

	public Node findNearestNode( float x, float y, float minDistanceThreshold ) {
		Node nearestNode = null;
		float smallestDistanceSquared = 0;
		for ( int i = 0; i < nodeArray.size(); ++i ) {
			Node n = nodeArray.get(i);
			float dx = x - n.x;
			float dy = y - n.y;
			float distanceSquared = dx*dx + dy*dy;
			if ( nearestNode == null || distanceSquared < smallestDistanceSquared ) {
				smallestDistanceSquared = distanceSquared;
				nearestNode = n;
			}
		}
		if ( smallestDistanceSquared <= minDistanceThreshold*minDistanceThreshold )
			return nearestNode;
		return null;
	}












	// In the next few methods, if ``nodes'' is null,
	// the operation is performed on all nodes in the network,
	// otherwise it is limited to the given ``nodes''.
	public void setShowLabelsOfNodes( ArrayList<Node> nodes, boolean flag ) {
		if ( nodes==null ) nodes=nodeArray;
		for ( int i = 0; i < nodes.size(); ++i ) {
			Node n = nodes.get(i);
			n.showLabel = flag;
		}
	}
	public void toggleShowLabelsOfNodes( ArrayList<Node> nodes ) {
		if ( nodes==null ) nodes=nodeArray;
		for ( int i = 0; i < nodes.size(); ++i ) {
			Node n = nodes.get(i);
			n.showLabel = ! n.showLabel;
		}
	}
	public void setFixedFlagOfNodes( ArrayList<Node> nodes, boolean flag ) {
		if ( nodes==null ) nodes=nodeArray;
		for ( int i = 0; i < nodes.size(); ++i ) {
			Node n = nodes.get(i);
			n.isFixed = flag;
		}
	}

	public void randomizePositionsOfNodes(
		// If null, the randomization is applied to all nodes in the network,
		// otherwise it is only applied to the given ``nodes''.
		ArrayList<Node> nodes,

		// If true, even fixed nodes are randomized.
		boolean randomizeFixedNodes,

		// If true, the random displacements are proportional
		// to the extents of the current layout of the nodes.
		// If false, the randomization is chosen to uniformly
		// fill the given ``boundingRectangle''.
		boolean localized,

		// If non-null, the randomized positions will not fall
		// outside this rectangle.
		AlignedRectangle2D boundingRectangle
	) {
		if ( nodes==null ) nodes=nodeArray;
		AlignedRectangle2D rectangle = new AlignedRectangle2D();
		for ( int i = 0; i < nodes.size(); ++i ) {
			Node n = nodes.get(i);
			if ( randomizeFixedNodes || ! n.isFixed )
				rectangle.bound( new Point2D(n.x, n.y ));
		}
		float distance = rectangle.getDiagonal().length();
		if ( distance < Constant.SPRING_REST_LENGTH/2 ) distance = Constant.SPRING_REST_LENGTH/2;
		for ( int i = 0; i < nodes.size(); ++i ) {
			Node n = nodes.get(i);
			if ( randomizeFixedNodes || ! n.isFixed ) {
				if ( localized || boundingRectangle==null ) {
					n.x += distance * (Math.random()-0.5f);
					n.y += distance * (Math.random()-0.5f);
				}
				else {
					n.x = (float)(boundingRectangle.getMin().x() + Math.random()*boundingRectangle.getDiagonal().x());
					n.y = (float)(boundingRectangle.getMin().y() + Math.random()*boundingRectangle.getDiagonal().y());
				}

				if ( boundingRectangle != null ) {
					// bound the node's position

					if ( n.x < boundingRectangle.getMin().x() )
						n.x = boundingRectangle.getMin().x();
					else if ( n.x > boundingRectangle.getMax().x() )
						n.x = boundingRectangle.getMax().x();

					if ( n.y < boundingRectangle.getMin().y() )
						n.y = boundingRectangle.getMin().y();
					else if ( n.y > boundingRectangle.getMax().y() )
						n.y = boundingRectangle.getMax().y();
				}
			}
		}
	}

	public Point2D computeGeometricCentreOfNodes(
		// If null, the centre of all nodes in the network is returned,
		// otherwise only the given ``nodes'' are considered.
		ArrayList<Node> nodes
	) {
		return getBoundingRectangle(nodes).getCenter();
	}

	/**
	 * Returns the bounding rectangle of the nodes in 2D. If the given vector
	 * of nodes is null, calculates and returns the bounding rectangle of the
	 * entire network.
	 */
	public AlignedRectangle2D getBoundingRectangle( ArrayList<Node> nodes ) {
		if ( nodes==null ) nodes=nodeArray;
		AlignedRectangle2D rectangle = new AlignedRectangle2D();
		for ( int i = 0; i < nodes.size(); ++i ) {
			Node n = nodes.get(i);
			rectangle.bound( new Point2D( n.x, n.y ) );
		}
		return rectangle;
	}

	/**
	 * Computers the centroid of the given nodes in 2 dimensions, or the entire network if
	 * the given vector is null.
	 */
	public Point2D computeCentroidOfNodes(
		// If null, the centroid of all nodes in the network is returned,
		// otherwise only the given ``nodes'' are considered.
		ArrayList<Node> nodes
	) {
		if ( nodes==null ) nodes=nodeArray;
		Vector2D centroid = new Vector2D(0,0);
		int numNodes = nodes.size();
		for ( int i = 0; i < nodes.size(); ++i ) {
			Node n = nodes.get(i);
			centroid.v[0] += n.x;
			centroid.v[1] += n.y;
		}
		centroid = Vector2D.mult( centroid, 1.0f/numNodes );
		return new Point2D( centroid );
	}


	public abstract class NodeVisitor {
		// Called by a routine traversing the graph.
		// Returns false if the visitor wants the traversal to be stopped,
		// which may happen, for example, if the visitor finds the one
		// node it was searching for.
		public abstract boolean visit(
			Node node,
			Node predecessorNode,
			int distanceToStartNode, // in edges
			Network network
		);
	}

	public void breadthFirstTraversal(
		ArrayList<Node> rootNodes, // the starting nodes for the traversal
		NodeVisitor visitor
	) {
		if ( rootNodes.isEmpty() )
			return;

		// During the traversal, nodes are either "white" (not yet visited),
		// "grey" (added to list of nodes to visit next),
		// or "black" (already visited).

		// Initialization.
		for ( Node n : nodeArray ) {
			n.TMP_color = Node.TMP_WHITE;
			n.TMP_predecessorNode = null;
			n.TMP_distanceFromStartNode = -1;
		}

		LinkedList<Node> greyNodes = new LinkedList<Node>();
		for ( Node rootNode : rootNodes ) {
			rootNode.TMP_color = Node.TMP_GREY;
			rootNode.TMP_distanceFromStartNode = 0;
			greyNodes.addFirst(rootNode);
		}
		do {
			// Get the next node to visit
			Node n = greyNodes.removeLast();

			// Mark all adjacent nodes as grey.
			for ( Node n2 : n.neighbours ) {
				if (n2.TMP_color == Node.TMP_WHITE) {
					n2.TMP_color = Node.TMP_GREY;
					n2.TMP_predecessorNode = n;
					n2.TMP_distanceFromStartNode = n.TMP_distanceFromStartNode + 1;
					greyNodes.addFirst(n2);
				}
			}

			if ( visitor != null ) {
				// Visit the node
				if ( ! visitor.visit(n, n.TMP_predecessorNode, n.TMP_distanceFromStartNode, this) ) {
					break;
				}
			}

			// Mark the node as having been visited
			n.TMP_color = Node.TMP_BLACK;

		} while (!greyNodes.isEmpty());
	}

	public void breadthFirstTraversal(
		Node rootNode, // a single starting node for the traversal
		NodeVisitor visitor
	) {
		if ( rootNode == null )
			return;
		ArrayList<Node> rootNodes = new ArrayList<Node>();
		rootNodes.add( rootNode );
		breadthFirstTraversal( rootNodes, visitor );
	}

	public void findNeighbourhoodOfNodesPartitionedByDistance(
		ArrayList<Node> rootNodes, // the nodes that form the "centre" of the neighbourhood
		int radius, // the radius of the neighbourhood to find, in edges from the ``rootNodes''; -1 means infinity

		// This is where the neighbourhood is returned,
		// with nodes organized classified by distance from the ``rootNodes''.
		// The caller should pass in an empty vector object ( new ArrayList<ArrayList<Node>>() ).
		// What is returned is a vector of vectors of nodes.
		// There's one "sub" vector for each distance from the root nodes.
		// Afterwards, the caller can index into the "top level" vector using distance.
		// So, the root nodes are in the first "sub" vector (at index 0 in the "top level" vector),
		// and all nodes that are 1 edge away from the root nodes are in the next "sub" vector,
		// and all nodes 2 edges away are in the next vector, etc.
		ArrayList<ArrayList<Node>> setsOfNodes
	) {
		if ( rootNodes.isEmpty() )
			return;
		setsOfNodes.clear();

		class MyNodeVisitor extends NodeVisitor {
			private int radius;
			ArrayList<ArrayList<Node>> setsOfNodes;
			public MyNodeVisitor( int r, ArrayList<ArrayList<Node>> s ) {
				radius = r;
				setsOfNodes = s;
			}
			public boolean visit(
				Node node,
				Node predecessorNode,
				int distanceToStartNode,
				Network network
			) {
				if ( /*radius==-1 means infinity, i.e. don't stop*/radius>=0 && distanceToStartNode > radius )
					// stop the traversal
					return false;

				// Add the node to ``setsOfNodes''
				while (node.TMP_distanceFromStartNode >= setsOfNodes.size()) {
					setsOfNodes.add(new ArrayList<Node>());
				}
				(setsOfNodes.get(node.TMP_distanceFromStartNode)).add(node);

				return true;
			}
		};
		NodeVisitor visitor = new MyNodeVisitor( radius, setsOfNodes );

		breadthFirstTraversal( rootNodes, visitor );
	}

	public void findNeighbourhoodOfNodePartitionedByDistance(
		Node rootNode, // a single centre of the neighbourhood
		int radius,
		ArrayList<ArrayList<Node>> setsOfNodes
	) {
		if ( rootNode == null )
			return;
		ArrayList<Node> rootNodes = new ArrayList<Node>();
		rootNodes.add( rootNode );
		findNeighbourhoodOfNodesPartitionedByDistance( rootNodes, radius, setsOfNodes );
	}

	// Similar to findNeighbourhoodOfNodesPartitionedByDistance(),
	// except the set of nodes returned is flat instead of being
	// partitioned by distance.
	public void findNeighbourhoodOfNodes(
		ArrayList<Node> rootNodes, // the nodes that form the "centre" of the neighbourhood
		int radius, // the radius of the neighbourhood to find, in edges from the ``rootNodes''; -1 means infinity
		ArrayList<Node> setOfNodes
	) {
		setOfNodes.clear();
		ArrayList<ArrayList<Node>> setsOfNodes = new ArrayList<ArrayList<Node>>();
		findNeighbourhoodOfNodesPartitionedByDistance( rootNodes, radius, setsOfNodes );

		// "flatten" the sets into a single set
		for ( int i = 0; i < setsOfNodes.size(); ++i ) {
			ArrayList<Node> subArray = setsOfNodes.get(i);
			for ( int j = 0; j < subArray.size(); ++j ) {
				Node n = subArray.get(j);
				setOfNodes.add(n);
			}
		}
	}

	public void findNeighbourhoodOfNode(
		Node rootNode, // a single centre of the neighbourhood
		int radius,
		ArrayList<Node> setOfNodes
	) {
		ArrayList<Node> rootNodes = new ArrayList<Node>();
		rootNodes.add( rootNode );
		findNeighbourhoodOfNodes( rootNodes, radius, setOfNodes );
	}

	private static float computeAngle(float x, float y) {
		float r = (float)Math.sqrt(x*x + y*y);
		if ( r <= 0 )
			return 0;
		float angle = (float)Math.asin(y/r);
		if ( x < 0 ) angle = (float)Math.PI - angle;
		// Note that angle is in [-pi/2,3*pi/2]
		return angle;
	}

	private static float computeAngle( Vector2D v ) {
		return computeAngle( v.x(), v.y() );
	}

	// Lays out the nodes that are within the given radius of the given central nodes
	// in concentric circles.
	public void performConcentricCircleLayout( ArrayList<Node> centralNodes, int neighbourhoodRadius, boolean fixNodes ) {
		if ( centralNodes.size() < 1 )
			return;
		boolean multipleCentralNodes = centralNodes.size() > 1;
		ArrayList<ArrayList<Node>> setsOfNodes = new ArrayList<ArrayList<Node>>();
		findNeighbourhoodOfNodesPartitionedByDistance( centralNodes, neighbourhoodRadius, setsOfNodes );
		Point2D centre = computeGeometricCentreOfNodes( centralNodes );

		int i;

		// Save the original positions of the nodes
		// that will be placed on the inner-most circle.
		// These will be used later.
		int indexOfInnerMostCircle = multipleCentralNodes ? 0 : 1;
		ArrayList<Node> nodesOnInnerMostCircle = null;
		Point2D originalPositionsOfNodesOnInnerMostCircle[] = null;
		if ( setsOfNodes.size() > indexOfInnerMostCircle ) {
			nodesOnInnerMostCircle
				= setsOfNodes.get(indexOfInnerMostCircle);
			originalPositionsOfNodesOnInnerMostCircle
				= new Point2D[ nodesOnInnerMostCircle.size() ];
			for ( i = 0; i < nodesOnInnerMostCircle.size(); ++i ) {
				Node n = nodesOnInnerMostCircle.get(i);
				originalPositionsOfNodesOnInnerMostCircle[i] = new Point2D(n.x,n.y);
			}
		}

		// Initalization: most nodes marked as unvisited (i.e. white).
		for ( i = 0; i < setsOfNodes.size(); ++i ) {
			ArrayList<Node> subArray = setsOfNodes.get(i);
			if ( i == 0 && ! multipleCentralNodes ) {
				// Special case:
				// position, and mark as visited,
				// the one central node.
				Node n = centralNodes.get(0);
				n.TMP_color = Node.TMP_BLACK;
				n.x = centre.x();
				n.y = centre.y();
			}
			else if ( i == setsOfNodes.size()-1 ) {
				// Special case:
				// position, and mark as visited,
				// the outer-most nodes.
				float r = (multipleCentralNodes ? i+1 : i) * Constant.SPRING_REST_LENGTH;
				for ( int j = 0; j < subArray.size(); ++j ) {
					Node n = subArray.get(j);
					n.TMP_color = Node.TMP_BLACK;
					float theta = j / (float)subArray.size() * 2*(float)Math.PI;
					n.x = r*(float)Math.cos(theta) + centre.x();
					n.y = r*(float)Math.sin(theta) + centre.y();
				}
			}
			else {
				for ( int j = 0; j < subArray.size(); ++j ) {
					Node n = subArray.get(j);
					n.TMP_color = Node.TMP_WHITE;
				}
			}

			// Also mark all the nodes as fixed,
			// if the caller wants us to.
			if (fixNodes) {
				for ( int j = 0; j < subArray.size(); ++j ) {
					Node n = subArray.get(j);
					n.isFixed = true;
				}
			}
		}

		// Position the remaining white nodes.
		for ( i = setsOfNodes.size()-1; i >= (multipleCentralNodes ? 0 : 1); --i ) {
			float r = (multipleCentralNodes ? i+1 : i) * Constant.SPRING_REST_LENGTH;
			ArrayList<Node> subArray = setsOfNodes.get(i);
			int N = subArray.size();

			// Recall that the black nodes are already positioned.
			// We need to find white nodes and evenly space them between surrounding black nodes.
			// Specifically, we need to find pairs of indices (j1,j2) where node_j1 is black,
			// and is followed by un unbroken series of white nodes, which is followed by black node_j2.
			// For each such pair (j1,j2), we will evenly space the white nodes
			// between node_j1 and node_j2.
			int j1 = 0, j2 = 0;
			boolean weAreFinished = false;

			// Initialization: find the first positioned (black) node.
			// There should be at least one black node.
			for ( j1 = 0; j1 < N; ++j1 ) {
				if ( (subArray.get(j1)).TMP_color == Node.TMP_BLACK )
					break;
			}
			assert j1 < N && (subArray.get(j1)).TMP_color == Node.TMP_BLACK;
			j2 = j1;
			// Now j1 and j2 are both at a black node.

			while ( ! weAreFinished ) {
				// Advance j1 to the next positioned (black) node that is followed by a white node
				while ( (subArray.get((j1+1)%N)).TMP_color != Node.TMP_WHITE ) {
					j1 = (j1+1) % N;
					if ( j1 == j2 ) {
						// We've wrapped around to our starting point.
						// Note that this will happen, in particular,
						// if N == 1, or if there are no longer any white nodes in ``subArray''.
						weAreFinished = true;
						break;
					}
				}
				if ( weAreFinished ) {
					break;
				}

				// Advance j2 to the next positioned (black) node after j1
				j2 = (j1+1) % N;
				assert N>1 && j1 != j2 && (subArray.get(j2)).TMP_color == Node.TMP_WHITE;
				while ( (subArray.get(j2)).TMP_color == Node.TMP_WHITE ) {
					j2 = (j2+1) % N;
					if ( j1 == j2 ) {
						// We've wrapped around to our starting point.
						// Note that this will happen if there's only 1 black node in the whole subArray
						// and all other nodes are white.
						break;
					}
				}


				// Evenly space all the white nodes between j1 and j2
				float theta_j1 = computeAngle(
					(subArray.get(j1)).x - centre.x(),
					(subArray.get(j1)).y - centre.y()
				);
				float theta_j2 = computeAngle(
					(subArray.get(j2)).x - centre.x(),
					(subArray.get(j2)).y - centre.y()
				);
				if ( j1 == j2 ) {
					// Special case: there's only 1 black node in the subArray,
					// and all other nodes are white.
					theta_j2 += 2*Math.PI;
				}
				if ( theta_j2 < theta_j1 ) theta_j2 += 2*Math.PI;
				float deltaTheta = (theta_j2 - theta_j1) / (((j2-j1+N-1)%N)+1);
				for ( int j3 = (j1+1)%N; j3 != j2; j3=(j3+1)%N ) {
					float theta_j3 = deltaTheta*((j3-j1+N)%N) + theta_j1;
					Node n = subArray.get(j3);
					n.TMP_color = Node.TMP_BLACK;
					n.x = r*(float)Math.cos(theta_j3) + centre.x();
					n.y = r*(float)Math.sin(theta_j3) + centre.y();
				}

				j1 = j2;
				// Now j1 and j2 are both at a black node.
			}

			// now position parent nodes in the next circle inwards
			j1 = j2 = 0;
			while ( j2 < N ) {
				while (
					j2 < N-1
					&&
					(subArray.get(j1)).TMP_predecessorNode
					== (subArray.get(j2+1)).TMP_predecessorNode
				)
					++j2;


				// position the parent node of node_j1 and node_j2
				Node parentNode = (subArray.get(j1)).TMP_predecessorNode;
				if ( parentNode != null && parentNode.TMP_color == Node.TMP_WHITE ) {
					float theta_j1 = computeAngle(
						(subArray.get(j1)).x - centre.x(),
						(subArray.get(j1)).y - centre.y()
					);
					float theta_j2 = computeAngle(
						(subArray.get(j2)).x - centre.x(),
						(subArray.get(j2)).y - centre.y()
					);
					if ( theta_j2 < theta_j1 ) theta_j2 += 2*Math.PI;
					float theta_parent = (theta_j2 - theta_j1) / 2 + theta_j1;
					parentNode.TMP_color = Node.TMP_BLACK;
					parentNode.x = (r-Constant.SPRING_REST_LENGTH)*(float)Math.cos(theta_parent) + centre.x();
					parentNode.y = (r-Constant.SPRING_REST_LENGTH)*(float)Math.sin(theta_parent) + centre.y();
				}


				// advance to the next group of children
				j1 = j2 = j2+1;
			}

		} // for loop

		if ( setsOfNodes.size() <= indexOfInnerMostCircle )
			return;
		// Finally, we rotate all the nodes by some angle
		// chosen to minimize how much the nodes on the inner-most
		// circle are moved from their original positions.
		//
		// To determine the angle of rotation to use,
		// we iterate over the nodes on the inner-most circle,
		// and find the average angle of rotation that
		// exists between their original position and
		// their currently assigned position.
		// We will then rotate *all* nodes by the negative
		// of that average angle, to move the nodes on the
		// inner-most circle back as close as possible to
		// their original positions.
		ArrayList< Float > rotationAngles = new ArrayList< Float >();
		for ( i = 0; i < nodesOnInnerMostCircle.size(); ++i ) {
			Node n = nodesOnInnerMostCircle.get(i);

			Point2D originalPosition = originalPositionsOfNodesOnInnerMostCircle[i];
			Vector2D originalRelativePosition = Point2D.diff(originalPosition,centre);
			float originalAngle = computeAngle( originalRelativePosition );

			Point2D currentPosition = new Point2D(n.x,n.y);
			Vector2D currentRelativePosition = Point2D.diff(currentPosition,centre);
			float currentAngle = computeAngle( currentRelativePosition );

			float rotationAngle = currentAngle - originalAngle;

			rotationAngles.add( rotationAngle );
		}
		float averageRotationAngle = Point2DUtil.computeAverageAngle( rotationAngles );
//System.out.println("based on " + nodesOnInnerMostCircle.size() + " nodes, the average angle is " + averageRotationAngle);
		float globalRotationAngle = - averageRotationAngle;
		float sineOfGlobalRotationAngle = (float)Math.sin(globalRotationAngle);
		float cosineOfGlobalRotationAngle = (float)Math.cos(globalRotationAngle);
		for ( i = 0; i < setsOfNodes.size(); ++i ) {
			ArrayList<Node> subArray = setsOfNodes.get(i);
			for ( int j = 0; j < subArray.size(); ++j ) {
				Node n = subArray.get(j);

				// perform the rotation
				float relativeX = n.x - centre.x();
				float relativeY = n.y - centre.y();
				n.x = cosineOfGlobalRotationAngle*relativeX - sineOfGlobalRotationAngle*relativeY + centre.x();
				n.y = sineOfGlobalRotationAngle*relativeX + cosineOfGlobalRotationAngle*relativeY + centre.y();
			}
		}

	}

	public void computeDerivedData() {
		for ( int i = 0; i < nodeArray.size(); ++i ) {
			Node n = nodeArray.get(i);
			if ( isClusteringCoefficientDefinedForNode( n ) )
				n.clusteringCoefficient = computeClusteringCoefficientOfNode(n);
		}
	}




	public String getStatusString() {
		String status = "" + getNumNodes() + " nodes total; "
			+ selectedNodes.size() + " nodes selected";
		return status;
	}

}


