package net.gamers411.nodeka.umc.plugin.nembot;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JToggleButton;
import javax.swing.LayoutStyle;

public class minibar extends JFrame
{
  private nembot core;
  private ImageIcon red_indicator;
  private ImageIcon green_indicator;
  private ImageIcon yellow_indicator;
  private JLabel jIndicator;
  private JToggleButton jPause;
  public JProgressBar jProgress;

  public minibar(nembot core)
  {
    this.core = core;
    initComponents();
    this.red_indicator = new ImageIcon(getClass().getResource("/net/gamers411/nodeka/umc/plugin/nembot/images/red.png"));
    this.green_indicator = new ImageIcon(getClass().getResource("/net/gamers411/nodeka/umc/plugin/nembot/images/green.png"));
    this.yellow_indicator = new ImageIcon(getClass().getResource("/net/gamers411/nodeka/umc/plugin/nembot/images/yellow.png"));
  }

  public void script_end() {
    this.jIndicator.setIcon(this.red_indicator);
    this.jIndicator.setToolTipText("The script is stopped.");
    this.jPause.setSelected(false);
  }

  public void script_pause() {
    if (this.core.area.status == 4) {
      this.jIndicator.setIcon(this.yellow_indicator);
      this.jIndicator.setToolTipText("The script is paused.");
      this.jPause.setSelected(true);
    } else {
      this.jIndicator.setIcon(this.green_indicator);
      this.jIndicator.setToolTipText("The script is running.");
      this.jPause.setSelected(false);
    }
  }

  public void script_start() {
    this.jIndicator.setIcon(this.green_indicator);
    this.jIndicator.setToolTipText("The script is running.");
    this.jPause.setSelected(false);
  }

  private void initComponents()
  {
    this.jIndicator = new JLabel();
    this.jProgress = new JProgressBar();
    this.jPause = new JToggleButton();

    setDefaultCloseOperation(2);
    setTitle("Script Status");
    setAlwaysOnTop(true);

    this.jIndicator.setHorizontalAlignment(0);
    this.jIndicator.setIcon(new ImageIcon(getClass().getResource("/net/gamers411/nodeka/umc/plugin/nembot/images/red.png")));

    this.jProgress.setStringPainted(true);

    if (this.core.area != null)
    this.jPause.setSelected(this.core.area.status == 4);
    
    this.jPause.setText("Pause");
    this.jPause.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        minibar.this.jPauseActionPerformed(evt);
      }
    });
    GroupLayout layout = new GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addContainerGap().addComponent(this.jIndicator).addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED).addComponent(this.jProgress, -1, 154, 32767).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(this.jPause).addContainerGap()));

    layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addContainerGap().addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(this.jProgress, -2, -1, -2).addComponent(this.jPause)).addComponent(this.jIndicator)).addContainerGap(-1, 32767)));

    pack();
  }

  private void jPauseActionPerformed(ActionEvent evt) {
    if (this.core.area.status != 4)
      this.jIndicator.setIcon(this.yellow_indicator);
    else
      this.jIndicator.setIcon(this.green_indicator);
    this.core.area.pause();
  }
}