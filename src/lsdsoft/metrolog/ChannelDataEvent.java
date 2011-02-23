package lsdsoft.metrolog;

import lsdsoft.units.*;


public class ChannelDataEvent {

  private int channel;
  private ChannelValue value;
  public ChannelDataEvent(int chan, ChannelValue value) {
    channel = chan;
    this.value = value;
  }
  public int getChannel() { return channel; }
  public ChannelValue getValue() { return value; }
};

