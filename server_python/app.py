from flask import Flask,jsonify,request
import couchdb

id = 'thakkark1313'



server = couchdb.Server()
db = server['location_details']
db_auth = server['db_auth']
'''
doc = db.get(id)

location={'latitude':doc['latitude'],'longitude':doc['longitude'],'name':doc['name'],'route':doc['route'],'driver_id':doc['_id']}

to_send = []
to_send.append(location)
'''
app = Flask(__name__)



@app.route('/get_locations', methods=['GET'])
def get_locations():
	to_send = []
	args = request.args
	temp = args['route_id']	
	if (len(temp) == 2):		
		for row in db.view('_all_docs'):
			doc = db.get(row.id)
			to_send.append(doc)				
		return jsonify({'locations': to_send})
	
	else:				
		route_id = []

		temp1 = temp[1:len(temp)-1]				
		if (len(temp1) == 1):			
			route_id.append(int(temp1))
		else:
			s = temp1.split(',')						
			for i in s:
				route_id.append(int(i))	
			to_send = []			
		map_fun = '''function(doc){			
		emit(doc._id)			
		}'''		
		for row in db.query(map_fun,language = 'javascript'):						
			doc = db.get(row.key)									
			if (int(doc['route']) in route_id):
				to_send.append(doc)				
		return jsonify({'locations': to_send})



@app.route('/login',methods=['POST'])
def login():		
	username = request.form['username']
	password = request.form['password']	
	doc = db_auth.get(username)	
	if (doc is not None):
		if (doc['password'] == password):
			return jsonify({'user':doc})
	return jsonify({'user':{}})


@app.route('/start',methods=['POST'])
def start():	
	
	doc={}
	username = request.form['driver_id']
	if (username in db):
		dctemp = db[username]
		dctemp['route'] = request.form['route_id']
		dctemp['latitude'] = request.form['latitude']
		dctemp['longitude'] = request.form['longitude']	
		db.save(dctemp)
		return jsonify({})

	route_id = request.form['route_id']	
	latitude = request.form['latitude']
	longitude = request.form['longitude']	
	doc['_id'] = username
	doc['latitude'] = latitude
	doc['longitude'] = longitude
	doc['route'] = route_id
	db.save(doc)	
	return jsonify({})


@app.route('/end',methods=['POST'])
def end():	
	driver_id = request.form['driver_id']	
	doc = db.get(driver_id)
	db.delete(doc)
	return jsonify({})
	




@app.route('/report_location', methods=['PUT'])
def update_location():	

	latitude = request.form['latitude']
	longitude = request.form['longitude']
	driver_id = request.form['driver_id']
	route_id = request.form['route_id']	
	doc = db[driver_id]	
	doc['latitude'] = latitude
	doc['longitude'] = longitude
	doc['route'] = route_id
	db.save(doc)
	return ('',204)



'''


@app.route('/')
def index():
    return "Hello, World!"
'''
if __name__ == '__main__':
	app.run(host='0.0.0.0')	