import React, {Component} from 'react';
import InputTextField from "../felles/fields/InputTextField";

class Person extends Component {
    constructor(props, context) {
        super(props, context);

        this.state = {
            person: this.props.person,
            expanded: false
        };

        this.toggle = this.toggle.bind(this);
        this.onInputChange = this.onInputChange.bind(this);
    }

    toggle(){
        this.setState(prevState => {
            return {expanded: !prevState.expanded}
        });
    }

    onInputChange(statePropToChange , value){
        let person = Object.assign({}, this.state.person);
        person[statePropToChange] = value;

        this.setState( {person: person });
    }

    render() {
        if(this.state.expanded){
            return (
                <div className="person-input-row">
                    <InputTextField label={"Fornavn"}
                                id={"navn-id"}
                                value={this.state.person.fornavn}
                                onChange={this.onInputChange}
                                statePropToChange={"fornavn"}/>

                    <InputTextField label={"Etternavn"}
                                id={"etternavn-id"}
                                value={this.state.person.etternavn}
                                onChange={this.onInputChange}
                                statePropToChange={"etternavn"}/>
                </div>
            )

        } else {
            return (
                <div onClick={this.toggle}>
                    --> {this.state.person.fornavn}
                </div>
            )
        }
    }
}

export default Person;

//{this.state.person.id} - {this.state.person.fornavn} - {this.state.person.fnr}
