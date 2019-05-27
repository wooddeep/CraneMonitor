package com.wooddeep.crane.ebus;

public class UserEvent {
    private String name;
    private String password;
    private String viewId;
    private Object view;

    public UserEvent() {
    }

    public UserEvent(String name, String password, String viewId, Object view) {
        this.name = name;
        this.password = password;
        this.view = view;
        this.viewId = viewId;
    }

    public String getViewId() {
        return viewId;
    }

    public void setViewId(String viewId) {
        this.viewId = viewId;
    }

    public Object getView() {
        return view;
    }

    public void setView(Object view) {
        this.view = view;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "UserEvent{" +
                "name='" + name + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
