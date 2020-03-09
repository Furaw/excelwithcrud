Project installation guide
1. Open command prompt and cd to folder , where you want to locate project.
2. git clone https://github.com/Furaw/excelwithcrud.git
3. Run your IDE (preferable IntelliJIDEA) and resolve dependencies.
4. Set up mysql.
5.Create new connection
Connection Name = AndrushaConnection
Connection Method = Standart (TCP/IP)
Hostname = 127.0.0.1
Port = 3306
Username = root
Password = 1223
6.Connect via it.
7.Run mysql query 
DROP DATABASE IF EXISTS students;
create database students;

 8.Move back to Idea and run CrudApplication class.
9. Go to browser and paste this url
http://localhost:8080/      			Default url
10.This application gives you availability to add/update/delete students from page.
Import and export excel files.
Also pagination is available
