package com.manuzid.systeminfowidget.preferences;

public class Preferences
{
    public String title;
    public String summary;
    public boolean header;

    public Preferences(final String title, final String summary, final boolean header)
    {
        this.title = title;
        this.summary = summary;
        this.header = header;
    }
}