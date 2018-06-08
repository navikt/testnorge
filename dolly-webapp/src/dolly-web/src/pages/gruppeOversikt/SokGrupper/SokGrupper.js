import React, { Component } from 'react'
import InputAutocompleteField from '~/components/fields/InputAutocompleteField'
import LandKodeverk from './TEMP_kodeverk'

class SokGrupper extends Component {
	render() {
		return (
			<div>
				<h1>Søk etter testdatagrupper</h1>
				<InputAutocompleteField
					label={'Postnummer'}
					id={'postnummer-id'}
					onSelectedValue={this.onSelectedValue}
					kodeverk={LandKodeverk}
				/>
			</div>
		)
	}
}

export default SokGrupper
