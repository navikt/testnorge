import React, { Component } from 'react'
import Autocomplete from '~/components/fields/Autocomplete/Autocomplete'
import LandKodeverk from './TEMP_kodeverk'

export default class Sok extends Component {
	render() {
		return (
			<div>
				<h1>Søk etter testdatagrupper</h1>
				<Autocomplete
					label={'Postnummer'}
					id={'postnummer-id'}
					onSelectedValue={this.onSelectedValue}
					kodeverk={LandKodeverk}
				/>
			</div>
		)
	}
}
