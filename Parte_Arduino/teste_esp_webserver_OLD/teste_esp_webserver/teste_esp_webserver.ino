// Programa: Web Server com modulo ESP8266
 
#include <SoftwareSerial.h>
#include "TimerOne.h"
 
//RX pino 8, TX pino 9
SoftwareSerial esp8266(11, 12);

const byte ledPin = 13;
const byte inputPin = 2;
int val;

 
#define DEBUG true
 
void setup()
{
  Serial.begin(9600);
  esp8266.begin(9600);
  pinMode(ledPin, OUTPUT);      // declare LED as output
  pinMode(inputPin, INPUT_PULLUP);     // declare sensor as input
 
  
  sendData("AT+RST\r\n", 2000, DEBUG); // rst
  // Conecta a rede wireless
  sendData("AT+CWJAP=\"vg\",\"12345678\"\r\n", 10000, DEBUG);
  sendData("AT+CWMODE=1\r\n", 1000, DEBUG);
  // Mostra o endereco IP
  sendData("AT+CIFSR\r\n", 1000, DEBUG);
  // Configura para multiplas conexoes
  sendData("AT+CIPMUX=1\r\n", 1000, DEBUG);
  // Inicia o web server na porta 80
  sendData("AT+CIPSERVER=1,80\r\n", 1000, DEBUG);

  attachInterrupt(digitalPinToInterrupt(inputPin), changeValue, CHANGE);
}
 
void loop()
{  

  
  // Verifica se o ESP8266 esta enviando dados
  if (esp8266.available())
  {
    if (esp8266.find("+IPD,"))
    {
      delay(300);
      int connectionId = esp8266.read() - 48;
 
      String webpage = "<head><meta http-equiv=""refresh"" content=""3"">";
      webpage += "</head><h2>Porta";
      webpage += "Digital 2: ";
      webpage += val;
      webpage += "</h2>";
 
      String cipSend = "AT+CIPSEND=";
      cipSend += connectionId;
      cipSend += ",";
      cipSend += webpage.length();
      cipSend += "\r\n";
 
      sendData(cipSend, 3000, DEBUG);
      sendData(webpage, 3000, DEBUG);
 
      String closeCommand = "AT+CIPCLOSE=";
      closeCommand += connectionId; // append connection id
      closeCommand += "\r\n";
 
      sendData(closeCommand, 3000, DEBUG);
    }
  }
}
 
String sendData(String command, const int timeout, boolean debug)
{
  // Envio dos comandos AT para o modulo
  String response = "";
  esp8266.print(command);
  long int time = millis();
  while ( (time + timeout) > millis())
  {
    while (esp8266.available())
    {
      // The esp has data so display its output to the serial window
      char c = esp8266.read(); // read the next character.
      response += c;
    }
  }
  if (debug)
  {
    Serial.print(response);
  }
  return response;
}


int timer_count;

void changeValue() {
  int temp=digitalRead(inputPin);
  if (temp == HIGH) {            // check if the input is HIGH
    val=temp;
    digitalWrite(ledPin, HIGH);  // turn LED ON
    Timer1.initialize(8000000);
    Timer1.pwm(9, 512);
    Timer1.attachInterrupt(endHoldValue);
    detachInterrupt(digitalPinToInterrupt(inputPin));
    timer_count=0;
  } 
}


void endHoldValue(){
  if(timer_count==0||timer_count==1){
    //Serial.println("Entrou no 0");
    timer_count++;  
    return;
  }
    //Serial.println("Entrou no 1");
    val=LOW;
    digitalWrite(ledPin, LOW);
    attachInterrupt(digitalPinToInterrupt(inputPin), changeValue, CHANGE);
    Timer1.restart();
}

