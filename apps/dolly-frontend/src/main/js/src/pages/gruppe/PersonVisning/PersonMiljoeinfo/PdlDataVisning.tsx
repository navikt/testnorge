import React from 'react'
import './DataVisning.less'
import 'rc-tooltip/assets/bootstrap_white.css'
import { PdlVisning } from '~/components/fagsystem/pdl/visning/PdlVisning'
import { PdlDataWrapper } from '~/pages/gruppe/PersonVisning/PersonMiljoeinfo/PdlDataTyper'
import Tooltip from 'rc-tooltip'
import { SivilstandVisning } from '~/components/fagsystem/pdlf/visning/partials/Sivilstand'
import { KontaktinformasjonForDoedsbo } from '~/components/fagsystem/pdlf/visning/partials/KontaktinformasjonForDoedsbo'
import { ForelderBarnRelasjonVisning } from '~/components/fagsystem/pdlf/visning/partials/ForeldreBarnRelasjon'
import { DoedfoedtBarnVisning } from '~/components/fagsystem/pdlf/visning/partials/DoedfoedtBarn'
import { Foedsel } from '~/components/fagsystem/pdlf/visning/partials/Foedsel'
import { VergemaalVisning } from '~/components/fagsystem/pdlf/visning/partials/Vergemaal'

type PdlDataVisningProps = {
	pdlData: PdlDataWrapper
}

export const PdlDataVisning = ({ pdlData }: PdlDataVisningProps) => {
	const data = pdlData.data
	if (!data || !data.hentPerson) {
		return null
	}

	const getPersonInfo = () => {
		return <PdlVisning pdlData={pdlData} />
	}

	return (
		<div className="flexbox--flex-wrap">
			<Tooltip
				overlay={getPersonInfo()}
				placement="top"
				align={{
					offset: [0, -10],
				}}
				mouseEnterDelay={0.1}
				mouseLeaveDelay={0.1}
				arrowContent={<div className="rc-tooltip-arrow-inner" />}
				overlayStyle={{ opacity: 1 }}
			>
				<div className="miljoe-knapp">PDL</div>
			</Tooltip>
		</div>
	)
}
