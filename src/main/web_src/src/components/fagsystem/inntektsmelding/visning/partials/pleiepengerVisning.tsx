import React from 'react'
import { DollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import Formatters from '~/utils/DataFormatter'
import { Pleiepenger } from '~/components/fagsystem/inntektsmelding/InntektsmeldingTypes'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'

interface PleiepengerVisning {
	data?: Array<Pleiepenger>
}

export default ({ data }: PleiepengerVisning) => {
	if (!data || data.length < 1) return null
	return (
		<ErrorBoundary>
			<DollyFieldArray data={data} header="Pleiepenger">
				{(id: Pleiepenger) => (
					<>
						<div className="person-visning_content">
							<TitleValue title="Fra og med dato" value={Formatters.formatDate(id.fom)} />
							<TitleValue title="Til og med dato" value={Formatters.formatDate(id.tom)} />
						</div>
					</>
				)}
			</DollyFieldArray>
		</ErrorBoundary>
	)
}
