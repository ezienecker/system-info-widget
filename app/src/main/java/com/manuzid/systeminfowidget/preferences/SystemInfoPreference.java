package com.manuzid.systeminfowidget.preferences;

public class SystemInfoPreference
{
    public String title;
    public String summary;
    public boolean header;

    public SystemInfoPreference(final String title, final String summary, final boolean header)
    {
        this.title = title;
        this.summary = summary;
        this.header = header;
    }
}