import React from 'react'
import { Personinfo } from '~/components/fagsystem/tpsf/visning/partials/Personinfo'
import { Nasjonalitet } from '~/components/fagsystem/tpsf/visning/partials/Nasjonalitet'

import { Boadresse } from '~/components/fagsystem/tpsf/visning/partials/Boadresse'
import { Postadresse } from '~/components/fagsystem/tpsf/visning/partials/Postadresse'
import { Identhistorikk } from '~/components/fagsystem/tpsf/visning/partials/Identhistorikk'
import { Relasjoner } from '~/components/fagsystem/tpsf/visning/partials/Relasjoner'
import { getBoadresse, getNasjonalitet, getPersonInfo } from './utils'

export const MiniNorgeVisning = (data: any) => {
	if (!data) return null

	return (
		<div>
			<Personinfo data={getPersonInfo(data.data)} />
			<Nasjonalitet data={getNasjonalitet(data.data)} />
			<Boadresse boadresse={getBoadresse(data.data.boadresse)} />
			{/*<Postadresse postadresse ={}/>*/}
			{/*<Identhistorikk identhistorikk={data.identHistorikk} />*/}
			{/*<Relasjoner relasjoner={data.relasjoner} />*/}
		</div>
	)
}
//TODO postadresse, identhistorikk og relasjoner