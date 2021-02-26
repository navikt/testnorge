import React from 'react'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'
import Formatters from '~/utils/DataFormatter'

export const IdentInfo = ({ data, visTittel = true }) => {
	if (!data) {
		return null
	}
	return (
		<ErrorBoundary>
			<div>
				{visTittel && <SubOverskrift label="Ident info" iconKind="personinformasjon" />}
				{data.identer.map((ident, index) => (
					<div key={index}>
						<h4 style={{ marginTop: '0px' }}>{ident.gruppe}</h4>
						<div className="person-visning_content">
							<TitleValue title="Ident" value={ident.ident} />
							<TitleValue title="Historisk" value={Formatters.oversettBoolean(ident.historisk)} />
						</div>
					</div>
				))}
			</div>
		</ErrorBoundary>
	)
}
