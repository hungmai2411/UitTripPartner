package com.uittrippartner.hotel;

public class Sort {
    private String name;
    private boolean isChecked;
    private int index;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public Sort(String name, boolean isChecked) {
        this.name = name;
        this.isChecked = isChecked;
    }
}
