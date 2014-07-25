package net.gamers411.nodeka.umc.plugin.nembot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class config
  implements Serializable
{
  protected int min_hp;
  protected int recover_pool;
  protected int recover_sleepon;
  protected int sleepon;
  protected String attack;
  protected String pre_attack;
  protected boolean showaggro;
  protected boolean recall;
  protected boolean togmap;
  protected boolean detect_status;
  protected boolean use_prompt;
  protected boolean player_skip;
  protected String areas_dir;
  protected String profiles_dir;
  protected String file;
  protected String username;
  protected String key;
  protected boolean events_enabled;
  protected List<event>[] events;
  protected boolean saved_to_file;

  public config()
  {
    this.min_hp = 30;
    this.recover_pool = 0;
    this.recover_sleepon = 5;
    this.sleepon = 5;
    this.attack = "kill";
    this.pre_attack = "";
    this.showaggro = false;
    this.recall = true;
    this.togmap = false;
    this.detect_status = false;
    this.use_prompt = false;
    this.saved_to_file = false;
    this.areas_dir = "areas";
    this.profiles_dir = "profiles";
    this.username = "";
    this.file = "";
    this.key = UUID.randomUUID().toString();

    this.events = new List[4];
    for (int x = 0; x < 4; x++)
      this.events[x] = new ArrayList();
    this.events_enabled = false;
  }

  public void set_name(String name) {
    this.username = name;
  }

  public void set_key(String name) {
    this.key = name;
  }

  public void setSaved() {
    this.saved_to_file = true;
  }

  public void set_attack(String attack)
  {
    this.attack = attack;
  }

  public void set_preattack(String attack) {
    this.pre_attack = attack;
  }

  public void set_recoverpools(int percent) {
    this.recover_pool = percent;
  }

  public void set_recoversleepon(int percent) {
    this.recover_sleepon = percent;
  }

  public void set_sleepon(int tick) {
    this.sleepon = tick;
  }

  public void set_minhp(int percent) {
    this.min_hp = percent;
  }

  public void set_file(String name) {
    this.file = name;
  }

  public void set_recall(boolean value)
  {
    this.recall = value;
  }

  public void set_togmap(boolean value) {
    this.togmap = value;
  }

  public void set_showaggro(boolean value) {
    this.showaggro = value;
  }

  public void set_detectstatus(boolean value) {
    this.detect_status = value;
  }

  public void set_playerskip(boolean value) {
    this.player_skip = value;
  }

  public void set_useprompt(boolean value) {
    this.use_prompt = value;
  }
}