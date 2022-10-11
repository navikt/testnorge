import React from 'react'
import _omit from 'lodash/omit'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import Loading from '~/components/ui/loading/Loading'
import { Oppholdsstatus } from './partials/Oppholdsstatus'
import { Arbeidsadgang } from './partials/Arbeidsadgang'
import { Aliaser } from './partials/Aliaser'
import { Annet } from './partials/Annet'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'
import { hasProperty } from 'dot-prop'
import { Alert } from '@navikt/ds-react'

export const UdiVisning = ({ data, loading }) => {
	if (loading) return <Loading label="Laster UDI-data" />
	if (!data || Object.keys(data).length === 0) return false

	const { oppholdStatus, arbeidsadgang, aliaser } = data

	const manglerFagsystemdata =
		(!oppholdStatus || Object.keys(oppholdStatus)?.length < 1) &&
		(!arbeidsadgang || Object.keys(arbeidsadgang)?.length < 1) &&
		(!aliaser || (Array.isArray(aliaser) && aliaser.length < 1)) &&
		!hasProperty(data, 'flyktning') &&
		!hasProperty(data, 'soeknadOmBeskyttelseUnderBehandling')

	return (
		<div>
			<ErrorBoundary>
				<SubOverskrift label="UDI" iconKind="udi" isWarning={manglerFagsystemdata} />
				{manglerFagsystemdata ? (
					<Alert variant={'warning'} size={'small'} inline style={{ marginBottom: '20px' }}>
						Kunne ikke hente UDI-data på person
					</Alert>
				) : (
					<div className="person-visning">
						<Oppholdsstatus oppholdsstatus={data.oppholdStatus} />
						<Arbeidsadgang arbeidsadgang={data.arbeidsadgang} />
						<Aliaser aliaser={data.aliaser} />
						<Annet data={data} />
					</div>
				)}
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
