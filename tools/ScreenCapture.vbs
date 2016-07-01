set objShell = WScript.CreateObject("WScript.Shell")

' 用run最小化执行自己，使得后面exec也无窗口闪现
if LCase(Right(WScript.FullName, 12)) = "\wscript.exe" then
    objShell.Run "cscript """ & WScript.ScriptFullName & chr(34), 0, false
    WScript.quit
end if

' 查看手机连接
' List of devices attached
' e3ad37fb        device
devices = Split(objShell.exec("adb devices").Stdout.ReadAll, chr(10))

if ubound(devices) > 0 and instr(devices(1), "device") > 0 then
	' 获取设备sn
	sn = Split(devices(1), chr(9))(0)
	' 截图文件名 ScreenCapture_20160211_172118.png
	datedisp = Year(Now) & FillZero(Month(Now)) & FillZero(Day(Now))
	timedisp = FillZero(Hour(Now)) & FillZero(Minute(Now)) & FillZero(Second(Now))
	imgname = "ScreenCapture_" & datedisp & "_" & timedisp & ".png"
	' 开始截图
	objShell.Run "adb -s " & sn & " shell screencap -p > /sdcard/" & imgname, 0, true
	objShell.Run "adb -s " & sn & " pull /sdcard/" & imgname & " " & imgname, 0, true
	objShell.Run "adb -s " & sn & " shell rm /sdcard/" & imgname, 0, true
	ret = objShell.Popup("截图成功 (" & imgname & ")" & chr(10) & "是否打开?", 3, "ScreenCapture.vbs", vbQuestion or vbOKCancel)
	if ret = vbOK then
		objShell.Run imgname
	end if
else
	objShell.Popup "无手机连接", 2, "ScreenCapture.vbs", vbExclamation
end if

WScript.quit


' 不足两位的字符串前面补0
function FillZero(src)
	if len(src) = 1 then
		FillZero = "0" & src
	else
		FillZero = src
	end if
end function