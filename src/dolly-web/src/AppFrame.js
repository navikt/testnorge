import React, {Component} from 'react';
import Header from './components/felles/header/Header';
import Sidebar from './components/felles/sidebar/Sidebar';
import './AppFrame.css';

class AppFrame extends Component {
  render() {

    const {children} = this.props;

    return (
      <div id="dolly-app">
          <Header/>
          <div id="dolly-content-outer" >
              <div id="left-content"> </div>
              <div id="dolly-content">
                  {children}
              </div>
              <div id="right-content"> </div>
          </div>
      </div>
    );
  }
}

//<Sidebar/>  Denne er over {children}

export default AppFrame;
