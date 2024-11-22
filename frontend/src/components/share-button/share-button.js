import './share-button.css'

import { ReactComponent as Share_icon } from './share-icon.svg'

function Share_button( ) {

  
    return (
        
                <div className="dropdown">
                    <button className='share-btn btn btn-outline-primary under-btn dropdown-toggle' type="button" data-bs-toggle="dropdown" aria-expanded="false"><Share_icon />share</button>
                    <ul className="dropdown-menu">
                        <li><a className="dropdown-item" href="#">Whatsapp</a></li>
                        <li><hr className="dropdown-divider"></hr></li>
                        <li><a className="dropdown-item" href="#">Facebook</a></li>
                        <li><hr className="dropdown-divider"></hr></li>
                        <li><a className="dropdown-item" href="#">Instagram</a></li>
                    </ul>
                </div>
           
    );
}
export default Share_button