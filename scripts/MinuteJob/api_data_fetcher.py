import json
import pandas as pd
import numpy as np
from urllib.request import Request, urlopen

def load_HDB_carpark_availability():
    # Load HDB Carpark Lots Availability Information
    url_HDB_lots_avail = 'https://api.data.gov.sg/v1/transport/carpark-availability'
    req_HDB_lots_avail = Request(url_HDB_lots_avail, headers={'User-Agent': 'Mozilla/5.0'})
    webpage_HDB_lots_avail = urlopen(req_HDB_lots_avail).read()
    data_HDB_lots_avail = json.loads(webpage_HDB_lots_avail.decode())

    # Load HDB Carpark Lots Information into Pandas Data Frame
    HDB_lots_avail = {}
    for record in data_HDB_lots_avail['items'][0]['carpark_data']:
        carpark_info = record['carpark_info']
        car_lot_avail = 0
        motor_lot_avail = 0
        heavy_lot_avail = 0
        for i in range(len(carpark_info)):
            if carpark_info[i]['lot_type'] == 'C':
                car_lot_avail = carpark_info[i]['lots_available']
            elif carpark_info[i]['lot_type'] == 'Y':
                motor_lot_avail = carpark_info[i]['lots_available']
            elif carpark_info[i]['lot_type'] == 'L':
                heavy_lot_avail = carpark_info[i]['lots_available']
        try:
            if HDB_lots_avail[record['carpark_number']][1] == 0:
                HDB_lots_avail[record['carpark_number']][1] = car_lot_avail
            if HDB_lots_avail[record['carpark_number']][2] == 0:
                HDB_lots_avail[record['carpark_number']][2] = motor_lot_avail
            if HDB_lots_avail[record['carpark_number']][3] == 0:
                HDB_lots_avail[record['carpark_number']][3] = heavy_lot_avail
        except:
            HDB_lots_avail[record['carpark_number']] = [record['carpark_number'], car_lot_avail, motor_lot_avail, heavy_lot_avail]
    HDB_lots_avail = dict(zip(range(len(HDB_lots_avail)), HDB_lots_avail.values()))
    columns = ['carParkNo', 'carLotAvail', 'motorLotAvail', 'heavyLotAvail']
    HDB_lots_avail = pd.DataFrame.from_dict(HDB_lots_avail, orient='index', columns=columns)
    
    return HDB_lots_avail
