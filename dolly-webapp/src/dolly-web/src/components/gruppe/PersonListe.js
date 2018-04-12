import React from 'react';

const PersonList = ( {personer} ) => {

    const personList = personer.map(person =>
        <li key={person.id}>
            {person.fornavn}
        </li>
    );

    return (
        <div>
            <ul>{personList}</ul>

        </div>
    )
};

export default PersonList;