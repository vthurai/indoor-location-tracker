#!/usr/bin/python

import blescan
import sys
import os
import math
import bluetooth._bluetooth as bluez
import argparse
import json
import datetime
import ConfigParser

from google.cloud import pubsub_v1


publisher = pubsub_v1.PublisherClient()

#Arguments parser
parser = argparse.ArgumentParser(description='Reliable Bluetooth LE (iBeacon) scanner')
parser.add_argument('-i', type=int, default=0, help='Bluetooth adapter ID')
parser.add_argument('-t', type=int, default=10, help='Seconds between two survey. A small value can cause some beacon to be missed')
parser.add_argument('-n', type=float, default=1.0, help='Path loss exponent')
parser.add_argument('-pdz', type=int, default=1, help='TxPower at taring position')
parser.add_argument('-dz', type=int, default=1, help='Distance of taring position (m) ')

args = parser.parse_args()

# Console colors
W = '\033[0m'  # white (normal)
R = '\033[31m'  # red
G = '\033[32m'  # green
O = '\033[33m'  # orange
B = '\033[34m'  # blue
P = '\033[35m'  # purple
C = '\033[36m'  # cyan
GR = '\033[37m'  # gray

#knownUUID = ['f7826da64fa24e988024bc5b71e089ff','f7826da64fa24e988024bc5b71e0abcd']
#rpi = 'rpiA'
#topic = 
config = ConfigParser.ConfigParser()
config.readfp(open(r'properties.txt'))
rpi = config.get('Gateway Properties', 'rpi')
topic = config.get('Gateway Properties', 'topic')
knownUUID = config.get('Gateway Properties', 'knownUUID').split(",")

def printLogo():
    logo = """
    _ ____                                 _____                                 
   (_) __ )___  ____ __________  ____     / ___/_________ _____  ____  ___  _____
  / / __  / _ \/ __ `/ ___/ __ \/ __ \    \__ \/ ___/ __ `/ __ \/ __ \/ _ \/ ___/
 / / /_/ /  __/ /_/ / /__/ /_/ / / / /   ___/ / /__/ /_/ / / / / / / /  __/ /    
/_/_____/\___/\__,_/\___/\____/_/ /_/   /____/\___/\__,_/_/ /_/_/ /_/\___/_/     
                                                                                 
"""
    print logo

def printInfo(str):
    print G + "[INFO]" + str

def printError(str):
    print R + "[ERROR]" + str

def gracefulExit():
    print
    print R + "Quitting... ByeBye!"
    print W
    sys.exit(0)

def badExit():
    print
    print R + "Somethings went wrong...! Quitting!"
    print W
    sys.exit(1)

def getDistance(rssi):

    #dist = args.dz * math.exp( (args.pdz - txP)/( 10 * args.n ) )
    #print((-69.0 - rssi)/(10*2))
    dist = math.pow(10, (-69.0 - rssi)/(10*3))
    return dist



#Orange logo
print O
printLogo()

printInfo("Starting BLE thread on device ID: " + str(args.i) + "...")
try:
    sock = bluez.hci_open_dev(int(args.i))

except:
    printError("Error accessing bluetooth device!")
    badExit()


printInfo("Setting up BLE device ...")
try:
    blescan.hci_le_set_scan_parameters(sock)

except:
    printError("Error setting up bluetooth device!")
    badExit()

printInfo("Start scanning...")
try:
    blescan.hci_enable_le_scan(sock)
except:
    printError("Error scanning! Maybe not root?")
    badExit()


while True:
    try:
        #Try to retrive the full scan result
        returnedList = blescan.parse_events(sock, args.t)

        purgedList = []
        #seen = set()
        #purgedList = []
        #We search and delete all the beacon from the same device
        #Looping througth the returnedList, every time we found a MAC adr
        #that is not present in 'seen', we add it to 'seen' and 'purgedList'
        for d in returnedList:
            #t = d['MAC']
            #if t not in seen and
            if d['UUID'] in knownUUID:
            #if t == 'C6:F1:21:2E:33:03':# and d['UUID'] in knownUUID:
                #seen.add(t)
                purgedList.append(d)

        #os.system('clear')

        #Orange logo
        #print O
        #printLogo()
        #Formated output for result
        #print G + "{0:<20s}{1:<10s}{2:<10s}{3:<10s}{4:<10s}{5:<13s}{6:<10s}".format("MAC","MAJOR","MINOR","RSSI","TxPOWER","DISTANCE(m)","UUID")
        message = {}
        message['timecollected'] = datetime.datetime.now().strftime('%Y-%m-%d %H:%M:%S')
        message['rpi'] = rpi
        for beacon in purgedList:
            #print("{0:<20s}{1:<10}{2:<10}{3:<10d}{4:<10d}{5:<13.2f}{6:<10}".format(beacon['MAC'],beacon['MAJOR'],beacon['MINOR'],beacon['RSSI'][0],beacon['TxPOWER'][0],getDistance(beacon['RSSI'][0]),beacon['UUID']))

            message['uuid'] = beacon['UUID']
            message['rssi'] = beacon['RSSI'][0]

            print(message)
            publisher.publish(topic, str.encode(json.dumps(message)))

        print P+"Scanning.... "
    except KeyboardInterrupt: #Did the user press CTRL+C ?
        print
        printInfo("User press CTRL+C")
        gracefulExit()
    except Exception, e: #Did somethings went wrong? (ie. hot-unpluged  BT adapter)
        printError(str(e))
        badExit()
        
def print_results(purgedList):
    os.system('clear')

    #Orange logo
    print O
    printLogo()
    #Formated output for result
    print G + "{0:<20s}{1:<10s}{2:<10s}{3:<10s}{4:<10s}{5:<13s}{6:<10s}".format("MAC","MAJOR","MINOR","RSSI","TxPOWER","DISTANCE(m)","UUID")

    for beacon in purgedList:
        print("{0:<20s}{1:<10}{2:<10}{3:<10d}{4:<10d}{5:<13.2f}{6:<10}".format(beacon['MAC'],beacon['MAJOR'],beacon['MINOR'],beacon['RSSI'][0],beacon['TxPOWER'][0],getDistance(beacon['RSSI'][0]),beacon['UUID']))
        
