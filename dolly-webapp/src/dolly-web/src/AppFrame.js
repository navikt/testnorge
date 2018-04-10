import React from 'react';
import Header from './components/felles/header/Header';
import Sidebar from './components/felles/sidebar/Sidebar';
import './AppFrame.css';

//Dette er main klassen som skal stå rundt hva som skal routes, --
// -- den må derfor ikke
class AppFrame extends React.Component {
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
