set objArgs = WScript.Arguments
set objFs = WScript.CreateObject("Scripting.FileSystemObject")
set imgFile = WScript.CreateObject("WIA.ImageFile")
set imgProcess = WScript.CreateObject("WIA.ImageProcess")

dim scale

' 从文件名中读取缩放比例参数
' ScaleImage#0.25.vbs 即代表scale=0.25
scriptFullName = WScript.ScriptFullName
scriptName = Right(scriptFullName, Len(scriptFullName) - InStrRev(scriptFullName, "\"))
scriptName = Left(scriptName, Len(scriptName) - 4)
scriptNameInfos = Split(scriptName, "#")
if UBound(scriptNameInfos) = 1 then
	scale = scriptNameInfos(1)
end if
on error resume next
	FormatNumber scale, 2, true
if err.number<>0 then
	scale = 1.0
end if

if objArgs.Count > 0 then
	srcImgName = objArgs(0)
	if objFs.FileExists(srcImgName) then
		destImgName = Left(srcImgName, Len(srcImgName) - 4) & "_scaled_" & FormatNumber(scale, 2, true) & Right(srcImgName, 4)
		if objFs.FileExists(destImgName) then
			handle = MsgBox("目标图片已存在, 是否删除?" & Chr(10) & destImgName, vbOKCancel or vbExclamation, "ScaleImage.vbs")
			if handle = vbOK then
				objFs.DeleteFile(destImgName)
			end if
		end if
		if not objFs.FileExists(destImgName) then
			imgFile.LoadFile srcImgName
			msgscale = imgFile.Width & "*" & imgFile.Height & " >>> " 
			imgProcess.Filters.Add imgProcess.FilterInfos("Scale").FilterID
			imgProcess.Filters(1).Properties("MaximumWidth") = imgFile.Width * scale
			imgProcess.Filters(1).Properties("MaximumHeight") = imgFile.Height * scale
			set imgFile = imgProcess.Apply(imgFile)
			imgFile.SaveFile destImgName
			msgscale = msgscale & imgFile.Width & "*" & imgFile.Height
			MsgBox "已保存到" & destImgName & Chr(10) & msgscale, vbInformation, "ScaleImage.vbs"
		end if
	else
		MsgBox "源图片不存在!" & Chr(10) & srcImgName , vbExclamation, "ScaleImage.vbs"
	end if
else
	MsgBox "请将源图片拖到vbs上!", vbExclamation, "ScaleImage.vbs"
end if


