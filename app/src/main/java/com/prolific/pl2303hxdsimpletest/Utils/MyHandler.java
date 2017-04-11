package com.prolific.pl2303hxdsimpletest.Utils;


import android.util.Log;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.List;

public class MyHandler extends DefaultHandler
{

  private boolean in_resp = false;
  private boolean in_city = false;
  private boolean in_wendu = false;
  private boolean in_fengli = false;
  private boolean in_shidu = false;
  private boolean in_pm25 = false;
  private boolean in_quality=false;
  private boolean in_type0=false;


  private boolean in_mainTitle = false;
  private List<News> li;
  private News news;
  private String title="";
  private StringBuffer buf=new StringBuffer();

  public String getCityData() {

      return news._city;

  }
  public String getWenduData() {
    return news._wendu;
  }
  public String getShiduData() {
    return news._shidu;
  }
  public String getFengliDate() {
    return news._fengli;
  }
  public String getPm25Data() {
    return news._pm25;
  }
  public String getQualityData() {
    return news._quality;
  }
  public String getTypeData() {
    return news._type0.get(0);
  }

  public String getRssTitle()
  {
    return title;
  }
  @Override
  public void startDocument() throws SAXException
  {
    li = new ArrayList<News>();
  }
  @Override
  public void endDocument() throws SAXException
  {
  }
  @Override
  public void startElement(String namespaceURI, String localName,
                           String qName, Attributes atts) throws SAXException
  {
    Log.i("yao", "开始解析元素");
    if (localName.equals("resp"))
    {
      this.in_resp = true;
      news=new News();
    }
    else if (localName.equals("city"))
    {
      if(this.in_resp)
      {
        this.in_city = true;
      }
      else
      {
        this.in_mainTitle = true;
      }
    }
    else if (localName.equals("wendu"))
    {
      if(this.in_resp)
      {
        this.in_wendu = true;
      }
    }
    else if (localName.equals("fengli"))
    {
      if(this.in_resp)
      {
        this.in_fengli = true;
      }
    }
    else if (localName.equals("shidu"))
    {
      if(this.in_resp)
      {
        this.in_shidu = true;
      }
    }
    else if (localName.equals("pm25"))
    {
      if(this.in_resp)
      {
        this.in_pm25 = true;
      }
    }
    else if (localName.equals("quality"))
    {
      if(this.in_resp)
      {
        this.in_quality = true;
      }
    }
    else if (localName.equals("type"))
    {
      if(this.in_resp)
      {
        this.in_type0 = true;
      }
    }

  }
  @Override
  public void endElement(String namespaceURI, String localName,
                         String qName) throws SAXException
  {

    if (localName.equals("resp"))
    {
      this.in_resp = false;
      li.add(news);
    }
    else if (localName.equals("city"))
    {
      if(this.in_resp)
      {
        news._city=buf.toString().trim();
        buf.setLength(0);
        this.in_city = false;
      }
      else
      {
        title=buf.toString().trim();
        buf.setLength(0);
        this.in_mainTitle = false;
      }
    }
    else if (localName.equals("wendu"))
    {
      if(this.in_resp)
      {
        news._wendu=buf.toString().trim();
        buf.setLength(0);
        this.in_wendu = false;
      }
    }
    else if (localName.equals("fengli"))
    {
      if(in_resp)
      {
        news._fengli=buf.toString().trim();
        buf.setLength(0);
        this.in_fengli = false;
      }
    }
    else if (localName.equals("shidu"))
    {
      if(in_resp)
      {
        news._shidu=buf.toString().trim();
        buf.setLength(0);
        this.in_shidu = false;
      }
    }
    else if (localName.equals("pm25"))
    {
      if(in_resp)
      {
        news._pm25=buf.toString().trim();
        buf.setLength(0);
        this.in_pm25 = false;
      }
    }
    else if (localName.equals("quality"))
    {
      if(in_resp)
      {
        news._quality=buf.toString().trim();
        buf.setLength(0);
        this.in_quality = false;
      }
    }
    else if (localName.equals("type"))
    {
      if(in_resp)
      {
        news._type0.add(buf.toString().trim());
        buf.setLength(0);
        this.in_type0 = false;
        Log.i("type0", news._type0.toString());
      }
    }

  else{
      buf.setLength(0);
    }

    Log.i("yao", "元素解析结束");
  }
  @Override
  public void characters(char ch[], int start, int length)
  {
    if(this.in_resp||this.in_mainTitle)
    {
      buf.append(ch,start,length);
      Log.i("yao", "找到元素赋值");
    }

}}