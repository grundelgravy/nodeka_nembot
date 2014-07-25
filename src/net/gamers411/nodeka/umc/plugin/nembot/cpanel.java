package net.gamers411.nodeka.umc.plugin.nembot;

import com.lsd.umc.script.ScriptInterface;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.HierarchyBoundsListener;
import java.awt.event.HierarchyEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

public class cpanel extends JFrame
{
  protected nembot corescript;
  private JFileChooser chooseFile;
  private ScriptInterface script;
  private DefaultTableModel jScriptMobModel;
  private updater update_panel;
  public minibar ministatus;
  private event_gui pool_gui;
  private JCheckBox jAlwaysOnTop;
  private JButton jApplyConfig;
  private JButton jApplyScript;
  private JTextField jAttack;
  private JLabel jCurrentScript;
  private JLabel jCurrentVersion;
  private JButton jEventsGUI;
  private JLabel jLabel1;
  private JLabel jLabel10;
  private JLabel jLabel11;
  private JLabel jLabel12;
  private JLabel jLabel13;
  private JLabel jLabel14;
  private JLabel jLabel15;
  private JLabel jLabel16;
  private JLabel jLabel17;
  private JLabel jLabel18;
  private JLabel jLabel19;
  private JLabel jLabel2;
  private JLabel jLabel3;
  private JLabel jLabel4;
  private JLabel jLabel5;
  private JLabel jLabel6;
  private JLabel jLabel8;
  private JLabel jLabel9;
  private JLabel jLatestVersion;
  private JLayeredPane jLayeredPane1;
  private JButton jLoad;
  private JButton jLoadScript;
  private JSpinner jLowHPsleep;
  private JSpinner jLowPool;
  private JSpinner jMinHP;
  private JButton jMiniBar;
  private JButton jOpenEditor;
  private JCheckBox jPSkip;
  private JPanel jPanel1;
  private JPanel jPanel2;
  private JPanel jPanel3;
  private JTextField jPreAttack;
  private JComboBox jProfile;
  private JProgressBar jProgress;
  private JCheckBox jRecall;
  private JButton jRegister;
  private JButton jSaveConfig;
  private JTable jScriptMobs;
  private JButton jScriptSave;
  private JComboBox jScripts;
  private JScrollPane jScrollPane1;
  private JSeparator jSeparator1;
  private JSeparator jSeparator2;
  private JSpinner jSleepon;
  private JTabbedPane jTabbedPane1;
  private JButton jUpdates;

  public cpanel(nembot corescript)
  {
    this.corescript = corescript;
    this.script = corescript.script;

    this.chooseFile = new JFileChooser();
    this.chooseFile.setFileFilter(new botFilter());

    this.ministatus = new minibar(this.corescript);
    this.ministatus.setLocationRelativeTo(this);

    this.pool_gui = new event_gui(this.corescript);
    this.pool_gui.setLocationRelativeTo(this);

    this.jScriptMobModel = new DefaultTableModel(new Object[0][], new String[] { "Kill", "Name", "Attack", "Skip" })
    {
      Class[] types = { Boolean.class, String.class, String.class, Boolean.class };

      @Override
      public Class getColumnClass(int columnIndex)
      {
        return this.types[columnIndex];
      }
    };
    initComponents();
    String vers;
    if (this.corescript.socket.status != -1)
    {
      vers = this.corescript.socket.getVersion();

      if (this.corescript.version.compareTo(vers) >= 0) {
        this.jCurrentVersion.setForeground(Color.GREEN);
      } else if (this.corescript.version.compareTo(vers) < 0) {
        this.jCurrentVersion.setForeground(Color.RED);

        this.update_panel = new updater(this.corescript, this.chooseFile);
        this.update_panel.setLocationRelativeTo(null);
        this.update_panel.setVisible(true);
      }
    }
    else {
      vers = "Unknown";
    }
    this.jCurrentVersion.setText(this.corescript.version);
    this.jLatestVersion.setText(vers);

    this.jCurrentScript.setText("");

    populateProfiles();
    populateScripts();
    refreshVars();
  }

