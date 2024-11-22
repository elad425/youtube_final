import { useNavigate, Link } from 'react-router-dom';
import { useState } from 'react';
import './login_screen.css';

function Login_screen({ setUser, setIsUserLoggedIn }) {
  const navigate = useNavigate();
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');

  const handleLogin = async () => {
    const userData = {
      email:email,
      password:password
    };
    try {
      
      const response = await fetch('/api/users/login', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(userData)
      });

      const data = await response.json();

      if (response.ok) {
        localStorage.setItem('token', data.token);
        setUser(data.user);
        setIsUserLoggedIn(true);
        navigate('/');
      } else {
        document.getElementById('email').classList.add('is-invalid');
        document.getElementById('password').classList.add('is-invalid');
      }
    } catch (error) {
      console.error('Login failed', error);
    }
  };

  return (
    <div className="login">
      <div className="container d-flex justify-content-center align-items-center vh-100">
        <div className="row border rounded-2 p-2 bg-light shadow box-area">
          <div className="col-md-6 d-flex justify-content-center align-items-center flex-column left-box">
            <div className="featured-image mb-3">
              <img src="youtube.png" className="img-fluid p-4" alt="Responsive image"></img>
            </div>
          </div>
          <div className="col-md-6 right-box">
            <div className="row align-items-center">
              <div className="header-text mb-4">
                <h1>Hello</h1>
                <p>Please verify yourself</p>
              </div>
              <div className="input-group mb-3">
                <input
                  type="text"
                  className="form-control needs-validation form-control-lg bg-light fs-6"
                  id="email"
                  placeholder="Email address"
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                />
              </div>
              <div className="input-group mb-1">
                <input
                  type="password"
                  className="form-control needs-validation form-control-lg bg-light fs-6"
                  id="password"
                  placeholder="Password"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                />
                <div className="invalid-feedback">
                  Wrong email or password.
                </div>
              </div>
              <div className="input-group mb-2 pt-4">
                <button
                  className="btn btn-lg btn-primary w-100 submit-btn"
                  onClick={handleLogin}
                  type='submit'
                >
                  Login
                </button>
              </div>
              <div className="row">
                <div className="col">
                  <small>Don't have an account? <Link to="/signup" className='small-a'>Sign up</Link></small>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

export default Login_screen;