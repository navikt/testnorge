import React from 'react'
import DollyTooltip from '~/components/ui/button/DollyTooltip'
import { DollyApi } from '~/service/Api'
import { useAsync } from 'react-use'
import { PensjonvisningMiljo } from '~/components/fagsystem/pensjon/visning/PensjonVisning'

type PoppVisningMiljoeinfoTypes = {
	miljoer: Array<string>
	ident: string
	bestilteMiljoer: Array<string>
}

export const PoppVisningMiljoeinfo = ({
	miljoer,
	ident,
	bestilteMiljoer,
}: PoppVisningMiljoeinfoTypes) => {
	const getMiljoeinfo = useAsync(async () => {
		const tmpMiljoeinfo = []
		await Promise.allSettled(
			miljoer?.map((miljoe) => {
				DollyApi.getPoppInntekter(ident, miljoe).then((response: { data: any }) => {
					tmpMiljoeinfo.push(response?.data)
				})
			})
		)
		return tmpMiljoeinfo
	}, [])

	return (
		<div className="flexbox--flex-wrap">
			{miljoer.map((miljoe, idx) => {
				const miljoeClassName = bestilteMiljoer?.includes(miljoe)
					? 'miljoe-knapp'
					: 'miljoe-knapp miljoe-knapp-grey'

				return (
					<DollyTooltip
						overlay={<PensjonvisningMiljo data={getMiljoeinfo?.value} miljoe={miljoe} />}
						align={{
							offset: [0, -10],
						}}
						arrowContent={<div className="rc-tooltip-arrow-inner"></div>}
						key={idx}
						overlayStyle={{ opacity: 1 }}
					>
						<div className={miljoeClassName}>{miljoe.toUpperCase()}</div>
					</DollyTooltip>
				)
			})}
		</div>
	)
}
