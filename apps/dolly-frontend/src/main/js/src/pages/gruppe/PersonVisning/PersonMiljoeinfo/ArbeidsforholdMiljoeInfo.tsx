import React from 'react'
import Loading from '~/components/ui/loading/Loading'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import { useDollyEnvironments } from '~/utils/hooks/useEnvironments'
import DollyTooltip from '~/components/ui/button/DollyTooltip'
import { ArbeidsforholdMiljoeVisning } from '~/pages/gruppe/PersonVisning/PersonMiljoeinfo/ArbeidsforholdMiljoeVisning'

type PersonMiljoeinfoProps = {
	ident: string
}

export const ArbeidsforholdMiljoeInfo = ({ ident }: PersonMiljoeinfoProps) => {
	const { loading: loading, dollyEnvironments } = useDollyEnvironments()

	if (!ident) {
		return null
	}

	if (loading) {
		return <Loading label="Laster miljøer" fullpage />
	}

	const environmentArray = dollyEnvironments?.Q?.concat(dollyEnvironments?.T)

	return (
		<div>
			<SubOverskrift label="Arbeidsforhold i miljøer" iconKind="visTpsData" />
			<div className="flexbox--flex-wrap">
				{/* @ts-ignore */}
				{environmentArray?.map((miljoe, idx) => {
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
							<div className="miljoe-knapp">{miljoe?.id?.toUpperCase()}</div>
						</DollyTooltip>
					)
				})}
				{dollyEnvironments && (
					<p>
						<i>
							Hold pekeren over et miljø for å se arbeidsforholdene som finnes på denne personen i
							det aktuelle miljøet.
						</i>
					</p>
				)}
			</div>
		</div>
	)
}
