import './side_bar_button.css'
import React from 'react';

//import { ReactComponent as Cloud} from './cloud.svg'
function side_bar_button({ name, link, icon }) {
    const Icon = React.lazy(() => import(`./icons/${icon}.svg`).then(module => ({ default: module.ReactComponent })));
    return (
        <button>
 <icon /> {name}
        </button>
    );
}
export default side_bar_button