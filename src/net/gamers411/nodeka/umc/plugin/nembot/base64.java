package net.gamers411.nodeka.umc.plugin.nembot;

public class base64
{
  static final String baseTable = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";

  public static String encode(byte[] bytes)
  {
    StringBuffer tmp = new StringBuffer();

    int i = 0;

    for (i = 0; i < bytes.length - bytes.length % 3; i += 3)
    {
      byte pos = (byte)(bytes[i] >> 2 & 0x3F);

      tmp.append("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".charAt(pos));

      pos = (byte)(((bytes[i] & 0x3) << 4) + (bytes[(i + 1)] >> 4 & 0xF));

      tmp.append("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".charAt(pos));

      pos = (byte)(((bytes[(i + 1)] & 0xF) << 2) + (bytes[(i + 2)] >> 6 & 0x3));

      tmp.append("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".charAt(pos));

      pos = (byte)(bytes[(i + 2)] & 0x3F);

      tmp.append("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".charAt(pos));

      if ((i + 2) % 56 == 0)
      {
        tmp.append("\r\n");
      }

    }

    if (bytes.length % 3 != 0)
    {
      if (bytes.length % 3 == 2)
      {
        byte pos = (byte)(bytes[i] >> 2 & 0x3F);

        tmp.append("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".charAt(pos));

        pos = (byte)(((bytes[i] & 0x3) << 4) + (bytes[(i + 1)] >> 4 & 0xF));

        tmp.append("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".charAt(pos));

        pos = (byte)((bytes[(i + 1)] & 0xF) << 2);

        tmp.append("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".charAt(pos));

        tmp.append("=");
      }
      else if (bytes.length % 3 == 1)
      {
        byte pos = (byte)(bytes[i] >> 2 & 0x3F);

        tmp.append("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".charAt(pos));

        pos = (byte)((bytes[i] & 0x3) << 4);

        tmp.append("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".charAt(pos));

        tmp.append("==");
      }

    }

    return tmp.toString();
  }

  public static String encode(String src)
  {
    return encode(src.getBytes());
  }

  public static byte[] decode(String src)
    throws Exception
  {
    byte[] bytes = null;

    StringBuffer buf = new StringBuffer(src);

    int i = 0;

    char c = ' ';

    char oc = ' ';

    while (i < buf.length())
    {
      oc = c;

      c = buf.charAt(i);

      if ((oc == '\r') && (c == '\n'))
      {
        buf.deleteCharAt(i);

        buf.deleteCharAt(i - 1);

        i -= 2;
      }
      else if (c == '\t')
      {
        buf.deleteCharAt(i);

        i--;
      }
      else if (c == ' ')
      {
        i--;
      }

      i++;
    }

    if (buf.length() % 4 != 0)
    {
      throw new Exception("Base64 decoding invalid length");
    }

    bytes = new byte[3 * (buf.length() / 4)];

    int index = 0;

    for (i = 0; i < buf.length(); i += 4)
    {
      byte data = 0;

      int nGroup = 0;

      for (int j = 0; j < 4; j++)
      {
        char theChar = buf.charAt(i + j);

        if (theChar == '=')
        {
          data = 0;
        }
        else
        {
          data = getBaseTableIndex(theChar);
        }

        if (data == -1)
        {
          throw new Exception("Base64 decoding bad character");
        }

        nGroup = 64 * nGroup + data;
      }

      bytes[index] = ((byte)(0xFF & nGroup >> 16));

      index++;

      bytes[index] = ((byte)(0xFF & nGroup >> 8));

      index++;

      bytes[index] = ((byte)(0xFF & nGroup));

      index++;
    }

    byte[] newBytes = new byte[index];

    for (i = 0; i < index; i++)
    {
      newBytes[i] = bytes[i];
    }

    return newBytes;
  }

  protected static byte getBaseTableIndex(char c)
  {
    byte index = -1;

    for (byte i = 0; i < "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".length(); i = (byte)(i + 1))
    {
      if ("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".charAt(i) == c)
      {
        index = i;

        break;
      }

    }

    return index;
  }
}