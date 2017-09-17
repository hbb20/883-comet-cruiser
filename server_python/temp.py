from pycouchdb import Server
server = Server()
db = server.database('python-tests')
map_fun = '''function(doc){
	if(doc.route == 1){
		emit(doc.name)
	}
}'''
'''
for row in db.query(map_fun,language = 'javascript'):
	print(row.key)
	
'''

for row in db.view('_all_docs'):
	print(row.id)