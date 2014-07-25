package net.gamers411.nodeka.umc.plugin.nembot;

import java.io.File;
import javax.swing.filechooser.FileFilter;

public class botFilter extends FileFilter
{
  public boolean accept(File f)
  {
    return (f.getName().toLowerCase().endsWith(".bot")) || (f.isDirectory()) || (f.getName().toLowerCase().endsWith(".txt"));
  }

  public String getDescription()
  {
    return "Nembot files (*.bot,*.txt)";
  }
}