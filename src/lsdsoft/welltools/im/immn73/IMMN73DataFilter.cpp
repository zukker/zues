#include <math.h>
#include "IMMN73DataFilter.hpp"

#define IN_BUFFER_SIZE 4

static bool allowedParams[] = { false, false, true, false, true, true, false, false };

void IMMN73DataFilter::connect() {
  port.openPort(portID);
  port.setSerialPortParams(2400,
      SerialPort::DATABITS_8,
      SerialPort::STOPBITS_1,
      SerialPort::PARITY_ODD);
  port.setInBufferSize(IN_BUFFER_SIZE);
  port.setOutBufferSize(1);
  port.setDTR(true);
  port.setRTS(false);
  port.monitor(true);
  connected = true;
}

void IMMN73DataFilter::ask(unsigned int parameter, bool singleRequest) {
char buffer[3];
  if(parameter > 7) {
    nextParam = -1;
    return;
  }
  //if(parameter == 2) Sleep(1000);
  buffer[0] = 'S';
  buffer[1] = '0' + parameter;
  buffer[2] = 13;
  askedParam = parameter;
  startPacket = true;
  port.clearInput();
  port.Write(buffer, 3);
  arrived = false;
  if(singleRequest)
    nextParam = -1;
  else
  switch(parameter) {
    case 2: nextParam = 4; break;
    case 4: nextParam = 5; break;
    case 5: nextParam = 2; break;
  }
  //nextParam = singleRequest?-1:parameter + 1;
}

void IMMN73DataFilter::reset() {
char * buffer = "R\x0d";
  port.clearInput();
  port.Write(buffer, 2);
}

void IMMN73DataFilter::ask() {
  // запрос нулей датчиков
  ask(2, false);
//  waitForArrived();
  // запрос значения коордынаты X
//  ask(4);
//  waitForArrived();
  // запрос значения координаты Y

//  ask(5);
//  waitForArrived();
}

int IMMN73DataFilter::hexCharToInt(char hex) {
 if(hex >= '0' && hex <= '9') return (hex - '0');
 if(hex >= 'A' && hex <= 'F') return (hex - 'A' + 10);
 return -1;
}

int IMMN73DataFilter::decodeChunk(unsigned char * buffer) {
// arrived string looks like this: "F7F8"
// but signful values only las three
  int mul = 1;
  int ret = 0;
  for(int i = 0; i < 3; i++) {
    int value = hexCharToInt(buffer[3 - i]);

    ret += value * mul;
    mul *= 16;
  }
  return ret;
}

void IMMN73DataFilter::waitForArrived() {
  while(!arrived)Sleep(50);
}

IMMN73DataFilter::IMMN73DataFilter() : fifo(256), ChannelDataSource(16){
//  port = 0;
  //eventListener = 0;
  //dataEventListener = 0;
  packetCount = 0;
  //isSync = false;
  inputBuffer = new unsigned char[IN_BUFFER_SIZE];
  bufferIndex = 0;
  port.addEventListener(*this);
  //id = "immn73";
  //sem = CreateSemaphore(NULL, 0, 1, "sem\ion1");
}

//IONDataSource::IONDataSource(SerialPort* sPort) {
//  IONDataSource::IONDataSource();
//  setPort(sPort);
//}

IMMN73DataFilter::~IMMN73DataFilter() {
  port.closePort();
  delete[] inputBuffer;
}

//---------------------------------------------------------------------------
void IMMN73DataFilter::getInput() {
char buf[IN_BUFFER_SIZE];
  if(startPacket) {
    fifo.clear();
    startPacket = false;
  }
  //port->Read(inputBuffer, IN_BUFFER_SIZE);
  port.Read(buf, IN_BUFFER_SIZE);
  for(int i = 0; i < IN_BUFFER_SIZE; i++)
    if(hexCharToInt(buf[i]) >= 0)
      fifo.push(buf[i]);
    //fifo.push(0);
//  bufferIndex = 0;
}
//---------------------------------------------------------------------------
bool IMMN73DataFilter::findPacket() {
  if(fifo.count() < IMMN73_PACKET_SIZE) return false;
  for(int i = 0; i < IMMN73_PACKET_SIZE; i++) {
    packetBuffer[i] = fifo.pop();
  }
  return true;
}
//---------------------------------------------------------------------------
void IMMN73DataFilter::processPacket() {
unsigned int * packet = (unsigned int *)&packetBuffer;
  for(int i = 0; i < 2; i++) {
    int value = decodeChunk(packetBuffer + i * 4);
    ChannelDataEvent ev(askedParam * 2 + i, value);
    sendEvent(ev);
  }
  arrived = true;
  if(nextParam == 2) {
    //reset();
    //Sleep(500);
  }

  if(nextParam >=0) {
    ask(nextParam, false);
    Sleep(400);
    reset();
  }

}
//---------------------------------------------------------------------------
void IMMN73DataFilter::serialEvent(SerialPortEvent& ev) {
//  if((eventListener == NULL) && (dataEventListener == NULL)) {
//    port->clearInput();
//    return;
//  }
int e = ev.getEventType();
  switch(e) {
    case SerialPortEvent::DATA_AVAILABLE:
      getInput();
//      findPacket();
//      processPacket();
      while(findPacket()) {
        processPacket();
      }
      //port->clearInput();
      break;
  }
}


