package net.gamers411.nodeka.umc.plugin.nembot;

import com.sun.net.ssl.internal.ssl.Provider;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.security.Security;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.LinkedList;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.swing.JProgressBar;

public class socketMan
{
  private static boolean SSLLoaded = false;

  protected static HashMap URLMan = new HashMap();

  protected int status = 0;

  socketMan()
  {
    LinkedList result = getURL("http://www.nodeka411.net/public/devStatus.txt");
    this.status = Integer.parseInt(result.get(0).toString());
    System.out.println("System Link status :" + this.status);
  }

  public String getVersion()
  {
    return getURL((String)URLMan.get("version")).get(0).toString();
  }

  public String getCRC(String version) {
    return getURL((String)URLMan.get("update") + "/crc-" + version + ".txt").get(0).toString();
  }

  public String getChanges(String version) {
    String changes = "";
    LinkedList results = getURL((String)URLMan.get("update") + "/changes-" + version + ".txt");
    for (int x = 0; x < results.size(); x++) {
      changes = changes + results.get(x).toString() + "\n";
    }
    return changes;
  }

  public String md5(String passphrase) throws Exception {
    String s = passphrase;
    MessageDigest m = MessageDigest.getInstance("MD5");
    m.update(s.getBytes(), 0, s.length());
    return new BigInteger(1, m.digest()).toString(16);
  }

  public String validate(String username, String uuid) {
    LinkedList result = new LinkedList();
    try {
      result = getURL(URLMan.get("authenticate") + "&name=" + URLEncoder.encode(username, "UTF-8") + "&uuid=" + URLEncoder.encode(uuid, "UTF-8"));
    } catch (Exception ex) {
    }
    return result.get(0).toString();
  }

  public String register(String username, String pass, String charactername, String uuid) {
    LinkedList result = new LinkedList();
    System.out.println(pass);
    try
    {
      System.out.println(md5(pass));
      result = getURL(URLMan.get("register") + "&username=" + URLEncoder.encode(username, "UTF-8") + "&password=" + URLEncoder.encode(md5(pass), "UTF-8") + "&charactername=" + URLEncoder.encode(charactername, "UTF-8") + "&uuid=" + URLEncoder.encode(uuid, "UTF-8"));
    } catch (Exception ex) {
    }
    return result.get(0).toString();
  }

  public String recover(String username, String password, String charactername) {
    LinkedList result = new LinkedList();
    try {
      result = getURL(URLMan.get("recover") + "&username=" + URLEncoder.encode(username, "UTF-8") + "&password=" + URLEncoder.encode(md5(password), "UTF-8") + "&charactername=" + URLEncoder.encode(charactername, "UTF-8")); } catch (Exception ex) {
    }
    System.out.println(result.get(0));
    return result.get(0).toString();
  }

  public String release(String username, String uuid) {
    LinkedList result = new LinkedList();
    try {
      result = getURL(URLMan.get("release") + "&name=" + URLEncoder.encode(username, "UTF-8") + "&uuid=" + URLEncoder.encode(uuid, "UTF-8"));
    } catch (Exception ex) {
    }
    return result.get(0).toString();
  }

  private LinkedList getURL(String address) {
    initSSL(true);
    LinkedList returnList = new LinkedList();
    if (this.status == -1)
    {
      System.out.print("Setting status failed earlier check...");
      returnList.add("Error");
    }
    else
    {
      try
      {
        URL url = new URL(address);
        URLConnection connection = url.openConnection();
        connection.setUseCaches(false);
        connection.setReadTimeout(5000);

        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String line;
        while ((line = in.readLine()) != null)
        {
          returnList.add(line);
        }

        this.status = 1;
        in.close();
      }
      catch (Exception e) {
        this.status = -1;
        returnList.add("Error");
        System.err.println("Failed to retrieve URL");
      }
    }
    return returnList;
  }

  public boolean downloadupdate(String update, String filename, JProgressBar jProgress) {
    try {
      URL url = new URL(URLMan.get("update") + "/" + update);
      URLConnection uc = url.openConnection();
      String contentType = uc.getContentType();
      int contentLength = uc.getContentLength();
      if ((contentType.startsWith("text/")) || (contentLength == -1)) {
        throw new IOException("This is not a binary file.");
      }
      InputStream raw = uc.getInputStream();
      InputStream in = new BufferedInputStream(raw);
      byte[] data = new byte[contentLength];
      int bytesRead = 0;
      int offset = 0;
      while (offset < contentLength) {
        jProgress.setValue((int)(offset / contentLength * 100.0D));
        bytesRead = in.read(data, offset, data.length - offset);
        if (bytesRead == -1)
          break;
        offset += bytesRead;
      }
      jProgress.setValue(100);
      in.close();

      if (offset != contentLength) {
        throw new IOException("Only read " + offset + " bytes; Expected " + contentLength + " bytes");
      }

      FileOutputStream out = new FileOutputStream(filename);
      out.write(data);
      out.flush();
      out.close();
    } catch (Exception e) {
      return false;
    }

    return true;
  }

  public static void initSSL(boolean trusted)
  {
    if (!SSLLoaded)
    {
      SSLLoaded = true;
      if (!trusted)
      {
        Security.addProvider(new Provider());
        TrustManager[] trustAllCerts = { new X509TrustManager()
        {
          public X509Certificate[] getAcceptedIssuers()
          {
            return null;
          }

          public void checkServerTrusted(X509Certificate[] ax509certificate, String s)
            throws CertificateException
          {
          }

          public void checkClientTrusted(X509Certificate[] ax509certificate, String s)
            throws CertificateException
          {
          }
        }
         };
        try
        {
          SSLContext sc = SSLContext.getInstance("SSL");
          sc.init(null, trustAllCerts, new SecureRandom());
          HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        }
        catch (Exception e)
        {
          e.printStackTrace();
        }
      }
    }
  }

  static
  {
    try
    {
      new base64(); URLMan.put("authenticate", new String(base64.decode("aHR0cHM6Ly9kZXYubm9kZWthNDExLm5ldC9uZW1ib3QvYmFja2VuZC5waHA/b3A9dmFsaWRhdGU=")).trim());
      new base64(); URLMan.put("version", new String(base64.decode("aHR0cHM6Ly9kZXYubm9kZWthNDExLm5ldC9uZW1ib3QvdmVyc2lvbi50eHQ=")).trim());
      new base64(); URLMan.put("register", new String(base64.decode("aHR0cHM6Ly9kZXYubm9kZWthNDExLm5ldC9uZW1ib3QvYmFja2VuZC5waHA/b3A9cmVnaXN0ZXI=")).trim());
      new base64(); URLMan.put("recover", new String(base64.decode("aHR0cHM6Ly9kZXYubm9kZWthNDExLm5ldC9uZW1ib3QvYmFja2VuZC5waHA/b3A9cmVjb3Zlcg==")).trim());
      new base64(); URLMan.put("release", new String(base64.decode("aHR0cHM6Ly9kZXYubm9kZWthNDExLm5ldC9uZW1ib3QvYmFja2VuZC5waHA/b3A9cmVsZWFzZQ==")).trim());
      new base64(); URLMan.put("update", new String(base64.decode("aHR0cHM6Ly9kZXYubm9kZWthNDExLm5ldC9uZW1ib3QvdXBkYXRlcw==")).trim());
    }
    catch (Exception ex)
    {
    }
  }
}