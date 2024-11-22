import './user_logo.css';
import React, { useState } from 'react';

import Signup_input from '../signup_input/signup_input';
import { useNavigate } from 'react-router-dom';
import { checkEmailExists } from '../../utils/apiUtils';

function User_logo({ user, setUser, setIsUserLoggedIn ,returnToHome,resetVideos}) {
    const [showModal, setShowModal] = useState(false);
    const [username, setUsername] = useState(user.username);
    const [email, setEmail] = useState(user.email);
    const [password, setPassword] = useState('');
    const [pictureFile, setPictureFile] = useState(null);

    const navigate = useNavigate();
    const logOut = () => {
        setUser(null);
        setIsUserLoggedIn(false);
        localStorage.removeItem('token');
        setTimeout(() => {
            navigate('/');
            resetVideos();
            returnToHome();
        }, 300);
        
    }

    const handleUpload = (e) => {
        setPictureFile(e.target.files[0]);
    }

    const editDetails = async () => {
        const forms = document.querySelectorAll('.needs-validation') 
        forms.forEach(form => {
            form.classList.remove('is-invalid')
        })
        const token = localStorage.getItem('token');
        const formData = new FormData();
        formData.append('file', pictureFile);
        let picPath = ''
        if (pictureFile != null) {
            try {
                const response = await fetch(`/upload`, {
                    method: 'POST',
                    body: formData,
                });
                const json = await response.json();
                picPath = json.path

            } catch (error) {
                console.log("User Creation failed", error);
            }

            try {
                const fileData =
                {
                    file: user.icon
                }
                const response = await fetch(`/api/misc/deleteFile`, {
                    method: 'DELETE',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': `Bearer ${token}`
                    },
                    body: JSON.stringify(fileData)
                });
                if (response.ok) {
                    console.log("file successfully deleted")
                }
            } catch (error) {
                console.log("Deleting video failed", error);
            }
        }
        const regex = /^(?=.*[A-Za-z])(?=.*\d)/;
        var email = document.getElementById("email_edit").value;
        const emailExists = await checkEmailExists(email); // Await the email check
        if (emailExists) {
            document.getElementById('email_edit').classList.add('is-invalid');
            return; // Exit if email exists
        }
        var password = document.getElementById("password_edit").value;
        var username = document.getElementById("user_name_edit").value;
        let data = {};
        if (username != '') data.username = username;
        if (email != '') data.email = email;
        if (password != '') data.password = password;
        if (picPath != '') data.icon = picPath;
        if (password!=''&&!regex.test(password)){
            document.getElementById('password_edit').classList.add('is-invalid');
            return; 
        }
        try {
            const response = await fetch(`/api/users/${user._id}`, {
                method: 'PATCH',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`

                },
                body: JSON.stringify(data)
            });
            const json = await response.json();
            if (response.ok) {
                setUser(json);
                setShowModal(false); // Close the modal on success
            }
        } catch (error) {
            console.log("Editing user failed", error);
        }
    }
    const deleteUser = async () => {
        const token = localStorage.getItem('token');
        try {
            const response = await fetch(`/api/users/${user._id}`, {
                method: 'DELETE',
                headers: {
                    'Authorization': `Bearer ${token}`
                }
            });
            if (response.ok) {
                console.log("user successfully deleted")
            }
        } catch (error) {
            console.log("Deleting user failed", error);
        }
        setUser(null);
        setIsUserLoggedIn(false);
        localStorage.removeItem('token');
        navigate('/');
    }

    return (
        <div className='user-logo-container'>
            <button className='user-logo-btn' data-bs-toggle="dropdown" aria-expanded="false">
                <img src={user.icon} className='user-logo-image' alt="User Profile"></img>
            </button>
            <ul className="dropdown-menu">
                <li><a className="dropdown-item" href="#" onClick={logOut}>Log Out</a></li>
                <li><a className="dropdown-item" href="#" data-bs-toggle="modal" data-bs-target="#editUserModal">Edit details</a></li>
                <li><a className="dropdown-item" href="#" onClick={deleteUser}>Delete</a></li>
            </ul>
            <div className={`modal ${showModal ? 'show' : ''}`} style={{ display: showModal ? 'block' : 'none' }} id="editUserModal" tabindex="-1" aria-labelledby="exampleModalLabel" aria-hidden="true">
                <div className="modal-dialog">
                    <div className="modal-content">
                        <div className="modal-header">
                            <h1 className="modal-title fs-5" id="exampleModalLabel">Edit your details</h1>
                            <button type="button" className="btn-close" data-bs-dismiss="modal" aria-label="Close" onClick={() => setShowModal(false)}></button>
                        </div>
                        <div className="modal-body">
                            <Signup_input id="user_name_edit" placeholder="User name" value={username} onChange={(e) => setUsername(e.target.value)} />
                            <Signup_input id="email_edit" placeholder="Email address" invalid="invalid email adress or email already exists" value={email} onChange={(e) => setEmail(e.target.value)} />
                            <Signup_input id="password_edit" placeholder="Password" invalid="password need to contain at least 8 characters with letters and numbers" value={password} onChange={(e) => setPassword(e.target.value)} />
                            <div className="mb-1">
                                <small className="">Select profile picture</small>
                                <input className="form-control needs-validation form-control-lg bg-light fs-6" type="file" id="picture_edit" onChange={handleUpload}></input>
                                <div className="invalid-feedback">
                                    Missing picture
                                </div>
                            </div>
                        </div>
                        <div className="modal-footer">
                            <button type="button" className="btn btn-secondary" data-bs-dismiss="modal" onClick={() => setShowModal(false)}>Close</button>
                            <button type="button" className="btn btn-primary" onClick={editDetails}>Save changes</button>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
}

export default User_logo;
