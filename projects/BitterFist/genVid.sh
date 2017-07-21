ffmpeg -r 30 -f image2 -s 800x600 -i frame-%06d.png -vcodec libx264 -crf 25 -pix_fmt yuv420p BitterFist.mp4
