python /Users/ozzy/Documents/CodeMonkey/ToPeeCAAAW/adapter.py /Users/ozzy/Documents/CodeMonkey/ToPeeCAAAW/ToPeeCAAAW_base.gcode -sx -184 -sy 260 -ox 408 -oy 370 -r
mv /Users/ozzy/Documents/CodeMonkey/ToPeeCAAAW/rescaled.gcode /Users/ozzy/Documents/CodeMonkey/ToPeeCAAAW/base.gcode
python /Users/ozzy/Documents/CodeMonkey/ToPeeCAAAW/adapter.py /Users/ozzy/Documents/CodeMonkey/ToPeeCAAAW/base.gcode -ox 408 -r
mv /Users/ozzy/Documents/CodeMonkey/ToPeeCAAAW/rescaled.gcode /Users/ozzy/Documents/CodeMonkey/ToPeeCAAAW/base.gcode

python /Users/ozzy/Documents/CodeMonkey/ToPeeCAAAW/adapter.py /Users/ozzy/Documents/CodeMonkey/ToPeeCAAAW/ToPeeCAAAW_acct.gcode -sx -184 -sy 260 -ox 408 -oy 370 -r
mv /Users/ozzy/Documents/CodeMonkey/ToPeeCAAAW/rescaled.gcode /Users/ozzy/Documents/CodeMonkey/ToPeeCAAAW/acct.gcode
python /Users/ozzy/Documents/CodeMonkey/ToPeeCAAAW/adapter.py /Users/ozzy/Documents/CodeMonkey/ToPeeCAAAW/acct.gcode -ox 408 -r
mv /Users/ozzy/Documents/CodeMonkey/ToPeeCAAAW/rescaled.gcode /Users/ozzy/Documents/CodeMonkey/ToPeeCAAAW/acct.gcode
