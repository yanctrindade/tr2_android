// Programa: Web Server com modulo ESP8266
 
#include <SoftwareSerial.h>

 
//RX pino 11, TX pino 12
SoftwareSerial esp8266(11, 12);

const byte ledPin = 13;
const byte inputPin = 2;
String target_ip="192.168.43.20";
String port="4444";
volatile boolean value;
String sendStr="foto";
 
#define DEBUG true
 
void setup()
{
  digitalWrite(ledPin, LOW);
  Serial.begin(9600);
  esp8266.begin(9600);
  pinMode(ledPin, OUTPUT);      // declare LED as output
  pinMode(inputPin, INPUT_PULLUP);     // declare sensor as input
 
  sendData("AT+RST\r\n", 2000, DEBUG);
  // Conecta a rede wireless
  sendData("AT+CWJAP=\"Weebo-Phone\",\"weebophone\"\r\n", 10000, DEBUG);
  sendData("AT+CWMODE=1\r\n", 1000, DEBUG);
  // Mostra o endereco IP
  sendData("AT+CIFSR\r\n", 1000, DEBUG);
  // Configura para apenas 1 conexao
  sendData("AT+CIPMUX=0\r\n", 1000, DEBUG);

  attachInterrupt(digitalPinToInterrupt(inputPin), sendUpdate, RISING);
  value=false;
  digitalWrite(ledPin, HIGH);  // turn LED ON
}


 
void loop()
{  
  
    if(value==true) {
      //Setup connection  
      String at_connection_start="";
      at_connection_start+= "AT+CIPSTART=\"TCP\",\"";
      at_connection_start+= target_ip;
      at_connection_start+="\",";
      at_connection_start+=port;
      at_connection_start+="\r\n";
      sendData(at_connection_start,3000,DEBUG);
    
      //Send data
      String cipsend="";
      cipsend+="AT+CIPSEND=";
      cipsend+=sendStr.length();
      cipsend+="\r\n";
      sendData(cipsend, 1000, DEBUG); // rst
      sendData(sendStr,1000,DEBUG);
      sendData("AT+CIPCLOSE\r\n", 1000, DEBUG);

      
      value=false;

    }


}
 
void sendUpdate() {
  value=true; //essa rotina precisa ser curta (varias limitacoes)    
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


