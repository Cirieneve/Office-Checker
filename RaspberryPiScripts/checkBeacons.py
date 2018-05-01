# Use CouchDB to create a CouchDB client
# from cloudant.client import CouchDB
# client = CouchDB(USERNAME, PASSWORD, url='http://127.0.0.1:5984')

# Use Cloudant to create a Cloudant client using account
import blescan
import sys
import bluetooth._bluetooth as bluez
import time
from cloudant.client import Cloudant


def main():
	#connect to cloudant database
	client = Cloudant("<account>",
			"<password>", 
			account="<account>", 
			connect=True,
            auto_renew=True,
            timeout=300)

	dev_id = 0
	try:
		sock = bluez.hci_open_dev(dev_id)
	except:
		print("error accessing bluetooth device...")
		sys.exit(1)

	blescan.hci_le_set_scan_parameters(sock)
	blescan.hci_enable_le_scan(sock)

	count = 0
	my_document = client['present']['<ble Device UDID>']
	while count < 30:
		returnedList = blescan.parse_events(sock, 10)
		for beacon in returnedList:
			if beacon.split(",")[1] == "<ble Device UDID>":
				my_document['present'] = True
				my_document.save()
				return
			count += 1
	my_document['present'] = False
	my_document.save()

main()
