import React, { PureComponent } from 'react'
import LinkButton from '~/components/button/LinkButton/LinkButton'

export default class FilledAdresse extends PureComponent {
	render() {
		const { values } = this.props
		return (
			<div>
				{values.boadresse_gateadresse && (
					<p>
						{values.boadresse_gateadresse}
						{values.boadresse_husnummer && ' ' + values.boadresse_husnummer}
						{values.boadresse_postnr && ' - ' + 'Postnummer: ' + values.boadresse_postnr}
					</p>
				)}
				{values.boadresse_kommunenr && <p>Kommunenummer: {values.boadresse_kommunenr}</p>}
				{values.boadresse_flyttedato && <p>Flyttedato: {values.boadresse_flyttedato}</p>}

				<LinkButton text="Endre" onClick={() => this._onClickNyAdresse(values)} />
			</div>
		)
	}

	_onClickNyAdresse = values => {
		const { setValues } = this.props

		setValues({
			values: {
				...values,
				boadresse_gateadresse: '',
				boadresse_husnummer: '',
				boadresse_kommunenr: '',
				boadresse_gatekode: ''
			},
			keepPage: true
		})
	}
}
