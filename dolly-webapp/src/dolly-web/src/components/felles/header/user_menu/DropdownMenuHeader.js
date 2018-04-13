import React from 'react';
import { DropdownButton, MenuItem, Glyphicon} from 'react-bootstrap';


class DropdownMenuHeader extends React.Component {

    render() {
        const icon = <Glyphicon glyph="glyphicon glyphicon-user" />;

        return (
              <DropdownButton
                  bsSize="small"
                  bsStyle="default"
                  title={icon}
                  key={1}
                  id="btn-id"
              >
                  <MenuItem eventKey="1" href="/#">Link 1</MenuItem>
                  <MenuItem eventKey="2" href="/#">Link 2</MenuItem>
                  <MenuItem eventKey="3" href="/#">
                      Link 3
                  </MenuItem>
                  <MenuItem divider />
                  <MenuItem eventKey="4" href="/#">
                      <Glyphicon glyph="glyphicon glyphicon-log-in"/>&nbsp; Logg inn
                  </MenuItem>
              </DropdownButton>
        );
    }
}

export default DropdownMenuHeader;