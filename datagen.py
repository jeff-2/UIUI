#Generates a SQL script that creates data on the server.

from __future__ import division, generators

from datetime import time
from random import randint, random, normalvariate, seed


print 'WARNING: this file generates a simple script and assumes non-malicious input; ' + \
		'it is vulnerable to SQL injection\n'

print 'WARING: times at or after midnight are cut off due to schema limitations\n'



DAYS_MAP = {'M':'MONDAY', 'T':'TUESDAY', 'W':'WEDNESDAY', 'R':'THURSDAY', 'F':'FRIDAY', \
		'A':'SATURDAY', 'S':'SUNDAY'}

def next_restaurant_id():
	next_restaurant_id.counter += 1
	return next_restaurant_id.counter
next_restaurant_id.counter = 0


def rand_time(a,b):
	'''Returns a random time that occurs in the range [a,b]'''
	ma, mb = a.hour * 60 + a.minute, b.hour * 60 + b.minute
	diff = mb - ma
	result = int(ma + random() * diff)
	return time(hour=int(result/60), minute=result%60)

def rand_date_on_any(days):
	'''Returns a random date that matches any of the provided days.
	days is a string containing any combination of the characters in MTWRFAS'''
	return DAYS_MAP[days[randint(0, len(days) - 1)]]
		

class Restaurant:
	'''tags: a list of strings.
	hours: a list of tuples (start, end, daystring)'''
	def __init__(self, name, location, lat, lon, tags):
		self.dbid = next_restaurant_id()
		self.name, self.location = name, location
		self.lat, self.lon = lat, lon
		self.tags, self.hours = tags, []


class CrowdRecord:
	'''date is actually a string like "MONDAY"'''
	def __init__(self, crowd, restaurant, date, time):
		self.crowd, self.restaurant, self.date, self.time = crowd, restaurant, date, time


class Dataset:
	'''tags: list of strings'''
	
	def __init__(self):
		self.restaurants, self.crowd_records = [], []
	
	def get_restaurant_by_name(self, name):
		for r in self.restaurants:
			if r.name == name:
				return r
		raise ValueError(name)



def put_hours(start, end, days, dataset):
	'''Adds hours for the last restaurant in the dataset.
	days contains any combination of characters in "MTWRFAS"'''
	for k in days:
		dataset.restaurants[-1].hours.append((start, end, DAYS_MAP[k]))


def populate(count, mean, start, end, days, dataset):
	'''Puts count crowdedness values in the dataset's crowd records list,
	Values are within the provided time span, on one of the provided days, and close to the provided mean.
	days is a string containing any combination of characters in MTWRFAS'''
	restaurant = dataset.restaurants[-1]
	for n in range(count):
		result_date = rand_date_on_any(days)
		result_time = rand_time(start, end)
		value = normalvariate(mean, .15)
		value = max(value, 0)
		value = min(value, 1)
		dataset.crowd_records.append(CrowdRecord(value, restaurant, result_date, result_time))




def create_script(dataset):
	'''Turns a dataset into an SQL script that will put values in the dataset into a database.
	Prints the results to standard output.'''
	print 'DELETE FROM crowding;'
	print 'DELETE FROM restauranthours;'
	print 'DELETE FROM tags;'
	print 'DELETE FROM restaurants;'
	print '\n'
	
	print 'INSERT INTO restaurants (id, name, address, Latitude, Longitude) VALUES'
	print ',\n'.join(['({dbid}, "{name}", "{loc}", {lat}, {lon})'.format(dbid=r.dbid, name=r.name, loc=r.location,
			lat=r.lat, lon=r.lon)
			for r in dataset.restaurants]) + ';\n'
	
	print 'INSERT INTO tags (restaurantid, tag) VALUES'
	print ',\n'.join(['({rid}, "{name}")'.format(rid=r.dbid, name=t) for r in dataset.restaurants for t in r.tags]) + ';\n'
	
	print 'INSERT INTO restauranthours (opentime, closetime, day, restaurantid) VALUES'
	print ',\n'.join(['("{a}", "{b}", "{day}", {rid})'.format(a=t[0], b=t[1], day=t[2], rid=r.dbid)
			for r in dataset.restaurants for t in r.hours]) + ';\n'
	
	print 'INSERT INTO crowding (crowdedness, restaurantid, day, time) VALUES'
	print ',\n'.join(['({crowd}, {rid}, "{day}", "{time}")'.format(crowd=c.crowd, rid=c.restaurant.dbid, day=c.date, time=c.time)
			for c in dataset.crowd_records]) + ';\n'



