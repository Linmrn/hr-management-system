"""Exercise the DAO's actual SQL against an isolated in-memory table."""
import re
import sqlite3
from pathlib import Path

source = (Path(__file__).resolve().parents[1] / 'src/main/java/net/xju/dao/SalaryRecordDao.java').read_text(encoding='utf-8')
def sql_for(method):
    body = source.split('public int ' + method + '(', 1)[1]
    assignment = body.split('String sql =', 1)[1].split(';', 1)[0]
    return ''.join(re.findall(r'"([^"]*)"', assignment))

update = sql_for('update')
delete = sql_for('deleteById')
db = sqlite3.connect(':memory:')
columns = 'basicSalary bonus lunchSalary trafficSalary pension medical accumulationFund tax allSalary actualSalary'.split()
db.execute('CREATE TABLE salary_record (id INTEGER PRIMARY KEY, status INTEGER, ' + ','.join(c+' NUMERIC' for c in columns) + ')')
db.execute('INSERT INTO salary_record (id,status,basicSalary) VALUES (1,0,100),(2,1,200)')
values = [300]*10
assert db.execute(update, values+[0,2]).rowcount == 0, 'settled row updated'
assert db.execute(delete, [2]).rowcount == 0, 'settled row deleted'
assert db.execute(update, values+[1,1]).rowcount == 1, 'unsettled row cannot settle'
assert db.execute(update, values+[0,1]).rowcount == 0, 'stale request reverted settlement'
assert db.execute(delete, [1]).rowcount == 0, 'newly settled row deleted'
assert db.execute('SELECT basicSalary FROM salary_record WHERE id=2').fetchone()[0] == 200
assert db.execute(update, values+[0,999]).rowcount == 0
assert db.execute(delete, [999]).rowcount == 0
db.execute('INSERT INTO salary_record (id,status) VALUES (3,0)')
assert db.execute(delete, [3]).rowcount == 1
print('PASS: actual DAO SQL rejects settled updates/deletes and stale requests; unsettled delete and settlement work')
