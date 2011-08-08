package edu.baylor.cs.holder.security.test.tools;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

import org.xml.sax.SAXException;

import edu.baylor.cs.holder.security.service.accessobjects.AccessRuleKey;
import edu.baylor.cs.holder.security.service.impl.Dom4jSecurityRepository;

/**
 * Simple viewer to allow the security.xml file to be viewed in a more visual
 * way.
 * 
 * @author holder
 */
public class SecurityViewer extends JFrame {

    /**
     * UID
     */
    private static final long serialVersionUID = 1147895352498577585L;

    private ExposedRepositoryAccessService repositoryAccessService;

    final JFrame thisFrame = this;

    // create a file chooser
    final JFileChooser fileChooser = new JFileChooser(new File("./"));

    private JSplitPane mainSplit;
    private JSplitPane securitySplitPane;
    private JTextArea log;

    public SecurityViewer() throws IOException, SAXException {
        super("Security Viewer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(1024, 768));
        initializeFrame();
        setVisible(true);
    }

    private void initializeFrame() throws IOException, SAXException {
        // set up log
        log = new JTextArea();
        log.setEditable(true);
        log.setText("Add your own notes here. You can also copy and paste "
                + "from the tree with CTRL+C and CTRL+V.");

        // set up the primary JSplitPane
        mainSplit = new JSplitPane();
        mainSplit.setOrientation(JSplitPane.VERTICAL_SPLIT);
        add(mainSplit);

        initializeMenu();

        // prompt for opening the sample or another security xml
        int result = JOptionPane.showConfirmDialog(this,
                "Do you want to load the default sample security.xml?",
                "Open security.xml", JOptionPane.YES_NO_OPTION);
        try {
            switch (result) {
            case JOptionPane.OK_OPTION:
                // open sample
                loadXML(SecurityViewer.class.getResourceAsStream("/security.xml"));
                break;
            default:
                // load from a file
                promptForFile();
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private void promptForFile() throws FileNotFoundException, IOException,
            SAXException {
        int returnVal = fileChooser.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            loadXML(new FileInputStream(file));
        } else {
            // exit if there is no repository defined and the user has canceled
            if (repositoryAccessService == null) {
                System.exit(0);
            }
        }
    }

    private JSplitPane createSecurityViewerPane() {
        JScrollPane byCategoriesPane = new JScrollPane(createCategoryTree());
        JScrollPane byRolesPane = new JScrollPane(createRoleTree());

        securitySplitPane = new JSplitPane();
        securitySplitPane.setLeftComponent(byCategoriesPane);
        securitySplitPane.setRightComponent(byRolesPane);

        return securitySplitPane;
    }

    private void loadXML(InputStream xmlInputStream)
            throws FileNotFoundException, IOException, SAXException {
        
        Dom4jSecurityRepository securityRepository = new Dom4jSecurityRepository();
        securityRepository.populate(xmlInputStream);
        
        ExposedRepositoryAccessService repositoryAccessService = new ExposedRepositoryAccessService();
        repositoryAccessService.update(securityRepository);
        
        this.repositoryAccessService = repositoryAccessService;

        // set up the top JSplitPane
        JSplitPane securityViewerPane = createSecurityViewerPane();
        JScrollPane logPane = new JScrollPane(log);

        // set up the primary JSplitPane
        mainSplit.setLeftComponent(securityViewerPane);
        mainSplit.setRightComponent(logPane);
        pack();

        // set divider locations
        securitySplitPane.setDividerLocation(0.5);
        mainSplit.setDividerLocation(0.75);
    }

    private void initializeMenu() {

        // create the menu bar
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");

        // open
        JMenuItem open = new JMenuItem("Open...");
        open.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    promptForFile();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        fileMenu.add(open);

        // reload
        JMenuItem reload = new JMenuItem("Reload XML");
        reload.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    loadXML(SecurityViewer.class.getResourceAsStream("/security.xml"));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        fileMenu.add(reload);
        fileMenu.addSeparator();

        // exit
        JMenuItem exit = new JMenuItem("Exit");
        exit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        fileMenu.add(exit);

        JMenu aboutMenu = new JMenu("Help");
        JMenuItem about = new JMenuItem("About");
        about.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showConfirmDialog(thisFrame, getAboutMessage(),
                        "About", JOptionPane.PLAIN_MESSAGE,
                        JOptionPane.PLAIN_MESSAGE);
            }
        });
        aboutMenu.add(about);

        // build the menu bar
        menuBar.add(fileMenu);
        menuBar.add(aboutMenu);
        setJMenuBar(menuBar);
    }

