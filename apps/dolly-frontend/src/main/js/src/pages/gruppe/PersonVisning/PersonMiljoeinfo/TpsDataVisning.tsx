import React from 'react'
import './DataVisning.less'
// @ts-ignore
import Tooltip from 'rc-tooltip'
import 'rc-tooltip/assets/bootstrap_white.css'
import { TpsfVisning } from '~/components/fagsystem'

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
					<Tooltip
						overlay={getPersonInfo(miljoe.person)}
						placement="top"
						align={{
							offset: ['0', '-10'],
						}}
						mouseEnterDelay={0.1}
						mouseLeaveDelay={0.1}
						arrowContent={<div className="rc-tooltip-arrow-inner"></div>}
						key={idx}
						overlayStyle={{ opacity: 1 }}
					>
						<div className="miljoe-knapp">{miljoe.miljoe.toUpperCase()}</div>
					</Tooltip>
				)
			})}
		</div>
	)
}
