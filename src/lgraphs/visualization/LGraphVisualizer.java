/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package lgraphs.visualization;


import java.util.Iterator;
import javax.swing.JFrame;
import lgraphs.LGraph;
import lgraphs.LGraphEdge;
import lgraphs.LGraphNode;
import prefuse.Constants;
import prefuse.Display;
import prefuse.Visualization;
import prefuse.action.ActionList;
import prefuse.action.RepaintAction;
import prefuse.action.assignment.ColorAction;
import prefuse.action.layout.graph.ForceDirectedLayout;
import prefuse.activity.Activity;
import prefuse.controls.DragControl;
import prefuse.controls.PanControl;
import prefuse.controls.ZoomControl;
import prefuse.data.Edge;
import prefuse.data.Graph;
import prefuse.data.Node;
import prefuse.render.DefaultRendererFactory;
import prefuse.render.EdgeRenderer;
import prefuse.render.LabelRenderer;
import prefuse.util.ColorLib;
import prefuse.util.force.DragForce;
import prefuse.util.force.ForceSimulator;
import prefuse.util.force.NBodyForce;
import prefuse.util.force.SpringForce;
import prefuse.visual.VisualGraph;
import prefuse.visual.VisualItem;

public class LGraphVisualizer extends Display {

	public static final String GRAPH = "graph";
	public static final String NODES = "graph.nodes";
	public static final String EDGES = "graph.edges";

	private LGraph graph;

	public static JFrame newWindow(String name, int dx, int dy, LGraph graph) {
		LGraphVisualizer ad = new LGraphVisualizer(dx, dy, graph);
		JFrame frame = new JFrame(name);
		frame.getContentPane().add(ad);
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setVisible(true);

		return frame;
	}


	public LGraphVisualizer(int dx, int dy, LGraph a_graph) {
		// initialize display and data
		super(new Visualization());

		graph = a_graph;

		initDataGroups(graph);

                // set up the renderers
                // draw the nodes as basic shapes
        //        Renderer nodeR = new ShapeRenderer(20);
                LabelRenderer nodeR = new LabelRenderer("name");
                nodeR.setHorizontalPadding(4);
                nodeR.setVerticalPadding(2);
                nodeR.setRoundedCorner(8, 8); // round the corners
                EdgeRenderer edgeR = new LabelEdgeRenderer(Constants.EDGE_TYPE_LINE,Constants.EDGE_ARROW_FORWARD);
                edgeR.setArrowHeadSize(6,6);

                DefaultRendererFactory drf = new DefaultRendererFactory(nodeR,edgeR);
                m_vis.setRendererFactory(drf);

                // set up the visual operators
                // first set up all the color actions
                ColorAction nFill = new ColorAction(NODES, VisualItem.FILLCOLOR);
                nFill.setDefaultColor(ColorLib.gray(255));
                nFill.add("_hover", ColorLib.gray(200));

                ColorAction nEdges = new ColorAction(EDGES, VisualItem.STROKECOLOR);
                nEdges.setDefaultColor(ColorLib.gray(100));

                // bundle the color actions
                ActionList colors = new ActionList();
                colors.add(nFill);
                colors.add(nEdges);

                // now create the main layout routine
                ActionList layout = new ActionList(Activity.INFINITY);
                ForceDirectedLayout fdl = new ForceDirectedLayout(GRAPH, true);
                ForceSimulator m_fsim = new ForceSimulator();
                m_fsim.addForce(new NBodyForce());
                m_fsim.addForce(new SpringForce(0.5E-4f,100));
                m_fsim.addForce(new DragForce());
                fdl.setForceSimulator(m_fsim);

                layout.add(colors);
                layout.add(fdl);
                layout.add(new RepaintAction());
                m_vis.putAction("layout", layout);

                // set up the display
                setSize(dx,dy);
                pan(250, 250);
                setHighQuality(true);
                addControlListener(new DragControl());
                addControlListener(new ZoomControl());
                addControlListener(new PanControl());

        //      ActionList draw = new ActionList();
        //      draw.add(new GraphDistanceFilter(GRAPH, 50));
        //      m_vis.putAction("draw", draw);


                // set things running
                m_vis.run("layout");
	}


        private void initDataGroups(LGraph graph) {
		Graph g = new Graph(true);

		g.addColumn("name", String.class);

//		HashMap<LGraphNode, List<Pair<TermFeatureTerm, Symbol>>> variables = FTRefinement.variablesWithAllParents(f);
//		List<FeatureTerm> orderedVariables = new LinkedList<FeatureTerm>();
//		List<String> orderedFeatures = new LinkedList<String>();

		// Create nodes:
		for (LGraphNode v : graph.getNodes()) g.addNode();
		VisualGraph vg = m_vis.addGraph(GRAPH, g);

		// Set labelSet for nodes
		Iterator<?> i = vg.nodes();
		for (LGraphNode v : graph.getNodes()) {
                    VisualItem vi = (VisualItem) i.next();
                    vi.set("name", v.toStringLabel());

                    vi.setStrokeColor(ColorLib.rgb(128, 128, 128));
                    vi.set(VisualItem.TEXTCOLOR, ColorLib.gray(0));
		}

		// Create edges:
		for (LGraphNode v : graph.getNodes()) {
                    for(LGraphEdge le:v.getEdges()) {
                        int i1 = graph.getNodes().indexOf(le.start);
                        int i2 = graph.getNodes().indexOf(le.end);
                        Node n1 = g.getNode(i1); // Target
                        Node n2 = g.getNode(i2); // Source
                        Edge e = g.addEdge(n1, n2); // e columns: source, target, name
                        int edge = isMultiLine(g);
                        if (edge != -1) {
                            g.getEdge(edge).set("name", g.getEdge(edge).get("name") + " // " + le.labelSet.toString());
                            g.removeEdge(e);
                        } else {
                            e.set("name", le.labelSet.toString());
                        }
                        
                    }
		}
		// Set colors:
		i = vg.edges();
		while (i.hasNext()) {
			VisualItem vi = (VisualItem) i.next();
			vi.setFillColor(ColorLib.gray(0));
			vi.setTextColor(ColorLib.rgb(0, 128, 0));
		}

		m_vis.setInteractive(EDGES, null, false);
	}

        
	private int isMultiLine(Graph g) {
		int multi = -1;
		Object origin = g.getEdge(g.getEdgeCount() - 1).get("source"); 
		Object destination = g.getEdge(g.getEdgeCount() - 1).get("target");

		for (int i = 0; i < g.getEdgeCount() - 1; i++) {
                    if (((Integer) origin).equals((Integer) (g.getEdge(i).get("source"))) &&
                        ((Integer) destination).equals((Integer) (g.getEdge(i).get("target")))) {
                            multi = i;
                            return multi;
                    }
		}

		return multi;
	}

} 
