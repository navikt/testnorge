import _ from 'lodash'
import SubOverskrift from '@/components/ui/subOverskrift/SubOverskrift'
import Loading from '@/components/ui/loading/Loading'
import { Oppholdsstatus } from './partials/Oppholdsstatus'
import { Arbeidsadgang } from './partials/Arbeidsadgang'
import { Aliaser } from './partials/Aliaser'
import { Annet } from './partials/Annet'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { hasProperty } from 'dot-prop'
import { Alert } from '@navikt/ds-react'
import { isEmpty } from '@/components/fagsystem/pdlf/form/partials/utils'

const kunTommeObjekter = (udiData) => {
	const { oppholdStatus, arbeidsadgang, aliaser } = udiData
	return (
		(isEmpty(oppholdStatus) || Object.keys(oppholdStatus)?.length < 1) &&
		(_.isEmpty(arbeidsadgang) || Object.keys(arbeidsadgang)?.length < 1) &&
		(_.isEmpty(aliaser) || (Array.isArray(aliaser) && aliaser.length < 1)) &&
		!udiData?.flyktning &&
		!hasProperty(udiData, 'soeknadOmBeskyttelseUnderBehandling')
	)
}

export const sjekkManglerUdiData = (udiData) => {
	return kunTommeObjekter(udiData) && !udiData?.harOppholdsTillatelse
}

export const UdiVisning = ({ data, loading }) => {
	if (loading) return <Loading label="Laster UDI-data" />
	if (!data || Object.keys(data).length === 0) return false

	const manglerFagsystemdata = sjekkManglerUdiData(data)
	const visHarOppholdstillatelse = kunTommeObjekter(data) && data.harOppholdsTillatelse

	return (
		<div>
			<ErrorBoundary>
				<SubOverskrift label="UDI" iconKind="udi" isWarning={manglerFagsystemdata} />
				{manglerFagsystemdata ? (
					<Alert variant={'warning'} size={'small'} inline style={{ marginBottom: '20px' }}>
						Fant ikke UDI-data på person
					</Alert>
				) : (
					<div className="person-visning">
						<Oppholdsstatus
							oppholdsstatus={data?.oppholdStatus}
							oppholdstillatelse={visHarOppholdstillatelse}
						/>
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
		data = _.omit(data, 'soeknadOmBeskyttelseUnderBehandling')

	return data
}
