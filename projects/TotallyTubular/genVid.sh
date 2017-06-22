ffmpeg -r 60 -f image2 -s 1280x1024 -i TotallyTubular-%06d.png -vcodec libx264 -crf 25 -pix_fmt yuv420p hyphae.mp4
