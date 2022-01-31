import React from 'react'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import { Adresse } from '~/components/fagsystem/pdlf/visning/partials/Oppholdsadresse'
import { Oppholdsadresse } from '~/pages/gruppe/PersonVisning/PersonMiljoeinfo/PdlDataTyper'
import { ArrayHistorikk } from '~/components/ui/historikk/ArrayHistorikk'

type PdlOppholdsadresseProps = {
	data: Array<Oppholdsadresse>
}

type AdresseProps = {
	data: Oppholdsadresse
	idx?: number
}

const AdresseVisning = ({ data, idx }: AdresseProps) => {
	return (
		<div className="person-visning_content">
			<Adresse adresse={data} idx={idx} />
		</div>
	)
}

export const PdlOppholdsadresse = ({ data }: PdlOppholdsadresseProps) => {
	if (!data || data.length === 0) return null

	const gyldigeAdresser = data.filter((adresse: Oppholdsadresse) => !adresse.metadata?.historisk)
	const historiskeAdresser = data.filter((adresse: Oppholdsadresse) => adresse.metadata?.historisk)

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
