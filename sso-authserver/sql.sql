--  服务认证注册表
DROP TABLE IF EXISTS authserver.oauth2_client;
CREATE TABLE authserver.oauth2_client (
  id INT AUTO_INCREMENT PRIMARY KEY,
  clientId VARCHAR(50),
  clientSecret VARCHAR(50),
  redirectUrl VARCHAR(2000),
  grantType VARCHAR(100),
  scope VARCHAR(100)
);
-- 创建一个注册服务
INSERT INTO authserver.oauth2_client(clientId, clientSecret, redirectUrl, grantType, scope)
VALUES ('clientId','clientSecret','http://192.168.1.126:8085/getTokenForAuthorizationCode.html,http://www.csdn.net', 'authorization_code,client_credentials,password,implicit', 'scope1,scope2');

-- 创建一个用户表
DROP TABLE IF EXISTS authserver.oauth2_user;
CREATE TABLE authserver.oauth2_user (
  id INT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(50),
  PASSWORD VARCHAR(50)
);

INSERT INTO authserver.oauth2_user (username, PASSWORD)
VALUES ('admin','admin');

INSERT INTO authserver.oauth2_user (username, PASSWORD)
VALUES ('guest','guest');