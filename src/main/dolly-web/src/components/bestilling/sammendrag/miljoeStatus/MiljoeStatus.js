import React from 'react'
import Header from '~/components/bestilling/sammendrag/header/Header'
import StaticValue from '~/components/fields/StaticValue/StaticValue'
import Formatters from '~/utils/DataFormatter'
import miljoeStatusSelector from '~/utils/MiljoeStatusSelector'

export default function MiljoeStatus({ bestilling }) {
	const { successEnvs, failedEnvs, avvikEnvs } = miljoeStatusSelector(bestilling)

	const successEnvsStr = Formatters.arrayToString(successEnvs)
	const failedEnvsStr = Formatters.arrayToString(failedEnvs)
	const avvikEnvsStr = Formatters.arrayToString(avvikEnvs)

	const successValue = successEnvsStr.length > 0 ? successEnvsStr : 'ingen'

	return (
		<div>
			<Header label="Miljøstatus" />
			<div className="flexbox--align-center info-block">
				<StaticValue size="medium" header="Suksess" value={successValue} />

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
