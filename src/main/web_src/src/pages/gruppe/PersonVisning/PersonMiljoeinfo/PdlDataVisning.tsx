import React from 'react'
import './DataVisning.less'
// @ts-ignore
import Tooltip from 'rc-tooltip'
import 'rc-tooltip/assets/bootstrap_white.css'
import { IdentInfo } from '~/components/fagsystem/pdlf/visning/partials/Identinfo'
import { GeografiskTilknytning } from '~/components/fagsystem/pdlf/visning/partials/GeografiskTilknytning'
import { PdlPersonInfo } from '~/components/fagsystem/pdlf/visning/partials/PdlPersonInfo'
import { PdlNasjonalitet } from '~/components/fagsystem/pdlf/visning/partials/PdlNasjonalitet'
import { PdlBoadresse } from '~/components/fagsystem/pdlf/visning/partials/PdlBoadresse'
import { PdlFullmakt } from '~/components/fagsystem/pdlf/visning/partials/PdlFullmakt'

type PdlData = {
	data: Data
}

type Data = {
	hentIdenter: object
	hentPerson: HentPerson
	hentGeografiskTilknytning: object
}

type HentPerson = {
	bostedsadresse: [object]
	fullmakt: [object]
}

export const PdlDataVisning = ({ data }: PdlData) => {
	if (!data || !data.hentPerson) {
		return null
	}

	const getPersonInfo = (identInfo: Data) => {
		return (
			<div className="boks">
				<PdlPersonInfo data={identInfo.hentPerson} />
				<IdentInfo data={identInfo.hentIdenter} />
				<GeografiskTilknytning data={identInfo.hentGeografiskTilknytning} />
				<PdlNasjonalitet data={identInfo.hentPerson} />
				<PdlBoadresse data={identInfo.hentPerson.bostedsadresse[0]} />
				<PdlFullmakt data={identInfo.hentPerson.fullmakt[0]} />
			</div>
		)
	}

	return (
		<div className="flexbox--flex-wrap">
			<Tooltip
				overlay={getPersonInfo(data)}
				placement="top"
				align={{
					offset: ['0', '-10']
				}}
				mouseEnterDelay={0.1}
				mouseLeaveDelay={0.1}
				arrowContent={<div className="rc-tooltip-arrow-inner"></div>}
				overlayStyle={{ opacity: 1 }}
			>
				<div className="miljoe-knapp">PDL</div>
			</Tooltip>
		</div>
	)
}
