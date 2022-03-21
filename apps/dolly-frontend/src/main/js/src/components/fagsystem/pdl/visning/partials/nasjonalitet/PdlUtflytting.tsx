import React from 'react'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import { AdresseKodeverk } from '~/config/kodeverk'
import { UtflyttingFraNorge } from '~/pages/gruppe/PersonVisning/PersonMiljoeinfo/PdlDataTyper'
import { ArrayHistorikk } from '~/components/ui/historikk/ArrayHistorikk'
import Formatters from '~/utils/DataFormatter'

type UtflyttingProps = {
	data: UtflyttingFraNorge
	idx?: number
}

type VisningProps = {
	utflytting: [UtflyttingFraNorge]
}

const Utflytting = ({ data, idx }: UtflyttingProps) => {
	if (data) {
		return (
			<div key={idx} className="person-visning_content">
				<TitleValue
					title="Tilflyttingsland"
					value={data.tilflyttingsland}
					kodeverk={AdresseKodeverk.InnvandretUtvandretLand}
				/>
				<TitleValue title="Tilflyttingssted" value={data.tilflyttingsstedIUtlandet} />
				<TitleValue title="Utflyttingsdato" value={Formatters.formatDate(data.utflyttingsdato)} />
			</div>
		)
	}
	return null
}

export const PdlUtflytting = ({ utflytting }: VisningProps) => {
	if (utflytting?.length < 1) return null

	const gyldigeUtflyttinger = utflytting.filter(
		(flytting: UtflyttingFraNorge) => !flytting.metadata?.historisk
	)
	const historiskeUtflyttinger = utflytting.filter(
		(flytting: UtflyttingFraNorge) => flytting.metadata?.historisk
	)

	return (
		<ArrayHistorikk
			component={Utflytting}
			data={gyldigeUtflyttinger}
			historiskData={historiskeUtflyttinger}
			header="Utflyttet"
		/>
	)
}
