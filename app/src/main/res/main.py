# sudo pip install flask
# sudo pip install pymongo
# https://my.oschina.net/u/3579120/blog/1533496

from flask import request, Flask, jsonify
import pymongo, time

from dateutil import parser
datestr = "2019-05-14 01:11:11"

app = Flask(__name__)
app.config['JSON_AS_ASCII'] = False

def store(imei, mac):
    client = pymongo.MongoClient('127.0.0.1', 27017)
    db = client.crane
    monitor = db.monitor
    timestamp = parser.parse(datestr)
    identify = {"imei": imei, "mac": mac, "ts": timestamp}
    print(identify)
    monitor.insert(identify)
pass

@app.route('/register', methods=['POST'])
def register():
    imei = request.form['imei']
    mac = request.form['mac']
    identify = {"imei": imei, "mac": mac}
    store(imei, mac)
    return jsonify(identify), 200


if __name__ == '__main__':

    app.run(debug=False, host='0.0.0.0', port=1833)
