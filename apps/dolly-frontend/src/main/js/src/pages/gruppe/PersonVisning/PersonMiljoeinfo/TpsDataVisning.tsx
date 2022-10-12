import React from 'react'
import './DataVisning.less'
// @ts-ignore
import Tooltip from 'rc-tooltip'
import 'rc-tooltip/assets/bootstrap_white.css'
import { TpsfVisning } from '~/components/fagsystem'
import DollyTooltip from '~/components/ui/button/DollyTooltip'

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

	const getPersonInfo = (person: object) => {
		return (
			<div className="boks">
				<TpsfVisning data={person} />
			</div>
		)
	}

	return (
		<div className="flexbox--flex-wrap">
			{data.map((miljoe, idx) => {
				return (
					<DollyTooltip
						overlay={getPersonInfo(miljoe.person)}
						align={{
							offset: ['0', '-10'],
						}}
						arrowContent={<div className="rc-tooltip-arrow-inner"></div>}
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
