set objArgs = WScript.Arguments
set objFs = WScript.CreateObject("Scripting.FileSystemObject")
set objImgFile = WScript.CreateObject("WIA.ImageFile")
set objImgProcess = WScript.CreateObject("WIA.ImageProcess")

dim scale

dim leftPoint
dim rightPoint
dim topPoint
dim bottomPoint

' 从文件名中读取切割的左上角和右下角坐标
' ClipImage#10;100;300;500.vbs 即代表左上角坐标为(10, 100), 右下角坐标为(300, 500)
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

' 各种参数检查
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
				MsgBox "输入坐标位置不合法!", vbExclamation, "ClipImage.vbs"
				checkarg = false
			end if
		else
			MsgBox "输入坐标格式错误!", vbExclamation, "ClipImage.vbs"
		end if
	else
		MsgBox "源图片不存在!" & Chr(10) & srcImgName , vbExclamation, "ClipImage.vbs"
	end if
else
	MsgBox "请将源图片拖到vbs上!", vbExclamation, "ClipImage.vbs"
end if

' 参数检查未通过，直接退出
if checkarg = false then
	WScript.Quit
end if

destImgRange = "(" & leftPoint & "," & topPoint & ")-(" & rightPoint & "," & bottomPoint & ")"
destImgName = Left(srcImgName, Len(srcImgName) - 4) & "_clipped_" & destImgRange & Right(srcImgName, 4)
if objFs.FileExists(destImgName) then
	handle = MsgBox("目标图片已存在, 是否删除?" & Chr(10) & destImgName, vbOKCancel or vbExclamation, "ClipImage.vbs")
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
	MsgBox "已保存到" & destImgName, vbInformation, "ClipImage.vbs"
end if




	
	




