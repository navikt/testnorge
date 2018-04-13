import React from 'react';
import Person from './Person';

const PersonList = ( {personer} ) => {

    const personList = personer.map(person =>
        <Person key={person.id} person={person} />
    );

    return (
        <div>
            {personList}
        </div>
    )
};

export default PersonList;