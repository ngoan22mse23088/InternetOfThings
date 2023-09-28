import argparse
import serial.tools.list_ports
import Adafruit_IO
import time
import sys
from Adafruit_IO import MQTTClient

print("RS485")

try:
     ser = serial.Serial(port="/dev/ttyUSB0", baudrate=9600)
except:
     print("Can not open the port")

AIO_1 = 'cambien1'
AIO_2 = 'cambien2'
AIO_3 = 'cambien3'
AIO_FEED_ID_3 = "equation"
# ADAFRUIT_IO_KEY = 'aio_nefi68KtXQH8ThPaLrwM3xo77B7c'
ADAFRUIT_IO_USERNAME = 'lcngoan0607'

relay1_ON  = [0, 6, 0, 0, 0, 255, 200, 91]
relay1_OFF = [0, 6, 0, 0, 0, 0, 136, 27]

relay2_ON  = [15, 6, 0, 0, 0, 255, 200, 164]
relay2_OFF = [15, 6, 0, 0, 0, 0, 136, 228]

device1ON = False
device2ON = False

def setDevice1(state):
    if state == True:
        ser.write(relay1_ON)
        device1ON = True
    else:
        ser.write(relay1_OFF)
        device1ON = False

def setDevice2(state):
    if state == True:
        ser.write(relay2_ON)
        device2ON = True
    else:
        ser.write(relay2_OFF)
        device2ON = False

def serial_read_data(ser):
    bytesToRead = ser.inWaiting()
    if bytesToRead > 0:
        out = ser.read(bytesToRead)
        data_array = [b for b in out]
        print(data_array)
        if len(data_array) >= 7:
            array_size = len(data_array)
            value = data_array[array_size - 4] * 256 + data_array[array_size - 3]
            return value
        else:
            return -1
    return 0

soil_temperature = [1,3,0,6,0,1,100,11]
def readTemperature():
    serial_read_data(ser)
    ser.write(soil_temperature)
    time.sleep(1)
    return serial_read_data(ser)


soil_moisture = [1,3,0,7,0,1,53,203]
def readMoisture():
    serial_read_data(ser)
    ser.write(soil_moisture)
    time.sleep(1)
    return serial_read_data(ser)

# Define callback functions which will be called when certain events happen.
def connected(client):
    print('Connected to Adafruit IO!  Listening for {0} changes...'.format(AIO_1))
    print('Connected to Adafruit IO!  Listening for {0} changes...'.format(AIO_2))
    print('Connected to Adafruit IO!  Listening for {0} changes...'.format(AIO_3))
    # Subscribe to changes on a feed named DemoFeed.
    client.subscribe(AIO_1)
    client.subscribe(AIO_2)
    client.subscribe(AIO_3)

def subscribe(client, userdata, mid, granted_qos):
    # This method is called when the client subscribes to a new feed.
    print('Subscribed to {0} with QoS {1}'.format(AIO_1, granted_qos[0]))
    print('Subscribed to {0} with QoS {1}'.format(AIO_2, granted_qos[1]))
    print('Subscribed to {0} with QoS {1}'.format(AIO_3, granted_qos[2]))

def disconnected(client):
    # Disconnected function will be called when the client disconnects.
    print('Disconnected from Adafruit IO!')
    sys.exit(1)

def message(client, feed_id, payload):
    print('Feed {0} received new value: {1}'.format(feed_id, payload))
    print('Received: ' + payload)
    if feed_id == AIO_1:
        x1 = int(payload)
        print(x1)
        setDevice1("1" == payload)
    if feed_id == AIO_2:
        x2 = int(payload)
        print(x2)
        setDevice1("1" == payload)
    if feed_id == AIO_3:
        x3 = int(payload)
        print(x3)
        setDevice1("1" == payload)
    else:
        setDevice2("1" == payload)

parser = argparse.ArgumentParser(description='Python script with user and password arguments')
parser.add_argument('-key', required=True, help='password')
args = parser.parse_args()
ADAFRUIT_IO_KEY = args.key

# Create an MQTT client instance.
client = MQTTClient(ADAFRUIT_IO_USERNAME, ADAFRUIT_IO_KEY)

# Setup the callback functions defined above.
client.on_connect    = connected
client.on_disconnect = disconnected
client.on_message    = message
client.on_subscribe  = subscribe

# Connect to the Adafruit IO server.
client.connect()

while True:
    if device1ON:
        value = readTemperature()
        print('Temperature value: ' + str(value))
        client.publish('cambien1', value)
    else: 
        print('Temperature value: ' + str(value))

    if device2ON:
          value = readMoisture()
          print('Moisture value: ' + str(value))
          client.publish('cambien2', value)
    else: 
        print('Moisture value: ' + str(value))

    time.sleep(3)
    pass