    private JTree createCategoryTree() {

        // category = Node
        Map<String, DefaultMutableTreeNode> categories = new HashMap<String, DefaultMutableTreeNode>();

        // category.action = Node
        Map<String, DefaultMutableTreeNode> actions = new HashMap<String, DefaultMutableTreeNode>();

        // category.action(a, b, c) = List of allowed roles
        Map<String, DefaultMutableTreeNode> contexts = new HashMap<String, DefaultMutableTreeNode>();

        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("Security By Category");
        for (AccessRuleKey key : repositoryAccessService.getAccessRuleMap().keySet()) {
            String categoryName = key.getCategory();
            DefaultMutableTreeNode categoryNode = categories.get(categoryName);
            if (categoryNode == null) {
                categoryNode = new DefaultMutableTreeNode(categoryName);
                categories.put(categoryName, categoryNode);
                rootNode.add(categoryNode);
            }

            String actionName = key.getAction();
            String fullActionName = categoryName + "." + actionName;
            DefaultMutableTreeNode actionNode = actions.get(fullActionName);
            if (actionNode == null) {
                actionNode = new DefaultMutableTreeNode(actionName);
                actions.put(fullActionName, actionNode);
                categoryNode.add(actionNode);
            }

            String contextsName = getContextString(key.getContexts());
            String contextsFullName = fullActionName + "." + contextsName;
            DefaultMutableTreeNode contextsNode = contexts.get(contextsFullName);
            if (contextsNode == null) {
                contextsNode = new DefaultMutableTreeNode(contextsName);
                contexts.put(contextsFullName, contextsNode);
                actionNode.add(contextsNode);
            }

            for (String allowedRole : repositoryAccessService.getAccessRuleMap().get(key)) {
                contextsNode.add(new DefaultMutableTreeNode(allowedRole));
            }
        }

        return new JTree(rootNode);
    }

    private JTree createRoleTree() {

        // role = Node
        Map<String, DefaultMutableTreeNode> roles = new HashMap<String, DefaultMutableTreeNode>();

        // role.category = Node
        Map<String, DefaultMutableTreeNode> categories = new HashMap<String, DefaultMutableTreeNode>();

        // role.category.action = Node
        Map<String, DefaultMutableTreeNode> actions = new HashMap<String, DefaultMutableTreeNode>();

        // role.category.action(a, b, c) = List of allowed roles
        Map<String, DefaultMutableTreeNode> contexts = new HashMap<String, DefaultMutableTreeNode>();

        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("Security By Role");
        for (String role : repositoryAccessService.getAllAvailableRoles()) {
            DefaultMutableTreeNode roleNode = new DefaultMutableTreeNode(role);
            roles.put(role, roleNode);
            rootNode.add(roleNode);
        }

        Map<AccessRuleKey, Set<String>> accessRuleMap = repositoryAccessService.getAccessRuleMap();

        // for every rule in place
        for (AccessRuleKey key : accessRuleMap.keySet()) {

            // for every role that has access to the given
            // category.action(context)
            for (String role : accessRuleMap.get(key)) {
                DefaultMutableTreeNode roleNode = roles.get(role);

                // category
                String categoryName = key.getCategory();
                String fullCategoryName = role.toString() + "." + categoryName;
                DefaultMutableTreeNode cateogryNode = categories.get(fullCategoryName);
                if (cateogryNode == null) {
                    cateogryNode = new DefaultMutableTreeNode(categoryName);
                    categories.put(fullCategoryName, cateogryNode);
                    roleNode.add(cateogryNode);
                }

                // action
                String actionName = key.getAction();
                String fullActionName = fullCategoryName + "." + actionName;
                DefaultMutableTreeNode actionNode = actions.get(fullActionName);
                if (actionNode == null) {
                    actionNode = new DefaultMutableTreeNode(actionName);
                    actions.put(fullActionName, actionNode);
                    cateogryNode.add(actionNode);
                }

                // contexts
                String contextsName = getContextString(key.getContexts());
                String contextsFullName = fullActionName + "." + contextsName;
                DefaultMutableTreeNode contextsNode = contexts.get(contextsFullName);
                if (contextsNode == null) {
                    contextsNode = new DefaultMutableTreeNode(contextsName);
                    contexts.put(contextsFullName, contextsNode);
                    actionNode.add(contextsNode);
                }
            }
        }
        return new JTree(rootNode);
    }

    private String getContextString(List<Class<?>> contexts) {
        StringBuilder fullContext = new StringBuilder("(");
        for (Class<?> contextType : contexts) {
            fullContext.append(" " + contextType.getSimpleName());
        }
        fullContext.append(" )");
        return fullContext.toString();
    }

    private String getAboutMessage() {
        String help = "In the left panel, security is presented in a tree\n"
                + "structure by category, with the leaves indicating\n"
                + "which roles a user must possess with respect to the\n"
                + "context (in parentheses). The right panel presents\n"
                + "security by role, also in a tree structure, to indicate\n"
                + "precisely which category, action, and context can\n"
                + "be executed by the top level role. Each panel presents\n"
                + "the same information but in slightly different forms.";
        return "Security Viewer\nVersion 1.01\n\n" + help;
    }

    public static void main(String[] args) throws IOException, SAXException {
        new SecurityViewer();
    }
}
