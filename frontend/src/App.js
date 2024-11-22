import './App.css';
import Home_screen from './home_screen/home_screen.js';
import Login_screen from './login_screen/login_screen.js';
import Signup_screen from './signup_screen/signup_screen.js';
import { BrowserRouter, Routes, Route } from "react-router-dom";
import React, { useState, useEffect } from 'react';
import user_data from './user_info.json';
//import Videos from './data/video_data.json';

function App() {
  const [user, setUser] = useState(null);
  const [isUserLoggedIn, setIsUserLoggedIn] = useState(false);
  useEffect(() => {
    const token = localStorage.getItem('token');
    if (token) {
        validateToken(token);
    }
}, []);

const validateToken = async (token) => {
    try {
        const response = await fetch('/api/tokens', {
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });
        if (response.ok) {
            const userData = await response.json();
            console.log("user data",userData)
            setUser(userData);
            setIsUserLoggedIn(true);
        } else {
            localStorage.removeItem('token');
        }
    } catch (error) {
        console.error('Token validation failed', error);
        localStorage.removeItem('token');
    }
};

  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Home_screen user={user} isUserLoggedIn={isUserLoggedIn} setUser={setUser} setIsUserLoggedIn={setIsUserLoggedIn} />} />

        <Route path="signup" element={<Signup_screen user={user} setUser={setUser} setIsUserLoggedIn={setIsUserLoggedIn} />} />

        <Route path="login" element={<Login_screen  setUser={setUser} user={user} setIsUserLoggedIn={setIsUserLoggedIn} />} />

      </Routes>
    </BrowserRouter>
  );
}

export default App;
