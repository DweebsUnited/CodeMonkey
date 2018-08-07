import numpy as np
import matplotlib.pyplot as plt
from mpl_toolkits.mplot3d import Axes3D
from sklearn.cluster import KMeans

data = open( "cols.csv", "r" )

xs = [ ]
ys = [ ]
zs = [ ]

for l in data.readlines( ):
    cs = l.split( ',' )
    cs = list( map( lambda c: int( c ), cs ) )

    xs.append( cs[ 0 ] )
    ys.append( cs[ 1 ] )
    zs.append( cs[ 2 ] )

data.close( )

fig = plt.figure( 1, figsize = ( 1, 2 ) )
ax = Axes3D( fig )

ax.scatter( xs, ys, zs, c = 'r', marker = 'o' )


est = KMeans( n_clusters = 10 )
data = np.array( zip( xs, ys, zs ) )
est.fit( data )
labels = est.labels_

fig = plt.figure( 2, figsize = ( 1, 2 ) )
ax = Axes3D( fig )

ax.scatter( data[ :, 0 ], data[ :, 1 ], data[ :, 2 ],
    c = labels.astype( np.float ), edgecolor='k' )

plt.show( )
