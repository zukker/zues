#include "LIRDataSource.hpp"

int bcdDecode(int bcd) {
  return (bcd % 16) + ((bcd&0xff) /16 * 10);
}
//---------------------------------------------------------------------------
void LIRDataSource::decodePacket() {
  // first 12 word are 13 bit data
  for(int i = 0; i < LIR_PACKET_SIZE; i++) {
    channelData[i + 2] = packetBuffer[i];
  }
  channelData[0] = bcdDecodeBuffer(inputBuffer + bufferIndex);
  channelData[1] = bcdDecodeBuffer(inputBuffer + bufferIndex + 4);
  bufferIndex += LIR_PACKET_SIZE;
}
//---------------------------------------------------------------------------
int LIRDataSource::bcdDecodeBuffer(unsigned char * buf) {
  int val = 0;
  int deg = 1;
  // decoding first angle
  for(int i = 0; i < 4; i++) {
     val += bcdDecode(buf[i]) * deg;
     deg *= 100;
  }
  // if value negative
  if(val > 9999999) {
    val -= 100000000;
  }
 return val;
}
//---------------------------------------------------------------------------
void LIRDataSource::getPacket() {
unsigned char b;
  if(!isSync) {
    sync();
    isSync = true;
  }
  for(int i = 0; i < LIR_PACKET_SIZE; i++) {
    packetBuffer[i] = port->Read();
  }
  checkPacket();
}
//---------------------------------------------------------------------------
void LIRDataSource::checkPacket() {
//  if(!((packetBuffer[8] == 11) && (packetBuffer[9] == 10))) {
  for(int i = 0; i < 8; i++) {
    unsigned char b = inputBuffer[bufferIndex + i];
    if(((b&0x0f) > 9) || (((b>>4)&0x0f) > 9)) {
      isSync = false;
      break;
    }
  }
  if(!((inputBuffer[bufferIndex + 8] >9 ) &&
       (inputBuffer[bufferIndex + 9] == 10))) {
    isSync = false;
    //MessageBeep(0xFFFFFFFF);
  }
}
//---------------------------------------------------------------------------
bool LIRDataSource::findPacket() {
bool ret = false;
int prevIndex = bufferIndex;
  for(;bufferIndex < IN_BUFFER_SIZE - LIR_PACKET_SIZE; bufferIndex++) {
    if(inputBuffer[bufferIndex] == 11 && inputBuffer[bufferIndex + 1] == 10) {
      bufferIndex += 2;
      if(bufferIndex == prevIndex + LIR_PACKET_SIZE)
        bufferIndex = prevIndex;
      ret = true;
      isSync = true;
      break;
    }
  }
  return ret;
}
//---------------------------------------------------------------------------
void LIRDataSource::sync() {
unsigned int currentChar = 0, prevChar;
  do {
    prevChar = currentChar;
    currentChar = port->Read();
  } while(!(currentChar == 10 && prevChar == 11));
}
//---------------------------------------------------------------------------
void LIRDataSource::getInput() {
  port->Read(inputBuffer, IN_BUFFER_SIZE);
  for(int i = 0; i < LIR_PACKET_SIZE; i++)
    packetBuffer[i] = inputBuffer[i];
  bufferIndex = 0;
}
//---------------------------------------------------------------------------
void LIRDataSource::getByte() {
char buffer[1024];
  port->Read(packetBuffer, 10);
//  packetBuffer[bufferIndex] = port->Read();
//  bufferIndex++;
//  port->clearInput();
//  packetBuffer[bufferIndex] = port->Read();
//  bufferIndex++;
}
//---------------------------------------------------------------------------
LIRDataSource::LIRDataSource() {
  port = 0;
  eventListener = 0;
  packetCount = 0;
  //port->setBuf(IN_BUFFER_SIZE);
  isSync = false;
  bufferIndex = 0;
  //port->addEventListener(*this);
//  setBufferAsData(false);
}
//---------------------------------------------------------------------------
LIRDataSource::LIRDataSource(SerialPort* sPort) {
  port = sPort;
  eventListener = 0;
  packetCount = 0;
  port->setBuf(IN_BUFFER_SIZE);
  isSync = false;
  bufferIndex = 0;
  port->addEventListener(*this);
//  setBufferAsData(false);
}
//---------------------------------------------------------------------------
void LIRDataSource::finish() {
  isSync = false;
}
//---------------------------------------------------------------------------
void LIRDataSource::setPort(SerialPort * aPort) {
  port = aPort;
  port->addEventListener(*this);
}
//---------------------------------------------------------------------------
void LIRDataSource::addEventListener(ChannelDataEventListener* evListener) {
  eventListener = evListener;
  port->addEventListener(*this);
}
//---------------------------------------------------------------------------
void LIRDataSource::removeEventListener() {
  eventListener = 0;
}
//---------------------------------------------------------------------------
void LIRDataSource::setBufferAsData(bool state) {
//  flagBufferAsData  = state;
}
//---------------------------------------------------------------------------
void LIRDataSource::process() {
  //port->waitData();
  while(!port->hasData());
  getPacket();
  if(!isSync) return;
  decodePacket();
  packetCount++;
//  if(channel == 15)
  if(packetCount == 32) {
    packetCount = 0;
    port->clearInput();
  }
  if(eventListener != 0) {

/*
    if(flagBufferAsData) {
      ev = new ChannelDataEvent(0, packetBuffer[0]);
      eventListener->channelEvent(*ev);
      delete ev;
      ev = new ChannelDataEvent(1, packetBuffer[1]);
      eventListener->channelEvent(*ev);
      delete ev;
      ev = new ChannelDataEvent(2, packetBuffer[2]);
      eventListener->channelEvent(*ev);
      delete ev;
    }
  */
    //ev = new ChannelDataEvent(channel, value);
    for(int i = 0; i < LIRCHANNELS; i++) {
      ChannelDataEvent ev(i, channelData[i]);
      eventListener->channelEvent(ev);
    }
    //delete ev;
  }

}
//---------------------------------------------------------------------------
void LIRDataSource::processPacket() {
  checkPacket();
  if(!isSync) {
    port->clearInput();
    //bufferIndex = 0;
    MessageBeep(0xFFFFFFFF);
    //return;
    }
  decodePacket();
  if(eventListener != 0) {
    for(int i = 0; i < 2; i++) {
      ChannelDataEvent ev(i, channelData[i]);
      eventListener->channelEvent(ev);
    }
    //delete ev;
  }

}
//---------------------------------------------------------------------------
void LIRDataSource::serialEvent(SerialPortEvent& ev) {
int e = ev.getEventType();
  switch(e) {
    case SerialPortEvent::DATA_AVAILABLE:
      //if(!isSync) sync();
      getInput();
      while(findPacket()) {
        processPacket();
      }
      break;
  }

}
