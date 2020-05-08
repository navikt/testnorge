import React from 'react'
import { Personinfo } from '~/components/fagsystem/tpsf/visning/partials/Personinfo'
import { Nasjonalitet } from '~/components/fagsystem/tpsf/visning/partials/Nasjonalitet'

import { Boadresse } from '~/components/fagsystem/tpsf/visning/partials/Boadresse'
import { Postadresse } from '~/components/fagsystem/tpsf/visning/partials/Postadresse'
import { Relasjoner } from '~/components/fagsystem/tpsf/visning/partials/Relasjoner'
import { getBoadresse, getNasjonalitet, getPersonInfo, getPostAdresse, getRelasjoner } from './utils'


export const MiniNorgeVisning = (data: any) => {
	if (!data) return null

	const relasjoner = getRelasjoner(data.data)
	return (
		<div>
			<Personinfo data={getPersonInfo(data.data)} />
			<Nasjonalitet data={getNasjonalitet(data.data)} />
			{data.data.boadresse.postnr &&
			// @ts-ignore
			<Boadresse boadresse={getBoadresse(data.data)} />}
			{data.data.post.adresse1 &&
			// @ts-ignore
			<Postadresse postadresse={getPostAdresse(data.data)} />}
			{relasjoner.length > 0 &&
			// @ts-ignore
			<Relasjoner relasjoner={relasjoner}/>}
		</div>
	)
}
//TODO relasjoner, oppholdstilatelse