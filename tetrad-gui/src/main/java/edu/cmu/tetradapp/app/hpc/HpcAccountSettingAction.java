package edu.cmu.tetradapp.app.hpc;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import edu.cmu.tetrad.util.JOptionUtils;
import edu.cmu.tetradapp.app.TetradDesktop;
import edu.cmu.tetradapp.util.DesktopController;
import edu.pitt.dbmi.tetrad.db.entity.ComputingAccount;

/**
 * 
 * Nov 1, 2016 12:42:35 PM
 * 
 * @author Chirayu (Kong) Wongchokprasitti, PhD
 * 
 */
public class HpcAccountSettingAction extends AbstractAction {

    private static final long serialVersionUID = -4084211497363128243L;

    public HpcAccountSettingAction() {
	super("HPC Account");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
	TetradDesktop desktop = (TetradDesktop) DesktopController.getInstance();
	ComputingAccountManager manager = desktop.getComputingAccountManager();

	JComponent comp = buildHpcAccountSettingComponent(manager);
	JOptionPane.showMessageDialog(JOptionUtils.centeringComp(), comp,
		"High-Performance Computing Account Setting",
		JOptionPane.PLAIN_MESSAGE);
    }
    
    private static JComponent buildHpcAccountSettingComponent(
	    final ComputingAccountManager manager) {
	// Get ComputingAccount from DB
	final DefaultListModel<ComputingAccount> listModel = new DefaultListModel<ComputingAccount>();
	
	for (ComputingAccount account : manager.getComputingAccounts()) {
	    listModel.addElement(account);
	}
	
	// JSplitPane
	final JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
	
	// Left pane -> JList (parent pane)
	JPanel leftPanel = new JPanel(new BorderLayout());

	// Right pane -> ComputingAccountEditor
	final JPanel accountDetailPanel = new JPanel(new BorderLayout());
	
	splitPane.setLeftComponent(leftPanel);
	splitPane.setRightComponent(accountDetailPanel);

	
	
	// Center Panel
	final JList<ComputingAccount> accountList = new JList<>(listModel);
	accountList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	accountList.setLayoutOrientation(JList.VERTICAL);
	accountList.setSelectedIndex(-1);
	accountList.addListSelectionListener(new ListSelectionListener() {

	    @Override
	    public void valueChanged(ListSelectionEvent e) {
		if (e.getValueIsAdjusting())
		    return;
		int selectedIndex = ((JList<?>) e.getSource())
			.getSelectedIndex();
		// Show or remove the detail
		accountDetailPanel.removeAll();
		if (selectedIndex > -1) {
		    ComputingAccount computingAccount = listModel
			    .get(selectedIndex);
		    System.out.println(computingAccount);
		    accountDetailPanel.add(new ComputingAccountEditor(splitPane, listModel, manager,
			    computingAccount), BorderLayout.CENTER);
		}
		accountDetailPanel.updateUI();
	    }
	});

	// Left Panel
	JPanel buttonPanel = new JPanel(new BorderLayout());
	JButton addButton = new JButton("Add");
	addButton.setSize(new Dimension(14, 8));
	addButton.addActionListener(new ActionListener() {

	    @Override
	    public void actionPerformed(ActionEvent e) {
		// Show the empty ComputingAccountEditor
		accountDetailPanel.removeAll();
		accountDetailPanel.add(new ComputingAccountEditor(splitPane, listModel, manager,
			new ComputingAccount()), BorderLayout.CENTER);
		accountDetailPanel.updateUI();
	    }
	});
	buttonPanel.add(addButton, BorderLayout.WEST);

	JButton removeButton = new JButton("Remove");
	removeButton.setSize(new Dimension(14, 8));
	removeButton.addActionListener(new ActionListener() {

	    @Override
	    public void actionPerformed(ActionEvent e) {
		if (accountList.isSelectionEmpty())
		    return;
		int selectedIndex = accountList.getSelectedIndex();
		if (selectedIndex > -1) {
		    ComputingAccount computingAccount = listModel
			    .get(selectedIndex);
		    // Pop up the confirm dialog
		    int option = JOptionPane.showConfirmDialog(
			    accountDetailPanel, "Are you sure that you want to delete "
				    + computingAccount + " ?",
			    "HPC Account Setting", JOptionPane.YES_NO_OPTION,
			    JOptionPane.QUESTION_MESSAGE);

		    // If yes, remove it from DB and listModel
		    if (option == JOptionPane.YES_OPTION) {
			manager.removeAccount(computingAccount);
			listModel.remove(selectedIndex);
		    }

		}
	    }
	});
	buttonPanel.add(removeButton, BorderLayout.EAST);
	leftPanel.add(buttonPanel, BorderLayout.NORTH);

	JScrollPane accountListScroller = new JScrollPane(accountList);
	leftPanel.add(accountListScroller, BorderLayout.CENTER);

	int minWidth = 300;
	int minHeight = 200;
	int screenWidth = Toolkit.getDefaultToolkit().getScreenSize().width;
	int screenHeight = Toolkit.getDefaultToolkit().getScreenSize().height;
	int frameWidth = screenWidth / 2;
	int frameHeight = screenHeight / 2;
	frameWidth = minWidth > frameWidth ? minWidth : frameWidth;
	frameHeight = minHeight > frameHeight ? minHeight : frameHeight;

	splitPane.setDividerLocation(frameWidth / 4);
	accountListScroller.setPreferredSize(new Dimension(frameWidth / 4,
		frameHeight));
	accountDetailPanel.setPreferredSize(new Dimension(frameWidth * 3 / 4,
		frameHeight));

	return splitPane;
    }

}
