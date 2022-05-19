import React from 'react'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import { Adresse } from '~/components/fagsystem/pdlf/visning/partials/Oppholdsadresse'
import { OppholdsadresseData } from '~/pages/gruppe/PersonVisning/PersonMiljoeinfo/PdlDataTyper'
import { ArrayHistorikk } from '~/components/ui/historikk/ArrayHistorikk'

type PdlOppholdsadresseProps = {
	data: Array<OppholdsadresseData>
}

type AdresseProps = {
	data: OppholdsadresseData
	idx?: number
}

const AdresseVisning = ({ data, idx }: AdresseProps) => {
	return (
		<div className="person-visning_content">
			<Adresse oppholdsadresseData={data} idx={idx} />
		</div>
	)
}

export const PdlOppholdsadresse = ({ data }: PdlOppholdsadresseProps) => {
	if (!data || data.length === 0) return null

	const gyldigeAdresser = data.filter(
		(adresse: OppholdsadresseData) => !adresse.metadata?.historisk
	)
	const historiskeAdresser = data.filter(
		(adresse: OppholdsadresseData) => adresse.metadata?.historisk
	)

	return (
		<>
			<SubOverskrift label="Oppholdsadresse" iconKind="adresse" />
			<ArrayHistorikk
				component={AdresseVisning}
				data={gyldigeAdresser}
				historiskData={historiskeAdresser}
				header={''}
			/>
		</>
	)
}
