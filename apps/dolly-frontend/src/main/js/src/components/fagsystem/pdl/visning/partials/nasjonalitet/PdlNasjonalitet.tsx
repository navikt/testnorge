import React from 'react'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import { HentPerson } from '~/pages/gruppe/PersonVisning/PersonMiljoeinfo/PdlDataTyper'
import { PdlStatsborgerskap } from '~/components/fagsystem/pdl/visning/partials/nasjonalitet/PdlStatsborgerskap'
import { PdlInnflytting } from '~/components/fagsystem/pdl/visning/partials/nasjonalitet/PdlInnflytting'
import { PdlUtflytting } from '~/components/fagsystem/pdl/visning/partials/nasjonalitet/PdlUtflytting'

type NasjonalitetProps = {
	data: HentPerson
	visTittel?: boolean
}

export const PdlNasjonalitet = ({ data, visTittel = true }: NasjonalitetProps) => {
	const { statsborgerskap, innflyttingTilNorge, utflyttingFraNorge } = data
	if (
		statsborgerskap?.length < 1 &&
		innflyttingTilNorge?.length < 1 &&
		utflyttingFraNorge?.length < 1
	)
		return null

	return (
		<div>
			{visTittel && <SubOverskrift label="Nasjonalitet" iconKind="nasjonalitet" />}
			<PdlStatsborgerskap statsborgerskapListe={statsborgerskap} />
			<PdlInnflytting innflytting={innflyttingTilNorge} />
			<PdlUtflytting utflytting={utflyttingFraNorge} />
		</div>
	)
}
