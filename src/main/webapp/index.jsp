<html>
<head>
<link rel="stylesheet" href="css/bootstrap.min.css" />
<link rel="stylesheet" href="css/bootstrap-theme.min.css" />
<link rel="stylesheet" href="css/athena-style.css" />
<script type="text/javascript" src="js/angular.min.js"></script>
<script type="text/javascript" src="js/bootstrap.min.css"></script>
<script type="text/javascript" src="modules/AthenaApp.js"></script>
</head>
<body ng-app="AthenaApp">
	<h2>Athena-Core</h2>
	<div ng-view />
</body>
<script type="text/ng-template" id="addJar.htm">
<form action="services/upload/jar" enctype="multipart/form-data" method="post">
	<input type="file" name="file">
	<input type="submit">
</form>
   </script>
<script type="text/ng-template" id="login.htm">
      <h2> Add Student </h2>
      {{message}}
   </script>
</html>