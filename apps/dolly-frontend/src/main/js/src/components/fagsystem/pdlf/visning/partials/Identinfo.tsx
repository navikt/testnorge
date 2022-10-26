import React from 'react'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'
import Formatters from '~/utils/DataFormatter'
import { Ident } from '~/pages/gruppe/PersonVisning/PersonMiljoeinfo/PdlDataTyper'

type Data = {
	pdlResponse: {
		identer: [Ident]
	}
}

export const IdentInfo = ({ pdlResponse }: Data) => {
	if (!pdlResponse?.identer) {
		return null
	}

	const formatTitle = (title) => {
		if (title.includes('FOLKEREGISTERIDENT')) {
			return 'Folkeregisterident'
		}
		if (title.includes('AKTORID')) {
			return 'Aktør-ID'
		} else return title
	}

	return (
		<ErrorBoundary>
			<div>
				<SubOverskrift label="Identinformasjon" iconKind="personinformasjon" />
				{pdlResponse.identer.map((ident: Ident, index: number) => (
					<div key={index}>
						<h4 style={{ marginTop: '0px' }}>{formatTitle(ident.gruppe)}</h4>
						<div className="person-visning_content">
							<TitleValue
								title={ident.gruppe.includes('AKTORID') ? 'ID' : 'ident'}
								value={ident.ident}
								visKopier
							/>
							<TitleValue title="Historisk" value={Formatters.oversettBoolean(ident.historisk)} />
						</div>
					</div>
				))}
			</div>
		</ErrorBoundary>
	)
}
