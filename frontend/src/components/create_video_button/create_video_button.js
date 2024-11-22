import { ReactComponent as Create } from './create.svg'
import './create_video_button.css'
import React from 'react';
import {Link} from 'react-router-dom'

function Create_video_button({onClick,user}) {
 
  return (
    user? (
      <button className="create-btn" onClick={onClick}><Create /></button>
    ) : (
      <Link to="/signup">
        <button className="create-btn"><Create /></button>
      </Link>
    )
  );
}
export default Create_video_button;

