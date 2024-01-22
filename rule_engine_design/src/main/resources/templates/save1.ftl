<!DOCTYPE html>  
<html>  
<head>  
	<!--坑比-->
	<!--<meta http-equiv="Access-Control-Allow-Origin" content="*">-->
	<meta charset="UTF-8"> 
	<link href="/css/common.css" rel="stylesheet" /> 
	<script src="/js/jquery-3.1.0.js"></script>  
	  
</head>  
  
<body>  
	<h2>HTML5异步上传文件，带进度条</h2>
	  
	<form id="uf" method="post" enctype="multipart/form-data">  
		username：<input type="text" name="username"/><br/>	
		选择要上传的qr文件：<br/>  
		<input type="file" name="qrFile" /><span></span><br/>  		 
	</form>  
	  
	<br/><br/>  
	<input type="button" value="上传吧" onclick="upload()"/>  
	<br/><br/>  
	上传进度：<progress></progress><br/>  
	<p id="progress">0 bytes</p>  
	<p id="info"></p>  
</body>  
	<script>  
	
		function upload1() {
			$("#uf").submit();
		}  
	
        var totalSize = 0;  
          
        //绑定所有type=file的元素的onchange事件的处理函数  
        $(':file').change(function() {  
            var file = this.files[0]; //假设file标签没打开multiple属性，那么只取第一个文件就行了  
            name = file.name;  
            size = file.size;  
            type = file.type;  
            url = window.URL.createObjectURL(file); //获取本地文件的url，如果是图片文件，可用于预览图片  
              
            $(this).next().html("文件名：" + name + " 文件类型：" + type + " 文件大小：" + size + " url: " + url);  
              
            totalSize += size;  
              
            $("#info").html("总大小: " + totalSize + "bytes");  
              
        });  
      
        function upload() {  
            //创建FormData对象，初始化为form表单中的数据。需要添加其他数据可使用formData.append("property", "value");  
            var formData = new FormData($('form')[0]);  
              
            //ajax异步上传  
            $.ajax({  
			    //Access-Control-Allow-Origin: '*', 坑！跨域无效！
                url: "/ruleModelRelease/uploadRuleModelFile",  
                type: "POST",  
                data: formData,  
				//dataType:'JSONP',
                xhr: function(){ //获取ajaxSettings中的xhr对象，为它的upload属性绑定progress事件的处理函数  
                  
                    myXhr = $.ajaxSettings.xhr();  
                    if(myXhr.upload){ //检查upload属性是否存在  
                        //绑定progress事件的回调函数  
                        myXhr.upload.addEventListener('progress',progressHandlingFunction, false);   
                    }  
                    return myXhr; //xhr对象返回给jQuery使用  
                },  
                success: function(result){  
                    $("#result").html(result.data);  
                },  
                contentType: false, //必须false才会自动加上正确的Content-Type  
                processData: false  //必须false才会避开jQuery对 formdata 的默认处理  
            });  
        }         
      
        //上传进度回调函数：  
        function progressHandlingFunction(e) {  
            if (e.lengthComputable) {  
                $('progress').attr({value : e.loaded, max : e.total}); //更新数据到进度条  
                var percent = e.loaded/e.total*100;  
                $('#progress').html(e.loaded + "/" + e.total+" bytes. " + percent.toFixed(2) + "%");  
            }  
        }  
    </script>  

</html>  