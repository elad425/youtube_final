import './side_bar.css'
import { ReactComponent as Home_icon } from './icons/home.svg'
import { ReactComponent as Shorts_icon } from './icons/shorts.svg'
import { ReactComponent as Subsctriptions_icon } from './icons/subscriptions.svg'
import { ReactComponent as You_icon } from './icons/you.svg'
import { ReactComponent as History_icon } from './icons/history.svg'
import { ReactComponent as Trending } from './icons/trending.svg'
import { ReactComponent as Music } from './icons/music.svg'
import { ReactComponent as Gaming } from './icons/gaming.svg'
import { ReactComponent as News } from './icons/news.svg'
import { ReactComponent as Sports } from './icons/sports.svg'
import { ReactComponent as Podcasts } from './icons/podcasts.svg'

import Sign_in_button from '../sign_in_button/sign_in_button';
function Side_bar({isUserLoggedIn}) {
   return (
      <div className="side_bar">
         <div className="side_bar_container">
            <lu className="list">
               <li className='side-bar-list-item'>
                  <button className="side_bar_button"><Home_icon /> Home</button>
               </li>
               <li className='side-bar-list-item'>
                  <button className="side_bar_button"><Shorts_icon /> Shorts</button>
               </li>
               <li className='side-bar-list-item'>
                  <button className="side_bar_button"><Subsctriptions_icon /> Subscriptions</button>
               </li>
            </lu>
         </div>
         <div className="side_bar_container">
            <lu className="list">
               <li className='side-bar-list-item'>
                  <button className="side_bar_button"><You_icon /> You</button>
               </li>
               <li className='side-bar-list-item'>
                  <button className="side_bar_button"><History_icon /> History</button>
               </li>
            </lu>
         </div>
        {!isUserLoggedIn && (
            <div className="side_bar_container">
               <lu className="list">
                  <li className='side-bar-list-item'>
                     <p className="sign_in_text">Sign in to like videos, comment, and subscribe.</p>
                  </li >
                  <li className='side-bar-list-item'>
                     <Sign_in_button />
                  </li>
               </lu>
            </div>
         )}
         <div className="side_bar_container">
            <lu className="list">
               <li className='side-bar-list-item'>
                  <h3 className="sign_in_text">Explore</h3>
               </li>
               <li className='side-bar-list-item'>
                  <button className="side_bar_button"><Trending /> Trending</button>
               </li>
               <li className='side-bar-list-item'>
                  <button className="side_bar_button"><Music /> Music</button>
               </li>
               <li className='side-bar-list-item'>
                  <button className="side_bar_button"><Gaming /> Gaming</button>
               </li>
               <li className='side-bar-list-item'>
                  <button className="side_bar_button"><News /> News</button>
               </li>
               <li className='side-bar-list-item'>
                  <button className="side_bar_button"><Sports /> Sports</button>
               </li>
               <li className='side-bar-list-item'>
                  <button className="side_bar_button"><Podcasts /> Podcasts</button>
               </li>
            </lu>
         </div>
         
      </div>
   );


}
export default Side_bar;