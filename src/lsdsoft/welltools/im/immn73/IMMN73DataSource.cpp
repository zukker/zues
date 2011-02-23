#include <math.h>
#include "IMMN73DataSource.hpp"


IMMN73DataSource::IMMN73DataSource() {
//  zeroM_X = 2174;
//  zeroM_Y = 2337;
//  zeroA_X = 2048;
//  zeroA_Y = 2048;
//  amplitudeM_X = 1460;
//  amplitudeM_Y = -1480;
//  amplitudeA_X = -2000;
//  amplitudeA_Y = -2000;
  id = "immn73";
  addEventListener(this);
  mode = 1;
  //IMMN73DataFilter::connect();
}

//IMMN73DataSource::~IMMN73DataSource() {
//}
void IMMN73DataSource::assignWellTool() {
  corrTable.setNumber(tool->number);
  corrTable.load();
}

void IMMN73DataSource::connect() {
  IMMN73DataFilter::connect();
  ask();
}
void IMMN73DataSource::calcAngles() {
  corrTable.compute(channelValue, angles);
  if(mode == 1) {
    corrTable.correct(angles);
  }
  //calcZenit();
  //calcAzimut();
}

void IMMN73DataSource::exec(char* command) {
  if(strcmp(command, "saveCalib") == 0)
    corrTable.save();
}


void IMMN73DataSource::sendAngleEvent() {
  if(dataEventListener) {
    IMDataEvent ev(&angles);
    dataEventListener->dataEvent(ev);
  }
}

void IMMN73DataSource::channelEvent(ChannelDataEvent& ev) {
int chan = ev.getChannel();
  if(chan >= IMMN73_CHANNEL_COUNT) return;
  channelValue[chan] = ev.getValue();
  if(chan == 11) { // get last value in row - clac and send
    calcAngles();
    sendAngleEvent();
  }
}

void IMMN73DataSource::getAngles(InklinometerAngles &ang) {
  ask();
  ang = angles;
}

SplineInterpolation* IMMN73DataSource::getZenitSpline() {
  return corrTable.getZenitSpline();
}
SplineInterpolation* IMMN73DataSource::getAzimutSpline(unsigned int zenit) {
  return corrTable.getAzimutSpline(zenit);
}

double IMMN73DataSource::getZenit(unsigned int zenit) {
  return corrTable.getZenit(zenit);
}

