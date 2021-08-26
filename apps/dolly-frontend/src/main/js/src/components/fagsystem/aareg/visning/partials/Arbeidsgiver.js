import React, { Fragment } from 'react'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'

export const Arbeidsgiver = ({ data }) => {
	if (!data || data.length === 0) return null

	return (
		<React.Fragment>
			<h4>Arbeidsgiver</h4>
			<div className="person-visning_content">
				<TitleValue title="Aktørtype" value={data.type} />
				{(data.type === 'Organisasjon' || data.type === 'ORGANISASJON') && (
					<TitleValue title="Organisasjonsnummer" value={data.organisasjonsnummer} />
				)}
				{data.type === 'Person' && (
					<TitleValue title="Arbeidsgiverident" value={data.offentligIdent} />
				)}
			</div>
		</React.Fragment>
	)
}
