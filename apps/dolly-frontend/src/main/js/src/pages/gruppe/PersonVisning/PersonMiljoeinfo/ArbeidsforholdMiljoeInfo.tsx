import React from 'react'
import Loading from '~/components/ui/loading/Loading'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import { useDollyEnvironments } from '~/utils/hooks/useEnvironments'
import DollyTooltip from '~/components/ui/button/DollyTooltip'
import { DollyApi } from '~/service/Api'

type PersonMiljoeinfoProps = {
	ident: string
	miljoe: string
}

export const ArbeidsforholdMiljoeInfo = ({ ident }: PersonMiljoeinfoProps) => {
	const { loading: loading, dollyEnvironments } = useDollyEnvironments()

	if (!ident) {
		return null
	}

	if (loading) {
		return <Loading label="Laster miljøer" fullpage />
	}

	const getArbeidsforholdInfo = async (ident: string, miljoe: string) => {
		const kapplah = await DollyApi.getArbeidsforhold(ident, miljoe)
		console.log('kapplah: ', kapplah) //TODO - SLETT MEG
		return <div className="boks"></div>
	}

	return (
		<div>
			<SubOverskrift label="Opprettet i miljøer" iconKind="visTpsData" />
			{/* @ts-ignore */}
			{dollyEnvironments.map((miljoe, idx) => {
				return (
					<DollyTooltip
						overlay={getArbeidsforholdInfo(ident, miljoe)}
						align={{
							offset: [0, -10],
						}}
						arrowContent={<div className="rc-tooltip-arrow-inner"></div>}
						key={idx}
						overlayStyle={{ opacity: 1 }}
					>
						<div className="miljoe-knapp">{miljoe.miljoe.toUpperCase()}</div>
					</DollyTooltip>
				)
			})}
			{dollyEnvironments && (
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