  protected boolean checkValidation() {
    this.pool_gui.setVisible(false);

    this.jScriptSave.setEnabled(false);
    this.jOpenEditor.setEnabled(false);
    this.jLowPool.setEnabled(false);
    this.jLowHPsleep.setEnabled(false);
    this.jEventsGUI.setEnabled(false);

    this.jScriptSave.setEnabled(true);
    this.jOpenEditor.setEnabled(true);
    this.jLowPool.setEnabled(true);
    this.jLowHPsleep.setEnabled(true);
    this.jEventsGUI.setEnabled(true);

    this.pool_gui.loadEvents();

    return true;
  }

  public void loadScript()
  {
    if (this.corescript.area == null) {
      this.jCurrentScript.setText("Unable to load...");
      return;
    }
    this.jCurrentScript.setText(this.corescript.area.filename);

    for (int x = this.jScriptMobModel.getRowCount(); x > 0; x--) {
      this.jScriptMobModel.removeRow(x - 1);
    }
    for (int x = 0; x < this.corescript.area.mobs.length; x++) {
      this.jScriptMobModel.addRow(new Object[] { new Boolean(this.corescript.area.mobs[x].kill), this.corescript.area.mobs[x].name.toString(), this.corescript.area.mobs[x].attack.toString(), new Boolean(this.corescript.area.mobs[x].skip) });
    }

    this.ministatus.setTitle("Status for " + this.corescript.area.filename);
    this.jProgress.setValue(0);
  }

  public void populateScripts() {
    File dir = new File(this.corescript.settings.areas_dir);
    String[] children = dir.list();
    this.jScripts.removeAllItems();
    if (children != null)
    {
      for (int i = 0; i < children.length; i++)
      {
        if (((children[i].startsWith("NOD_")) || (children[i].startsWith("NODe_"))) && (children[i].endsWith(".txt")) && (this.corescript.validated))
          this.jScripts.addItem(children[i]);
        else if ((children[i].startsWith("NOD_")) && (children[i].endsWith(".txt")) && (!this.corescript.validated))
          this.jScripts.addItem(children[i]);
      }
    }
    this.jScripts.addItem("From file...");
  }

  public void populateProfiles() {
    File dir = new File(this.corescript.settings.profiles_dir);
    String[] children = dir.list();
    this.jProfile.removeAllItems();
    this.jProfile.addItem("Create a profile...");
    if (children != null)
    {
      int x = 1;
      for (int i = 0; i < children.length; i++)
      {
        if (children[i].endsWith(".bot")) {
          this.jProfile.addItem(children[i]);
          if (this.corescript.settings.file.endsWith(children[i]))
            this.jProfile.setSelectedIndex(x);
          x++;
        }
      }
    }

    this.jProfile.addItem("From file...");
  }

  private void refreshVars() {
    this.jAttack.setText(this.corescript.settings.attack);
    this.jSleepon.setValue(Integer.valueOf(this.corescript.settings.sleepon));
    this.jPSkip.setSelected(this.corescript.settings.player_skip);
    this.jPreAttack.setText(this.corescript.settings.pre_attack);
    this.jMinHP.setValue(Integer.valueOf(this.corescript.settings.min_hp));
    this.jRecall.setSelected(this.corescript.settings.recall);
    this.jLowPool.setValue(Integer.valueOf(this.corescript.settings.recover_pool));
    this.jLowHPsleep.setValue(Integer.valueOf(this.corescript.settings.recover_sleepon));
  }

