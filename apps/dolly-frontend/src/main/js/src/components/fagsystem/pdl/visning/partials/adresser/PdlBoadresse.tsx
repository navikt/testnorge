import React from 'react'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import { BostedData } from '~/pages/gruppe/PersonVisning/PersonMiljoeinfo/PdlDataTyper'
import { ArrayHistorikk } from '~/components/ui/historikk/ArrayHistorikk'
import { Adresse } from '~/components/fagsystem/pdlf/visning/partials/Boadresse'

type PdlBoadresseProps = {
	data: Array<BostedData>
}

type AdresseProps = {
	data: BostedData
	idx?: number
}

const AdresseVisning = ({ data, idx }: AdresseProps) => {
	return (
		<div className="person-visning_content">
			<Adresse adresse={data} idx={idx} />
		</div>
	)
}

export const PdlBoadresse = ({ data }: PdlBoadresseProps) => {
	if (!data || data.length === 0) return null

	const gyldigeAdresser = data.filter((adresse: BostedData) => !adresse.metadata?.historisk)
	const historiskeAdresser = data.filter((adresse: BostedData) => adresse.metadata?.historisk)

	return (
		<>
			<SubOverskrift label="Boadresse" iconKind="adresse" />
			<ArrayHistorikk
				component={AdresseVisning}
				data={gyldigeAdresser}
				historiskData={historiskeAdresser}
				header={''}
			/>
		</>
	)
}
