package net.gamers411.nodeka.umc.plugin.nembot;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

public class event_gui extends JFrame
{
  private nembot core;
  private DefaultTableModel jEventModel;
  private JTextField jAction;
  private JTable jActionTable;
  private JButton jAdd;
  private JButton jClear;
  private JSpinner jConditionPercent;
  private JComboBox jConditionPool;
  private JSpinner jCost;
  private JComboBox jCostPool;
  private JButton jDelete;
  private JLabel jLabel1;
  private JLabel jLabel2;
  private JLabel jLabel3;
  private JLabel jLabel4;
  private JLabel jLabel5;
  private JLabel jLabel6;
  private JLabel jLabel7;
  private JLabel jLabel8;
  private JLabel jLabel9;
  private JSpinner jLagTime;
  private JComboBox jPosition;
  private JTextField jPrevent;
  private JButton jSave;
  private JScrollPane jScrollPane1;
  private JCheckBox jStatus;

  public event_gui(nembot core)
  {
    this.core = core;

    this.jEventModel = new DefaultTableModel(new Object[0][], new String[] { "Action", "Condition" })
    {
      Class[] types = { String.class, String.class };

      public Class getColumnClass(int columnIndex)
      {
        return this.types[columnIndex];
      }

      public boolean isCellEditable(int rowIndex, int mColIndex) {
        return false;
      }
    };
    initComponents();

    ListSelectionListener listener = new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent e) {
        event_gui.this.jActionTableSelectionPerformed(e);
      }
    };
    this.jActionTable.getSelectionModel().addListSelectionListener(listener);
    this.jActionTable.getColumnModel().getColumn(0).setPreferredWidth(150);

    refreshGUI();
  }

  protected void refreshGUI() {
    loadEvents();
    setFunctions(this.core.settings.events_enabled);
    this.jStatus.setSelected(this.core.settings.events_enabled);
  }

  private void setFunctions(boolean enabled) {
    this.jAction.setEnabled(enabled);
    this.jActionTable.setEnabled(enabled);
    this.jAdd.setEnabled(enabled);
    this.jConditionPercent.setEnabled(enabled);
    this.jConditionPool.setEnabled(enabled);
    this.jCost.setEnabled(enabled);
    this.jCostPool.setEnabled(enabled);
    this.jDelete.setEnabled(enabled);
    this.jPosition.setEnabled(enabled);
    this.jPrevent.setEnabled(enabled);
    this.jSave.setEnabled(enabled);
    this.jLagTime.setEnabled(enabled);
  }

  protected void loadEvents() {
    if (this.core.settings.events == null) {
      return;
    }
    for (int x = this.jEventModel.getRowCount(); x > 0; x--)
      this.jEventModel.removeRow(x - 1);
    event tmp = null;
    for (int x = 0; x < 4; x++)
      for (int y = 0; y < this.core.settings.events[x].size(); y++) {
        tmp = (event)this.core.settings.events[x].get(y);
        this.jEventModel.addRow(new Object[] { tmp.action, (x == 2 ? "SP" : x == 1 ? "MP" : x == 0 ? "HP" : "EP") + "<" + tmp.condition_percent + "%" });
      }
  }

  private int[] lin2multiarray(int loc)
  {
    int[] ret = new int[2];

    for (int x = 0; x < 4; x++) {
      for (int y = 0; y < this.core.settings.events[x].size(); y++) {
        if (x + y == loc) {
          ret[0] = x;
          ret[1] = y;
        }
      }
    }

    return ret;
  }
  private void jActionTableSelectionPerformed(ListSelectionEvent e) {
    int[] loc = lin2multiarray(this.jActionTable.getSelectedRow());

    event tmp = (event)this.core.settings.events[loc[0]].get(loc[1]);
    this.jAction.setText(tmp.action);
    this.jConditionPercent.setValue(Integer.valueOf(tmp.condition_percent));
    this.jConditionPool.setSelectedIndex(loc[0]);
    this.jCost.setValue(Integer.valueOf(tmp.cost));
    this.jCostPool.setSelectedIndex(tmp.cost_pool);
    this.jPosition.setSelectedIndex(tmp.position);
    this.jPrevent.setText(tmp.prevent);
    this.jLagTime.setValue(Integer.valueOf(tmp.lag));
  }

  private void initComponents()
  {
    this.jScrollPane1 = new JScrollPane();
    this.jActionTable = new JTable();
    this.jAdd = new JButton();
    this.jDelete = new JButton();
    this.jLabel2 = new JLabel();
    this.jAction = new JTextField();
    this.jLabel3 = new JLabel();
    this.jConditionPool = new JComboBox();
    this.jLabel4 = new JLabel();
    this.jConditionPercent = new JSpinner();
    this.jLabel5 = new JLabel();
    this.jLabel6 = new JLabel();
    this.jCostPool = new JComboBox();
    this.jCost = new JSpinner();
    this.jPosition = new JComboBox();
    this.jLabel7 = new JLabel();
    this.jLabel8 = new JLabel();
    this.jPrevent = new JTextField();
    this.jSave = new JButton();
    this.jStatus = new JCheckBox();
    this.jLabel1 = new JLabel();
    this.jLagTime = new JSpinner();
    this.jLabel9 = new JLabel();
    this.jClear = new JButton();

    setDefaultCloseOperation(2);
    setTitle("Pool Events");

    this.jActionTable.setModel(this.jEventModel);
    this.jScrollPane1.setViewportView(this.jActionTable);

    this.jAdd.setText("Add");
    this.jAdd.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        event_gui.this.jAddActionPerformed(evt);
      }
    });
    this.jDelete.setText("Delete");
    this.jDelete.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        event_gui.this.jDeleteActionPerformed(evt);
      }
    });
    this.jLabel2.setText("Action");
    this.jLabel2.setToolTipText("The skill or spell used to replenish a pool.");

    this.jAction.setToolTipText("The skill or spell used to replenish a pool.");

    this.jLabel3.setText("Condition");
    this.jLabel3.setToolTipText("When the pool falls bellow the given percentage.");

    this.jConditionPool.setModel(new DefaultComboBoxModel(new String[] { "HP", "MP", "SP", "EP" }));

    this.jLabel4.setText("<");

    this.jConditionPercent.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent evt) {
        event_gui.this.jConditionPercentStateChanged(evt);
      }
    });
    this.jLabel5.setText("%");

    this.jLabel6.setText("Costs");
    this.jLabel6.setToolTipText("The cost to use or cast the given skill or spell.");

    this.jCostPool.setModel(new DefaultComboBoxModel(new String[] { "MP", "SP", "EP" }));

    this.jCost.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent evt) {
        event_gui.this.jCostStateChanged(evt);
      }
    });
    this.jPosition.setModel(new DefaultComboBoxModel(new String[] { "Rest - Stand", "Stand - Stand", "Stand - Fight", "Fight - Fight" }));

    this.jLabel7.setText("Posit.");
    this.jLabel7.setToolTipText("When the spell can be used, given in the skills command.");

    this.jLabel8.setText("Prevent");
    this.jLabel8.setToolTipText("The name of the prevention.");

    this.jPrevent.setToolTipText("The name of the prevention.");
    this.jPrevent.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        event_gui.this.jPreventActionPerformed(evt);
      }
    });
    this.jSave.setText("Save");
    this.jSave.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        event_gui.this.jSaveActionPerformed(evt);
      }
    });
    this.jStatus.setText("Pool Events");
    this.jStatus.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        event_gui.this.jStatusActionPerformed(evt);
      }
    });
    this.jLabel1.setText("Lag");
    this.jLabel1.setToolTipText("The overhead time before casting again, can include prevention time as well.");

    this.jLagTime.setToolTipText("The overhead time before casting again, can include prevention time as well.");
    this.jLagTime.addPropertyChangeListener(new PropertyChangeListener() {
      public void propertyChange(PropertyChangeEvent evt) {
        event_gui.this.jLagTimePropertyChange(evt);
      }
    });
    this.jLabel9.setText("ms");

    this.jClear.setText("Clear");
    this.jClear.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        event_gui.this.jClearActionPerformed(evt);
      }
    });
    GroupLayout layout = new GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addContainerGap().addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(this.jScrollPane1, GroupLayout.Alignment.TRAILING, -1, 284, 32767).addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup().addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false).addComponent(this.jLabel2, -1, -1, 32767).addComponent(this.jLabel3, -1, -1, 32767).addComponent(this.jLabel8, -1, -1, 32767)).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false).addComponent(this.jAction, GroupLayout.Alignment.TRAILING).addComponent(this.jPrevent).addGroup(layout.createSequentialGroup().addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(this.jConditionPool, -2, -1, -2).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(this.jLabel4).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(this.jConditionPercent, -2, 41, -2).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(this.jLabel5))).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false).addComponent(this.jLabel1, -1, -1, 32767).addComponent(this.jLabel7, -1, -1, 32767).addComponent(this.jLabel6, -1, 27, 32767)).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup().addComponent(this.jCost, -1, 42, 32767).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(this.jCostPool, -2, -1, -2)).addComponent(this.jPosition, 0, 87, 32767).addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup().addComponent(this.jLagTime, -1, 70, 32767).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(this.jLabel9)))).addComponent(this.jStatus).addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup().addComponent(this.jAdd).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(this.jDelete)).addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup().addComponent(this.jClear).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(this.jSave))).addContainerGap()));

    layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addContainerGap().addComponent(this.jStatus).addGap(1, 1, 1).addComponent(this.jScrollPane1, -2, 87, -2).addGap(7, 7, 7).addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(this.jDelete).addComponent(this.jAdd)).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(this.jLabel6).addComponent(this.jCostPool, -2, -1, -2).addComponent(this.jCost, -2, -1, -2).addComponent(this.jLabel2).addComponent(this.jAction, -2, -1, -2)).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(this.jLabel5).addComponent(this.jLabel7).addComponent(this.jPosition, -2, -1, -2).addComponent(this.jLabel3).addComponent(this.jConditionPool, -2, -1, -2).addComponent(this.jLabel4).addComponent(this.jConditionPercent, -2, -1, -2)).addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING).addGroup(layout.createSequentialGroup().addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(this.jLabel8).addComponent(this.jPrevent, -2, -1, -2).addComponent(this.jLabel1).addComponent(this.jLabel9).addComponent(this.jLagTime, -2, -1, -2)).addContainerGap(38, 32767)).addGroup(layout.createSequentialGroup().addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(this.jSave).addComponent(this.jClear)).addContainerGap()))));

    pack();
  }

  private void jAddActionPerformed(ActionEvent evt) {
    if (this.jAction.getText().length() == 0) {
      JOptionPane.showMessageDialog(this, "The action input box is a required field.", "Error", 0);
    }

    event tmp = new event();

    tmp.action = this.jAction.getText();
    tmp.condition_percent = Integer.parseInt(this.jConditionPercent.getValue().toString());
    tmp.cost = Integer.parseInt(this.jCost.getValue().toString());
    tmp.cost_pool = this.jCostPool.getSelectedIndex();
    tmp.position = this.jPosition.getSelectedIndex();
    tmp.prevent = this.jPrevent.getText();
    tmp.lag = Integer.parseInt(this.jLagTime.getValue().toString());

    this.core.settings.events[this.jConditionPool.getSelectedIndex()].add(tmp);

    loadEvents();
  }

  private void jDeleteActionPerformed(ActionEvent evt) {
    int loc = this.jActionTable.getSelectedRow();
    if (loc != -1) {
      this.jEventModel.removeRow(loc);
      int[] event_loc = lin2multiarray(loc);
      this.core.settings.events[event_loc[0]].remove(event_loc[1]);
    }
  }

  private void jSaveActionPerformed(ActionEvent evt) {
    if (this.jActionTable.getSelectedRow() != -1) {
      int[] loc = lin2multiarray(this.jActionTable.getSelectedRow());

      event tmp = new event();

      tmp.action = this.jAction.getText();
      tmp.condition_percent = Integer.parseInt(this.jConditionPercent.getValue().toString());
      tmp.cost = Integer.parseInt(this.jCost.getValue().toString());
      tmp.lag = Integer.parseInt(this.jLagTime.getValue().toString());
      tmp.cost_pool = this.jCostPool.getSelectedIndex();
      tmp.position = this.jPosition.getSelectedIndex();
      tmp.prevent = this.jPrevent.getText();

      this.core.settings.events[loc[0]].set(loc[1], tmp);

      loadEvents();
    }
  }

  private void jCostStateChanged(ChangeEvent evt) {
    if (Integer.parseInt(this.jCost.getValue().toString()) < 0)
      this.jCost.setValue(Integer.valueOf(0));
  }

  private void jConditionPercentStateChanged(ChangeEvent evt) {
    if (Integer.parseInt(this.jCost.getValue().toString()) < 0)
      this.jCost.setValue(Integer.valueOf(0));
    if (Integer.parseInt(this.jCost.getValue().toString()) > 100)
      this.jCost.setValue(Integer.valueOf(100));
  }

  private void jStatusActionPerformed(ActionEvent evt) {
    setFunctions(this.jStatus.isSelected());

    this.core.settings.events_enabled = this.jStatus.isSelected();
  }

  private void jPreventActionPerformed(ActionEvent evt) {
    Pattern p = Pattern.compile("^This action cannot be performed while (.*).");
    Matcher m = p.matcher(this.jPrevent.getText());
    if (m.matches()) {
      this.jPrevent.setText(m.group(1).toString());
    }

    p = Pattern.compile("^[\\s]{4}(.*) \\([\\d:]+\\)");
    m = p.matcher(this.jPrevent.getText());
    if (m.matches())
      this.jPrevent.setText(m.group(1).toString());
  }

  private void jLagTimePropertyChange(PropertyChangeEvent evt)
  {
    if (Integer.parseInt(this.jLagTime.getValue().toString()) < -1)
      this.jLagTime.setValue(Integer.valueOf(-1));
  }

  private void jClearActionPerformed(ActionEvent evt)
  {
    this.jActionTable.removeRowSelectionInterval(0, this.jActionTable.getRowCount() - 1);
    this.jAction.setText("");
    this.jConditionPercent.setValue(Integer.valueOf(0));
    this.jConditionPool.setSelectedIndex(0);
    this.jCost.setValue(Integer.valueOf(0));
    this.jCostPool.setSelectedIndex(0);
    this.jPosition.setSelectedIndex(0);
    this.jPrevent.setText("");
    this.jLagTime.setValue(Integer.valueOf(0));
  }
}