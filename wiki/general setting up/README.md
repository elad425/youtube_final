# General setting up.
This section shows a few things that are needed to be done before running the project.
Important things to do before running the project: setting up the mongodb ,.env file , changing android res value, changing miscellaneousController ip 

# Getting the project.

1. clone the project to your pc.  

   ![1](../pics/67.png)

2. Navigate to the backend folder using cd backend , and use npm install.  

   ![2](../pics/68.png)

3. Navigate to the frontend folder using cd frontend , and use npm install.  

   ![3](../pics/77.png)

# Uploading our videos to your mongodb.

1. Navigate to the backend folder using cd backend, and create a .env file filling in PORT:"your port" MONGO_URI="your connection string" ACCESS_TOKEN_SECRET= "any token we will show how to produce one" .  

   ![4](../pics/72.png)

2. Navigate to the main folder and use npm install mongodb.

   ![5](../pics/73.png)

3. Open importData.js and change const uri to be your uri.

   ![6](../pics/74.png)

4. Change client.db("your connection string").

   ![7](../pics/75.png)

5. Run node importData.js and you should see confirmations it uploaded the data.

   ![8](../pics/76.png)

# Producing secret token.

1. Open a terminal and insert node , then insert require('crypto').randomBytes(64).toString('hex') and copy the output.  

   ![9](../pics/78.png)

# Android set up.

1. Navigate to res/values/strings and change the baseurl to be "http://"your ip":"your port".  

   ![10](../pics/79.png)

