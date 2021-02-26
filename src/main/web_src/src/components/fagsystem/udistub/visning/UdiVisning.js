import React from 'react'
import _omit from 'lodash/omit'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import Loading from '~/components/ui/loading/Loading'
import { Oppholdsstatus } from './partials/Oppholdsstatus'
import { Arbeidsadgang } from './partials/Arbeidsadgang'
import { Aliaser } from './partials/Aliaser'
import { Annet } from './partials/Annet'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'

export const UdiVisning = ({ data, loading }) => {
	if (loading) return <Loading label="Laster UDI-data" />
	if (!data) return false

	return (
		<div>
			<ErrorBoundary>
				<SubOverskrift label="UDI" iconKind="udi" />
				<div className="person-visning">
					<Oppholdsstatus oppholdsstatus={data.oppholdStatus} />
					<Arbeidsadgang arbeidsadgang={data.arbeidsadgang} />
					<Aliaser aliaser={data.aliaser} />
					<Annet data={data} />
				</div>
			</ErrorBoundary>
		</div>
	)
}

UdiVisning.filterValues = (data, bestKriterier) => {
	if (!bestKriterier) return data

	// Asylsøker
	if (!bestKriterier.soeknadOmBeskyttelseUnderBehandling)
		data = _omit(data, 'soeknadOmBeskyttelseUnderBehandling')

	return data
}
