set objArgs = WScript.Arguments
set objFs = WScript.CreateObject("Scripting.FileSystemObject")
set objImgFile = WScript.CreateObject("WIA.ImageFile")
set objImgProcess = WScript.CreateObject("WIA.ImageProcess")

dim scale

dim leftPoint
dim rightPoint
dim topPoint
dim bottomPoint

' ���ļ����ж�ȡ�и�����ϽǺ����½�����
' ClipImage#10;100;300;500.vbs ���������Ͻ�����Ϊ(10, 100), ���½�����Ϊ(300, 500)
scriptFullName = WScript.ScriptFullName
scriptName = Right(scriptFullName, Len(scriptFullName) - InStrRev(scriptFullName, "\"))
scriptName = Left(scriptName, Len(scriptName) - 4)
scriptNameInfos = Split(scriptName, "#")
if UBound(scriptNameInfos) = 1 then
	clipInfos = Split(scriptNameInfos(1), ";")
	if UBound(clipInfos) = 3 then
		leftPoint = clipInfos(0)
		topPoint = clipInfos(1)
		rightPoint = clipInfos(2)
		bottomPoint = clipInfos(3)
	end if
end if

' ���ֲ������
dim checkarg
checkarg = false
if objArgs.Count > 0 then
	srcImgName = objArgs(0)
	if objFs.FileExists(srcImgName) then
		on error resume next
		FormatNumber leftPoint, 0, true
		if Err.Number = 0 then
			on error resume next
			FormatNumber topPoint, 0, true
			if Err.Number = 0 then
				on error resume next
				FormatNumber rightPoint, 0, true
				if Err.Number = 0 then
					on error resume next
					FormatNumber bottomPoint, 0, true
					if Err.Number = 0 then
						checkarg = true
					end if
				end if
			end if
		end if
		if checkarg = true then
			objImgFile.LoadFile srcImgName
			leftPoint = leftPoint + 0
			topPoint = topPoint + 0
			rightPoint = rightPoint + 0
			bottomPoint = bottomPoint + 0
			if leftPoint < 0 Or topPoint < 0 Or rightPoint > objImgFile.Width Or bottomPoint > objImgFile.Height then
				MsgBox "��������λ�ò��Ϸ�!", vbExclamation, "ClipImage.vbs"
				checkarg = false
			end if
		else
			MsgBox "���������ʽ����!", vbExclamation, "ClipImage.vbs"
		end if
	else
		MsgBox "ԴͼƬ������!" & Chr(10) & srcImgName , vbExclamation, "ClipImage.vbs"
	end if
else
	MsgBox "�뽫ԴͼƬ�ϵ�vbs��!", vbExclamation, "ClipImage.vbs"
end if

' �������δͨ����ֱ���˳�
if checkarg = false then
	WScript.Quit
end if

destImgRange = "(" & leftPoint & "," & topPoint & ")-(" & rightPoint & "," & bottomPoint & ")"
destImgName = Left(srcImgName, Len(srcImgName) - 4) & "_clipped_" & destImgRange & Right(srcImgName, 4)
if objFs.FileExists(destImgName) then
	handle = MsgBox("Ŀ��ͼƬ�Ѵ���, �Ƿ�ɾ��?" & Chr(10) & destImgName, vbOKCancel or vbExclamation, "ClipImage.vbs")
	if handle = vbOK then
		objFs.DeleteFile(destImgName)
	end if
end if
if not objFs.FileExists(destImgName) then
	objImgProcess.Filters.Add objImgProcess.FilterInfos("Crop").FilterID
	objImgProcess.Filters(1).Properties("Left") = leftPoint
	objImgProcess.Filters(1).Properties("Top") = topPoint
	objImgProcess.Filters(1).Properties("Right") = objImgFile.Width - rightPoint
	objImgProcess.Filters(1).Properties("Bottom") = objImgFile.Height - bottomPoint
	set objImgFile = objImgProcess.Apply(objImgFile)
	objImgFile.SaveFile destImgName
	MsgBox "�ѱ��浽" & destImgName, vbInformation, "ClipImage.vbs"
end if




	
	




