import React, { Fragment } from 'react'
import Loading from '~/components/ui/loading/Loading'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'

export const Arbeidsgiver = ({ data, loading }) => {
	if (loading) return <Loading label="laster Aareg-data" />
	if (!data || data.length === 0) return false

	return (
		<div>
			<h4>Arbeidsgiver</h4>
			<div className="person-visning_content">
				{data.type === 'Organisasjon' && (
					<Fragment>
						<TitleValue title="Aktørtype" value={data.type} />
						<TitleValue title="Antall timer per uke" value={data.organisasjonsnummer} />
					</Fragment>
				)}
				{data.type === 'Person' && (
					<Fragment>
						<TitleValue title="Aktørtype" value={data.type} />
						<TitleValue title="Antall timer per uke" value={data.offentligIdent} />
					</Fragment>
				)}
			</div>
		</div>
	)
}
