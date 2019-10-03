package sk.gjar.game12.client;

import com.google.gwt.user.client.Window;

import sk.gjar.game12.ActionListener;

public class HtmlActionListener implements ActionListener {

    @Override
    public void doAction() {
        Window.alert("OK?");
    }
}
