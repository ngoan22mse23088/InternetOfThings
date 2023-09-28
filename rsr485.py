print("Hello AIoT")
import sys
from Adafruit_IO import MQTTClient
from Physical.temperature import *
from Physical.moisture import *
import view

from scheduler import *

import argparse


AIO_FEED_ID = ["button1", "button2", "sensor1","sensor2","sensor3","equation"]
AIO_USERNAME = "kiin11"
AIO_KEY = ""
global_equation = ""


try:
    # ls /dev/tty* lenh tim cong com
    ser = serial.Serial(port="/dev/ttyUSB0", baudrate=9600)
except:
    print("Can not open the port")

# camera_detect_model = CameraDetector()

def connected(client):
    print("Ket noi thanh cong ...")
    for topic in AIO_FEED_ID:
        client.subscribe(topic)

def subscribe(client , userdata , mid , granted_qos):
    print("Subscribe thanh cong ...")

def disconnected(client):
    print("Ngat ket noi ...")
    sys.exit (1)

def message(client , feed_id , payload):
    print("Nhan du lieu: " + payload + " , feed id :" + feed_id)
    if feed_id == "button1":
        setDevice1(ser,payload == "1")
    elif feed_id == "button2":
        setDevice2(ser,payload == "1")
    if(feed_id == "equation"):
        global_equation = payload
        print(global_equation)


counter = 10
counter_ai = 5
sensor_type = 0
ai_result = ""
previous_result = ""
scheduler = Scheduler()
scheduler.SCH_Init()


parser = argparse.ArgumentParser(description='Python script with user and password arguments')
parser.add_argument('-key', required=True, help='password')
args = parser.parse_args()

# Access the arguments
AIO_KEY = args.key

print(AIO_KEY)
client = MQTTClient(AIO_USERNAME, AIO_KEY)
client.on_connect = connected
client.on_disconnect = disconnected
client.on_message = message
client.on_subscribe = subscribe
client.connect()
client.loop_background()

temp = Temperature()
temp.client = client
moisture = Moisture()
moisture.client = client

scheduler.SCH_Add_Task(temp.readTemperature,3000,5000)
scheduler.SCH_Add_Task(moisture.readMoisture,3000,5000)


while True:
    scheduler.SCH_Update()
    scheduler.SCH_Dispatch_Tasks()
    view.window.update()

    time.sleep(0.1)

    pass