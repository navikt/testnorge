import React from 'react'
import DollyTooltip from '~/components/ui/button/DollyTooltip'
import { DollyApi } from '~/service/Api'
import { useAsync } from 'react-use'
import { TpvisningMiljo } from '~/components/fagsystem/pensjon/visning/TpVisning'

type TpVisningMiljoeinfoTypes = {
	miljoer: Array<string>
	ident: string
	bestilteMiljoer: { value: Array<string> }
	ordninger: Array<{ value: string; label: string }>
}

export const TpVisningMiljoeinfo = ({
	miljoer,
	ident,
	bestilteMiljoer,
	ordninger,
}: TpVisningMiljoeinfoTypes) => {
	const getMiljoeinfo = useAsync(async () => {
		const tmpMiljoeinfo = []
		await Promise.allSettled(
			miljoer?.map((miljoe) => {
				DollyApi.getTpOrdning(ident, miljoe).then((response: { data: any }) => {
					tmpMiljoeinfo.push({
						ordninger: response?.data,
						miljo: miljoe,
					})
				})
			})
		)
		return tmpMiljoeinfo
	}, [])

	return (
		<div className="flexbox--flex-wrap">
			{miljoer.map((miljoe, idx) => {
				const miljoeClassName = bestilteMiljoer?.value?.includes(miljoe)
					? 'miljoe-knapp'
					: 'miljoe-knapp miljoe-knapp-grey'

				return (
					<DollyTooltip
						overlay={
							<TpvisningMiljo data={getMiljoeinfo?.value} miljoe={miljoe} ordninger={ordninger} />
						}
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
