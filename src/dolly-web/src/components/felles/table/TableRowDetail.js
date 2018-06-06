import React, { PureComponent } from 'react'
import PersonInfoBlock from '~/components/PersonInfoBlock/PersonInfoBlock'

export default class TableRowDetail extends PureComponent {
	render() {
		return (
			<tr className="table-row-detail">
				<td colSpan="100">
					<div className="table-row-detail_view">
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
				</td>
			</tr>
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
