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
  populate(5, .2, time(7), time(9), 'MTWRF', data) #weekday early mornings empty
  populate(6, .39, time(9), time(11), 'MTWRF', data) #weekday mid mornings light
  populate(7, .55, time(11), time(13), 'MTWRF', data) #weekday early afternoon crowded
  populate(5, .57, time(13), time(15), 'MTWRF', data) #weekday mid afternoon crowded
  populate(8, .51, time(15), time(16), 'MTWRF', data) #weekday early evening crowded
  populate(6, .64, time(16), time(18), 'MTWRF', data) #weekday mid evening crowded
  populate(5, .76, time(18), time(19), 'MTWRF', data) #weekday evening packed
  populate(7, .49, time(20), time(21), 'MTWRF', data) #weekday night crowded
  populate(3, .12, time(8), time(9), 'AS', data) #weekend early mornings empty
  populate(2, .34, time(9), time(11), 'AS', data) #weekend mid mornings light
  populate(4, .78, time(11), time(13), 'AS', data) #weekend early afternoon packed
  populate(2, .64, time(13), time(15), 'AS', data) #weekend mid afternoon crowded
  populate(3, .53, time(15), time(16), 'AS', data) #weekend early evening crowded
  populate(3, .61, time(16), time(18), 'AS', data) #weekend mid evening crowded
  populate(2, .81, time(18), time(19), 'AS', data) #weekend crowded packed
  populate(4, .85, time(20), time(21), 'AS', data) #weekend night packed
  
  data.restaurants.append(Restaurant('Noodles & Company', '6th & Green St, Champaign, IL', 40.110482, -88.230605, ['Italian']))
  put_hours(time(11), time(10+12), 'MTWRFAS', data)
  populate(7, .62, time(11), time(13), 'MTWRF', data) #weekday early afternoon crowded
  populate(5, .54, time(13), time(15), 'MTWRF', data) #weekday mid afternoon crowded
  populate(8, .41, time(15), time(16), 'MTWRF', data) #weekday early evening light
  populate(6, .53, time(16), time(18), 'MTWRF', data) #weekday mid evening crowded
  populate(5, .82, time(18), time(19), 'MTWRF', data) #weekday evening packed
  populate(7, .65, time(20), time(22), 'MTWRF', data) #weekday night crowded
  populate(4, .78, time(11), time(13), 'AS', data) #weekend early afternoon packed
  populate(2, .64, time(13), time(15), 'AS', data) #weekend mid afternoon crowded
  populate(3, .53, time(15), time(16), 'AS', data) #weekend early evening crowded
  populate(3, .61, time(16), time(18), 'AS', data) #weekend mid evening crowded
  populate(2, .81, time(18), time(19), 'AS', data) #weekend crowded packed
  populate(4, .85, time(20), time(21), 'AS', data) #weekend night packed
  
  data.restaurants.append(Restaurant('Penn Station East Coast Subs', '605 S 6th St, Champaign, IL', 40.110764, -88.230630, ['Sandwich']))
  put_hours(time(11), time(10+12), 'MTWRFA', data)
  put_hours(time(12), time(8+12), 'S', data)
  populate(7, .62, time(11), time(13), 'MTWRF', data) #weekday early afternoon crowded
  populate(5, .67, time(13), time(15), 'MTWRF', data) #weekday mid afternoon crowded
  populate(8, .53, time(15), time(16), 'MTWRF', data) #weekday early evening crowded
  populate(6, .6, time(16), time(18), 'MTWRF', data) #weekday mid evening crowded
  populate(5, .71, time(18), time(19), 'MTWRF', data) #weekday evening packed
  populate(7, .54, time(20), time(22), 'MTWRF', data) #weekday night crowded
  populate(4, .27, time(12), time(13), 'AS', data) #weekend early afternoon light
  populate(2, .72, time(13), time(15), 'AS', data) #weekend mid afternoon crowded
  populate(3, .59, time(15), time(16), 'AS', data) #weekend early evening crowded
  populate(3, .66, time(16), time(18), 'AS', data) #weekend mid evening crowded
  populate(2, .85, time(18), time(19), 'AS', data) #weekend crowded packed
  populate(4, .31, time(20), time(20), 'AS', data) #weekend night light
  
  data.restaurants.append(Restaurant('Penn Station East Coast Subs', '906 W Town Center Blvd, Champaign, IL', 40.143203, -88.259134, ['Sandwich']))
  put_hours(time(11), time(10+12), 'MTWRFA', data)
  put_hours(time(12), time(8+12), 'S', data)      #few visits--little data
  populate(7, .28, time(11), time(13), 'MTWRF', data) #weekday early afternoon light
  populate(5, .12, time(13), time(15), 'MTWRF', data) #weekday mid afternoon empty
  populate(8, .26, time(15), time(16), 'MTWRF', data) #weekday early evening light
  populate(6, .3, time(16), time(18), 'MTWRF', data) #weekday mid evening light
  populate(5, .56, time(18), time(19), 'MTWRF', data) #weekday evening crowded
  populate(7, .32, time(20), time(22), 'MTWRF', data) #weekday night light
  populate(4, .27, time(12), time(13), 'AS', data) #weekend early afternoon light
  populate(2, .37, time(13), time(15), 'AS', data) #weekend mid afternoon light
  populate(3, .41, time(15), time(16), 'AS', data) #weekend early evening light
  populate(3, .29, time(16), time(18), 'AS', data) #weekend mid evening light
  populate(2, .57, time(18), time(19), 'AS', data) #weekend crowded crowded
  populate(4, .31, time(20), time(20), 'AS', data) #weekend night light
  
  data.restaurants.append(Restaurant('Potbelly Sandwich Shop', 'Green & 5th, Champaign, IL', 40.110082, -88.231789, ['Sandwich']))
  put_hours(time(11), time(9+12), 'MTWRFAS', data)    #light traffic
  populate(7, .55, time(11), time(13), 'MTWRF', data) #weekday early afternoon crowded
  populate(5, .79, time(13), time(15), 'MTWRF', data) #weekday mid afternoon packed
  populate(8, .59, time(15), time(16), 'MTWRF', data) #weekday early evening crowded
  populate(6, .52, time(16), time(18), 'MTWRF', data) #weekday mid evening crowded
  populate(5, .79, time(18), time(19), 'MTWRF', data) #weekday evening packed
  populate(7, .52, time(20), time(21), 'MTWRF', data) #weekday night crowded
  populate(4, .77, time(11), time(13), 'AS', data) #weekend early afternoon packed
  populate(2, .60, time(13), time(15), 'AS', data) #weekend mid afternoon crowded
  populate(3, .55, time(15), time(16), 'AS', data) #weekend early evening crowded
  populate(3, .65, time(16), time(18), 'AS', data) #weekend mid evening crowded
  populate(2, .83, time(18), time(19), 'AS', data) #weekend crowded packed
  populate(4, .74, time(20), time(21), 'AS', data) #weekend night crowded
  
  data.restaurants.append(Restaurant("Papa Del's Pizza", '206 E Green St, Champaign, IL', 40.110395, -88.236185, ['Pizza', 'Tavern']))
  put_hours(time(10,30), time(11+12), 'MTWR', data)
  put_hours(time(10,30), time(23,59), 'FA', data)
  put_hours(time(11), time(11+12), 'S', data)
  populate(7, .34, time(10,30), time(13), 'MTWR', data) #weekday early afternoon light
  populate(5, .19, time(13), time(15), 'MTWR', data) #weekday mid afternoon empty
  populate(8, .59, time(15), time(16), 'MTWR', data) #weekday early evening crowded
  populate(6, .26, time(16), time(18), 'MTWR', data) #weekday mid evening light
  populate(5, .68, time(18), time(19), 'MTWR', data) #weekday evening crowded
  populate(7, .54, time(20), time(23), 'MTWR', data) #weekday night crowded
  populate(3, .3, time(10,30), time(13), 'FA', data) #friday, saturday early afternoon light
  populate(2, .66, time(13), time(15), 'FA', data) #friday, saturday mid afternoon crowded
  populate(3, .57, time(15), time(16), 'FA', data) #friday, saturday early evening crowded
  populate(3, .52, time(16), time(18), 'FA', data) #friday, saturday evening crowded
  populate(2, .76, time(18), time(19), 'FA', data) #friday, saturday crowded packed
  populate(4, .67, time(20), time(23,59), 'FA', data) #friday, saturday night crowded
  populate(4, .09, time(11), time(13), 'S', data) #sunday early afternoon empty
  populate(2, .31, time(13), time(15), 'S', data) #sunday mid afternoon light
  populate(3, .51, time(15), time(16), 'S', data) #sunday early evening crowded
  populate(3, .66, time(16), time(18), 'S', data) #sunday mid evening crowded
  populate(2, .56, time(18), time(19), 'S', data) #sunday evening crowded
  populate(4, .28, time(20), time(23), 'S', data) #sunday night light
  
  data.restaurants.append(Restaurant('Maize Mexican Grill', '60 E Green St, Champaign, IL 61820', 40.110409, -88.238956, ['Mexican']))
  put_hours(time(11), time(10+12), 'MTWR', data)
  put_hours(time(11), time(11+12), 'FA', data)
  put_hours(time(11), time(9+12), 'S', data)
  populate(7, .34, time(11), time(13), 'MTWR', data) #weekday early afternoon light
  populate(5, .15, time(13), time(15), 'MTWR', data) #weekday mid afternoon empty
  populate(8, .57, time(15), time(16), 'MTWR', data) #weekday early evening crowded
  populate(6, .34, time(16), time(18), 'MTWR', data) #weekday mid evening light
  populate(5, .71, time(18), time(19), 'MTWR', data) #weekday evening crowded
  populate(7, .64, time(20), time(22), 'MTWR', data) #weekday night crowded
  populate(3, .51, time(11), time(13), 'FA', data) #friday, saturday early afternoon crowded
  populate(2, .58, time(13), time(15), 'FA', data) #friday, saturday mid afternoon crowded
  populate(3, .62, time(15), time(16), 'FA', data) #friday, saturday early evening crowded
  populate(3, .85, time(16), time(18), 'FA', data) #friday, saturday evening crowded
  populate(2, .82, time(18), time(19), 'FA', data) #friday, saturday crowded packed
  populate(4, .71, time(20), time(23), 'FA', data) #friday, saturday night crowded
  populate(4, .17, time(11), time(13), 'S', data) #sunday early afternoon empty
  populate(2, .36, time(13), time(15), 'S', data) #sunday mid afternoon light
  populate(3, .58, time(15), time(16), 'S', data) #sunday early evening crowded
  populate(3, .61, time(16), time(18), 'S', data) #sunday mid evening crowded
  populate(2, .71, time(18), time(19), 'S', data) #sunday evening crowded
  populate(4, .45, time(20), time(21), 'S', data) #sunday night light
  
  data.restaurants.append(Restaurant('D.P. Dough', '33 E Green St, Champaign, IL', 40.109969, -88.241056, ['Calzone', 'Italian']))
  #times cut off at midnight
  put_hours(time(11), time(23,59), 'MTWRFAS', data)
  populate(7, .33, time(11), time(13), 'MTWRF', data) #weekday early afternoon light
  populate(5, .54, time(13), time(15), 'MTWRF', data) #weekday mid afternoon crowded
  populate(8, .59, time(15), time(16), 'MTWRF', data) #weekday early evening crowded
  populate(6, .65, time(16), time(18), 'MTWRF', data) #weekday mid evening crowded
  populate(5, .77, time(18), time(19), 'MTWRF', data) #weekday evening packed
  populate(7, .61, time(20), time(23,59), 'MTWRF', data) #weekday night crowded
  populate(4, .29, time(11), time(13), 'AS', data) #weekend early afternoon light
  populate(2, .58, time(13), time(15), 'AS', data) #weekend mid afternoon crowded
  populate(3, .64, time(15), time(16), 'AS', data) #weekend early evening crowded
  populate(3, .32, time(16), time(18), 'AS', data) #weekend mid evening light
  populate(2, .87, time(18), time(19), 'AS', data) #weekend crowded packed
  populate(4, .79, time(20), time(23,59), 'AS', data) #weekend night packed
  
  create_script(data)