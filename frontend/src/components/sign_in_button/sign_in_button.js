import { ReactComponent as Person_circle} from './person_circle.svg'
import './sign_in_button.css'
import {Link} from 'react-router-dom'

function Sign_in_button() {
return (
    <div className ="button_div">
      <Link to="/login"><button className="sign_in_button"><Person_circle />sign in</button></Link>
    </div>
     
    );
}
export default Sign_in_button;

