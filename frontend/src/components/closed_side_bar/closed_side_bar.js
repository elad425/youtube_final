import './closed_side_bar.css'
import { ReactComponent as Home_icon } from './icons/home.svg'
import { ReactComponent as Shorts_icon } from './icons/shorts.svg'
import { ReactComponent as Subscriptions_icon } from './icons/subscriptions.svg'
import { ReactComponent as You_icon } from './icons/you.svg'
import { ReactComponent as History_icon } from './icons/history.svg'
function Closed_side_bar() {

    return (
        <div className="closed_side_bar">

            <div className="closed_side_bar_container">
                <div className="list">
                    <li>
                        <a href='#' className="closed_btn" >
                            <Home_icon />
                            <span className="btn-text">Home</span>
                        </a>

                    </li>
                    <li>
                        <a href='#' className="closed_btn" >
                            <Shorts_icon />
                            <span className="btn-text">Shorts</span>
                        </a>
                    </li>
                    <li>
                        <a href='#' className="closed_btn" >
                            <Subscriptions_icon />
                            <span className="btn-text">Subscriptions</span>
                        </a>
                    </li>
                    <li>
                        <a href='#' className="closed_btn" >
                            <You_icon />
                            <span className="btn-text">You</span>
                        </a>
                    </li>
                    <li>
                        <a href='#' className="closed_btn" >
                            <History_icon />
                            <span className="btn-text">History</span>
                        </a>
                    </li>
                </div>
            </div>
        </div>
    );


}
export default Closed_side_bar;