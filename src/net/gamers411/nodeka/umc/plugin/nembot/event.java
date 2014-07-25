package net.gamers411.nodeka.umc.plugin.nembot;

import java.io.Serializable;

public class event
  implements Serializable
{
  protected String action;
  protected String prevent;
  protected int cost;
  protected int cost_pool;
  protected int condition_percent;
  protected int position;
  protected int lag;
  protected long last_cast;
}