if __name__ == '__main__':
	seed(4)
	
	data = Dataset()
	data.restaurants.append(Restaurant('Panera Bread', '616 E Green St, Champaign, IL', 40.110534, -88.229456, ['Bread', 'Fast', 'Sandwich']))
	put_hours(time(7), time(9+12), 'MTWRF', data)
	put_hours(time(8), time(9+12), 'AS', data)
	populate(3, .25, time(7), time(8), 'MTWRF', data)#empty early morning
	populate(5, .7, time(8), time(10), 'MTWRF', data)#crowded breakfast
	populate(8, .45, time(10), time(8+12), 'MTWR', data)#medium afternoon and dinner
	populate(3, .3, time(10), time(9+12), 'F', data)#light friday after noon
	populate(2, .1, time(8+12), time(9+12), 'MTWR', data)#empty night weekdays
	populate(6, .75, time(8), time(1+12), 'AS', data)#packed weekend mornings
	populate(4, .5, time(1+12), time(9+12), 'AS', data)#medium weekend after noon
	
	data.restaurants.append(Restaurant('Noodles & Company', '6th & Green St, Champaign, IL', 40.110482, -88.230605, ['Italian']))
	put_hours(time(11), time(10+12), 'MTWRFAS', data)
	populate(4, .3, time(11), time(4,30), 'MTWRFAS', data)#light daytime
	populate(6, .65, time(4,30), time(8+12), 'MTWRFAS', data)#crowded evening
	populate(3, .35, time(8+12), time(10+12), 'MTWRFAS', data)#light night
	
	data.restaurants.append(Restaurant('Penn Station East Coast Subs', '605 S 6th St, Champaign, IL', 40.110764, -88.230630, ['Sandwich']))
	put_hours(time(11), time(10+12), 'MTWRFA', data)
	put_hours(time(12), time(8+12), 'S', data)
	populate(6, .65, time(11), time(1+12), 'MTWRFAS', data)#crowded lunch
	populate(3, .35, time(1+12), time(5+12), 'MTWRFAS', data)#light afternoon
	populate(6, .6, time(5+12), time(8+12), 'MTWRFAS', data)#crowded dinner
	populate(4, .2, time(8+12), time(10+12), 'MTWRFA', data)#empty evening (no Sunday)
	
	data.restaurants.append(Restaurant('Penn Station East Coast Subs', '906 W Town Center Blvd, Champaign, IL', 40.143203, -88.259134, ['Sandwich']))
	put_hours(time(11), time(10+12), 'MTWRFA', data)
	put_hours(time(12), time(8+12), 'S', data)			#few visits--little data
	populate(2, .5, time(11), time(3), 'MTWRFAS', data)#medium lunch
	populate(4, .5, time(5+12), time(8+12), 'MTWRFAS', data)#medium dinner
	
	data.restaurants.append(Restaurant('Potbelly Sandwich Shop', 'Green & 5th, Champaign, IL', 40.110082, -88.231789, ['Sandwich']))
	put_hours(time(11), time(9+12), 'MTWRFAS', data)		#light traffic
	populate(4, .35, time(11), time(1+12), 'MTWRF', data)#light weekday lunch
	populate(4, .15, time(1+12), time(4+12), 'MTWRF', data)#empty weekday afternoon
	populate(7, .3, time(4+12), time(7+12), 'MTWRF', data)#light weekday dinner
	populate(6, .6, time(11), time(3+12), 'AS', data)#medium weekend lunch/afternoon
	populate(5, .2, time(3+12), time(9+12), 'AS', data)#light weekend dinner/evening
	
	data.restaurants.append(Restaurant("Papa Del's Pizza", '206 E Green St, Champaign, IL', 40.110395, -88.236185, ['Pizza', 'Tavern']))
	put_hours(time(10,30), time(11+12), 'MTWR', data)
	put_hours(time(10,30), time(3,59), 'FA', data)
	put_hours(time(11), time(11+12), 'S', data)			#very crowded
	populate(9, .75, time(10,30), time(2+12), 'MTWRF', data)#crowded weekday lunch
	populate(6, .45, time(2+12), time(6+12), 'MTWRF', data)#medium weekday afternoon
	populate(10, .9, time(6+12), time(9+12), 'MTWRF', data)#packed weekday dinner
	populate(5, .45, time(9+12), time(11+12), 'MTWRF', data)#moderate weekday evening
	populate(1, .4, time(10,30), time(11), 'A', data)#extra saturday result
	populate(5, .6, time(11), time(1+12), 'AS', data)#crowded weekend lunch
	populate(5, .4, time(1+12), time(5+12), 'AS', data)#light weekend afternoon
	populate(5, .7, time(5+12), time(8+12), 'AS', data)#crowded weekend dinner
	populate(2, .3, time(8+12), time(11+12), 'AS', data)#light weekend night
	
	data.restaurants.append(Restaurant('Maize Mexican Grill', '?', 40.110409, -88.238956, ['Mexican']))
	put_hours(time(11), time(10+12), 'MTWR', data)
	put_hours(time(11), time(11+12), 'FA', data)
	put_hours(time(11), time(9+12), 'S', data)
	populate(6, .7, time(11), time(1+12), 'MTWRF', data)#crowded weekday lunch
	populate(5, .25, time(1), time(5+12), 'MTWRF', data)#light weekday afternoon
	populate(6, .45, time(5+12), time(8+12), 'MTWRF', data)#medium weekday dinner
	populate(2, .1, time(8+12), time(10+12), 'MTWRF', data)#empty weekday night
	populate(6, .6, time(11), time(11+12), 'A', data)#crowded saturday
	populate(5, .25, time(11), time(9+12), 'S', data)#empty sunday
	
	data.restaurants.append(Restaurant('D.P. Dough', '33 E Green St, Champaign, IL', 40.109969, -88.241056, ['Calzone', 'Italian']))
	#times cut off at midnight
	put_hours(time(11), time(23,59), 'MTWRFAS', data)
	populate(20, .5, time(11), time(23,59), 'MTWRFAS', data)
	
	create_script(data)
