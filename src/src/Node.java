
import java.util.ArrayList;


public class Node {

	public ArrayList<Node> neighbours = new ArrayList<Node>();

	public float clusteringCoefficient = 0;

	public String label;
	public boolean showLabel = false;
	
	// Artist specific infos
	public String pgid;
	public int tier;
	public String artist;
	public String act_yrs;
	public String existence_year;
	public String born_form_place;
	public String ceased_year;
	public String ceased_place;
	public String amgid;
	public String born_name;
	public boolean is_solo;
	

	public float x = 0, y = 0; // position in world space

	public float color_r = 0, color_g = 0, color_b = 0;

	public boolean isFixed = false;

	/**
	 * Used in the force-directed layout algorithms
	 * to temporarily store components of pseudo "forces" acting on this node.
	 * These components are later used to update the node's position.
	 */
	public float forceX, forceY;

	/**
	 * These data members are used for Breadth-First Traversal (BFT)
	 * or Depth-First Traversal (DFT).
	 * They are only of temporary use during a traversal,
	 * afterwhich they can be ignored.
	 * Before performing a traversal and using them,
	 * they should be re-initialized to overwrite any garbage values
	 * they may contain.
	 */
	public static final int TMP_WHITE = 0;  // nodes not yet visited
	public static final int TMP_GREY = 1;   // nodes added to list of nodes to visit next
	public static final int TMP_BLACK = 2;  // nodes already visited
	public int TMP_color = TMP_WHITE;
	public Node TMP_predecessorNode = null;
	public int TMP_distanceFromStartNode = 0;

	// We assume that each node is "owned" by only one network.
	// Within a network, each node has a corresponding index
	// that can be used to look up the node in an array (or vector or something similar)
	// of nodes that is maintained by the network.
	// To make it easier to determine the index for a given node,
	// we have the following data member that can be used to cache the node's index.
	// If a few nodes get deleted or added to the network,
	// causing the index stored here to become invalid,
	// we can simply lazily update it whenever we find it to be incorrect.
	// (The updating happens in Network.getIndexOfNode() )
	//
	private int index = -1;

	public Node()
	{
	}

	public Node( String s )
	{
		label = s;
	}
	
	// Set all artist informations from the artist CSV file
	public void setArtistInfos(String tier, 
			String artist, 
			String act_yrs, 
			String existence_year, 
			String born_form_place, 
			String ceased_year,
			String ceased_place, 
			String amgid, 
			String born_name, 
			String is_solo) {
		
		this.tier = Character.getNumericValue(tier.charAt(0));
		this.artist = artist;
		this.act_yrs = act_yrs;
		this.existence_year = existence_year;
		this.born_form_place = born_form_place;
		this.ceased_year = ceased_year;
		this.amgid = amgid;
		this.born_name = born_name;
		
		if(is_solo.equals("1")) {
			this.is_solo = true;
		}
		else {
			this.is_solo = false;
		}
		
	}
	
	public void setIndex( int i ) {
		index = i;
	}

	// Note: the index returned here is not guaranteed to be correct.
	// Clients who want an index guaranteed to be correct
	// should call Network.getIndexOfNode()
	public int getIndex() {
		return index;
	}

} // End class

