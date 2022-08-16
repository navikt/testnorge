import React from 'react'

import { DollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'
import Panel from '~/components/ui/panel/Panel'
import './historikk.less'

export const ArrayHistorikk = ({ component, data, historiskData, header }) => {
	const Main = component
	const historikkHeader = header !== '' ? header + ' historikk' : 'Historikk'

	return (
		<div className="med-historikk">
			{data?.length > 0 && (
				<ErrorBoundary>
					<DollyFieldArray data={data} header={header} nested>
						{(element, idx) => <Main idx={idx} data={element} />}
					</DollyFieldArray>
				</ErrorBoundary>
			)}
			{historiskData?.length > 0 && (
				<Panel heading={historikkHeader}>
					<ErrorBoundary>
						<DollyFieldArray data={historiskData} header={header} nested>
							{(element, idx) => <Main idx={idx} data={element} />}
						</DollyFieldArray>
					</ErrorBoundary>
				</Panel>
			)}
		</div>
	)
}
