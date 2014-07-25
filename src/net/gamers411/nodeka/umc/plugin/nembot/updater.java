package net.gamers411.nodeka.umc.plugin.nembot;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.LayoutStyle;

public class updater extends JFrame
{
  private nembot script;
  private String latestVersion;
  private JFileChooser chooseFile;
  private JButton jClose;
  private JLabel jCurrentVersion;
  private JLabel jLabel1;
  private JLabel jLabel2;
  private JLabel jLabel5;
  private JLabel jLatestVersion;
  private JProgressBar jProgress;
  private JScrollPane jScrollPane1;
  private JTextPane jStatus;
  private JButton jUpdate;

  public updater(nembot script, JFileChooser chooseFile)
  {
    this.script = script;
    this.chooseFile = chooseFile;
    this.latestVersion = this.script.socket.getVersion();

    initComponents();

    this.jCurrentVersion.setText(this.script.version);
    this.jLatestVersion.setText(this.latestVersion);

    if (this.script.version.compareTo(this.latestVersion) == 0)
      this.jUpdate.setEnabled(false);
  }

  private void initComponents()
  {
    this.jLabel1 = new JLabel();
    this.jLabel2 = new JLabel();
    this.jCurrentVersion = new JLabel();
    this.jLatestVersion = new JLabel();
    this.jClose = new JButton();
    this.jUpdate = new JButton();
    this.jProgress = new JProgressBar();
    this.jLabel5 = new JLabel();
    this.jScrollPane1 = new JScrollPane();
    this.jStatus = new JTextPane();

    setDefaultCloseOperation(2);
    setTitle("Live Update");
    setAlwaysOnTop(true);

    this.jLabel1.setText("Current Version");

    this.jLabel2.setText("Latest Version");

    this.jCurrentVersion.setText("jLabel3");

    this.jLatestVersion.setText("jLabel4");

    this.jClose.setText("Close");
    this.jClose.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        updater.this.jCloseActionPerformed(evt);
      }
    });
    this.jUpdate.setText("Update");
    this.jUpdate.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        updater.this.jUpdateActionPerformed(evt);
      }
    });
    this.jLabel5.setText("Progress");

    if (this.script.socket.status != -1) {
      try {
        this.jStatus.setPage(socketMan.URLMan.get("update") + "/changes-" + this.latestVersion + ".txt");
      } catch (IOException e1) {
        e1.printStackTrace();
      }
    }
    this.jScrollPane1.setViewportView(this.jStatus);

    GroupLayout layout = new GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup().addContainerGap().addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING).addComponent(this.jScrollPane1, GroupLayout.Alignment.LEADING, -1, 240, 32767).addGroup(GroupLayout.Alignment.LEADING, layout.createSequentialGroup().addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING, false).addComponent(this.jLabel2, GroupLayout.Alignment.LEADING, -1, -1, 32767).addComponent(this.jLabel1, GroupLayout.Alignment.LEADING, -2, 95, -2)).addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED).addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(this.jCurrentVersion).addComponent(this.jLatestVersion))).addGroup(layout.createSequentialGroup().addComponent(this.jUpdate).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(this.jClose)).addComponent(this.jProgress, GroupLayout.Alignment.LEADING, -1, 240, 32767).addComponent(this.jLabel5, GroupLayout.Alignment.LEADING)).addContainerGap()));

    layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addContainerGap().addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(this.jLabel1).addComponent(this.jCurrentVersion)).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(this.jLabel2).addComponent(this.jLatestVersion)).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 10, 32767).addComponent(this.jLabel5).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(this.jProgress, -2, -1, -2).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(this.jScrollPane1, -2, 76, -2).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(this.jClose).addComponent(this.jUpdate)).addGap(4, 4, 4)));

    pack();
  }

  private void jCloseActionPerformed(ActionEvent evt) {
    setVisible(false);
    dispose();
  }

  private void jUpdateActionPerformed(ActionEvent evt) {
    String filename = "nembot.jar";
    String directory = "plugins/nembot";
    File jarFile = new File(directory + "/" + filename);
    if (!jarFile.exists()) {
      this.chooseFile.setCurrentDirectory(new File("plugins"));
      int rVal = this.chooseFile.showSaveDialog(this);
      if (rVal == 0) {
        filename = this.chooseFile.getSelectedFile().getName();
        directory = this.chooseFile.getCurrentDirectory().toString();
      }

      if (rVal == 1) {
        return;
      }
    }

    this.jStatus.setText("Downloading update now...");
    if (!this.script.socket.downloadupdate("nembot-" + this.latestVersion + ".jar", directory + "/nembot-" + this.latestVersion + ".jar", this.jProgress))
    {
      this.jStatus.setText(this.jStatus.getText() + "\nFailed to download update, please try again in a few minutes.");
      return;
    }
    this.jStatus.setText(this.jStatus.getText() + "\nValidating file...");

    String crc = this.script.socket.getCRC(this.latestVersion);
    byte[] buf = null;
    MessageDigest md = null;
    try {
      BufferedInputStream bis = new BufferedInputStream(new FileInputStream(directory + "/nembot-" + this.latestVersion + ".jar"));

      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      int c;
      while ((c = bis.read()) > -1) {
        baos.write(c);
      }
      bis.close();

      buf = baos.toByteArray();

      md = MessageDigest.getInstance("MD5");
    } catch (Exception ex) {
    }
    md.update(buf);
    byte[] digest = md.digest();

    String downloadedCRC = "";
    for (int i = 0; i < digest.length; i++)
    {
      int decValue;
      if (digest[i] >= 0)
        decValue = digest[i];
      else {
        decValue = 256 + digest[i];
      }
      String hexVal = Integer.toHexString(decValue);
      if (hexVal.length() == 1) hexVal = "0" + hexVal;
      downloadedCRC = downloadedCRC + hexVal;
    }

    System.out.println("CRC:" + crc + " Downloaded CRC: " + downloadedCRC);

    if (crc.equals(downloadedCRC)) {
      File old = new File(directory + "/" + filename);
      old.delete();
      File update = new File(directory + "/nembot-" + this.latestVersion + ".jar");

      if (update.renameTo(old)) {
        this.jStatus.setText(this.jStatus.getText() + "\nFailed to apply update, delete the old plugin and rename" + "nembot-" + this.latestVersion + ".jar to nembot.jar");

        update.delete();
      }
      this.jUpdate.setEnabled(false);
      this.jStatus.setText(this.jStatus.getText() + "\nUpdate complete, reload plugins to activate new changes.");
    } else {
      new File(directory + "/nembot-" + this.latestVersion + ".jar").delete();

      this.jStatus.setText(this.jStatus.getText() + "\nInvalid download, failed checksum, please try again.");
      return;
    }
  }
}