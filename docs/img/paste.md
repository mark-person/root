

~~~html

<!DOCTYPE html>
<html lang="utf-8">
<head>
  <meta charset="UTF-8">
  <title>Document</title>
</head>
<body>
  <div id="div" style="width:100%;height:700px;border:1px solid #ccc;padding:20px" contenteditable="true"></div>
  <script>
    var div = document.getElementById('div');
    div.addEventListener('paste', function(e) {
      if(e.clipboardData) {
        for(var i = 0; i < e.clipboardData.items.length; i++) {
          var c = e.clipboardData.items[i];
          var f = c.getAsFile();
          var reader = new FileReader();
          reader.onload = function(e) {
            div.innerHTML += '<img src="' + e.target.result + '">';
          }
          reader.readAsDataURL(f);
        }
      }
    });
  </script>
</body>
</html>

~~~