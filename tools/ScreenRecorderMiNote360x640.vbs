set objShell = WScript.CreateObject("WScript.Shell")

' ��run��С��ִ���Լ���ʹ����execҲ�޺ڴ�������
if LCase(Right(WScript.FullName, 12)) = "\wscript.exe" then
    objShell.Run "cscript """ & WScript.ScriptFullName & chr(34), 0, false
    WScript.quit
end if

' ���ļ����ж�ȡ���ű�������
' ScreenRecorder#0.25#rotate.vbs ������scale=0.5,��Ƶ��ת90��
' ScreenRecorder#0.25.vbs ������scale=0.5,��Ƶ����ת
' ScreenRecorder.vbs ������scale=1.0,��Ƶ����ת
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

' �鿴�ֻ�����
' List of devices attached
' e3ad37fb        device
devices = Split(objShell.exec("adb devices").Stdout.ReadAll, chr(10))

' ��ȡ��Ļ�ֱ���
dim width, height
if StrComp(LCase(rotate), "rotate") <> 0 then
	GetDisplaySize width, height
else
	GetDisplaySize height, width
end if

' ������Ƶ��߼�������
videowidth = 360'width * scale
videoheight = 640'height * scale
bitrate = videowidth * videoheight * 15


if ubound(devices) > 0 and instr(devices(1), "device") > 0 and width > 0 and height > 0 then
	' ��ȡ�豸sn
	sn = Split(devices(1), chr(9))(0)
	' ��Ƶ�ļ��� ScreenRecorder_20160211_172118.mp4
	datedisp = Year(Now) & FillZero(Month(Now)) & FillZero(Day(Now))
	timedisp = FillZero(Hour(Now)) & FillZero(Minute(Now)) & FillZero(Second(Now))
	videoname = "ScreenRecorder" & videowidth & "x" & videoheight & "_" & datedisp & "_" & timedisp & ".mp4"
	' ��ʼ¼����Ƶ
	Set oExec = objShell.exec("adb -s " & sn & " shell screenrecord --size " & videowidth & "x" & videoheight & " --bit-rate " & bitrate & " /sdcard/" & videoname)
	MsgBox "����¼����Ƶ..." & chr(10) & "�����ȷ������ֹͣ!", vbInformation, "ScreenRecorder"
	' ����¼�Ʋ��ȴ���Ƶ�ļ��������
	oExec.Terminate
	Dim oldinfo, newinfo
	do
		WScript.Sleep 1
		oldinfo = newinfo
		newinfo = objShell.exec("adb -s " & sn & " shell ls -l /sdcard/" & videoname).Stdout.ReadAll
	loop until StrComp(oldinfo, newinfo) = 0
	WScript.Sleep 1
	' ����Ƶ��sdcard������ɾ��ԭ�ļ�
	objShell.Run "adb -s " & sn & " pull /sdcard/" & videoname & " " & videoname, 0, true
	'objShell.Run "adb -s " & sn & " shell rm /sdcard/" & videoname, 0, true
	' �����ɹ�
	ret = objShell.Popup("¼�Ƴɹ� (" & videoname & ")" & chr(10) & "�Ƿ��?", 5, "ScreenRecorder", vbQuestion or vbOKCancel)
	if ret = vbOK then
		objShell.Run videoname
	end if
else
	objShell.Popup "�ֻ������쳣", 2, "ScreenRecorder", vbExclamation
end if

WScript.quit

' ������λ���ַ���ǰ�油0
function FillZero(src)
	if len(src) = 1 then
		FillZero = "0" & src
	else
		FillZero = src
	end if
end function

' ��ȡ��Ļ�ֱ���
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
