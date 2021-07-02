mkdir ~/.songreader/
cp songreader.scpt ~/.songreader/songreader.scpt
cp songreader.plist ~/Library/LaunchAgents/songreader.plist
launchctl load -w ~/Library/LaunchAgents/songreader.plist
