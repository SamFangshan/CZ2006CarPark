import json
import pandas as pd
import numpy as np
from urllib.request import Request, urlopen
from onemap_converter import OneMapConverter

def load_HDB_carpark():
    converter = OneMapConverter('FJIANG003@e.ntu.edu.sg', 'XS4teTdcYz')

    # Load HDB Carpark Information
    url_HDB_carpark = 'https://data.gov.sg/api/action/datastore_search?resource_id=139a3035-e624-4f56-b63f-89ae28d4ae4c&limit={}'
    req_HDB_carpark = Request(url_HDB_carpark.format(1), headers={'User-Agent': 'Mozilla/5.0'})
    webpage_HDB_carpark = urlopen(req_HDB_carpark).read()
    data_HDB_carpark = json.loads(webpage_HDB_carpark.decode())
    no_rec = data_HDB_carpark['result']['total'] # number of HDB Carpark Records
    url_HDB_carpark = 'https://data.gov.sg/api/action/datastore_search?resource_id=139a3035-e624-4f56-b63f-89ae28d4ae4c&limit={}'
    req_HDB_carpark = Request(url_HDB_carpark.format(no_rec), headers={'User-Agent': 'Mozilla/5.0'})
    webpage_HDB_carpark = urlopen(req_HDB_carpark).read()
    data_HDB_carpark = json.loads(webpage_HDB_carpark.decode())

    # Load HDB Carpark Lots Information
    url_HDB_lots_info = 'https://api.data.gov.sg/v1/transport/carpark-availability'
    req_HDB_lots_info = Request(url_HDB_lots_info, headers={'User-Agent': 'Mozilla/5.0'})
    webpage_HDB_lots_info = urlopen(req_HDB_lots_info).read()
    data_HDB_lots_info = json.loads(webpage_HDB_lots_info.decode())

    # Load HDB Carpark Information into Pandas Data Frame
    short_term_parking = [data_HDB_carpark['result']['records'][i]['short_term_parking'] for i in range(no_rec)]
    car_park_type = [data_HDB_carpark['result']['records'][i]['car_park_type'] for i in range(no_rec)]
    x_coord = [data_HDB_carpark['result']['records'][i]['x_coord'] for i in range(no_rec)]
    y_coord = [data_HDB_carpark['result']['records'][i]['y_coord'] for i in range(no_rec)]
    coord = [(x_coord[i], y_coord[i]) for i in range(no_rec)]
    coord = [converter.convert(float(coord[i][0]), float(coord[i][1])) for i in range(no_rec)]
    x_coord = [coord[i][0] for i in range(no_rec)]
    y_coord = [coord[i][1] for i in range(no_rec)]
    free_parking = [data_HDB_carpark['result']['records'][i]['free_parking'] for i in range(no_rec)]
    gantry_height = [data_HDB_carpark['result']['records'][i]['gantry_height'] for i in range(no_rec)]
    car_park_basement = [data_HDB_carpark['result']['records'][i]['car_park_basement'] for i in range(no_rec)]
    night_parking = [data_HDB_carpark['result']['records'][i]['night_parking'] for i in range(no_rec)]
    address = [data_HDB_carpark['result']['records'][i]['address'] for i in range(no_rec)]
    car_park_decks = [data_HDB_carpark['result']['records'][i]['car_park_decks'] for i in range(no_rec)]
    car_park_no = [data_HDB_carpark['result']['records'][i]['car_park_no'] for i in range(no_rec)]
    type_of_parking_system = [data_HDB_carpark['result']['records'][i]['type_of_parking_system'] for i in range(no_rec)]
    HDB_carpark = {
                   'carParkNo': car_park_no,
                   'address': address,
                   'xCoord': x_coord,
                   'yCoord': y_coord,
                   'carParkType': car_park_type,
                   'typeOfParkingSystem': type_of_parking_system,
                   'shortTermParking': short_term_parking,
                   'freeParking': free_parking,
                   'nightParking': night_parking,
                   'carParkDecks': car_park_decks,
                   'gantryHeight': gantry_height,
                   'carParkBasement': car_park_basement,
                  }
    HDB_carpark = pd.DataFrame.from_dict(HDB_carpark)

    # Load HDB Carpark Lots Information into Pandas Data Frame
    HDB_lots_info = {}
    for record in data_HDB_lots_info['items'][0]['carpark_data']:
        carpark_info = record['carpark_info']
        car_lot_num = 0
        motor_lot_num = 0
        heavy_lot_num = 0
        for i in range(len(carpark_info)):
            if carpark_info[i]['lot_type'] == 'C':
                car_lot_num = carpark_info[i]['total_lots']
            elif carpark_info[i]['lot_type'] == 'Y':
                motor_lot_num = carpark_info[i]['total_lots']
            elif carpark_info[i]['lot_type'] == 'L':
                heavy_lot_num = carpark_info[i]['total_lots']
        try:
            if HDB_lots_info[record['carpark_number']][1] == 0:
                HDB_lots_info[record['carpark_number']][1] = car_lot_num
            if HDB_lots_info[record['carpark_number']][2] == 0:
                HDB_lots_info[record['carpark_number']][2] = motor_lot_num
            if HDB_lots_info[record['carpark_number']][3] == 0:
                HDB_lots_info[record['carpark_number']][3] = heavy_lot_num
        except:
            HDB_lots_info[record['carpark_number']] = [record['carpark_number'], car_lot_num, motor_lot_num, heavy_lot_num]
    HDB_lots_info = dict(zip(range(len(HDB_lots_info)), HDB_lots_info.values()))
    columns = ['carParkNo', 'carLotNum', 'motorLotNum', 'heavyLotNum']
    HDB_lots_info = pd.DataFrame.from_dict(HDB_lots_info, orient='index', columns=columns)
    
    # Merge two Pandas Data Frames
    HDB_carpark = pd.merge(HDB_carpark, HDB_lots_info, on='carParkNo', how='inner')
    
    # Provide rates information
    # Information Source:
    # https://www.hdb.gov.sg/cs/infoweb/car-parks/short-term-parking/short-term-parking-charges
    central = ['HLM', 'KAB', 'KAM', 'KAS', 'PRM', 'SLS', 'SR1', 'SR2', 'TPM', 'UCS']
    loading = ['GSML', 'BRBL', 'JCML', 'T55', 'GEML', 'KAML', 'J57L', 'J6OL', 'TPL', 'EPL', 'BL8L']    
    car_rates = '$0.60 per half-hour'
    motor_rates = '$0.20 per half-hour'
    heavy_rates = '$1.20 per half-hour'
    central_rates = """$1.20 per half-hour
    (7:00am to 5:00pm, Monday to Saturday)
    $0.60 per half hour
    (Other hours)
                    """
    loading_rates = """Free -  First 15 minutes
    $2 - first half hour
    $4 - subsequent half hour
    """
    HDB_carpark['carRates'] = np.where(pd.to_numeric(HDB_carpark['carLotNum']) != 0, car_rates, None)
    HDB_carpark['carRates'] = np.where(np.isin(HDB_carpark['carParkNo'], central), central_rates, HDB_carpark['carRates'])
    HDB_carpark['motorRates'] = np.where(pd.to_numeric(HDB_carpark['motorLotNum']) != 0, motor_rates, None)
    HDB_carpark['heavyRates'] = np.where(pd.to_numeric(HDB_carpark['heavyLotNum']) != 0, heavy_rates, None)
    HDB_carpark['carRates'] = np.where(np.isin(HDB_carpark['carParkNo'], loading), loading_rates, HDB_carpark['carRates'])
    HDB_carpark['motorRates'] = np.where(np.isin(HDB_carpark['carParkNo'], loading), loading_rates, HDB_carpark['motorRates'])
    HDB_carpark['heavyRates'] = np.where(np.isin(HDB_carpark['carParkNo'], loading), loading_rates, HDB_carpark['heavyRates'])
    
    return HDB_carpark
