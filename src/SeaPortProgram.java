import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * file name: SeaPortProgram.java
 *
 * @author Ryan Cohan
 *         date: 12/12/16
 *         purpose: The purpose of this class is to run the Sea Port simulation
 */
public class SeaPortProgram {
    static World world;

    public static void main(String[] args) {
        Scanner scanner;
        world = new World();
        try {
            scanner = new Scanner(new File(selectSimulationFile()));
            world.readFile(scanner);
            scanner = null;
            System.gc();
            JDisplay jDisplay = new JDisplay(world.toString());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Select a simulation file using jFileChooser
     *
     * @return full file path with file name
     */

    private static String selectSimulationFile() {
        String file = null;
        JFileChooser jFileChooser = new JFileChooser(".");
        jFileChooser.setDialogTitle("Please select a data file");
        jFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        if (jFileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            file = jFileChooser.getSelectedFile().toString();
        } else {
            System.exit(0);
        }
        return file;
    }


    /**
     * This class creates display to view related World info
     */
    @SuppressWarnings("serial")
    private static class JDisplay extends JFrame {
        String str;
        JTextArea jta;
        JComboBox sortBox;
        JComboBox jcb;
        JTextField jtf;
        JButton loadDataButton;
        JButton sortByNameButton;
        JScrollPane jspForjta;
        JTree jTree;
        JScrollPane jspForjTree;


        public JDisplay(String str) {
            this.str = str;
            createAndShowGUI();
        }

        /**
         * Add components to pane
         *
         * @param contentPane: Container to add components
         */
        public void addComponentsToPane(Container contentPane) {
            initComponentsAndSetFeatures();

            addActionListeners();

            setLayoutForDisplay(contentPane);

        }

        /**
         * Adds action listeners to components
         */
        private void addActionListeners() {
            sortBox.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    int index = sortBox.getSelectedIndex();
                    String sorted = sortShipsInQue(index);

                    if (index != 0 && !sorted.matches(str)) {
                        jta.setText(sorted);
                        loadDataButton.setEnabled(true);
                    }
                }
            });

            jcb.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    jtf.setEditable(jcb.getSelectedIndex() != 0);
                }
            });

            jtf.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
