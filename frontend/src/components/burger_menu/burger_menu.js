import { ReactComponent as BurgerMenu } from './burger_menu.svg'
import './burger_menu.css'

function Burger_Menu({ toggleSidebar }) {
  return (
    <div className="button_div">
      <button className="burger_menu" onClick={toggleSidebar}>
        <BurgerMenu />
      </button>
    </div>
  );
}
export default Burger_Menu;

