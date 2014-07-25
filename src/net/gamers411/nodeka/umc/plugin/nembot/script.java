package net.gamers411.nodeka.umc.plugin.nembot;

import com.lsd.umc.script.ScriptInterface;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class script
{
  protected String filename;
  protected mob[] mobs;
  protected List<String> path;
  protected HashMap shortcuts;
  private ScriptInterface script;
  private config settings;
  private cpanel panel;
  public int sleep;
  protected boolean move_random;
  private String last_move;
  private String[] exits;
  public int status;
  protected int move;
  protected int skip;
  private int lastKill;

  public script(ScriptInterface script, config settings, String filename, cpanel panel)
  {
    this.panel = panel;
    this.script = script;
    this.settings = settings;
    this.mobs = null;
    this.path = null;
    this.status = -1;
    this.skip = 0;
    this.sleep = this.settings.sleepon;

    if (loadScript(filename))
    {
      if (new File(filename.replace("NOD_", "NODe_")).exists())
        loadScript_extras(filename.replace("NOD_", "NODe_"));
    }
    this.last_move = "";
  }

  public script(ScriptInterface script)
  {
    this.script = script;
    this.mobs = null;
    this.path = null;
    this.status = -1;
    this.last_move = "";
  }

  public void update_settings(config settings) {
    this.settings = settings;
  }

  protected void setMove(int move) {
    this.move = move;
  }

  protected boolean setMove(String shortcut) {
    if (this.shortcuts.containsKey(shortcut))
    {
      this.move = Integer.parseInt(this.shortcuts.get(shortcut).toString());
      return true;
    }
    return false;
  }

  protected void updateScript(mob[] mobs, List<String> path, HashMap shortcuts) {
    this.mobs = mobs;
    this.path = path;
    this.shortcuts = shortcuts;
  }

  public void processPrompt(ScriptInterface script)
  {
    boolean mobCheck = false;

    if (this.status == 2) {
      this.status = 0;
      mobCheck = true;
    }
    if (this.status == 0)
    {
      for (int x = 0; x < this.mobs.length; x++) {
        if (this.skip > 0) {
          this.skip -= 1;
          break;
        }
        if (this.mobs[x].count > 0) {
          this.mobs[x].count -= 1;
          this.lastKill = x;
          this.status = 3;

          if ((mobCheck == true) && (this.settings.pre_attack.length() > 0)) {
            script.parse(this.settings.pre_attack);
          }
          script.parse((this.mobs[x].attack.length() != 0 ? this.mobs[x].attack : this.settings.attack) + " " + this.mobs[x].name);
          return;
        }
      }

      move();
    }
  }

  public void outputDebug() {
    this.script.print("| Script Information\001");
    this.script.print("| Status: " + this.status + "\001");
    this.script.print("| Sleep: " + this.sleep + "\001");
    String exitString = "";
    if (this.exits.length != 0) {
      for (int x = 0; x < this.exits.length; x++)
        exitString = this.exits[x] + (x + 1 < this.exits.length ? " " : "");
    }
    this.script.print("| Exits: " + exitString + "\001");
    this.script.print("| Move: " + this.move + "\001");
    this.script.print("| Move Random: " + this.move_random + "\001");
    this.script.print("| Last move: " + this.last_move + "\001");
    this.script.print("| Last kill: " + this.mobs[this.lastKill].name + "\001");
    this.script.print("|\001");
  }

  public void processEvent(ScriptInterface script) {
    String line = script.getText();

    if ((this.status == 4) || (this.status == 5)) {
      return;
    }
    if ((this.status == 3) && (line.equals("While you're sleeping? Maybe you should wake up first?"))) {
      script.send("wake");
      script.parse((this.mobs[this.lastKill].attack.length() != 0 ? this.mobs[this.lastKill].attack : this.settings.attack) + " " + this.mobs[this.lastKill].name);
    } else if ((this.status != 3) && (line.equals("While you're fighting?? No way!!"))) {
      if (this.status == 1)
        this.move -= 1;
      this.status = 3;
    }

    if (this.status == 1) {
      Pattern p = Pattern.compile("\\[ exits: ([\\(\\)\\w\\s]+) \\]$|^\\[Exits: ([\\(\\)\\w\\s]+)\\]");
      Matcher m = p.matcher(line);

      if ((m.matches()) || (m.find())) {
        for (int x = 0; x < this.mobs.length; x++)
          this.mobs[x].count = 0;
        if (m.group(1) != null)
          this.exits = m.group(1).split("\\s");
        else
          this.exits = m.group(2).split("\\s");
        this.status = 2;
      }

    }
    else if (this.status == 2)
    {
      if ((this.settings.player_skip) && ((line.startsWith("( immoral")) || (line.startsWith("( moral")) || (line.startsWith("( true impartial")) || (line.startsWith("( impartial"))))
      {
        move();
        return;
      }
      for (int x = 0; x < this.mobs.length; x++)
      {
        if ((line.equals(this.mobs[x].singular)) && (this.mobs[x].kill)) {
          if (this.mobs[x].skip) {
            move();
          }
          this.mobs[x].count += 1;
          break;
        }

        if (((line.startsWith(this.mobs[x].plural)) || (line.endsWith(this.mobs[x].plural))) && (this.mobs[x].plural.length() > 0) && (this.mobs[x].kill)) {
          if (this.mobs[x].skip) {
            move();
          }
          Pattern p = Pattern.compile("\\[ ([\\d\\w-]+) \\]");
          Matcher m = p.matcher(line);

          if (m.find()) {
            int val = 0;
            try
            {
              val = Integer.parseInt(m.group(1));
            } catch (NumberFormatException ex) {
              if (m.group(1).contains("sixty"))
                val = 60;
              if (m.group(1).contains("fifty"))
                val = 50;
              if (m.group(1).contains("fourty"))
                val = 40;
              if (m.group(1).contains("thirty"))
                val = 30;
              if (m.group(1).contains("twenty"))
                val = 20;
              if (m.group(1).equals("nineteen"))
                val = 19;
              if (m.group(1).equals("eighteen"))
                val = 18;
              if (m.group(1).equals("seventeen"))
                val = 17;
              if (m.group(1).equals("sixteen"))
                val = 16;
              if (m.group(1).equals("fifteen"))
                val = 15;
              if (m.group(1).equals("fourteen"))
                val = 14;
              if (m.group(1).equals("thirteen"))
                val = 13;
              if (m.group(1).equals("twelve"))
                val = 12;
              if (m.group(1).equals("eleven"))
                val = 11;
              if (m.group(1).equals("ten"))
                val = 10;
              if (!m.group(1).contains("teen")) {
                if (m.group(1).contains("nine"))
                  val += 9;
                if (m.group(1).contains("eight"))
                  val += 8;
                if (m.group(1).contains("seven"))
                  val += 7;
                if (m.group(1).contains("six"))
                  val += 6;
                if (m.group(1).contains("five"))
                  val += 5;
                if (m.group(1).contains("four"))
                  val += 4;
                if (m.group(1).contains("three"))
                  val += 3;
                if (m.group(1).contains("two"))
                  val += 2;
                if (m.group(1).contains("one")) {
                  val++;
                }
              }
            }
            this.mobs[x].count += val;
            break;
          }System.out.println("Found " + this.mobs[x].name + " but couldn't parse count...");

          break;
        }

      }

    }
    else if (this.status == 3) {
      Pattern p = null;
      Matcher m = null;

      p = Pattern.compile("^([\\w\\s]+) has arrived.$");
      m = p.matcher(line);
      if (m.matches()) {
        for (int x = 0; x < this.mobs.length; x++) {
          if ((this.mobs[x].fullname.equals(m.group(1).toLowerCase())) && (this.mobs[x].kill)) {
            this.mobs[x].count += 1;
            break;
          }
        }
      }

      p = Pattern.compile("^([\\w\\s]+) leaves (north|south|east|west|up|down).$");
      m = p.matcher(line);
      if (m.matches()) {
        for (int x = 0; x < this.mobs.length; x++) {
          if ((this.mobs[x].fullname.equals(m.group(1).toLowerCase())) && (this.mobs[x].kill)) {
            this.mobs[x].count -= 1;
            break;
          }

        }

      }

      if (line.equals("A mystical shield prevents you from performing that action.")) {
        script.parse((this.mobs[this.lastKill].attack.length() != 0 ? this.mobs[this.lastKill].attack : this.settings.attack) + " " + this.mobs[this.lastKill].name);
      }
      if ((line.endsWith("is dead!")) || (line.equals("You hear the screams of a spirit as it flies by you.")) || (line.equals("They aren't here.")) || (line.equals("There is no one here by that name.")))
      {
        if ((line.endsWith("is dead!")) && (this.mobs[this.lastKill].fullname.length() == 0)) {
          p = Pattern.compile("^(.*) falls to the ground! \\w+ is dead!$", 2);
          m = p.matcher(line);

          if ((m.matches()) && 
            (m.group(1).contains(this.mobs[this.lastKill].name))) {
            this.mobs[this.lastKill].fullname = m.group(1).toLowerCase().trim();
            System.out.println("Match found for full name: " + m.group(1).toLowerCase());
          }

        }

        this.status = 0;
      }
    }
  }

  public String get_move(int position)
  {
    if (!this.move_random) {
      this.last_move = ((String)this.path.get(position));
      return (String)this.path.get(position);
    }
    Random gen = new Random();
    int val = 0;
    String rev_dir = "";
    if (this.last_move.equals("north"))
      rev_dir = "south";
    else if (this.last_move.equals("south"))
      rev_dir = "north";
    else if (this.last_move.equals("east"))
      rev_dir = "west";
    else if (this.last_move.equals("west"))
      rev_dir = "east";
    else if (this.last_move.equals("up"))
      rev_dir = "down";
    else if (this.last_move.equals("down")) {
      rev_dir = "up";
    }
    for (int x = 0; x < 10; x++) {
      val = gen.nextInt(this.exits.length);
      if ((!this.exits[val].equals(rev_dir)) && (this.exits[val].indexOf("(") == -1))
        break;
    }
    this.last_move = this.exits[val];
    return this.exits[val];
  }

  public void move() {
    if ((this.move + 1 < this.path.size()) || (this.move_random)) {
      this.status = 1;
      this.script.parse(get_move(++this.move));
      this.panel.setProgress((int)Math.round(this.move / this.path.size() * 100.0D));
    } else {
      this.status = -1;
      this.script.print("[Nembot] Notice: Script finished...");
      this.panel.setProgress(100);
      this.panel.ministatus.script_end();
    }
  }

  public boolean start() {
    if (!loaded()) {
      this.script.print("[Nembot] Error: Script not loaded.\001");
      return false;
    }
    this.script.parse("#rates clear");
    this.status = 1;
    this.script.send("look");

    return true;
  }

  protected void set_filename(String filename) {
    this.filename = filename;
  }

  public void pause() {
    if (!loaded()) {
      this.script.print("[Nembot] Error: Script not loaded.\001");
      return;
    }
    if (!started()) {
      this.script.print("[Nembot] Error: Script not started.\001");
      return;
    }
    this.script.send("");
    this.status = (this.status == 4 ? 0 : 4);
  }

  public void sleep() {
    if (!loaded()) {
      this.script.print("[Nembot] Error: Script not loaded.\001");
      return;
    }
    if (!started()) {
      this.script.print("[Nembot] Error: Script not started.\001");
      return;
    }
    this.script.send(this.status == 5 ? "wake" : "sleep");
    this.status = (this.status == 5 ? 0 : 5);
  }

  public boolean started() {
    if (this.status == -1) {
      return false;
    }
    return true;
  }

  private boolean loaded() {
    if ((this.mobs == null) || (this.path == null)) {
      return false;
    }
    return true;
  }

  public void incmob(int index, int count) {
    this.mobs[index].count += count;
  }

  public boolean loadScript(String filename) {
    this.filename = filename;
    Scanner scan = null;
    try {
      scan = new Scanner(new File(filename));
    } catch (FileNotFoundException ex) {
      if (this.script != null)
        this.script.print("[Nembot] Error: " + ex.getMessage() + "\001");
      return false;
    }
    if (this.script != null) {
      this.script.print("Loading : " + filename + "\001");
    }
    int stage = 0;
    int count = 0;
    int pos = 0;

    String name = "";
    String singular = "";
    String plural = "";
    while (scan.hasNext()) {
      String token = scan.next();

      switch (stage) {
      case 0:
        if (token.equals("MOBDEF")) {
          count = scan.nextInt();
          this.move_random = false;
          this.mobs = new mob[count];
          this.move = 0;
          stage = 1;
        } else {
          if (this.script != null)
            this.script.print("[Nembot] Error : Parse error at `" + token + "`, expecting MOBDEF.");
          return false;
        }
        break;
      case 1:
        if (token.equals("MOBILE")) {
          name = scan.next();
          if (this.script != null) {
            this.script.print("Loading " + name + "...\001");
          }
          scan.nextLine();
          singular = scan.nextLine();
          plural = scan.nextLine();

          this.mobs[(pos++)] = new mob(name, singular, plural);

          if (pos == count)
            stage = 2;
        }
        else {
          if (this.script != null)
            this.script.print("[Nembot] Error : Parse error at `" + token + "`, expecting MOBILE.");
          return false;
        }

        break;
      case 2:
        if (token.equals("PATH")) {
          if (this.script != null)
            this.script.print("Reading path...\001");
          this.path = new ArrayList();
          pos = 0;
          Pattern p = Pattern.compile("^// (.*)$");

          this.shortcuts = new HashMap();
          for (token = scan.nextLine(); !token.equals("END"); token = scan.nextLine()) {
            Matcher m = p.matcher(token);
            if (m.matches()) {
              if (this.script != null)
                this.script.print("Loaded shortcut: `" + m.group(1) + "`\001");
              this.shortcuts.put(m.group(1), Integer.valueOf(pos));
            }
            else {
              if (token.equals("RANDOM")) {
                this.move_random = true;
              }
              this.path.add(pos++, token);

              if (!scan.hasNext()) {
                if (this.script != null)
                  this.script.print("[Nembot] Error : Parse error at `" + token + "`, end of file reached.");
                return false;
              }
            }
          }
        } else {
          if (this.script != null)
            this.script.print("[Nembot] Error : Parse error at `" + token + "`, missing PATH.");
          return false;
        }
        break;
      }

    }

    if (this.script != null) {
      this.script.print("Finished loading " + name + ".\001");
      this.script.print("You may type #begin to start.\001");
    }
    return true;
  }

  public boolean loadScript_extras(String filename)
  {
    Scanner scan = null;
    try {
      scan = new Scanner(new File(filename));
    } catch (FileNotFoundException ex) {
      if (this.script != null)
        this.script.print("[Nembot] Notice: " + filename + " doesn't exist.\001");
      return false;
    }
    if (this.script != null)
      this.script.print("Loading : " + filename + "\001");
    int stage = 0;
    int count = 0;
    int pos = 0;

    Pattern p = Pattern.compile("^[\"]?([\\w+\\s]+)[\"]?\\s?[\"]?([\\w+\\s']+)?[\"]?\\s?(\\w+)?$");
    Matcher m = null;

    String name = "";
    String line = "";
    while (scan.hasNext()) {
      String token = scan.next();

      switch (stage) {
      case 0:
        if (token.equals("MOBDEF")) {
          count = scan.nextInt();
          this.move = 0;
          stage = 1;
        } else {
          if (this.script != null)
            this.script.print("[Nembot] Error : Parse error at `" + token + "`, expecting MOBDEF.");
          return false;
        }
        break;
      case 1:
        if (token.equals("MOBILE"))
        {
          line = scan.nextLine().trim();
          scan.nextLine();
          scan.nextLine();

          m = p.matcher(line);

          pos++;

          if (m.matches()) {
            for (int x = 0; x < this.mobs.length; x++) {
              if (this.mobs[x].name.equals(m.group(1))) {
                if (m.group(2) != null)
                  this.mobs[x].set_attack(m.group(2));
                if (m.group(3) != null)
                  if (m.group(3).toString().toLowerCase().equals("nokill"))
                    this.mobs[x].set_kill(false);
                  else if (m.group(3).toString().toLowerCase().equals("skip"))
                    this.mobs[x].set_skip(false);
              }
            }
          }
          else
          {
            if (this.script != null)
              this.script.print("[Nembot] Error : Malformed line at `" + line + "`.");
            return false;
          }
          if (pos == count)
            stage = 2;
        }
        else {
          if (this.script != null)
            this.script.print("[Nembot] Error : Parse error at `" + token + "`, expecting MOBILE.");
          return false;
        }
        break;
      }
    }

    if (this.script != null)
      this.script.print("Finished loading " + name + ".\001");
    return true;
  }

  public void saveScript(String filename) {
    try {
      BufferedWriter out = new BufferedWriter(new FileWriter(filename));
      out.write("MOBDEF " + this.mobs.length);
      out.newLine();
      for (int x = 0; x < this.mobs.length; x++) {
        out.write("MOBILE " + this.mobs[x].name);
        out.newLine();
        out.write(this.mobs[x].singular);
        out.newLine();
        out.write(this.mobs[x].plural);
        out.newLine();
      }

      out.write("PATH");
      out.newLine();
      for (int x = 0; x < this.path.size(); x++) {
        if (this.shortcuts.containsValue(Integer.valueOf(x))) {
          Iterator keys = this.shortcuts.keySet().iterator();
          while (keys.hasNext()) {
            String key = keys.next().toString();
            int value = Integer.parseInt(this.shortcuts.get(key).toString());
            if (value == x) {
              out.write("// " + key);
              out.newLine();
            }
          }
        }
        out.write((String)this.path.get(x));
        out.newLine();
      }
      out.write("END");
      out.newLine();

      out.close();
    } catch (Exception ex) {
      System.out.println(ex.getMessage());
    }
  }

  public void saveScript_extras(String filename) {
    try {
      BufferedWriter out = new BufferedWriter(new FileWriter(filename));
      out.write("MOBDEF " + this.mobs.length);
      out.newLine();
      for (int x = 0; x < this.mobs.length; x++) {
        out.write("MOBILE \"" + this.mobs[x].name + "\" \"" + this.mobs[x].attack + "\" " + (this.mobs[x].skip ? "skip" : !this.mobs[x].kill ? "nokill" : ""));

        out.newLine();
        out.newLine();
        out.newLine();
      }
      out.close();
    }
    catch (Exception ex)
    {
    }
  }
}