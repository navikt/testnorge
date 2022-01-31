import React from 'react'

import { DollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'
import Panel from '~/components/ui/panel/Panel'
import './historikk.less'

export const ArrayHistorikk = ({ component, data, historiskData, header }) => {
	const Main = component
	const historikkHeader = header + ' historikk'

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
				<div className="med-historikk-blokk">
					<Panel heading={historikkHeader}>
						{historiskData.map((element, idx) => (
							<div key={idx} className="med-historikk-content">
								<Main idx={idx} data={element} />
							</div>
						))}
					</Panel>
				</div>
			)}
		</div>
	)
}
