#include <math.h>
#include "IMMN73CorrectionTable.hpp"
#include "Properties.hpp"
#include "TextTemplate.hpp"

extern Properties props;

IMMN73CorrectionTable::IMMN73CorrectionTable() {
  setNumber("0");
  setDefaults();
}

IMMN73CorrectionTable::IMMN73CorrectionTable(AnsiString num) {
  setNumber(num);
  setDefaults();
}

IMMN73CorrectionTable::~IMMN73CorrectionTable() {
}

void IMMN73CorrectionTable::setDefaults() {
  loaded = false;
}

void IMMN73CorrectionTable::load(void * buf, int size) {
char * input = (char*)buf;
char * line;
  // получение данных для БСКТ
  line = getLine(input, 3);
  computer.bSensor.zeroX = atol(line + 11);
  computer.bSensor.zeroY = atol(line + 20);
  line = getLine(input, 4);
  computer.bSensor.amplitudeX = atol(line + 11);
  computer.bSensor.amplitudeY = atol(line + 18);
  // данные для магнитометров
  line = getLine(input, 6);
  computer.mSensor.zeroX = atol(line + 11);
  computer.mSensor.zeroY = atol(line + 17);
  line = getLine(input, 7);
  computer.mSensor.amplitudeX = atol(line + 11);
  computer.mSensor.amplitudeY = atol(line + 17);
  // установочная погрешность по зениту
  line = getLine(input, 8);
  zenitOffset = atof(line + 64);
  // поправки по зениту
  zenitTable.spl.setDimention(11); // жестко заданы 11 значений по азимуту
  line = getLine(input, 9); // полученные углы по прибору
  for(int i = 0; i < 11; i++) {
    zenitTable.spl[i].x = atof(line + i * 8);
  }
  line = getLine(input, 10); // значение поправки
  for(int i = 0; i < 11; i++) {
    zenitTable.spl[i].y = atof(line + i * 8);
  }
  zenitTable.spl.preCalc();
  // установочная погрешность по азимуту
  line = getLine(input, 11);
  azimutOffset = atof(line + 64);
  // поправки по азимуту
  clearAzimutTable();
  for(int table = 0; table < 4; table++) {
    line = getLine(input, table * 3 + 12);
    AzimutZenitPoint * point = new AzimutZenitPoint;
    point->repZenit = atof(line + 26);
    point->spl.setDimention(12);
    line = getLine(input, table * 3 + 13); // полученные углы по прибору
    for(int i = 0; i < 12; i++) {
      double value = atof(line + i * 8);
      if(i < 2 && value > 300) value -= 360;
      point->spl[i].x = value;
    }
    line = getLine(input, table * 3 + 14); // значение поправки
    for(int i = 0; i < 12; i++) {
      point->spl[i].y = atof(line + i * 8);
    }
    azimutTable.zenPoints.addItem(point);
  }

}

// 0x0d0a - end of line
char* IMMN73CorrectionTable::getLine(char * buf, int lineNumber) {
int currentLine = 0;
int pos = 0;
char * line = buf;
  while(currentLine < lineNumber) {
    if(line[pos] == 0x0d) {
      if(line[pos + 1] == 0x0a) { // found end of line
        line = line + pos + 2; // next line
      } else {
        line = line + pos + 1;
      }
        pos = 0;
        currentLine ++;
    } else if(line[pos] == 0x00) break; // end of buf
    else pos++;
  }
  return line;
}

void IMMN73CorrectionTable::setNumber(AnsiString num) {
  number = num;
}

void IMMN73CorrectionTable::load() {
  AnsiString file = props.getProperty("immn73.calib");
  file = IncludeTrailingBackslash(file);
  AnsiString name;
  name.sprintf("IM73_%s.CLB", number.c_str());
  file += name;
  load(file);
}

void IMMN73CorrectionTable::load(AnsiString fileName) {
TFileStream * file = new TFileStream(fileName, fmOpenRead);
  int size = file->Size;
char * buf = new char[size + 1];
  if(!buf) throw Exception("Not enough memory");
  file->Read(buf, size);
  buf[size] = 0;
  load(buf, size);
  delete buf;
  delete file;
}



void IMMN73CorrectionTable::compute(int * sensorValues, InklinometerAngles& ang) {
  computer.compute(sensorValues, ang);
}

