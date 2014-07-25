package net.gamers411.nodeka.umc.plugin.nembot;

import com.lsd.umc.script.ScriptInterface;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class nembot
{
  protected ScriptInterface script;
  protected String version = "1.0.4.2";
  protected script area;
  protected config settings;
  private cpanel config_panel;
  private int time_diff;
  private Pattern p;
  private boolean gotoSleep;
  protected socketMan socket;
  protected boolean validated;
  private int checkPools;
  protected int[] hp_pool;
  protected int[] mp_pool;
  protected int[] sp_pool;
  protected int[] ep_pool;
  private List<event>[] event_queue;
  private List<event>[] prevented_queue;

  public nembot()
  {
    this.script = null;
    this.area = null;
    this.config_panel = null;
    this.gotoSleep = false;
    this.validated = false;
    this.checkPools = 0;
    this.hp_pool = new int[2];
    this.mp_pool = new int[2];
    this.sp_pool = new int[2];
    this.ep_pool = new int[2];
    this.event_queue = new List[4];
    for (int x = 0; x < 4; x++)
      this.event_queue[x] = new ArrayList();
    this.prevented_queue = new List[4];
    for (int x = 0; x < 4; x++)
      this.prevented_queue[x] = new ArrayList();
  }

  public void init(ScriptInterface script) {
    this.script = script;

    script.print("Nembot Plugin v" + this.version + " Loaded.");
    script.print("-- Brought to you by Nemesis");
    script.print("-- http://nodeka.gamers411.net/");
    script.print("\001");

    this.settings = new config();
    this.socket = new socketMan();

    script.registerCommand("LOAD", "net.gamers411.nodeka.umc.plugin.nembot.nembot", "loadScript");
    script.registerCommand("PAUSE", "net.gamers411.nodeka.umc.plugin.nembot.nembot", "pauseScript");
    script.registerCommand("BEGIN", "net.gamers411.nodeka.umc.plugin.nembot.nembot", "beginScript");
    script.registerCommand("END", "net.gamers411.nodeka.umc.plugin.nembot.nembot", "endScript");
    script.registerCommand("CONFIG", "net.gamers411.nodeka.umc.plugin.nembot.nembot", "showConfig");
    script.registerCommand("RESYNC", "net.gamers411.nodeka.umc.plugin.nembot.nembot", "reSync");
    script.registerCommand("NBDEBUG", "net.gamers411.nodeka.umc.plugin.nembot.nembot", "showdebug");
    script.registerCommand("SKIP", "net.gamers411.nodeka.umc.plugin.nembot.nembot", "skiprooms");
    script.registerCommand("SETMOVE", "net.gamers411.nodeka.umc.plugin.nembot.nembot", "setMove");

    this.config_panel = new cpanel(this);
    this.config_panel.setLocationRelativeTo(null);
    this.config_panel.setVisible(true);

    this.time_diff = -3;
  }

  public void IncomingEvent(ScriptInterface script)
  {
    if (this.checkPools > 0) {
      processPools(script);
    }

    this.p = Pattern.compile("^You may again perform (.*) abilities.");
    Matcher m = this.p.matcher(script.getText());
    boolean checkPrevent = false;
    String prevent = "";
    if (m.matches()) {
      checkPrevent = true;
      prevent = m.group(1).toString();
    }

    for (int x = 0; x < 4; x++) {
      for (int y = 0; y < this.prevented_queue[x].size(); y++) {
        if ((checkPrevent) && (prevent.equalsIgnoreCase(((event)this.prevented_queue[x].get(y)).prevent)) && (((event)this.prevented_queue[x].get(y)).prevent.length() > 0))
        {
          System.out.println("Queue prevent fallen for `" + ((event)this.prevented_queue[x].get(y)).action + "`");
          this.prevented_queue[x].remove(y);
        } else if (System.currentTimeMillis() - ((event)this.prevented_queue[x].get(y)).last_cast > ((event)this.prevented_queue[x].get(y)).lag) {
          System.out.println("Queue prevent timed out for `" + ((event)this.prevented_queue[x].get(y)).action + "` " + (System.currentTimeMillis() - ((event)this.prevented_queue[x].get(y)).last_cast) + " > " + ((event)this.prevented_queue[x].get(y)).lag);
          this.prevented_queue[x].remove(y);
        }

      }

    }

    this.p = Pattern.compile("^This action cannot be performed while (.*).");
    m = this.p.matcher(script.getText());

    if (m.matches()) {
      boolean fighting = false;
      if (m.group(1).toString().equals("standing")) {
        fighting = true;
      }
      for (int x = 0; x < 4; x++) {
        for (int y = 0; y < this.prevented_queue[x].size(); y++) {
          event tmp = (event)this.prevented_queue[x].get(y);
          if (fighting) {
            if ((tmp.prevent.length() != 0) && (tmp.position == 3) && (this.area.status != 5))
            {
              this.prevented_queue[x].remove(y);
            }
          } else if ((tmp.prevent.length() != 0) && (this.area.status == 5) && ((tmp.position == 1) || (tmp.position == 2)))
          {
            this.prevented_queue[x].remove(y);
          }
        }
      }
    }

    this.p = Pattern.compile("^The closed (\\w+) block\\(s\\) your passage (\\w+).$");
    m = this.p.matcher(script.getText());
    if (m.matches()) {
      script.send("open " + m.group(2) + "." + m.group(1));
      script.send(m.group(2));
    }

    if ((this.area != null) && (this.area.started()))
    {
      if (this.hp_pool[0] / this.hp_pool[1] * 100.0D < this.settings.min_hp) {
        endScript("");
        if (this.settings.recall)
          script.send("recall");
        return;
      }

      if (((this.hp_pool[0] / this.hp_pool[1] * 100.0D < this.settings.recover_pool) || (this.mp_pool[0] / this.mp_pool[1] * 100.0D < this.settings.recover_pool) || (this.sp_pool[0] / this.sp_pool[1] * 100.0D < this.settings.recover_pool) || (this.ep_pool[0] / this.ep_pool[1] * 100.0D < this.settings.recover_pool)) && (this.validated) && (this.area.sleep != this.settings.recover_sleepon))
      {
        this.area.sleep = this.settings.recover_sleepon;
      }
      else if ((this.hp_pool[0] / this.hp_pool[1] * 100.0D > this.settings.recover_pool) && (this.mp_pool[0] / this.mp_pool[1] * 100.0D > this.settings.recover_pool) && (this.sp_pool[0] / this.sp_pool[1] * 100.0D > this.settings.recover_pool) && (this.ep_pool[0] / this.ep_pool[1] * 100.0D > this.settings.recover_pool) && (this.validated) && ((this.area.sleep == this.settings.recover_sleepon) || (this.area.sleep != this.settings.sleepon)))
      {
        this.area.sleep = this.settings.sleepon;
      }

      this.area.processEvent(script);
      processQueue();
    }

    if (script.getText().equals("You have failed to recall!")) {
      script.send("recall");
    }

    if (this.time_diff == -2) {
      this.p = Pattern.compile("^\\[ Nodeka Uptime  \\]:.* ([\\d]{1,2}) seconds");
      m = this.p.matcher(script.getText());
      if (m.matches()) {
        script.print("[Nembot] Notice : Timer will be set in " + (60 - Integer.parseInt(m.group(1))) + " sec(s).\001");
        this.script.setTimer("nembot_setTimer", (60 - Integer.parseInt(m.group(1))) * 10);
        this.time_diff = -1;
      }
    }
  }

  protected void processQueue() {
    event tmp = null;
    for (int x = 0; x < 4; x++)
      for (int y = 0; y < this.event_queue[x].size(); y++) {
        tmp = (event)this.event_queue[x].get(y);
        if (((tmp.position != 0) && (tmp.position != 1) && (tmp.position != 2)) || ((this.area.status != 3) || (((tmp.position == 2) || (tmp.position == 3)) && (this.area.status == 3))))
        {
          System.out.println("Queue Action: `" + tmp.action + "` Position: `" + tmp.position + "` Status: `" + this.area.status + "`");
          this.script.parse(((event)this.event_queue[x].get(y)).action);
          ((event)this.event_queue[x].get(y)).last_cast = System.currentTimeMillis();
          this.prevented_queue[x].add(this.event_queue[x].get(y));
          this.event_queue[x].remove(y);

          y--;
        }
      }
  }

  public void processPools(ScriptInterface script)
  {
    if (this.checkPools > 0) {
      String line = script.getText();
      Matcher m = null;

      boolean trigger = false;

      this.p = Pattern.compile("H:\\s?(\\d+)(/(\\d+))?", 2);
      m = this.p.matcher(line);

      if (m.find()) {
        trigger = true;
        this.hp_pool[0] = Integer.parseInt(m.group(1));
        if (m.group(3) != null) {
          this.hp_pool[1] = Integer.parseInt(m.group(3));
        }
      }

      this.p = Pattern.compile("M:\\s?(\\d+)(/(\\d+))?", 2);
      m = this.p.matcher(line);

      if (m.find()) {
        trigger = true;
        this.mp_pool[0] = Integer.parseInt(m.group(1));
        if (m.group(3) != null) {
          this.mp_pool[1] = Integer.parseInt(m.group(3));
        }
      }

      this.p = Pattern.compile("S:\\s?(\\d+)(/(\\d+))?", 2);
      m = this.p.matcher(line);

      if (m.find()) {
        trigger = true;
        this.sp_pool[0] = Integer.parseInt(m.group(1));
        if (m.group(3) != null) {
          this.sp_pool[1] = Integer.parseInt(m.group(3));
        }
      }

      this.p = Pattern.compile("E:\\s?(\\d+)(/(\\d+))?", 2);
      m = this.p.matcher(line);

      if (m.find()) {
        trigger = true;
        this.ep_pool[0] = Integer.parseInt(m.group(1));
        if (m.group(3) != null) {
          this.ep_pool[1] = Integer.parseInt(m.group(3));
        }

      }

      if ((this.settings.events_enabled) && (trigger) && (this.area != null)) {
        int percent = 0;

        for (int x = 0; x < 4; x++) {
          if (x == 0)
            percent = (int)(this.hp_pool[0] / this.hp_pool[1] * 100.0D);
          if (x == 1)
            percent = (int)(this.mp_pool[0] / this.mp_pool[1] * 100.0D);
          if (x == 2)
            percent = (int)(this.sp_pool[0] / this.sp_pool[1] * 100.0D);
          if (x == 3)
            percent = (int)(this.ep_pool[0] / this.ep_pool[1] * 100.0D);
          for (int y = 0; y < this.settings.events[x].size(); y++) {
            event tmp = (event)this.settings.events[x].get(y);
            boolean prevented = false;
            for (int z = 0; z < this.prevented_queue[x].size(); z++) {
              if (tmp.action.equals(((event)this.prevented_queue[x].get(z)).action))
                prevented = true;
            }
            for (int z = 0; z < this.event_queue[x].size(); z++) {
              if (tmp.action.equals(((event)this.event_queue[x].get(z)).action))
                prevented = true;
            }
            int pool = tmp.cost_pool == 2 ? this.sp_pool[0] : tmp.cost_pool == 1 ? this.mp_pool[0] : tmp.cost_pool == 0 ? this.hp_pool[0] : this.ep_pool[0];

            if ((percent < tmp.condition_percent) && (!prevented) && (tmp.cost < pool)) {
              if (this.event_queue[x].size() == 0) {
                this.event_queue[x].add(tmp);
                System.out.println("Queue Added `" + tmp.action + "`, no other event listed.");
              } else {
                for (int z = 0; z < this.event_queue[x].size(); z++) {
                  event queue_item = (event)this.event_queue[x].get(z);
                  if ((tmp.condition_percent < queue_item.condition_percent) && (!tmp.prevent.equals(queue_item.prevent)))
                  {
                    this.event_queue[x].add(tmp);
                    System.out.println("Queue Added `" + tmp.action + "`, other spells ahead, no interference.");
                  } else if ((tmp.condition_percent < queue_item.condition_percent) && (tmp.prevent.equals(queue_item.prevent)))
                  {
                    System.out.println("Queue replaced `" + ((event)this.event_queue[x].get(z)).action + "` with `" + tmp.action + "`");
                    this.event_queue[x].set(z, tmp);
                  }
                }
              }
            }
          }
        }
      }
      this.checkPools -= 1;
    }
  }

  public void PromptEvent(ScriptInterface script)
  {
    if (this.settings.username.length() == 0) {
      this.settings.set_name(this.script.getVariable("UMC_NAME"));
    }

    this.checkPools = 3;

    if (this.time_diff == -3) {
      this.time_diff = -2;
      script.send("time");
    }

    if (this.area == null) {
      return;
    }

    if ((this.area.started()) && (!this.gotoSleep) && (this.area.status != 4) && (this.area.status != 5)) {
      this.area.processPrompt(script);
    } else if ((this.gotoSleep) && (this.area.status != 3) && (getTime() <= this.area.sleep)) {
      this.gotoSleep = false;
      this.area.sleep();
    } else if ((this.gotoSleep) && (this.area.status != 3) && (this.area.status != 5) && (getTime() > this.area.sleep)) {
      this.gotoSleep = false;
      this.area.status = 0;
      this.area.processPrompt(script);
    }
  }

  public void TimerEvent(ScriptInterface script, String timerid)
  {
    if (timerid.equals("nembot_setTimer")) {
      try {
        Thread.sleep(500L); } catch (InterruptedException ex) {
      }
      Calendar now = Calendar.getInstance();
      this.time_diff = now.get(13);
      script.setTimer("nembot_Timer", 20);
      script.killTimer("nembot_setTimer");
      script.print("[Nembot] Notice : Timer set, you may begin running.\001");
    }

    if ((timerid.equals("nembot_Timer")) && (this.time_diff > 0)) {
      if (this.area != null) {
        if ((getTime() <= this.settings.sleepon) && (this.area.started())) {
          this.gotoSleep = true;
        }

        if ((this.area.status == 5) && (getTime() > 55) && (this.area.started()))
          this.area.sleep();
      }
      this.config_panel.setTitle("Nembot v" + this.version + " (" + getTime() + ")");
    }
  }

  public void PreTimerEvent(ScriptInterface script, String timerid)
  {
  }

  public void CombatPromptEvent(ScriptInterface script)
  {
  }

  public int getTime()
  {
    Calendar now = Calendar.getInstance();
    int secs = now.get(13);
    if (secs >= this.time_diff) {
      return 60 + this.time_diff - secs;
    }
    return this.time_diff - secs;
  }

  public boolean loadConfig(String filename)
  {
    try
    {
      FileInputStream stream = new FileInputStream(filename);
      ObjectInputStream serializer = new ObjectInputStream(stream);
      this.settings = ((config)serializer.readObject());
      this.settings.set_file(filename);
    } catch (Exception ex) {
      this.script.print("[Nembot] Error : " + ex.getMessage());
      return false;
    }

    if (this.area != null) {
      this.area.update_settings(this.settings);
    }
    boolean last_validated = this.validated;

    this.validated = this.config_panel.checkValidation();

    if ((last_validated) && (last_validated != this.validated)) {
      this.script.unregisterCommand("LOADE");
      this.script.unregisterCommand("SCSEARCH");
    }

    if (this.validated) {
      this.script.registerCommand("SCSEARCH", "net.gamers411.nodeka.umc.plugin.nembot.nembot", "scriptSearch");
      this.script.registerCommand("LOADE", "net.gamers411.nodeka.umc.plugin.nembot.nembot", "loade");
      this.config_panel.populateScripts();
    }
    return true;
  }

  public boolean saveConfig(String filename)
  {
    try
    {
      FileOutputStream stream = new FileOutputStream(filename);
      ObjectOutputStream serializer = new ObjectOutputStream(stream);
      if (this.settings.username.length() == 0)
        this.settings.username = this.script.getVariable("UMC_NAME");
      this.settings.setSaved();
      serializer.writeObject(this.settings);
    } catch (IOException ex) {
      this.script.print("[Nembot] Error : " + ex.getMessage());
      return false;
    }
    return true;
  }

  public String showdebug(String command)
  {
    this.script.print("Nembot Debug Information\001");
    this.script.print("|---------------------------------------|");
    this.script.print("| Version: " + this.version + "\001");
    this.script.print("| GotoSleep: " + this.gotoSleep + "\001");
    this.script.print("|\001");
    this.script.print("| Pool Information\001");
    this.script.print("| HP: " + this.hp_pool[0] + "/" + this.hp_pool[1] + "\001");
    this.script.print("| MP: " + this.mp_pool[0] + "/" + this.mp_pool[1] + "\001");
    this.script.print("| SP: " + this.sp_pool[0] + "/" + this.sp_pool[1] + "\001");
    this.script.print("| EP: " + this.ep_pool[0] + "/" + this.ep_pool[1] + "\001");
    this.script.print("|\001");
    if (this.area != null)
      this.area.outputDebug();
    this.script.print("|---------------------------------------|\001");
    return "";
  }

  public String loadScript(String area)
  {
    this.area = new script(this.script, this.settings, this.settings.areas_dir + "/NOD_" + area + ".txt", this.config_panel);
    this.config_panel.loadScript();
    return "";
  }

  public String loade(String command)
  {
    this.area.loadScript_extras(command);
    return "";
  }

  public String reSync(String args)
  {
    this.script.send("time");
    this.time_diff = -2;
    return "";
  }

  public String beginScript(String command)
  {
    if (this.area == null)
      this.script.print("[Nembot] Notice: There is no script loaded.\001");
    if (!this.area.started()) {
      this.area.start();
      this.config_panel.ministatus.script_start();
    } else {
      this.script.print("[Nembot] Notice: The script has already been started.\001");
    }
    return "";
  }

  public String skiprooms(String command)
  {
    if (this.area == null) {
      this.script.print("[Nembot] Notice: There is no script loaded.\001");
      return "";
    }
    try {
      this.area.skip = Integer.parseInt(command);
    }
    catch (Exception ex)
    {
      this.script.print("Syntax : #skip <integer>\001");
    }
    return "";
  }

  public String endScript(String command)
  {
    if (this.area != null) {
      this.area = null;
      this.script.print("[Nembot] Notice: Stopping current script.\001");
      this.config_panel.ministatus.script_end();
    } else {
      this.script.print("[Nembot] Notice: There is no script loaded at the moment.\001");
    }
    return "";
  }

  public String showConfig(String command)
  {
    this.config_panel.setVisible(true);
    return "";
  }

  public String pauseScript(String command)
  {
    if (this.area != null) {
      this.script.print("[Nembot] Notice: Script paused.\001");
      this.area.pause();
      this.config_panel.ministatus.script_pause();
    } else {
      this.script.print("[Nembot] Notice: There isn't a script loaded.\001");
    }
    return "";
  }

  public String scriptSearch(String command)
  {
    if (command.length() == 0) {
      this.script.print("Syntax: #scsearch <area>\001");
      return "";
    }
    File dir = new File(this.settings.areas_dir);
    String[] children = dir.list();
    this.script.print("Search results for: `" + command + "`\001");
    if (children != null)
    {
      for (int i = 0; i < children.length; i++)
      {
        if (((children[i].startsWith("NOD_")) || (children[i].startsWith("NODe_"))) && (children[i].contains(command)) && (children[i].endsWith(".txt")) && (this.validated))
          this.script.print(this.settings.areas_dir + "/" + children[i] + "\001");
        else if ((children[i].startsWith("NOD_")) && (children[i].endsWith(".txt")) && (children[i].contains(command)) && (!this.validated))
          this.script.print(this.settings.areas_dir + "/" + children[i] + "\001");
      }
    }
    return "";
  }

  public String setMove(String args)
  {
    if (args.length() == 0)
    {
      this.script.print("Syntax : #setmove <move or shortcut name>\001");
      return "";
    }
    if (this.area == null)
    {
      this.script.print("[Nembot] Error : You must have an area loaded first.\001");
      return "";
    }
    try
    {
      this.area.setMove(Integer.parseInt(args));
    }
    catch (Exception ex)
    {
      if (this.area.setMove(args))
        this.script.print("[Nembot Notice: Set move to " + this.area.move);
      else {
        this.script.print("[Nembot Notice: A shortcut by that name does not exist.\001");
      }
    }
    return "";
  }
}