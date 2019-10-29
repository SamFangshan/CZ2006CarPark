import logging
import datetime
import time
import os
from sqlalchemy import create_engine
from api_data_fetcher import load_HDB_carpark_availability
from sql_handler import switch_view, delete_old_tables
from sqlalchemy.types import Integer, Float
from twilio.rest import Client

# WhatsApp set up
os.environ['TWILIO_ACCOUNT_SID'] = 'AC7ab9159fa8cab479802cd027522d2d09'
os.environ['TWILIO_AUTH_TOKEN'] = '164885c790a25c0e6df4cab3cf062c87'
client = Client()
from_whatsapp_number='whatsapp:+14155238886'
to_whatsapp_number='whatsapp:+6583062078'

# Logger set up
logging.basicConfig(filename='/home/VMadmin/MinuteJob/MinuteJob.log', format='%(asctime)s - %(levelname)s - %(message)s',
        level=logging.DEBUG)
logger = logging.getLogger("MinuteJob")

# MySQL connection set up
logger.info('Connecting to MySQL Server...')
try:
    conn = create_engine('mysql+pymysql://cz2006:cz2006ala@127.0.0.1:3306/cz2006').connect()
except Exception as e:
    logger.error(e)
    logger.error('Failed to connect to MySQL Server.')
    time.sleep(5)
    logger.info('Reconnecting...')
    try:
        conn = create_engine('mysql+pymysql://cz2006:cz2006ala@127.0.0.1:3306/cz2006').connect()
    except Exception as e:
        logger.error(e)
        logger.error('Failed to connect to MySQL Server.')
        client.messages.create(body='Failed to connect to MySQL Server.\n' + str(e),
                       from_=from_whatsapp_number,
                       to=to_whatsapp_number)
        exit()
logger.info('Connection to MySQL Server established successfully.')

# Load HDB car park availability data
logger.info('Loading HDB car park data from API...')
try:
    df = load_HDB_carpark_availability()
except Exception as e:
    logger.error(e)
    logger.error('Failed to load HDB car park data.')
    time.sleep(5)
    logger.info('Retrying...')
    try:
        df = load_HDB_carpark_availability()
    except Exception as e:
        logger.error(e)
        logger.error('Failed to load HDB car park data.')
        client.messages.create(body='Failed to load HDB car park data.\n' + str(e),
                       from_=from_whatsapp_number,
                       to=to_whatsapp_number)
        conn.close()
        exit()
logger.info('Loaded HDB car park data from API successfully.')

# Write to table
dtype = {
         'carLotAvail': Integer(),
         'motorLotAvail': Integer(),
         'heavyLotAvail': Integer()
        }
table_name = 'HDBCarParkAvail_' + '_'.join(str(datetime.datetime.now())[:-10].split())
logger.info('Writing data to MySQL table...')
try:
    df.to_sql(table_name, conn, if_exists='replace', index=False, dtype=dtype)
except Exception as e:
    logger.error(e)
    logger.error('Failed to write to MySQL table.')
    client.messages.create(body='Failed to write to MySQL table.\n' + str(e),
                       from_=from_whatsapp_number,
                       to=to_whatsapp_number)
    conn.close()
    exit()
logger.info('Written data to MySQL successfully.')

# Switch view
logger.info('Switching view to new car park availability table...')
try:
    switch_view(conn, table_name)
except Exception as e:
    logger.error(e)
    logger.error('Failed to switch view.')
    time.sleep(5)
    logger.info('Retrying...')
    try:
        switch_view(conn, table_name)
    except Exception as e:
        logger.error(e)
        logger.error('Failed to switch view.')
        client.messages.create(body='Failed to switch view.\n' + str(e),
                               from_=from_whatsapp_number,
                               to=to_whatsapp_number)
        conn.close()
        exit()
logger.info('Switched view to new car park availability table successfully.')

# Delete old tables
logger.info('Deleting old car park availability tables...')
try:
    delete_old_tables(conn, table_name)
except Exception as e:
    logger.error(e)
    logger.error('Failed to delete tables.')
    time.sleep(5)
    logger.info('Retrying...')
    try:
        delete_old_tables(conn, table_name)
    except Exception as e:
        logger.error(e)
        logger.error('Failed to delete tables.')
        client.messages.create(body='Failed to delete tables.\n' + str(e),
                               from_=from_whatsapp_number,
                               to=to_whatsapp_number)
        conn.close()
        exit()
logger.info('Deleted old car park availability tables successfully.')

conn.close()