void IMMN73CorrectionTable::correct(InklinometerAngles& angles) {
double value = angles.zenit.getValue();
  value += zenitTable.spl.calc(value) + zenitOffset;
  angles.zenit = value;
  // correct azimut
  // find nearest zenit
  Angle delta = 360.0;
  int index = 0;
  unsigned int count = azimutTable.zenPoints.getCount();
  if(count == 0) return;
  for(unsigned int i = 0; i < azimutTable.zenPoints.getCount(); i++) {
    Angle zen = ((AzimutZenitPoint*)azimutTable.zenPoints[i])->repZenit;
    if((zen - angles.zenit).abs() < delta) {
      index = i;
      delta = (zen - angles.zenit).abs();
    }
  }
  value = angles.azimut.getValue();
  double corr = ((AzimutZenitPoint*)azimutTable.zenPoints[index])->spl.calc(value);
  value += corr + azimutOffset;
  angles.azimut = value;

}



void IMMN73CorrectionTable::clearAzimutTable() {
  // free allocated space
  // TODO this
  azimutTable.zenPoints.clear();
}

void IMMN73CorrectionTable::save() {
TextTemplate tpl;
Properties prop1;
  TMemoryStream * out = new TMemoryStream;
  AnsiString value;
  AnsiString name;
  AnsiString f, f1;
  AnsiString tplroot = props.getProperty("path.templates");
  tplroot = IncludeTrailingBackslash(tplroot);
  tpl.setProperties(&prop1);
// rotate protocol

  name = tplroot;
  name += "immn73clb.txt";
  tpl.Load(name);
  // set some values
  prop1.setProperty("tool.number", number);
  prop1.setProperty("date.calib", props.getProperty("date.calib"));
  prop1.setProperty("bSensor.zeroX", computer.bSensor.zeroX);
  prop1.setProperty("bSensor.zeroY", computer.bSensor.zeroY);
  prop1.setProperty("bSensor.ampX", computer.bSensor.amplitudeX);
  prop1.setProperty("bSensor.ampY", computer.bSensor.amplitudeY);
  prop1.setProperty("mSensor.zeroX", computer.mSensor.zeroX);
  prop1.setProperty("mSensor.zeroY", computer.mSensor.zeroY);
  prop1.setProperty("mSensor.ampX", computer.mSensor.amplitudeX);
  prop1.setProperty("mSensor.ampY", computer.mSensor.amplitudeY);
// gen zenit string
  f ="";
  for(int i = 0; i < zenitTable.spl.getDimension(); i++) {
    value.sprintf("%8.2f", zenitTable.spl[i].x);
    f += value;
    value.sprintf("%8.2f", zenitTable.spl[i].y);
    f1 += value;
  }
  prop1.setProperty("zenit.points", f);
  prop1.setProperty("zenit.corr", f1);
// gen azimt strings
  for(int i = 0; i < azimutTable.zenPoints.getCount(); i++) {
    AzimutZenitPoint* point = (AzimutZenitPoint*)azimutTable.zenPoints[i];
    f.sprintf("azenit.%d", i);
    value.sprintf("%8.2f", point->repZenit.getValue());
    prop1.setProperty(f, value);
    f = "";
    f1 = "";
    for(int j = 0; j < point->spl.getDimension(); j++) {
      value.sprintf("%8.2f", point->spl[j].x);
      f += value;
      value.sprintf("%8.2f", point->spl[j].y);
      f1 += value;
    }
    value.sprintf("azimut.points.%d", i);
    prop1.setProperty(value, f);
    value.sprintf("azimut.corr.%d", i);
    prop1.setProperty(value, f1);
  }
// store to file
  tpl.doFilter();
  tpl.Save(out);
  AnsiString file = props.getProperty("immn73.calib");
  file = IncludeTrailingBackslash(file);
  name.sprintf("IM73_%s_.CLB", number.c_str());
  file += name;
  out->SaveToFile(file);
  delete out;
}

SplineInterpolation* IMMN73CorrectionTable::getZenitSpline() {
  return &zenitTable.spl;
}

SplineInterpolation* IMMN73CorrectionTable::getAzimutSpline(unsigned int zenit) {
  if(zenit >= azimutTable.zenPoints.getCount()) return 0;
  return &((AzimutZenitPoint*)azimutTable.zenPoints[zenit])->spl;
}

double IMMN73CorrectionTable::getZenit(unsigned int zenit) {
  if(zenit >= azimutTable.zenPoints.getCount()) return 0;
  return ((AzimutZenitPoint*)azimutTable.zenPoints[zenit])->repZenit.getValue();
}
