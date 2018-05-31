import React from 'react';

const TableRow = ({id, rowObject}) => {

    const s = Object.keys(rowObject).map(key => {
        return (
            <td>
                <div className="dolly-table-row-inner" data-flex data-layout="row" data-layout-align="start center">
                    {rowObject[key]}
                </div>
            </td>
        )
    });


    return (
        <tr className="dolly-table-row">
            {s}
        </tr>
    )

};

export default TableRow;
