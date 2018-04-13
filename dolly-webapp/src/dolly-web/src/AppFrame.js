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
          <div id="dolly-content">
              <Sidebar/>
              {children}
          </div>
      </div>
    );
  }
}

export default AppFrame;
