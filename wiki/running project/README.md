# Running the Project

This section provides a step-by-step guide on how to set up the project, including the TCP server, Node.js server, and the React/Android apps.

## TCP Server Setup
1. First, open the `tcp` folder in a Linux environment and navigate to `tcp-server.cpp`.  

   ![1](../pics/1.jpg)

2. Once in `tcp-server.cpp`, simply run it by pressing `F5`.  

   ![2](../pics/2.jpg)

3. On success, a message saying "Listening on port 5555" will appear in the terminal.

   ![3](../pics/3.jpg)

4. If you want to change the port number, you can modify it in the code here: 

   ![4](../pics/4.jpg)

## Node.js Server Setup
5. Before running the Node.js server, navigate to the `backend/Controllers/miscellaneousController` file and update `serverHost` to match your Linux environment's IP address (the one running the TCP server).  

   ![13](../pics/14.jpg)

6. To open the Node.js server, open a terminal.  

   ![5](../pics/5.jpg)

7. Input `cd backend` (or navigate to the `backend` folder).  

   ![6](../pics/6.jpg)

8. Start the server by running `npm run dev`.  

   ![7](../pics/7.jpg)

9. Upon success, a message will display that the server has started on port 5000.  

   ![8](../pics/8.jpg)

10. A message will also show all the video details/user details sent to the TCP server on startup.  

    ![9](../pics/9.jpg)

## React App Setup
11. To open the React app, open a new terminal.  

    ![10](../pics/10.jpg)

12. Input `cd frontend` (or navigate to the `frontend` folder).  

    ![11](../pics/11.jpg)

13. Start the React app by running `npm start`.  

    ![12](../pics/12.jpg)

14. A new tab should automatically open with the site loaded.  

    ![14](../pics/13.jpg)

## Android App Setup
15. To open the Android app, simply open the Android app folder in the Android Studio environment and start the app on a virtual machine/phone.  

    ![15](../pics/15.jpg)
