import React from 'react'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import { HentPerson } from '~/pages/gruppe/PersonVisning/PersonMiljoeinfo/PdlDataTyper'
import { PdlStatsborgerskap } from '~/components/fagsystem/pdl/visning/partials/nasjonalitet/PdlStatsborgerskap'
import { PdlInnflytting } from '~/components/fagsystem/pdl/visning/partials/nasjonalitet/PdlInnflytting'
import { PdlUtflytting } from '~/components/fagsystem/pdl/visning/partials/nasjonalitet/PdlUtflytting'
import { PdlSpraak } from '~/components/fagsystem/pdl/visning/partials/nasjonalitet/PdlSpraak'

type NasjonalitetProps = {
	data: HentPerson
	tpsMessagingData?: any
	tpsMessagingLoading?: boolean
	visTittel?: boolean
}

export const PdlNasjonalitet = ({
	data,
	tpsMessagingData,
	tpsMessagingLoading = false,
	visTittel = true,
}: NasjonalitetProps) => {
	const { statsborgerskap, innflyttingTilNorge, utflyttingFraNorge } = data
	if (
		statsborgerskap?.length < 1 &&
		innflyttingTilNorge?.length < 1 &&
		utflyttingFraNorge?.length < 1 &&
		!tpsMessagingData
	)
		return null

	return (
		<div>
			{visTittel && <SubOverskrift label="Nasjonalitet" iconKind="nasjonalitet" />}
			<PdlSpraak data={tpsMessagingData} loading={tpsMessagingLoading} />
			<PdlStatsborgerskap statsborgerskapListe={statsborgerskap} />
			<PdlInnflytting innflytting={innflyttingTilNorge} />
			<PdlUtflytting utflytting={utflyttingFraNorge} />
		</div>
	)
}
