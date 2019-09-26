import React, { Fragment } from 'react'
import Icon from '~/components/ui/icon/Icon'
import antallIdenterOpprettet from '~/components/bestilling/utils/antallIdenterOpprettet'
import groupMiljoeByStatus from '~/components/bestilling/utils/groupMiljoeByStatus'

export default function MiljoeStatus({ bestilling }) {
	const { successEnvs, failedEnvs, avvikEnvs } = groupMiljoeByStatus(bestilling)
	const antall = antallIdenterOpprettet(bestilling)

	const _renderMiljoe = (env, key, iconType) => (
		<div className="miljoe" key={key}>
			<Icon size="24px" kind={iconType} />
			<p>{env}</p>
		</div>
	)

	return (
		<Fragment>
			{antall.harMangler && <span className="error-text">{antall.tekst}</span>}
			<span className="miljoe-container miljoe-container-rad">
				{successEnvs.map((env, i) => _renderMiljoe(env, i, 'feedback-check-circle'))}
				{failedEnvs.map((env, i) => _renderMiljoe(env, i, 'report-problem-triangle'))}
				{avvikEnvs.map((env, i) => _renderMiljoe(env, i, 'report-problem-circle'))}
			</span>
		</Fragment>
	)
}
