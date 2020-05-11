import React from 'react'
import {
	Personinfo,
	Nasjonalitet,
	Boadresse,
	Postadresse,
	Relasjoner
} from '~/components/fagsystem/tpsf/visning/partials'
import { getBoadresse, getNasjonalitet, getPersonInfo, getPostAdresse, getRelasjoner } from './utils'
import { Innhold } from '~/pages/soekMiniNorge/hodejegeren/types'

interface MiniNorgeVisningProps {
	data: Innhold
}

export const MiniNorgeVisning = ({data}: MiniNorgeVisningProps) => {
	if (!data) return null

	const relasjoner = getRelasjoner(data)
	return (
		<div>
			<Personinfo data={getPersonInfo(data)} />
			<Nasjonalitet data={getNasjonalitet(data)} />
			{data.boadresse.postnr &&
			// @ts-ignore
			<Boadresse boadresse={getBoadresse(data)} />}
			{data.post.adresse1 &&
			// @ts-ignore
			<Postadresse postadresse={getPostAdresse(data)} />}
			{relasjoner.length > 0 &&
			// @ts-ignore
			<Relasjoner relasjoner={relasjoner}/>}
		</div>
	)
}
//TODO oppholdstillatelse