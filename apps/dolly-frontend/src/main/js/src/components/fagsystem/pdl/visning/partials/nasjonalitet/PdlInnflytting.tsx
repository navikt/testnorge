import React from 'react'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import { AdresseKodeverk } from '~/config/kodeverk'
import { InnflyttingTilNorge } from '~/pages/gruppe/PersonVisning/PersonMiljoeinfo/PdlDataTyper'
import { ArrayHistorikk } from '~/components/ui/historikk/ArrayHistorikk'
import Formatters from '~/utils/DataFormatter'

type InnflyttingProps = {
	data: InnflyttingTilNorge
	idx?: number
}

type VisningProps = {
	innflytting: [InnflyttingTilNorge]
}

const Innflytting = ({ data, idx }: InnflyttingProps) => {
	if (data) {
		return (
			<div key={idx} className="person-visning_content">
				<TitleValue
					title="Fraflyttingsland"
					value={data.fraflyttingsland}
					kodeverk={AdresseKodeverk.InnvandretUtvandretLand}
				/>
				<TitleValue title="Fraflyttingssted" value={data.fraflyttingsstedIUtlandet} />
				<TitleValue
					title="Innflyttingsdato"
					value={Formatters.formatDate(data.folkeregistermetadata?.gyldighetstidspunkt)}
				/>
			</div>
		)
	}
	return null
}

export const PdlInnflytting = ({ innflytting }: VisningProps) => {
	if (innflytting?.length < 1) return null

	const gyldigeInnflyttinger = innflytting.filter(
		(flytting: InnflyttingTilNorge) => !flytting.metadata?.historisk
	)
	const historiskeInnflyttinger = innflytting.filter(
		(flytting: InnflyttingTilNorge) => flytting.metadata?.historisk
	)

	return (
		<ArrayHistorikk
			component={Innflytting}
			data={gyldigeInnflyttinger}
			historiskData={historiskeInnflyttinger}
			header="Innvandret"
		/>
	)
}
