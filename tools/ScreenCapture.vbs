set objShell = WScript.CreateObject("WScript.Shell")

' ��run��С��ִ���Լ���ʹ�ú���execҲ�޴�������
if LCase(Right(WScript.FullName, 12)) = "\wscript.exe" then
    objShell.Run "cscript """ & WScript.ScriptFullName & chr(34), 0, false
    WScript.quit
end if

' �鿴�ֻ�����
' List of devices attached
' e3ad37fb        device
devices = Split(objShell.exec("adb devices").Stdout.ReadAll, chr(10))

if ubound(devices) > 0 and instr(devices(1), "device") > 0 then
	' ��ȡ�豸sn
	sn = Split(devices(1), chr(9))(0)
	' ��ͼ�ļ��� ScreenCapture_20160211_172118.png
	datedisp = Year(Now) & FillZero(Month(Now)) & FillZero(Day(Now))
	timedisp = FillZero(Hour(Now)) & FillZero(Minute(Now)) & FillZero(Second(Now))
	imgname = "ScreenCapture_" & datedisp & "_" & timedisp & ".png"
	' ��ʼ��ͼ
	objShell.Run "adb -s " & sn & " shell screencap -p > /sdcard/" & imgname, 0, true
	objShell.Run "adb -s " & sn & " pull /sdcard/" & imgname & " " & imgname, 0, true
	objShell.Run "adb -s " & sn & " shell rm /sdcard/" & imgname, 0, true
	ret = objShell.Popup("��ͼ�ɹ� (" & imgname & ")" & chr(10) & "�Ƿ��?", 3, "ScreenCapture.vbs", vbQuestion or vbOKCancel)
	if ret = vbOK then
		objShell.Run imgname
	end if
else
	objShell.Popup "���ֻ�����", 2, "ScreenCapture.vbs", vbExclamation
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