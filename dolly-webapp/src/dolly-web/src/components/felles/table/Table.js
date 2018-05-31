import React from 'react';
import TableRow from './TableRow';
import './table.css'


const Table = ({id, tableObjects}) => {

    const oby = [
        {"Navn": 'dust', "Team": 'FREG', "Eier": "Holene, Axel" ,"Hensikt": 'Hensikten med dette er å hh', "Personer": "30", "Miljø": "t5, t6, t7"},
        {"Navn": 'bra', "Team": 'FO', "Eier": "Fløgstad, Peter", "Hensikt": 'Hensikten loooooool', "Personer": "60", "Miljø": "t5, t6, t7"}
    ];

    tableObjects = oby;

    const rows = (
        <tbody>
        {
            tableObjects.map(obj =>
                <TableRow rowObject={obj} key={obj.id}/>
            )
        }
        </tbody>
    );

    const headers = (
        <thead className="dolly-table-header">
        <tr>
            {Object.keys(tableObjects[0]).map(objKey => <th>{objKey}</th>)}
        </tr>
        </thead>
    );

    let addRow = null;
    if (false) {
        addRow = <div>Hei</div>
    }

    return (
        <div className="dolly-table-container" id={id}>
            <table className="dolly-table">

                {headers}

                {addRow}

                {rows}
            </table>
        </div>
    )
};

export default Table;