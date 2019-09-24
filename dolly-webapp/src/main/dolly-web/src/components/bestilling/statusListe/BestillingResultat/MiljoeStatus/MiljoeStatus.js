import React, { Fragment } from 'react'
import Icon from '~/components/ui/icon/Icon'
import miljoeStatusSelector from '~/utils/MiljoeStatusSelector'

export default function MiljoeStatus({ bestilling }) {
	const { successEnvs, failedEnvs, avvikEnvs } = miljoeStatusSelector(bestilling)

	const _renderMiljoeStatus = () => (
		<Fragment>
			{successEnvs.map((env, i) => _renderMiljoe(env, i, 'feedback-check-circle'))}
			{failedEnvs.map((env, i) => _renderMiljoe(env, i, 'report-problem-triangle'))}
			{avvikEnvs.map((env, i) => _renderMiljoe(env, i, 'report-problem-circle'))}
		</Fragment>
	)

	const _renderMiljoe = (env, key, iconType) => (
		<div className="miljoe" key={key}>
			<Icon size="24px" kind={iconType} />
			<p>{env}</p>
		</div>
	)

	const _manglerIdenterOpprettet = () => {
		const { antallIdenterOpprettet } = bestilling
		if (antallIdenterOpprettet === bestilling.antallIdenter) return null
		return (
			<span className="miljoe-status error-text">
				{antallIdenterOpprettet} av {bestilling.antallIdenter} bestilte identer ble opprettet i TPS.
			</span>
		)
	}

	return (
		<Fragment>
			{_manglerIdenterOpprettet()}
			<span className="miljoe-container miljoe-container-rad">{_renderMiljoeStatus()}</span>
		</Fragment>
	)
}
