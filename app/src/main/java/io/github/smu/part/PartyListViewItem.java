package io.github.smu.part;

/**
 * Created by kug00 on 2017-10-12.
 */

public class PartyListViewItem {
    private String iconDrawable ;
    private String titleStr ;
    private String addrStr ;
    private String Start ;
    private String End ;

    public void setIcon(String icon) {
        iconDrawable = icon ;
    }
    public void setTitle(String title) {
        titleStr = title ;
    }
    public void setDesc(String addr) {
        addrStr = addr ;
    }
    public void setStart(String start) {
        Start = start ;
    }
    public void setEnd(String end) {
        End = end ;
    }

    public String getIcon() {
        return this.iconDrawable ;
    }
    public String getTitle() {
        return this.titleStr ;
    }
    public String getDesc() {
        return this.addrStr ;
    }
    public String getStart() {
        return this.Start ;
    }
    public String getEnd() {
        return this.End ;
    }
}
