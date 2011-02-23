#include "IMDataSource.hpp"

IMDataSource::IMDataSource() {
  dataEventListener = 0;
  tool = 0;
  id = "imempty";
}

IMDataSource::~IMDataSource() {
}

void IMDataSource::addDataEventListener(IMDataEventListener* evListener) {
  dataEventListener = evListener;
}

void IMDataSource::removeDataEventListener() {
  dataEventListener = 0;
}

void IMDataSource::getLastAngles(InklinometerAngles &ang) {
  ang = angles;
}


