import React from 'react';
import { DropdownButton, MenuItem, Glyphicon} from 'react-bootstrap';
import {NavLink} from 'react-router-dom';

class DropdownMenuHeader extends React.Component {
    render() {
        const icon = <Glyphicon glyph="glyphicon glyphicon-user" />;

        return (
              <DropdownButton
                  bsSize="small"
                  bsStyle="btn-primary"
                  title={icon}
                  key={1}
                  id="btn-id"
              >
                  <MenuItem eventKey="1">
                      Link 1
                  </MenuItem>
                  <MenuItem eventKey="2">Link 2</MenuItem>
                  <MenuItem eventKey="3">
                      Link 3
                  </MenuItem>
                  <MenuItem divider />
                  <MenuItem eventKey="4">
                      <NavLink exact to="/gruppe/81">
                          <Glyphicon glyph="	glyphicon glyphicon-log-in"/>&nbsp; Logg inn
                      </NavLink>
                  </MenuItem>
              </DropdownButton>
        );
    }
}

export default DropdownMenuHeader;