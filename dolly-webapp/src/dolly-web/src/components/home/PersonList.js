import React from 'react';

const PersonList = ( {
    persons
}) => {

    const personList = persons.map(person =>
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