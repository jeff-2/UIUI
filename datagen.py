#Generates a SQL script that creates data on the server.

from __future__ import division, generators

from datetime import time
from random import randint, random, normalvariate, seed


print 'WARNING: this file generates a simple script and assumes non-malicious input; ' + \
		'it is vulnerable to SQL injection\n'



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
	populate(4, .3, time(11), time(4,30), 'MTWRF', data)#light daytime
	
	data.restaurants.append(Restaurant('Penn Station East Coast Subs', '605 S 6th St, Champaign, IL', 0, 0, ['Sandwich']))
	put_hours(time(11), time(10+12), 'MTWRFA', data)
	put_hours(time(12), time(8+12), 'S', data)
	
	data.restaurants.append(Restaurant('Penn Station East Coast Subs', '906 W Town Center Blvd, Champaign, IL', 0, 0, ['Sandwich']))
	put_hours(time(11), time(10+12), 'MTWRFA', data)
	put_hours(time(12), time(8+12), 'S', data)
	
	data.restaurants.append(Restaurant('Potbelly Sandwich Shop', 'Green & 5th, Champaign, IL', 0, 0, ['Sandwich']))
	put_hours(time(11), time(9+12), 'MTWRFAS', data)
	
	data.restaurants.append(Restaurant("Papa Del's Pizza", '206 E Green St, Champaign, IL', 0, 0, ['Pizza', 'Tavern']))
	
	data.restaurants.append(Restaurant('Maize Mexican Grill', '?', 0, 0, ['Mexican']))
	
	data.restaurants.append(Restaurant('D.P. Dough', '?', 0, 0, ['?']))
	
	create_script(data)