  private void storeVars() {
    this.corescript.settings.set_attack(this.jAttack.getText());
    this.corescript.settings.set_sleepon(Integer.parseInt(this.jSleepon.getValue().toString()));
    this.corescript.settings.set_playerskip(this.jPSkip.isSelected());
    this.corescript.settings.set_preattack(this.jPreAttack.getText());
    this.corescript.settings.set_minhp(Integer.parseInt(this.jMinHP.getValue().toString()));
    this.corescript.settings.set_recall(this.jRecall.isSelected());
    this.corescript.settings.set_recoverpools(Integer.parseInt(this.jLowPool.getValue().toString()));
    this.corescript.settings.set_recoversleepon(Integer.parseInt(this.jLowHPsleep.getValue().toString()));
  }

  public void setProgress(int percent) {
    this.jProgress.setValue(percent);
    this.ministatus.jProgress.setValue(percent);
  }

  private void initComponents()
  {
    this.jLayeredPane1 = new JLayeredPane();
    this.jAlwaysOnTop = new JCheckBox();
    this.jTabbedPane1 = new JTabbedPane();
    this.jPanel1 = new JPanel();
    this.jLabel1 = new JLabel();
    this.jProfile = new JComboBox();
    this.jLoad = new JButton();
    this.jLabel2 = new JLabel();
    this.jAttack = new JTextField();
    this.jLabel3 = new JLabel();
    this.jSleepon = new JSpinner();
    this.jPSkip = new JCheckBox();
    this.jLabel4 = new JLabel();
    this.jPreAttack = new JTextField();
    this.jSaveConfig = new JButton();
    this.jApplyConfig = new JButton();
    this.jRegister = new JButton();
    this.jLabel16 = new JLabel();
    this.jMinHP = new JSpinner();
    this.jRecall = new JCheckBox();
    this.jLabel17 = new JLabel();
    this.jLabel18 = new JLabel();
    this.jLowPool = new JSpinner();
    this.jLabel19 = new JLabel();
    this.jLowHPsleep = new JSpinner();
    this.jEventsGUI = new JButton();
    this.jPanel2 = new JPanel();
    this.jLabel5 = new JLabel();
    this.jScripts = new JComboBox();
    this.jLoadScript = new JButton();
    this.jSeparator1 = new JSeparator();
    this.jLabel6 = new JLabel();
    this.jCurrentScript = new JLabel();
    this.jProgress = new JProgressBar();
    this.jScrollPane1 = new JScrollPane();
    this.jScriptMobs = new JTable();
    this.jApplyScript = new JButton();
    this.jScriptSave = new JButton();
    this.jOpenEditor = new JButton();
    this.jMiniBar = new JButton();
    this.jPanel3 = new JPanel();
    this.jLabel8 = new JLabel();
    this.jLabel9 = new JLabel();
    this.jCurrentVersion = new JLabel();
    this.jLatestVersion = new JLabel();
    this.jSeparator2 = new JSeparator();
    this.jLabel10 = new JLabel();
    this.jLabel11 = new JLabel();
    this.jLabel12 = new JLabel();
    this.jLabel13 = new JLabel();
    this.jLabel14 = new JLabel();
    this.jLabel15 = new JLabel();
    this.jUpdates = new JButton();

    setDefaultCloseOperation(2);
    setTitle("Nembot v" + this.corescript.version);
    setMinimumSize(new Dimension(260, 345));

    this.jLayeredPane1.addHierarchyBoundsListener(new HierarchyBoundsListener() {
      public void ancestorMoved(HierarchyEvent evt) {
      }
      public void ancestorResized(HierarchyEvent evt) {
        cpanel.this.jLayeredPane1AncestorResized(evt);
      }
    });
    this.jAlwaysOnTop.setText("Always on Top");
    this.jAlwaysOnTop.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        cpanel.this.jAlwaysOnTopActionPerformed(evt);
      }
    });
    this.jAlwaysOnTop.setBounds(146, 10, 95, 20);
    this.jLayeredPane1.add(this.jAlwaysOnTop, JLayeredPane.DEFAULT_LAYER);

    this.jLabel1.setText("Profile");
    this.jLabel1.setToolTipText("Load custom settings for your characters.");

    this.jProfile.addItemListener(new ItemListener() {
      public void itemStateChanged(ItemEvent evt) {
        cpanel.this.jProfileItemStateChanged(evt);
      }
    });
    this.jLoad.setText("New");
    this.jLoad.setMaximumSize(new Dimension(53, 25));
    this.jLoad.setMinimumSize(new Dimension(53, 25));
    this.jLoad.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        cpanel.this.jLoadActionPerformed(evt);
      }
    });
    this.jLabel2.setText("Attack");
    this.jLabel2.setToolTipText("Your default attack for every mob without a custom attack.");

    this.jAttack.setText("jTextField1");

    this.jLabel3.setText("Sleepon");
    this.jLabel3.setToolTipText("How many seconds before tick would you like to sleep on?");

    this.jSleepon.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent evt) {
        cpanel.this.jSleeponStateChanged(evt);
      }
    });
    this.jPSkip.setText("Skip Players");
    this.jPSkip.setToolTipText("Allows you to skip rooms that already have players in them.");

    this.jLabel4.setText("PreAttack");
    this.jLabel4.setToolTipText("Mainly used by Necromancers to summon their army. This allows you to cast a skill or spell prior to attack runs in the room.");

    this.jPreAttack.setText("jTextField1");

    this.jSaveConfig.setText("Save");
    this.jSaveConfig.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        cpanel.this.jSaveConfigActionPerformed(evt);
      }
    });
    this.jApplyConfig.setText("Apply");
    this.jApplyConfig.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        cpanel.this.jApplyConfigActionPerformed(evt);
      }
    });
    this.jRegister.setText("Register");
    this.jRegister.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        cpanel.this.jRegisterActionPerformed(evt);
      }
    });
    this.jLabel16.setText("Min HP");
    this.jLabel16.setToolTipText("THe minimum hp before the script ends in percentages.");

    this.jMinHP.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent evt) {
        cpanel.this.jMinHPStateChanged(evt);
      }
    });
    this.jRecall.setText("Recall on low HP");

    this.jLabel17.setText("Low Pools Settings");

    this.jLabel18.setText("Percent");

    this.jLowPool.setEnabled(false);

    this.jLabel19.setText("Sleep On");

    this.jLowHPsleep.setEnabled(false);

    this.jEventsGUI.setText("Events");
    this.jEventsGUI.setEnabled(false);
    this.jEventsGUI.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        cpanel.this.jEventsGUIActionPerformed(evt);
      }
    });
    GroupLayout jPanel1Layout = new GroupLayout(this.jPanel1);
    this.jPanel1.setLayout(jPanel1Layout);
    jPanel1Layout.setHorizontalGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(jPanel1Layout.createSequentialGroup().addContainerGap().addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(jPanel1Layout.createSequentialGroup().addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(this.jLabel1, GroupLayout.Alignment.TRAILING, -2, 47, -2).addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.TRAILING).addComponent(this.jLabel2, -2, 47, -2).addComponent(this.jLabel4, -2, 47, -2))).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup().addComponent(this.jProfile, 0, 99, 32767).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(this.jLoad, -2, -1, -2)).addComponent(this.jAttack, -1, 158, 32767).addComponent(this.jPreAttack, -1, 158, 32767))).addGroup(jPanel1Layout.createSequentialGroup().addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.TRAILING, false).addGroup(GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup().addComponent(this.jLabel16, -2, 46, -2).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(this.jMinHP)).addGroup(GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup().addComponent(this.jLabel3, -2, 47, -2).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(this.jSleepon, -2, 43, -2))).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(this.jPSkip).addComponent(this.jRecall))).addGroup(jPanel1Layout.createSequentialGroup().addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.TRAILING, false).addGroup(GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup().addGap(10, 10, 10).addComponent(this.jLabel18).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(this.jLowPool)).addComponent(this.jLabel17, GroupLayout.Alignment.LEADING)).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(this.jLabel19).addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED).addComponent(this.jLowHPsleep, -2, 40, -2)).addGroup(GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup().addComponent(this.jRegister).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(this.jSaveConfig).addGap(8, 8, 8).addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING, false).addComponent(this.jApplyConfig, -1, -1, 32767).addComponent(this.jEventsGUI, -1, -1, 32767)))).addContainerGap()));

    jPanel1Layout.setVerticalGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(jPanel1Layout.createSequentialGroup().addContainerGap().addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(this.jLoad, -2, -1, -2).addComponent(this.jProfile, -2, -1, -2).addComponent(this.jLabel1)).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(this.jAttack, -2, -1, -2).addComponent(this.jLabel2)).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(this.jPreAttack, -2, -1, -2).addComponent(this.jLabel4)).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(this.jSleepon, -2, -1, -2).addComponent(this.jLabel3).addComponent(this.jPSkip)).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(this.jMinHP, -2, -1, -2).addComponent(this.jLabel16).addComponent(this.jRecall)).addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED).addComponent(this.jLabel17).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(this.jLabel18).addComponent(this.jLowPool, -2, -1, -2).addComponent(this.jLabel19).addComponent(this.jLowHPsleep, -2, -1, -2)).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 15, 32767).addComponent(this.jEventsGUI).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(this.jRegister).addComponent(this.jSaveConfig).addComponent(this.jApplyConfig)).addContainerGap()));

    this.jTabbedPane1.addTab("Settings", this.jPanel1);

    this.jLabel5.setText("Scripts");

    this.jLoadScript.setText("Load");
    this.jLoadScript.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        cpanel.this.jLoadScriptActionPerformed(evt);
      }
    });
    this.jLabel6.setText("Script");

    this.jCurrentScript.setText("jLabel7");
    this.jCurrentScript.setMaximumSize(new Dimension(30, 14));
    this.jCurrentScript.setMinimumSize(new Dimension(30, 14));
    this.jCurrentScript.setPreferredSize(new Dimension(30, 14));

    this.jProgress.setToolTipText("Script Completion");
    this.jProgress.setStringPainted(true);

    this.jScriptMobs.setModel(this.jScriptMobModel);
    TableColumn col = this.jScriptMobs.getColumnModel().getColumn(0);
    col.setPreferredWidth(25);
    col.setResizable(false);
    col = this.jScriptMobs.getColumnModel().getColumn(3);
    col.setPreferredWidth(40);
    col.setResizable(false);
    this.jScriptMobs.setAutoResizeMode(4);
    this.jScrollPane1.setViewportView(this.jScriptMobs);

    this.jApplyScript.setText("Apply");
    this.jApplyScript.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        cpanel.this.jApplyScriptActionPerformed(evt);
      }
    });
    this.jScriptSave.setText("Save");
    this.jScriptSave.setEnabled(false);
    this.jScriptSave.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        cpanel.this.jScriptSaveActionPerformed(evt);
      }
    });
    this.jOpenEditor.setText("Editor");
    this.jOpenEditor.setEnabled(false);
    this.jOpenEditor.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        cpanel.this.jOpenEditorActionPerformed(evt);
      }
    });
    this.jMiniBar.setText("Mini-Bar");
    this.jMiniBar.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        cpanel.this.jMiniBarActionPerformed(evt);
      }
    });
    GroupLayout jPanel2Layout = new GroupLayout(this.jPanel2);
    this.jPanel2.setLayout(jPanel2Layout);
    jPanel2Layout.setHorizontalGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup().addGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.TRAILING).addGroup(GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup().addContainerGap().addComponent(this.jScrollPane1, -1, 209, 32767)).addGroup(jPanel2Layout.createSequentialGroup().addContainerGap().addComponent(this.jSeparator1, -1, 209, 32767)).addGroup(GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup().addContainerGap().addGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(jPanel2Layout.createSequentialGroup().addComponent(this.jLabel5).addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED).addComponent(this.jScripts, 0, 106, 32767).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(this.jLoadScript)).addGroup(GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup().addComponent(this.jLabel6).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(this.jCurrentScript, -1, 111, 32767).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(this.jOpenEditor)))).addGroup(jPanel2Layout.createSequentialGroup().addGap(9, 9, 9).addComponent(this.jProgress, -1, 210, 32767)).addGroup(jPanel2Layout.createSequentialGroup().addContainerGap().addComponent(this.jMiniBar).addGap(18, 18, 18).addComponent(this.jScriptSave).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(this.jApplyScript))).addContainerGap()));

    jPanel2Layout.setVerticalGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(jPanel2Layout.createSequentialGroup().addContainerGap().addGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(jPanel2Layout.createSequentialGroup().addGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(this.jLabel5).addComponent(this.jLoadScript).addComponent(this.jScripts, -2, -1, -2)).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(this.jSeparator1, -2, 10, -2).addGap(26, 26, 26).addComponent(this.jScrollPane1, -2, 125, -2).addGap(6, 6, 6).addGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(this.jApplyScript).addComponent(this.jScriptSave).addComponent(this.jMiniBar)).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(this.jProgress, -2, -1, -2).addContainerGap()).addGroup(GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup().addGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(this.jOpenEditor).addComponent(this.jLabel6).addComponent(this.jCurrentScript, -2, -1, -2)).addGap(197, 197, 197)))));

    this.jTabbedPane1.addTab("Scripts", this.jPanel2);

    this.jLabel8.setText("Current Version");

    this.jLabel9.setText("Latest Version");

    this.jCurrentVersion.setText("jLabel10");

    this.jLatestVersion.setText("jLabel10");

    this.jLabel10.setText("Author");

    this.jLabel11.setText("Website");

    this.jLabel12.setText("Roger Fedor (AKA Nemesis)");

    this.jLabel13.setText("http://nodeka.gamers411.net");

    this.jLabel14.setText("Email");

    this.jLabel15.setText("rfedor@gamers411.net");

    this.jUpdates.setText("Check for Updates");
    this.jUpdates.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        cpanel.this.jUpdatesActionPerformed(evt);
      }
    });
    GroupLayout jPanel3Layout = new GroupLayout(this.jPanel3);
    this.jPanel3.setLayout(jPanel3Layout);
    jPanel3Layout.setHorizontalGroup(jPanel3Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(jPanel3Layout.createSequentialGroup().addContainerGap().addGroup(jPanel3Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(this.jSeparator2, -1, 209, 32767).addGroup(jPanel3Layout.createParallelGroup(GroupLayout.Alignment.LEADING, false).addGroup(jPanel3Layout.createSequentialGroup().addComponent(this.jLabel8).addGap(18, 18, 18).addComponent(this.jCurrentVersion)).addGroup(jPanel3Layout.createSequentialGroup().addComponent(this.jLabel9).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, -1, 32767).addComponent(this.jLatestVersion))).addGroup(jPanel3Layout.createSequentialGroup().addComponent(this.jLabel10).addGap(18, 18, 18).addComponent(this.jLabel12)).addGroup(jPanel3Layout.createSequentialGroup().addGroup(jPanel3Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(this.jLabel11).addComponent(this.jLabel14)).addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED).addGroup(jPanel3Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(this.jLabel15).addComponent(this.jLabel13).addComponent(this.jUpdates)))).addContainerGap()));

    jPanel3Layout.setVerticalGroup(jPanel3Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(jPanel3Layout.createSequentialGroup().addContainerGap().addGroup(jPanel3Layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(this.jLabel8).addComponent(this.jCurrentVersion)).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(jPanel3Layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(this.jLabel9).addComponent(this.jLatestVersion)).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(this.jSeparator2, -2, 10, -2).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(jPanel3Layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(this.jLabel10).addComponent(this.jLabel12)).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(jPanel3Layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(this.jLabel11).addComponent(this.jLabel13)).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(jPanel3Layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(this.jLabel14).addComponent(this.jLabel15)).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 112, 32767).addComponent(this.jUpdates).addContainerGap()));

    this.jTabbedPane1.addTab("About", this.jPanel3);

    this.jTabbedPane1.setBounds(10, 10, 234, 295);
    this.jLayeredPane1.add(this.jTabbedPane1, JLayeredPane.DEFAULT_LAYER);

    GroupLayout layout = new GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(this.jLayeredPane1, -1, 252, 32767));

    layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(this.jLayeredPane1, -1, 312, 32767));

    pack();
  }

  private void jLoadActionPerformed(ActionEvent evt) {
    if (this.jLoad.getText().equals("New")) {
      this.corescript.settings = new config();
      refreshVars();
      checkValidation();
      return;
    }

    String filename = (String)this.jProfile.getSelectedItem();

    String directory = this.corescript.settings.profiles_dir;
    if (filename.equals("From file...")) {
      filename = "";
      directory = "";
      this.chooseFile.setCurrentDirectory(new File(this.corescript.settings.profiles_dir));
      int rVal = this.chooseFile.showSaveDialog(this);
      if (rVal == 0) {
        filename = this.chooseFile.getSelectedFile().getName();
        directory = this.chooseFile.getCurrentDirectory().toString();
      }

      if (rVal == 1) {
        return;
      }
    }

    this.corescript.loadConfig(directory + "/" + filename);

    refreshVars();
    this.pool_gui.refreshGUI();
  }

  private void jApplyConfigActionPerformed(ActionEvent evt) {
    storeVars();
  }

  private void jSaveConfigActionPerformed(ActionEvent evt) {
    String filename = "";
    String directory = "";

    if (this.script.getVariable("UMC_NAME").length() == 0) {
      JOptionPane.showMessageDialog(this, "You must be logged into nodeka to save profiles.", "Error", 0);

      return;
    }

    if (this.corescript.settings.file.length() == 0) {
      this.chooseFile.setCurrentDirectory(new File(this.corescript.settings.profiles_dir));
      int rVal = this.chooseFile.showSaveDialog(this);
      if (rVal == 0) {
        filename = this.chooseFile.getSelectedFile().getName() + ".bot";

        directory = this.chooseFile.getCurrentDirectory().toString();
      }

      if (rVal == 1) {
        return;
      }
      filename = directory + "/" + filename;
      this.corescript.settings.set_file(filename);
    } else {
      filename = this.corescript.settings.file;
    }
    storeVars();
    this.corescript.saveConfig(filename);
    populateProfiles();
  }

  private void jSleeponStateChanged(ChangeEvent evt) {
    if (Integer.parseInt(this.jSleepon.getValue().toString()) < -1)
      this.jSleepon.setValue(Integer.valueOf(-1));
    if (Integer.parseInt(this.jSleepon.getValue().toString()) > 60)
      this.jSleepon.setValue(Integer.valueOf(60));
  }

  private void jLoadScriptActionPerformed(ActionEvent evt) {
    String filename = (String)this.jScripts.getSelectedItem();

    String directory = this.corescript.settings.areas_dir;

    if (filename.equals("From file...")) {
      filename = "";
      directory = "";
      this.chooseFile.setCurrentDirectory(new File(this.corescript.settings.areas_dir));
      int rVal = this.chooseFile.showSaveDialog(this);
      if (rVal == 0) {
        filename = this.chooseFile.getSelectedFile().getName();
        directory = this.chooseFile.getCurrentDirectory().toString();
      }

      if (rVal == 1) {
        return;
      }
    }

    if ((filename.startsWith("NODe_")) && (this.corescript.validated)) {
      if (this.corescript.area != null)
        this.corescript.area.loadScript_extras(directory + "/" + filename);
      else {
        JOptionPane.showMessageDialog(this, "You must have a script loaded first before loading the extras.", "Error", 0);
      }

    }
    else
    {
      this.corescript.area = new script(this.script, this.corescript.settings, directory + "/" + filename, this);
    }
    loadScript();
  }

  private void jApplyScriptActionPerformed(ActionEvent evt) {
    if (this.corescript.area == null) {
      this.script.print("[NemBot] Error : Cannot modify script, no script loaded.");
      return;
    }
    for (int x = 0; x < this.jScriptMobModel.getRowCount(); x++) {
      this.corescript.area.mobs[x].set_kill(((Boolean)this.jScriptMobModel.getValueAt(x, 0)).booleanValue());
      this.corescript.area.mobs[x].set_attack((String)this.jScriptMobModel.getValueAt(x, 2));
      this.corescript.area.mobs[x].set_skip(((Boolean)this.jScriptMobModel.getValueAt(x, 3)).booleanValue());
    }
  }

  private void jRegisterActionPerformed(ActionEvent evt) {
    if (this.script.getVariable("UMC_NAME").length() == 0) {
      JOptionPane.showMessageDialog(this, "You must be logged into the game to register.", "Error", 0);

      return;
    }if (!this.corescript.settings.saved_to_file) {
      JOptionPane.showMessageDialog(this, "You must have a profile loaded to register.", "Error", 0);

      return;
    }

    register panel = new register(this);
    panel.setLocationRelativeTo(this);
    panel.setVisible(true);
  }

  private void jMinHPStateChanged(ChangeEvent evt)
  {
    if (Integer.parseInt(this.jMinHP.getValue().toString()) < -1)
      this.jMinHP.setValue(Integer.valueOf(-1));
    if (Integer.parseInt(this.jMinHP.getValue().toString()) > 100)
      this.jMinHP.setValue(Integer.valueOf(100));
  }

  private void jScriptSaveActionPerformed(ActionEvent evt) {
    String filename = "";
    String directory = "";
    this.chooseFile.setCurrentDirectory(new File(this.corescript.settings.areas_dir));
    int rVal = this.chooseFile.showSaveDialog(this);
    if (rVal == 0) {
      filename = (!this.chooseFile.getSelectedFile().getName().startsWith("NODe_") ? "NODe_" : "") + this.chooseFile.getSelectedFile().getName() + (!this.chooseFile.getSelectedFile().getName().endsWith(".txt") ? ".txt" : "");

      directory = this.chooseFile.getCurrentDirectory().toString();
    }

    if (rVal == 1) {
      return;
    }

    if (this.corescript.area != null)
      this.corescript.area.saveScript_extras(directory + "/" + filename);
  }

  private void jOpenEditorActionPerformed(ActionEvent evt)
  {
    script_editor editor = new script_editor(this.corescript, this.chooseFile);
    editor.pack();
    editor.setLocationRelativeTo(null);
    editor.setVisible(true);
  }

  private void jUpdatesActionPerformed(ActionEvent evt) {
    this.update_panel = new updater(this.corescript, this.chooseFile);
    this.update_panel.setLocationRelativeTo(null);
    this.update_panel.setVisible(true);
  }

  private void jAlwaysOnTopActionPerformed(ActionEvent evt) {
    setAlwaysOnTop(this.jAlwaysOnTop.isSelected());
  }

  private void jMiniBarActionPerformed(ActionEvent evt) {
    this.ministatus.setVisible(true);
  }

  private void jProfileItemStateChanged(ItemEvent evt) {
    if (this.jProfile.getItemCount() != 0)
      if (this.jProfile.getSelectedItem().toString().equals("Create a profile..."))
        this.jLoad.setText("New");
      else
        this.jLoad.setText("Load");
  }

  private void jLayeredPane1AncestorResized(HierarchyEvent evt)
  {
    int width = getWidth();
    int height = getHeight();

    this.jLayeredPane1.setSize(width - 10, height - 10);
    this.jTabbedPane1.setSize(width - 25, height - 50);
    this.jAlwaysOnTop.setLocation(width - 110, 10);
  }

  private void jEventsGUIActionPerformed(ActionEvent evt)
  {
    this.pool_gui.setVisible(true);
  }
}