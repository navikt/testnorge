import * as _ from 'lodash-es'
import SubOverskrift from '@/components/ui/subOverskrift/SubOverskrift'
import Loading from '@/components/ui/loading/Loading'
import { Oppholdsstatus } from './partials/Oppholdsstatus'
import { Arbeidsadgang } from './partials/Arbeidsadgang'
import { Annet } from './partials/Annet'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { hasProperty } from 'dot-prop'
import { Alert } from '@navikt/ds-react'
import { isEmpty } from '@/components/fagsystem/pdlf/form/partials/utils'
import { getFagsystemTimeoutTitle } from '@/utils/getFagsystemTimeoutTitle'

type UdiData = {
	oppholdStatus?: any
	arbeidsadgang?: any
	flyktning?: boolean
	soeknadOmBeskyttelseUnderBehandling?: boolean
	harOppholdsTillatelse?: boolean
}

type UdiVisningProps = {
	data?: UdiData
	loading?: boolean
	timedOutFagsystemer?: string[]
}

const kunTommeObjekter = (udiData: UdiData) => {
	const { oppholdStatus, arbeidsadgang } = udiData
	return (
		(isEmpty(oppholdStatus) || Object.keys(oppholdStatus || {})?.length < 1) &&
		(_.isEmpty(arbeidsadgang) || Object.keys(arbeidsadgang || {})?.length < 1) &&
		!udiData?.flyktning &&
		!hasProperty(udiData, 'soeknadOmBeskyttelseUnderBehandling')
	)
}

export const sjekkManglerUdiData = (udiData: UdiData) => {
	return kunTommeObjekter(udiData) && !udiData?.harOppholdsTillatelse
}

export const UdiVisning = ({ data, loading, timedOutFagsystemer = [] }: UdiVisningProps) => {
	if (loading) return <Loading label="Laster UDI-data" />
	if (!data || Object.keys(data).length === 0) return null

	const manglerFagsystemdata = sjekkManglerUdiData(data)
	const visHarOppholdstillatelse = kunTommeObjekter(data) && data.harOppholdsTillatelse

	return (
		<div>
			<ErrorBoundary>
				<SubOverskrift
					label="UDI"
					iconKind="udi"
					isWarning={manglerFagsystemdata}
					title={timedOutFagsystemer.includes('UDI') ? getFagsystemTimeoutTitle('UDI') : undefined}
				/>
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
						<Annet data={data} />
					</div>
				)}
			</ErrorBoundary>
		</div>
	)
}

UdiVisning.filterValues = (
	data: UdiData,
	bestKriterier?: { soeknadOmBeskyttelseUnderBehandling?: boolean },
): UdiData => {
	if (!bestKriterier) return data

	if (!bestKriterier.soeknadOmBeskyttelseUnderBehandling)
		data = _.omit(data, 'soeknadOmBeskyttelseUnderBehandling') as UdiData

	return data
}