//                    Remove all redundant spaces
                    String searchStr = jtf.getText().replaceAll("[\\s\\t]+$", "").replaceAll("^[\\s\\t]+", "");
                    searchStr = searchStr.replaceAll("(\\s+){2,}", "");
                    String result = search(searchStr);

                    if (result != null && result.length() > 0) {
                        jta.setText(result);
                    } else {
                        jta.setText("No result found!");
                    }
                    loadDataButton.setEnabled(result == null || !str.matches(result));
                }
            });

            loadDataButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    jta.setText(str);
                    loadDataButton.setEnabled(false);
                }
            });
            sortByNameButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    jta.setText(world.sortByName());
                    loadDataButton.setEnabled(true);
                }
            });
        }

        /**
         * Searches for given string
         *
         * @param searchStr: String to be searched for
         * @return search results
         */
        private String search(String searchStr) {
            switch (jcb.getSelectedIndex()) {
                case 1:
                    return world.searchIndex(searchStr);
                case 2:
                    return world.searchName(searchStr);
                case 3:
                    return world.searchSkill(searchStr);
                default:
                    return "";
            }
        }

        /**
         * Calls related methods accordingly with selected option from sort CB
         *
         * @param index: from sort combo box
         * @return string to display in text area
         */
        private String sortShipsInQue(int index) {
            switch (index) {
                case 1:
                    return world.sortByWeight();
                case 2:
                    return world.sortByLength();
                case 3:
                    return world.sortByWidth();
                case 4:
                    return world.sortByDraft();
                default:
                    return "";
            }
        }

        /**
         * Sets layout for GUI
         *
         * @param contentPane: Container for components and layout
         */
        private void setLayoutForDisplay(Container contentPane) {

            JSplitPane jSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, jspForjta, jspForjTree);
            JPanel containerForJobs = new JPanel();
            JPanel containerForResources = new JPanel();
            containerForJobs.setLayout(new BoxLayout(containerForJobs, BoxLayout.Y_AXIS));
            containerForResources.setLayout(new BoxLayout(containerForResources, BoxLayout.Y_AXIS));
            boolean hasJob = false;
            boolean hasResource = false;
            for (SeaPort seaPort : world.ports.values()) {
                containerForResources.add(seaPort.getContainerPanel());
                if (!hasResource)
                    hasResource = seaPort.persons.size() > 0;
                for (Ship ship : seaPort.ships) {
                    for (Job job : ship.jobs) {
                        containerForJobs.add(job.getContainerPanel());
                        hasJob = true;
                    }
                }
            }

            JScrollPane sPaneForJobsContainer = new JScrollPane(new JLabel("No jobs!".toUpperCase(), SwingConstants.CENTER));
            if (hasJob)
                sPaneForJobsContainer = new JScrollPane(containerForJobs);

            JScrollPane sPaneForResourcesContainer = new JScrollPane(new JLabel("No resources!".toUpperCase(), SwingConstants.CENTER));
            if (hasResource)
                sPaneForResourcesContainer = new JScrollPane(containerForResources);
            sPaneForJobsContainer.setMaximumSize(new Dimension(5660, 800));
            sPaneForResourcesContainer.setMaximumSize(new Dimension(5660, 200));

            GroupLayout layout = new GroupLayout(contentPane);
            contentPane.setLayout(layout);
            layout.setAutoCreateGaps(true);
            layout.setAutoCreateContainerGaps(true);

            layout.setHorizontalGroup(layout.createSequentialGroup()
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                    .addComponent(jcb)
                                    .addComponent(jtf))
                            .addComponent(sortBox)
                            .addComponent(jSplitPane)
                            .addGroup(layout.createSequentialGroup()
                                    .addComponent(loadDataButton)
                                    .addComponent(sortByNameButton))
                            .addComponent(sPaneForResourcesContainer))
                    .addComponent(sPaneForJobsContainer));

            layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                    .addComponent(jcb)
                                    .addComponent(jtf))
                            .addComponent(sortBox)
                            .addComponent(jSplitPane)
                            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                    .addComponent(loadDataButton)
                                    .addComponent(sortByNameButton))
                            .addComponent(sPaneForResourcesContainer))
                    .addComponent(sPaneForJobsContainer));
        }

        /**
         * Initializes components and set their features
         */
        private void initComponentsAndSetFeatures() {
            String[] sortOptions = {"Select a feature to sort ships in que", "weight", "length", "width", "draft"};
            String[] searchOptions = {"Select a feature to search", "index", "name", "skill"};

            jta = new JTextArea(str, 20, 60);
            sortBox = new JComboBox(sortOptions);
            jcb = new JComboBox(searchOptions);
            jtf = new JTextField();
            loadDataButton = new JButton("Reload Data");
            sortByNameButton = new JButton("Sort All Things");

            jtf.setPreferredSize(new Dimension(120, 25));
            jtf.setEditable(false);

            sortBox.setSelectedIndex(0);
            sortBox.setMaximumSize(new Dimension(5660, 25));
            jcb.setSelectedIndex(0);
            jta.setFont(new java.awt.Font("Monospaced", 0, 12));
            jta.setEditable(false);
            jta.setLineWrap(true);

            jspForjta = new JScrollPane(jta, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            loadDataButton.setEnabled(false);

            initTree();
        }

        /**
         * Initializes jTree
         */
        private void initTree() {
            DefaultMutableTreeNode top = new DefaultMutableTreeNode("World");
            createNodes(top);
            jTree = new JTree(top);
            jspForjTree = new JScrollPane(jTree);
        }

        /**
         * Creates nodes for jTree
         *
         * @param top: Root element for tree
         */
        private void createNodes(DefaultMutableTreeNode top) {
            DefaultMutableTreeNode portNode;
            for (SeaPort seaPort : world.ports.values()) {
                portNode = createThingNode(seaPort);
                DefaultMutableTreeNode node = new DefaultMutableTreeNode("Docks");
                for (Dock dock : seaPort.docks) {
                    DefaultMutableTreeNode dockNode = createThingNode(dock);
                    if (dock.getShip() != null) {
                        DefaultMutableTreeNode shipNode = createThingNode(dock.getShip());
                        for (Job job : dock.getShip().jobs) {
                            shipNode.add(createThingNode(job));
                        }
                        dockNode.add(shipNode);
                    }
                    node.add(dockNode);
                    portNode.add(node);
                }
                node = new DefaultMutableTreeNode("Ships in Que");
                for (Ship ship : seaPort.que) {
                    DefaultMutableTreeNode shipNode = createThingNode(ship);
                    for (Job job : ship.jobs) {
                        shipNode.add(createThingNode(job));
                    }
                    node.add(shipNode);
                }
                portNode.add(node);

                node = new DefaultMutableTreeNode("All Ships");
                for (Ship ship : seaPort.ships) {
                    DefaultMutableTreeNode shipNode = createThingNode(ship);
                    for (Job job : ship.jobs) {
                        shipNode.add(createThingNode(job));
                    }
                    node.add(shipNode);
                }
                portNode.add(node);

                node = new DefaultMutableTreeNode("People");
                for (Person person : seaPort.persons) {
                    DefaultMutableTreeNode personNode = createThingNode(person);
                    node.add(personNode);
                }
                portNode.add(node);
                top.add(portNode);
            }
        }

        /**
         * Creates new node from Thing object
         *
         * @param thing: Thing object
         * @return new DefaultMutableTreeNode
         */
        private DefaultMutableTreeNode createThingNode(Thing thing) {
            return new DefaultMutableTreeNode(thing.getIndex() + " " + thing.getName());
        }

        /**
         * Creates and displays GUI
         */
        private void createAndShowGUI() {

            JFrame frame = new JFrame("Sea Port Simulation");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            //Set up the content pane and add swing components to it
            addComponentsToPane(frame.getContentPane());
            frame.setPreferredSize(new Dimension(1300, 800));
            frame.pack();
            frame.setVisible(true);
            World.setInit(); // world is set!
        }
    }
}
