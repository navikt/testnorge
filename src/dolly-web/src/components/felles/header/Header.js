import React from 'react';
import './header.css';
import {Row, Col, Glyphicon} from 'react-bootstrap';
import DropdownMenuHeader from './user_menu/DropdownMenuHeader';

export default () => {
    return (
        <div className="dolly-header">
            <Row className="header_row">
                <Col md={2}>

                </Col>

                <Col md={8} mdOffset={1} mdPull={1}>
                    <h1 className="header_title">Dolly</h1>
                </Col>

                <Col md={1} mdPull={1} className="header_dropdown">
                    <DropdownMenuHeader />
                </Col>
            </Row>
        </div>
    );
};

