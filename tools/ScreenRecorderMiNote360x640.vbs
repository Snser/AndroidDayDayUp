set objShell = WScript.CreateObject("WScript.Shell")

' 用run最小化执行自己，使后面exec也无黑窗口闪现
if LCase(Right(WScript.FullName, 12)) = "\wscript.exe" then
    objShell.Run "cscript """ & WScript.ScriptFullName & chr(34), 0, false
    WScript.quit
end if

' 从文件名中读取缩放比例参数
' ScreenRecorder#0.25#rotate.vbs 即代表scale=0.5,视频旋转90度
' ScreenRecorder#0.25.vbs 即代表scale=0.5,视频不旋转
' ScreenRecorder.vbs 即代表scale=1.0,视频不旋转
dim scale
dim rotate
scriptFullName = WScript.ScriptFullName
scriptName = Right(scriptFullName, Len(scriptFullName) - InStrRev(scriptFullName, "\"))
scriptName = Left(scriptName, Len(scriptName) - 4)
scriptNameInfos = Split(scriptName, "#")

if UBound(scriptNameInfos) >= 1 then
	scale = scriptNameInfos(1)
else
	scale = 1.0
end if
on error resume next
	FormatNumber scale, 2, true
if err.number <> 0 or scale < 0.1 or scale > 1.0 then
	scale = 1.0
end if

if UBound(scriptNameInfos) >= 2 then
	rotate = scriptNameInfos(2)
else
	rotate = "normal"
end if

' 查看手机连接
' List of devices attached
' e3ad37fb        device
devices = Split(objShell.exec("adb devices").Stdout.ReadAll, chr(10))

' 获取屏幕分辨率
dim width, height
if StrComp(LCase(rotate), "rotate") <> 0 then
	GetDisplaySize width, height
else
	GetDisplaySize height, width
end if

' 计算视频宽高及比特率
videowidth = 360'width * scale
videoheight = 640'height * scale
bitrate = videowidth * videoheight * 15


if ubound(devices) > 0 and instr(devices(1), "device") > 0 and width > 0 and height > 0 then
	' 获取设备sn
	sn = Split(devices(1), chr(9))(0)
	' 视频文件名 ScreenRecorder_20160211_172118.mp4
	datedisp = Year(Now) & FillZero(Month(Now)) & FillZero(Day(Now))
	timedisp = FillZero(Hour(Now)) & FillZero(Minute(Now)) & FillZero(Second(Now))
	videoname = "ScreenRecorder" & videowidth & "x" & videoheight & "_" & datedisp & "_" & timedisp & ".mp4"
	' 开始录制视频
	Set oExec = objShell.exec("adb -s " & sn & " shell screenrecord --size " & videowidth & "x" & videoheight & " --bit-rate " & bitrate & " /sdcard/" & videoname)
	MsgBox "正在录制视频..." & chr(10) & "点击“确定”以停止!", vbInformation, "ScreenRecorder"
	' 结束录制并等待视频文件处理完成
	oExec.Terminate
	Dim oldinfo, newinfo
	do
		WScript.Sleep 1
		oldinfo = newinfo
		newinfo = objShell.exec("adb -s " & sn & " shell ls -l /sdcard/" & videoname).Stdout.ReadAll
	loop until StrComp(oldinfo, newinfo) = 0
	WScript.Sleep 1
	' 将视频从sdcard导出并删除原文件
	objShell.Run "adb -s " & sn & " pull /sdcard/" & videoname & " " & videoname, 0, true
	'objShell.Run "adb -s " & sn & " shell rm /sdcard/" & videoname, 0, true
	' 导出成功
	ret = objShell.Popup("录制成功 (" & videoname & ")" & chr(10) & "是否打开?", 5, "ScreenRecorder", vbQuestion or vbOKCancel)
	if ret = vbOK then
		objShell.Run videoname
	end if
else
	objShell.Popup "手机连接异常", 2, "ScreenRecorder", vbExclamation
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

' 获取屏幕分辨率
function GetDisplaySize(width, height)
' 	init=1440x2560 640dpi cur=1440x2560 app=1440x2560 rng=1440x1340-2560x2460
	infos = Split(Trim(objShell.exec("adb shell dumpsys window displays | grep init=").Stdout.ReadAll), chr(32))
	if UBound(infos) > 0 and instr(infos(0), "init=") > 0 then
		size = Split(Split(infos(0), "=")(1), "x")
		width = size(0)
		height = size(1)
	else
		width = 0
		height = 0
	end if
end function
