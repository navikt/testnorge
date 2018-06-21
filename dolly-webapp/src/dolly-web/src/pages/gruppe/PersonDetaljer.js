import React, { PureComponent } from 'react'
import PropTypes from 'prop-types'
import PersonInfoBlock from '~/components/personInfoBlock/PersonInfoBlock'

export default class PersonDetaljer extends PureComponent {
	static propTypes = {}

	render() {
		return (
			<div className="person-details">
				<h4>Personlig informasjon</h4>
				<PersonInfoBlock data={temp_DATA} />
				<h4>Bostedsadresse</h4>
				<PersonInfoBlock data={temp_DATA} />
				<h4>Postadresse</h4>
				<PersonInfoBlock data={temp_DATA} />

				<h4>Familierelasjoner</h4>
				<PersonInfoBlock data={temp_DATA} header="BARN" />
				<PersonInfoBlock data={temp_DATA} header="BARN" />
				<PersonInfoBlock data={temp_DATA} header="MOR" />
			</div>
		)
	}
}

const temp_DATA = [
	{
		label: 'FNR',
		value: '01234567891'
	},
	{
		label: 'FORNAVN',
		value: 'Helga'
	},
	{
		label: 'MELLOMNAVN',
		value: 'Woll'
	},
	{
		label: 'ETTERNAVN',
		value: 'Lunder'
	},
	{
		label: 'LAND',
		value: 'NO'
	},
	{
		label: 'KJØNN',
		value: 'Kvinne'
	}
]
