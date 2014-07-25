package net.gamers411.nodeka.umc.plugin.nembot;

import com.lsd.umc.script.ScriptInterface;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.LayoutStyle;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

public class script_editor extends JFrame
{
  private ScriptInterface script;
  private nembot core;
  private JFileChooser chooseFile;
  private DefaultTableModel jMobsModel;
  private script area;
  private JTextField jClose;
  private JButton jCloseDo;
  private JMenuItem jCopytoMemory;
  private JMenuItem jExit;
  private JMenu jFileMenu;
  private JLabel jLabel1;
  private JLabel jLabel2;
  private JLabel jLabel3;
  private JLabel jLabel4;
  private JLabel jLabel5;
  private JLabel jLabel6;
  private JLayeredPane jLayeredPane1;
  private JMenuItem jLoad;
  private JMenuItem jLoadMemory;
  private JButton jMapC;
  private JButton jMapD;
  private JButton jMapE;
  private JButton jMapN;
  private JButton jMapNW;
  private JButton jMapS;
  private JButton jMapSW;
  private JButton jMapU;
  private JButton jMapW;
  private JMenu jMenu2;
  private JMenuBar jMenuBar1;
  private JButton jMobAdd;
  private JButton jMobDelete;
  private JTable jMobs;
  private JMenuItem jNew;
  private JTextField jOpen;
  private JButton jOpenDo;
  private JTextArea jPath;
  private JCheckBox jRandom;
  private JMenuItem jSave;
  private JMenuItem jSaveAs;
  private JScrollPane jScrollPane1;
  private JScrollPane jScrollPane2;
  private JSeparator jSeparator1;

  public script_editor(nembot core, JFileChooser chooseFile)
  {
    this.core = core;
    this.script = core.script;
    this.chooseFile = chooseFile;

    this.jMobsModel = new DefaultTableModel(new Object[0][], new String[] { "Name", "Singular", "Plural" })
    {
      Class[] types = { String.class, String.class, String.class };

      public Class getColumnClass(int columnIndex)
      {
        return this.types[columnIndex];
      }
    };
    initComponents();

    setTitle("Script Editor");
  }

  private void resetScript() {
    this.area = null;
    for (int x = this.jMobsModel.getRowCount(); x > 0; x--)
      this.jMobsModel.removeRow(x - 1);
    this.jPath.setText("");
    this.jPath.setEnabled(true);
    this.jOpen.setText("");
    this.jClose.setText("");
    this.jRandom.setSelected(false);
  }

  public void loadScript(script area)
  {
    for (int x = 0; x < area.mobs.length; x++) {
      this.jMobsModel.addRow(new Object[] { area.mobs[x].name.toString(), area.mobs[x].singular.toString(), area.mobs[x].plural.toString() });
    }

    String path = "";
    for (int x = 1; x < area.path.size(); x++) {
      if (area.shortcuts.containsValue(Integer.valueOf(x))) {
        Iterator keys = area.shortcuts.keySet().iterator();
        while (keys.hasNext()) {
          String key = keys.next().toString();
          int value = Integer.parseInt(area.shortcuts.get(key).toString());
          if (value == x) {
            path = path + "// " + key + "\n";
          }
        }
      }
      path = path + (String)area.path.get(x) + "\n";
    }

    this.jRandom.setSelected(area.move_random);
    this.jPath.setEnabled(!area.move_random);
    this.jPath.setText(path);
  }

