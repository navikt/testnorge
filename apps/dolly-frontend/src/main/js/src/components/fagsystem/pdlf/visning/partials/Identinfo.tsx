import React from 'react'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'
import Formatters from '~/utils/DataFormatter'

type Data = {
	pdlResponse: {
		identer: [Ident]
	}
}

type Ident = {
	gruppe: string
	ident: string
	historisk: boolean
}

export const IdentInfo = ({ pdlResponse }: Data) => {
	if (!pdlResponse) {
		return null
	}

	return (
		<ErrorBoundary>
			<div>
				<SubOverskrift label="Ident informasjon" iconKind="personinformasjon" />
				{pdlResponse.identer.map((ident: Ident, index: number) => (
					<div key={index}>
						<h4 style={{ marginTop: '0px' }}>{ident.gruppe}</h4>
						<div className="person-visning_content">
							<TitleValue
								title={ident.gruppe.includes('AKTORID') ? 'ID' : 'ident'}
								value={ident.ident}
							/>
							<TitleValue title="Historisk" value={Formatters.oversettBoolean(ident.historisk)} />
						</div>
					</div>
				))}
			</div>
		</ErrorBoundary>
	)
}
