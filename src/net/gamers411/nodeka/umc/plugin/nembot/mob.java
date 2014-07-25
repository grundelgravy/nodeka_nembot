package net.gamers411.nodeka.umc.plugin.nembot;

public class mob
{
  public int count;
  protected String name;
  protected String fullname;
  protected String singular;
  protected String plural;
  protected String attack;
  protected boolean skip;
  protected boolean kill;

  public mob(String name, String singular, String plural)
  {
    this.name = name;
    this.singular = singular;
    this.plural = plural;
    this.skip = false;
    this.kill = true;
    this.attack = "";
    this.count = 0;
    this.fullname = "";
  }

  public void set_attack(String attack) {
    this.attack = attack;
  }

  public void set_skip(boolean value) {
    this.skip = value;
  }

  public void set_kill(boolean value) {
    this.kill = value;
  }
}