  private void initComponents()
  {
    this.jScrollPane1 = new JScrollPane();
    this.jMobs = new JTable();
    this.jLabel1 = new JLabel();
    this.jMobDelete = new JButton();
    this.jMobAdd = new JButton();
    this.jLabel2 = new JLabel();
    this.jScrollPane2 = new JScrollPane();
    this.jPath = new JTextArea();
    this.jLayeredPane1 = new JLayeredPane();
    this.jMapD = new JButton();
    this.jMapN = new JButton();
    this.jMapU = new JButton();
    this.jMapE = new JButton();
    this.jMapC = new JButton();
    this.jMapW = new JButton();
    this.jMapNW = new JButton();
    this.jMapSW = new JButton();
    this.jMapS = new JButton();
    this.jLabel4 = new JLabel();
    this.jLabel3 = new JLabel();
    this.jLabel5 = new JLabel();
    this.jOpen = new JTextField();
    this.jOpenDo = new JButton();
    this.jLabel6 = new JLabel();
    this.jClose = new JTextField();
    this.jCloseDo = new JButton();
    this.jRandom = new JCheckBox();
    this.jMenuBar1 = new JMenuBar();
    this.jFileMenu = new JMenu();
    this.jNew = new JMenuItem();
    this.jLoad = new JMenuItem();
    this.jSave = new JMenuItem();
    this.jSaveAs = new JMenuItem();
    this.jSeparator1 = new JSeparator();
    this.jExit = new JMenuItem();
    this.jMenu2 = new JMenu();
    this.jLoadMemory = new JMenuItem();
    this.jCopytoMemory = new JMenuItem();

    setDefaultCloseOperation(2);
    setMinimumSize(new Dimension(402, 289));

    this.jMobs.setModel(this.jMobsModel);
    TableColumn col = this.jMobs.getColumnModel().getColumn(0);
    col.setPreferredWidth(25);
    col.setResizable(false);
    this.jScrollPane1.setViewportView(this.jMobs);

    this.jLabel1.setText("Mobs");

    this.jMobDelete.setText("Delete");
    this.jMobDelete.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        script_editor.this.jMobDeleteActionPerformed(evt);
      }
    });
    this.jMobAdd.setText("Add");
    this.jMobAdd.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        script_editor.this.jMobAddActionPerformed(evt);
      }
    });
    this.jLabel2.setText("Path");

    this.jPath.setColumns(11);
    this.jPath.setRows(5);
    this.jScrollPane2.setViewportView(this.jPath);

    this.jLayeredPane1.setPreferredSize(new Dimension(73, 73));

    this.jMapD.setBorder(null);
    this.jMapD.setBorderPainted(false);
    this.jMapD.setContentAreaFilled(false);
    this.jMapD.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        script_editor.this.jMapDActionPerformed(evt);
      }
    });
    this.jMapD.setBounds(70, 70, 30, 30);
    this.jLayeredPane1.add(this.jMapD, JLayeredPane.DEFAULT_LAYER);

    this.jMapN.setBorder(null);
    this.jMapN.setBorderPainted(false);
    this.jMapN.setContentAreaFilled(false);
    this.jMapN.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        script_editor.this.jMapNActionPerformed(evt);
      }
    });
    this.jMapN.setBounds(40, 10, 30, 30);
    this.jLayeredPane1.add(this.jMapN, JLayeredPane.DEFAULT_LAYER);

    this.jMapU.setBorder(null);
    this.jMapU.setBorderPainted(false);
    this.jMapU.setContentAreaFilled(false);
    this.jMapU.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        script_editor.this.jMapUActionPerformed(evt);
      }
    });
    this.jMapU.setBounds(70, 10, 30, 30);
    this.jLayeredPane1.add(this.jMapU, JLayeredPane.DEFAULT_LAYER);

    this.jMapE.setBorder(null);
    this.jMapE.setBorderPainted(false);
    this.jMapE.setContentAreaFilled(false);
    this.jMapE.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        script_editor.this.jMapEActionPerformed(evt);
      }
    });
    this.jMapE.setBounds(70, 40, 30, 30);
    this.jLayeredPane1.add(this.jMapE, JLayeredPane.DEFAULT_LAYER);

    this.jMapC.setBorder(null);
    this.jMapC.setBorderPainted(false);
    this.jMapC.setContentAreaFilled(false);
    this.jMapC.setBounds(40, 40, 30, 30);
    this.jLayeredPane1.add(this.jMapC, JLayeredPane.DEFAULT_LAYER);

    this.jMapW.setBorder(null);
    this.jMapW.setBorderPainted(false);
    this.jMapW.setContentAreaFilled(false);
    this.jMapW.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        script_editor.this.jMapWActionPerformed(evt);
      }
    });
    this.jMapW.setBounds(10, 40, 30, 30);
    this.jLayeredPane1.add(this.jMapW, JLayeredPane.DEFAULT_LAYER);

    this.jMapNW.setBorder(null);
    this.jMapNW.setBorderPainted(false);
    this.jMapNW.setContentAreaFilled(false);
    this.jMapNW.setBounds(10, 10, 30, 30);
    this.jLayeredPane1.add(this.jMapNW, JLayeredPane.DEFAULT_LAYER);

    this.jMapSW.setBorder(null);
    this.jMapSW.setBorderPainted(false);
    this.jMapSW.setContentAreaFilled(false);
    this.jMapSW.setBounds(10, 70, 30, 30);
    this.jLayeredPane1.add(this.jMapSW, JLayeredPane.DEFAULT_LAYER);

    this.jMapS.setBorder(null);
    this.jMapS.setBorderPainted(false);
    this.jMapS.setContentAreaFilled(false);
    this.jMapS.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        script_editor.this.jMapSActionPerformed(evt);
      }
    });
    this.jMapS.setBounds(40, 70, 30, 30);
    this.jLayeredPane1.add(this.jMapS, JLayeredPane.DEFAULT_LAYER);

    this.jLabel4.setHorizontalAlignment(0);
    this.jLabel4.setText("Map Controls");
    this.jLabel4.setBounds(0, 0, 110, 14);
    this.jLayeredPane1.add(this.jLabel4, JLayeredPane.DEFAULT_LAYER);

    this.jLabel3.setHorizontalAlignment(0);
    this.jLabel3.setIcon(new ImageIcon(getClass().getResource("/net/gamers411/nodeka/umc/plugin/nembot/images/nav_controls.png")));
    this.jLabel3.setDoubleBuffered(true);
    this.jLabel3.setBounds(10, 10, 90, 90);
    this.jLayeredPane1.add(this.jLabel3, JLayeredPane.DEFAULT_LAYER);

    this.jLabel5.setText("Open");

    this.jOpenDo.setText("Do");
    this.jOpenDo.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        script_editor.this.jOpenDoActionPerformed(evt);
      }
    });
    this.jLabel6.setText("Close");

    this.jCloseDo.setText("Do");
    this.jCloseDo.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        script_editor.this.jCloseDoActionPerformed(evt);
      }
    });
    this.jRandom.setText("Random");
    this.jRandom.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        script_editor.this.jRandomActionPerformed(evt);
      }
    });
    this.jFileMenu.setText("File");

    this.jNew.setAccelerator(KeyStroke.getKeyStroke(78, 2));
    this.jNew.setText("New");
    this.jNew.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        script_editor.this.jNewActionPerformed(evt);
      }
    });
    this.jFileMenu.add(this.jNew);

    this.jLoad.setAccelerator(KeyStroke.getKeyStroke(79, 2));
    this.jLoad.setText("Open");
    this.jLoad.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        script_editor.this.jLoadActionPerformed(evt);
      }
    });
    this.jFileMenu.add(this.jLoad);

    this.jSave.setAccelerator(KeyStroke.getKeyStroke(83, 2));
    this.jSave.setText("Save");
    this.jSave.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        script_editor.this.jSaveActionPerformed(evt);
      }
    });
    this.jFileMenu.add(this.jSave);

    this.jSaveAs.setText("Save As");
    this.jSaveAs.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        script_editor.this.jSaveAsActionPerformed(evt);
      }
    });
    this.jFileMenu.add(this.jSaveAs);
    this.jFileMenu.add(this.jSeparator1);

    this.jExit.setText("Exit");
    this.jExit.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        script_editor.this.jExitActionPerformed(evt);
      }
    });
    this.jFileMenu.add(this.jExit);

    this.jMenuBar1.add(this.jFileMenu);

    this.jMenu2.setText("Tools");

    this.jLoadMemory.setAccelerator(KeyStroke.getKeyStroke(76, 2));
    this.jLoadMemory.setText("Load from Memory");
    this.jLoadMemory.setToolTipText("Loads the currently loaded script.");
    this.jLoadMemory.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        script_editor.this.jLoadMemoryActionPerformed(evt);
      }
    });
    this.jMenu2.add(this.jLoadMemory);

    this.jCopytoMemory.setText("Copy to Memory");
    this.jCopytoMemory.setToolTipText("Stores the currently loaded script back into the bot.");
    this.jCopytoMemory.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        script_editor.this.jCopytoMemoryActionPerformed(evt);
      }
    });
    this.jMenu2.add(this.jCopytoMemory);

    this.jMenuBar1.add(this.jMenu2);

    setJMenuBar(this.jMenuBar1);

    GroupLayout layout = new GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addContainerGap().addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(this.jScrollPane1, -1, 382, 32767).addComponent(this.jLabel1, -2, 90, -2).addGroup(layout.createSequentialGroup().addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false).addGroup(layout.createSequentialGroup().addComponent(this.jLabel2).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, -1, 32767).addComponent(this.jRandom)).addComponent(this.jScrollPane2, -2, 117, -2)).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(this.jOpen, -1, 82, 32767).addComponent(this.jClose, -1, 82, 32767).addComponent(this.jLabel6, -1, 82, 32767).addComponent(this.jLabel5, -1, 82, 32767)).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(this.jOpenDo).addComponent(this.jCloseDo)).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false).addGroup(layout.createSequentialGroup().addComponent(this.jMobAdd).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(this.jMobDelete)).addGroup(layout.createSequentialGroup().addGap(10, 10, 10).addComponent(this.jLayeredPane1, -1, -1, 32767))))).addContainerGap()));

    layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addContainerGap().addComponent(this.jLabel1).addGap(4, 4, 4).addComponent(this.jScrollPane1, -2, 97, -2).addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(this.jMobDelete).addComponent(this.jMobAdd)).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(this.jLayeredPane1, -1, 107, 32767)).addGroup(layout.createSequentialGroup().addGap(15, 15, 15).addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(this.jLabel2).addComponent(this.jRandom)).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addComponent(this.jLabel5).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(this.jOpenDo).addComponent(this.jOpen, -2, -1, -2)).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(this.jLabel6).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(this.jClose, -2, -1, -2).addComponent(this.jCloseDo))).addComponent(this.jScrollPane2, -2, -1, -2)).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 6, 32767))).addContainerGap()));

    pack();
  }

  private void jMapNActionPerformed(ActionEvent evt) {
    this.script.send("n");
    if (!this.jRandom.isSelected())
      this.jPath.setText(this.jPath.getText() + "n\n");
  }

  private void jMapSActionPerformed(ActionEvent evt) {
    this.script.send("s");
    if (!this.jRandom.isSelected())
      this.jPath.setText(this.jPath.getText() + "s\n");
  }

  private void jMapWActionPerformed(ActionEvent evt) {
    this.script.send("w");
    if (!this.jRandom.isSelected())
      this.jPath.setText(this.jPath.getText() + "w\n");
  }

  private void jMapEActionPerformed(ActionEvent evt) {
    this.script.send("e");
    if (!this.jRandom.isSelected())
      this.jPath.setText(this.jPath.getText() + "e\n");
  }

  private void jMapUActionPerformed(ActionEvent evt) {
    this.script.send("u");
    if (!this.jRandom.isSelected())
      this.jPath.setText(this.jPath.getText() + "u\n");
  }

  private void jMapDActionPerformed(ActionEvent evt) {
    this.script.send("d");
    if (!this.jRandom.isSelected())
      this.jPath.setText(this.jPath.getText() + "d\n");
  }

  private void jOpenDoActionPerformed(ActionEvent evt) {
    this.script.send("open " + this.jOpen.getText());
    if (!this.jRandom.isSelected())
      this.jPath.setText(this.jPath.getText() + "open " + this.jOpen.getText() + ";");
  }

  private void jCloseDoActionPerformed(ActionEvent evt)
  {
    this.script.send("close " + this.jOpen.getText());
    if (!this.jRandom.isSelected())
      this.jPath.setText(this.jPath.getText() + "close " + this.jOpen.getText() + ";");
  }

  private void jMobAddActionPerformed(ActionEvent evt)
  {
    this.jMobsModel.addRow(new Object[] { null, null, null });
  }

  private void jLoadMemoryActionPerformed(ActionEvent evt) {
    if (this.core.area != null)
      loadScript(this.core.area);
    else
      JOptionPane.showMessageDialog(this, "You do not have an area loaded into memory.", "Error", 0);
  }

  private void jNewActionPerformed(ActionEvent evt)
  {
    resetScript();
  }

  private void jCopytoMemoryActionPerformed(ActionEvent evt) {
    mob[] mobs = new mob[this.jMobs.getRowCount()];
    List path = null;
    String name = "";
    String singular = "";
    String plural = "";
    HashMap shortcuts = new HashMap();

    for (int x = 0; x < this.jMobs.getRowCount(); x++) {
      name = (String)this.jMobsModel.getValueAt(x, 0);
      singular = (String)this.jMobsModel.getValueAt(x, 1);
      plural = (String)this.jMobsModel.getValueAt(x, 2);
      mobs[x] = new mob(name, singular, plural);
    }
    Scanner scan = null;
    Pattern p = Pattern.compile("^// (.*)$");
    try
    {
      scan = new Scanner(this.jPath.getText()); } catch (Exception ex) {
    }
    path = new ArrayList();
    String line = "";
    int x = 0;
    while (scan.hasNext()) {
      line = scan.nextLine();
      Matcher m = p.matcher(line);
      if (m.matches()) {
        shortcuts.put(m.group(1), Integer.valueOf(x));
      }
      else {
        path.add(x, line);
        x++;
      }
    }
    this.core.area.updateScript(mobs, path, shortcuts);
    this.core.area.move_random = this.jRandom.isSelected();
  }

  private void jLoadActionPerformed(ActionEvent evt) {
    String filename = "";
    String directory = "";
    this.chooseFile.setCurrentDirectory(new File(this.core.settings.areas_dir));
    int rVal = this.chooseFile.showSaveDialog(this);
    if (rVal == 0) {
      filename = this.chooseFile.getSelectedFile().getName();
      directory = this.chooseFile.getCurrentDirectory().toString();
    }

    if (rVal == 1) {
      return;
    }

    this.area = new script(null, this.core.settings, directory + "/" + filename, null);

    loadScript(this.area);
  }

  private void jSaveActionPerformed(ActionEvent evt) {
    String filename = "";
    String directory = "";
    if (this.area == null) {
      jSaveAsActionPerformed(evt);
      return;
    }
    if (this.area.filename.length() == 0) {
      this.chooseFile.setCurrentDirectory(new File(this.core.settings.areas_dir));
      int rVal = this.chooseFile.showSaveDialog(this);
      if (rVal == 1) {
        return;
      }
      if (rVal == 0) {
        filename = (!this.chooseFile.getSelectedFile().getName().startsWith("NOD_") ? "NOD_" : "") + this.chooseFile.getSelectedFile().getName() + (!this.chooseFile.getSelectedFile().getName().endsWith(".txt") ? ".txt" : "");

        directory = this.chooseFile.getCurrentDirectory().toString();
        this.area.set_filename(directory + "/" + filename);
      }

    }

    mob[] mobs = new mob[this.jMobs.getRowCount()];
    List path = null;
    String name = "";
    String singular = "";
    String plural = "";
    HashMap shortcuts = new HashMap();

    for (int x = 0; x < this.jMobs.getRowCount(); x++) {
      name = (String)this.jMobsModel.getValueAt(x, 0);
      singular = (String)this.jMobsModel.getValueAt(x, 1);
      plural = (String)this.jMobsModel.getValueAt(x, 2);
      mobs[x] = new mob(name, singular, plural);
    }
    Scanner scan = null;
    Pattern p = Pattern.compile("^// (.*)$");
    try
    {
      scan = new Scanner(this.jPath.getText()); } catch (Exception ex) {
    }
    String line = "";
    int x = 0;
    while (scan.hasNext()) {
      line = scan.nextLine();
      Matcher m = p.matcher(line);
      if (m.matches()) {
        shortcuts.put(m.group(1), Integer.valueOf(x));
      }
      else {
        path.add(x, line);
        x++;
      }
    }
    this.area.updateScript(mobs, path, shortcuts);
    this.area.saveScript(this.area.filename);
  }

  private void jSaveAsActionPerformed(ActionEvent evt) {
    String filename = "";
    String directory = "";

    this.chooseFile.setCurrentDirectory(new File(this.core.settings.areas_dir));
    int rVal = this.chooseFile.showSaveDialog(this);

    if (rVal == 1) {
      return;
    }
    if (rVal == 0) {
      filename = (!this.chooseFile.getSelectedFile().getName().startsWith("NOD_") ? "NOD_" : "") + this.chooseFile.getSelectedFile().getName() + (!this.chooseFile.getSelectedFile().getName().endsWith(".txt") ? ".txt" : "");
      directory = this.chooseFile.getCurrentDirectory().toString();
      if (this.area == null)
        this.area = new script(null, this.core.settings, directory + "/" + filename, null);
      else {
        this.area.set_filename(directory + "/" + filename);
      }
    }

    mob[] mobs = new mob[this.jMobs.getRowCount()];
    String name = "";
    String singular = "";
    String plural = "";
    HashMap shortcuts = new HashMap();

    for (int x = 0; x < this.jMobs.getRowCount(); x++) {
      name = (String)this.jMobsModel.getValueAt(x, 0);
      singular = (String)this.jMobsModel.getValueAt(x, 1);
      plural = (String)this.jMobsModel.getValueAt(x, 2);
      mobs[x] = new mob(name, singular, plural);
    }
    Scanner scan = null;
    Pattern p = Pattern.compile("^// (.*)$");
    try
    {
      scan = new Scanner(this.jPath.getText()); } catch (Exception ex) {
    }
    String line = "";
    int x = 0;
    ArrayList path = new ArrayList();
    while (scan.hasNext()) {
      line = scan.nextLine();
      Matcher m = p.matcher(line);
      if (m.matches()) {
        shortcuts.put(m.group(1), Integer.valueOf(x));
      }
      else {
        path.add(x, line);
        x++;
      }
    }
    this.area.updateScript(mobs, path, shortcuts);
    this.area.saveScript(this.area.filename);
  }

  private void jExitActionPerformed(ActionEvent evt) {
    setVisible(false);
    dispose();
  }

  private void jMobDeleteActionPerformed(ActionEvent evt) {
    int loc = this.jMobs.getSelectedRow();
    if (loc != -1)
      this.jMobsModel.removeRow(loc);
  }

  private void jRandomActionPerformed(ActionEvent evt)
  {
    this.jPath.setEnabled(!this.jRandom.isSelected());
    this.jPath.setText(this.jRandom.isSelected() ? "RANDOM" : "");
  }
}