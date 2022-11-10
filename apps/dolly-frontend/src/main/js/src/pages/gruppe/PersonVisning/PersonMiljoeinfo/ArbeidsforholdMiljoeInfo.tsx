import React from 'react'
import Loading from '~/components/ui/loading/Loading'
import { useDollyEnvironments } from '~/utils/hooks/useEnvironments'
import DollyTooltip from '~/components/ui/button/DollyTooltip'
import { ArbeidsforholdMiljoeVisning } from '~/pages/gruppe/PersonVisning/PersonMiljoeinfo/ArbeidsforholdMiljoeVisning'

type PersonMiljoeinfoProps = {
	ident: string
	bestilteMiljoer: {
		value: Array<string>
	}
}

export const ArbeidsforholdMiljoeInfo = ({ ident, bestilteMiljoer }: PersonMiljoeinfoProps) => {
	const { loading, dollyEnvironmentList } = useDollyEnvironments()

	const unsupportedEnvironments = ['t0', 't13', 'qx']

	if (!ident) {
		return null
	}

	if (loading) {
		return <Loading label="Laster miljøer" fullpage />
	}

	const environmentArray = dollyEnvironmentList?.filter(
		(miljoe) => !unsupportedEnvironments.includes(miljoe.id)
	)
	return (
		<div className="flexbox--flex-wrap" style={{ marginTop: '15px' }}>
			{/* @ts-ignore */}
			{environmentArray?.map((miljoe, idx) => {
				const miljoeClassName = bestilteMiljoer?.value?.includes(miljoe.id)
					? 'miljoe-knapp'
					: 'miljoe-knapp miljoe-knapp-grey'
				return (
					<DollyTooltip
						overlay={<ArbeidsforholdMiljoeVisning ident={ident} miljoe={miljoe.id} />}
						align={{
							offset: [0, -10],
						}}
						arrowContent={<div className="rc-tooltip-arrow-inner"></div>}
						key={idx}
						overlayStyle={{ opacity: 1 }}
					>
						<div className={miljoeClassName}>{miljoe?.id?.toUpperCase()}</div>
					</DollyTooltip>
				)
			})}
			{dollyEnvironmentList && (
				<p>
					<i>
						Hold pekeren over et miljø for å se arbeidsforholdene som finnes på denne personen i det
						aktuelle miljøet.
					</i>
				</p>
			)}
		</div>
	)
}
