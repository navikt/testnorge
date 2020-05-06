import React from 'react'
import { Personinfo } from '~/components/fagsystem/tpsf/visning/partials/Personinfo'
import { Nasjonalitet } from '~/components/fagsystem/tpsf/visning/partials/Nasjonalitet'

import { Boadresse } from '~/components/fagsystem/tpsf/visning/partials/Boadresse'
import { Postadresse } from '~/components/fagsystem/tpsf/visning/partials/Postadresse'
import { Identhistorikk } from '~/components/fagsystem/tpsf/visning/partials/Identhistorikk'
import { Relasjoner } from '~/components/fagsystem/tpsf/visning/partials/Relasjoner'
import {getBoadresse} from './utils'

export const MiniNorgeVisning = (data: any) => {
	if (!data) return null

	return (
		<div>
			{/*<Personinfo data={data} />*/}
			{/*<Nasjonalitet data={data} />*/}
			//@ts-ignore
			<Boadresse boadresse={getBoadresse(data.data.boadresse)} />
			{/*<Identhistorikk identhistorikk={data.identHistorikk} />*/}
			{/*<Relasjoner relasjoner={data.relasjoner} />*/}
		</div>
	)
}