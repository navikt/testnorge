import './DataVisning.less'
// @ts-ignore
import Tooltip from 'rc-tooltip'
import 'rc-tooltip/assets/bootstrap_white.css'
import DollyTooltip from '@/components/ui/button/DollyTooltip'
import { ApiFeilmelding } from '@/pages/gruppe/PersonVisning/PersonMiljoeinfo/PdlDataVisning'
import React from 'react'
import { TestComponentSelectors } from '#/mocks/Selectors'
import { TpsfVisning } from '@/components/fagsystem/tpsf/visning/Visning'

type TpsData = {
	data: Array<Data>
}

type Data = {
	miljoe: string
	person: object
}

export const TpsDataVisning = ({ data }: TpsData) => {
	data.sort(function (a, b) {
		if (a.miljoe < b.miljoe) {
			return -1
		}
		if (a.miljoe > b.miljoe) {
			return 1
		}
		return 0
	})

	const getPersonInfo = (miljoeData: any) => {
		if (miljoeData?.status === 'FEIL') {
			const feilmelding = miljoeData.utfyllendeMelding
				? miljoeData.utfyllendeMelding
				: 'Ukjent feil'
			return <ApiFeilmelding feil={'Feil ved henting fra TPS: ' + feilmelding} />
		} else {
			return (
				<div className="boks">
					<TpsfVisning data={miljoeData.person} />
				</div>
			)
		}
	}

	return (
		<div className="flexbox--flex-wrap">
			{data.map((miljoe, idx) => {
				return (
					<DollyTooltip
						useExternalTooltip={true}
						testLocator={TestComponentSelectors.HOVER_MILJOE}
						content={getPersonInfo(miljoe)}
						align={{
							offset: [0, -10],
						}}
						key={idx}
						overlayStyle={{ opacity: 1 }}
					>
						<div className="miljoe-knapp">{miljoe.miljoe.toUpperCase()}</div>
					</DollyTooltip>
				)
			})}
		</div>
	)
}
