<!DOCTYPE html>
<html>
  <head>
  <title>html5_2.html</title>
  <meta http-equiv="content-type" content="text/html; charset=UTF-8">
  
  <style type="text/css">  
    #parent{width:550px; height:10px; border:2px solid #09F;}  
    #son {width:0; height:100%; background-color:#09F; text-align:center; line-height:10px; font-size:20px; font-weight:bold;}  
  </style>
  
  <script src="/js/jquery-3.1.0.js"></script>
  
  <script type="text/javascript">
	  function showPic(){
	    var pic = $("#pic").get(0).files[0];
	    $("img").prop("src" , window.URL.createObjectURL(pic) );
	    uploadPic();
	  }
	  function uploadPic(){
	    var pic = $("#pic").get(0).files[0];
	    var formData = new FormData();
	    formData.append("file" , pic);
	    /**   
	     * 必须false才会避开jQuery对 formdata 的默认处理   
	     * XMLHttpRequest会对 formdata 进行正确的处理   
	     */ 
	    $.ajax({
	       type: "POST",
	       url: "http://localhost:8080/netty/uploadPic",
	       data: formData ,
	       processData : false,  
	       //必须false才会自动加上正确的Content-Type   
	       contentType : false , 
	       xhr: function(){
	        var xhr = $.ajaxSettings.xhr();
	        if(onprogress && xhr.upload) {
	          xhr.upload.addEventListener("progress" , onprogress, false);
	          return xhr;
	        }
	      } 
	    });
	  }
	  /**
	   *	侦查附件上传情况	,这个方法大概0.05-0.1秒执行一次
	   */
	  function onprogress(evt){
	    var loaded = evt.loaded;				  //已经上传大小情况 
	    var tot = evt.total;					  //附件总大小 
	    var per = Math.floor(100*loaded/tot);	  //已经上传的百分比  
	    $("#son").html( per +"%" );
	    $("#son").css("width" , per +"%");
	  }
  </script>
  
  </head>
  <body>
  <img  width="400" height="250"/><br />  
  <input type="file" id="pic" name="pic"/>
  <input type="button" value="上传图片" onclick="uploadPic()" /><br />  
  <div id="parent">
    <div id="son"></div>
  </div> 
  </body>
</html>