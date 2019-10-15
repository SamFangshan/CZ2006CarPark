def switch_view(conn, new_table_name):
    sql_template = """CREATE OR REPLACE VIEW HDBCarParkAvail AS
                      SELECT * FROM `{}`;
                   """
    conn.execute(sql_template.format(new_table_name))

def delete_old_tables(conn, new_table_name):
    result = conn.execute('show tables;').fetchall()
    tables_to_delete = []
    for table in result:
        table = table[0]
        if table != new_table_name and table != 'HDBCarPark' and table != 'HDBCarParkAvail':
            tables_to_delete.append(table)
    for table in tables_to_delete:
        conn.execute("DROP TABLE `{}`;".format(table))
