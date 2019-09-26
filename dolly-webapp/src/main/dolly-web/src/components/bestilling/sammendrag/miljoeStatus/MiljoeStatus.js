import React from 'react'
import StaticValue from '~/components/fields/StaticValue/StaticValue'
import Formatters from '~/utils/DataFormatter'
import miljoeStatusSelector from '~/utils/MiljoeStatusSelector'

export default function MiljoeStatus({ bestilling }) {
	const { successEnvs, failedEnvs, avvikEnvs } = miljoeStatusSelector(bestilling)

	const successEnvsStr = Formatters.arrayToString(successEnvs)
	const failedEnvsStr = Formatters.arrayToString(failedEnvs)
	const avvikEnvsStr = Formatters.arrayToString(avvikEnvs)

	return (
		<div>
			<h3>Miljøstatus</h3>
			<div className="flexbox--align-center info-block">
				{successEnvsStr.length > 0 ? (
					<StaticValue size="medium" header="Suksess" value={successEnvsStr} />
				) : (
					<StaticValue size="medium" header="Suksess" value={'Ingen'} />
				)}
				{failedEnvsStr.length > 0 && (
					<StaticValue size="medium" header="Feilet" value={failedEnvsStr} />
				)}
				{avvikEnvsStr.length > 0 && (
					<StaticValue size="medium" header="Avvik" value={avvikEnvsStr} />
				)}
			</div>
		</div>
	)
}
