<h1 align="center">Hi there, I'm Igor 
<img src="https://github.com/blackcater/blackcater/raw/main/images/Hi.gif" height="32"/></h1>
<h3 align="center">QAA engineer 🇷🇺</h3>


# vk_test
Tests for such of vk api methods as likes. 

1) Clone the repository from here to your IntelljIdea
2) Go to the link https://oauth.vk.com/authorize?client_id=8207646&redirect_uri=https://oauth.vk.com/blank.html&scope=wall&response_type=code.
    Your browser will redirect your to another page with the parameter code in url there. Copy it
3) Open file /src/main/resources/application.properties in your project and find the parameter user.code(4 line). 
    Put code from the second step in there.
4) Enjoy of stability vk services.

