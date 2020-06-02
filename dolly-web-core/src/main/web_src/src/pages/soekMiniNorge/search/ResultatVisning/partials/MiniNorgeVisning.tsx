import React from 'react'
import {
	Personinfo,
	Nasjonalitet,
	Boadresse,
	Postadresse,
	Relasjoner
} from '~/components/fagsystem/tpsf/visning/partials'
import {
	getBoadresse,
	getNasjonalitet,
	getPersonInfo,
	getPostAdresse,
	getRelasjoner
} from './utils'
import { Innhold } from '~/pages/soekMiniNorge/hodejegeren/types'
import { Oppholdstillatelse } from '~/pages/soekMiniNorge/search/ResultatVisning/partials/Oppholdstillatelse'

interface MiniNorgeVisningProps {
	data: Innhold
}

export const MiniNorgeVisning = ({ data }: MiniNorgeVisningProps) => {
	if (!data) return null

	const relasjoner = getRelasjoner(data)
	return (
		<div>
			<Personinfo data={getPersonInfo(data)} />
			<Nasjonalitet data={getNasjonalitet(data)} />
			{
				// @ts-ignore
				<Boadresse boadresse={data.boadresse.postnr ? getBoadresse(data) : null} />
			}
			{
				// @ts-ignore
				<Postadresse postadresse={data.post.adresse1 ? getPostAdresse(data) : null} />
			}
			{
				// @ts-ignore
				<Relasjoner relasjoner={relasjoner.length > 0 ? relasjoner : null} />
			}
			<Oppholdstillatelse data={data.oppholdstillatelse} />
		</div>
	)
}
