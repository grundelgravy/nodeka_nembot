package net.gamers411.nodeka.umc.plugin.nembot;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.LayoutStyle;

public class register extends JFrame
{
  private cpanel core;
  private JLabel jLabel1;
  private JLabel jLabel2;
  private JLabel jLabel3;
  private JPasswordField jPassword;
  private JButton jRecover;
  private JButton jRegister;
  private JButton jRelease;
  private JScrollPane jScrollPane1;
  private JTextPane jStatus;
  private JTextField jUsername;

  public register(cpanel core)
  {
    this.core = core;
    initComponents();
  }

  private void initComponents()
  {
    this.jLabel1 = new JLabel();
    this.jLabel2 = new JLabel();
    this.jUsername = new JTextField();
    this.jPassword = new JPasswordField();
    this.jRegister = new JButton();
    this.jRelease = new JButton();
    this.jRecover = new JButton();
    this.jLabel3 = new JLabel();
    this.jScrollPane1 = new JScrollPane();
    this.jStatus = new JTextPane();

    setDefaultCloseOperation(2);
    setTitle("Nodeka411 Login");
    setAlwaysOnTop(true);
    setMinimumSize(new Dimension(250, 158));

    this.jLabel1.setText("Username");

    this.jLabel2.setText("Password");

    this.jRegister.setText("Register");
    this.jRegister.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        register.this.jRegisterActionPerformed(evt);
      }
    });
    this.jRelease.setText("Release");
    this.jRelease.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        register.this.jReleaseActionPerformed(evt);
      }
    });
    this.jRecover.setText("Recover");
    this.jRecover.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        register.this.jRecoverActionPerformed(evt);
      }
    });
    this.jLabel3.setIcon(new ImageIcon(getClass().getResource("/com/lsd/umc/nodeka/plugin/nembot/images/40px-Lock_233.png")));

    this.jStatus.setEditable(false);
    this.jStatus.setText("Fill in the username and password fields from your http://www.nodeka411.net account to recover or register your bot.");
    this.jScrollPane1.setViewportView(this.jStatus);

    GroupLayout layout = new GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup().addContainerGap().addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING).addComponent(this.jScrollPane1, GroupLayout.Alignment.LEADING, -1, 230, 32767).addGroup(GroupLayout.Alignment.LEADING, layout.createSequentialGroup().addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING, false).addComponent(this.jLabel2, GroupLayout.Alignment.LEADING, -1, -1, 32767).addComponent(this.jLabel1, GroupLayout.Alignment.LEADING, -1, 57, 32767)).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false).addComponent(this.jUsername).addComponent(this.jPassword, -1, 118, 32767)).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 11, 32767).addComponent(this.jLabel3)).addGroup(GroupLayout.Alignment.LEADING, layout.createSequentialGroup().addComponent(this.jRelease).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(this.jRecover).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(this.jRegister))).addContainerGap()));

    layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addContainerGap().addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(this.jLabel1).addComponent(this.jUsername, -2, -1, -2)).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(this.jLabel2).addComponent(this.jPassword, -2, -1, -2))).addComponent(this.jLabel3)).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(this.jScrollPane1, -1, 55, 32767).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(this.jRegister).addComponent(this.jRecover).addComponent(this.jRelease)).addContainerGap()));

    pack();
  }

  private void jReleaseActionPerformed(ActionEvent evt) {
    String ret = this.core.corescript.socket.release(this.core.corescript.settings.username, this.core.corescript.settings.key);

    if (ret.equals("false")) {
      this.jStatus.setText("Failed to release registration.");
    } else {
      this.core.checkValidation();
      this.jStatus.setText("Successfully released registration.");
    }
  }

  private void jRegisterActionPerformed(ActionEvent evt) {
    String ret = this.core.corescript.socket.register(this.jUsername.getText(), new String(this.jPassword.getPassword()), this.core.corescript.settings.username, this.core.corescript.settings.key);

    if (ret.equals("false")) {
      this.jStatus.setText("Failed to register character.");
    } else if (ret.equals("true")) {
      this.core.checkValidation();
      this.jStatus.setText("Successfully registered character.");
    }
  }

  private void jRecoverActionPerformed(ActionEvent evt) {
    String ret = this.core.corescript.socket.recover(this.jUsername.getText(), new String(this.jPassword.getPassword()), this.core.corescript.settings.username);
    if (ret.equals("false")) {
      this.jStatus.setText("Failed to recover registration.");
    } else {
      this.core.corescript.settings.set_key(ret);
      this.core.corescript.saveConfig(this.core.corescript.settings.file);
      this.core.checkValidation();
      this.jStatus.setText("Successfully recovered registration.");
    }
  }
}